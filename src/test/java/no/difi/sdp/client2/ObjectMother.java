package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.AktoerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Avsender;
import no.difi.sdp.client2.domain.AvsenderOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Databehandler;
import no.difi.sdp.client2.domain.DatabehandlerOrganisasjonsnummer;
import no.difi.sdp.client2.domain.Dokument;
import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.Forsendelse;
import no.difi.sdp.client2.domain.MetadataDokument;
import no.difi.sdp.client2.domain.Mottaker;
import no.difi.sdp.client2.domain.NoValidationNoekkelpar;
import no.difi.sdp.client2.domain.Noekkelpar;
import no.difi.sdp.client2.domain.Organisasjonsnummer;
import no.difi.sdp.client2.domain.Sertifikat;
import no.difi.sdp.client2.domain.digital_post.DigitalPost;
import no.difi.sdp.client2.domain.digital_post.EpostVarsel;
import no.difi.sdp.client2.domain.digital_post.Sikkerhetsnivaa;
import no.difi.sdp.client2.domain.digital_post.SmsVarsel;
import no.difi.sdp.client2.domain.fysisk_post.FysiskPost;
import no.difi.sdp.client2.domain.fysisk_post.KonvoluttAdresse;
import no.difi.sdp.client2.domain.fysisk_post.Posttype;
import no.difi.sdp.client2.domain.fysisk_post.Returhaandtering;
import no.difi.sdp.client2.domain.fysisk_post.Utskriftsfarge;
import no.difi.sdp.client2.internal.TrustedCertificates;
import no.digipost.security.DigipostSecurity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ObjectMother {

    public static final X509Certificate POSTEN_TEST_CERTIFICATE = DigipostSecurity.readCertificate("certificates/test/posten_test.pem");
    public static final X509Certificate POSTEN_PROD_CERTIFICATE = DigipostSecurity.readCertificate("certificates/prod/posten_prod.pem");
    public static final String SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS = "avsender";
    public static final String SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD = "password1234";
    public static final String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE = "no_difi_sdp_client2_virksomhetssertifikat_sti";
    public static final String TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE = "no_difi_sdp_client2_virksomhetssertifikat_alias";
    public static final String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE = "no_difi_sdp_client2_virksomhetssertifikat_passord";
    public static String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE = System.getenv(TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE);
    public static String TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE = System.getenv(TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_ENVIRONMENT_VARIABLE);
    public static String TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE = System.getenv(TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_ENVIRONMENT_VARIABLE);
    public static final String PERSONIDENTIFIKATOR = "30066714477";

    public static Noekkelpar testEnvironmentNoekkelpar() {
        return Noekkelpar.fraKeyStoreUtenTrustStore(getVirksomhetssertifikat(), TESTMILJO_VIRKSOMHETSSERTIFIKAT_ALIAS_VALUE, TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE);
    }

    public static KeyStore getVirksomhetssertifikat() {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE), TESTMILJO_VIRKSOMHETSSERTIFIKAT_PASSWORD_VALUE.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException(MessageFormat.format("Fant ikke virksomhetssertifikat på sti {0}. Eksporter environmentvariabel {1} til virksomhetssertifikatet.", TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_VALUE, TESTMILJO_VIRKSOMHETSSERTIFIKAT_PATH_ENVIRONMENT_VARIABLE), e);
        }
    }

    public static KeyStore testEnvironmentTrustStore() {
        return getKeyStore("/test-environment-trust-keystore.jceks", "sophisticatedpassword", "jceks");
    }

    private static KeyStore getKeyStore(String path, String password, String keyStoreType) {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(ObjectMother.class.getResourceAsStream(path), password.toCharArray());
            return keyStore;

        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke laste keystore", e);
        }
    }

    public static Forsendelse digitalForsendelse() {
        DigitalPost digitalPost = digitalPost();

        Dokument hovedDokument = Dokument.builder("Sensitiv brevtittel", "faktura.pdf", new ByteArrayInputStream("hei".getBytes()))
                .mimeType("application/pdf")
                .metadataDocument(new MetadataDokument("lenke.xml", "application/vnd.difi.dpi.lenke+xml", "<lenke></lenke>".getBytes()))
                .build();

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(new ArrayList<Dokument>())
                .build();

        Avsender avsender = avsender();

        return Forsendelse.digital(avsender, digitalPost, dokumentpakke)
                .konversasjonsId(UUID.randomUUID().toString())
                .spraakkode("NO")
                .build();
    }

    public static DigitalPost digitalPost() {
        String varslingsTekst = "Du har mottatt brev i din digitale postkasse";

        EpostVarsel epostVarsel = EpostVarsel.builder(varslingsTekst)
                .build();

        Mottaker mottaker = Mottaker.builder(PERSONIDENTIFIKATOR, "", mottakerSertifikat(), Organisasjonsnummer.of("984661185"))
                .build();

        SmsVarsel smsVarsel = SmsVarsel.builder(varslingsTekst)
                .build();

        return DigitalPost.builder(mottaker, "Forretningsmeldingtittel")
                .virkningsdato(new Date())
                .aapningskvittering(false)
                .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
                .epostVarsel(epostVarsel)
                .smsVarsel(smsVarsel)
                .build();
    }

    public static Forsendelse fysiskPostForsendelse(){
        final Mottaker mottaker = Mottaker.builder("27127000293").build();
        return Forsendelse.fysisk(avsender(), fysiskPost(), printDokumentpakke(), mottaker).build();
    }

    public static FysiskPost fysiskPost() {
        final KonvoluttAdresse konvoluttAdresse = KonvoluttAdresse.build("Jarand Bjarte").iNorge(
            "Storgata 1"
            , null
            , null
            , null
            , "0155"
            , "Oslo"
        ).build();

        return FysiskPost.builder()
            .adresse(konvoluttAdresse)
            .retur(Returhaandtering.DIREKTE_RETUR, konvoluttAdresse)
            .sendesMed(Posttype.A_PRIORITERT)
            .utskrift(Utskriftsfarge.FARGE)
            .build();
    }

    public static Avsender avsender() {
        return Avsender.builder(avsenderOrganisasjonsnummer()).build();
    }

    public static Sertifikat mottakerSertifikat() {
        return DigipostMottakerSertifikatTest();
    }

    public static AvsenderOrganisasjonsnummer avsenderOrganisasjonsnummer() {
        return AktoerOrganisasjonsnummer.of("984661185").forfremTilAvsender();
    }

    private static Sertifikat DigipostMottakerSertifikatTest() {
        return Sertifikat.fraBase64X509String(
                "MIIE7jCCA9agAwIBAgIKGBZrmEgzTHzeJjANBgkqhkiG9w0BAQsFADBRMQswCQYD" +
                        "VQQGEwJOTzEdMBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxIzAhBgNVBAMM" +
                        "GkJ1eXBhc3MgQ2xhc3MgMyBUZXN0NCBDQSAzMB4XDTE0MDQyNDEyMzA1MVoXDTE3" +
                        "MDQyNDIxNTkwMFowVTELMAkGA1UEBhMCTk8xGDAWBgNVBAoMD1BPU1RFTiBOT1JH" +
                        "RSBBUzEYMBYGA1UEAwwPUE9TVEVOIE5PUkdFIEFTMRIwEAYDVQQFEwk5ODQ2NjEx" +
                        "ODUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCLCxU4oBhtGmJxXZWb" +
                        "dWdzO2uA3eRNW/kPdddL1HYl1iXLV/g+H2Q0ELadWLggkS+1kOd8/jKxEN++biMm" +
                        "mDqqCWbzNdmEd1j4lctSlH6M7tt0ywmXIYdZMz5kxcLAMNXsaqnPdikI9uPJZQEL" +
                        "3Kc8hXhXISvpzP7gYOvKHg41uCxu1xCZQOM6pTlNbxemBYqvES4fRh2xvB9aMjwk" +
                        "B4Nz8jrIsyoPI89i05OmGMkI5BPZt8NTa40Yf3yU+SQECW0GWalB5cxaTMeB01tq" +
                        "slUzBJPV3cQx+AhtQG4hkOhQnAMDJramSPVtwbEnqOjQ+lyNmg5GQ4FJO02ApKJT" +
                        "ZDTHAgMBAAGjggHCMIIBvjAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFD+u9XgLkqNw" +
                        "IDVfWvr3JKBSAfBBMB0GA1UdDgQWBBQ1gsJfVC7KYGiWVLP7ZwzppyVYTTAOBgNV" +
                        "HQ8BAf8EBAMCBLAwFgYDVR0gBA8wDTALBglghEIBGgEAAwIwgbsGA1UdHwSBszCB" +
                        "sDA3oDWgM4YxaHR0cDovL2NybC50ZXN0NC5idXlwYXNzLm5vL2NybC9CUENsYXNz" +
                        "M1Q0Q0EzLmNybDB1oHOgcYZvbGRhcDovL2xkYXAudGVzdDQuYnV5cGFzcy5uby9k" +
                        "Yz1CdXlwYXNzLGRjPU5PLENOPUJ1eXBhc3MlMjBDbGFzcyUyMDMlMjBUZXN0NCUy" +
                        "MENBJTIwMz9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0MIGKBggrBgEFBQcBAQR+" +
                        "MHwwOwYIKwYBBQUHMAGGL2h0dHA6Ly9vY3NwLnRlc3Q0LmJ1eXBhc3Mubm8vb2Nz" +
                        "cC9CUENsYXNzM1Q0Q0EzMD0GCCsGAQUFBzAChjFodHRwOi8vY3J0LnRlc3Q0LmJ1" +
                        "eXBhc3Mubm8vY3J0L0JQQ2xhc3MzVDRDQTMuY2VyMA0GCSqGSIb3DQEBCwUAA4IB" +
                        "AQCe67UOZ/VSwcH2ov1cOSaWslL7JNfqhyNZWGpfgX1c0Gh+KkO3eVkMSozpgX6M" +
                        "4eeWBWJGELMiVN1LhNaGxBU9TBMdeQ3SqK219W6DXRJ2ycBtaVwQ26V5tWKRN4Ul" +
                        "RovYYiY+nMLx9VrLOD4uoP6fm9GE5Fj0vSMMPvOEXi0NsN+8MUm3HWoBeUCLyFpe" +
                        "7/EPsS/Wud5bb0as/E2zIztRodxfNsoiXNvWaP2ZiPWFunIjK1H/8EcktEW1paiP" +
                        "d8AZek/QQoG0MKPfPIJuqH+WJU3a8J8epMDyVfaek+4+l9XOeKwVXNSOP/JSwgpO" +
                        "JNzTdaDOM+uVuk75n2191Fd7");
    }

    public static Databehandler databehandler() {
        return Databehandler.builder(databehandlerOrganisasjonsnummer()).build();
    }

    public static Noekkelpar selvsignertNoekkelparUtenTrustStore() {
        return new NoValidationNoekkelpar(selvsignertKeyStore(), SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);
    }

    public static Noekkelpar selvsignertNoekkelparMedTrustStore() {
        return new NoValidationNoekkelpar(selvsignertKeyStore(), TrustedCertificates.getTrustStore(), SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_ALIAS, SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD);
    }

    public static DatabehandlerOrganisasjonsnummer databehandlerOrganisasjonsnummer() {
        return AktoerOrganisasjonsnummer.of("984661185").forfremTilDatabehandler();
    }

    public static KeyStore selvsignertKeyStore() {
        return getKeyStore("/selfsigned-keystore.jks", SELVSIGNERT_VIRKSOMHETSSERTIFIKAT_PASSORD, "jks");
    }

    public static Mottaker mottaker() {
        return Mottaker.builder("01129955131", "postkasseadresse", mottakerSertifikat(), Organisasjonsnummer.of("984661185")).build();
    }

