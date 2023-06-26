package org.kimtaehoondev.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class HttpResponse {
    private String httpVersion;
    private Integer httpStatus;
    private boolean isChunked = false;
    private Integer contentLength;
    private String body;

    private final Map<String, String> headers = new HashMap<>();

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

    public void setHeaders(List<String> headers) {
        for (String header : headers) {
            addHeader(header);
        }
    }

    private void addHeader(String line) {
        List<String> values = Arrays.stream(line.split(":"))
            .map(String::trim)
            .collect(Collectors.toList());
        headers.put(values.get(0), values.get(1));

        if (values.get(0).equals("Transfer-Encoding") && values.get(1).equals("chunked")) {
            isChunked = true;
        }
        if (values.get(0).equals("Content-Length")) {
            contentLength = Integer.parseInt(values.get(1));
        }
    }

    public String serialize() {
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(httpVersion + " " + httpStatus);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringJoiner.add(entry.getKey() + ": " + entry.getValue());
        }
        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }
}
