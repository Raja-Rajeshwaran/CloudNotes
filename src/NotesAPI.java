import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class NotesAPI {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // ✅ Root context: /
        // ✅ Root context: /
server.createContext("/", (exchange -> {
    String response = """
👋 Welcome to CloudNotes API!

📌 This is a lightweight cloud-based Java API deployed on Render.

✨ Available Endpoints:
- GET  /notes         → View all saved notes
- POST /notes         → Add a new note (send raw text in request body)

💡 Example Usage:
curl -X POST https://cloudnotes-odw0.onrender.com/notes -d "Study DevOps daily"
curl https://cloudnotes-odw0.onrender.com/notes

🚀 Git Commands Guide for Beginners:

🧰 Initialize a local repo:
    git init

📂 Add files to staging:
    git add .

💬 Commit your changes:
    git commit -m "Initial commit"

🔗 Connect to remote GitHub repo:
    git remote add origin https://github.com/<your-username>/<your-repo>.git

⬆️ Push your code:
    git push -u origin main

🔄 If remote already exists, update it:
    git remote set-url origin https://github.com/<your-username>/<your-repo>.git

🛠 To check remote info:
    git remote -v

🌐 Deployed via: Render (https://render.com)
📣 Built with: Java + Minimal HTTP Server (com.sun.net.httpserver)

🔐 PORT environment variable is auto-detected or defaults to 8080.
    """;

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
