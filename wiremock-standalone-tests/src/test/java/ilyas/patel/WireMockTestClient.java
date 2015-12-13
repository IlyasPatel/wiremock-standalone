package ilyas.patel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URI;

public class WireMockTestClient {

    private static final String LOCAL_WIREMOCK_ROOT = "http://%s:%d%s";

    private int port;
    private String address;

    public WireMockTestClient(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public WireMockTestClient(int port) {
        this(port, "localhost");
    }

    public WireMockResponse get(String url, TestHttpHeader... headers) {
        String actualUrl = URI.create(url).isAbsolute() ? url : mockServiceUrlFor(url);
        HttpUriRequest httpRequest = new HttpGet(actualUrl);
        return executeMethodAndCovertExceptions(httpRequest, headers);
    }

    private String mockServiceUrlFor(String path) {
        return String.format(LOCAL_WIREMOCK_ROOT, address, port, path);
    }

    private WireMockResponse executeMethodAndCovertExceptions(HttpUriRequest httpRequest, TestHttpHeader... headers) {
        try {
            for (TestHttpHeader header : headers) {
                httpRequest.addHeader(header.getName(), header.getValue());
            }
            HttpResponse httpResponse = httpClient().execute(httpRequest);
            return new WireMockResponse(httpResponse);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static HttpClient httpClient() {
        return HttpClientBuilder.create()
                .disableAuthCaching()
                .disableAutomaticRetries()
                .disableCookieManagement()
                .disableRedirectHandling()
                .disableContentCompression()
                .build();
    }
}
