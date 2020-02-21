package no.difi.sdp.client2.foretningsmelding;

public enum ForretningMeldingsType {
    DIGITAL("digital"),
    PRINT("print"),
    ;

    private String type;

    ForretningMeldingsType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
