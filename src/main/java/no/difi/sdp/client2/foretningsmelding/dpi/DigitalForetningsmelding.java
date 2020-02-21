package no.difi.sdp.client2.foretningsmelding.dpi;

import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.foretningsmelding.ForretningsMelding;

import static no.difi.sdp.client2.foretningsmelding.ForretningMeldingsType.DIGITAL;

public class DigitalForetningsmelding extends ForretningsMelding {

    public String tittel;
    public String spraak;
    public DigitalPostInfo digitalPostInfo;
    public DigitaltVarsel varsler;

    public DigitalForetningsmelding() {
        super(DIGITAL);
    }

    public DigitalForetningsmelding(int sikkerhetsnivaa, String hoveddokument, String tittel, String spraak, DigitalPostInfo digitalPostInfo, DigitaltVarsel varsler) {
        super(DIGITAL,sikkerhetsnivaa,hoveddokument);
        this.tittel = tittel;
        this.spraak = spraak;
        this.digitalPostInfo = digitalPostInfo;
        this.varsler = varsler;
    }


    public static DigitalForetningsmelding from(Forsendelse forsendelse) {
        int sikkerhetsnivaa = Integer.parseInt(forsendelse.getDigitalPost().getSikkerhetsnivaa().getXmlValue().value());
        String hoveddokument = forsendelse.getDokumentpakke().getHoveddokument().getFilnavn();
        String tittel = forsendelse.getDokumentpakke().getHoveddokument().getTittel();
        String spraak = "NO";
        DigitalPostInfo digitalPostInfo = new DigitalPostInfo(forsendelse.getDigitalPost().getVirkningsdato(), forsendelse.getDigitalPost().isAapningskvittering());
        DigitaltVarsel varsler = new DigitaltVarsel(forsendelse.getDigitalPost().getEpostVarsel().getVarslingsTekst(), forsendelse.getDigitalPost().getSmsVarsel().getVarslingsTekst());
        return new DigitalForetningsmelding(sikkerhetsnivaa, hoveddokument, tittel, spraak, digitalPostInfo, varsler);
    }
}


///{
//    "digital": {
//        "sikkerhetsnivaa": "",
//        "hoveddokument": "",
//        "tittel": "",
//        "spraak": "NO",
//        "digitalPostInfo": {
//            "virkningsdato": "",
//            "aapningskvittering": "false"
//        },
//        "varsler": {
//            "epostTekst": "Varseltekst",
//            "smsTekst": "Varseltekst"
//        }
//    }
//}
