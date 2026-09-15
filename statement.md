# Project Statement

## Problem Statement

Libraries that rely on manual paper-based records struggle with:
- No real-time visibility into book availability or issue status
- Overdue books going unnoticed, resulting in financial losses
- No systematic waiting list when a book is in demand by multiple members
- Difficulty generating reports or summaries for management decisions
- Zero access control — anyone can modify records

This project solves all of the above with a structured, secure, Java-based application that automates every library operation and persists data across sessions.

---

## Scope of the Project

| In Scope | Out of Scope |
|---|---|
| Book CRUD and search | Online/web interface |
| Member registration and validation | Payment gateway for fines |
| Book issue and return | SMS / email notifications |
| Reservation (waiting list) | Multi-branch support |
| Fine calculation (Rs. 2/day) | Cloud storage |
| Admin login with lockout | Role-based permissions |
| File persistence (data survives restarts) | |
| Transaction history and reports | |

---

## Target Users

| User | How They Use the System |
|---|---|
| Library Admin | Logs in, manages books and members, views reports |
| Library Staff | Issues and returns books, handles reservations and fines |
| Students / Members | Indirect users — their data is managed by staff |

---

## High-Level Features

1. **Secure Admin Login** — Username/password authentication with 3-attempt lockout and hashed password storage.

2. **Book Management** — Full CRUD with auto-generated IDs, partial-match search by title/author/genre, and availability tracking.

3. **Member Management** — Registration with email and phone validation, duplicate detection, and safe removal guards.

4. **Borrow & Return** — 14-day due dates, 3-book limit enforcement, overdue detection, reservation-priority issue handling.

5. **Reservation System** — Members can reserve an issued book; staff are notified when it becomes available.

6. **Fine Calculator** — Rs. 2 per overdue day, shown per-book and as a total for each member.

7. **File Persistence** — All data saved to `data/` folder automatically; reloaded on next startup with graceful error handling for corrupt records.

8. **Logging** — Every key event timestamped and written to `data/app.log` for audit and monitoring.
