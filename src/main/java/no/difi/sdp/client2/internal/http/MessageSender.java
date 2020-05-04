package no.difi.sdp.client2.internal.http;


import no.difi.sdp.client2.domain.Dokumentpakke;
import no.difi.sdp.client2.domain.sbd.StandardBusinessDocument;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;


public interface MessageSender {

    static Builder create(URI uri) {
        return new Builder(uri);
    }

    void send(StandardBusinessDocument sbd, Dokumentpakke dokumentpakke);

    Optional<IntegrasjonspunktKvittering> hentKvittering();

    void bekreftKvittering(long id);


    @FunctionalInterface
    interface ClientInterceptorWrapper {
        HttpRequestInterceptor wrap(HttpRequestInterceptor httpRequestInterceptor);
    }

    class Builder {

        public static final int DEFAULT_MAX_PER_ROUTE = 10;

        private static final Logger LOG = LoggerFactory.getLogger(MessageSender.Builder.class);

        private final URI endpointUri;
        private final List<InsertInterceptor> interceptorBefore = new ArrayList<>();
        private final List<HttpRequestInterceptor> httpRequestInterceptors = new ArrayList<>();
        private final List<HttpResponseInterceptor> httpResponseInterceptors = new ArrayList<>();
        // Network config
        private int maxTotal = DEFAULT_MAX_PER_ROUTE;
        private int defaultMaxPerRoute = DEFAULT_MAX_PER_ROUTE;
        private HttpHost httpHost;
        private int socketTimeout = 30000;
        private int connectTimeout = 10000;
        private int connectionRequestTimeout = 10000;
        private Duration validateAfterInactivity = Duration.of(2, ChronoUnit.SECONDS);
        private ClientInterceptorWrapper clientInterceptorWrapper = interceptor -> interceptor;

        private Builder(URI uri) {
            this.endpointUri = uri;
        }

        private static void insertInterceptor(final List<HttpRequestInterceptor> meldingInterceptors,
                                              final InsertInterceptor insertInterceptor) {
            for (HttpRequestInterceptor c : meldingInterceptors) {
                if (insertInterceptor.clazz.isAssignableFrom(c.getClass())) {
                    meldingInterceptors.add(meldingInterceptors.indexOf(c), insertInterceptor.interceptor);
                    return;
                }
            }
            throw new IllegalArgumentException("Could not find interceptor of class " + insertInterceptor.clazz);
        }

        public Builder withMeldingInterceptorBefore(final Class<?> clazz, final HttpRequestInterceptor interceptor) {
            interceptorBefore.add(new InsertInterceptor(clazz, interceptor));
            return this;
        }

        public Builder withValidateAfterInactivity(final Duration validateAfterInactivity) {
            this.validateAfterInactivity = validateAfterInactivity;
            return this;
        }

        public Builder withMaxTotal(final int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        public Builder withDefaultMaxPerRoute(final int defaultMaxPerRoute) {
            this.defaultMaxPerRoute = defaultMaxPerRoute;
            return this;
        }

        public Builder withSocketTimeout(final int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder withConnectTimeout(final int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder withConnectionRequestTimeout(final int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
            return this;
        }

        public Builder withHttpProxy(final String proxyHost, final int proxyPort) {
            httpHost = new HttpHost(proxyHost, proxyPort, "https");
            return this;
        }

        public Builder withHttpProxy(final String proxyHost, final int proxyPort, final String scheme) {
            httpHost = new HttpHost(proxyHost, proxyPort, scheme);
            return this;
        }

        public Builder withHttpRequestInterceptors(final HttpRequestInterceptor... httpRequestInterceptors) {
            this.httpRequestInterceptors.addAll(asList(httpRequestInterceptors));
            return this;
        }

        public Builder withHttpResponseInterceptors(final HttpResponseInterceptor... httpResponseInterceptors) {
            this.httpResponseInterceptors.addAll(asList(httpResponseInterceptors));
            return this;
        }

        public Builder withClientInterceptorWrapper(final ClientInterceptorWrapper clientInterceptorWrapper) {
            this.clientInterceptorWrapper = clientInterceptorWrapper;
            return this;
        }

        public MessageSender build() {
            List<HttpRequestInterceptor> meldingInterceptors = new ArrayList<>();

            for (InsertInterceptor insertInterceptor : interceptorBefore) {
                insertInterceptor(meldingInterceptors, insertInterceptor);
            }

//            HttpRequestInterceptor[] clientInterceptors = new HttpRequestInterceptor[meldingInterceptors.size()];
//            for (int i = 0; i < meldingInterceptors.size(); i++) {
//                clientInterceptors[i] = clientInterceptorWrapper.wrap(meldingInterceptors.get(i));
//            }

            return new MessageSenderImpl(endpointUri, getHttpClient());
        }


        private CloseableHttpClient getHttpClient() {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setValidateAfterInactivity((int)validateAfterInactivity.toMillis());
            connectionManager.setMaxTotal(maxTotal);
            connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

            RequestConfig.Builder requestConfigBuilder = RequestConfig.copy(RequestConfig.DEFAULT).setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout)
                    .setCookieSpec(CookieSpecs.IGNORE_COOKIES);

            if (httpHost != null) {
                requestConfigBuilder.setProxy(httpHost);
            }

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(connectTimeout).build();
            httpClientBuilder.setDefaultSocketConfig(socketConfig);

            for (HttpRequestInterceptor httpRequestInterceptor : httpRequestInterceptors) {
                httpClientBuilder.addInterceptorFirst(httpRequestInterceptor);
            }

            for (HttpResponseInterceptor httpResponseInterceptor : httpResponseInterceptors) {
                httpClientBuilder.addInterceptorFirst(httpResponseInterceptor);
            }

            return httpClientBuilder.setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfigBuilder.build()).build();
        }

    }

    class InsertInterceptor {

        private final Class<?> clazz;
        private final HttpRequestInterceptor interceptor;


        public InsertInterceptor(final Class<?> clazz, final HttpRequestInterceptor interceptor) {
            this.clazz = clazz;
            this.interceptor = interceptor;
        }
    }
}
