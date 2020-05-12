---
title: Logging av forespørsel og respons
identifier: logging
layout: default
---

Klienten støtter registrering av http interceptors som kan brukes for direkte tilgang og til logging av request og responser mot integrasjonspunktet.

```java
KlientKonfigurasjon.builder()
    .httpRequestInterceptors(new HttpRequestInterceptor() {
        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            System.out.println("Utgående request!");
        }
    })
    .httpResponseInterceptors(new HttpResponseInterceptor() {
        @Override
        public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
            System.out.println("Innkommende request!");
        }
    })
    .build();
```

### Debugging

> Merk: Innstillingene under er ikke anbefalt i produksjonsmiljøer.

Den underliggende http-klienten har støtte for å logge meldingene som sendes over nettverket. Sett `org.apache.http.wire` til `debug` eller lavere for å slå på denne loggingen. 
Alternativt kan logging av requests gjøres ved hjelp av interceptors som beskrevet over.

