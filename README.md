# Seminar Management System

A Java Swing desktop application for managing the FCI Postgraduate Academic Research Seminar.

## Quick Start

### Requirements
- Java 17+
- MySQL (via Laragon)
- Maven 3.9.12 (included)

### Setup
1. Start Laragon MySQL
2. Run `database/schema.sql` in phpMyAdmin
3. Double-click `run-seminar-app.bat`

### Default Login
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** Coordinator

---

## Features by Role

### 👨‍🎓 Student (Presenter)

**Registration**
- Sign up with username, password, student ID
- Enter research title, abstract, supervisor
- Choose presentation type (ORAL/POSTER)
- Upload materials (PDF, images, text)

**My Session**
- View session details (date, venue/meeting link)
- Join ORAL sessions via Teams link
- View POSTER venue location

**Voting**
- Vote for one presenter (People's Choice)
- View other students' materials
- One vote per student

---

### 👨‍🏫 Evaluator (Panel Member)

**Dashboard**
- View assigned sessions
- See evaluation status

**Evaluation**
- View presenter materials
- Join ORAL sessions via Teams link
- Score using rubric (1-10 scale):
  - Problem Clarity
  - Methodology
  - Results
  - Presentation
- Add comments
- Submit/update evaluations

---

### 👨‍💼 Coordinator (Admin)

**User Management**
- View all users (students, evaluators)

**Session Management**
- Create ORAL sessions (with Teams link)
- Create POSTER sessions (with venue)
- Delete sessions

**Assignments**
- Assign presenters to sessions
- Assign evaluators to sessions
- Conflict detection

**Poster Boards**
- Assign boards (B001-B020) to POSTER presenters
- View presenter materials

**Awards**
- View vote counts
- Compute winners:
  - Best Oral Presentation
  - Best Poster Presentation
  - People's Choice
- Generate ceremony agenda

**Reports**
- Generate system reports
- Export to TXT, PDF, CSV

---

## Key Features

### Centralized File Storage
- Files stored in `uploads/presentations/{presenter_id}/`
- Supported: PDF, JPG, JPEG, PNG, GIF, TXT
- Automatic file validation and cleanup

### Session Types
- **ORAL:** Online with Teams meeting links
- **POSTER:** Physical venues with poster boards

### Presentation Viewer
- Zoom controls (50%-120%)
- PDF rendering
- Image display

### Auto-Refresh
All panels refresh automatically when displayed.

---

## Database Tables

- `users` - User accounts
- `sessions` - Seminar sessions
- `session_presenters` - Presenter assignments
- `session_evaluators` - Evaluator assignments
- `evaluations` - Scores and comments
- `poster_boards` - Board assignments
- `venues` - Available venues
- `awards` - Award winners
- `votes` - Voting records

---

## Project Structure

```
├── database/           # SQL scripts
├── logs/              # Error logs
├── uploads/           # Presentation files
├── src/main/java/
│   ├── model/         # Data models
│   ├── service/       # Business logic
│   ├── ui/            # Swing panels
│   └── util/          # Utilities
└── pom.xml            # Dependencies
```

---

## Troubleshooting

**Database connection failed**
- Ensure Laragon MySQL is running
- Verify `seminar_db` exists

**Login fails**
- Check username/password (case-sensitive)
- Student ID must be 10 alphanumeric characters

**File upload fails**
- Check file type (PDF, JPG, PNG, GIF, TXT only)
- Check `logs/file-storage-errors.log`

**Materials not showing**
- Verify file uploaded successfully
- Check `uploads/presentations/{presenter_id}/`

**Meeting link not showing**
- Only visible for ORAL sessions
- Hidden for POSTER sessions

---

## Reset Database

```sql
-- Run in phpMyAdmin
1. database/clear_data.sql
2. database/schema.sql
```

---

## Technical Details

- **Window Size:** 1000 x 800 pixels
- **Dependencies:** MySQL Connector, HikariCP, JCalendar, Apache PDFBox
- **Architecture:** See `UML_DIAGRAMS.md`

---

## Support

Check `logs/file-storage-errors.log` for error details.
