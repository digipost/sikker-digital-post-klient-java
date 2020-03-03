package no.difi.sdp.client2.domain.fysisk_post;

public enum Utskriftsfarge {
	SORT_HVIT   ("SORT_HVIT"),
	FARGE       ("FARGE");

	public final String sdpUtskriftsfarge;

	private Utskriftsfarge(String sdpUtskriftsfarge) {
		this.sdpUtskriftsfarge = sdpUtskriftsfarge;
	}

}
