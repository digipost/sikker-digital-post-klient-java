package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocument;
import no.difi.sdp.client2.domain.sbdh.StandardBusinessDocumentHeader;
import no.difi.sdp.client2.foretningsmelding.dpi.DigitalForetningsmelding;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static no.difi.sdp.client2.domain.sbdh.Process.DIGITAL_POST_INFO;

public class SBDForsendelseBuilder {

    private final SDPBuilder sdpBuilder;

    public SBDForsendelseBuilder() {
        sdpBuilder = new SDPBuilder();
    }


    public StandardBusinessDocument buildSBD(Databehandler databehandler, Forsendelse forsendelse) {
        Clock clock = Clock.system(ZoneId.of("UTC"));

        //SBD
        String mottaker = forsendelse.getDigitalPost().getMottaker().getPersonidentifikator();

        DigitalForetningsmelding forretningsmelding = DigitalForetningsmelding.from(forsendelse);
        StandardBusinessDocument sbd = new StandardBusinessDocument();

        String instanceIdentifier = UUID.randomUUID().toString();
        final StandardBusinessDocumentHeader sbdHeader = new StandardBusinessDocumentHeader.Builder().process(DIGITAL_POST_INFO)
                .standard(forsendelse.type)
                .to(mottaker)
                .type(forretningsmelding.getType())
                .relatedToConversationId(instanceIdentifier)
                .relatedToMessageId(instanceIdentifier)
                .creationDateAndTime(ZonedDateTime.now(clock)).build();

        sbd.setStandardBusinessDocumentHeader(sbdHeader);
        sbd.setAny(forretningsmelding);

        return sbd;
    }

}
