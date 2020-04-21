package no.difi.sdp.client2.domain.exceptions;

@Deprecated
public abstract class MessageSenderException extends RuntimeException {

    @Deprecated
    public MessageSenderException(String message) {
        super(message);
    }

    @Deprecated
    public MessageSenderException(String message, Throwable cause) {
        super(message, cause);
    }

}
