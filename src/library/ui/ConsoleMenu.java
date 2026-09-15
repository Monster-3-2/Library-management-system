package library.ui;

import library.model.Book;
import library.model.Member;
import library.service.*;
import library.util.Logger;
import library.util.Validator;

import java.util.List;
import java.util.Scanner;

/**
 * ConsoleMenu — the user interface layer of the Library Management System.
 *
 * Requires admin login before granting access to any functionality.
 * Drives all four modules through a clean, nested menu structure.
 *
 * @author  Student
 * @version 2.0
 */
public class ConsoleMenu {

    private final AdminService   adminService;
    private final BookService    bookService;
    private final MemberService  memberService;
    private final BorrowService  borrowService;
    private final Scanner        sc;

    public ConsoleMenu() {
        this.adminService   = new AdminService();
        this.bookService    = new BookService();
        this.memberService  = new MemberService();
        this.borrowService  = new BorrowService(bookService, memberService);
        this.sc             = new Scanner(System.in);
    }

    // ── Entry Point ───────────────────────────────────────────────────────────

    public void start() {
        Logger.info("Application started.");
        printBanner();

        // Require login before proceeding
        if (!loginScreen()) {
            System.out.println("Too many failed attempts. Exiting.");
            return;
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            switch (input()) {
                case "1" -> bookMenu();
                case "2" -> memberMenu();
                case "3" -> borrowMenu();
                case "4" -> reservationMenu();
                case "5" -> fineMenu();
                case "6" -> reportsMenu();
                case "0" -> { adminService.logout(); Logger.info("Application exited."); running = false; }
                default  -> System.out.println("Invalid option. Please try again.");
            }
        }
        sc.close();
    }

    // ── Login Screen ──────────────────────────────────────────────────────────

