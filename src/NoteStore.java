
import java.io.*;
import java.util.*;

public class NoteStore {
    private static final String FILE = "notes.txt";

    public static synchronized void addNote(String note) throws IOException {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(note + "\n");
        }
    }

    public static synchronized List<String> getAllNotes() throws IOException {
        List<String> notes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                notes.add(line);
            }
        }
        return notes;
    }
}
