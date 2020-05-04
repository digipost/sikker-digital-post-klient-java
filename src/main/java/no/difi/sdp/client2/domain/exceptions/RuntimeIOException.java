package no.difi.sdp.client2.domain.exceptions;

import java.io.IOException;

/**
 * Wrapper for IOExceptions i situasjoner der det er ingen/liten praktisk grunn til å anta at en IOException kan oppstå (f.eks. minnerepresentasjoner av streams).
 */
@Deprecated
public class RuntimeIOException extends SikkerDigitalPostException {
    @Deprecated
    public RuntimeIOException(IOException e) {
        super(e);
    }

}
