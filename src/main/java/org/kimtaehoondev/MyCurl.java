package org.kimtaehoondev;

import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.domain.MyOption;

public class MyCurl {
    private final HttpRequest request;

    public MyCurl(String[] args) {
        this.request = makeHttpRequest(args);
    }

    public void run() {
        System.out.println(this.request.serialize());
        // TODO requestHeader를 URL로 요청보낸다
    }

    public HttpRequest makeHttpRequest(String[] args) {
        String url = args[args.length - 1];
        HttpRequest.Builder httpRequestBuilder = HttpRequest.builder(url);

        String[] argsExceptUrl = Arrays.copyOfRange(args, 0, args.length - 1);
        CommandLine commandLine = ArgsParser.makeCmdUsingArgs(argsExceptUrl);

        for (MyOption option : MyOption.values()) {
            String optName = option.getOptName();
            if (commandLine.hasOption(optName)) {
                httpRequestBuilder.setValueUsingParams(option, commandLine.getOptionValue(optName));
            }
        }
        return httpRequestBuilder.build();
    }
}

