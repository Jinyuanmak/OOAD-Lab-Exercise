# Seminar Management System

A Java Swing desktop application for managing the FCI Postgraduate Academic Research Seminar.

## Requirements

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **MySQL** - Via Laragon (localhost:3306)
- **Maven 3.9.12** - Included in project

## Database Setup

1. Start Laragon and ensure MySQL is running
2. Open phpMyAdmin (http://localhost/phpmyadmin)
3. Run `database/schema.sql` to create database and sample data

## Running the Application

**Option 1:** Double-click `run-seminar-app.bat`

**Option 2:** Command line
```cmd
apache-maven-3.9.12\bin\mvn.cmd exec:java -Dexec.mainClass="com.fci.seminar.ui.SeminarApp"
```

## Sample Accounts

| Role | Username | Password |
|------|----------|----------|
| Coordinator | `admin` | `admin123` |
| Evaluator | `Dr. Sarah Johnson` | `eval123` |
| Evaluator | `Prof. Michael Chen` | `eval123` |
| Evaluator | `Dr. Emily Rodriguez` | `eval123` |
| Evaluator | `Prof. David Kumar` | `eval123` |
| Evaluator | `Dr. Lisa Anderson` | `eval123` |
| Student | `student1` - `student4` | `stud123` |

## User Roles

### Student
- Register for seminar with research title, abstract, and presentation type
- Select supervisor from dropdown (populated from database)
- Presenter ID is generated automatically upon registration
- Upload presentation materials

### Evaluator (Supervisor)
- View assigned sessions
- Evaluate presentations using rubric scores (1-10):
  - Problem Clarity
  - Methodology
  - Results
  - Presentation
- Submit feedback comments

### Coordinator
- Create and manage seminar sessions
- Assign presenters and evaluators to sessions
- Manage poster board assignments (B001-B020)
- Compute award winners (Best Oral, Best Poster, People's Choice)
- Generate reports

## Database Tables

- `users` - All user accounts (students, evaluators, coordinators)
- `sessions` - Seminar sessions
- `session_presenters` - Presenter assignments to sessions
- `session_evaluators` - Evaluator assignments to sessions
- `evaluations` - Evaluation scores and comments
- `poster_boards` - Poster board assignments (B001-B020)
- `venues` - Available venues
- `awards` - Award winners

## Key Features

- Role-based authentication
- Supervisor dropdown populated from database
- Automatic presenter ID generation on registration
- Session assignments persist to database
- Conflict detection for double-booking
- Auto-save functionality

## Project Structure

```
├── database/
│   ├── schema.sql      # Database setup with sample data
│   └── clear_data.sql  # Reset database to initial state
├── src/main/java/com/fci/seminar/
│   ├── model/          # Data models
│   ├── service/        # Business logic
│   ├── ui/             # Swing UI panels
│   └── util/           # Utilities
└── src/test/           # Property-based tests
```

## Testing

```cmd
apache-maven-3.9.12\bin\mvn.cmd test
```

## Reset Database

To clear all data and reload sample accounts:
1. Run `database/clear_data.sql` in phpMyAdmin

## Troubleshooting

**Database connection failed:**
- Ensure Laragon MySQL is running
- Verify database `seminar_db` exists

**Login fails:**
- Select correct role from dropdown
- Check username/password (case-sensitive)

**Session assignments not saving:**
- Verify MySQL connection is active
- Check console for error messages
