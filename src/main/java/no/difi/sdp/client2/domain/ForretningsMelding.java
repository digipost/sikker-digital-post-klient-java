package no.difi.sdp.client2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ForretningsMelding {

    @JsonIgnore
    private ForretningsMeldingType type;

    public String hoveddokument;

    private String avsenderId;
    private String fakturaReferanse;

    public ForretningsMelding(ForretningsMeldingType type, String hoveddokument) {
        this.type = type;
        this.hoveddokument = hoveddokument;
    }

    public ForretningsMelding(ForretningsMeldingType type) {
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

    public String getAvsenderId() {
        return avsenderId;
    }

    public void setAvsenderId(String avsenderId) {
        this.avsenderId = avsenderId;
    }

    public String getFakturaReferanse() {
        return fakturaReferanse;
    }

    public void setFakturaReferanse(String fakturaReferanse) {
        this.fakturaReferanse = fakturaReferanse;
    }

    public boolean is(ForretningsMeldingType type){
        return this.type.equals(type);
    }

    public void setType(ForretningsMeldingType type) {
        this.type = type;
    }

    public String toString() {
        return "ForetningsMelding(type=" + this.getType() + ", hoveddokument=" + this.getHoveddokument() + ")";
    }
}
