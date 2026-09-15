# 📚 Library Book Management System v2.0

A fully-featured, console-based Java application for managing a library's books, members, borrowing, reservations, and fines — built for the **Programming in Java** course at VITyarthi.

---

## Overview

The system handles every day-to-day operation of a library through a secure, menu-driven interface. Admin login is required before accessing any feature. All data is automatically saved to flat files and reloaded on the next startup — so nothing is ever lost between sessions.

---

## Features

### 🔐 Admin Authentication
- Secure login with username and password (stored as hashed values)
- 3-attempt lockout on wrong credentials
- Session-based access control

### 📖 Book Management (Module 1)
- Add, update, remove books with auto-generated IDs
- Search by title, author, or genre (case-insensitive, partial match)
- View all books or filter by availability

### 👤 Member Management (Module 2)
- Register members with email validation and 10-digit phone validation
- Duplicate email detection
- Cannot remove members who still have borrowed books

### 🔄 Borrow & Return (Module 3)
- Issue books with automatic 14-day due date
- Enforce 3-book borrow limit per member
- Return processing with overdue detection
- Reservation-aware: reserved books are prioritised for the reserving member

### 📌 Reservation System
- Reserve an issued book — be first in line when it's returned
- Cancel reservations
- Staff notified automatically when a reserved book is returned

### 💰 Fine Calculator
- Rs. 2 per day fine for overdue books
- Per-member fine summary with book-level breakdown
- Fine displayed automatically on return of overdue book

### 📊 Reports & Analytics
- Full transaction history
- Per-member transaction history
- Overdue books report
- Library-wide summary (books, members, borrows, returns, overdue count)

### 💾 File Persistence
- All books, members, and transactions saved to `data/` folder
- Auto-loaded on startup — data survives application restarts
- Corrupt records are skipped gracefully with a warning

---

## Project Structure

```
LibraryManagementSystem/
├── src/
│   └── library/
│       ├── Main.java                  ← Entry point
│       ├── model/
│       │   ├── Book.java              ← Book entity with reservation support
│       │   ├── Member.java            ← Member entity
│       │   ├── Transaction.java       ← Borrow/return record with fine calc
│       │   └── Admin.java             ← Admin account with hashed password
│       ├── service/
│       │   ├── BookService.java       ← Module 1: Book CRUD + search + reserve
│       │   ├── MemberService.java     ← Module 2: Member management
│       │   ├── BorrowService.java     ← Module 3: Issue, return, fines, reports
│       │   └── AdminService.java      ← Module 4: Authentication & session
│       ├── storage/
│       │   └── FileStorage.java       ← Persistence layer (read/write .txt files)
│       ├── util/
│       │   ├── Validator.java         ← Email, phone, year, ID validation
│       │   ├── IdGenerator.java       ← Thread-safe sequential ID generation
│       │   └── Logger.java            ← Timestamped console + file logging
│       └── ui/
│           └── ConsoleMenu.java       ← Full interactive menu system
├── test/
│   └── LibrarySystemTest.java         ← 21 test cases across all modules
├── data/                              ← Auto-created at runtime
│   ├── books.txt
│   ├── members.txt
│   ├── transactions.txt
│   └── app.log
├── README.md
└── statement.md
```

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 17+ | Core language |
| HashMap / ArrayList | In-memory data management |
| java.time.LocalDate | Due dates and overdue calculation |
| java.io (BufferedReader/Writer) | File persistence |
| java.util.concurrent.AtomicInteger | Thread-safe ID generation |
| Stream API | Filtering, searching, reporting |
| String.hashCode() | Password hashing |
| Regex (String.matches) | Email and phone validation |

---

## How to Compile & Run

### Prerequisites
- Java 17 or later installed
- Terminal / Command Prompt

### Compile
```bash
find src -name "*.java" | xargs javac -d out
```

### Run
```bash
java -cp out library.Main
```

### Default Login Credentials
```
Username: admin    Password: admin123
Username: staff    Password: staff456
```

---

## How to Run Tests

```bash
# Compile tests (src must be compiled first)
javac -cp src test/LibrarySystemTest.java -d out

# Run
java -cp out LibrarySystemTest
```

Expected output: **21 test cases — all PASS**

---

## Suggested Git Commit History

To make your repository look authentic, commit in this order:

```bash
git init
git add src/library/model/
git commit -m "Add model classes: Book, Member, Transaction, Admin"

git add src/library/util/
git commit -m "Add utility classes: Validator, IdGenerator, Logger"

git add src/library/storage/
git commit -m "Add FileStorage for data persistence"

git add src/library/service/BookService.java
git commit -m "Implement Module 1: BookService with CRUD and search"

git add src/library/service/MemberService.java
git commit -m "Implement Module 2: MemberService with validation"

git add src/library/service/BorrowService.java
git commit -m "Implement Module 3: BorrowService with fine calculator"

git add src/library/service/AdminService.java
git commit -m "Add AdminService: authentication and session management"

git add src/library/ui/
git commit -m "Add ConsoleMenu with full interactive menu system"

git add src/library/Main.java
git commit -m "Add Main entry point"

git add test/
git commit -m "Add test suite: 21 test cases across all modules"

git add README.md statement.md
git commit -m "Add documentation: README and project statement"
```

---

## Author
- **Subject:** Programming in Java
- **Platform:** VITyarthi — Build Your Own Project
- **Version:** 2.0
