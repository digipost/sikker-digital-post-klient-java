package no.difi.sdp.client2.domain.sbd;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class DocumentIdentification implements Serializable {

    protected String standard;
    protected String typeVersion;
    protected String instanceIdentifier;
    protected String type;
    protected Boolean multipleType;
    protected ZonedDateTime creationDateAndTime;

    public DocumentIdentification() {
    }

    public DocumentIdentification(String standard, String typeVersion, String instanceIdentifier, String type, ZonedDateTime creationDateAndTime) {
        this.standard = standard;
        this.typeVersion = typeVersion;
        this.instanceIdentifier = instanceIdentifier;
        this.type = type;
        this.multipleType = multipleType;
        this.creationDateAndTime = creationDateAndTime;
    }

    public String getStandard() {
        return this.standard;
    }

    public String getTypeVersion() {
        return this.typeVersion;
    }

    public String getInstanceIdentifier() {
        return this.instanceIdentifier;
    }

    public String getType() {
        return this.type;
    }

    public ZonedDateTime getCreationDateAndTime() {
        return this.creationDateAndTime;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public void setTypeVersion(String typeVersion) {
        this.typeVersion = typeVersion;
    }

    public void setInstanceIdentifier(String instanceIdentifier) {
        this.instanceIdentifier = instanceIdentifier;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCreationDateAndTime(ZonedDateTime creationDateAndTime) {
        this.creationDateAndTime = creationDateAndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentIdentification that = (DocumentIdentification) o;
        return Objects.equals(standard, that.standard) &&
                Objects.equals(typeVersion, that.typeVersion) &&
                Objects.equals(instanceIdentifier, that.instanceIdentifier) &&
                Objects.equals(type, that.type) &&
                Objects.equals(multipleType, that.multipleType) &&
                Objects.equals(creationDateAndTime, that.creationDateAndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(standard, typeVersion, instanceIdentifier, type, multipleType, creationDateAndTime);
    }

    public String toString() {
        return "DocumentIdentification(standard=" + this.standard + ", typeVersion=" + this.typeVersion + ", instanceIdentifier=" + this.instanceIdentifier + ", type=" + this.type + ", multipleType=" + this.multipleType + ", creationDateAndTime=" + this.creationDateAndTime + ")";
    }
}
