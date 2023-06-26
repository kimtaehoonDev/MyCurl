package org.kimtaehoondev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringJoiner;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.domain.HttpResponse;

public class MyCurl {
    public static final String CRLF = "\r\n";

    private final HttpRequestFactory httpRequestFactory;

    public MyCurl() {
        httpRequestFactory = new HttpRequestFactory();
    }

    public void run(String[] args) {
        HttpRequest request = httpRequestFactory.make(args);

        try (Socket socket = new Socket(request.getHost(), request.getPort())) {
            BufferedReader readerFromServer =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writerToServer =
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            sendRequestToServer(request, writerToServer);
            receiveResponseFromServer(readerFromServer);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 메세지를 한줄씩 서버로 보낸다.
     * 각 줄의 끝에는 CRLF가 붙어야 한다.
     */
    private void sendRequestToServer(HttpRequest request, BufferedWriter writerToServer)
        throws IOException {
        List<String> lines = request.serialize();
        for (String line : lines) {
            System.out.println("request |" + line);
            writerToServer.write(line + CRLF);
        }
        writerToServer.flush();
    }

    /**
     * 서버에서 응답을 받아온다
     */
    private HttpResponse receiveResponseFromServer(BufferedReader readerFromServer) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        String line;
        line = readerFromServer.readLine();
        httpResponse.setStartLine(line);

        while ((line = readerFromServer.readLine()) != null && !line.isEmpty()) {
            // 헤더를 만든다
            System.out.println("resp HEADER]" + line);
            httpResponse.addHeader(line);
        }

        // Content-Length타입일 때
        if (httpResponse.isChunked()) {
            StringJoiner stringJoiner = new StringJoiner("\n");
            Integer chunkedSize;
            while (true) {
                chunkedSize = Integer.parseInt(readerFromServer.readLine(), 16); // 16진수
                line = readerFromServer.readLine();
                if (chunkedSize == 0) {
                    break;
                }
                stringJoiner.add(line);
            }
            httpResponse.setBody(stringJoiner.toString());
            return httpResponse;
        }

        int totalLength = 0;
        StringJoiner stringJoiner = new StringJoiner("\n");
        while (httpResponse.getContentLength() != totalLength
            && (line = readerFromServer.readLine()) != null) {
            totalLength += ("\n".length() + line.getBytes(StandardCharsets.UTF_8).length);
            stringJoiner.add(line);
        }
        httpResponse.setBody(stringJoiner.toString());
        return httpResponse;
    }

}

