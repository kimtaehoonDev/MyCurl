package org.kimtaehoondev;

import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.utils.ArgsParser;

public class HttpRequestFactory {
    public HttpRequest make(String[] args) {
        String url = args[args.length - 1];
        HttpRequest.Builder httpRequestBuilder = HttpRequest.builder(url);

        String[] argsExceptUrl = Arrays.copyOfRange(args, 0, args.length - 1);
        CommandLine commandLine = ArgsParser.makeCmdUsingArgs(argsExceptUrl);

        for(Option option : commandLine.getOptions()) {
            httpRequestBuilder.setValueUsingParams(option);
        }
        return httpRequestBuilder.build();
    }

}
