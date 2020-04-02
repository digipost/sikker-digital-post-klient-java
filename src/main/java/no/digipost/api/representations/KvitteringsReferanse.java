package no.digipost.api.representations;

import org.w3.xmldsig.Reference;

@Deprecated
public class KvitteringsReferanse {

    private final String marshalled;

    private KvitteringsReferanse(String marshalledReference) {
        marshalled = marshalledReference;
    }

    @Deprecated
    public static Builder builder(String marshalledReference) {
        return new Builder(marshalledReference);
    }

    @Deprecated
    public String getMarshalled() {
        return marshalled;
    }

    @Deprecated
    public Reference getUnmarshalled() {
        return null;
    }


    @Deprecated
    public static class Builder {
        private KvitteringsReferanse target;
        private boolean built = false;

        private Builder(String marshalledReference) {
            this.target = new KvitteringsReferanse(marshalledReference);
        }

        @Deprecated
        public KvitteringsReferanse build() {
            if (built) {
                throw new IllegalStateException("Kan ikke bygges flere ganger.");
            }
            built = true;
            return target;
        }
    }
}
