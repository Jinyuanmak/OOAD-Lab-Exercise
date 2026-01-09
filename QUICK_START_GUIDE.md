# Seminar Management System - Quick Start Guide

## Running the Application

### Method 1: Maven Exec (Easiest)
```cmd
apache-maven-3.9.12\bin\mvn.cmd exec:java -Dexec.mainClass="com.fci.seminar.ui.SeminarApp"
```

### Method 2: Build JAR and Run
```cmd
apache-maven-3.9.12\bin\mvn.cmd clean package
java -jar target\seminar-management-system-1.0-SNAPSHOT.jar
```

---

## First Time Setup

1. **Launch the application** using one of the methods above
2. **Sample Data Prompt:** Click "Yes" to load test data
3. **Login Screen:** Use one of the sample accounts below

---

## Sample Accounts

### Coordinator Account
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** Select "Coordinator" from dropdown

**What you can do:**
- Create and manage seminar sessions
- Assign presenters and evaluators to sessions
- Manage poster board assignments
- Compute award winners
- Generate reports

### Evaluator Account
- **Username:** `eval1` or `eval2`
- **Password:** `eval123`
- **Role:** Select "Evaluator" from dropdown

**What you can do:**
- View assigned sessions
- Evaluate presentations using rubric scores
- Submit feedback comments

### Student Account
- **Username:** `student1`, `student2`, `student3`, or `student4`
- **Password:** `stud123`
- **Role:** Select "Student" from dropdown

**What you can do:**
- Register for the seminar
- Upload presentation materials
- View registration status

---

## Testing the System

### As Coordinator (admin/admin123)

1. **Create a Session:**
   - Click "Session Management"
   - Fill in date (format: yyyy-MM-dd, e.g., 2026-01-15)
   - Enter venue (e.g., "Room 101")
   - Select session type (Oral or Poster)
   - Click "Create Session"

2. **Assign Presenters:**
   - Click "Assignments"
   - Select a session from dropdown
   - Select a student from available presenters
   - Click "Assign Presenter"

3. **Assign Evaluators:**
   - In the same Assignments panel
   - Select an evaluator from available evaluators
   - Click "Assign Evaluator"

4. **Manage Poster Boards:**
   - Click "Poster Management"
   - Select a board ID (e.g., B001)
   - Select a poster presenter
   - Click "Assign Board"

5. **Compute Awards:**
   - Click "Awards"
   - Click "Compute Winners"
   - View Best Oral, Best Poster results
   - Click "Generate Agenda" for ceremony

6. **Generate Reports:**
   - Click "Reports"
   - Click "Schedule Report" to see all sessions
   - Click "Evaluation Report" to see all scores
   - Click "Summary" for analytics
   - Click "Export" to save to file

### As Evaluator (eval1/eval123)

1. **View Assignments:**
   - After login, you'll see your assigned sessions
   - Click "Evaluate" to open evaluation form

2. **Submit Evaluation:**
   - Select a presenter from your assigned list
   - Enter scores for each rubric criterion (1-10):
     - Problem Clarity
     - Methodology
     - Results
     - Presentation
   - Add comments (optional)
   - View total score calculation
   - Click "Submit Evaluation"

### As Student (student1/stud123)

1. **Register for Seminar:**
   - Click "Register"
   - Fill in research details:
     - Research Title
     - Abstract
     - Supervisor Name
     - Presentation Type (Oral/Poster)
   - Click "Submit Registration"

2. **Upload Materials:**
   - Click "Upload Materials"
   - Click "Choose File"
   - Select your presentation file
   - Click "Upload"

---

## Navigation

### Menu Bar
- **File Menu:**
  - Save Data - Manually save all data
  - Exit - Close application (auto-saves)

- **Navigation Menu:**
  - Home - Return to your dashboard
  - Logout - Return to login screen

- **Help Menu:**
  - About - View application information

### Role-Based Dashboards
Each role has a dedicated dashboard with quick access to their functions:
- **Student:** Registration, Upload
- **Evaluator:** Assigned Sessions, Evaluation Form
- **Coordinator:** Sessions, Assignments, Posters, Awards, Reports

---

## Data Persistence

- **Auto-Save:** Data is automatically saved when you make changes
- **Save on Exit:** Data is saved when you close the application
- **Data File:** `seminar_data.ser` (in the application directory)
- **Restart:** Your data persists across application restarts

---

## Troubleshooting

### Application Won't Start
- Ensure Java 17 or higher is installed
- Check that Maven is accessible
- Try: `java -version` to verify Java installation

### Login Fails
- Verify you selected the correct role from dropdown
- Check username and password (case-sensitive)
- If using sample data, ensure you clicked "Yes" on first launch

### Data Not Saving
- Check file permissions in application directory
- Look for `seminar_data.ser` file
- Try manual save from File menu

### Can't See Assigned Sessions (Evaluator)
- Ensure coordinator has assigned you to sessions
- Logout and login again to refresh

### Can't Submit Evaluation
- Verify all scores are between 1-10
- Ensure you selected a presenter
- Check that you're assigned to evaluate that presenter

---

## System Requirements

- **Java:** Version 17 or higher
- **Operating System:** Windows (as configured)
- **Memory:** Minimum 512MB RAM
- **Disk Space:** 50MB

---

## Additional Resources

- **Full Verification Report:** See `SYSTEM_VERIFICATION_REPORT.md`
- **Requirements:** See `.kiro/specs/seminar-management-system/requirements.md`
- **Design Document:** See `.kiro/specs/seminar-management-system/design.md`
- **Implementation Tasks:** See `.kiro/specs/seminar-management-system/tasks.md`

---

## Support

For issues or questions:
1. Check the verification report for system status
2. Review the requirements and design documents
3. Verify all tests are passing: `apache-maven-3.9.12\bin\mvn.cmd test`

---

**Enjoy using the FCI Seminar Management System!**
