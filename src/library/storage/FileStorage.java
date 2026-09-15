package library.storage;

import library.model.Book;
import library.model.Member;
import library.model.Transaction;
import library.util.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileStorage handles reading and writing all data to flat files (.txt).
 *
 * This gives the system persistence across sessions — data survives
 * after the program is closed and restarted.
 *
 * Files stored in the /data directory:
 *   books.txt       — one book per line (pipe-delimited)
 *   members.txt     — one member per line (pipe-delimited)
 *   transactions.txt — one transaction per line (pipe-delimited)
 *
 * @author  Student
 * @version 2.0
 */
public class FileStorage {

    private static final String DATA_DIR        = "data/";
    private static final String BOOKS_FILE      = DATA_DIR + "books.txt";
    private static final String MEMBERS_FILE    = DATA_DIR + "members.txt";
    private static final String TXNS_FILE       = DATA_DIR + "transactions.txt";

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Creates the data directory and empty files if they don't exist yet.
     */
    public static void initialise() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            ensureFile(BOOKS_FILE);
            ensureFile(MEMBERS_FILE);
            ensureFile(TXNS_FILE);
            Logger.info("Data directory ready.");
        } catch (IOException e) {
            Logger.error("Could not initialise data directory: " + e.getMessage());
        }
    }

    // ── Books ─────────────────────────────────────────────────────────────────

    public static void saveBooks(Collection<Book> books) {
        writeLines(BOOKS_FILE, books.stream().map(Book::toFileString).toList());
    }

    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        for (String line : readLines(BOOKS_FILE)) {
            try { books.add(Book.fromFileString(line)); }
            catch (Exception e) { Logger.warn("Skipping corrupt book record: " + line); }
        }
        return books;
    }

    // ── Members ───────────────────────────────────────────────────────────────

    public static void saveMembers(Collection<Member> members) {
        writeLines(MEMBERS_FILE, members.stream().map(Member::toFileString).toList());
    }

    public static List<Member> loadMembers() {
        List<Member> members = new ArrayList<>();
        for (String line : readLines(MEMBERS_FILE)) {
            try { members.add(Member.fromFileString(line)); }
            catch (Exception e) { Logger.warn("Skipping corrupt member record: " + line); }
        }
        return members;
    }

    // ── Transactions ──────────────────────────────────────────────────────────

    public static void saveTransactions(List<Transaction> txns) {
        writeLines(TXNS_FILE, txns.stream().map(Transaction::toFileString).toList());
    }

    public static List<Transaction> loadTransactions() {
        List<Transaction> txns = new ArrayList<>();
        for (String line : readLines(TXNS_FILE)) {
            try { txns.add(Transaction.fromFileString(line)); }
            catch (Exception e) { Logger.warn("Skipping corrupt transaction record: " + line); }
        }
        return txns;
    }

    // ── Internal Helpers ──────────────────────────────────────────────────────

    private static void writeLines(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            Logger.error("Failed to write to " + filePath + ": " + e.getMessage());
        }
    }

    private static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) lines.add(line.trim());
            }
        } catch (IOException e) {
            Logger.warn("Could not read " + filePath + " — starting fresh.");
        }
        return lines;
    }

    private static void ensureFile(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.exists(p)) Files.createFile(p);
    }
}
