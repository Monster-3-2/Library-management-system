package library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered Library Member.
 *
 * Stores personal details and tracks currently borrowed book IDs.
 * Supports file serialisation for persistent storage.
 *
 * @author  Student
 * @version 2.0
 */
public class Member {

    private String memberId;
    private String name;
    private String email;
    private String phone;
    private List<String> borrowedBookIds;

    public Member(String memberId, String name, String email, String phone) {
        this.memberId       = memberId;
        this.name           = name;
        this.email          = email;
        this.phone          = phone;
        this.borrowedBookIds = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getMemberId()               { return memberId; }
    public String getName()                   { return name; }
    public String getEmail()                  { return email; }
    public String getPhone()                  { return phone; }
    public List<String> getBorrowedBookIds()  { return borrowedBookIds; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    // ── Borrow helpers ────────────────────────────────────────────────────────

    public void borrowBook(String bookId)  { borrowedBookIds.add(bookId); }
    public void returnBook(String bookId)  { borrowedBookIds.remove(bookId); }
    public boolean hasBorrowed(String id)  { return borrowedBookIds.contains(id); }

    // ── Serialisation ─────────────────────────────────────────────────────────

    /**
     * Format: memberId|name|email|phone|bookId1,bookId2,...
     */
    public String toFileString() {
        String books = borrowedBookIds.isEmpty() ? "none" : String.join(",", borrowedBookIds);
        return String.join("|", memberId, name, email, phone, books);
    }

    public static Member fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        Member m = new Member(p[0], p[1], p[2], p[3]);
        if (!p[4].equals("none")) {
            for (String id : p[4].split(",")) m.borrowBook(id);
        }
        return m;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Borrowed: %d book(s)",
            memberId, name, email, phone, borrowedBookIds.size());
    }
}
