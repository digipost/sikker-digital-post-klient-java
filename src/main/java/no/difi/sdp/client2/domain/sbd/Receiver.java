package no.difi.sdp.client2.domain.sbd;

import java.util.List;

public class Receiver extends Partner {

    public Receiver(PartnerIdentification identifier) {
        super(identifier);
    }

    public Receiver(PartnerIdentification identifier, List<ContactInformation> contactInformation) {
        super(identifier, contactInformation);
    }

    @Override
    public Receiver setIdentifier(PartnerIdentification identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public void setContactInformation(List<ContactInformation> contactInformation) {
        this.contactInformation = contactInformation;
    }
}
