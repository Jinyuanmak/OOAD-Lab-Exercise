# Seminar Management System

A Java Swing desktop application for managing the FCI Postgraduate Academic Research Seminar.

## Requirements

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **MySQL** - Via Laragon (localhost:3306)
- **Maven 3.9.12** - Included in project

## Database Setup

1. Start Laragon and ensure MySQL is running
2. Open phpMyAdmin (http://localhost/phpmyadmin)
3. Run `database/schema.sql` to create database and tables

## Running the Application

**Option 1:** Double-click `run-seminar-app.bat`

**Option 2:** Command line
```cmd
apache-maven-3.9.12\bin\mvn.cmd exec:java -Dexec.mainClass="com.fci.seminar.ui.SeminarApp"
```

## Default Account

| Role | Username | Password |
|------|----------|----------|
| Coordinator | `admin` | `admin123` |

**Note:** Students and Evaluators must sign up through the application.

## User Roles & Features

### Student (Presenter)
- **Sign Up & Registration:**
  - Register with username, password, and student ID (10 alphanumeric characters)
  - Provide research title, abstract, and supervisor name
  - Select presentation type (ORAL or POSTER)
  - Presenter ID is auto-generated (format: P-xxxxxxxx)
  
- **Upload Materials:**
  - Upload presentation files (PDF, images, text)
  - Materials are viewable by evaluators and other students

- **My Session:**
  - View assigned session details (date, venue/meeting link, evaluator)
  - For ORAL sessions: Join Teams meeting via clickable link
  - For POSTER sessions: View venue location

- **People's Choice Voting:**
  - Vote for one other presenter (cannot vote for self)
  - View other students' presentation materials before voting
  - Each student can only vote once
  - Presentation viewer with zoom controls (50%-120%)

### Evaluator (Panel Member)
- **Sign Up:**
  - Register with username and password
  - Evaluator ID is auto-generated (format: EV-xxxxxxxx)

- **Dashboard:**
  - View all assigned sessions with presenters
  - Auto-refreshes when displayed
  - Double-click to evaluate a presenter

- **Evaluation Form:**
  - View session details and meeting link (for ORAL sessions)
  - Join ORAL presentations via Teams meeting link
  - View and download presenter's materials
  - Presentation viewer with zoom functionality
  - Score presentations using rubric (1-10 scale):
    - Problem Clarity
    - Methodology
    - Results
    - Presentation
  - Total score calculated automatically (max 40 points)
  - Add comments and feedback
  - Update evaluations if needed

### Coordinator (Admin)
- **User Management:**
  - View all users (students, evaluators, coordinators)
  - Auto-refreshes when displayed

- **Session Management:**
  - Create sessions with date, venue, and type (ORAL/POSTER)
  - For ORAL sessions: Set meeting link (Teams URL)
  - For POSTER sessions: Select physical venue
  - Auto-refreshes when displayed

- **Assignment Panel:**
  - Assign presenters to sessions (one per session)
  - Assign evaluators to sessions (one per session)
  - Conflict detection prevents double-booking
  - Auto-refreshes when displayed

- **Poster Board Management:**
  - Assign poster boards (B001-B020) to POSTER presenters
  - View presenter materials by double-clicking
  - Presentation viewer with zoom controls
  - Auto-refreshes when displayed

- **Awards & Ceremony:**
  - View vote counts for People's Choice (read-only)
  - Compute award winners:
    - Best Oral Presentation (highest evaluation score)
    - Best Poster Presentation (highest evaluation score)
    - People's Choice (most student votes)
  - Generate ceremony agenda
  - Auto-refreshes when displayed

- **Reports:**
  - Generate comprehensive reports
  - Export to PDF format

## Database Tables

- `users` - All user accounts with role-specific fields
- `sessions` - Seminar sessions with meeting links
- `session_presenters` - Presenter-to-session assignments
- `session_evaluators` - Evaluator-to-session assignments
- `evaluations` - Evaluation scores and comments
- `poster_boards` - Poster board assignments (B001-B020)
- `venues` - Available venues (11 pre-configured)
- `awards` - Award winners
- `votes` - Student voting records for People's Choice

## Key Features

### Authentication & Security
- Role-based access control (Student, Evaluator, Coordinator)
- Secure login with role selection
- Auto-generated IDs for presenters and evaluators

### Session Management
- ORAL sessions: Online with Teams meeting links
- POSTER sessions: Physical venues with poster boards
- Conflict detection for scheduling
- Auto-refresh on all management panels

### Presentation Materials
- Upload and store presentation files
- View materials with zoom controls (50%-120%)
- Download materials for offline viewing
- Supports PDF, images, and text files
- PDF rendering with high quality (supersampling)

### Voting System
- Student-driven People's Choice voting
- One vote per student
- Cannot vote for self
- Vote counts stored in database
- Real-time vote tracking

### Evaluation System
- Rubric-based scoring (1-10 per criterion)
- Automatic total score calculation
- Comments and feedback
- Update capability for evaluations
- Meeting link integration for ORAL sessions

### Awards & Reporting
- Automated winner computation
- Ceremony agenda generation
- PDF report export
- Vote count display (read-only for admin)

## Project Structure

```
├── database/
│   ├── schema.sql      # Complete database setup
│   └── clear_data.sql  # Reset database
├── src/main/java/com/fci/seminar/
│   ├── model/          # Data models (User, Session, Evaluation, etc.)
│   ├── service/        # Business logic and database operations
│   ├── ui/             # Swing UI panels (dashboards, forms, dialogs)
│   └── util/           # Utilities (error handling, ID generation)
├── pom.xml             # Maven dependencies
└── run-seminar-app.bat # Quick start script
```

## Dependencies

- **MySQL Connector/J 8.2.0** - Database connectivity
- **HikariCP 5.1.0** - Connection pooling
- **JCalendar 1.4** - Date picker component
- **Apache PDFBox 2.0.30** - PDF rendering and export
- **JUnit 5** - Unit testing (test scope)
- **jqwik** - Property-based testing (test scope)

## Testing

```cmd
apache-maven-3.9.12\bin\mvn.cmd test
```

## Reset Database

To clear all data and reload default admin account:
1. Run `database/clear_data.sql` in phpMyAdmin
2. Run `database/schema.sql` to recreate tables

## Troubleshooting

**Database connection failed:**
- Ensure Laragon MySQL is running
- Verify database `seminar_db` exists
- Check connection settings in `DatabaseManager.java`

**Login fails:**
- Select correct role from dropdown
- Username and password are case-sensitive
- Student ID must be exactly 10 alphanumeric characters (uppercase)

**Sessions not showing for evaluator:**
- Ensure evaluator has been assigned to sessions
- Check that evaluator_id is correctly set
- Restart application after assignments

**Materials not displaying:**
- Verify file path is correct
- Check file exists in the specified location
- Supported formats: PDF, JPG, PNG, GIF, TXT

**Zoom not working:**
- Use Ctrl + Mouse Scroll to zoom
- Or use +/- buttons in viewer
- Zoom range: 50% - 120%

## Auto-Refresh Panels

The following panels automatically refresh when displayed:
- Student Dashboard
- Student Registration
- My Session
- Voting Panel
- Evaluator Dashboard
- Session Management
- Assignment Panel
- Poster Management
- User Management
- Awards & Ceremony

No manual refresh buttons needed!
