package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {
    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new HttpServer(8080).start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(() -> handleClient(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try (socket;
             BufferedReader input = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter output = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            System.out.println("Client connected: " + Thread.currentThread().getName());

            HttpRequest request = HttpRequest.readFrom(input);
            request.print();

            HttpResponse response = new HttpResponse(output);

            if ("GET".equals(request.getMethod()) && "/".equals(request.getPath())) {
                response.send200("<h1>Главная страница</h1>");
            } else if ("GET".equals(request.getMethod()) && "/api/hello".equals(request.getPath())) {
                response.send200Json("{\"message\":\"Hello\"}");
            } else if ("POST".equals(request.getMethod()) && "/api/echo".equals(request.getPath())) {
                response.send200Json(request.getBody());
            } else {
                response.send404();
            }

            System.out.println("Client disconnected: " + Thread.currentThread().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}