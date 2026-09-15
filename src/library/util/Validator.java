package library.util;

/**
 * Validator — centralised input validation for the entire application.
 *
 * All validation rules are defined here so changes in rules
 * propagate everywhere automatically.
 *
 * @author  Student
 * @version 2.0
 */
public class Validator {

    /** Valid email: local@domain.tld — basic structure check */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /** Phone: exactly 10 digits, no spaces or dashes */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("\\d{10}");
    }

    /** Rejects null or blank strings */
    public static boolean isNonEmpty(String value) {
        return value != null && !value.isBlank();
    }

    /** Publication year must be realistic */
    public static boolean isValidYear(int year) {
        return year >= 1000 && year <= 2100;
    }

    /** Book ID format: BK-NNNN */
    public static boolean isValidBookId(String id) {
        return id != null && id.matches("BK-\\d+");
    }

    /** Member ID format: MB-NNNN */
    public static boolean isValidMemberId(String id) {
        return id != null && id.matches("MB-\\d+");
    }
}
