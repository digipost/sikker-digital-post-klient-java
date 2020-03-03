package no.difi.sdp.client2.domain.digital_post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.difi.sdp.client2.domain.ForretningMeldingsType;
import no.difi.sdp.client2.domain.ForretningsMelding;
import no.difi.sdp.client2.domain.Mottaker;

import java.util.Date;

public class DigitalPost extends ForretningsMelding {


    private Mottaker mottaker;
    private String tittel;

    private DigitalPostInfo digitalPostInfo = new DigitalPostInfo();
    private Sikkerhetsnivaa sikkerhetsnivaa = Sikkerhetsnivaa.NIVAA_4;
    private DigitaltVarsel varsler = new DigitaltVarsel();
    private Spraak spraak = Spraak.NO;

    private DigitalPost(Mottaker mottaker) {
        super(ForretningMeldingsType.DIGITAL);
        this.mottaker = mottaker;
    }

    @JsonIgnore
    public Mottaker getMottaker() {
        return mottaker;
    }

    @JsonIgnore
    public Date getVirkningsdato() {
        return Date.from(digitalPostInfo.getVirkningsdato());
    }

    @JsonIgnore
    public boolean isAapningskvittering() {
        return digitalPostInfo.isAapningskvittering();
    }

    public DigitalPostInfo getDigitalPostInfo() {
        return digitalPostInfo;
    }

    public Sikkerhetsnivaa getSikkerhetsnivaa() {
        return sikkerhetsnivaa;
    }

    @JsonIgnore
    public EpostVarsel getEpostVarsel() {
        return varsler.getEpostVarsel();
    }

    @JsonIgnore
    public SmsVarsel getSmsVarsel() {
        return varsler.getSmsVarsel();
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public String getTittel() {
        return tittel;
    }

    public Spraak getSpraak() {
        return spraak;
    }

    public DigitaltVarsel getVarsler() {
        return varsler;
    }

    /**
     * @param mottaker           Mottaker av digital post.
     */
    public static Builder builder(Mottaker mottaker) {
        return new Builder(mottaker);
    }

    public static class Builder {

        private final DigitalPost target;
        private boolean built = false;

        private Builder(Mottaker mottaker) {
            target = new DigitalPost(mottaker);
        }

        /**
         * Når brevet tilgjengeliggjøres for mottaker.
         * <p>
         * Standard er nå.
         */
        public Builder virkningsdato(Date virkningsdato) {
            target.digitalPostInfo.setVirkningsdato(virkningsdato);
            return this;
        }

        /**
         * Ønskes kvittering når brevet blir åpnet av mottaker?
         * <p>
         * Standard er false.
         */
        public Builder aapningskvittering(boolean aapningskvittering) {
            target.digitalPostInfo.setAapningskvittering(aapningskvittering);
            return this;
        }

        /**
         * Nødvendig autentiseringsnivå som kreves av mottaker i postkassen for å åpne brevet.
         * <p>
         * Standard er {@link Sikkerhetsnivaa#NIVAA_4}.
         */
        public Builder sikkerhetsnivaa(Sikkerhetsnivaa sikkerhetsnivaa) {
            target.sikkerhetsnivaa = sikkerhetsnivaa;
            return this;
        }

        /**
         * Minimum e-postvarsel som skal sendes til mottaker av brevet. Postkassen kan velge å sende andre varsler i tillegg.
         * <p>
         * Standard er standardoppførselen til postkasseleverandøren.
         */
        public Builder epostVarsel(EpostVarsel epostVarsel) {
            target.varsler.setEpostVarsel(epostVarsel);
            return this;
        }

        /**
         * Minimum sms-varsel som skal sendes til mottaker av brevet. Postkassen kan velge å sende andre varsler i tillegg.
         * <p>
         * Standard er standardoppførselen til postkasseleverandøren.
         */
        public Builder smsVarsel(SmsVarsel smsVarsel) {
            target.varsler.setSmsVarsel(smsVarsel);
            return this;
        }

        public Builder tittel(String tittel) {
            target.tittel = tittel;
            return this;
        }

        public DigitalPost build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;

            return target;
        }
    }

//    public static DigitalPost from(Forsendelse forsendelse) {
//        int sikkerhetsnivaa = forsendelse.getDigitalPost().getSikkerhetsnivaa().getVerdi();
//        String hoveddokument = forsendelse.getDokumentpakke().getHoveddokument().getFilnavn();
//        String tittel = forsendelse.getDokumentpakke().getHoveddokument().getTittel();
//        String spraak = "NO";
//        DigitalPostInfo digitalPostInfo = new DigitalPostInfo(forsendelse.getDigitalPost().getVirkningsdato(), forsendelse.getDigitalPost().isAapningskvittering());
//        DigitaltVarsel varsler = new DigitaltVarsel(forsendelse.getDigitalPost().getEpostVarsel().getVarslingsTekst(), forsendelse.getDigitalPost().getSmsVarsel().getVarslingsTekst());
//        return new DigitalPost(sikkerhetsnivaa, hoveddokument, tittel, spraak, digitalPostInfo, varsler);
//    }


//{
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

}
