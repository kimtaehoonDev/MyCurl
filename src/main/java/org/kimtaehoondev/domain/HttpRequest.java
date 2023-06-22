package org.kimtaehoondev.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class HttpRequest {
    public static final String SPLITTER = ":";
    public static final int NAME = 0;
    public static final int VALUE = 1;

    private final String url;
    private HttpMethod httpMethod;
    private String httpVersion;

    private final Map<HeaderName, String> headers;

    private String body;

    public HttpRequest(String url) {
        this.url = url;
        this.httpMethod = HttpMethod.GET;
        this.httpVersion = "HTTP/1.1";
        this.headers = new HashMap<>();
    }

    public void addHeader(String value) {
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

    public void setBody(String body) {
        this.body = body;
    }

    public String serialize() {
        return null;
    }

    public enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE;
    }

    @EqualsAndHashCode
    @Getter
    public static class HeaderName {
        private final String value;
        public HeaderName(String value) {
            // TODO 타입 적절한지 검사한다
            this.value = value.toLowerCase().trim();
        }
    }
}
