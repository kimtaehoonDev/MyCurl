package org.kimtaehoondev;

import org.apache.commons.cli.Option;

public enum MyOption {
    HTTP_REQUEST_METHOD("X", "request",
        "HTTP 메서드를 지정하기 위해 사용합니다. 기본값은 GET입니다.",
        true, "command"),
    HEADER("H", "header", "헤더를 추가합니다", true, "line"),
    DATA("d", "data", "데이터를 넣습니다", true, "data");

    private final String optName;
    private final Option option;

    MyOption(String opt, String longOpt, String description, boolean hasArgs, String argName) {
        this.optName = opt;

        Option.Builder builder = Option.builder()
            .option(opt)
            .longOpt(longOpt)
            .desc(description)
            .hasArg(hasArgs);
        if (argName != null) {
            builder.argName(argName);
        }
        this.option = builder.build();
    }

    public Option getOption() {
        return option;
    }

    public String getOptName() {
        return optName;
    }
}
