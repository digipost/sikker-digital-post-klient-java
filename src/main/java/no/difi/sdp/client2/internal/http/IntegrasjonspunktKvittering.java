package no.difi.sdp.client2.internal.http;

import java.time.ZonedDateTime;
import java.util.UUID;

public class IntegrasjonspunktKvittering {
    private Long id;
    private ZonedDateTime lastUpdate;
    private IntegrasjonspunktKvittering.KvitteringStatus status;
    private String description;
    private String rawReceipt;
    private UUID messageId;
    private Long convId;
    private UUID conversationId;

    public enum KvitteringStatus {
        OPPRETTET,
        SENDT,
        MOTTATT,
        LEVERT,
        LEST,
        FEIL,
        ANNET,
        INNKOMMENDE_MOTTATT,
        INNKOMMENDE_LEVERT,
        LEVETID_UTLOPT
    }

    public IntegrasjonspunktKvittering() {
    }

    public IntegrasjonspunktKvittering(Long id, ZonedDateTime lastUpdate, KvitteringStatus status, String description, String rawReceipt, UUID messageId, Long convId, UUID conversationId) {
        this.id = id;
        this.lastUpdate = lastUpdate;
        this.status = status;
        this.description = description;
        this.rawReceipt = rawReceipt;
        this.messageId = messageId;
        this.convId = convId;
        this.conversationId = conversationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public KvitteringStatus getStatus() {
        return status;
    }

    public void setStatus(KvitteringStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRawReceipt() {
        return rawReceipt;
    }

    public void setRawReceipt(String rawReceipt) {
        this.rawReceipt = rawReceipt;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public Long getConvId() {
        return convId;
    }

    public void setConvId(Long convId) {
        this.convId = convId;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "IntegrasjonspunktKvittering{" +
            "id=" + id +
            ", lastUpdate=" + lastUpdate +
            ", status=" + status +
            ", description='" + description + '\'' +
            ", rawReceipt='" + rawReceipt + '\'' +
            ", messageId=" + messageId +
            ", convId=" + convId +
            ", conversationId=" + conversationId +
            '}';
    }
}
