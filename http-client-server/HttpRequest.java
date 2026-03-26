package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String path, String version,
                       Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest readFrom(BufferedReader input) throws IOException {
        String requestLine = input.readLine();

        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            throw new IOException("Bad request line: " + requestLine);
        }

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = input.readLine()) != null && !line.isEmpty()) {
            int index = line.indexOf(':');
            if (index != -1) {
                String key = line.substring(0, index).trim();
                String value = line.substring(index + 1).trim();
                headers.put(key, value);
            }
        }

        String body = "";
        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader != null) {
            int contentLength = Integer.parseInt(contentLengthHeader);
            char[] buffer = new char[contentLength];
            int totalRead = 0;

            while (totalRead < contentLength) {
                int read = input.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) {
                    break;
                }
                totalRead += read;
            }

            body = new String(buffer, 0, totalRead);
        }

        return new HttpRequest(method, path, version, headers, body);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void print() {
        System.out.println("Method: " + method);
        System.out.println("Path: " + path);
        System.out.println("Version: " + version);
        System.out.println("Headers:");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Body: " + body);
    }
}