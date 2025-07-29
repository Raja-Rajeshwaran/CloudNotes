
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class NotesAPI {

    @SuppressWarnings("ConvertToStringSwitch")
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", (exchange -> {
            String response = """
-> Hey there! Welcome to **CloudNotes API** — your lightweight, cloud-ready backend for taking quick notes using simple HTTP requests.

-> This is a mini RESTful API built in raw Java using `HttpServer`. Perfect for beginners, cloud learners, or anyone who vibes with simple, no-framework solutions!

-> API Endpoints:
--------------------------------------
GET  /              → View this welcome message  
GET  /notes         → Get all stored notes  
POST /notes         → Add a new note (plain text body)

-> Example curl test:
curl -X POST http://localhost:8080/notes -d "Learn Git deeply!"
curl http://localhost:8080/notes

-> Built-in Memory Store: Notes are stored in memory only. Restarting the server clears all notes.

--------------------------------------
-> Git Commands Cheat Sheet:
--------------------------------------
🔹 git init                  → Initialize a new local repo  
🔹 git clone <url>           → Clone an existing repo  
🔹 git add .                 → Stage all files for commit  
🔹 git commit -m "message"   → Commit changes with message  
🔹 git status                → See current file status  
🔹 git log                   → View commit history  
🔹 git branch                → List or create branches  
🔹 git checkout <branch>     → Switch to a branch  
🔹 git merge <branch>        → Merge another branch into current  
🔹 git push origin main      → Push your code to GitHub  
🔹 git pull                  → Fetch + merge remote updates  

-> Windows CRLF Warning:
When you see:  
  `LF will be replaced by CRLF`  
It's just Git telling you that your system is converting Unix-style line endings (`LF`) to Windows-style (`CRLF`).  
You can control this behavior with:
- `git config --global core.autocrlf true` → convert LF to CRLF on checkout
- `git config --global core.autocrlf input` → preserve LF
- `git config --global core.autocrlf false` → disable conversions

--------------------------------------
-> About the Developer — Vishal:
--------------------------------------
-> Hey! I'm "Vishal", a Java developer, cloud computing learner, and passionate full-stack builder.

-> Projects I love building:
- ERP systems from scratch (like IntegraOne)
- Clean, minimal UI dashboards
- Backend APIs with Java + DevOps tooling

-> Tech Skills:
Java · Swing · JDBC · Servlets · MySQL · Git · CI/CD · Docker · Basic AWS & DevOps

-> I often dive into platforms like LeetCode, HackerRank, and explore building solutions that are *simple yet scalable*.

-> Fun fact: I'm a total cinema nerd. From old classics to new-gen thrillers — always down to discuss scripts and screenwriting.

-> Let's connect!
GitHub: github.com/yourusername  
LinkedIn: linkedin.com/in/yourprofile  
Portfolio: yourwebsite.dev

-> Thanks for checking this out. Happy coding!
""";

            exchange.getResponseHeaders().add("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }));
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
                    String response = "Empty note can't be added.";
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    NoteStore.addNote(body);
                    String response = "Note added!";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }

            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }));

        server.setExecutor(null);
        server.start();
        System.out.println("Server running on port " + port);
    }
}
