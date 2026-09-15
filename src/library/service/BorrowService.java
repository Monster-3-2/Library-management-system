package library.service;

import library.model.Book;
import library.model.Member;
import library.model.Transaction;
import library.storage.FileStorage;
import library.util.IdGenerator;
import library.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BorrowService — Module 3
 *
 * Manages book issue, return, fine calculation, and transaction reporting.
 * Coordinates between BookService and MemberService.
 * Aware of the reservation system — reserved books go to the reserving member first.
 *
 * @author  Student
 * @version 2.0
 */
public class BorrowService {

    private static final int MAX_BORROW_LIMIT = 3;

    private BookService       bookService;
    private MemberService     memberService;
    private List<Transaction> transactionLog;

    public BorrowService(BookService bookService, MemberService memberService) {
        this.bookService    = bookService;
        this.memberService  = memberService;
        this.transactionLog = new ArrayList<>(FileStorage.loadTransactions());
        Logger.info("Loaded " + transactionLog.size() + " transaction records.");
    }

    // ── Issue a Book ──────────────────────────────────────────────────────────

    public boolean issueBook(String memberId, String bookId) {
        Member member = memberService.getMemberById(memberId);
        Book   book   = bookService.getBookById(bookId);

        if (member == null) { System.out.println("✘ Member not found: " + memberId); return false; }
        if (book == null)   { System.out.println("✘ Book not found: "   + bookId);   return false; }

        if (!book.isAvailable()) {
            // If this member is the one who reserved it, allow the issue
            if (book.isReserved() && book.getReservedByMemberId().equals(memberId)) {
                System.out.println("✔ Issuing reserved copy to you, " + member.getName() + ".");
            } else if (book.isReserved()) {
                System.out.println("✘ \"" + book.getTitle() + "\" is reserved for another member.");
                return false;
            } else {
                System.out.println("✘ \"" + book.getTitle() + "\" is currently issued to someone else.");
                return false;
            }
        }

        if (member.getBorrowedBookIds().size() >= MAX_BORROW_LIMIT) {
            System.out.println("✘ " + member.getName() + " has reached the borrow limit of " + MAX_BORROW_LIMIT + " books.");
            return false;
        }

        bookService.markAsIssued(bookId);
        member.borrowBook(bookId);

        Transaction txn = new Transaction(IdGenerator.generateTransactionId(), memberId, bookId, Transaction.Type.BORROW);
        transactionLog.add(txn);
        persist();

        System.out.println("✔ Issued: \"" + book.getTitle() + "\" → " + member.getName());
        System.out.println("  Due date: " + txn.getDueDate());
        Logger.info("Book issued: " + bookId + " to " + memberId);
        return true;
    }

    // ── Return a Book ─────────────────────────────────────────────────────────

    public boolean returnBook(String memberId, String bookId) {
        Member member = memberService.getMemberById(memberId);
        Book   book   = bookService.getBookById(bookId);

        if (member == null || book == null) {
            System.out.println("✘ Invalid member or book ID.");
            return false;
        }
        if (!member.hasBorrowed(bookId)) {
            System.out.println("✘ " + member.getName() + " has not borrowed \"" + book.getTitle() + "\".");
            return false;
        }

        // Calculate and display any fine before completing the return
        Transaction activeBorrow = getActiveBorrow(memberId, bookId);
        if (activeBorrow != null && activeBorrow.isOverdue()) {
            double fine = activeBorrow.calculateFine();
            System.out.printf("⚠  OVERDUE! Fine due: Rs. %.2f — please pay at the counter.%n", fine);
        }

        bookService.markAsAvailable(bookId);
        member.returnBook(bookId);

        Transaction txn = new Transaction(IdGenerator.generateTransactionId(), memberId, bookId, Transaction.Type.RETURN);
        transactionLog.add(txn);
        persist();

        System.out.println("✔ Returned: \"" + book.getTitle() + "\" from " + member.getName());

        // Notify if another member had reserved this book
        if (book.isReserved()) {
            System.out.println("  ★ Note: This book is reserved by member " + book.getReservedByMemberId() + " — notify them it's available!");
        }

        Logger.info("Book returned: " + bookId + " by " + memberId);
        return true;
    }

    // ── Fine Calculator ───────────────────────────────────────────────────────

    /**
     * Shows the current outstanding fine for a member's active borrows.
     */
    public void showFineForMember(String memberId) {
        Member member = memberService.getMemberById(memberId);
        if (member == null) { System.out.println("Member not found."); return; }

        double totalFine = 0;
        System.out.println("\n── Fine Summary for " + member.getName() + " ──");
        for (String bookId : member.getBorrowedBookIds()) {
            Transaction t = getActiveBorrow(memberId, bookId);
            if (t != null && t.isOverdue()) {
                double fine = t.calculateFine();
                totalFine += fine;
                System.out.printf("  Book %-12s | Due: %s | Fine: Rs. %.2f%n",
                    bookId, t.getDueDate(), fine);
            }
        }
        if (totalFine == 0) {
            System.out.println("  No outstanding fines.");
        } else {
            System.out.printf("  Total Fine Payable: Rs. %.2f%n", totalFine);
        }
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    public void printFullHistory() {
        if (transactionLog.isEmpty()) { System.out.println("No transactions yet."); return; }
        System.out.println("\n═══ Full Transaction History ═══");
        transactionLog.forEach(System.out::println);
    }

    public void printMemberHistory(String memberId) {
        List<Transaction> list = transactionLog.stream()
            .filter(t -> t.getMemberId().equals(memberId))
            .collect(Collectors.toList());
        if (list.isEmpty()) { System.out.println("No transactions for: " + memberId); return; }
        System.out.println("\n═══ History — " + memberId + " ═══");
        list.forEach(System.out::println);
    }

    public void printOverdueReport() {
        List<Transaction> overdue = transactionLog.stream()
            .filter(Transaction::isOverdue)
            .collect(Collectors.toList());
        if (overdue.isEmpty()) { System.out.println("✔ No overdue books — great!"); return; }
        System.out.println("\n═══ Overdue Books ═══");
        overdue.forEach(System.out::println);
    }

    public void printLibrarySummary() {
        long borrows  = transactionLog.stream().filter(t -> t.getType() == Transaction.Type.BORROW).count();
        long returns  = transactionLog.stream().filter(t -> t.getType() == Transaction.Type.RETURN).count();
        long overdue  = transactionLog.stream().filter(Transaction::isOverdue).count();

        System.out.println("\n═══════════════════════════════");
        System.out.println("       LIBRARY SUMMARY         ");
        System.out.println("═══════════════════════════════");
        System.out.println("Total Books       : " + bookService.getAllBooks().size());
        System.out.println("Available Books   : " + bookService.getAvailableBooks().size());
        System.out.println("Currently Issued  : " + bookService.getIssuedBooks().size());
        System.out.println("Reserved Books    : " + bookService.getReservedBooks().size());
        System.out.println("Registered Members: " + memberService.getAllMembers().size());
        System.out.println("Total Borrows     : " + borrows);
        System.out.println("Total Returns     : " + returns);
        System.out.println("Overdue Items     : " + overdue);
        System.out.println("═══════════════════════════════");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Transaction getActiveBorrow(String memberId, String bookId) {
        return transactionLog.stream()
            .filter(t -> t.getMemberId().equals(memberId)
                      && t.getBookId().equals(bookId)
                      && t.getType() == Transaction.Type.BORROW)
            .reduce((a, b) -> b)
            .orElse(null);
    }

    private void persist() { FileStorage.saveTransactions(transactionLog); }
}
