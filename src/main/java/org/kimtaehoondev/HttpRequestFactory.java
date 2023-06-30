package org.kimtaehoondev;

import java.net.URL;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.utils.ArgsParser;

public class HttpRequestFactory {
    public HttpRequest make(URL url, String[] args) {
        HttpRequest httpRequest = new HttpRequest(url);
        CommandLine commandLine = ArgsParser.makeCmdUsingArgs(args);
        for (Option option : commandLine.getOptions()) {
            httpRequest.setValueUsingParams(option);
        }
        return httpRequest;
    }

}
