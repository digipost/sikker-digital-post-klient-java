package no.difi.sdp.client2.domain.sbd;


public enum Process {

    DIGITAL_POST_INFO("urn:no:difi:profile:digitalpost:info:ver1.0"),
    DIGITAL_POST_VEDTAK("urn:no:difi:profile:digitalpost:vedtak:ver1.0"),
    ;

    private final String value;

    Process(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
