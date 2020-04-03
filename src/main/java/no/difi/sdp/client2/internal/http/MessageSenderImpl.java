package no.difi.sdp.client2.internal.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.MedDokumentEgenskaper;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPostSerializer;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocument;
import no.difi.sdp.client2.foretningsmelding.IntegrasjonspunktMessageSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
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
    private final static String STATUSES_PATH = "/api/statuses/peek";

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
    public void bekreft(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering) {

    }

    @Override
    public Optional<IntegrasjonspunktKvittering> hentKvittering() {
        //{
        //  "id": 396,
        //  "lastUpdate": "2020-04-01T13:25:32.66+02:00",
        //  "status": "LEVERT",
        //  "description": "Kvittering fra utskrift og forsendelsestjenesten om at melding er mottatt og lagt til print",
        //  "rawReceipt": "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns3:StandardBusinessDocument xmlns:ns3=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\" xmlns:ns9=\"http://begrep.difi.no/sdp/schema_v10\"><ns3:StandardBusinessDocumentHeader><ns3:HeaderVersion>1.0</ns3:HeaderVersion><ns3:Sender><ns3:Identifier Authority=\"urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908\">9908:984661185</ns3:Identifier></ns3:Sender><ns3:Receiver><ns3:Identifier Authority=\"urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908\">9908:984661185</ns3:Identifier></ns3:Receiver><ns3:DocumentIdentification><ns3:Standard>urn:no:difi:sdp:1.0</ns3:Standard><ns3:TypeVersion>1.0</ns3:TypeVersion><ns3:InstanceIdentifier>fad8b7db-86d3-4b63-94e6-497816ab0647</ns3:InstanceIdentifier><ns3:Type>kvittering</ns3:Type><ns3:CreationDateAndTime>2020-04-01T13:25:32.660+02:00</ns3:CreationDateAndTime></ns3:DocumentIdentification><ns3:BusinessScope><ns3:Scope><ns3:Type>ConversationId</ns3:Type><ns3:InstanceIdentifier>1be9ac16-014f-4444-85f9-d964eb6cd0f4</ns3:InstanceIdentifier><ns3:Identifier>urn:no:difi:sdp:1.0</ns3:Identifier></ns3:Scope></ns3:BusinessScope></ns3:StandardBusinessDocumentHeader><ns9:kvittering><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/><DigestValue>T+NxuZh/VFmJpDaqFJq3wZtZ/9iyw4OK8r1Tju0xmdM=</DigestValue></Reference></SignedInfo><SignatureValue>Uj3T0Ab1cJ1qmnARck834ZLdK5lGURPa1QhyDUNuWff89Nz3xFbwyJYzjLSFJHcJLcnd3Y8qTF4ttIobQddRgLbAvBBiNHNC02/zz/mD4kfN2juaZb8TTch00Sy2RSi1UMIdlVWhkQPE1FE6RQrkZPzUABWVSJcNoQWS9/v2FY4tzm86+GODg+qeoLQRGDxEHzKcALcoCBvW+dCGduWHjPpBYgGnK4M8h/LHOAplJu7kTR1OfIet7FFYSAq6rJonRbGezcPWlS/NQxhznPXT/PES+78I8WiKHpCzZ/kAD3jONlwMDZiLuihC3ih7EDu5Xs/jpTn8/RCs4bDCuB/ukw==</SignatureValue><KeyInfo><X509Data><X509Certificate>MIIFEzCCA/ugAwIBAgILAjxAYEhC5sBU48QwDQYJKoZIhvcNAQELBQAwUTELMAkGA1UEBhMCTk8xHTAbBgNVBAoMFEJ1eXBhc3MgQVMtOTgzMTYzMzI3MSMwIQYDVQQDDBpCdXlwYXNzIENsYXNzIDMgVGVzdDQgQ0EgMzAeFw0yMDAyMDYxNTE4MThaFw0yMzAyMDYyMjU5MDBaMFoxCzAJBgNVBAYTAk5PMRgwFgYDVQQKDA9QT1NURU4gTk9SR0UgQVMxHTAbBgNVBAMMFFBPU1RFTiBOT1JHRSBBUyBURVNUMRIwEAYDVQQFEwk5ODQ2NjExODUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCU3m0kTYPTNX/ftxf6KcY0iLXQ6pAozxqyTrmbwGZw+LzPpY3phKKE5kbKp6oYKDFW1OehRB1L+bqZJTYEXSHWxUA/NGr7SoCV7UEycBSX6tA4MLwAzn3yEccApRa4Vqwv+XphOEUg0v/x/DkwJaT4o1YOFD8QRNjqmJcz4iW0I3Wp4C7dGJxYF2CK7UX5KXwHdgrSdTt6lF4M3ZshJH4quzhAY5y7tdO2EMVq9Bkkc+oA3xvJQ/O3GjjAUpy4ywglDIW022sJKjjAlNY8mjJMcybnRaWLoLC6YprSbzb6wsmu8GJGjiHQEFvB5EAfmIyr7cvT50usAnMZC9gprS9tAgMBAAGjggHhMIIB3TAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFD+u9XgLkqNwIDVfWvr3JKBSAfBBMB0GA1UdDgQWBBRaOn2NsEXqQ8eZojgdbhHCAdrU8DAOBgNVHQ8BAf8EBAMCBLAwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMBYGA1UdIAQPMA0wCwYJYIRCARoBAAMCMIG7BgNVHR8EgbMwgbAwN6A1oDOGMWh0dHA6Ly9jcmwudGVzdDQuYnV5cGFzcy5uby9jcmwvQlBDbGFzczNUNENBMy5jcmwwdaBzoHGGb2xkYXA6Ly9sZGFwLnRlc3Q0LmJ1eXBhc3Mubm8vZGM9QnV5cGFzcyxkYz1OTyxDTj1CdXlwYXNzJTIwQ2xhc3MlMjAzJTIwVGVzdDQlMjBDQSUyMDM/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdDCBigYIKwYBBQUHAQEEfjB8MDsGCCsGAQUFBzABhi9odHRwOi8vb2NzcC50ZXN0NC5idXlwYXNzLm5vL29jc3AvQlBDbGFzczNUNENBMzA9BggrBgEFBQcwAoYxaHR0cDovL2NydC50ZXN0NC5idXlwYXNzLm5vL2NydC9CUENsYXNzM1Q0Q0EzLmNlcjANBgkqhkiG9w0BAQsFAAOCAQEAZGpNYvzd7mmh7V2OlQOc0B7+1N3apZMEnMj6iiPH6l7oZ5aNFP73fLlDiB2NpPpkQEDcrt6MCnNiO/U3qIkWz/blWDD9k1xUs9ZSeQZJnapuGnN7zSbIUcFnTDNik4cFlJOG7hcnPvxv3ewMSffuhoqnnaPA7J1gzNMA2hkmM7l+sGfCzhr7h9THgo51uGnscTL6PI2qB9qpHN4lR2Aw4yEV0Ve16ENQxASucGc2N+6ZiJQWZiHQL8Z6076NogeMqzG1KIklh5ZogPJxBbnFg72Y0aMrKHw799jm9n64HnOAt1c3qOjduxnjdRMRy+YcIuIy+bUPX4bexmsuX0ehGw==</X509Certificate></X509Data></KeyInfo></Signature><ns9:tidspunkt>2020-04-01T13:25:32.660+02:00</ns9:tidspunkt><ns9:mottak/></ns9:kvittering></ns3:StandardBusinessDocument>",
        //  "messageId": "1be9ac16-014f-4444-85f9-d964eb6cd0f4",
        //  "convId": 392,
        //  "conversationId": "1be9ac16-014f-4444-85f9-d964eb6cd0f4"
        //}
        HttpGet httpGet = new HttpGet(endpointUri + STATUSES_PATH);

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
}
