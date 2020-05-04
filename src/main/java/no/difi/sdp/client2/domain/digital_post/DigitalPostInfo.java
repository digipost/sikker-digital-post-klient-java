package no.difi.sdp.client2.domain.digital_post;

import java.time.Instant;
import java.util.Date;

public class DigitalPostInfo {

    private Instant virkningsdato;
    private boolean aapningskvittering;

    public DigitalPostInfo() {
    }

    public Instant getVirkningsdato() {
        return virkningsdato;
    }

    public void setVirkningsdato(Date virkningsdato) {
        this.virkningsdato = virkningsdato.toInstant();
    }

    public boolean isAapningskvittering() {
        return aapningskvittering;
    }

    public void setAapningskvittering(boolean aapningskvittering) {
        this.aapningskvittering = aapningskvittering;
    }
}
