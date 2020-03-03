package no.difi.sdp.client2.internal.temp;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * See
 * {@code <a href="https://jira.springsource.org/browse/SWS-563?focusedCommentId=67885&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-67885">SWS-563</a>}
 */
public class RemoveContentLengthInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        if (request instanceof HttpEntityEnclosingRequest) {
            if (request.containsHeader(HTTP.TRANSFER_ENCODING)) {
                request.removeHeaders(HTTP.TRANSFER_ENCODING);
            }
            if (request.containsHeader(HTTP.CONTENT_LEN)) {
                request.removeHeaders(HTTP.CONTENT_LEN);
            }
        }
    }
}
