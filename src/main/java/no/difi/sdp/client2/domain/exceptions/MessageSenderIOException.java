package no.difi.sdp.client2.domain.exceptions;

import java.io.IOException;

public class MessageSenderIOException extends MessageSenderException {

    public MessageSenderIOException(String message, IOException e) {
        super(message, e);
    }

}
