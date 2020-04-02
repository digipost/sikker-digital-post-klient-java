package no.difi.sdp.client2.internal.kvittering;

import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFeiltype;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPVarslingfeilet;
import no.difi.begrep.sdp.schema_v10.SDPVarslingskanal;
import no.difi.sdp.client2.domain.exceptions.SikkerDigitalPostException;
import no.difi.sdp.client2.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client2.domain.kvittering.Feil;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.KvitteringsInfo;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client2.domain.kvittering.MottaksKvittering;
import no.difi.sdp.client2.domain.kvittering.ReturpostKvittering;
import no.difi.sdp.client2.domain.kvittering.VarslingFeiletKvittering;
import no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;

import static no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering.KvitteringStatus.SENDT;

public class KvitteringBuilder {

    private KvitteringTransformer transformer = new KvitteringTransformer();


    public ForretningsKvittering buildForretningsKvittering(IntegrasjonspunktKvittering integrasjonspunktKvittering) {
        KvitteringsInfo kvitteringsinfo = KvitteringsInfo.builder()
            .konversasjonsId(integrasjonspunktKvittering.getConversationId().toString())
            .referanseTilMeldingId(integrasjonspunktKvittering.getMessageId().toString())
            .tidspunkt(integrasjonspunktKvittering.getLastUpdate().toInstant())
            .build();

        if(integrasjonspunktKvittering.getRawReceipt() != null) {
            final SimpleSBDMessage simpleSBDMessage = transformer.transform(integrasjonspunktKvittering.getRawReceipt());
            return buildForretningsKvittering(simpleSBDMessage, kvitteringsinfo);
        } else  if (integrasjonspunktKvittering.getStatus().equals(SENDT)) {
            return null;
        } else if (integrasjonspunktKvittering.getStatus().equals(IntegrasjonspunktKvittering.KvitteringStatus.ANNET)) {
            throw new SikkerDigitalPostException("Kvittering tilbake fra meldingsformidler var verken kvittering eller feil.");
        } else {
            throw new SikkerDigitalPostException("En uventet feil oppsto ved håndtering av kvittering: " + integrasjonspunktKvittering.toString());
        }
    }
//
//    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer meldingsformidler, KvitteringForespoersel kvitteringForespoersel) {
//        return new EbmsPullRequest(meldingsformidler(meldingsformidler), kvitteringForespoersel.getPrioritet().getEbmsPrioritet(), kvitteringForespoersel.getMpcId());
//    }
//
    private ForretningsKvittering buildForretningsKvittering(SimpleSBDMessage simpleSBDMessage, KvitteringsInfo kvitteringsInfo) {

        KanBekreftesSomBehandletKvittering kvittering = kvitteringsInfo::getReferanseTilMeldingId;
        if (simpleSBDMessage.erKvittering()) {
            SDPKvittering sdpKvittering = simpleSBDMessage.getKvittering().kvittering;

            if (sdpKvittering.getAapning() != null) {
                return new AapningsKvittering(kvittering, kvitteringsInfo);
            } else if (sdpKvittering.getMottak() != null) {
                return new MottaksKvittering(kvittering, kvitteringsInfo);
            } else if (sdpKvittering.getLevering() != null) {
                return new LeveringsKvittering(kvittering, kvitteringsInfo);
            } else if (sdpKvittering.getVarslingfeilet() != null) {
                return varslingFeiletKvittering(sdpKvittering, kvitteringsInfo);
            } else if (sdpKvittering.getReturpost() != null) {
                return new ReturpostKvittering(kvittering, kvitteringsInfo);
            }
        } else if (simpleSBDMessage.erFeil()) {
            SDPFeil sdpFeil = simpleSBDMessage.getFeil();
            return feil(kvittering, sdpFeil, kvitteringsInfo);
        }

        throw new SikkerDigitalPostException("Kvittering tilbake fra meldingsformidler var verken kvittering eller feil.");
    }

    private ForretningsKvittering feil(KanBekreftesSomBehandletKvittering kvittering, SDPFeil feil, KvitteringsInfo kvitteringsInfo) {
        return Feil.builder(kvittering, kvitteringsInfo, mapFeilType(feil.getFeiltype()))
                .detaljer(feil.getDetaljer())
                .build();
    }

    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, KvitteringsInfo kvitteringsInfo) {
        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
        VarslingFeiletKvittering.Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());

        return VarslingFeiletKvittering.builder(kvitteringsInfo::getReferanseTilMeldingId, kvitteringsInfo, varslingskanal)
                .beskrivelse(varslingfeilet.getBeskrivelse())
                .build();
    }

    private Feil.Feiltype mapFeilType(SDPFeiltype feiltype) {
        if (feiltype == SDPFeiltype.KLIENT) {
            return Feil.Feiltype.KLIENT;
        }
        return Feil.Feiltype.SERVER;
    }

    private VarslingFeiletKvittering.Varslingskanal mapVarslingsKanal(SDPVarslingskanal varslingskanal) {
        if (varslingskanal == SDPVarslingskanal.EPOST) {
            return VarslingFeiletKvittering.Varslingskanal.EPOST;
        }
        return VarslingFeiletKvittering.Varslingskanal.SMS;
    }

}
