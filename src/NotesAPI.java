import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class NotesAPI {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // ✅ Root context: /
        server.createContext("/", (exchange -> {
            String response = "👋 Welcome to CloudNotes API!\nUse /notes to GET or POST notes.";
            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }));

        // ✅ Notes context: /notes
        server.createContext("/notes", (exchange -> {
            String method = exchange.getRequestMethod();
            exchange.getResponseHeaders().add("Content-Type", "text/plain");

            if ("GET".equals(method)) {
                List<String> notes = NoteStore.getAllNotes();
                String response = String.join("\n", notes);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } else if ("POST".equals(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes()).trim();

                if (body.isEmpty()) {
                    String response = "❌ Empty note can't be added.";
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    NoteStore.addNote(body);
                    String response = "✅ Note added!";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }

            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }));

        server.setExecutor(null); // default executor
        server.start();
        System.out.println("🚀 Server running on port " + port);
    }
}
