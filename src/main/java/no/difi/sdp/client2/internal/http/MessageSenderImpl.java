package no.difi.sdp.client2.internal.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.MedDokumentEgenskaper;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.SendIOException;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPostSerializer;
import no.difi.sdp.client2.domain.sbd.StandardBusinessDocument;
import no.difi.sdp.client2.foretningsmelding.IntegrasjonspunktMessageSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.fraHttpStatusCode;

public class MessageSenderImpl implements MessageSender {


    private static final Logger LOG = LoggerFactory.getLogger(MessageSenderImpl.class);

    private final static String CREATE_ENDPOINT_PATH = "/api/messages/out";
    private final static String MESSAGE_ENDPOINT_PATH_TEMPLATE = "/api/messages/out/%s";
    private final static String STATUSES_PEEK_PATH = "/api/statuses/peek";
    private final static String STATUSES_CONFIRM_PATH = "/api/statuses/%d";

    private final String endpointUri;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper mapper;

    public MessageSenderImpl(URI endpointUri, CloseableHttpClient httpClient) {
        //Fjern trailing slash hvis den eksisterer
        this.endpointUri = endpointUri.toString().replaceFirst("/$", "");
        this.httpClient = httpClient;

        final SimpleModule sbdSerializerModule = new SimpleModule("sbd-serializer");
        sbdSerializerModule.addSerializer(StandardBusinessDocument.class, new IntegrasjonspunktMessageSerializer());
        sbdSerializerModule.addSerializer(FysiskPost.class, new FysiskPostSerializer());

        mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(sbdSerializerModule)


            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void send(StandardBusinessDocument sbd, Dokumentpakke dokumentpakke) {
        try {
            createMessage(sbd);

            final String conversationId = sbd.getConversationId();

            for (Dokument dokument : dokumentpakke.getHoveddokumentOgVedlegg().collect(toList())) {
                LOG.info("---------------------------");
                addContent(conversationId, dokument);
                if (dokument.getMetadataDocument().isPresent()) {
                    addContent(conversationId, dokument.getMetadataDocument().get());
                }
            }
            LOG.info("---------------------------");
            closeMessage(sbd);
        } catch (IOException e) {
            throw new SendIOException(e);
        }
    }

    private void createMessage(StandardBusinessDocument sbd) throws IOException {
        final String json = mapper.writeValueAsString(sbd);
        LOG.info(json);
        LOG.info("");
        LOG.info("---------------------------");
        LOG.info("");
        HttpPost httpPost = new HttpPost(endpointUri + CREATE_ENDPOINT_PATH);
        httpPost.setEntity(new StringEntity(json));
        httpPost.setHeader("content-type", "application/json");
        CloseableHttpResponse response = httpClient.execute(httpPost);
        LOG.info(EntityUtils.toString(response.getEntity()));
        handleResponse(response.getStatusLine().getStatusCode());

    }

    private void handleResponse(int statusCode) {
        if (statusCode / 100 != 2) {
            throw new SendException("Received none 2xx code: " + statusCode, fraHttpStatusCode(statusCode), null);
        }
    }

    private void addContent(String conversationId, MedDokumentEgenskaper dokument) throws IOException {
        String messageEndpointPath = String.format(MESSAGE_ENDPOINT_PATH_TEMPLATE, conversationId);
        HttpPut documentPut = new HttpPut(endpointUri + messageEndpointPath);
        documentPut.setEntity(new ByteArrayEntity(dokument.getBytes()));
        documentPut.setHeader("content-type", dokument.getMimeType());

        String contentDisposition = String.format("attachment; filename=\"%s\"", dokument.getFileName());
        contentDisposition += dokument.getDokumentTittel().map(tittel -> String.format("; name=\"%s\"", tittel)).orElse("");

        documentPut.setHeader("content-disposition", contentDisposition);

        final HttpResponse response = httpClient.execute(documentPut);
        LOG.info(EntityUtils.toString(response.getEntity()));
        handleResponse(response.getStatusLine().getStatusCode());
    }

    private void closeMessage(StandardBusinessDocument sbd) throws IOException {
        String messageEndpointPath = String.format(MESSAGE_ENDPOINT_PATH_TEMPLATE, sbd.getConversationId());
        HttpPost fileHttpPost = new HttpPost(endpointUri + messageEndpointPath);
        CloseableHttpResponse response = httpClient.execute(fileHttpPost);
        LOG.info(EntityUtils.toString(response.getEntity()));
        handleResponse(response.getStatusLine().getStatusCode());
    }

    @Override
    public Optional<IntegrasjonspunktKvittering> hentKvittering() {
        HttpGet httpGet = new HttpGet(endpointUri + STATUSES_PEEK_PATH);

        try {
            final CloseableHttpResponse response = httpClient.execute(httpGet);
            final int statusCode = response.getStatusLine().getStatusCode();
            handleResponse(statusCode);
            if (statusCode == 204) {
                return Optional.empty();
            }
            final String kvitteringJson = EntityUtils.toString(response.getEntity());
            LOG.info(kvitteringJson);
            final IntegrasjonspunktKvittering kvittering = mapper.readerFor(IntegrasjonspunktKvittering.class).readValue(kvitteringJson);
            return Optional.ofNullable(kvittering);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void bekreftKvittering(long id) {
        String statusPath = endpointUri + String.format(STATUSES_CONFIRM_PATH, id);

        HttpDelete httpDelete = new HttpDelete(statusPath);
        try {
            final CloseableHttpResponse response = httpClient.execute(httpDelete);
            handleResponse(response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
