package no.difi.sdp.client2.domain;

public enum ForretningsMeldingType {
    DIGITAL("digital"),
    PRINT("print"),
    ;

    private String type;

    ForretningsMeldingType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
