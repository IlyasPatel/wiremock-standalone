package ilyas.patel;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.common.HttpClientUtils.getEntityAsByteArrayAndCloseStream;
import static com.google.common.collect.Iterables.getFirst;

public class WireMockResponse {

    private final HttpResponse httpResponse;
    private final byte[] content;

    public WireMockResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
        content = getEntityAsByteArrayAndCloseStream(httpResponse);
    }

    public int statusCode() {
        return httpResponse.getStatusLine().getStatusCode();
    }

    public String content() {
        if(content==null) {
            return null;
        }
        return new String(content, Charsets.UTF_8);
    }

    public byte[] binaryContent() {
        return content;
    }

    public String firstHeader(String key) {
        return getFirst(headers().get(key), null);
    }

    public Multimap<String, String> headers() {
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();

        for (Header header: httpResponse.getAllHeaders()) {
            builder.put(header.getName(), header.getValue());
        }

        return builder.build();
    }

}
