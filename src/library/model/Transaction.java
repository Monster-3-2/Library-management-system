package library.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Records a single borrow or return event in the library.
 *
 * For BORROW transactions a due date is set 14 days from today.
 * Fine calculation: Rs. 2 per day overdue.
 *
 * @author  Student
 * @version 2.0
 */
public class Transaction {

    public enum Type { BORROW, RETURN }

    // Fine rate — Rs. 2 per overdue day
    public static final double FINE_PER_DAY = 2.0;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private String    transactionId;
    private String    memberId;
    private String    bookId;
    private Type      type;
    private LocalDate date;
    private LocalDate dueDate;

    public Transaction(String transactionId, String memberId, String bookId, Type type) {
        this.transactionId = transactionId;
        this.memberId      = memberId;
        this.bookId        = bookId;
        this.type          = type;
        this.date          = LocalDate.now();
        this.dueDate       = (type == Type.BORROW) ? date.plusDays(14) : null;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String    getTransactionId() { return transactionId; }
    public String    getMemberId()      { return memberId; }
    public String    getBookId()        { return bookId; }
    public Type      getType()          { return type; }
    public LocalDate getDate()          { return date; }
    public LocalDate getDueDate()       { return dueDate; }

    // ── Overdue & Fine Logic ──────────────────────────────────────────────────

    public boolean isOverdue() {
        return type == Type.BORROW && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Calculates the overdue fine in rupees.
     * Returns 0.0 if not overdue or not a borrow transaction.
     */
    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return daysLate * FINE_PER_DAY;
    }

    // ── Serialisation ─────────────────────────────────────────────────────────

    /**
     * Format: txnId|memberId|bookId|type|date|dueDate
     */
    public String toFileString() {
        return String.join("|",
            transactionId, memberId, bookId, type.name(),
            date.format(FMT),
            dueDate != null ? dueDate.format(FMT) : "null"
        );
    }

    public static Transaction fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        Transaction t = new Transaction(p[0], p[1], p[2], Type.valueOf(p[3]));
        t.date    = LocalDate.parse(p[4], FMT);
        t.dueDate = "null".equals(p[5]) ? null : LocalDate.parse(p[5], FMT);
        return t;
    }

    @Override
    public String toString() {
        String due      = dueDate != null ? " | Due: " + dueDate.format(FMT) : "";
        String overdue  = isOverdue() ? String.format(" *** OVERDUE — Fine: Rs. %.2f ***", calculateFine()) : "";
        return String.format("[%s] %s | Member: %s | Book: %s | Date: %s%s%s",
            transactionId, type, memberId, bookId, date.format(FMT), due, overdue);
    }
}
