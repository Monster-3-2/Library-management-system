package library;

import library.storage.FileStorage;
import library.ui.ConsoleMenu;

/**
 * Main — Entry point for the Library Book Management System v2.0
 *
 * Initialises persistent storage, then launches the console menu.
 * The menu requires admin login before any operation is accessible.
 *
 * Compile:
 *   find src -name "*.java" | xargs javac -d out
 *
 * Run:
 *   java -cp out library.Main
 *
 * @author  Student
 * @subject Programming in Java (VITyarthi)
 * @version 2.0
 */
public class Main {

    public static void main(String[] args) {
        // Step 1: Ensure the data/ directory and .txt files exist
        FileStorage.initialise();

        // Step 2: Launch the interactive console menu
        ConsoleMenu menu = new ConsoleMenu();
        menu.start();
    }
}
