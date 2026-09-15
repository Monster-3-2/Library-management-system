package library.service;

import library.model.Member;
import library.storage.FileStorage;
import library.util.IdGenerator;
import library.util.Logger;
import library.util.Validator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MemberService — Module 2
 *
 * Manages library member registration, updates, search, and removal.
 * All data is persisted to members.txt automatically.
 *
 * @author  Student
 * @version 2.0
 */
public class MemberService {

    private Map<String, Member> registry;

    public MemberService() {
        this.registry = new HashMap<>();
        FileStorage.loadMembers().forEach(m -> registry.put(m.getMemberId(), m));

        if (registry.isEmpty()) {
            Logger.info("No saved members found — loading sample members.");
            loadSampleMembers();
        } else {
            Logger.info("Loaded " + registry.size() + " members from storage.");
        }
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public Member registerMember(String name, String email, String phone) {
        if (!Validator.isValidEmail(email)) {
            System.out.println("✘ Invalid email address: " + email);
            return null;
        }
        if (!Validator.isValidPhone(phone)) {
            System.out.println("✘ Phone number must be exactly 10 digits.");
            return null;
        }
        // Prevent duplicate emails
        boolean emailExists = registry.values().stream()
            .anyMatch(m -> m.getEmail().equalsIgnoreCase(email));
        if (emailExists) {
            System.out.println("✘ A member with this email already exists.");
            return null;
        }
        String id = IdGenerator.generateMemberId();
        Member member = new Member(id, name, email, phone);
        registry.put(id, member);
        persist();
        Logger.info("Member registered: " + name);
        System.out.println("✔ Member registered → " + member);
        return member;
    }

    public boolean updateMember(String memberId, String name, String email, String phone) {
        Member m = registry.get(memberId);
        if (m == null) { System.out.println("Member not found: " + memberId); return false; }
        if (!Validator.isValidEmail(email)) { System.out.println("✘ Invalid email."); return false; }
        m.setName(name); m.setEmail(email); m.setPhone(phone);
        persist();
        System.out.println("✔ Member updated: " + name);
        return true;
    }

    public boolean removeMember(String memberId) {
        Member m = registry.get(memberId);
        if (m == null) { System.out.println("Member not found: " + memberId); return false; }
        if (!m.getBorrowedBookIds().isEmpty()) {
            System.out.println("✘ Cannot remove " + m.getName() + " — they have " +
                m.getBorrowedBookIds().size() + " unreturned book(s).");
            return false;
        }
        registry.remove(memberId);
        persist();
        System.out.println("✔ Member removed: " + m.getName());
        return true;
    }

    // ── Search & Retrieval ────────────────────────────────────────────────────

    public Member getMemberById(String id) { return registry.get(id); }

    public List<Member> searchByName(String kw) {
        return registry.values().stream()
            .filter(m -> m.getName().toLowerCase().contains(kw.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<Member> getAllMembers() { return new ArrayList<>(registry.values()); }

    // ── Persistence ───────────────────────────────────────────────────────────

    private void persist() { FileStorage.saveMembers(registry.values()); }

    // ── Sample Data ───────────────────────────────────────────────────────────

    private void loadSampleMembers() {
        registerMember("Arjun Sharma", "arjun@email.com", "9876543210");
        registerMember("Priya Nair",   "priya@email.com", "9123456780");
        registerMember("Rohan Mehta",  "rohan@email.com", "9988776655");
    }
}
