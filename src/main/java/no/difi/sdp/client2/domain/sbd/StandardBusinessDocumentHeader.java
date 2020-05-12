package no.difi.sdp.client2.domain.sbd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.difi.sdp.client2.domain.AvsenderOrganisasjonsnummer;
import no.difi.sdp.client2.domain.DatabehandlerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.Mottaker;
import no.digipost.api.representations.Organisasjonsnummer;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static no.digipost.api.representations.Organisasjonsnummer.COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY;
import static no.digipost.api.representations.Organisasjonsnummer.ISO6523_ACTORID;


public class StandardBusinessDocumentHeader {


    private String headerVersion;
    private List<Sender> sender;
    private List<Receiver> receiver;
    private DocumentIdentification documentIdentification;
    private BusinessScope businessScope;

    public void setSender(List<Sender> sender) {
        this.sender = sender;
    }

    public List<Sender> getSender() {
        if (sender == null) {
            sender = new ArrayList<>();
        }
        return this.sender;
    }

    public StandardBusinessDocumentHeader addSender(Sender partner) {
        if (partner != null) {
            getSender().add(partner);
        }
        return this;
    }

    public List<Receiver> getReceiver() {
        if (receiver == null) {
            receiver = new ArrayList<>();
        }
        return this.receiver;
    }

    public StandardBusinessDocumentHeader addReceiver(Receiver partner) {
        getReceiver().add(partner);
        return this;
    }

    @JsonIgnore
    Optional<Sender> getFirstSender() {
        if (sender == null) {
            return Optional.empty();
        }
        return sender.stream().findFirst();
    }

    @JsonIgnore
    Optional<Receiver> getFirstReceiver() {
        if (receiver == null) {
            return Optional.empty();
        }
        return receiver.stream().findFirst();
    }

    @JsonIgnore
    public String getReceiverOrganisationNumber() {

        if (receiver.size() != 1) {
            throw new RuntimeException(String.valueOf(receiver.size()));
        }
        Partner partner = receiver.iterator().next();
        PartnerIdentification identifier = partner.getIdentifier();
        if (identifier == null) {
            throw new RuntimeException();
        }
        return identifier.getValue();
    }

    public String getHeaderVersion() {
        return this.headerVersion;
    }

    public DocumentIdentification getDocumentIdentification() {
        return this.documentIdentification;
    }

    public BusinessScope getBusinessScope() {
        return this.businessScope;
    }

    public StandardBusinessDocumentHeader withHeaderVersion(String headerVersion) {
        this.headerVersion = headerVersion;
        return this;
    }

    public void setReceiver(List<Receiver> receiver) {
        this.receiver = receiver;
    }

    public StandardBusinessDocumentHeader setDocumentIdentification(DocumentIdentification documentIdentification) {
        this.documentIdentification = documentIdentification;
        return this;
    }


    public StandardBusinessDocumentHeader setBusinessScope(BusinessScope businessScope) {
        this.businessScope = businessScope;
        return this;
    }

    public String toString() {
        return "StandardBusinessDocumentHeader(headerVersion=" + this.getHeaderVersion() + ", sender=" + this.getSender() + ", receiver=" + this.getReceiver() + ", documentIdentification=" + this.getDocumentIdentification() + ", businessScope=" + this.getBusinessScope() + ")";
    }

    public static class Builder {

        private static final String HEADER_VERSION = "1.0";
        private static final String TYPE_VERSION = "1.0";

        private String mottaker;
        private String sender;
        private String onBehalfOf;
        private String conversationId;
        private String messageId;
        private String documentType;
        private String standard;
        private Process process;
        private ZonedDateTime creationDateAndTime;

        public Builder to(Organisasjonsnummer mottaker) {
            this.mottaker = mottaker.getOrganisasjonsnummerMedLandkode();
            return this;
        }

        public Builder to(Mottaker mottaker) {
            this.mottaker = mottaker.getPersonidentifikator();
            return this;
        }

        public Builder to(String mottaker) {
            this.mottaker = mottaker;
            return this;
        }

        public Builder from(DatabehandlerOrganisasjonsnummer sender) {
            this.sender = sender.getOrganisasjonsnummer();
            return this;
        }

        public Builder onBehalfOf(AvsenderOrganisasjonsnummer onBehalfOf) {
            this.onBehalfOf = onBehalfOf.getOrganisasjonsnummer();
            return this;
        }

        public Builder type(String type) {
            this.documentType = type;
            return this;
        }

        public Builder standard(Forsendelse.Type type) {
            this.standard = type.type;
            return this;
        }

        public Builder process(Process process) {
            this.process = process;
            return this;
        }

        public Builder relatedToConversationId(String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder relatedToMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder creationDateAndTime(ZonedDateTime creationDateAndTime) {
            this.creationDateAndTime = creationDateAndTime;
            return this;
        }

        public StandardBusinessDocumentHeader build() {
            final StandardBusinessDocumentHeader standardBusinessDocumentHeader = new StandardBusinessDocumentHeader()
                    .withHeaderVersion(HEADER_VERSION);

            return standardBusinessDocumentHeader
                    .addReceiver(createReciever(mottaker))
                    .addSender(createSender(sender, onBehalfOf))
                    .setBusinessScope(createBusinessScope(fromConversationId(conversationId)))
                    .setDocumentIdentification(createDocumentIdentification(messageId, documentType, standard, creationDateAndTime));
        }

        private static Receiver createReciever(String mottaker) {
            PartnerIdentification identification = new PartnerIdentification();
            identification.setValue(mottaker);
            identification.setAuthority(ISO6523_ACTORID);

            return new Receiver(identification);
        }

        private static Sender createSender(String avsender, String onBehalfOf) {
            PartnerIdentification identification = new PartnerIdentification();
            String value = COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY + ":" + avsender;
            if(onBehalfOf != null) {
                value += ":" + onBehalfOf;
            }
            identification.setValue(value);
            identification.setAuthority(ISO6523_ACTORID);
            return new Sender(identification);
        }

        private static DocumentIdentification createDocumentIdentification(String messageId, String documentType, String standard, ZonedDateTime creationDateAndTime) {
            if (documentType == null) {
                throw new RuntimeException("documentType must be set");
            }

            final DocumentIdentification documentIdentification = new DocumentIdentification();
            documentIdentification.setStandard(standard);
            documentIdentification.setType(documentType);
            documentIdentification.setTypeVersion(TYPE_VERSION);
            documentIdentification.setInstanceIdentifier(messageId);
            documentIdentification.setCreationDateAndTime(creationDateAndTime);
            return documentIdentification;
        }

        private static BusinessScope createBusinessScope(Scope... scopes) {
            final BusinessScope businessScope = new BusinessScope();
            businessScope.setScope(new ArrayList<>(Arrays.asList(scopes)));
            return businessScope;
        }

        private Scope fromConversationId(String conversationId) {
            final Scope scope = createDefaultScope();
            scope.setType(ScopeType.CONVERSATION_ID.toString());
            scope.setInstanceIdentifier(conversationId);
            return scope;
        }

        private Scope createDefaultScope() {
            if (process == null) {
                throw new RuntimeException("Process must be set");
            }

            final Scope scope = new Scope();
            scope.setIdentifier(process.getValue());
            return scope;
        }
    }
}
