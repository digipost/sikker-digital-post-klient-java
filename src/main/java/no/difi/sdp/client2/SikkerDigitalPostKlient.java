package no.difi.sdp.client2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocument;
import no.difi.sdp.client2.foretningsmelding.IntegrasjonspunktMessageSerializer;
import no.difi.sdp.client2.internal.CertificateValidator;
import no.difi.sdp.client2.internal.IntegrasjonspunktMessageSenderFacade;
import no.difi.sdp.client2.internal.KvitteringBuilder;
import no.difi.sdp.client2.internal.SBDForsendelseBuilder;
import no.difi.sdp.client2.util.CryptoChecker;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.io.File;
import java.io.IOException;

public class SikkerDigitalPostKlient {

    private final Databehandler databehandler;
    private final SBDForsendelseBuilder SBDForsendelseBuilder;
    private final KvitteringBuilder kvitteringBuilder;
    private final IntegrasjonspunktMessageSenderFacade integrasjonspunktMessageSenderFacade;
    private final KlientKonfigurasjon klientKonfigurasjon;

    /**
     * @param databehandler       parten som er ansvarlig for den tekniske utførelsen av sendingen.
     *                            Se <a href="http://begrep.difi.no/SikkerDigitalPost/forretningslag/Aktorer">oversikt over aktører</a> for mer informasjon.
     * @param klientKonfigurasjon Oppsett for blant annet oppkoblingen mot meldingsformidler og interceptorer for å få ut data som sendes.
     */
    public SikkerDigitalPostKlient(Databehandler databehandler, KlientKonfigurasjon klientKonfigurasjon) {
        CryptoChecker.checkCryptoPolicy();

        this.SBDForsendelseBuilder = new SBDForsendelseBuilder();
        this.kvitteringBuilder = new KvitteringBuilder();
        this.integrasjonspunktMessageSenderFacade = new IntegrasjonspunktMessageSenderFacade(databehandler, klientKonfigurasjon);

        this.klientKonfigurasjon = klientKonfigurasjon;
        this.databehandler = databehandler;

        CertificateValidator.validate(klientKonfigurasjon.getMiljo(), databehandler.noekkelpar.getVirksomhetssertifikat().getX509Certificate());
    }

    /**
     * Sender en forsendelse til meldingsformidler. Dersom noe feilet i sendingen til meldingsformidler, vil det kastes en exception med beskrivende feilmelding.
     *
     * @param forsendelse Et objekt som har all informasjon klar til å kunne sendes (mottakerinformasjon, sertifikater, dokumenter mm),
     *                    enten digitalt eller fyisk.
     * @throws SendException
     */
    public SendResultat send(Forsendelse forsendelse) throws SendException {
        StandardBusinessDocument sbd = SBDForsendelseBuilder.buildSBD(databehandler, forsendelse);

        try {
            final SimpleModule sbdSerializerModule = new SimpleModule("sbd-serializer");
            sbdSerializerModule.addSerializer(StandardBusinessDocument.class, new IntegrasjonspunktMessageSerializer());

            String json = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .registerModule(sbdSerializerModule)

                    .enable(SerializationFeature.INDENT_OUTPUT)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .writeValueAsString(sbd);
            System.out.println(json);

            System.out.println();
            System.out.println("-------------------------------------");
            System.out.println();


            HttpPost httpPost = new HttpPost("http://localhost:9093/api/messages/out");
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("content-type", "application/json");
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
            System.out.println(EntityUtils.toString(response.getEntity()));

            //if ok

            File file = new File("/Users/johnksv/Downloads/test.pdf");

            final String conversationId = sbd.getConversationId();

            lastOppFil(conversationId, file, "primær");
            lastOppFil(conversationId, file, "sekundær");

            HttpPost fileHttpPost = new HttpPost("http://localhost:9093/api/messages/out/" + conversationId);
            CloseableHttpResponse fileResponsePost = HttpClients.createDefault().execute(fileHttpPost);
            System.out.println(EntityUtils.toString(fileResponsePost.getEntity()));

        } catch (IOException e) {
            System.out.println(e);
        }


        return null;
    }

    private void lastOppFil(String instanceIdentifier, File file, String title) throws IOException {
        HttpPut fileHttpPut = new HttpPut("http://localhost:9093/api/messages/out/" + instanceIdentifier + "?title=" + title);
        fileHttpPut.setEntity(new ByteArrayEntity(FileUtils.readFileToByteArray(file)));
        fileHttpPut.setHeader("content-type", "application/pdf");
        if (title.equals("primær")) {
            fileHttpPut.setHeader("content-disposition", "attachment; filename=\"faktura.pdf\"");
        } else {
            fileHttpPut.setHeader("content-disposition", "attachment; filename=\"sekundær.pdf\"");
        }
        CloseableHttpResponse fileResponse = HttpClients.createDefault().execute(fileHttpPut);
        System.out.println(EntityUtils.toString(fileResponse.getEntity()));
    }

