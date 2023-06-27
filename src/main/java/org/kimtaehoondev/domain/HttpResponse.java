package org.kimtaehoondev.domain;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class HttpResponse {
    private final String httpVersion;
    private final Integer httpStatus;
    private final Headers headers;
    private String body;

    protected HttpResponse(String httpVersion, Integer httpStatus, Headers headers, String body) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder() {
        return Builder.builder();
    }

    public String getBody() {
        return body;
    }

    public String serialize() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(httpVersion + " " + httpStatus);

        List<String> total = headers.getAll().stream()
            .map(Header::getPrettier)
            .collect(Collectors.toList());
        for (String each : total) {
            stringJoiner.add(each);
        }

        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }

    public static class Builder {
        private String httpVersion;
        private Integer httpStatus;
        private boolean isChunked = false;
        private Integer contentLength;
        private String body;
        private final Headers headers;


        private Builder() {
            headers = new Headers();
        }

        public static Builder builder() {
            return new Builder();
        }

        public HttpResponse build() {
            if (httpVersion == null || httpStatus == null || headers == null) {
                throw new RuntimeException("초기화가 제대로 이뤄지지 않았습니다");
            }
            return new HttpResponse(httpVersion, httpStatus, headers, body);
        }

        public Builder setStartLine(String line) {
            String[] values = line.split(" ");
            httpVersion = values[0];
            httpStatus = Integer.parseInt(values[1]);
            return this;
        }

        public Builder setHeaders(List<Header> headers) {
            for (Header header : headers) {
                addHeader(header);
            }
            return this;
        }

        private void addHeader(Header header) {
            headers.put(header);

            if (header.isKeyEquals("Transfer-Encoding") && header.isValueEqual("chunked")) {
                isChunked = true;
            }
            if (header.isKeyEquals("Content-Length")) {
                contentLength = Integer.parseInt(header.getValue());
            }
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Integer getContentLength() {
            return contentLength;
        }

        public boolean isChunked() {
            return isChunked;
        }
    }
}
