package no.difi.sdp.client2.domain.digital_post;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DigitaltVarsel {

    private EpostVarsel epostVarsel;
    private SmsVarsel smsVarsel;

    public DigitaltVarsel() {
    }


    public String getEpostTekst(){
        return epostVarsel.getVarslingsTekst();
    }

    public String getSmsTekst(){
        return smsVarsel.getVarslingsTekst();
    }

    @JsonIgnore
    public EpostVarsel getEpostVarsel() {
        return epostVarsel;
    }

    public void setEpostVarsel(EpostVarsel epostVarsel) {
        this.epostVarsel = epostVarsel;
    }

    @JsonIgnore
    public SmsVarsel getSmsVarsel() {
        return smsVarsel;
    }

    public void setSmsVarsel(SmsVarsel smsVarsel) {
        this.smsVarsel = smsVarsel;
    }
}
