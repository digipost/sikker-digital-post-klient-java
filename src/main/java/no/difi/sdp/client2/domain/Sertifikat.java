package no.difi.sdp.client2.domain;

import no.difi.sdp.client2.domain.exceptions.SertifikatException;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static java.util.Base64.getDecoder;

/**
 * Benyttes ikke.
 */
@Deprecated
public class Sertifikat {

    private X509Certificate x509Certificate;

    private Sertifikat(X509Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
    }

    @Deprecated
    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }

    @Deprecated
    public byte[] getEncoded() {
        try {
            return x509Certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new SertifikatException("Kunne ikke hente encoded utgave av sertifikatet", e);
        }
    }

    @Deprecated
    public static Sertifikat fraBase64X509String(String base64) {
        try {

            return lagSertifikat(getDecoder().decode(base64));
        } catch (CertificateException e) {
            throw new SertifikatException("Kunne ikke lese sertifikat fra base64-streng", e);
        }
    }

    @Deprecated
    public static Sertifikat fraByteArray(byte[] certificate) {
        try {
            return lagSertifikat(certificate);
        } catch (CertificateException e) {
            throw new SertifikatException("Kunne ikke lese sertifikat fra byte array", e);
        }
    }

    @Deprecated
    public static Sertifikat fraCertificate(X509Certificate certificate) {
        return new Sertifikat(certificate);
    }

    @Deprecated
    public static Sertifikat fraKeyStore(KeyStore keyStore, String alias) {
        Certificate certificate;
        try {
            certificate = keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new SertifikatException("Klarte ikke lese sertifikat fra keystore", e);
        }

        if (certificate == null) {
            throw new SertifikatException("Kunne ikke finne sertifikat i keystore. Er du sikker på at det er brukt keystore med et sertifikat og at du har oppgitt riktig alias?");
        }

        if (!(certificate instanceof X509Certificate)) {
            throw new SertifikatException("Klienten støtter kun X509-sertifikater. Fikk sertifikat av typen " + certificate.getClass().getSimpleName());
        }

        return new Sertifikat((X509Certificate) certificate);
    }

    private static Sertifikat lagSertifikat(byte[] certificate) throws CertificateException {
        X509Certificate x509Certificate = (X509Certificate) CertificateFactory
                .getInstance("X509")
                .generateCertificate(new ByteArrayInputStream(certificate));
        return new Sertifikat(x509Certificate);
    }
}
