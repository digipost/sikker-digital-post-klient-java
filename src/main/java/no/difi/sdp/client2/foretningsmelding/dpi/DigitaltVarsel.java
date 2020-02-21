package no.difi.sdp.client2.foretningsmelding.dpi;

public class DigitaltVarsel {
    public String epostTekst;
    public String smsTekst;

    public DigitaltVarsel() { }

    public DigitaltVarsel(String epostTekst, String smsTekst) {
        this.epostTekst = epostTekst;
        this.smsTekst = smsTekst;
    }
}
