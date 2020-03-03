package no.difi.sdp.client2.domain.exceptions;

public abstract class MessageSenderException extends RuntimeException {

    public MessageSenderException(String message) {
        super(message);
    }

    public MessageSenderException(String message, Throwable cause) {
        super(message, cause);
    }

}
