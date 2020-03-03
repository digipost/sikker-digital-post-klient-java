package no.difi.sdp.client2.domain.digital_post;

public class EpostVarsel extends Varsel {

    private EpostVarsel(String varslingsTekst) {
        super(varslingsTekst);
    }

    /**
     * @param varslingsTekst Avsenderstyrt varslingstekst som skal inngå i varselet.
     */
    public static Builder builder(String varslingsTekst) {
        return new Builder(varslingsTekst);
    }

    public static class Builder {
        private EpostVarsel target;
        private boolean built = false;

        private Builder(String varslingsTekst) {
            target = new EpostVarsel(varslingsTekst);
        }

        public EpostVarsel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
