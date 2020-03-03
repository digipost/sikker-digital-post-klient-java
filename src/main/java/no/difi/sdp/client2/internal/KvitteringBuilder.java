package no.difi.sdp.client2.internal;

public class KvitteringBuilder {

//    public EbmsPullRequest buildEbmsPullRequest(Organisasjonsnummer meldingsformidler, KvitteringForespoersel kvitteringForespoersel) {
//        return new EbmsPullRequest(meldingsformidler(meldingsformidler), kvitteringForespoersel.getPrioritet().getEbmsPrioritet(), kvitteringForespoersel.getMpcId());
//    }
//
//    public ForretningsKvittering buildForretningsKvittering(EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering) {
//        SimpleStandardBusinessDocument simpleStandardBusinessDocument = ebmsApplikasjonsKvittering.getStandardBusinessDocument();
//
//        KvitteringsInfo.Builder kvitteringsinfoBuilder = KvitteringsInfo.builder()
//                .konversasjonsId(simpleStandardBusinessDocument.getConversationId())
//                .referanseTilMeldingId(ebmsApplikasjonsKvittering.refToMessageId);
//
//        if (simpleStandardBusinessDocument.erKvittering()) {
//            SDPKvittering sdpKvittering = simpleStandardBusinessDocument.getKvittering().kvittering;
//            kvitteringsinfoBuilder.tidspunkt(sdpKvittering.getTidspunkt().toInstant());
//
//            final KvitteringsInfo kvitteringsInfo = kvitteringsinfoBuilder.build();
//
//            if (sdpKvittering.getAapning() != null) {
//                return new AapningsKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
//            } else if (sdpKvittering.getMottak() != null) {
//                return new MottaksKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
//            } else if (sdpKvittering.getLevering() != null) {
//                return new LeveringsKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
//            } else if (sdpKvittering.getVarslingfeilet() != null) {
//                return varslingFeiletKvittering(sdpKvittering, kvitteringsInfo, ebmsApplikasjonsKvittering);
//            } else if (sdpKvittering.getReturpost() != null) {
//                return new ReturpostKvittering(ebmsApplikasjonsKvittering, kvitteringsInfo);
//            }
//        } else if (simpleStandardBusinessDocument.erFeil()) {
//            SDPFeil sdpFeil = simpleStandardBusinessDocument.getFeil();
//            kvitteringsinfoBuilder.tidspunkt(sdpFeil.getTidspunkt().toInstant());
//
//            final KvitteringsInfo kvitteringsInfo = kvitteringsinfoBuilder.build();
//
//            return feil(ebmsApplikasjonsKvittering, kvitteringsInfo);
//        }
//
//        throw new SikkerDigitalPostException("Kvittering tilbake fra meldingsformidler var verken kvittering eller feil.");
//    }
//
//    private ForretningsKvittering feil(EbmsApplikasjonsKvittering ebmsApplikasjonsKvittering, KvitteringsInfo kvitteringsInfo) {
//        SDPFeil feil = ebmsApplikasjonsKvittering.getStandardBusinessDocument().getFeil();
//
//        return Feil.builder(ebmsApplikasjonsKvittering, kvitteringsInfo, mapFeilType(feil.getFeiltype()))
//                .detaljer(feil.getDetaljer())
//                .build();
//    }
//
//    private ForretningsKvittering varslingFeiletKvittering(SDPKvittering sdpKvittering, KvitteringsInfo kvitteringsInfo, EbmsApplikasjonsKvittering ebmsAapplikasjonsKvittering) {
//        SDPVarslingfeilet varslingfeilet = sdpKvittering.getVarslingfeilet();
//        VarslingFeiletKvittering.Varslingskanal varslingskanal = mapVarslingsKanal(varslingfeilet.getVarslingskanal());
//
//        return VarslingFeiletKvittering.builder(ebmsAapplikasjonsKvittering, kvitteringsInfo, varslingskanal)
//                .beskrivelse(varslingfeilet.getBeskrivelse())
//                .build();
//    }
//
//    private Feil.Feiltype mapFeilType(SDPFeiltype feiltype) {
//        if (feiltype == SDPFeiltype.KLIENT) {
//            return Feil.Feiltype.KLIENT;
//        }
//        return Feil.Feiltype.SERVER;
//    }
//
//    private VarslingFeiletKvittering.Varslingskanal mapVarslingsKanal(SDPVarslingskanal varslingskanal) {
//        if (varslingskanal == SDPVarslingskanal.EPOST) {
//            return VarslingFeiletKvittering.Varslingskanal.EPOST;
//        }
//        return VarslingFeiletKvittering.Varslingskanal.SMS;
//    }

}
