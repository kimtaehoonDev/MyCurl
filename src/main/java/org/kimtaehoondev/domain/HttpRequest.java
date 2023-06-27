package org.kimtaehoondev.domain;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.Option;

public class HttpRequest {
    private static final HttpMethod DEFAULT_HTTP_METHOD = HttpMethod.GET;
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_LINE = "";


    private final String requestTarget;

    private final HttpMethod httpMethod;

    private final String httpVersion;

    private final Headers headers;

    private final String body;

    protected HttpRequest(String requestTarget, HttpMethod httpMethod, String httpVersion,
                          Headers headers, String body) {
        this.requestTarget = requestTarget;
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder(URL url) {
        return Builder.builder(url);
    }


    public List<String> serialize() {
        List<String> result = new ArrayList<>();
        String startLine = httpMethod + " " + requestTarget + " " + httpVersion;
        result.add(startLine);

        List<String> total = headers.getAll().stream()
            .map(Header::getPrettier)
            .collect(Collectors.toList());
        result.addAll(total);
        result.add(EMPTY_LINE);

        if (body != null) {
            result.add(body);
        }
        return result;
    }

    public String getHost() {
        String host = headers.get(HeaderName.HOST).stream()
            .findAny()
            .map(Header::getValue)
            .orElseThrow(() -> new RuntimeException("호스트 없어"));
        if (host.contains(":")) {
            return host.substring(0, host.indexOf(":"));
        }
        return host;
    }

    public Integer getPort() {
        String host = headers.get(HeaderName.HOST).stream()
            .findAny()
            .map(Header::getValue)
            .orElseThrow(() -> new RuntimeException("호스트 없어"));
        return Integer.parseInt(host.substring(host.indexOf(":") + 1));
    }

    public static class Builder {
        private final String requestTarget;
        private HttpMethod httpMethod;
        private String httpVersion;
        private final Headers headers;
        private String body;

        private Builder(URL url) {
            String requestTarget = url.getFile();
            if (requestTarget.isBlank()) {
                requestTarget = "/";
            }

            this.requestTarget = requestTarget;
            this.httpMethod = DEFAULT_HTTP_METHOD;
            this.httpVersion = DEFAULT_HTTP_VERSION;
            this.headers = new Headers();
            String host = url.getHost();
            if (url.getPort() != -1) {
                host += (":" + url.getPort());
            }
            this.headers.put(HeaderName.HOST, host);
        }

        public static Builder builder(URL url) {
            return new Builder(url);
        }

        public HttpRequest build() {
            if (httpMethod == null || httpVersion == null) {
                throw new RuntimeException("초기화가 다 되지 않았습니다");
            }
            if (!headers.containsKey(HeaderName.HOST)) {
                throw new RuntimeException("host 헤더는 필수값입니다");
            }
            return new HttpRequest(requestTarget, httpMethod, httpVersion, headers, body);
        }

        public Builder setValueUsingParams(Option option) {
            // getOpt로 Enum을 찾아와서 ~~

            String optionValue = option.getValue();

            if (option.getOpt() == MyOption.HTTP_REQUEST_METHOD.getOptName()) {
                this.httpMethod = HttpMethod.find(optionValue);
                return this;
            }
            if (option.getOpt() == MyOption.HEADER.getOptName()) {
                addHeader(new Header(optionValue));
                return this;
            }
            if (option.getOpt() == MyOption.DATA.getOptName()) {
                this.body = optionValue;
                return this;
            }
            throw new RuntimeException("선택할 수 없는 Option입니다");
        }

        private void addHeader(Header header) {
            headers.put(header.getKey(), header.getValue());
        }
    }
}
