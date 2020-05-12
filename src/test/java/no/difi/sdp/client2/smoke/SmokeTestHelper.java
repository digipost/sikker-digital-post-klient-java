package no.difi.sdp.client2.smoke;

import no.difi.sdp.client2.KlientKonfigurasjon;
import no.difi.sdp.client2.SikkerDigitalPostKlient;
import no.difi.sdp.client2.domain.Avsender;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Miljo;
import no.digipost.api.representations.Organisasjonsnummer;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringForespoersel;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;

import java.util.UUID;

import static java.lang.System.out;
import static java.lang.Thread.sleep;
import static no.difi.sdp.client2.ObjectMother.BRING_ORGNR;
import static no.difi.sdp.client2.ObjectMother.POSTEN_ORGNR;
import static no.difi.sdp.client2.ObjectMother.digitalForsendelse;
import static no.difi.sdp.client2.ObjectMother.ehfForsendelse;
import static no.difi.sdp.client2.ObjectMother.fysiskPostForsendelse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

class SmokeTestHelper {

    private final String _mpcId;
    private SikkerDigitalPostKlient _klient;
    private Forsendelse _forsendelse;
    private ForretningsKvittering _forretningskvittering;

    SmokeTestHelper(Miljo miljo) {
        Organisasjonsnummer databehanderOrgnr = Organisasjonsnummer.of(BRING_ORGNR.getOrganisasjonsnummer());
        _mpcId = UUID.randomUUID().toString();

        Databehandler databehandler = Databehandler.builder(POSTEN_ORGNR.forfremTilDatabehandler()).build();

        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(miljo).build();
        _klient = new SikkerDigitalPostKlient(databehandler, klientKonfigurasjon);
    }

    SmokeTestHelper create_print_forsendelse() {
        return setForsendelse(fysiskPostForsendelse());
    }

    SmokeTestHelper create_digital_forsendelse(Avsender avsender) {
        return setForsendelse(digitalForsendelse(_mpcId, SmokeTestHelper.class.getResourceAsStream("/test.pdf"), avsender));
    }

    SmokeTestHelper create_ehf_forsendelse(Avsender avsender) {
        return setForsendelse(ehfForsendelse(_mpcId, SmokeTestHelper.class.getResourceAsStream("/test.pdf"), avsender));
    }

    private SmokeTestHelper setForsendelse(Forsendelse forsendelse) {
        assertState(_klient);

        _forsendelse = forsendelse;

        return this;
    }

    SmokeTestHelper send() {
        assertState(_forsendelse);

        _klient.send(_forsendelse);

        return this;
    }

    SmokeTestHelper fetch_receipt() {
        KvitteringForespoersel kvitteringForespoersel = KvitteringForespoersel.builder().mpcId(_mpcId).build();
        ForretningsKvittering forretningsKvittering = null;

        try {
            sleep(2000);

            for (int i = 0; i < 10; i++) {
                forretningsKvittering = _klient.hentKvittering(kvitteringForespoersel);

                if (forretningsKvittering != null) {
                    out.println("Kvittering!");
                    out.println(String.format("%s: %s, %s, %s, %s", forretningsKvittering.getClass().getSimpleName(), forretningsKvittering.getKonversasjonsId(), forretningsKvittering.getReferanseTilMeldingId(), forretningsKvittering.getTidspunkt(), forretningsKvittering));
                    assertThat(forretningsKvittering.getKonversasjonsId(), not(emptyString()));
                    assertThat(forretningsKvittering.getReferanseTilMeldingId(), not(emptyString()));
                    assertThat(forretningsKvittering.getTidspunkt(), notNullValue());
                    assertThat(forretningsKvittering, instanceOf(LeveringsKvittering.class));

                    _klient.bekreft(forretningsKvittering);
                    break;
                } else {
                    out.println("Ingen kvittering");
                    sleep(1000);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        _forretningskvittering = forretningsKvittering;

        return this;
    }

    SmokeTestHelper expect_receipt_to_be_leveringskvittering() {
        assertState(_forretningskvittering);

        assertThat(_forretningskvittering, instanceOf(LeveringsKvittering.class));

        return this;
    }

    SmokeTestHelper confirm_receipt() {
        _klient.bekreft(_forretningskvittering);

        return this;
    }

    private void assertState(Object object) {
        if (object == null) {
            throw new IllegalStateException("Requires gradually built state. Make sure you use functions in the correct order.");
        }
    }

}
