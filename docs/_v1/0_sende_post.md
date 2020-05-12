---
title: Sende post
identifier: sendepost
layout: default
---

For å sende post så må du først lage en `DigitalPost` eller en `FysiskPost`, så opprette en `Forsendelse` og legge ved posten. Deretter sendes denne gjennom en `SikkerDigitalPostKlient`. Når brevet er sendt så kan du spørre om status på en meldingskø. Hvis du mottar en kvittering så kan du sjekke innholdet og så bekrefte mottatt kvittering.

### Opprette digital post

```java
Mottaker mottaker = Mottaker
        .builder("99999999999")
        .build();

// Integrasjonspunktet henter mobilnummer til mottaker fra KRR.
SmsVarsel smsVarsel = SmsVarsel.builder("Du har mottatt brev i din digitale postkasse")
        .build();

// Integrasjonspunktet henter E-postadresse til mottaker fra KRR.
EpostVarsel epostVarsel = EpostVarsel.builder("Du har mottatt brev i din digitale postkasse")
        .varselEtterDager(asList(1, 4, 10))
        .build();

DigitalPost digitalPost = DigitalPost
        .builder(mottaker, "Ikke-sensitiv tittel")
        .virkningsdato(new Date())
        .aapningskvittering(false)
        .sikkerhetsnivaa(Sikkerhetsnivaa.NIVAA_3)
        .epostVarsel(epostVarsel)
        .smsVarsel(smsVarsel)
        .build();
```

### Opprette fysisk post

```java
FysiskPost fysiskPost = FysiskPost.builder()
        .adresse(
                KonvoluttAdresse.build("Ola Nordmann")
                        .iNorge("Fjellheimen 22", "", "", "0001", "Oslo")
                        .build())
        .retur(
                Returhaandtering.DIREKTE_RETUR.MAKULERING_MED_MELDING,
                KonvoluttAdresse.build("Returkongen")
                        .iNorge("Returveien 3", "", "", "0002", "Oslo")
                        .build())
        .sendesMed(Posttype.A_PRIORITERT)
        .utskrift(Utskriftsfarge.FARGE)
        .build();
```

### Opprette selve forsendelsen

```java
DigitalPost digitalPost = null; //Som initiert tidligere

Dokument hovedDokument = Dokument
        .builder("Sensitiv brevtittel", new File("/sti/til/dokument"))
        .mimeType("application/pdf")
        .build();

Dokumentpakke dokumentpakke = Dokumentpakke.builder(hovedDokument)
        .vedlegg(new ArrayList<>())
        .build();

AvsenderOrganisasjonsnummer avsenderOrgnr =
        AktoerOrganisasjonsnummer.of("999999999").forfremTilAvsender();

Avsender avsender = Avsender
        .builder(avsenderOrgnr)
        .build();

Forsendelse forsendelse = Forsendelse
        .digital(avsender, digitalPost, dokumentpakke)
        .konversasjonsId(UUID.randomUUID().toString())
        .spraakkode("NO")
        .build();
```

### Utvidelser
Difi har egne dokumenttyper, eller utvidelser, som kan sendes som metadata til hoveddokumenter. Disse utvidelsene er strukturerte xml-dokumenter
med egne mime-typer. Disse utvidelsene benyttes av postkasseleverandørene til å gi en øket brukeropplevelse for innbyggere.
Les mer om utvidelser på [https://begrep.difi.no/SikkerDigitalPost/](https://begrep.difi.no/SikkerDigitalPost/)

Utvidelsene ligger som generert kode i `sdp-shared`, som er en avhengighet av `sikker-digital-post-klient-java`. Du kan selv lage kode
for å generere xml fra instanser av disse typene med JAXB, eller du kan lage xml på andre måter.

```java
SDPLenke lenke = new SDPLenke();
lenke.setUrl("http://example.com");

StringWriter stringWriter = new StringWriter(lenke.toString().length());
JAXBContext.newInstance(SDPLenke.class).createMarshaller().marshal(lenke, stringWriter);

MetadataDokument innkalling = MetadataDokument.builder(
        "lenke.xml", 
        "application/vnd.difi.dpi.lenke+xml", 
        stringWriter.toString().getBytes()
).build();


Dokument hovedDokument = Dokument
        .builder("Sensitiv brevtittel", new File("/sti/til/dokument"))
        .mimeType("application/pdf")
        .metadataDocument(innkalling)
        .build();
```

### Opprette klient og sende post

```java
Forsendelse forsendelse = null;         //Som initiert tidligere

KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
        .builder(URI.create("http://localhost:9093")) // Integrasjonspunkt URI.
        .connectionTimeout(20, TimeUnit.SECONDS)
        .build();

DatabehandlerOrganisasjonsnummer databehandlerOrgnr =
        AktoerOrganisasjonsnummer.of("555555555").forfremTilDatabehandler();

Databehandler databehandler = Databehandler
        .builder(databehandlerOrgnr)
        .build();

SikkerDigitalPostKlient sikkerDigitalPostKlient = new SikkerDigitalPostKlient(databehandler, klientKonfigurasjon);

try {
    sikkerDigitalPostKlient.send(forsendelse);
} catch (SendException sendException) {
    SendException.AntattSkyldig antattSkyldig = sendException.getAntattSkyldig();
    String message = sendException.getMessage();
}
```

### Hent kvittering og bekreft

```java
SikkerDigitalPostKlient sikkerDigitalPostKlient = null;     //Som initiert tidligere

ForretningsKvittering forretningsKvittering = sikkerDigitalPostKlient.hentKvittering();

if (forretningsKvittering instanceof LeveringsKvittering) {
    //Forsendelse er levert til digital postkasse
} else if (forretningsKvittering instanceof AapningsKvittering) {
    //Forsendelse ble åpnet av mottaker
} else if (forretningsKvittering instanceof MottaksKvittering) {
    //Kvittering på sending av fysisk post
} else if (forretningsKvittering instanceof ReturpostKvittering) {
    //Forsendelse er blitt sendt i retur
} else if (forretningsKvittering instanceof Feil) {
    //Feil skjedde under sending
}

sikkerDigitalPostKlient.bekreft(forretningsKvittering);
```

> Husk at det ikke er mulig å hente nye kvitteringer før du har bekreftet mottak av nåværende.
