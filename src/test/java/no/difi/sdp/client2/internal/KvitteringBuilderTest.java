package no.difi.sdp.client2.internal;

import no.difi.sdp.client2.domain.kvittering.AapningsKvittering;
import no.difi.sdp.client2.domain.kvittering.Feil;
import no.difi.sdp.client2.domain.kvittering.ForretningsKvittering;
import no.difi.sdp.client2.domain.kvittering.LeveringsKvittering;
import no.difi.sdp.client2.domain.kvittering.MottaksKvittering;
import no.difi.sdp.client2.domain.kvittering.ReturpostKvittering;
import no.difi.sdp.client2.domain.kvittering.VarslingFeiletKvittering;
import no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering;
import no.difi.sdp.client2.internal.http.IntegrasjonspunktKvittering.KvitteringStatus;
import no.difi.sdp.client2.internal.kvittering.KvitteringBuilder;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static co.unruly.matchers.Java8Matchers.where;
import static no.difi.sdp.client2.domain.kvittering.Feil.Feiltype.KLIENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class KvitteringBuilderTest {

    KvitteringBuilder kvitteringBuilder = new KvitteringBuilder();

    @Test
    public void skal_handtere_sendtkvittering() {
        IntegrasjonspunktKvittering kvittering = new IntegrasjonspunktKvittering(1L, ZonedDateTime.now(), KvitteringStatus.SENDT, "Beskrivelse", null, UUID.fromString(levertMessageId), 1L, UUID.fromString(levertConversationId));
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertNull(forretningsKvittering);
    }

    @Test
    public void skal_handtere_levetidutlopt_som_feil() {
        IntegrasjonspunktKvittering kvittering = new IntegrasjonspunktKvittering(1L, ZonedDateTime.now(), KvitteringStatus.LEVETID_UTLOPT, "Beskrivelse", null, UUID.fromString(levertMessageId), 1L, UUID.fromString(levertConversationId));
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, is(instanceOf(Feil.class)));
        assertThat(((Feil) forretningsKvittering), where(Feil::getFeiltype, is(KLIENT)));
    }

    @Test
    public void skal_parse_levert_kvitteringsmeldinger() {
        final ZonedDateTime kvitteringDateTime = ZonedDateTime.now();
        IntegrasjonspunktKvittering kvittering = new IntegrasjonspunktKvittering(1L, kvitteringDateTime, KvitteringStatus.LEVERT, "Beskrivelse", levertRawReceipt, UUID.fromString(levertMessageId), 1L, UUID.fromString(levertConversationId));
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, where(ForretningsKvittering::getKonversasjonsId, is(levertConversationId)));
        assertThat(forretningsKvittering, where(ForretningsKvittering::getMeldingsId, is(levertMessageId)));
        assertThat(forretningsKvittering, where(ForretningsKvittering::getTidspunkt, is(kvitteringDateTime.toInstant())));
        assertThat(forretningsKvittering, is(instanceOf(LeveringsKvittering.class)));
    }

    @Test
    public void skal_parse_feilet_kvittering() {
        IntegrasjonspunktKvittering kvittering = new IntegrasjonspunktKvittering(1L, ZonedDateTime.now(), KvitteringStatus.LEVERT, "Beskrivelse", feiletRawReceipt, UUID.randomUUID(), 1L, UUID.randomUUID());
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, is(instanceOf(Feil.class)));

        final Feil feilKvittering = (Feil) forretningsKvittering;
        assertThat(feilKvittering, where(Feil::getFeiltype, is(KLIENT)));
        assertThat(feilKvittering, where(Feil::getDetaljer, containsString("lenke.xml")));
    }

    @Test
    public void skal_parse_mottak_kvittering() {
        IntegrasjonspunktKvittering kvittering = new IntegrasjonspunktKvittering(1L, ZonedDateTime.now(), KvitteringStatus.LEVERT, "Beskrivelse", mottakRawReceipt, UUID.randomUUID(), 1L, UUID.randomUUID());
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, is(instanceOf(MottaksKvittering.class)));
    }

    @Test
    public void skal_parse_aapningskvittering() {
        final String rawReceipt = KvitteringTestUtil.Åpningskvittering();
        IntegrasjonspunktKvittering kvittering = byggKvitteringMedRawReceipt(KvitteringStatus.LEST, rawReceipt);
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, is(instanceOf(AapningsKvittering.class)));
    }

    @Test
    public void skal_parse_varslingfeiletkvittering() {
        final String rawReceipt = KvitteringTestUtil.VarslingFeiletKvittering();
        IntegrasjonspunktKvittering kvittering = byggKvitteringMedRawReceipt(KvitteringStatus.FEIL, rawReceipt);
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, is(instanceOf(VarslingFeiletKvittering.class)));
        assertThat((VarslingFeiletKvittering) forretningsKvittering, where(VarslingFeiletKvittering::getBeskrivelse, notNullValue()));
    }

    @Test
    public void skal_parse_leveringskvittering() {
        final String rawReceipt = KvitteringTestUtil.Leveringskvittering();
        IntegrasjonspunktKvittering kvittering = byggKvitteringMedRawReceipt(KvitteringStatus.LEVERT, rawReceipt);
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, is(instanceOf(LeveringsKvittering.class)));
    }

    @Test
    public void skal_parse_returpostkvittering() {
        final String rawReceipt = KvitteringTestUtil.Returpostkvittering();
        IntegrasjonspunktKvittering kvittering = byggKvitteringMedRawReceipt(KvitteringStatus.FEIL, rawReceipt);
        final ForretningsKvittering forretningsKvittering = kvitteringBuilder.buildForretningsKvittering(kvittering);
        assertThat(forretningsKvittering, is(instanceOf(ReturpostKvittering.class)));
    }

    private IntegrasjonspunktKvittering byggKvitteringMedRawReceipt(KvitteringStatus status, String rawReceipt) {
        return new IntegrasjonspunktKvittering(1L, ZonedDateTime.now(), status, "", rawReceipt, UUID.randomUUID(), 1L, UUID.randomUUID());
    }


    private static final String levertMessageId = "2f4acea8-3b15-49c2-898a-a65400eb9b86";
    private static final String levertConversationId = "ef99091a-de03-4a95-b190-46e96a9c658e";

    private static final String levertRawReceipt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
        "            <ns3:InstanceIdentifier>" + levertMessageId + "</ns3:InstanceIdentifier>\n" +
        "            <ns3:Type>kvittering</ns3:Type>\n" +
        "            <ns3:CreationDateAndTime>2020-04-02T12:58:26.330+02:00</ns3:CreationDateAndTime>\n" +
        "        </ns3:DocumentIdentification>\n" +
        "        <ns3:BusinessScope>\n" +
        "            <ns3:Scope>\n" +
        "                <ns3:Type>ConversationId</ns3:Type>\n" +
        "                <ns3:InstanceIdentifier>" + levertConversationId + "</ns3:InstanceIdentifier>\n" +
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

    private static final String feiletRawReceipt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<ns3:StandardBusinessDocument\n" +
        "        xmlns:ns3=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\"\n" +
        "        xmlns:ns9=\"http://begrep.difi.no/sdp/schema_v10\">\n" +
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
        "            <ns3:InstanceIdentifier>ee877a89-b1dd-4004-b03e-a597a9076b5f</ns3:InstanceIdentifier>\n" +
        "            <ns3:Type>feil</ns3:Type>\n" +
        "            <ns3:CreationDateAndTime>2020-03-19T09:40:10.668+01:00</ns3:CreationDateAndTime>\n" +
        "        </ns3:DocumentIdentification>\n" +
        "        <ns3:BusinessScope>\n" +
        "            <ns3:Scope>\n" +
        "                <ns3:Type>ConversationId</ns3:Type>\n" +
        "                <ns3:InstanceIdentifier>612d9da7-d2f4-467b-a147-4d497b613270</ns3:InstanceIdentifier>\n" +
        "                <ns3:Identifier>urn:no:difi:sdp:1.0</ns3:Identifier>\n" +
        "            </ns3:Scope>\n" +
        "        </ns3:BusinessScope>\n" +
        "    </ns3:StandardBusinessDocumentHeader>\n" +
        "    <ns9:feil>\n" +
        "        <Signature xmlns=\n" +
        "                           \"http://www.w3.org/2000/09/xmldsig#\">\n" +
        "            <SignedInfo>\n" +
        "                <CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n" +
        "                <SignatureMethod Algorithm=\n" +
        "                                         \"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\n" +
        "                <Reference URI=\"\">\n" +
        "                    <Transforms>\n" +
        "                        <Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\n" +
        "                    </Transforms>\n" +
        "                    <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\n" +
        "                    <DigestValue>W1R10aK/stl8w73Cgq0P7e0EZHfn6QUgERqpUPvTYc4=</DigestValue>\n" +
        "                </Reference>\n" +
        "            </SignedInfo>\n" +
        "            <SignatureValue>\n" +
        "                KKodmaGv34+1Iju1g9I1Giv/DmUr5Ma2141vcrRsdHPeUDXa7cyNp/KrCZadhc7aOy37QJCWGXVqpEPgyuMhpmp42Oh44fCNEI+C95bHQw4m5gv/x9WvRLhMq4ZEfAs5UWDN81ech7UkKIQoWx4bnOupqkDfd2SAc2zRkVxj6E7dYJJ1S5rpt9lCZTGe+xSVyAVEXT/vQSDH+5IAuqi/jk0OW23MS5i6r8mkcWzqd6HyDwqzpYOQWYrGZFjHshevMl60DFEnPlaAJrhzw0tdIUBQtE6ASG9+h1w5rNU/KhCCubE+ML7EwoZRUfQXHyYXET/9JxUBWfpZ6l8JQAAwKg==\n" +
        "            </SignatureValue>\n" +
        "            <KeyInfo>\n" +
        "                <X509Data>\n" +
        "                    <X509Certificate>\n" +
        "                        MIIFEzCCA/ugAwIBAgILAjxAYEhC5sBU48QwDQYJKoZIhvcNAQELBQAwUTELMAkGA1UEBhMCTk8xHTAbBgNVBAoMFEJ1eXBhc3MgQVMtOTgzMTYzMzI3MSMwIQYDVQQDDBpCdXlwYXNzIENsYXNzIDMgVGVzdDQgQ0EgMzAeFw0yMDAyMDYxNTE4MThaFw0yMzAyMDYyMjU5MDBaMFoxCzAJBgNVBAYTAk5PMRgwFgYDVQQKDA9QT1NURU4gTk9SR0UgQVMxHTAbBgNVBAMMFFBPU1RFTiBOT1JHRSBBUyBURVNUMRIwEAYDVQQFEwk5ODQ2NjExODUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCU3m0kTYPTNX/ftxf6KcY0iLXQ6pAozxqyTrmbwGZw+LzPpY3phKKE5kbKp6oYKDFW1OehRB1L+bqZJTYEXSHWxUA/NGr7SoCV7UEycBSX6tA4MLwAzn3yEccApRa4Vqwv+XphOEUg0v/x/DkwJaT4o1YOFD8QRNjqmJcz4iW0I3Wp4C7dGJxYF2CK7UX5KXwHdgrSdTt6lF4M3ZshJH4quzhAY5y7tdO2EMVq9Bkkc+oA3xvJQ/O3GjjAUpy4ywglDIW022sJKjjAlNY8mjJMcybnRaWLoLC6YprSbzb6wsmu8GJGjiHQEFvB5EAfmIyr7cvT50usAnMZC9gprS9tAgMBAAGjggHhMIIB3TAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFD+u9XgLkqNwIDVfWvr3JKBSAfBBMB0GA1UdDgQWBBRaOn2NsEXqQ8eZojgdbhHCAdrU8DAOBgNVHQ8BAf8EBAMCBLAwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMBYGA1UdIAQPMA0wCwYJYIRCARoBAAMCMIG7BgNVHR8EgbMwgbAwN6A1oDOGMWh0dHA6Ly9jcmwudGVzdDQuYnV5cGFzcy5uby9jcmwvQlBDbGFzczNUNENBMy5jcmwwdaBzoHGGb2xkYXA6Ly9sZGFwLnRlc3Q0LmJ1eXBhc3Mubm8vZGM9QnV5cGFzcyxkYz1OTyxDTj1CdXlwYXNzJTIwQ2xhc3MlMjAzJTIwVGVzdDQlMjBDQSUyMDM/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdDCBigYIKwYBBQUHAQEEfjB8MDsGCCsGAQUFBzABhi9odHRwOi8vb2NzcC50ZXN0NC5idXlwYXNzLm5vL29jc3AvQlBDbGFzczNUNENBMzA9BggrBgEFBQcwAoYxaHR0cDovL2NydC50ZXN0NC5idXlwYXNzLm5vL2NydC9CUENsYXNzM1Q0Q0EzLmNlcjANBgkqhkiG9w0BAQsFAAOCAQEAZGpNYvzd7mmh7V2OlQOc0B7+1N3apZMEnMj6iiPH6l7oZ5aNFP73fLlDiB2NpPpkQEDcrt6MCnNiO/U3qIkWz/blWDD9k1xUs9ZSeQZJnapuGnN7zSbIUcFnTDNik4cFlJOG7hcnPvxv3ewMSffuhoqnnaPA7J1gzNMA2hkmM7l+sGfCzhr7h9THgo51uGnscTL6PI2qB9qpHN4lR2Aw4yEV0Ve16ENQxASucGc2N+6ZiJQWZiHQL8Z6076NogeMqzG1KIklh5ZogPJxBbnFg72Y0aMrKHw799jm9n64HnOAt1c3qOjduxnjdRMRy+YcIuIy+bUPX4bexmsuX0ehGw==\n" +
        "                    </X509Certificate>\n" +
        "                </X509Data>\n" +
        "            </KeyInfo>\n" +
        "        </Signature>\n" +
        "        <ns9:tidspunkt>2020-03-19T09:40:10.668+01:00</ns9:tidspunkt>\n" +
        "        <ns9:feiltype>KLIENT</ns9:feiltype>\n" +
        "        <ns9:detaljer>Parsefeil: Finner ikke hash for: [lenke.xml]</ns9:detaljer>\n" +
        "    </ns9:feil>\n" +
        "</ns3:StandardBusinessDocument>";

    private static final String mottakRawReceipt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
        "            <ns3:InstanceIdentifier>fad8b7db-86d3-4b63-94e6-497816ab0647</ns3:InstanceIdentifier>\n" +
        "            <ns3:Type>kvittering</ns3:Type>\n" +
        "            <ns3:CreationDateAndTime>2020-04-01T13:25:32.660+02:00</ns3:CreationDateAndTime>\n" +
        "        </ns3:DocumentIdentification>\n" +
        "        <ns3:BusinessScope>\n" +
        "            <ns3:Scope>\n" +
        "                <ns3:Type>ConversationId</ns3:Type>\n" +
        "                <ns3:InstanceIdentifier>1be9ac16-014f-4444-85f9-d964eb6cd0f4</ns3:InstanceIdentifier>\n" +
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
        "                    <DigestValue>T+NxuZh/VFmJpDaqFJq3wZtZ/9iyw4OK8r1Tju0xmdM=</DigestValue>\n" +
        "                </Reference>\n" +
        "            </SignedInfo>\n" +
        "            <SignatureValue>\n" +
        "                Uj3T0Ab1cJ1qmnARck834ZLdK5lGURPa1QhyDUNuWff89Nz3xFbwyJYzjLSFJHcJLcnd3Y8qTF4ttIobQddRgLbAvBBiNHNC02/zz/mD4kfN2juaZb8TTch00Sy2RSi1UMIdlVWhkQPE1FE6RQrkZPzUABWVSJcNoQWS9/v2FY4tzm86+GODg+qeoLQRGDxEHzKcALcoCBvW+dCGduWHjPpBYgGnK4M8h/LHOAplJu7kTR1OfIet7FFYSAq6rJonRbGezcPWlS/NQxhznPXT/PES+78I8WiKHpCzZ/kAD3jONlwMDZiLuihC3ih7EDu5Xs/jpTn8/RCs4bDCuB/ukw==\n" +
        "            </SignatureValue>\n" +
        "            <KeyInfo>\n" +
        "                <X509Data>\n" +
        "                    <X509Certificate>\n" +
        "                        MIIFEzCCA/ugAwIBAgILAjxAYEhC5sBU48QwDQYJKoZIhvcNAQELBQAwUTELMAkGA1UEBhMCTk8xHTAbBgNVBAoMFEJ1eXBhc3MgQVMtOTgzMTYzMzI3MSMwIQYDVQQDDBpCdXlwYXNzIENsYXNzIDMgVGVzdDQgQ0EgMzAeFw0yMDAyMDYxNTE4MThaFw0yMzAyMDYyMjU5MDBaMFoxCzAJBgNVBAYTAk5PMRgwFgYDVQQKDA9QT1NURU4gTk9SR0UgQVMxHTAbBgNVBAMMFFBPU1RFTiBOT1JHRSBBUyBURVNUMRIwEAYDVQQFEwk5ODQ2NjExODUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCU3m0kTYPTNX/ftxf6KcY0iLXQ6pAozxqyTrmbwGZw+LzPpY3phKKE5kbKp6oYKDFW1OehRB1L+bqZJTYEXSHWxUA/NGr7SoCV7UEycBSX6tA4MLwAzn3yEccApRa4Vqwv+XphOEUg0v/x/DkwJaT4o1YOFD8QRNjqmJcz4iW0I3Wp4C7dGJxYF2CK7UX5KXwHdgrSdTt6lF4M3ZshJH4quzhAY5y7tdO2EMVq9Bkkc+oA3xvJQ/O3GjjAUpy4ywglDIW022sJKjjAlNY8mjJMcybnRaWLoLC6YprSbzb6wsmu8GJGjiHQEFvB5EAfmIyr7cvT50usAnMZC9gprS9tAgMBAAGjggHhMIIB3TAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFD+u9XgLkqNwIDVfWvr3JKBSAfBBMB0GA1UdDgQWBBRaOn2NsEXqQ8eZojgdbhHCAdrU8DAOBgNVHQ8BAf8EBAMCBLAwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMBYGA1UdIAQPMA0wCwYJYIRCARoBAAMCMIG7BgNVHR8EgbMwgbAwN6A1oDOGMWh0dHA6Ly9jcmwudGVzdDQuYnV5cGFzcy5uby9jcmwvQlBDbGFzczNUNENBMy5jcmwwdaBzoHGGb2xkYXA6Ly9sZGFwLnRlc3Q0LmJ1eXBhc3Mubm8vZGM9QnV5cGFzcyxkYz1OTyxDTj1CdXlwYXNzJTIwQ2xhc3MlMjAzJTIwVGVzdDQlMjBDQSUyMDM/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdDCBigYIKwYBBQUHAQEEfjB8MDsGCCsGAQUFBzABhi9odHRwOi8vb2NzcC50ZXN0NC5idXlwYXNzLm5vL29jc3AvQlBDbGFzczNUNENBMzA9BggrBgEFBQcwAoYxaHR0cDovL2NydC50ZXN0NC5idXlwYXNzLm5vL2NydC9CUENsYXNzM1Q0Q0EzLmNlcjANBgkqhkiG9w0BAQsFAAOCAQEAZGpNYvzd7mmh7V2OlQOc0B7+1N3apZMEnMj6iiPH6l7oZ5aNFP73fLlDiB2NpPpkQEDcrt6MCnNiO/U3qIkWz/blWDD9k1xUs9ZSeQZJnapuGnN7zSbIUcFnTDNik4cFlJOG7hcnPvxv3ewMSffuhoqnnaPA7J1gzNMA2hkmM7l+sGfCzhr7h9THgo51uGnscTL6PI2qB9qpHN4lR2Aw4yEV0Ve16ENQxASucGc2N+6ZiJQWZiHQL8Z6076NogeMqzG1KIklh5ZogPJxBbnFg72Y0aMrKHw799jm9n64HnOAt1c3qOjduxnjdRMRy+YcIuIy+bUPX4bexmsuX0ehGw==\n" +
        "                    </X509Certificate>\n" +
        "                </X509Data>\n" +
        "            </KeyInfo>\n" +
        "        </Signature>\n" +
        "        <ns9:tidspunkt>2020-04-01T13:25:32.660+02:00</ns9:tidspunkt>\n" +
        "        <ns9:mottak/>\n" +
        "    </ns9:kvittering>\n" +
        "</ns3:StandardBusinessDocument>";

}