    private boolean loginScreen() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           ADMIN LOGIN                ║");
        System.out.println("║  Default: admin / admin123           ║");
        System.out.println("╚══════════════════════════════════════╝");

        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username : "); String user = input();
            System.out.print("Password : "); String pass = input();
            if (adminService.login(user, pass)) return true;
            attempts++;
            System.out.println("Attempts remaining: " + (3 - attempts));
        }
        return false;
    }

    // ── Main Menu ─────────────────────────────────────────────────────────────

    private void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       LIBRARY MANAGEMENT SYSTEM      ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Book Management                  ║");
        System.out.println("║  2. Member Management                ║");
        System.out.println("║  3. Borrow / Return                  ║");
        System.out.println("║  4. Reservations                     ║");
        System.out.println("║  5. Fine Calculator                  ║");
        System.out.println("║  6. Reports & Analytics              ║");
        System.out.println("║  0. Logout & Exit                    ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Choice: ");
    }

    // ── Book Menu ─────────────────────────────────────────────────────────────

    private void bookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Book Management ──");
            System.out.println("1. Add Book      2. Remove Book    3. Update Book");
            System.out.println("4. Search Books  5. All Books      6. Available Books");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            switch (input()) {
                case "1" -> addBook();
                case "2" -> { System.out.print("Book ID: "); bookService.removeBook(input()); }
                case "3" -> updateBook();
                case "4" -> searchBooks();
                case "5" -> printList(bookService.getAllBooks(), "All Books");
                case "6" -> printList(bookService.getAvailableBooks(), "Available Books");
                case "0" -> back = true;
                default  -> System.out.println("Invalid option.");
            }
        }
    }

    private void addBook() {
        System.out.print("Title  : "); String title  = input();
        System.out.print("Author : "); String author = input();
        System.out.print("Genre  : "); String genre  = input();
        System.out.print("Year   : ");
        try {
            int year = Integer.parseInt(input());
            if (!Validator.isNonEmpty(title) || !Validator.isValidYear(year)) {
                System.out.println("✘ Invalid title or year."); return;
            }
            bookService.addBook(title, author, genre, year);
        } catch (NumberFormatException e) {
            System.out.println("✘ Year must be a number.");
        }
    }

    private void updateBook() {
        System.out.print("Book ID   : "); String id     = input();
        System.out.print("New Title : "); String title  = input();
        System.out.print("New Author: "); String author = input();
        System.out.print("New Genre : "); String genre  = input();
        bookService.updateBook(id, title, author, genre);
    }

    private void searchBooks() {
        System.out.println("Search by: 1) Title  2) Author  3) Genre");
        System.out.print("Choice: "); String opt = input();
        System.out.print("Keyword: "); String kw = input();
        List<Book> results = switch (opt) {
            case "1" -> bookService.searchByTitle(kw);
            case "2" -> bookService.searchByAuthor(kw);
            case "3" -> bookService.searchByGenre(kw);
            default  -> { System.out.println("Invalid."); yield List.of(); }
        };
        printList(results, "Search Results");
    }

    // ── Member Menu ───────────────────────────────────────────────────────────

    private void memberMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Member Management ──");
            System.out.println("1. Register   2. Remove   3. Update   4. Search   5. List All");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            switch (input()) {
                case "1" -> registerMember();
                case "2" -> { System.out.print("Member ID: "); memberService.removeMember(input()); }
                case "3" -> updateMember();
                case "4" -> { System.out.print("Name keyword: "); printList(memberService.searchByName(input()), "Results"); }
                case "5" -> printList(memberService.getAllMembers(), "All Members");
                case "0" -> back = true;
                default  -> System.out.println("Invalid option.");
            }
        }
    }

    private void registerMember() {
        System.out.print("Name  : "); String name  = input();
        System.out.print("Email : "); String email = input();
        System.out.print("Phone : "); String phone = input();
        memberService.registerMember(name, email, phone);
    }

    private void updateMember() {
        System.out.print("Member ID : "); String id    = input();
        System.out.print("New Name  : "); String name  = input();
        System.out.print("New Email : "); String email = input();
        System.out.print("New Phone : "); String phone = input();
        memberService.updateMember(id, name, email, phone);
    }

    // ── Borrow / Return Menu ──────────────────────────────────────────────────

    private void borrowMenu() {
        System.out.println("\n── Borrow / Return ──");
        System.out.println("1. Issue Book   2. Return Book   0. Back");
        System.out.print("Choice: ");
        switch (input()) {
            case "1" -> { System.out.print("Member ID: "); String m = input(); System.out.print("Book ID: "); borrowService.issueBook(m, input()); }
            case "2" -> { System.out.print("Member ID: "); String m = input(); System.out.print("Book ID: "); borrowService.returnBook(m, input()); }
            case "0" -> {}
            default  -> System.out.println("Invalid option.");
        }
    }

    // ── Reservation Menu ──────────────────────────────────────────────────────

    private void reservationMenu() {
        System.out.println("\n── Reservations ──");
        System.out.println("1. Reserve a Book   2. Cancel Reservation   3. View Reserved Books   0. Back");
        System.out.print("Choice: ");
        switch (input()) {
            case "1" -> {
                System.out.print("Member ID: "); String m = input();
                System.out.print("Book ID  : "); String b = input();
                bookService.reserveBook(b, m);
            }
            case "2" -> { System.out.print("Book ID: "); bookService.cancelReservation(input()); }
            case "3" -> printList(bookService.getReservedBooks(), "Reserved Books");
            case "0" -> {}
            default  -> System.out.println("Invalid option.");
        }
    }

    // ── Fine Menu ─────────────────────────────────────────────────────────────

    private void fineMenu() {
        System.out.println("\n── Fine Calculator ──");
        System.out.println("1. Check Fine for Member   2. View All Overdue   0. Back");
        System.out.print("Choice: ");
        switch (input()) {
            case "1" -> { System.out.print("Member ID: "); borrowService.showFineForMember(input()); }
            case "2" -> borrowService.printOverdueReport();
            case "0" -> {}
            default  -> System.out.println("Invalid option.");
        }
    }

    // ── Reports Menu ──────────────────────────────────────────────────────────

    private void reportsMenu() {
        System.out.println("\n── Reports & Analytics ──");
        System.out.println("1. Full Transaction History   2. Member History");
        System.out.println("3. Overdue Report             4. Library Summary");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        switch (input()) {
            case "1" -> borrowService.printFullHistory();
            case "2" -> { System.out.print("Member ID: "); borrowService.printMemberHistory(input()); }
            case "3" -> borrowService.printOverdueReport();
            case "4" -> borrowService.printLibrarySummary();
            case "0" -> {}
            default  -> System.out.println("Invalid option.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private <T> void printList(List<T> list, String title) {
        System.out.println("\n── " + title + " ──");
        if (list.isEmpty()) { System.out.println("  (none)"); return; }
        list.forEach(item -> System.out.println("  " + item));
        System.out.println("  Total: " + list.size());
    }

    private String input() { return sc.nextLine().trim(); }

    private void printBanner() {
        System.out.println("""
            ╔════════════════════════════════════════════════════╗
            ║       Library Book Management System v2.0          ║
            ║         Programming in Java — VITyarthi            ║
            ╚════════════════════════════════════════════════════╝
            """);
    }
}
