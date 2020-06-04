package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.sbd.StandardBusinessDocument;
import no.difi.sdp.client2.internal.IntegrasjonspunktMessageSenderFacade;
import no.difi.sdp.client2.internal.SBDForsendelseBuilder;
import no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering;
import no.difi.sdp.client2.internal.kvittering.KvitteringBuilder;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering.KvitteringStatus.OPPRETTET;
import static no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering.KvitteringStatus.SENDT;

public class SikkerDigitalPostKlient {

    private final Databehandler databehandler;
    private final KvitteringBuilder kvitteringBuilder;
    private final IntegrasjonspunktMessageSenderFacade integrasjonspunktMessageSenderFacade;
    private static final Logger LOG = LoggerFactory.getLogger(SikkerDigitalPostKlient.class);

    /**
     * @param databehandler       parten som er ansvarlig for den tekniske utførelsen av sendingen.
     *                            Se <a href="http://begrep.difi.no/SikkerDigitalPost/forretningslag/Aktorer">oversikt over aktører</a> for mer informasjon.
     * @param klientKonfigurasjon Oppsett for blant annet oppkoblingen mot integrasjonspunkt og interceptorer for å få ut data som sendes.
     */
    public SikkerDigitalPostKlient(Databehandler databehandler, KlientKonfigurasjon klientKonfigurasjon) {
        this.kvitteringBuilder = new KvitteringBuilder();
        this.integrasjonspunktMessageSenderFacade = new IntegrasjonspunktMessageSenderFacade(klientKonfigurasjon);
        this.databehandler = databehandler;
    }

    /**
     * Sender en forsendelse til meldingsformidler. Dersom noe feilet i sendingen til meldingsformidler, vil det kastes en exception med beskrivende feilmelding.
     *
     * @param forsendelse Et objekt som har all informasjon klar til å kunne sendes (mottakerinformasjon, sertifikater, dokumenter mm),
     *                    enten digitalt eller fyisk.
     * @throws SendException
     */
    public SendResultat send(Forsendelse forsendelse) throws SendException {
        StandardBusinessDocument sbd = SBDForsendelseBuilder.buildSBD(databehandler.organisasjonsnummer, forsendelse);
        integrasjonspunktMessageSenderFacade.send(sbd, forsendelse.getDokumentpakke());

        return new SendResultat(sbd.getConversationId());
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
    public ForretningsKvittering hentKvittering() throws SendException {
        return hentKvitteringOgBekreftForrige(null, null);
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
    public ForretningsKvittering hentKvitteringOgBekreftForrige(KanBekreftesSomBehandletKvittering forrigeKvittering) throws SendException {
        return hentKvitteringOgBekreftForrige(null, forrigeKvittering);
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
        if (forrigeKvittering != null) {
            bekreft(forrigeKvittering);
        }

        // This guard may be to low or high. The best thing to do would be to set integrasjonspunktet to not put SENDT and OPPRETTET-kvitteringer on the queue.
        int guard = 100;
        for (int count = 0; count < guard; count++) {
            final Optional<IntegrasjonspunktKvittering> kvitteringOptional = integrasjonspunktMessageSenderFacade.hentKvittering();
            boolean shouldFetchAgain = kvitteringOptional.map(IntegrasjonspunktKvittering::getStatus)
                .filter(status -> status.equals(SENDT) || status.equals(OPPRETTET))
                .isPresent();

            if (shouldFetchAgain) {
                LOG.info("Fikk integrasjonspunktspesifikk statusmelding ved henting av kvittering. Bekreft den og henter neste kvittering fra kø.");
                integrasjonspunktMessageSenderFacade.bekreft(kvitteringOptional.get().getId());
            } else {
                return kvitteringOptional
                    .map(kvitteringBuilder::buildForretningsKvittering)
                    .orElse(null);
            }

        }
        LOG.warn("Antall forsøk på å hente kvittering overskredet. " +
            "Det kan komme av det er mange " + SENDT + " og " + OPPRETTET + "-kvitteringer på integrasjonspunktkøen." +
            " Prøv igjen.");
        return null;
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
        final Long id = forrigeKvittering.getIntegrasjonspunktId();
        if (id != null) {
            integrasjonspunktMessageSenderFacade.bekreft(id);
        } else {
            integrasjonspunktMessageSenderFacade.hentKvittering()
                .map(IntegrasjonspunktKvittering::getId)
                .ifPresent(integrasjonspunktMessageSenderFacade::bekreft);
        }
    }

    /**
     * Registrer egen ExceptionMapper.
     */
    public void setExceptionMapper(ExceptionMapper exceptionMapper) {
        this.integrasjonspunktMessageSenderFacade.setExceptionMapper(exceptionMapper);
    }
}
