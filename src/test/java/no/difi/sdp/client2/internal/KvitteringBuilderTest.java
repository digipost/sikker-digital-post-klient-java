package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering;
import no.difi.sdp.client2.internal.kvittering.KvitteringBuilder;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

class KvitteringBuilderTest {

    KvitteringBuilder kvitteringBuilder = new KvitteringBuilder();

    @Test
    public void skal_parse_kvitteringsmeldinger() {
        final UUID messageId = UUID.randomUUID();
        final UUID conversationId = UUID.randomUUID();
        IntegrasjonspunktKvittering kvittering = new IntegrasjonspunktKvittering(1L, ZonedDateTime.now(), IntegrasjonspunktKvittering.KvitteringStatus.LEVERT, "Beskrivelse", rawReceipt, messageId, 1L, conversationId);
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
    }


    private String rawReceipt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<ns3:StandardBusinessDocument xmlns:ns3=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\"\n" +
        "                              xmlns:ns9=\"http://begrep.difi.no/sdp/schema_v10\">\n" +
        "    <ns3:StandardBusinessDocumentHeader>\n" +
        "        <ns3:HeaderVersion>1.0</ns3:HeaderVersion>\n" +
        "        <ns3:Sender>\n" +
        "            <ns3:Identifier Authority=\"urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908\">9908:984661185\n" +
        "            </ns3:Identifier>\n" +
        "        </ns3:Sender>\n" +
        "        <ns3:Receiver>\n" +
        "            <ns3:Identifier Authority=\"urn:oasis:names:tc:ebcore:partyid-type:iso6523:9908\">9908:984661185\n" +
        "            </ns3:Identifier>\n" +
        "        </ns3:Receiver>\n" +
        "        <ns3:DocumentIdentification>\n" +
        "            <ns3:Standard>urn:no:difi:sdp:1.0</ns3:Standard>\n" +
        "            <ns3:TypeVersion>1.0</ns3:TypeVersion>\n" +
        "            <ns3:InstanceIdentifier>2f4acea8-3b15-49c2-898a-a65400eb9b86</ns3:InstanceIdentifier>\n" +
        "            <ns3:Type>kvittering</ns3:Type>\n" +
        "            <ns3:CreationDateAndTime>2020-04-02T12:58:26.330+02:00</ns3:CreationDateAndTime>\n" +
        "        </ns3:DocumentIdentification>\n" +
        "        <ns3:BusinessScope>\n" +
        "            <ns3:Scope>\n" +
        "                <ns3:Type>ConversationId</ns3:Type>\n" +
        "                <ns3:InstanceIdentifier>ef99091a-de03-4a95-b190-46e96a9c658e</ns3:InstanceIdentifier>\n" +
        "                <ns3:Identifier>urn:no:difi:sdp:1.0</ns3:Identifier>\n" +
        "            </ns3:Scope>\n" +
        "        </ns3:BusinessScope>\n" +
        "    </ns3:StandardBusinessDocumentHeader>\n" +
        "    <ns9:kvittering>\n" +
        "        <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
        "            <SignedInfo>\n" +
        "                <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n" +
        "                <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\n" +
        "                <Reference URI=\"\">\n" +
        "                    <Transforms>\n" +
        "                        <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n" +
        "                    </Transforms>\n" +
        "                    <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n" +
        "                    <DigestValue>8PKVikck/T0M5qC5vOLqSMWroXna9pG9nFE4q9Hy2xI=</DigestValue>\n" +
        "                </Reference>\n" +
        "            </SignedInfo>\n" +
        "            <SignatureValue>\n" +
        "                QVN/ccPG5JYt0pMVNdVqHTLwa4F77D+sH4ctXXekzNjmjUQyEKtPtVXakfjh8ZO35Z5Jb5RQckYcvCpvTu3NxG5ffZHiyngCCbEcwmK95h4ZC7kM0o2K9luWoiED6LVuF9AT6m3HtB+M6iH5+RJ8b1amczyWZKafrb4qdHZvgU1GdVxZU2qfNkVLyvjrAqC2N2PmJ/znAzkymYSPjoUM84xiAZqPpTs9PYmVEFmgST8Ljoaq2YBavhm/pvD+iOfHmQy9ouKSrW6KZESKWBM2BBe+HMq/zhqozCSB+aMT5lUvJTAtikqGctrM509FUixNUUA0pqJZfzPnqE9f0nzCKQ==\n" +
        "            </SignatureValue>\n" +
        "            <KeyInfo>\n" +
        "                <X509Data>\n" +
        "                    <X509Certificate>\n" +
        "                        MIIFEzCCA/ugAwIBAgILAjxAYEhC5sBU48QwDQYJKoZIhvcNAQELBQAwUTELMAkGA1UEBhMCTk8xHTAbBgNVBAoMFEJ1eXBhc3MgQVMtOTgzMTYzMzI3MSMwIQYDVQQDDBpCdXlwYXNzIENsYXNzIDMgVGVzdDQgQ0EgMzAeFw0yMDAyMDYxNTE4MThaFw0yMzAyMDYyMjU5MDBaMFoxCzAJBgNVBAYTAk5PMRgwFgYDVQQKDA9QT1NURU4gTk9SR0UgQVMxHTAbBgNVBAMMFFBPU1RFTiBOT1JHRSBBUyBURVNUMRIwEAYDVQQFEwk5ODQ2NjExODUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCU3m0kTYPTNX/ftxf6KcY0iLXQ6pAozxqyTrmbwGZw+LzPpY3phKKE5kbKp6oYKDFW1OehRB1L+bqZJTYEXSHWxUA/NGr7SoCV7UEycBSX6tA4MLwAzn3yEccApRa4Vqwv+XphOEUg0v/x/DkwJaT4o1YOFD8QRNjqmJcz4iW0I3Wp4C7dGJxYF2CK7UX5KXwHdgrSdTt6lF4M3ZshJH4quzhAY5y7tdO2EMVq9Bkkc+oA3xvJQ/O3GjjAUpy4ywglDIW022sJKjjAlNY8mjJMcybnRaWLoLC6YprSbzb6wsmu8GJGjiHQEFvB5EAfmIyr7cvT50usAnMZC9gprS9tAgMBAAGjggHhMIIB3TAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFD+u9XgLkqNwIDVfWvr3JKBSAfBBMB0GA1UdDgQWBBRaOn2NsEXqQ8eZojgdbhHCAdrU8DAOBgNVHQ8BAf8EBAMCBLAwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMBYGA1UdIAQPMA0wCwYJYIRCARoBAAMCMIG7BgNVHR8EgbMwgbAwN6A1oDOGMWh0dHA6Ly9jcmwudGVzdDQuYnV5cGFzcy5uby9jcmwvQlBDbGFzczNUNENBMy5jcmwwdaBzoHGGb2xkYXA6Ly9sZGFwLnRlc3Q0LmJ1eXBhc3Mubm8vZGM9QnV5cGFzcyxkYz1OTyxDTj1CdXlwYXNzJTIwQ2xhc3MlMjAzJTIwVGVzdDQlMjBDQSUyMDM/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdDCBigYIKwYBBQUHAQEEfjB8MDsGCCsGAQUFBzABhi9odHRwOi8vb2NzcC50ZXN0NC5idXlwYXNzLm5vL29jc3AvQlBDbGFzczNUNENBMzA9BggrBgEFBQcwAoYxaHR0cDovL2NydC50ZXN0NC5idXlwYXNzLm5vL2NydC9CUENsYXNzM1Q0Q0EzLmNlcjANBgkqhkiG9w0BAQsFAAOCAQEAZGpNYvzd7mmh7V2OlQOc0B7+1N3apZMEnMj6iiPH6l7oZ5aNFP73fLlDiB2NpPpkQEDcrt6MCnNiO/U3qIkWz/blWDD9k1xUs9ZSeQZJnapuGnN7zSbIUcFnTDNik4cFlJOG7hcnPvxv3ewMSffuhoqnnaPA7J1gzNMA2hkmM7l+sGfCzhr7h9THgo51uGnscTL6PI2qB9qpHN4lR2Aw4yEV0Ve16ENQxASucGc2N+6ZiJQWZiHQL8Z6076NogeMqzG1KIklh5ZogPJxBbnFg72Y0aMrKHw799jm9n64HnOAt1c3qOjduxnjdRMRy+YcIuIy+bUPX4bexmsuX0ehGw==\n" +
        "                    </X509Certificate>\n" +
        "                </X509Data>\n" +
        "            </KeyInfo>\n" +
        "        </Signature>\n" +
        "        <ns9:tidspunkt>2020-04-02T12:58:26.330+02:00</ns9:tidspunkt>\n" +
        "        <ns9:levering/>\n" +
        "    </ns9:kvittering>\n" +
        "</ns3:StandardBusinessDocument>";

}
