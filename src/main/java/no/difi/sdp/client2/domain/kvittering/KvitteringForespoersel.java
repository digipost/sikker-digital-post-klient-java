package no.difi.sdp.client2.domain.kvittering;


public class KvitteringForespoersel {

    private String mpcId;

    private KvitteringForespoersel() {
    }


    public static Builder builder() {
        return new Builder();
    }

    public String getMpcId() {
        return mpcId;
    }

    public static class Builder {
        private final KvitteringForespoersel target;
        private boolean built = false;

        private Builder() {
            target = new KvitteringForespoersel();
        }

        /**
         * Brukes til å skille mellom ulike kvitteringskøer for samme tekniske avsender. En forsendelse gjort med en
         * MPC Id vil kun dukke opp i kvitteringskøen med samme MPC Id.
         * <p>
         * Standardverdi er blank MPC Id.
         *
         * @see no.difi.sdp.client2.domain.Forsendelse.Builder#mpcId(String)
         */
        public Builder mpcId(String mpcId) {
            target.mpcId = mpcId;
            return this;
        }

        public KvitteringForespoersel build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return target;
        }
    }
}
