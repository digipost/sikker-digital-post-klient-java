package no.difi.sdp.client2;

public class SendResultat {

    private final String conversationId;

    public SendResultat(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    /**
     * @see #getConversationId()
     */
    @Deprecated
    public String getMeldingsId() {
        return null;
    }

    /**
     * @see #getConversationId()
     */
    @Deprecated
    public String getReferanseTilMeldingsId() {
        return null;
    }

    @Deprecated
    public long getFakturerbareBytes() {
        return 0;
    }
}
