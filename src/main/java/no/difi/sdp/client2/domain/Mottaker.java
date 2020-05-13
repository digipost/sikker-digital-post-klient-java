package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

public class Mottaker {

    private final String personidentifikator;
    private final String postkasseadresse;
    private final TekniskMottaker postkasse;

    private Mottaker(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, Organisasjonsnummer organisasjonsnummerPostkasse) {
        this.personidentifikator = personidentifikator;
        this.postkasseadresse = postkasseadresse;
        this.postkasse = new TekniskMottaker(organisasjonsnummerPostkasse, mottakerSertifikat);
    }

    @Deprecated
    public TekniskMottaker getMottakersPostkasse() {
    	return postkasse;
    }

    @Deprecated
    public String getPostkasseadresse() {
        return postkasseadresse;
    }

    public String getPersonidentifikator() {
        return personidentifikator;
    }


    /**
     * @see #builder(String)
     */
    @Deprecated
    public static Builder builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, Organisasjonsnummer organisasjonsnummerPostkasse) {
        return new Builder(personidentifikator, postkasseadresse, mottakerSertifikat, organisasjonsnummerPostkasse);
    }

    /**
     *
     * @param personidentifikator Identifikator (fødselsnummer eller D-nummer) til mottaker av brevet.
     */
    public static Builder builder(String personidentifikator) {
        return new Builder(personidentifikator, null, null, null);
    }

    public static class Builder {
        private final Mottaker target;
        private boolean built = false;

        private Builder(String personidentifikator, String postkasseadresse, Sertifikat mottakerSertifikat, Organisasjonsnummer organisasjonsnummerPostkasse) {
            target = new Mottaker(personidentifikator, postkasseadresse, mottakerSertifikat, organisasjonsnummerPostkasse);
        }

        public Mottaker build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
