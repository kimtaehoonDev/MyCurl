package org.kimtaehoondev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.domain.MyOption;
import org.kimtaehoondev.utils.ArgsParser;

// TODO -H 여러개 붙이면 하나만 먹는 이슈 해결하기
public class MyCurl {
    public static final String CRLF = "\r\n";

    private final HttpRequest request;
    private final BufferedReader console;

    public MyCurl(String[] args) {
        this.request = makeHttpRequest(args);
        this.console = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() {
        try (Socket socket = new Socket(request.getHost(), request.getPort())) {
            BufferedReader readerFromServer =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writerToServer =
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 메세지를 한줄씩 서버로 보낸다
            String[] lines = request.serialize().split("\n");
            for (String line : lines) {
                System.out.println("request |" + line);
                writerToServer.write(line+CRLF);
            }
            writerToServer.write(CRLF);
            writerToServer.flush();

            // 한줄씩 응답을 출력한다
            String line;
            while ((line = readerFromServer.readLine()) != null) {
                System.out.println("response |" + line);
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

