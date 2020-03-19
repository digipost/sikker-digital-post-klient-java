package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.DatabehandlerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.ForretningsMelding;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocument;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocumentHeader;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.difi.sdp.client2.domain.Forsendelse.Type.DIGITAL;
import static no.difi.sdp.client2.domain.sbdh.Process.DIGITAL_POST_INFO;

public class SBDForsendelseBuilder {

    public static StandardBusinessDocument buildSBD(DatabehandlerOrganisasjonsnummer databehandler, Forsendelse forsendelse) {
        Clock clock = Clock.system(ZoneId.of("UTC"));

        //SBD
        ForretningsMelding forretningsMelding = forsendelse.getForretningsMelding();
        forretningsMelding.setHoveddokument(forsendelse.getDokumentpakke().getHoveddokument().getFilnavn());

        if(forsendelse.type == DIGITAL) {
            forsendelse.getDokumentpakke().getHoveddokumentOgVedlegg()
                .filter(dokument -> dokument.getMetadataDocument().isPresent())
                .forEach(dokument -> ((DigitalPost)forretningsMelding).addMetadataMapping(dokument.getFileName(), dokument.getMetadataDocument().get().getFileName()));
        }

        StandardBusinessDocument sbd = new StandardBusinessDocument();

        String instanceIdentifier = UUID.randomUUID().toString();
        final StandardBusinessDocumentHeader sbdHeader = new StandardBusinessDocumentHeader.Builder().process(DIGITAL_POST_INFO)
            .standard(forsendelse.type)
            .from(databehandler).onBehalfOf(forsendelse.getAvsender().getOrganisasjonsnummer())
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
