package no.difi.sdp.client2.domain.sbd;

import java.util.ArrayList;
import java.util.List;

public class Partner {

    protected PartnerIdentification identifier;
    protected List<ContactInformation> contactInformation;

    public Partner(PartnerIdentification identifier) {
        this.identifier = identifier;
    }

    public Partner(final PartnerIdentification identifier, final List<ContactInformation> contactInformation) {
        this.identifier = identifier;
        this.contactInformation = contactInformation;
    }

    public Partner setIdentifier(PartnerIdentification identifier) {
        this.identifier = identifier;
        identifier.setPartner(this);
        return this;
    }

    public List<ContactInformation> getContactInformation() {
        if (contactInformation == null) {
            contactInformation = new ArrayList<>();
        }
        return this.contactInformation;
    }

    public Partner addContactInformation(ContactInformation contactInformation) {
        getContactInformation().add(contactInformation);
        return this;
    }

    public PartnerIdentification getIdentifier() {
        return this.identifier;
    }

    public void setContactInformation(List<ContactInformation> contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String toString() {
        return "Partner(identifier=" + this.getIdentifier() + ", contactInformation=" + this.getContactInformation() + ")";
    }
}
