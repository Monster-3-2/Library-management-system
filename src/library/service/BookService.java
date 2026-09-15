package library.service;

import library.model.Book;
import library.storage.FileStorage;
import library.util.IdGenerator;
import library.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BookService — Module 1
 *
 * Handles the complete lifecycle of books in the library:
 * adding, updating, removing, searching, reserving, and availability management.
 *
 * All changes are automatically persisted to books.txt via FileStorage.
 *
 * @author  Student
 * @version 2.0
 */
public class BookService {

    // Primary data store: bookId → Book
    private Map<String, Book> catalogue;

    public BookService() {
        this.catalogue = new HashMap<>();
        // Load persisted data from file on startup
        FileStorage.loadBooks().forEach(b -> catalogue.put(b.getBookId(), b));

        // If this is a fresh run with no saved data, load sample books
        if (catalogue.isEmpty()) {
            Logger.info("No saved books found — loading sample catalogue.");
            loadSampleBooks();
        } else {
            Logger.info("Loaded " + catalogue.size() + " books from storage.");
        }
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public Book addBook(String title, String author, String genre, int year) {
        String id = IdGenerator.generateBookId();
        Book book = new Book(id, title, author, genre, year);
        catalogue.put(id, book);
        persist();
        Logger.info("Book added: " + title);
        System.out.println("✔ Book added → " + book);
        return book;
    }

    public boolean removeBook(String bookId) {
        Book book = catalogue.get(bookId);
        if (book == null) {
            System.out.println("No book found with ID: " + bookId);
            return false;
        }
        if (!book.isAvailable()) {
            System.out.println("Cannot remove \"" + book.getTitle() + "\" — it is currently issued.");
            return false;
        }
        catalogue.remove(bookId);
        persist();
        System.out.println("✔ Book removed: " + book.getTitle());
        return true;
    }

    public boolean updateBook(String bookId, String title, String author, String genre) {
        Book book = catalogue.get(bookId);
        if (book == null) { System.out.println("Book not found: " + bookId); return false; }
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        persist();
        System.out.println("✔ Book updated: " + book.getTitle());
        return true;
    }

    // ── Search & Retrieval ────────────────────────────────────────────────────

    public Book getBookById(String id) { return catalogue.get(id); }

    public List<Book> searchByTitle(String kw) {
        return filter(b -> b.getTitle().toLowerCase().contains(kw.toLowerCase()));
    }

    public List<Book> searchByAuthor(String kw) {
        return filter(b -> b.getAuthor().toLowerCase().contains(kw.toLowerCase()));
    }

    public List<Book> searchByGenre(String kw) {
        return filter(b -> b.getGenre().equalsIgnoreCase(kw));
    }

    public List<Book> getAllBooks()       { return new ArrayList<>(catalogue.values()); }
    public List<Book> getAvailableBooks() { return filter(Book::isAvailable); }
    public List<Book> getIssuedBooks()    { return filter(b -> !b.isAvailable()); }
    public List<Book> getReservedBooks()  { return filter(Book::isReserved); }

    // ── Reservation System ────────────────────────────────────────────────────

    /**
     * Reserves an issued book for a member.
     * When that book is returned, the reserving member gets first priority.
     */
    public boolean reserveBook(String bookId, String memberId) {
        Book book = catalogue.get(bookId);
        if (book == null) {
            System.out.println("Book not found: " + bookId);
            return false;
        }
        if (book.isAvailable()) {
            System.out.println("\"" + book.getTitle() + "\" is already available — no need to reserve. Just borrow it!");
            return false;
        }
        if (book.isReserved()) {
            System.out.println("\"" + book.getTitle() + "\" is already reserved by another member.");
            return false;
        }
        book.setReservedByMemberId(memberId);
        persist();
        System.out.println("✔ Reserved \"" + book.getTitle() + "\" for member " + memberId);
        return true;
    }

    public boolean cancelReservation(String bookId) {
        Book book = catalogue.get(bookId);
        if (book == null || !book.isReserved()) {
            System.out.println("No active reservation for book: " + bookId);
            return false;
        }
        book.setReservedByMemberId(null);
        persist();
        System.out.println("✔ Reservation cancelled for: " + book.getTitle());
        return true;
    }

    // ── Internal helpers used by BorrowService ────────────────────────────────

    public void markAsIssued(String bookId) {
        Book b = catalogue.get(bookId);
        if (b != null) { b.setAvailable(false); b.setReservedByMemberId(null); persist(); }
    }

    public void markAsAvailable(String bookId) {
        Book b = catalogue.get(bookId);
        if (b != null) { b.setAvailable(true); persist(); }
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    private void persist() { FileStorage.saveBooks(catalogue.values()); }

    private List<Book> filter(java.util.function.Predicate<Book> predicate) {
        return catalogue.values().stream().filter(predicate).collect(Collectors.toList());
    }

    // ── Sample Data ───────────────────────────────────────────────────────────

    private void loadSampleBooks() {
        addBook("The Pragmatic Programmer", "Andrew Hunt", "Technology", 1999);
        addBook("Clean Code", "Robert C. Martin", "Technology", 2008);
        addBook("To Kill a Mockingbird", "Harper Lee", "Fiction", 1960);
        addBook("1984", "George Orwell", "Dystopian", 1949);
        addBook("The Great Gatsby", "F. Scott Fitzgerald", "Classic", 1925);
        addBook("Introduction to Algorithms", "Cormen et al.", "Technology", 2009);
        addBook("Design Patterns", "Gang of Four", "Technology", 1994);
    }
}
