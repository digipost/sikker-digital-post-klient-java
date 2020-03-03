package no.difi.sdp.client2.domain.kvittering;

public class KvitteringsReferanse {

    private final String marshalled;

    private KvitteringsReferanse(String marshalledReference) {
        marshalled = marshalledReference;
    }

    public static Builder builder(String marshalledReference) {
        return new Builder(marshalledReference);
    }

    public String getMarshalled() {
        return marshalled;
    }


    public static class Builder {
        private KvitteringsReferanse target;
        private boolean built = false;

        private Builder(String marshalledReference) {
            this.target = new KvitteringsReferanse(marshalledReference);
        }

        public KvitteringsReferanse build() {
            if (built) {
                throw new IllegalStateException("Kan ikke bygges flere ganger.");
            }
            built = true;
            return target;
        }

    }
}
