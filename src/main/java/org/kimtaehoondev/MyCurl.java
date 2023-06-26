package org.kimtaehoondev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.utils.ArgsParser;

public class MyCurl {
    public static final String CRLF = "\r\n";

    private final HttpRequestFactory httpRequestFactory;
    private final BufferedReader console;

    public MyCurl() {
        httpRequestFactory = new HttpRequestFactory();
        this.console = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run(String[] args) {
        HttpRequest request = httpRequestFactory.make(args);

        try (Socket socket = new Socket(request.getHost(), request.getPort())) {
            BufferedReader readerFromServer =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writerToServer =
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 메세지를 한줄씩 서버로 보낸다
            List<String> lines = request.serialize();
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
}

