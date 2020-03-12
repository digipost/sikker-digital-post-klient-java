package no.difi.sdp.client2.domain.exceptions;

/**
 * Felles superklasse for alle Exceptions som oppstår under sending/mottak av forespørsler mot meldingsformidler.
 */
public class SendException extends SikkerDigitalPostException {

    private final AntattSkyldig antattSkyldig;

    public SendException(String message, AntattSkyldig antattSkyldig, Exception e) {
        super(message, e);
        this.antattSkyldig = antattSkyldig;
    }

    public AntattSkyldig getAntattSkyldig() {
        return antattSkyldig;
    }

    public enum AntattSkyldig {
        /**
         * Feilen er trolig forårsaket av en feil i klienten eller klientoppsettet.
         * <p>
         * Å forsøke samme forespørsel igjen vil sannsynligvis ikke gjøre noe med situasjonen.
         */
        KLIENT,

        /**
         * Feilen er trolig forårsaket av en feil i meldingsformidleren.
         * <p>
         * Det kan fungere å prøve forespørselen igjen senere.
         */
        SERVER,

        /**
         * Uvisst om feilen er forårsaket av klienten eller meldingsformidleren.
         */
        UKJENT;

        public static AntattSkyldig fraHttpStatusCode(int statusKode) {

            if (statusKode / 100 == 5) {
                return SERVER;
            } else if (statusKode / 100 == 4) {
                return KLIENT;
            } else {
                return UKJENT;
            }
        }
    }

}
