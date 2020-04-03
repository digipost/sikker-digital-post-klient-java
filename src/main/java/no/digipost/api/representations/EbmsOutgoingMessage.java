package no.digipost.api.representations;

@Deprecated
public class EbmsOutgoingMessage {
    @Deprecated
    public enum Prioritet {
        @Deprecated
        NORMAL("normal"),
        @Deprecated
        PRIORITERT("prioritert");
        private final String value;

        Prioritet(final String value) {
            this.value = value;
        }

        public static Prioritet from(final String val) {
            for (Prioritet p : Prioritet.values()) {
                if (p.value.equals(val)) {
                    return p;
                }
            }
            throw new IllegalArgumentException("Invalid Prioritet: " + val);
        }

        public String value() {
            return value;
        }
    }

}
