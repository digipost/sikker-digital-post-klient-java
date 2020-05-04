


package no.difi.sdp.client2.domain.sbd;

import java.time.ZonedDateTime;


/**
 * <p>Java class for CorrelationInformation complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CorrelationInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RequestingDocumentCreationDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="RequestingDocumentInstanceIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExpectedResponseDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class CorrelationInformation {
    protected ZonedDateTime requestingDocumentCreationDateTime;


    protected String requestingDocumentInstanceIdentifier;
    protected ZonedDateTime expectedResponseDateTime;

    public ZonedDateTime getRequestingDocumentCreationDateTime() {
        return this.requestingDocumentCreationDateTime;
    }

    public String getRequestingDocumentInstanceIdentifier() {
        return this.requestingDocumentInstanceIdentifier;
    }

    public ZonedDateTime getExpectedResponseDateTime() {
        return this.expectedResponseDateTime;
    }

    public void setRequestingDocumentCreationDateTime(ZonedDateTime requestingDocumentCreationDateTime) {
        this.requestingDocumentCreationDateTime = requestingDocumentCreationDateTime;
    }

    public void setRequestingDocumentInstanceIdentifier(String requestingDocumentInstanceIdentifier) {
        this.requestingDocumentInstanceIdentifier = requestingDocumentInstanceIdentifier;
    }

    public void setExpectedResponseDateTime(ZonedDateTime expectedResponseDateTime) {
        this.expectedResponseDateTime = expectedResponseDateTime;
    }

    public String toString() {
        return "CorrelationInformation(requestingDocumentCreationDateTime=" + this.getRequestingDocumentCreationDateTime() + ", requestingDocumentInstanceIdentifier=" + this.getRequestingDocumentInstanceIdentifier() + ", expectedResponseDateTime=" + this.getExpectedResponseDateTime() + ")";
    }
}
