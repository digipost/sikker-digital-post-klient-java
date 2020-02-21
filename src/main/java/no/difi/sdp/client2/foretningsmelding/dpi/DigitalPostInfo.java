package no.difi.sdp.client2.foretningsmelding.dpi;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class DigitalPostInfo {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date virkningsdato;
    public boolean aapningskvittering;

    public DigitalPostInfo() {
    }

    public DigitalPostInfo(Date virkningsdato, boolean aapningskvittering) {
        this.virkningsdato = virkningsdato;
        this.aapningskvittering = aapningskvittering;
    }
}
