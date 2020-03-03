package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocument;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocumentHeader;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static no.difi.sdp.client2.domain.sbdh.Process.DIGITAL_POST_INFO;

public class SBDForsendelseBuilder {

    public static StandardBusinessDocument buildSBD(Forsendelse forsendelse) {
        Clock clock = Clock.system(ZoneId.of("UTC"));

        //SBD
        final DigitalPost digitalPost = forsendelse.getDigitalPost();
        String mottaker = digitalPost.getMottaker().getPersonidentifikator();

        digitalPost.setHoveddokument(forsendelse.getDokumentpakke().getHoveddokument().getFilnavn());
        digitalPost.setTittel(forsendelse.getDokumentpakke().getHoveddokument().getTittel());

        StandardBusinessDocument sbd = new StandardBusinessDocument();

        String instanceIdentifier = UUID.randomUUID().toString();
        final StandardBusinessDocumentHeader sbdHeader = new StandardBusinessDocumentHeader.Builder().process(DIGITAL_POST_INFO)
                .standard(forsendelse.type)
                .to(mottaker)
                .type(digitalPost.getType())
                .relatedToConversationId(instanceIdentifier)
                .relatedToMessageId(instanceIdentifier)
                .creationDateAndTime(ZonedDateTime.now(clock)).build();

        sbd.setStandardBusinessDocumentHeader(sbdHeader);
        sbd.setAny(digitalPost);

        return sbd;
    }

}
