package no.difi.sdp.client2.domain;

public class Databehandler {

    public final DatabehandlerOrganisasjonsnummer organisasjonsnummer;

    private Databehandler(DatabehandlerOrganisasjonsnummer organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    /**
     * @param organisasjonsnummer Organisasjonsnummeret til avsender av brevet.
     */
    public static Builder builder(DatabehandlerOrganisasjonsnummer organisasjonsnummer) {
        return new Builder(organisasjonsnummer);
    }

    public static class Builder {

        private final Databehandler target;
        private boolean built = false;

        private Builder(DatabehandlerOrganisasjonsnummer organisasjonsnummer) {
            target = new Databehandler(organisasjonsnummer);
        }

        public Databehandler build() {
            if (built) throw new IllegalStateException("Can't build twice");
            built = true;
            return this.target;
        }
    }
}
