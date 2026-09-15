package library.model;

/**
 * Represents a Book in the Library Management System.
 *
 * Each book has a unique ID, metadata, availability status,
 * and an optional reservation queue tracking which member reserved it.
 *
 * @author  Student
 * @version 2.0
 */
public class Book {

    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int yearPublished;
    private boolean isAvailable;
    private String reservedByMemberId; // null if not reserved

    public Book(String bookId, String title, String author, String genre, int yearPublished) {
        this.bookId           = bookId;
        this.title            = title;
        this.author           = author;
        this.genre            = genre;
        this.yearPublished    = yearPublished;
        this.isAvailable      = true;
        this.reservedByMemberId = null;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getBookId()           { return bookId; }
    public String getTitle()            { return title; }
    public String getAuthor()           { return author; }
    public String getGenre()            { return genre; }
    public int    getYearPublished()    { return yearPublished; }
    public boolean isAvailable()        { return isAvailable; }
    public String getReservedByMemberId() { return reservedByMemberId; }
    public boolean isReserved()         { return reservedByMemberId != null; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setAvailable(boolean available)           { this.isAvailable = available; }
    public void setTitle(String title)                    { this.title = title; }
    public void setAuthor(String author)                  { this.author = author; }
    public void setGenre(String genre)                    { this.genre = genre; }
    public void setReservedByMemberId(String memberId)    { this.reservedByMemberId = memberId; }

    // ── Serialisation (for file persistence) ─────────────────────────────────

    /**
     * Converts this book to a pipe-delimited string for file storage.
     * Format: bookId|title|author|genre|year|isAvailable|reservedByMemberId
     */
    public String toFileString() {
        return String.join("|",
            bookId, title, author, genre,
            String.valueOf(yearPublished),
            String.valueOf(isAvailable),
            reservedByMemberId == null ? "null" : reservedByMemberId
        );
    }

    /**
     * Reconstructs a Book from a pipe-delimited file line.
     */
    public static Book fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        Book b = new Book(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]));
        b.setAvailable(Boolean.parseBoolean(p[5]));
        b.setReservedByMemberId("null".equals(p[6]) ? null : p[6]);
        return b;
    }

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Issued";
        String reserved = isReserved() ? " [Reserved by: " + reservedByMemberId + "]" : "";
        return String.format("[%s] \"%s\" by %s | %s | %d | %s%s",
            bookId, title, author, genre, yearPublished, status, reserved);
    }
}
