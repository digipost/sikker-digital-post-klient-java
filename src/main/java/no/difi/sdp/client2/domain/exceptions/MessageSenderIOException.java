package no.difi.sdp.client2.domain.exceptions;

import java.io.IOException;

@Deprecated
public class MessageSenderIOException extends MessageSenderException {

    @Deprecated
    public MessageSenderIOException(String message, IOException e) {
        super(message, e);
    }

}
