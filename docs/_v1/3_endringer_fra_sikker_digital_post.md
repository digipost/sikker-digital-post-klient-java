---
title: Endringer fra sikker digital post klient
identifier: endringerfrasikkerdigitalpostklient
layout: default
---

Formålet med dette biblioteket er å tilby en drop-in erstatning for sikker-digital-post-klient-biblioteket, slik at man enkelt kan ta i bruk Integrasjonspunktet fremfor direkte sending til meldingsformidleren.
API-et til sikker-digital-post-klient er bevart etter beste evne. Under er det listet opp konsepter som skiller seg fra sikker-digital-post-klient.


- Konfigurasjon av miljø
- Endring i opprettelse av printforsendelse
- Metoder og klasser markert som deprecated 
- mpcID
- Prioritet
- Nøkkelpar 
- Fakutererbare bytes
- SOAP-relatert kode er fjernet 
- SendResultat


### Konfigurasjon av miljø
Konfigurasjon av miljø gjøres nå ved å spesifisere URL-en til integrasjonspunktet som man ønsker å benytte.

`Klientkonfigurasjon` tilbyr nå tre metoder for å sette miljø. Vær OBS på å endre parameter hvis string- eller URI-builder-metoden ble benyttet i tidligere klientbibliotek da man i tilfellet ikke vil få noen typefeil/synlige feil i koden. 

```java
//builder(Miljo miljo)}
Miljo INTEGRASJONSPUNKT_LOCALHOST = new Miljo(URI.create("http://localhost:9093"));
KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(miljo).build();


//builder(URI integrasjonspunktRoot)
URI INTEGRASJONSPUNKT_URI = URI.create("http://localhost:9093");
KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(INTEGRASJONSPUNKT_URI).build();

//@Deprecated
//builder(String integrasjonspunktRootUri)
String INTEGRASJONSPUNKT_URI = "http://localhost:9093";
KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(INTEGRASJONSPUNKT_URI).build();
```    

### Endring i opprettelse av printforsendelse:

```java
// Tidligere
Forsendelse forsendelse = Forsendelse.fysisk(avsender, fysiskPost, dokumentpakke).build();

// Nå   
String mottakerPersonNummer = "27127000293";
final Mottaker mottaker = Mottaker.builder(mottakerPersonNummer).build();
Forsendelse forsendelse = Forsendelse.fysisk(avsender, fysiskPost, dokumentpakke, mottaker).build();
```

### Metoder og klasser markert som deprecated.
Utdaterte konsepter er markert med Deprecated. Det er anbefalt å benytte overloadet metoder i buildere.
Se JavaDoc for deprecated metoder for hvilken alternativ metode som burde benyttes.

### mpcID
Fra tidligere dokumentasjon:
```
Sett en unik `Forsendelse.mpcId` for å unngå at det konsumeres kvitteringer på tvers av ulike avsendere med samme organisasjonsnummer. Dette er nyttig i større organisasjoner som har flere avsenderenheter. I tillegg kan det være veldig nyttig i utvikling for å unngå at utviklere og testmiljøer går i beina på hverandre.
```

MpcID settes nå i integrasjonspunktet.

### Prioritet
Kø-prioritet settes nå i integrasjonspunktet.

### Nøkkelpar
Nøkler håndteres av integrasjonspunktet.

### Fakturerbare bytes
Denne informasjonen er ikke lengre mulig å hente ut fra klientbiblioteket da integrasjonspunktet ikke eksponerer dette.

### SOAP-relatert kode er fjernet.
Dette innebærer metoden `getMeldingTemplate` som ble eksponert fra `SikkerDigitalPostKlient`.

### SendResultat
Grunnet data eksponert fra integrasjonspunktet er det gjort endringer i følgende metoder i klassen `SendResultat`:
```java 
// Returnerer nå `null`
String getMeldingsId(); 

// Returnerer nå `null`
String getReferanseTilMeldingsId();

// Returnerer nå `0` 
long getFakturerbareBytes(); 
```
Det er samtidig innført en ny metode, som burde benyttes fremfor dem over:
```java
String getConversationId()
```
