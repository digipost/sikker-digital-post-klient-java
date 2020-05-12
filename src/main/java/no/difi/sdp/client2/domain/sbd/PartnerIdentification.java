package no.difi.sdp.client2.domain.sbd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.digipost.api.representations.Organisasjonsnummer;

import java.io.Serializable;
import java.util.Objects;


public class PartnerIdentification implements Serializable {

    @JsonIgnore
    private Partner partner;

    protected String value;

    protected String authority;

    public PartnerIdentification() {
    }

    public PartnerIdentification(String value, String authority) {
        this.value = value;
        this.authority = authority;
    }

    @JsonIgnore
    String getStrippedValue() {
        if (value == null) {
            return null;
        }

        return Organisasjonsnummer.erGyldig(value) ? Organisasjonsnummer.of(value).toString() : value;
    }


    public Partner getPartner() {
        return this.partner;
    }

    public String getValue() {
        return this.value;
    }

    public String getAuthority() {
        return this.authority;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartnerIdentification that = (PartnerIdentification) o;
        return Objects.equals(partner, that.partner) &&
                Objects.equals(value, that.value) &&
                Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partner, value, authority);
    }

    public String toString() {
        return "PartnerIdentification(value=" + this.getValue() + ", authority=" + this.getAuthority() + ")";
    }
}
