package org.kimtaehoondev.domain;

import java.util.Objects;

public class HeaderName {
    public static final HeaderName HOST = new HeaderName("host");
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