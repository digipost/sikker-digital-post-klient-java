package no.difi.sdp.client2.domain.digital_post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.difi.sdp.client2.domain.ForretningsMelding;
import no.difi.sdp.client2.domain.ForretningsMeldingType;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Mottaker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DigitalPost extends ForretningsMelding {


    private Mottaker mottaker;
    private String tittel;

    private DigitalPostInfo digitalPostInfo = new DigitalPostInfo();
    private Sikkerhetsnivaa sikkerhetsnivaa = Sikkerhetsnivaa.NIVAA_4;
    private DigitaltVarsel varsler = new DigitaltVarsel();
    private Spraak spraak = Spraak.NO;
    private Map<String, String> metadatafiler = new HashMap<>();

    private DigitalPost(Mottaker mottaker, String tittel) {
        super(ForretningsMeldingType.DIGITAL);
        this.mottaker = mottaker;
        this.tittel = tittel;
    }

    /**
     * @see Forsendelse#getMottaker()
     */
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

    /**
     *
     * @see #getVarsler()
     */
    @JsonIgnore
    @Deprecated
    public EpostVarsel getEpostVarsel() {
        return EpostVarsel.builder(varsler.getEpostTekst()).build();
    }

    /**
     *
     * @see #getVarsler()
     */
    @JsonIgnore
    @Deprecated
    public SmsVarsel getSmsVarsel() {
        return SmsVarsel.builder(varsler.getSmsTekst()).build();
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public String getTittel() {
        return tittel;
    }

    @JsonIgnore
    public String getIkkeSensitivTittel() {
        return tittel;
    }

    public Spraak getSpraak() {
        return spraak;
    }

    public DigitaltVarsel getVarsler() {
        return varsler;
    }

    public void addMetadataMapping(String dokumentTittel, String metaDokumentTittel) {
        metadatafiler.put(dokumentTittel, metaDokumentTittel);
    }

    public Map<String, String> getMetadataFiler() {
        return metadatafiler;
    }

    /**
     * @param mottaker           Mottaker av digital post.
     * @param tittel Ikke-sensitiv tittel på brevet.
     *                           Denne tittelen vil være synlig under transport av meldingen og kan vises i mottakerens postkasse selv om det ikke er autentisert med tilstrekkelig autentiseringsnivå.
     */
    public static Builder builder(Mottaker mottaker, String tittel) {
        return new Builder(mottaker, tittel);
    }

    public static class Builder {

        private final DigitalPost target;
        private boolean built = false;

        private Builder(Mottaker mottaker, String tittel) {
            target = new DigitalPost(mottaker, tittel);
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
            target.varsler.setEpostTekst(epostVarsel.getVarslingsTekst());
            return this;
        }

        /**
         * Minimum sms-varsel som skal sendes til mottaker av brevet. Postkassen kan velge å sende andre varsler i tillegg.
         * <p>
         * Standard er standardoppførselen til postkasseleverandøren.
         */
        public Builder smsVarsel(SmsVarsel smsVarsel) {
            target.varsler.setSmsTekst(smsVarsel.getVarslingsTekst());
            return this;
        }

        public DigitalPost build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;

            return target;
        }
    }

}
