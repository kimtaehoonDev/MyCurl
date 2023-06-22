package org.kimtaehoondev.domain;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import org.kimtaehoondev.utils.UrlParser;

public class HttpRequest {
    private final RequestTarget requestTarget;

    private final HttpMethod httpMethod;

    private final String httpVersion;

    private final Map<HeaderName, String> headers;

    private final String body;

    protected HttpRequest(RequestTarget requestTarget, HttpMethod httpMethod, String httpVersion,
                          Map<HeaderName, String> headers, String body) {
        this.requestTarget = requestTarget;
        this.httpMethod = httpMethod;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder(String url) {
        return Builder.builder(url);
    }


    public String serialize() {
        String startLine = httpMethod + " " + requestTarget.getValue() + " " + httpVersion;
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(startLine);
        for (Map.Entry<HeaderName, String> entry : headers.entrySet()) {
            stringJoiner.add(entry.getKey().getValue() + ": " + entry.getValue());
        }
        stringJoiner.add("");

        if (body != null) {
            stringJoiner.add(body);
        }

        return stringJoiner.toString();
    }

    public String getHost() {
        return headers.get(HeaderName.HOST);
    }

    public int getPort() {
        // TODO 추후 변경하기. HTTP 메세지 내에서 어디에 PORT 위치하는지 모름
        return 80;
    }

    public static class HeaderName {
        public static final HeaderName HOST = new HeaderName("host");
        private final String value;

        public HeaderName(String value) {
            // TODO 타입 적절한지 검사한다
            this.value = value.toLowerCase().trim();
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            HeaderName that = (HeaderName) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class Builder {
        public static final String SPLITTER = ":";
        public static final int NAME = 0;
        public static final int VALUE = 1;

        private final RequestTarget requestTarget;
        private HttpMethod httpMethod;
        private String httpVersion;
        private final Map<HeaderName, String> headers;
        private String body;

        private Builder(String urlValue) {
            URL url = UrlParser.parse(urlValue);
            String path = url.getPath();
            if (path.isBlank()) {
                path = "/";
            }
            this.requestTarget = new RequestTarget(path);
            this.httpMethod = HttpMethod.GET;
            this.httpVersion = "HTTP/1.1";
            this.headers = new HashMap<>();
            this.headers.put(HeaderName.HOST, url.getHost());

        }

        public static Builder builder(String url) {
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

        public Builder setValueUsingParams(MyOption option, String optionValue) {
            if (option == MyOption.HTTP_REQUEST_METHOD) {
                this.httpMethod = HttpMethod.find(optionValue);
                return this;
            }
            if (option == MyOption.HEADER) {
                addHeader(optionValue);
                return this;
            }
            if (option == MyOption.DATA) {
                this.body = optionValue;
                return this;
            }
            throw new RuntimeException("선택할 수 없는 Option입니다");
        }

        private void addHeader(String value) {
            String[] nameAndValue = value.split(SPLITTER);
            if (nameAndValue.length != 2) {
                throw new RuntimeException("헤더와 값으로만 이뤄져야 한다");
            }
            HeaderName headerName = new HeaderName(nameAndValue[NAME].trim());
            String headerValue = nameAndValue[VALUE].trim();
            if (headers.containsKey(headerName)) {
                throw new RuntimeException("이미 존재하는 헤더");
            }
            headers.put(headerName, headerValue);
        }
    }
}
