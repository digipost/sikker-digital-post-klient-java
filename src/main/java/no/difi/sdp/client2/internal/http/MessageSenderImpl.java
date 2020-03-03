package no.difi.sdp.client2.internal.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
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

import java.io.IOException;
import java.net.URI;

public class MessageSenderImpl implements MessageSender {


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

            final HttpResponse putResponse = putDocument(sbd.getConversationId(), dokumentpakke.getHoveddokument());
            System.out.println(EntityUtils.toString(putResponse.getEntity()));

            for (Dokument dokument : dokumentpakke.getVedlegg()) {
                final HttpResponse putResponseAttachment = putDocument(sbd.getConversationId(), dokument);
                System.out.println(EntityUtils.toString(putResponseAttachment.getEntity()));
            }

            String messageEndpointPath = String.format(MESSAGE_ENDPOINT_PATH_TEMPLATE, sbd.getConversationId());
            HttpPost fileHttpPost = new HttpPost(endpointUri + messageEndpointPath);
            CloseableHttpResponse fileResponsePost = HttpClients.createDefault().execute(fileHttpPost);
            System.out.println(EntityUtils.toString(fileResponsePost.getEntity()));

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void createMessage(StandardBusinessDocument sbd) throws IOException {
        final String json = mapper.writeValueAsString(sbd);

        HttpPost httpPost = new HttpPost(endpointUri + CREATE_ENDPOINT_PATH);
        httpPost.setEntity(new StringEntity(json));
        httpPost.setHeader("content-type", "application/json");
        CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    private HttpResponse putDocument(String conversationId, Dokument dokument) throws IOException {
        String messageEndpointPath = String.format(MESSAGE_ENDPOINT_PATH_TEMPLATE, conversationId);
        HttpPut documentPut = new HttpPut(endpointUri +messageEndpointPath);
        documentPut.setEntity(new ByteArrayEntity(dokument.getBytes()));
        documentPut.setHeader("content-type", dokument.getMimeType());

        String contentDisposition = String.format("attachment; name=\"%s\"; filename=\"%s\"", dokument.getTittel(), dokument.getFilnavn());
        documentPut.setHeader("content-disposition", contentDisposition);

        return httpClient.execute(documentPut);
    }

    @Override
    public void bekreft(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering) {

    }
}
