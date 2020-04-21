package no.difi.sdp.client2.domain.sbd;

import java.util.List;

public class Sender extends Partner {

    public Sender(PartnerIdentification identifier) {
        super(identifier);
    }

    public Sender(PartnerIdentification identifier, List<ContactInformation> contactInformation) {
        super(identifier, contactInformation);
    }

    @Override
    public Sender setIdentifier(PartnerIdentification identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public void setContactInformation(List<ContactInformation> contactInformation) {
        this.contactInformation = contactInformation;
    }

}
