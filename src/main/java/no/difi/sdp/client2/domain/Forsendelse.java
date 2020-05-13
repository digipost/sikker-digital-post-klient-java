package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPost;

import java.util.UUID;

import static no.difi.sdp.client2.domain.Forsendelse.Type.DIGITAL;
import static no.difi.sdp.client2.domain.Forsendelse.Type.FYSISK;

public class Forsendelse {

	public enum Type {
        DIGITAL("urn:no:difi:digitalpost:xsd:digital::digital"),
        FYSISK("urn:no:difi:digitalpost:xsd:fysisk::print"),
        ;

        public final String type;

        Type(String type) {
            this.type = type;
        }
    }

	public final Type type;
    private final DigitalPost digitalPost;
    private final FysiskPost fysiskPost;
    private final Dokumentpakke dokumentpakke;
    private final Mottaker mottaker;
    private final Avsender avsender;
    private String konversasjonsId = UUID.randomUUID().toString();
    private String spraakkode = "NO";
    private String mpcId;

    private Forsendelse(Avsender avsender, DigitalPost digitalPost, Dokumentpakke dokumentpakke, Mottaker mottaker) {
        this.mottaker = mottaker;
        this.type = DIGITAL;
        this.avsender = avsender;
        this.digitalPost = digitalPost;
        this.fysiskPost = null;
        this.dokumentpakke = dokumentpakke;
    }

    private Forsendelse(Avsender avsender, FysiskPost fysiskPost, Dokumentpakke dokumentpakke, Mottaker mottaker) {
        this.mottaker = mottaker;
        this.type = FYSISK;
    	this.avsender = avsender;
    	this.dokumentpakke = dokumentpakke;
    	this.fysiskPost = fysiskPost;
    	this.digitalPost = null;
    }

	public String getKonversasjonsId() {
        return konversasjonsId;
    }

    public DigitalPost getDigitalPost() {
        return digitalPost;
    }

	public FysiskPost getFysiskPost() {
		return fysiskPost;
    }

    public ForretningsMelding getForretningsMelding() {
        if (digitalPost != null) {
            return digitalPost;
        } else
        return (fysiskPost);
    }

    public Dokumentpakke getDokumentpakke() {
        return dokumentpakke;
    }

    public String getSpraakkode() {
        return spraakkode;
    }

    public String getMpcId() {
        return mpcId;
    }

    /**
     *
     * @return returnerer alltid Prioritet.NORMAL.
     */
    @Deprecated
    public Prioritet getPrioritet() {
        return Prioritet.NORMAL;
    }

    public Avsender getAvsender() {
        return avsender;
    }

    /**
     * @param avsender Ansvarlig avsender av forsendelsen. Dette vil i de aller fleste tilfeller være
     *                             den offentlige virksomheten som er ansvarlig for brevet som skal sendes.
     * @param digitalPost Informasjon som brukes av postkasseleverandør for å behandle den digitale posten.
     * @param dokumentpakke Pakke med hoveddokument og evt vedlegg som skal sendes.
     */
    public static Builder digital(Avsender avsender, DigitalPost digitalPost, Dokumentpakke dokumentpakke) {
        return new Builder(avsender, digitalPost, dokumentpakke);
    }

	public static Builder fysisk(Avsender avsender, FysiskPost fysiskPost, Dokumentpakke dokumentpakke, Mottaker mottaker) {
	    return new Builder(avsender, fysiskPost, dokumentpakke, mottaker);
    }

    public static class Builder {

        private final Forsendelse target;
        private boolean built = false;

        private Builder(Avsender avsender, DigitalPost digitalPost, Dokumentpakke dokumentpakke) {
            this.target = new Forsendelse(avsender, digitalPost, dokumentpakke, digitalPost.getMottaker());
        }

        private Builder(Avsender avsender, FysiskPost fysiskPost, Dokumentpakke dokumentpakke, Mottaker mottaker) {
            this.target = new Forsendelse(avsender, fysiskPost, dokumentpakke, mottaker);
        }

        /**
         * Unik ID opprettet og definert i en initiell melding og siden bruk i alle tilhørende kvitteringer knyttet til den opprinnelige meldingen.
         * Skal være unik for en avsender.
         *
         * Standard er {@link java.util.UUID#randomUUID()}}.
         */
        public Builder konversasjonsId(String konversasjonsId) {
            target.konversasjonsId = konversasjonsId;
            return this;
        }

        /**
         * Språkkode i henhold til ISO-639-1 (2 bokstaver). Brukes til å informere postkassen om hvilket språk som benyttes, slik at varselet om mulig kan vises i riktig språkkontekst.
         *
         * Standard er NO.
         */
        public Builder spraakkode(String spraakkode) {
            target.spraakkode = spraakkode;
            return this;
        }

        /**
         * Klientbibliotek har ikke lengre forhold til mpcID.
         */
        @Deprecated
        public Builder mpcId(String mpcId) {
            target.mpcId = mpcId;
            return this;
        }

        /**
         * Klientbibliotek har ikke lengre forhold til Prioritet.
         */
        @Deprecated
        public Builder prioritet(Prioritet prioritet) {
            return this;
        }

        public Forsendelse build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }

    public Mottaker getMottaker() {
        return mottaker;
    }

    public TekniskMottaker getTekniskMottaker() {
		switch (type) {
    		case DIGITAL: return mottaker.getMottakersPostkasse();
    		case FYSISK: return fysiskPost.getUtskriftsleverandoer();
    		default: throw new IllegalStateException("Forsendelse av type " + type + " har ikke teknisk mottaker");
		}
    }

}
