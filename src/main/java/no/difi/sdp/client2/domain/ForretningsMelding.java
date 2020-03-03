package no.difi.sdp.client2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ForretningsMelding {

    @JsonIgnore
    private ForretningMeldingsType type;

    public String hoveddokument;

    public ForretningsMelding(ForretningMeldingsType type, String hoveddokument) {
        this.type = type;
        this.hoveddokument = hoveddokument;
    }

    public ForretningsMelding(ForretningMeldingsType type) {
        this.type = type;
    }

    public ForretningsMelding() {
    }

    public void setHoveddokument(String hoveddokument) {
        this.hoveddokument = hoveddokument;
    }

    public String getType() {
        return this.type.getType();
    }

    public String getHoveddokument() {
        return this.hoveddokument;
    }

    public void setType(ForretningMeldingsType type) {
        this.type = type;
    }

    public String toString() {
        return "ForetningsMelding(type=" + this.getType() + ", hoveddokument=" + this.getHoveddokument() + ")";
    }
}
