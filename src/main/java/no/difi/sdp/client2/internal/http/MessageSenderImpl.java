package no.difi.sdp.client2.internal.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPostSerializer;
import no.difi.sdp.client2.domain.kvittering.KanBekreftesSomBehandletKvittering;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocument;
import no.difi.sdp.client2.foretningsmelding.IntegrasjonspunktMessageSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.fraHttpStatusCode;

public class MessageSenderImpl implements MessageSender {


    private static final Logger LOG = LoggerFactory.getLogger(MessageSenderImpl.class);

    private final static String CREATE_ENDPOINT_PATH = "/api/messages/out";
    private final static String MESSAGE_ENDPOINT_PATH_TEMPLATE = "/api/messages/out/%s";

    private final String endpointUri;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public MessageSenderImpl(URI endpointUri, HttpClient httpClient) {
        this.endpointUri = "http://localhost:9093";//endpointUri.toString().replaceFirst("/$", "");
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

            LOG.info("---------------------------");

            addContent(sbd.getConversationId(), dokumentpakke.getHoveddokument());
            for (Dokument dokument : dokumentpakke.getVedlegg()) {
                LOG.info("---------------------------");
                addContent(sbd.getConversationId(), dokument);
            }
            LOG.info("---------------------------");
            closeMessage(sbd);
        } catch (IOException e) {
            LOG.info("", e);
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
        CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
        LOG.info(EntityUtils.toString(response.getEntity()));
        handleResponse(response.getStatusLine().getStatusCode());

    }

    private void handleResponse(int statusCode) {
        if (statusCode / 100 != 2) {
            throw new SendException("Received none 2xx code: " + statusCode, fraHttpStatusCode(statusCode), null);
        }
    }

    private void addContent(String conversationId, Dokument dokument) throws IOException {
        String messageEndpointPath = String.format(MESSAGE_ENDPOINT_PATH_TEMPLATE, conversationId);
        HttpPut documentPut = new HttpPut(endpointUri + messageEndpointPath);
        documentPut.setEntity(new ByteArrayEntity(dokument.getBytes()));
        documentPut.setHeader("content-type", dokument.getMimeType());

        String contentDisposition = String.format("attachment; filename=\"%s\"", dokument.getFilnavn());
        if(dokument.getTittel() != null ) {
            contentDisposition += String.format("; name=\"%s\"", dokument.getTittel());
        }
        documentPut.setHeader("content-disposition", contentDisposition);

        final HttpResponse response = httpClient.execute(documentPut);
        LOG.info(EntityUtils.toString(response.getEntity()));
        handleResponse(response.getStatusLine().getStatusCode());
    }

    private void closeMessage(StandardBusinessDocument sbd) throws IOException {
        String messageEndpointPath = String.format(MESSAGE_ENDPOINT_PATH_TEMPLATE, sbd.getConversationId());
        HttpPost fileHttpPost = new HttpPost(endpointUri + messageEndpointPath);
        CloseableHttpResponse response = HttpClients.createDefault().execute(fileHttpPost);
        LOG.info(EntityUtils.toString(response.getEntity()));
        handleResponse(response.getStatusLine().getStatusCode());
    }

    @Override
    public void bekreft(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering) {

    }
}