    /**
     * Forespør kvittering for forsendelser. Kvitteringer blir tilgjengeliggjort etterhvert som de er klare i meldingsformidler.
     * Det er ikke mulig å etterspørre kvittering for en spesifikk forsendelse.
     * <p>
     * Dersom det ikke er tilgjengelige kvitteringer skal det ventes følgende tidsintervaller før en ny forespørsel gjøres:
     * <dl>
     * <dt>normal</dt>
     * <dd>Minimum 10 minutter</dd>
     * <dt>prioritert</dt>
     * <dd>Minimum 1 minutt</dd>
     * </dl>
     */
    public ForretningsKvittering hentKvittering(KvitteringForespoersel kvitteringForespoersel) throws SendException {
        return hentKvitteringOgBekreftForrige(kvitteringForespoersel, null);
    }

    /**
     * Forespør kvittering for forsendelser med mulighet til å samtidig bekrefte på forrige kvittering for å slippe å kjøre eget kall for bekreft.
     * Kvitteringer blir tilgjengeliggjort etterhvert som de er klare i meldingsformidler. Det er ikke mulig å etterspørre kvittering for en
     * spesifikk forsendelse.
     * <p>
     * Dersom det ikke er tilgjengelige kvitteringer skal det ventes følgende tidsintervaller før en ny forespørsel gjøres:
     * <dl>
     * <dt>normal</dt>
     * <dd>Minimum 10 minutter</dd>
     * <dt>prioritert</dt>
     * <dd>Minimum 1 minutt</dd>
     * </dl>
     */
    public ForretningsKvittering hentKvitteringOgBekreftForrige(KvitteringForespoersel kvitteringForespoersel, KanBekreftesSomBehandletKvittering forrigeKvittering) throws SendException {
        EbmsPullRequest ebmsPullRequest = kvitteringBuilder.buildEbmsPullRequest(klientKonfigurasjon.getMeldingsformidlerOrganisasjon(), kvitteringForespoersel);

        EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering;
        if (forrigeKvittering == null) {
            ebmsApplikasjonsKvittering = integrasjonspunktMessageSenderFacade.hentKvittering(ebmsPullRequest);
        } else {
            ebmsApplikasjonsKvittering = integrasjonspunktMessageSenderFacade.hentKvittering(ebmsPullRequest, forrigeKvittering);
        }

        if (ebmsApplikasjonsKvittering == null) {
            return null;
        }

        return kvitteringBuilder.buildForretningsKvittering(ebmsApplikasjonsKvittering);
    }

    /**
     * Bekreft mottak av forretningskvittering gjennom {@link #hentKvittering(KvitteringForespoersel)}.
     * {@link #hentKvittering(KvitteringForespoersel)} kommer ikke til å returnere en ny kvittering før mottak av den forrige er bekreftet.
     * <p>
     * Dette legger opp til følgende arbeidsflyt:
     * <ol>
     * <li>{@link #hentKvittering(KvitteringForespoersel)}</li>
     * <li>Gjør intern prosessering av kvitteringen (lagre til database, og så videre)</li>
     * <li>Bekreft mottak av kvittering</li>
     * </ol>
     */
    public void bekreft(KanBekreftesSomBehandletKvittering forrigeKvittering) throws SendException {
        integrasjonspunktMessageSenderFacade.bekreft(forrigeKvittering);
    }

    /**
     * Registrer egen ExceptionMapper.
     */
    public void setExceptionMapper(ExceptionMapper exceptionMapper) {
        this.integrasjonspunktMessageSenderFacade.setExceptionMapper(exceptionMapper);
    }

    /**
     * Hent ut Spring {@code WebServiceTemplate} som er konfigurert internt, og brukes av biblioteket
     * til kommunikasjon med meldingsformidler. Ved hjelp av denne instansen kan man f.eks. sette opp en
     * {@code MockWebServiceServer} for bruk i tester.
     * <p>
     * Man vil ikke under normale omstendigheter aksessere denne i produksjonskode.
     *
     * @return Spring {@code WebServiceTemplate} som er konfigurert internt i klientbiblioteket
     * @see <a href="https://docs.spring.io/spring-ws/docs/3.0.7.RELEASE/reference/#_using_the_client_side_api">Spring WS - 6.2. Using the client-side API</a>
     * @see <a href="https://docs.spring.io/spring-ws/docs/3.0.7.RELEASE/reference/#_client_side_testing">Spring WS - 6.3. Client-side testing</a>
     */
    public WebServiceTemplate getMeldingTemplate() {
        return integrasjonspunktMessageSenderFacade.getMeldingTemplate();
    }

}
