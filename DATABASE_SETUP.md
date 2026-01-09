# MySQL Database Setup Guide (Laragon)

This guide explains how to set up MySQL database for the Seminar Management System using Laragon.

---

## 📋 Prerequisites

- **Laragon** installed on your computer
- Download from: https://laragon.org/download/

---

## 🚀 Step-by-Step Setup

### Step 1: Start Laragon

1. Open **Laragon**
2. Click **"Start All"** button
3. Wait for MySQL to start (green indicator)

### Step 2: Open phpMyAdmin

1. In Laragon, click **"Menu"** → **"MySQL"** → **"phpMyAdmin"**
2. Or open browser and go to: `http://localhost/phpmyadmin`
3. Login with:
   - Username: `root`
   - Password: (leave empty - Laragon default)

### Step 3: Create Database

**Option A: Using phpMyAdmin UI**
1. Click **"New"** in the left sidebar
2. Database name: `seminar_db`
3. Collation: `utf8mb4_general_ci`
4. Click **"Create"**

**Option B: Using SQL**
1. Click **"SQL"** tab
2. Run: `CREATE DATABASE seminar_db;`
3. Click **"Go"**

### Step 4: Import Schema

1. Select **`seminar_db`** from left sidebar
2. Click **"Import"** tab
3. Click **"Choose File"**
4. Select `database/schema.sql` from your project folder
5. Click **"Go"**

**Or run SQL directly:**
1. Click **"SQL"** tab
2. Copy contents of `database/schema.sql`
3. Paste and click **"Go"**

### Step 5: Verify Tables

After import, you should see these tables:
- `users`
- `sessions`
- `session_presenters`
- `session_evaluators`
- `evaluations`
- `poster_boards`
- `awards`

---

## ⚙️ Database Configuration

The application uses these default settings (Laragon defaults):

```
Host: localhost
Port: 3306
Database: seminar_db
Username: root
Password: (empty)
```

If your Laragon uses different settings, edit:
`src/main/java/com/fci/seminar/service/DatabaseManager.java`

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/seminar_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = ""; // Change if needed
```

---

## 🔧 Troubleshooting

### Error: "Database connection failed"

**Cause:** MySQL is not running

**Solution:**
1. Open Laragon
2. Click "Start All"
3. Wait for MySQL indicator to turn green
4. Restart the application

### Error: "Unknown database 'seminar_db'"

**Cause:** Database not created

**Solution:**
1. Open phpMyAdmin
2. Create database named `seminar_db`
3. Import `database/schema.sql`

### Error: "Access denied for user 'root'"

**Cause:** Password mismatch

**Solution:**
1. Check your MySQL password in Laragon
2. Update `DB_PASSWORD` in `DatabaseManager.java`
3. Recompile: `apache-maven-3.9.12\bin\mvn.cmd compile`

### Error: "Table doesn't exist"

**Cause:** Schema not imported

**Solution:**
1. Open phpMyAdmin
2. Select `seminar_db`
3. Import `database/schema.sql`

---

## 📊 Database Schema Overview

### Tables Structure

```
users
├── id (PK)
├── username (UNIQUE)
├── password
├── role (STUDENT/EVALUATOR/COORDINATOR)
├── research_title (for students)
├── abstract_text (for students)
├── supervisor_name (for students)
├── presentation_type (ORAL/POSTER)
├── file_path
└── presenter_id

sessions
├── session_id (PK)
├── session_date
├── venue
└── session_type (ORAL/POSTER)

session_presenters
├── session_id (FK)
└── presenter_id

session_evaluators
├── session_id (FK)
└── evaluator_id (FK)

evaluations
├── evaluation_id (PK)
├── presenter_id
├── evaluator_id (FK)
├── session_id (FK)
├── problem_clarity (1-10)
├── methodology (1-10)
├── results (1-10)
├── presentation (1-10)
└── comments

poster_boards
├── board_id (PK)
├── presenter_id
└── session_id (FK)

awards
├── id (PK)
├── award_type
├── winner_id
├── score
└── ceremony_date
```

---

## 🔄 Switching Between Database and File Storage

The application automatically falls back to file storage if MySQL is unavailable.

**To force file storage:**
Edit `DataStore.java`:
```java
private static boolean useDatabase = false; // Change to false
```

**To use MySQL:**
```java
private static boolean useDatabase = true; // Default
```

---

## 📝 Quick SQL Commands

### View all users:
```sql
SELECT * FROM users;
```

### View all sessions:
```sql
SELECT * FROM sessions;
```

### View evaluations with scores:
```sql
SELECT e.*, (problem_clarity + methodology + results + presentation) as total_score
FROM evaluations e;
```

### Clear all data (reset):
```sql
DELETE FROM evaluations;
DELETE FROM session_presenters;
DELETE FROM session_evaluators;
DELETE FROM awards;
DELETE FROM sessions;
DELETE FROM users;
UPDATE poster_boards SET presenter_id = NULL, session_id = NULL;
```

---

## ✅ Verification Checklist

- [ ] Laragon is installed
- [ ] MySQL is running (green indicator)
- [ ] Database `seminar_db` created
- [ ] Schema imported (7 tables)
- [ ] Application connects successfully
- [ ] Sample data loads correctly

---

## 🎯 Running the Application

After database setup:

1. **Start Laragon** and ensure MySQL is running
2. **Double-click** `run-seminar-app.bat`
3. **Click "Yes"** to load sample data
4. **Login** with `admin` / `admin123` / Coordinator

---

## 📞 Support

If you encounter issues:
1. Check Laragon MySQL is running
2. Verify database exists in phpMyAdmin
3. Check console output for error messages
4. Review this guide's troubleshooting section

---

*Last Updated: January 2026*
