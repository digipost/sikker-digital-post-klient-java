package no.difi.sdp.client2.domain.digital_post;

public class SmsVarsel extends Varsel {

    private SmsVarsel(String varslingsTekst) {
        super(varslingsTekst);
    }

    /**
     * @param varslingsTekst Avsenderstyrt varslingstekst som skal inngå i varselet.
     */
    public static Builder builder(String varslingsTekst) {
        return new Builder(varslingsTekst);
    }

    public static class Builder {
        private SmsVarsel target;
        private boolean built = false;

        private Builder(String varslingsTekst) {
            target = new SmsVarsel(varslingsTekst);
        }

        public SmsVarsel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
