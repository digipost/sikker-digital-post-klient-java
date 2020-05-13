package no.difi.sdp.client2.domain.digital_post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SmsVarsel extends Varsel {

    private static final Logger LOG = LoggerFactory.getLogger(SmsVarsel.class);

    private SmsVarsel(String varslingsTekst) {
        super(varslingsTekst);
    }

    /**
     * Integrasjonspunkt er ansvarlig for å hente mobilnummer til mottaker.
     * @see #builder(String)
     */
    @Deprecated
    public static Builder builder(String mobilnummer, String varslingsTekst) {
        return new Builder(varslingsTekst);
    }

    /**
     * @param varslingsTekst Avsenderstyrt varslingstekst som skal inngå i varselet.
     */
    public static Builder builder(String varslingsTekst) {
        return new Builder(varslingsTekst);
    }

    @Deprecated
    public String getMobilnummer() {
        LOG.warn("NOT SUPPORTED");
        return null;
    }

    public static class Builder {
        private SmsVarsel target;
        private boolean built = false;

        private Builder(String varslingsTekst) {
            target = new SmsVarsel(varslingsTekst);
        }

        @Deprecated
        public Builder varselEtterDager(List<Integer> varselEtterDager) {
            LOG.warn("NOT SUPPORTED");
            return this;
        }

        public SmsVarsel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
