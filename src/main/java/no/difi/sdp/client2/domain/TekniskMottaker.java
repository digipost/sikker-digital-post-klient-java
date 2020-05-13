package no.difi.sdp.client2.domain;

import no.digipost.api.representations.Organisasjonsnummer;

/**
 * Benyttes ikke.
 */
@Deprecated
public class TekniskMottaker {

	public final Organisasjonsnummer organisasjonsnummer;
	@Deprecated
	public final Sertifikat sertifikat;

    public TekniskMottaker(Organisasjonsnummer organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.sertifikat = null;
    }

    public TekniskMottaker(Organisasjonsnummer organisasjonsnummer, Sertifikat sertifikat) {
	    this.organisasjonsnummer = organisasjonsnummer;
	    this.sertifikat = sertifikat;
    }

}
