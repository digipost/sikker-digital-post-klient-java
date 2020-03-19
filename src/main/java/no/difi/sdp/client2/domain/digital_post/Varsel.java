package no.difi.sdp.client2.domain.digital_post;

import java.util.Collections;
import java.util.List;

public abstract class Varsel {

    protected Varsel(String varslingsTekst) {
        this.varslingsTekst = varslingsTekst;
    }

    protected String varslingsTekst;

    public String getVarslingsTekst() {
        return varslingsTekst;
    }

}
