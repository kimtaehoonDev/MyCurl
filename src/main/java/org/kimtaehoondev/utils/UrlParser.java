package org.kimtaehoondev.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlParser {
    public static URL parse(String url) {
        try {
            if (!(url.startsWith("http://") || (url.startsWith("https://")))) {
                url = "http://" + url;
            }
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL을 파싱할 수 없습니다");
        }

    }
}
