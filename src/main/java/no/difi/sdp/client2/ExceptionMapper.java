package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.exceptions.MessageSenderIOException;
import no.difi.sdp.client2.domain.exceptions.MessageSenderValidationException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.SendIOException;
import no.difi.sdp.client2.domain.exceptions.SikkerDigitalPostException;
import no.difi.sdp.client2.domain.exceptions.ValideringException;

import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.UKJENT;

/**
 * Exception mapper for sending av sikker digital post. Gjør subclassing av denne for implementere egen/tilpasset feilhåndtering.
 * Bruk kall til super for å ta med innebygd feilhåndtering.
 */
public class ExceptionMapper {

    /**
     * Oversetter Exceptions kastet fra de underliggende lagene under sending av post.
     *
     * @param e original exception
     * @return Mappet exception som skal kastes. null dersom ingen mapping er gjort og opprinnelig exception skal brukes.
     */
    public SikkerDigitalPostException mapException(Exception e) {
        if (e instanceof SendException) {
            return (SendException) e;
        } else if (e instanceof MessageSenderIOException) {
            return new SendIOException(e);
        } else if (e instanceof MessageSenderValidationException) {
            return new ValideringException((MessageSenderValidationException) e);
        }

        return new SendException("En uhåndtert feil skjedde under sending", UKJENT, e);
    }
}
