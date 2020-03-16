package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.ForretningsMeldingType;
import no.difi.sdp.client2.domain.ForretningsMelding;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocument;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocumentHeader;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static no.difi.sdp.client2.domain.ForretningsMeldingType.DIGITAL;
import static no.difi.sdp.client2.domain.ForretningsMeldingType.PRINT;
import static no.difi.sdp.client2.domain.sbdh.Process.DIGITAL_POST_INFO;

public class SBDForsendelseBuilder {

    public static StandardBusinessDocument buildSBD(Forsendelse forsendelse) {
        Clock clock = Clock.system(ZoneId.of("UTC"));

        //SBD
        ForretningsMelding forretningsMelding = forsendelse.getForretningsMelding();
        forretningsMelding.setHoveddokument(forsendelse.getDokumentpakke().getHoveddokument().getFilnavn());

        StandardBusinessDocument sbd = new StandardBusinessDocument();

        String instanceIdentifier = UUID.randomUUID().toString();
        final StandardBusinessDocumentHeader sbdHeader = new StandardBusinessDocumentHeader.Builder().process(DIGITAL_POST_INFO)
            .standard(forsendelse.type)
            .to(forsendelse.getMottaker())
            .type(forretningsMelding.getType())
            .relatedToConversationId(instanceIdentifier)
            .relatedToMessageId(instanceIdentifier)
            .creationDateAndTime(ZonedDateTime.now(clock)).build();

        sbd.setStandardBusinessDocumentHeader(sbdHeader);
        sbd.setAny(forretningsMelding);

        return sbd;
    }

}
