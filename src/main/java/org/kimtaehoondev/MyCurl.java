package org.kimtaehoondev;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.kimtaehoondev.domain.Header;
import org.kimtaehoondev.domain.HttpRequest;
import org.kimtaehoondev.domain.HttpResponse;

public class MyCurl {
    public static final String CRLF = "\r\n";
    public static final int LINE_BREAK_LENGTH = 1;

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
            HttpResponse httpResponse = receiveResponseFromServer(readerFromServer);

            System.out.println(httpResponse.serialize());
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
            writerToServer.write(line + CRLF);
        }
        writerToServer.flush();
    }

    /**
     * 서버에서 응답을 받아온다.
     * 바디를 파싱하기 위한 정보를 얻기 위해 헤더를 따로 얻어낸다
     */
    private HttpResponse receiveResponseFromServer(BufferedReader readerFromServer) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        httpResponse.setStartLine(readerFromServer.readLine());

        List<Header> headers = receiveResponseHeaderFromServer(readerFromServer);
        httpResponse.setHeaders(headers);

        String body = receiveResponseBodyFromServer(httpResponse, readerFromServer);
        httpResponse.setBody(body);

        return httpResponse;
    }

    private List<Header> receiveResponseHeaderFromServer(BufferedReader readerFromServer)
        throws IOException {
        List<Header> headers = new ArrayList<>();
        String line;
        while ((line = readerFromServer.readLine()) != null && !line.isEmpty()) {
            headers.add(new Header(line));
        }
        return headers;
    }

    private String receiveResponseBodyFromServer(HttpResponse httpResponse,
                                                 BufferedReader readerFromServer) throws IOException {
        StringJoiner stringJoiner = new StringJoiner("\n");
        String line;

        if (httpResponse.isChunked()) {
            Integer chunkedSize;
            while (true) {
                chunkedSize = Integer.parseInt(readerFromServer.readLine(), 16); // 16진수
                line = readerFromServer.readLine();
                if (chunkedSize == 0) {
                    break;
                }
                stringJoiner.add(line);
            }
            return stringJoiner.toString();
        }

        int totalLength = 0;
        while (httpResponse.getContentLength() != totalLength
            && (line = readerFromServer.readLine()) != null) {
            totalLength += (LINE_BREAK_LENGTH + line.getBytes(StandardCharsets.UTF_8).length);
            stringJoiner.add(line);
        }
        return stringJoiner.toString();
    }

}

