package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.ObjectMother;
import no.difi.sdp.client2.domain.AktoerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Avsender;
import no.difi.sdp.client2.domain.DatabehandlerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.Forsendelse;
import no.digipost.api.representations.Organisasjonsnummer;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.sbd.StandardBusinessDocument;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static co.unruly.matchers.Java8Matchers.where;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

class SBDForsendelseBuilderTest {

    private static final Instant now = Instant.now();
    private static final Clock freezedClock = Clock.fixed(now, ZoneId.systemDefault());

    @Test
    void buildSBD() {
        DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer = AktoerOrganisasjonsnummer.of("123456789").forfremTilDatabehandler();
        final Avsender avsender = ObjectMother.avsender(AktoerOrganisasjonsnummer.of("987654321"));
        final DigitalPost digitalPost = ObjectMother.digitalPost();
        final Dokumentpakke dokumentpakke = ObjectMother.dokumentpakke();
        final String konversasjonsId = UUID.randomUUID().toString();
        Forsendelse forsendelse = Forsendelse.digital(avsender, digitalPost, dokumentpakke).konversasjonsId(konversasjonsId).build();

        final StandardBusinessDocument sbd = SBDForsendelseBuilder.buildSBD(databehandlerOrganisasjonsnummer, forsendelse, freezedClock);

        assertThat(sbd, where(StandardBusinessDocument::getConversationId, is(konversasjonsId)));

        String senderIdentifier = Organisasjonsnummer.COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY + ":" + databehandlerOrganisasjonsnummer.toString() + ":" + avsender.getOrganisasjonsnummer().toString();
        assertThat(sbd, where(StandardBusinessDocument::getSenderIdentifier, is(senderIdentifier)));

        assertThat(sbd, where(standardBusinessDocument -> standardBusinessDocument
                .getStandardBusinessDocumentHeader()
                .getDocumentIdentification()
                .getCreationDateAndTime(),
            is(ZonedDateTime.now(freezedClock))));

        assertThat(sbd, where(StandardBusinessDocument::getAny, is(instanceOf(DigitalPost.class))));
        assertThat(sbd, where(o -> ((DigitalPost) o.getAny()), is(forsendelse.getDigitalPost())));

    }
}
