package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Miljo;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class KlientKonfigurasjonTest {

    @Test
    public void uri_builder_initializes_meldingsformidler_root_and_miljo() {
        URI integrasjonspunktRoot = URI.create("http://meldingsformidlerroot.no");

        @SuppressWarnings("deprecation")
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon.builder(integrasjonspunktRoot).build();

        assertThat(klientKonfigurasjon.getIntegrasjonspunktRoot(), is(integrasjonspunktRoot));
        assertThat(klientKonfigurasjon.getMiljo().getIntegrasjonspunktRoot(), is(integrasjonspunktRoot));
    }

    @Test
    public void miljo_builder_initializes_meldingsformidler_root_and_miljo() {
        Miljo funksjoneltTestmiljo = new Miljo(URI.create("http://meldingsformidlerroot.no"));;
        KlientKonfigurasjon klientKonfigurasjon = KlientKonfigurasjon
                .builder(funksjoneltTestmiljo)
                .build();

        Miljo actualMiljo = klientKonfigurasjon.getMiljo();

        assertThat(actualMiljo, is(funksjoneltTestmiljo));
        assertThat(actualMiljo.getIntegrasjonspunktRoot(), is(klientKonfigurasjon.getIntegrasjonspunktRoot()));
    }

}
