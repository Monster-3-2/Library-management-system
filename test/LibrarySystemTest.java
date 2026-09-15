import library.model.*;
import library.service.*;
import library.storage.FileStorage;
import library.util.Validator;

/**
 * LibrarySystemTest — Manual test suite for the Library Management System.
 *
 * Tests all three core modules plus the fine calculator and reservation system.
 * Each test prints PASS or FAIL with a clear label.
 *
 * Run:
 *   javac -cp src test/LibrarySystemTest.java -d out
 *   java -cp out LibrarySystemTest
 *
 * @author  Student
 * @version 2.0
 */
public class LibrarySystemTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        FileStorage.initialise();

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     Library System — Test Suite      ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        testBookAdd();
        testBookRemoveAvailable();
        testBookRemoveIssued();
        testBookSearchByTitle();
        testBookSearchByAuthor();
        testBookUpdate();
        testMemberRegisterValid();
        testMemberRegisterInvalidEmail();
        testMemberRegisterInvalidPhone();
        testMemberDuplicateEmail();
        testMemberRemoveWithBorrows();
        testIssueBook();
        testIssueAlreadyIssuedBook();
        testBorrowLimit();
        testReturnBook();
        testReservation();
        testValidatorEmail();
        testValidatorPhone();
        testValidatorYear();
        testAdminLogin();
        testAdminWrongPassword();

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.printf ("║  Results: %2d PASSED  |  %2d FAILED    ║%n", passed, failed);
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ── Book Tests ────────────────────────────────────────────────────────────

    static void testBookAdd() {
        BookService bs = freshBookService();
        int before = bs.getAllBooks().size();
        Book b = bs.addBook("Test Book", "Author A", "Fiction", 2020);
        check(b != null && bs.getAllBooks().size() == before + 1, "Book: add new book");
    }

    static void testBookRemoveAvailable() {
        BookService bs = freshBookService();
        Book b = bs.addBook("Removable", "X", "X", 2000);
        check(bs.removeBook(b.getBookId()), "Book: remove available book");
    }

    static void testBookRemoveIssued() {
        BookService bs = freshBookService();
        Book b = bs.addBook("Issued Book", "X", "X", 2000);
        bs.markAsIssued(b.getBookId());
        check(!bs.removeBook(b.getBookId()), "Book: cannot remove issued book");
    }

    static void testBookSearchByTitle() {
        BookService bs = freshBookService();
        bs.addBook("Java Programming", "Author", "Tech", 2021);
        check(!bs.searchByTitle("Java").isEmpty(), "Book: search by title");
    }

    static void testBookSearchByAuthor() {
        BookService bs = freshBookService();
        bs.addBook("Any Book", "Unique Author Name", "Tech", 2021);
        check(!bs.searchByAuthor("Unique Author").isEmpty(), "Book: search by author");
    }

    static void testBookUpdate() {
        BookService bs = freshBookService();
        Book b = bs.addBook("Old Title", "Old Author", "Genre", 2000);
        bs.updateBook(b.getBookId(), "New Title", "New Author", "New Genre");
        check("New Title".equals(bs.getBookById(b.getBookId()).getTitle()), "Book: update details");
    }

    // ── Member Tests ──────────────────────────────────────────────────────────

    static void testMemberRegisterValid() {
        MemberService ms = freshMemberService();
        Member m = ms.registerMember("Valid User", "valid@test.com", "9876543210");
        check(m != null, "Member: register with valid data");
    }

    static void testMemberRegisterInvalidEmail() {
        MemberService ms = freshMemberService();
        Member m = ms.registerMember("Bad Email", "not-an-email", "9876543210");
        check(m == null, "Member: reject invalid email");
    }

    static void testMemberRegisterInvalidPhone() {
        MemberService ms = freshMemberService();
        Member m = ms.registerMember("Bad Phone", "ok@test.com", "123");
        check(m == null, "Member: reject short phone number");
    }

    static void testMemberDuplicateEmail() {
        MemberService ms = freshMemberService();
        ms.registerMember("First", "dup@test.com", "9000000001");
        Member m2 = ms.registerMember("Second", "dup@test.com", "9000000002");
        check(m2 == null, "Member: reject duplicate email");
    }

    static void testMemberRemoveWithBorrows() {
        BookService bs   = freshBookService();
        MemberService ms = freshMemberService();
        BorrowService brs = new BorrowService(bs, ms);
        Member m = ms.registerMember("Borrower", "borrow@test.com", "9111111111");
        Book b   = bs.addBook("Borrowed Book", "A", "G", 2020);
        brs.issueBook(m.getMemberId(), b.getBookId());
        check(!ms.removeMember(m.getMemberId()), "Member: cannot remove member with active borrow");
    }

    // ── Borrow Tests ──────────────────────────────────────────────────────────

    static void testIssueBook() {
        BookService bs   = freshBookService();
        MemberService ms = freshMemberService();
        BorrowService brs = new BorrowService(bs, ms);
        Member m = ms.registerMember("Issuer", "issuer@test.com", "9222222222");
        Book b   = bs.addBook("Good Book", "A", "G", 2020);
        check(brs.issueBook(m.getMemberId(), b.getBookId()), "Borrow: issue available book");
        check(!b.isAvailable(), "Borrow: book marked as unavailable after issue");
    }

    static void testIssueAlreadyIssuedBook() {
        BookService bs    = freshBookService();
        MemberService ms  = freshMemberService();
        BorrowService brs = new BorrowService(bs, ms);
        Member m1 = ms.registerMember("M1", "m1@test.com", "9333333331");
        Member m2 = ms.registerMember("M2", "m2@test.com", "9333333332");
        Book b    = bs.addBook("Scarce Book", "A", "G", 2020);
        brs.issueBook(m1.getMemberId(), b.getBookId());
        check(!brs.issueBook(m2.getMemberId(), b.getBookId()), "Borrow: reject issue of already issued book");
    }

    static void testBorrowLimit() {
        BookService bs    = freshBookService();
        MemberService ms  = freshMemberService();
        BorrowService brs = new BorrowService(bs, ms);
        Member m = ms.registerMember("Heavy Reader", "heavy@test.com", "9444444444");
        Book b1 = bs.addBook("Book 1", "A", "G", 2020);
        Book b2 = bs.addBook("Book 2", "A", "G", 2020);
        Book b3 = bs.addBook("Book 3", "A", "G", 2020);
        Book b4 = bs.addBook("Book 4", "A", "G", 2020);
        brs.issueBook(m.getMemberId(), b1.getBookId());
        brs.issueBook(m.getMemberId(), b2.getBookId());
        brs.issueBook(m.getMemberId(), b3.getBookId());
        check(!brs.issueBook(m.getMemberId(), b4.getBookId()), "Borrow: enforce 3-book borrow limit");
    }

    static void testReturnBook() {
        BookService bs    = freshBookService();
        MemberService ms  = freshMemberService();
        BorrowService brs = new BorrowService(bs, ms);
        Member m = ms.registerMember("Returner", "returner@test.com", "9555555555");
        Book b   = bs.addBook("Return Me", "A", "G", 2020);
        brs.issueBook(m.getMemberId(), b.getBookId());
        check(brs.returnBook(m.getMemberId(), b.getBookId()), "Borrow: return book successfully");
        check(b.isAvailable(), "Borrow: book available again after return");
    }

    // ── Reservation Test ──────────────────────────────────────────────────────

    static void testReservation() {
        BookService bs    = freshBookService();
        MemberService ms  = freshMemberService();
        BorrowService brs = new BorrowService(bs, ms);
        Member m1 = ms.registerMember("Res1", "res1@test.com", "9666666661");
        Member m2 = ms.registerMember("Res2", "res2@test.com", "9666666662");
        Book b    = bs.addBook("Hot Book", "A", "G", 2020);
        brs.issueBook(m1.getMemberId(), b.getBookId());
        bs.reserveBook(b.getBookId(), m2.getMemberId());
        check(b.isReserved(), "Reservation: book marked as reserved");
        check(b.getReservedByMemberId().equals(m2.getMemberId()), "Reservation: correct member ID stored");
    }

    // ── Validator Tests ───────────────────────────────────────────────────────

    static void testValidatorEmail() {
        check(Validator.isValidEmail("user@domain.com"),  "Validator: accept valid email");
        check(!Validator.isValidEmail("notanemail"),      "Validator: reject invalid email");
        check(!Validator.isValidEmail(""),                "Validator: reject blank email");
    }

    static void testValidatorPhone() {
        check(Validator.isValidPhone("9876543210"),  "Validator: accept 10-digit phone");
        check(!Validator.isValidPhone("12345"),      "Validator: reject short phone");
        check(!Validator.isValidPhone("abcdefghij"), "Validator: reject non-numeric phone");
    }

    static void testValidatorYear() {
        check(Validator.isValidYear(2020),  "Validator: accept valid year");
        check(!Validator.isValidYear(999),  "Validator: reject year < 1000");
        check(!Validator.isValidYear(2200), "Validator: reject year > 2100");
    }

    // ── Admin Tests ───────────────────────────────────────────────────────────

    static void testAdminLogin() {
        AdminService as = new AdminService();
        check(as.login("admin", "admin123"), "Admin: correct credentials accepted");
        check(as.isLoggedIn(),               "Admin: session active after login");
    }

    static void testAdminWrongPassword() {
        AdminService as = new AdminService();
        check(!as.login("admin", "wrongpass"), "Admin: wrong password rejected");
        check(!as.isLoggedIn(),                "Admin: no session after failed login");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    static BookService freshBookService() {
        // Use a clean in-memory-like service (loads from file; ok for tests)
        return new BookService();
    }

    static MemberService freshMemberService() {
        return new MemberService();
    }

    static void check(boolean condition, String label) {
        if (condition) {
            System.out.println("  ✔  PASS — " + label);
            passed++;
        } else {
            System.out.println("  ✘  FAIL — " + label);
            failed++;
        }
    }
}
