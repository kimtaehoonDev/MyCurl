package org.kimtaehoondev.domain;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.commons.cli.Option;

public class HttpRequest {
    public static final String CRLF = "\r\n";
    private static final HttpMethod DEFAULT_HTTP_METHOD = HttpMethod.GET;
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_LINE = "";


    private String requestTarget;
    private HttpMethod httpMethod;
    private String httpVersion;
    private final Headers headers;
    private String body;

    public HttpRequest(URL url) {
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

    public void setValueUsingParams(Option option) {
        String optionValue = option.getValue();

        if (Objects.equals(option.getOpt(), MyOption.HTTP_REQUEST_METHOD.getOptName())) {
            this.httpMethod = HttpMethod.find(optionValue);
            return;
        }
        if (Objects.equals(option.getOpt(), MyOption.HEADER.getOptName())) {
            addHeader(new Header(optionValue));
            return;
        }
        if (Objects.equals(option.getOpt(), MyOption.DATA.getOptName())) {
            setBody(optionValue);
            return;
        }
        throw new RuntimeException("선택할 수 없는 Option입니다");
    }

    private void setBody(String optionValue) {
        StringJoiner sj = new StringJoiner(",");
        String[] params = optionValue.split("&");
        for (String param : params) {
            String[] keyAndValue = param.split("=");
            sj.add("\"" + keyAndValue[0] + "\"" + ":" + "\"" + keyAndValue[1] + "\"");
        }

        this.body = "{" + sj + "}";
        Header contentTypeHeader = new Header(new HeaderName("Content-Type"), "application/json");
        addHeader(contentTypeHeader);
        Header contentLengthHeader = new Header(new HeaderName("Content-Length"),
            String.valueOf(this.body.getBytes(StandardCharsets.UTF_8).length));
        addHeader(contentLengthHeader);
    }

    private void addHeader(Header header) {
        headers.put(header.getKey(), header.getValue());
    }

    /**
     * 해당 객체를 List<String>으로 직렬화합니다.
     * 서버로 Http 요청을 보내기 위해 사용합니다
     */
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
}
