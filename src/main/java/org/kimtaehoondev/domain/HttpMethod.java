package org.kimtaehoondev.domain;

import java.util.Arrays;
import java.util.Objects;

public enum HttpMethod {
    GET, POST, PUT, PATCH, DELETE;

    public static HttpMethod find(String value) {
        return Arrays.stream(HttpMethod.values())
            .filter(each -> Objects.equals(each.name(), value.toUpperCase()))
            .findAny()
            .orElseThrow(() -> new RuntimeException("존재하지않는 HTTP REQUEST"));
    }
}