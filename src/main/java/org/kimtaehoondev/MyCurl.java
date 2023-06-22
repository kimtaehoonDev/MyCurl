package org.kimtaehoondev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.StringJoiner;
import org.apache.commons.cli.CommandLine;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.domain.MyOption;
import org.kimtaehoondev.utils.ArgsParser;

public class MyCurl {
    private final HttpRequest request;
    private final BufferedReader console;
    private final BufferedWriter terminal;

    public MyCurl(String[] args) {
        this.request = makeHttpRequest(args);
        this.console = new BufferedReader(new InputStreamReader(System.in));
        this.terminal = new BufferedWriter(new OutputStreamWriter(System.out));
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
                writerToServer.write(line);
            }
            writerToServer.flush();

            // 한줄씩 응답을 출력한다
            String line;
            while ((line = readerFromServer.readLine()) != null) {
                System.out.println("line = " + line);
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //1. URL 연결
        // 2. 요청보내고, 응답받아옴
        // 3. 응답을 console에 출력
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

