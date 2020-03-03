package no.difi.sdp.client2.domain.fysisk_post;

public enum Posttype {
	A_PRIORITERT ("A"),
	B_OEKONOMI   ("B");

	public final String sdpType;

	Posttype(String sdpType) {
		this.sdpType = sdpType;
	}
}
