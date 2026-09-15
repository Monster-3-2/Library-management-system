package library.service;

import library.model.Admin;
import library.util.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminService — Module 4
 *
 * Handles administrator authentication.
 * The system requires a valid admin login before any operations can be performed,
 * preventing unauthorised access to library data.
 *
 * Default credentials (for demo):
 *   Username: admin   Password: admin123
 *   Username: staff   Password: staff456
 *
 * @author  Student
 * @version 2.0
 */
public class AdminService {

    private Map<String, Admin> admins;
    private Admin loggedInAdmin;

    public AdminService() {
        admins = new HashMap<>();
        // Pre-configured admin accounts
        admins.put("admin", new Admin("ADM-001", "admin", "admin123"));
        admins.put("staff", new Admin("ADM-002", "staff", "staff456"));
        loggedInAdmin = null;
    }

    /**
     * Attempts to authenticate a user.
     * Returns true if credentials are correct.
     */
    public boolean login(String username, String password) {
        Admin admin = admins.get(username.toLowerCase());
        if (admin != null && admin.authenticate(password)) {
            loggedInAdmin = admin;
            Logger.info("Admin logged in: " + username);
            System.out.println("✔ Welcome, " + admin.getUsername() + "! Login successful.");
            return true;
        }
        Logger.warn("Failed login attempt for username: " + username);
        System.out.println("✘ Invalid credentials. Please try again.");
        return false;
    }

    public void logout() {
        if (loggedInAdmin != null) {
            Logger.info("Admin logged out: " + loggedInAdmin.getUsername());
            System.out.println("Logged out. Goodbye, " + loggedInAdmin.getUsername() + "!");
            loggedInAdmin = null;
        }
    }

    public boolean isLoggedIn()      { return loggedInAdmin != null; }
    public Admin   getLoggedInAdmin() { return loggedInAdmin; }
}
