
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class NotesAPI {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/notes", (exchange -> {
            String method = exchange.getRequestMethod();
            if ("GET".equals(method)) {
                List<String> notes = NoteStore.getAllNotes();
                String response = String.join("\n", notes);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            } else if ("POST".equals(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes());
                NoteStore.addNote(body.trim());
                String response = "Note added!";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }));

        server.setExecutor(null);
        server.start();
        System.out.println("✅ Server running on port " + port);
    }
}
