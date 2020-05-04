package no.difi.sdp.client2.domain.sbd;

public enum ScopeType {
    CONVERSATION_ID("ConversationId"),
    SENDER_REF("SenderRef"),
    RECEIVER_REF("ReceiverRef"),
    ;

    private String fullname;

    ScopeType(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String toString() {
        return this.fullname;
    }
}
