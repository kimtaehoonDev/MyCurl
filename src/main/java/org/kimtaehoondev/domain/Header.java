package org.kimtaehoondev.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Header {
    private static final int KEY_VALUE_SIZE = 2;
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final String DELIMITER = ":";

    private final String key;
    private final String value;

    public Header(String data) {
        List<String> keyAndValue = Arrays.stream(data.split(DELIMITER, KEY_VALUE_SIZE))
            .map(String::trim)
            .collect(Collectors.toList());
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
