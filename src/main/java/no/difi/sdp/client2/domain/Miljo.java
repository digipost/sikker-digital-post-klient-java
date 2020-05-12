package no.difi.sdp.client2.domain;

import java.net.URI;


public class Miljo {

    final URI integrasjonspunktRoot;

    public Miljo(URI integrasjonspunktRoot) {
        this.integrasjonspunktRoot = integrasjonspunktRoot;
    }

    public URI getIntegrasjonspunktRoot() {
        return integrasjonspunktRoot;
    }

}
