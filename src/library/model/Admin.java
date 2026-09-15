package library.model;

/**
 * Represents a library administrator account.
 *
 * Passwords are stored as a simple hash (not plaintext) for basic security.
 * In a production system, bcrypt or similar would be used.
 *
 * @author  Student
 * @version 2.0
 */
public class Admin {

    private String adminId;
    private String username;
    private int    passwordHash; // Java's built-in String.hashCode()

    public Admin(String adminId, String username, String password) {
        this.adminId      = adminId;
        this.username     = username;
        this.passwordHash = password.hashCode();
    }

    public String getAdminId()   { return adminId; }
    public String getUsername()  { return username; }

    /**
     * Validates a login attempt by comparing hashes.
     */
    public boolean authenticate(String inputPassword) {
        return inputPassword.hashCode() == this.passwordHash;
    }

    @Override
    public String toString() {
        return "[Admin] " + username + " (ID: " + adminId + ")";
    }
}
