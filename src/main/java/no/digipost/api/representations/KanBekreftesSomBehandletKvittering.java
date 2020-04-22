package no.digipost.api.representations;

public interface KanBekreftesSomBehandletKvittering {

    //Defaulter for å bevare API.
    default Long getIntegrasjonspunktId() {
        return null;
    }

    @Deprecated
    default String getMeldingsId() {
        return null;
    }

    @Deprecated
    default KvitteringsReferanse getReferanseTilMeldingSomKvitteres() {
        return null;
    }


}
