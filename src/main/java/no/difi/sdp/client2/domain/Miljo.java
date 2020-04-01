package no.difi.sdp.client2.domain;

import no.digipost.security.cert.Trust;

import java.net.URI;


public class Miljo {

    @Deprecated
    public static Miljo PRODUKSJON = new Miljo();
    @Deprecated
    public static Miljo PRODUKSJON_NORSK_HELSENETT = new Miljo();
    @Deprecated
    public static Miljo FUNKSJONELT_TESTMILJO = new Miljo();
    @Deprecated
    public static Miljo FUNKSJONELT_TESTMILJO_NORSK_HELSENETT = new Miljo();

    Trust godkjenteKjedeSertifikater;
    URI meldingsformidlerRoot;

    private Miljo() {}

    public Miljo(Trust godkjenteKjedeSertifikater, URI meldingsformidlerRoot) {
        this.godkjenteKjedeSertifikater = godkjenteKjedeSertifikater;
        this.meldingsformidlerRoot = meldingsformidlerRoot;
    }

    @Deprecated
    public URI getMeldingsformidlerRoot() {
        return meldingsformidlerRoot;
    }

    @Deprecated
    public void setMeldingsformidlerRoot(URI meldingsformidlerRoot) {
        this.meldingsformidlerRoot = meldingsformidlerRoot;
    }
}
