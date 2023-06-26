package org.kimtaehoondev.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Header {
    public static final int KEY = 0;
    public static final int VALUE = 1;
    private final String key;
    private final String value;

    public Header(String data) {
        List<String> keyAndValue = Arrays.stream(data.split(":"))
            .map(String::trim)
            .collect(Collectors.toList());
        // TODO 크기
        this.key = keyAndValue.get(KEY);
        this.value = keyAndValue.get(VALUE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Header header = (Header) o;
        return Objects.equals(key, header.key) &&
            Objects.equals(value, header.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    public boolean isKeyEquals(String key) {
        return this.key.equals(key);
    }

    public String getValue() {
        return value;
    }

    public boolean isValueEqual(String value) {
        return this.value.equals(value);
    }

    public String get() {
        return this.key + ": " + this.value;
    }
}
