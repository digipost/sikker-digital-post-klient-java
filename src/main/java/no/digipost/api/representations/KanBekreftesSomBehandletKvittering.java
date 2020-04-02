package no.digipost.api.representations;

public interface KanBekreftesSomBehandletKvittering {

    String getMeldingsId();

    @Deprecated
    default KvitteringsReferanse getReferanseTilMeldingSomKvitteres() {
        return null;
    }


}
