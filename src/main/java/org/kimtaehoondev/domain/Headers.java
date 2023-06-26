package org.kimtaehoondev.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {
    private final Map<HeaderName, List<Header>> store = new HashMap<>();

    public void put(HeaderName key, String value) {
        if (!store.containsKey(key)) {
            List<Header> headers = new ArrayList<>();
            store.put(key, headers);
        }
        List<Header> headers = store.get(key);
        headers.add(new Header(key, value));
    }

    public boolean containsKey(HeaderName key) {
        return store.containsKey(key);
    }

    public List<Header> getAll() {
        List<Header> result = new ArrayList<>();
        for (List<Header> header : store.values()) {
            result.addAll(header);
        }
        return result;
    }

    public List<Header> get(HeaderName key) {
        return store.get(key);
    }
}
