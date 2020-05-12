package no.difi.sdp.client2;

import no.difi.sdp.client2.domain.Miljo;
import no.digipost.api.representations.Organisasjonsnummer;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class KlientKonfigurasjon {

    public static Builder builder(Miljo miljo) {
        return new Builder(miljo);
    }

    @Deprecated
    public static Builder builder(String integrasjonspunktRootUri) {
        return builder(URI.create(integrasjonspunktRootUri));
    }

    public static Builder builder(URI integrasjonspunktRoot) {
        return new Builder(new Miljo(integrasjonspunktRoot));
    }


    private final Miljo miljo;
    private String proxyHost;
    private int proxyPort;
    private String proxyScheme = "https";
    private int maxConnectionPoolSize = 10;
    private long socketTimeoutInMillis = TimeUnit.SECONDS.toMillis(30);
    private long connectTimeoutInMillis = TimeUnit.SECONDS.toMillis(10);
    private long connectionRequestTimeoutInMillis = TimeUnit.SECONDS.toMillis(10);
    private HttpRequestInterceptor[] httpRequestInterceptors = new HttpRequestInterceptor[0];
    private HttpResponseInterceptor[] httpResponseInterceptors = new HttpResponseInterceptor[0];


    private KlientKonfigurasjon(Miljo miljo) {
        this.miljo = miljo;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyScheme() {
        return proxyScheme;
    }

    public long getSocketTimeoutInMillis() {
        return socketTimeoutInMillis;
    }

    public long getConnectTimeoutInMillis() {
        return connectTimeoutInMillis;
    }

    public long getConnectionRequestTimeoutInMillis() {
        return connectionRequestTimeoutInMillis;
    }

    public int getMaxConnectionPoolSize() {
        return maxConnectionPoolSize;
    }

    public boolean useProxy() {
        return !isEmpty(proxyHost) && proxyPort > 0;
    }

    @Deprecated
    public Organisasjonsnummer getMeldingsformidlerOrganisasjon() {
        return Organisasjonsnummer.of("984661185");
    }

    public HttpRequestInterceptor[] getHttpRequestInterceptors() {
        return httpRequestInterceptors;
    }

    public HttpResponseInterceptor[] getHttpResponseInterceptors() {
        return httpResponseInterceptors;
    }

    public Miljo getMiljo() {
        return miljo;
    }

    public URI getIntegrasjonspunktRoot() {
        return miljo.getIntegrasjonspunktRoot();
    }


    public static class Builder {

        private final KlientKonfigurasjon target;

        private Builder(Miljo miljo){
            target = new KlientKonfigurasjon(miljo);
        }

        public Builder proxy(String proxyHost, int proxyPort) {
            target.proxyHost = proxyHost;
            target.proxyPort = proxyPort;
            return this;
        }

        public Builder proxy(String proxyHost, int proxyPort, String proxyScheme) {
            target.proxyHost = proxyHost;
            target.proxyPort = proxyPort;
            target.proxyScheme = proxyScheme;
            return this;
        }

        public Builder socketTimeout(int socketTimeout, TimeUnit timeUnit) {
            target.socketTimeoutInMillis = timeUnit.toMillis(socketTimeout);
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout, TimeUnit timeUnit) {
            target.connectTimeoutInMillis = timeUnit.toMillis(connectionTimeout);
            return this;
        }

        public Builder connectionRequestTimeout(int connectionRequestTimeout, TimeUnit timeUnit) {
            target.connectionRequestTimeoutInMillis = timeUnit.toMillis(connectionRequestTimeout);
            return this;
        }

        public Builder maxConnectionPoolSize(int maxConnectionPoolSize) {
            target.maxConnectionPoolSize = maxConnectionPoolSize;
            return this;
        }

        public Builder httpRequestInterceptors(HttpRequestInterceptor... httpRequestInterceptors) {
            target.httpRequestInterceptors = httpRequestInterceptors;
            return this;
        }

        public Builder httpResponseInterceptors(HttpResponseInterceptor... httpResponseInterceptors) {
            target.httpResponseInterceptors = httpResponseInterceptors;
            return this;
        }

        public KlientKonfigurasjon build() {
            return target;
        }
    }

}
