package no.difi.sdp.client2.domain.fysisk_post;

public enum Returhaandtering {

	DIREKTE_RETUR            ("DIREKTE_RETUR"),
	MAKULERING_MED_MELDING   ("MAKULERING_MED_MELDING");


	public final String sdpReturhaandtering;

	private Returhaandtering(String sdpReturhaandtering) {
		this.sdpReturhaandtering = sdpReturhaandtering;
	}
}
