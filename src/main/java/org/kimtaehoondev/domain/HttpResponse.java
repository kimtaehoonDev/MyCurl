package org.kimtaehoondev.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    private String httpVersion;
    private Integer httpStatus;
    private boolean isChunked = false;
    private Integer contentLength;

    private final Map<String, String> headers = new HashMap<>();

    public void setStartLine(String line) {
        String[] values = line.split(" ");
        httpVersion = values[0];
        httpStatus = Integer.parseInt(values[1]);
    }

    public void addHeader(String line) {
        List<String> values = Arrays.stream(line.split(":"))
            .map(String::trim)
            .collect(Collectors.toList());
        headers.put(values.get(0), values.get(1));

        if (values.get(0).equals("Content-Type") && values.get(1).equals("chunked")) {
            isChunked = true;
        }
        if (values.get(0).equals("Content-Length")) {
            contentLength = Integer.parseInt(values.get(1));
        }
    }

    public Integer getContentLength() {
        return contentLength;
    }
}
