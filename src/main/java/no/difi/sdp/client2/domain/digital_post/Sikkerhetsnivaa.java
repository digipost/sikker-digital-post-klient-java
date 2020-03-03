package no.difi.sdp.client2.domain.digital_post;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Sikkerhetsnivaa {

    /**
     * "Mellomhøyt" sikkerhetsnivå.
     *
     * Vanligvis passord.
     */
    NIVAA_3(3),

    /**
     * Offentlig godkjent to-faktor elektronisk ID.
     *
     * For eksempel BankID, Buypass eller Commfides.
     */
    NIVAA_4(4);

    private final int verdi;

    Sikkerhetsnivaa(int verdi) {
        this.verdi = verdi;
    }

    @JsonValue
    public int getVerdi() {
        return verdi;
    }
}
