package org.kimtaehoondev.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HttpResponse {
    private String httpVersion;
    private Integer httpStatus;
    private boolean isChunked = false;
    private Integer contentLength;
    private String body;

    private final List<Header> headers = new ArrayList<>();

    public void setStartLine(String line) {
        String[] values = line.split(" ");
        httpVersion = values[0];
        httpStatus = Integer.parseInt(values[1]);
    }


    public Integer getContentLength() {
        return contentLength;
    }

    public boolean isChunked() {
        return isChunked;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setHeaders(List<Header> headers) {
        for (Header header : headers) {
            addHeader(header);
        }
    }

    private void addHeader(Header header) {
        headers.add(header);

        if (header.isKeyEquals("Transfer-Encoding") && header.isValueEqual("chunked")) {
            isChunked = true;
        }
        if (header.isKeyEquals("Content-Length")) {
            contentLength = Integer.parseInt(header.getValue());
        }
    }

    public String serialize() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(httpVersion + " " + httpStatus);
        for (Header header : headers) {
            stringJoiner.add(header.getPrettier());
        }
        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }
}