//    public static EbmsApplikasjonsKvittering createEbmsFeil(final SDPFeiltype feiltype) {
//        SDPFeil sdpFeil = new SDPFeil(null, ZonedDateTime.now(), feiltype, "Feilinformasjon");
//        return createEbmsKvittering(sdpFeil);
//    }
//
//    public static EbmsApplikasjonsKvittering createEbmsKvittering(final Object sdpMelding) {
//        Organisasjonsnummer avsenderOrganisasjonsnummer = Organisasjonsnummer.of("984661185");
//        Organisasjonsnummer mottakerOrganisasjonsnummer = Organisasjonsnummer.of("988015814");
//
//        StandardBusinessDocument sbd = new StandardBusinessDocument().withStandardBusinessDocumentHeader(
//                new StandardBusinessDocumentHeader()
//                        .withHeaderVersion("1.0")
//                        .withSender(new Partner().withIdentifier(new PartnerIdentification(avsenderOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), Organisasjonsnummer.ISO6523_ACTORID)))
//                        .withReceiver(new Partner().withIdentifier(new PartnerIdentification(mottakerOrganisasjonsnummer.getOrganisasjonsnummerMedLandkode(), Organisasjonsnummer.ISO6523_ACTORID)))
//                        .withDocumentIdentification(new DocumentIdentification()
//                                .withStandard("urn:no:difi:sdp:1.0")
//                                .withTypeVersion("1.0")
//                                .withInstanceIdentifier("instanceIdentifier")
//                                .withType(StandardBusinessDocumentFactory.Type.from((SDPMelding) sdpMelding).toString())
//                                .withCreationDateAndTime(ZonedDateTime.now())
//                        )
//                        .withBusinessScope(new BusinessScope()
//                                .withScope(new Scope()
//                                        .withIdentifier("urn:no:difi:sdp:1.0")
//                                        .withType("ConversationId")
//                                        .withInstanceIdentifier(UUID.randomUUID().toString())
//                                )
//                        )
//        )
//                .withAny(sdpMelding);
//
//        EbmsApplikasjonsKvittering build = EbmsApplikasjonsKvittering.create(EbmsAktoer.avsender(avsenderOrganisasjonsnummer.getOrganisasjonsnummer()), EbmsAktoer.postkasse(mottakerOrganisasjonsnummer.getOrganisasjonsnummer()), sbd)
//                .withReferences(getReferences())
//                .withRefToMessageId("RefToMessageId")
//                .build();
//
//        return build;
//    }
//
//    private static List<Reference> getReferences() {
//        List<Reference> incomingReferences = new ArrayList<>();
//
//        Reference reference = new Reference();
//        reference.setURI("#id-f2ecf3b2-101e-433b-a30d-65a9b6779b5a");
//        incomingReferences.add(reference);
//
//        List<Transform> transforms = new ArrayList<>();
//        Transform transform = new Transform();
//        transform.setAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
//        transforms.add(transform);
//
//        DigestMethod digestMethod = new DigestMethod();
//        digestMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
//        reference.setDigestMethod(digestMethod);
//        reference.setDigestValue("xQbKUtuEGSrsgZsSAT5rF+/yflr+hl2cUC4cKyiMxRM=".getBytes());
//
//        Transforms transformsContainer = new Transforms(transforms);
//        reference.setTransforms(transformsContainer);
//        return incomingReferences;
//    }
//
//    public static EbmsApplikasjonsKvittering createEbmsAapningsKvittering() {
//        SDPKvittering aapningsKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, null, new SDPAapning(), null, null);
//        return createEbmsKvittering(aapningsKvittering);
//    }
//
//    public static EbmsApplikasjonsKvittering createEbmsLeveringsKvittering() {
//        SDPKvittering leveringsKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, null, null, new SDPLevering(), null);
//
//        return createEbmsKvittering(leveringsKvittering);
//    }
//
//    public static EbmsApplikasjonsKvittering createEbmsMottaksKvittering() {
//        SDPKvittering mottaksKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, null, null, null, new SDPMottak());
//        return createEbmsKvittering(mottaksKvittering);
//    }
//
//    public static EbmsApplikasjonsKvittering createEbmsReturpostKvittering() {
//        SDPKvittering returpostKvittering = new SDPKvittering(null, ZonedDateTime.now(), new SDPReturpost(), null, null, null, null);
//        return createEbmsKvittering(returpostKvittering);
//    }
//
//    public static EbmsApplikasjonsKvittering createEbmsVarslingFeiletKvittering(final SDPVarslingskanal varslingskanal) {
//        SDPVarslingfeilet sdpVarslingfeilet = new SDPVarslingfeilet(varslingskanal, "Varsling feilet 'Viktig brev'");
//        SDPKvittering varslingFeiletKvittering = new SDPKvittering(null, ZonedDateTime.now(), null, sdpVarslingfeilet, null, null, null);
//        return createEbmsKvittering(varslingFeiletKvittering);
//    }

    public static Dokumentpakke dokumentpakke() {
        Dokument dokument = Dokument.builder("Sensitiv tittel", "filnavn", new ByteArrayInputStream("hei".getBytes())).build();
        return Dokumentpakke.builder(dokument).build();
    }

    public static Dokumentpakke printDokumentpakke() {
        Dokument dokument = Dokument.builder("Sensitiv tittel", "filnavn", ObjectMother.class.getResourceAsStream("/printvennligA4.pdf")).build();
        return Dokumentpakke.builder(dokument).build();
    }

    public static Forsendelse digitalForsendelse(String mpcId, InputStream dokumentStream) {
        DigitalPost digitalPost = digitalPost();

        Dokument hovedDokument = Dokument.builder("HoveddokumentTittel", "faktura.pdf", ObjectMother.class.getResourceAsStream("/test.pdf"))
            .mimeType("application/pdf")
            .metadataDocument(new MetadataDokument("lenke.xml", "application/vnd.difi.dpi.lenke+xml", "<lenke></lenke>".getBytes()))
            .build();

        final ArrayList<Dokument> list = new ArrayList<>();
        list.add(Dokument.builder("vedleggtittel", "vedlegg.pdf", dokumentStream).build());

        Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
                .vedlegg(list)
                .build();

        Avsender avsender = avsender();

        return Forsendelse.digital(avsender, digitalPost, dokumentpakke)
                .konversasjonsId(UUID.randomUUID().toString())
                .mpcId(mpcId)
                .spraakkode("NO")
                .build();
    }

    public static Databehandler databehandlerMedSertifikat(final Organisasjonsnummer organisasjonsnummer, final Noekkelpar noekkelpar) {
        return Databehandler
                .builder(AktoerOrganisasjonsnummer.of(organisasjonsnummer).forfremTilDatabehandler())
                .build();
    }

}
