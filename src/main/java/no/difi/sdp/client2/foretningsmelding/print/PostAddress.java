package no.difi.sdp.client2.foretningsmelding.print;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PostAddress implements Serializable {

    public String navn;
    public String adresselinje1;
    public String adresselinje2;
    public String adresselinje3;
    public String adresselinje4;
    public String postnummer;
    public String poststed;
    public String landkode;
    public String land;

    public PostAddress() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostAddress that = (PostAddress) o;
        return Objects.equals(navn, that.navn) &&
                Objects.equals(adresselinje1, that.adresselinje1) &&
                Objects.equals(adresselinje2, that.adresselinje2) &&
                Objects.equals(adresselinje3, that.adresselinje3) &&
                Objects.equals(adresselinje4, that.adresselinje4) &&
                Objects.equals(postnummer, that.postnummer) &&
                Objects.equals(poststed, that.poststed) &&
                Objects.equals(landkode, that.landkode) &&
                Objects.equals(land, that.land);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navn, adresselinje1, adresselinje2, adresselinje3, adresselinje4, postnummer, poststed, landkode, land);
    }

    public String toString() {
        return "PostAddress(navn=" + this.navn + ", adresselinje1=" + this.adresselinje1 + ", adresselinje2=" + this.adresselinje2 + ", adresselinje3=" + this.adresselinje3 + ", adresselinje4=" + this.adresselinje4 + ", postnummer=" + this.postnummer + ", poststed=" + this.poststed + ", landkode=" + this.landkode + ", land=" + this.land + ")";
    }
}
