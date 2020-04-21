package no.difi.sdp.client2.domain.digital_post;

public class DigitaltVarsel {

    private String epostTekst;
    private String smsTekst;

    public DigitaltVarsel() {
    }

    public String getEpostTekst(){
        return epostTekst;
    }

    public String getSmsTekst(){
        return smsTekst;
    }

    public void setEpostTekst(String epostTekst) {
        this.epostTekst = epostTekst;
    }

    public void setSmsTekst(String smsTekst) {
        this.smsTekst = smsTekst;
    }
}
