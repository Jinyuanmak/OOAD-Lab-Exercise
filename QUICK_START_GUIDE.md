# Quick Start Guide

## Setup

1. **Start Laragon** and ensure MySQL is running
2. **Run schema.sql** in phpMyAdmin to create database
3. **Launch application:** Double-click `run-seminar-app.bat`

## Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Coordinator | `admin` | `admin123` |
| Evaluator | `Dr. Sarah Johnson` | `eval123` |
| Student | `student1` | `stud123` |

## As Student

1. Login with `student1` / `stud123` (select "Student" role)
2. Click **"Register"**
3. Fill in:
   - Research Title
   - Abstract
   - Select Supervisor from dropdown
   - Presentation Type (Oral/Poster)
4. Click **"Register"**
5. Your Presenter ID is generated automatically

## As Coordinator

1. Login with `admin` / `admin123` (select "Coordinator" role)

**Create Session:**
- Click "Session Management"
- Enter date, venue, type
- Click "Create Session"

**Assign Presenters/Evaluators:**
- Click "Assignments"
- Select session
- Assign presenters and evaluators

**Generate Reports:**
- Click "Reports"
- View schedule, evaluations, or summary

## As Evaluator

1. Login with supervisor name (e.g., `Dr. Sarah Johnson` / `eval123`)
2. View assigned sessions
3. Click **"Evaluate"**
4. Select presenter, enter scores (1-10), add comments
5. Click **"Submit Evaluation"**

## Reset Data

Run `database/clear_data.sql` in phpMyAdmin to reset to initial state.
