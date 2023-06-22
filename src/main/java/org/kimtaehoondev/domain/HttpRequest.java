package org.kimtaehoondev.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class HttpRequest {
    private final Url url;

    private final HttpMethod httpMethod;

    private final String httpVersion;

    private final Map<HeaderName, String> headers;

    private final String body;

    protected HttpRequest(Url url, HttpMethod httpMethod, String httpVersion,
                       Map<HeaderName, String> headers, String body) {
        this.url = url;
        this.httpMethod = HttpMethod.GET;
        this.httpVersion = "HTTP/1.1";
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder(String url) {
        return Builder.builder(url);
    }


    public String serialize() {
        String startLine = httpMethod + " " + url.getValue() + " " + httpVersion;
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(startLine);
        for (Map.Entry<HeaderName, String> entry : headers.entrySet()) {
            stringJoiner.add(entry.getKey().getValue() + ": " + entry.getValue());
        }
        if (body != null) {
            stringJoiner.add("");
            stringJoiner.add(body);
        }

        return stringJoiner.toString();
    }

    public enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE;

        public static HttpMethod find(String value) {
            return Arrays.stream(HttpMethod.values())
                .filter(each -> Objects.equals(each.name(), value.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("존재하지않는 HTTP REQUEST"));
        }
    }

    public static class HeaderName {
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

        private final Url url;
        private HttpMethod httpMethod;
        private String httpVersion;
        private final Map<HeaderName, String> headers;
        private String body;

        private Builder(String url) {
            this.url = new Url(url);
            this.httpMethod = HttpMethod.GET;
            this.httpVersion = "HTTP/1.1";
            this.headers = new HashMap<>();
        }

        public static Builder builder(String url) {
            return new Builder(url);
        }

        public HttpRequest build() {
            return new HttpRequest(url, httpMethod, httpVersion, headers, body);
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
