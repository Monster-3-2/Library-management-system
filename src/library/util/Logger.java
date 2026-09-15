package library.util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger — writes timestamped log entries to both the console and a log file.
 *
 * Log file: data/app.log
 * Levels: INFO, WARN, ERROR
 *
 * Writing logs to a file satisfies the "Logging & Monitoring"
 * non-functional requirement.
 *
 * @author  Student
 * @version 2.0
 */
public class Logger {

    private static final String LOG_FILE = "data/app.log";
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void info(String msg)  { log("INFO ", msg); }
    public static void warn(String msg)  { log("WARN ", msg); }
    public static void error(String msg) { log("ERROR", msg); System.err.println("[ERROR] " + msg); }

    private static void log(String level, String msg) {
        String entry = "[" + level + "] " + now() + " — " + msg;
        System.out.println(entry);
        writeToFile(entry);
    }

    private static void writeToFile(String entry) {
        try {
            // Ensure data dir exists before writing
            Files.createDirectories(Paths.get("data"));
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
                bw.write(entry);
                bw.newLine();
            }
        } catch (IOException ignored) {
            // Don't crash the app if logging fails
        }
    }

    private static String now() { return LocalDateTime.now().format(FMT); }
}
