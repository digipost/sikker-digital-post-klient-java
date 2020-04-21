package no.difi.sdp.client2.domain.sbd;

import java.util.Objects;

public class DocumentType {

    private String type;

    private String standard;

    public DocumentType() {
    }

    public String getType() {
        return this.type;
    }

    public String getStandard() {
        return this.standard;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentType that = (DocumentType) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(standard, that.standard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, standard);
    }

    public String toString() {
        return "DocumentType(type=" + this.getType() + ", standard=" + this.getStandard() + ")";
    }
}
