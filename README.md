# Library Management System (Fall 2025)

This project is part of the **Software Engineering course** for Fall 2025.  
It is a simple Library Management System built using **Java** and **Maven**.

---

## Team Members
- Aseel
- Maram

---

## Project Overview
The system allows:
- Admins to log in, log out, and manage books
- Users to search and borrow books
- Detection of overdue books and fine calculation
- Sending reminder messages to users

---

## Architecture
- **Presentation Layer:** Handles user input/output
- **Service Layer:** Business logic (borrow, pay fine, send reminder)
- **Domain Layer:** Core entities (Book, User, Loan, Fine)

---

## Design Patterns
- **Strategy Pattern:** For fine calculation
- **Observer Pattern:** For notifications

---

## Tools & Testing
- Java 8+
- Maven
- JUnit 5 & Mockito
- Jacoco for code coverage

---

## Notion Task Board
📋 [View our Notion task table](https://www.notion.so/298f8ceaa0fe808fa01bcadaf74b18e7)

---

## How to Run
1. Install JDK 8 or higher
2. Import the project into Eclipse or IntelliJ
3. Run using:
   ```bash
   mvn test
