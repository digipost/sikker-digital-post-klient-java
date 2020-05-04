package no.difi.sdp.client2.domain.sbd;

public class ContactInformation {

    protected String contact;
    protected String emailAddress;
    protected String faxNumber;
    protected String telephoneNumber;
    protected String contactTypeIdentifier;

    public String getContact() {
        return this.contact;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getFaxNumber() {
        return this.faxNumber;
    }

    public String getTelephoneNumber() {
        return this.telephoneNumber;
    }

    public String getContactTypeIdentifier() {
        return this.contactTypeIdentifier;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public void setContactTypeIdentifier(String contactTypeIdentifier) {
        this.contactTypeIdentifier = contactTypeIdentifier;
    }

    public String toString() {
        return "ContactInformation(contact=" + this.getContact() + ", emailAddress=" + this.getEmailAddress() + ", faxNumber=" + this.getFaxNumber() + ", telephoneNumber=" + this.getTelephoneNumber() + ", contactTypeIdentifier=" + this.getContactTypeIdentifier() + ")";
    }
}
