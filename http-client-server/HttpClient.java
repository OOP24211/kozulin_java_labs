package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter output = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader input = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            String body = "{\"name\":\"Ivan\"}";

            output.print("POST /api/echo HTTP/1.1\r\n");
            output.print("Host: localhost\r\n");
            output.print("Content-Type: application/json\r\n");
            output.print("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n");
            output.print("\r\n");
            output.print(body);
            output.flush();

            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}