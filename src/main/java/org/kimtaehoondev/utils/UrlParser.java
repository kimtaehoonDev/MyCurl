package org.kimtaehoondev.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlParser {
    public static URL parse(String url) {
        try {
            if (!(url.startsWith("http://") || (url.startsWith("https://")))) {
                url = "http://" + url;
            }
            URL temp = new URL(url);
            if (temp.getPort() != -1) {
                return temp;
            }
            if (temp.getProtocol().equals("http")) {
                return new URL(temp.getProtocol(), temp.getHost(), 80, temp.getFile());
            }
            if (temp.getProtocol().equals("https")) {
                return new URL(temp.getProtocol(), temp.getHost(), 443, temp.getFile());
            }
            throw new RuntimeException("지원하지 않는 프로토콜입니다");
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL을 파싱할 수 없습니다");
        }
    }
}
