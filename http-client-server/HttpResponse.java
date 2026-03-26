package org.example;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private final PrintWriter output;

    public HttpResponse(PrintWriter output) {
        this.output = output;
    }

    public void sendHtml(String status, String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        output.print("HTTP/1.1 " + status + "\r\n");
        output.print("Content-Type: text/html; charset=utf-8\r\n");
        output.print("Content-Length: " + bodyBytes.length + "\r\n");
        output.print("\r\n");
        output.print(body);
        output.flush();
    }

    public void sendJson(String status, String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        output.print("HTTP/1.1 " + status + "\r\n");
        output.print("Content-Type: application/json; charset=utf-8\r\n");
        output.print("Content-Length: " + bodyBytes.length + "\r\n");
        output.print("\r\n");
        output.print(body);
        output.flush();
    }

    public void send200(String body) {
        sendHtml("200 OK", body);
    }

    public void send200Json(String body) {
        sendJson("200 OK", body);
    }

    public void send404() {
        sendHtml("404 Not Found", "<h1>404 Not Found</h1>");
    }
}