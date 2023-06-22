package org.kimtaehoondev;

import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.kimtaehoondev.domain.HttpRequest;

public class MyCurl {
    private final HttpRequest request;

    public MyCurl(String[] args) {
        String url = args[args.length - 1];
        this.request = new HttpRequest(url);

        String[] argsExceptUrl = Arrays.copyOfRange(args, 0, args.length - 1);
        initHttpRequest(argsExceptUrl);
    }

    public void run() {
        System.out.println(this.request.serialize());
        // TODO requestHeader를 URL로 요청보낸다
    }

    public void initHttpRequest(String[] args) {
        CommandLine commandLine = ArgsParser.makeCmdUsingArgs(args);

        for (MyOption option : MyOption.values()) {
            String optName = option.getOptName();
            if (commandLine.hasOption(optName)) {
                this.request.setUsingParams(option, commandLine.getOptionValue(optName));
            }
        }
    }
}

