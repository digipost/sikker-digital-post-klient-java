package no.difi.sdp.client2.foretningsmelding;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ForretningsMelding {

    @JsonIgnore
    private ForretningMeldingsType type;

    public Integer sikkerhetsnivaa;

    public String hoveddokument;

    public ForretningsMelding(ForretningMeldingsType type, Integer sikkerhetsnivaa, String hoveddokument) {
        this.type = type;
        this.sikkerhetsnivaa = sikkerhetsnivaa;
        this.hoveddokument = hoveddokument;
    }

    public ForretningsMelding(ForretningMeldingsType type) {
        this.type = type;
    }

    public ForretningsMelding() {
    }

    public void setSikkerhetsnivaa(Integer sikkerhetsnivaa) {
        this.sikkerhetsnivaa = sikkerhetsnivaa;
    }

    public void setHoveddokument(String hoveddokument) {
        this.hoveddokument = hoveddokument;
    }

    public String getType() {
        return this.type.getType();
    }

    public Integer getSikkerhetsnivaa() {
        return this.sikkerhetsnivaa;
    }

    public String getHoveddokument() {
        return this.hoveddokument;
    }

    public void setType(ForretningMeldingsType type) {
        this.type = type;
    }

    public String toString() {
        return "ForetningsMelding(type=" + this.getType() + ", sikkerhetsnivaa=" + this.getSikkerhetsnivaa() + ", hoveddokument=" + this.getHoveddokument() + ")";
    }
}
