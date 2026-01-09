# Seminar Management System

A comprehensive Java Swing-based desktop application for managing the Faculty of Computing and Informatics (FCI) Postgraduate Academic Research Seminar.

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3.9.12-blue)
![Tests](https://img.shields.io/badge/Tests-42%20Passing-brightgreen)
![Status](https://img.shields.io/badge/Status-Production%20Ready-success)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Quick Start](#quick-start)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [User Roles](#user-roles)
- [Sample Accounts](#sample-accounts)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Documentation](#documentation)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## 🎯 Overview

The Seminar Management System streamlines the organization and management of academic research seminars. It supports three user roles (Student, Evaluator, Coordinator) and provides comprehensive functionality for:

- Student registration and presentation management
- Session scheduling and assignment
- Evaluation and scoring with rubric-based criteria
- Award computation and ceremony planning
- Report generation and analytics
- Data persistence across sessions

---

## ✨ Features

### For Students
- ✅ Register for seminars with research details
- ✅ Upload presentation materials (slides/posters)
- ✅ View registration status
- ✅ Track presentation type (Oral/Poster)

### For Evaluators
- ✅ View assigned sessions and presenters
- ✅ Submit evaluations using standardized rubrics
- ✅ Provide detailed feedback comments
- ✅ Track evaluation history

### For Coordinators
- ✅ Create and manage seminar sessions
- ✅ Assign presenters and evaluators to sessions
- ✅ Manage poster board assignments
- ✅ Detect scheduling conflicts automatically
- ✅ Compute award winners (Best Oral, Best Poster, People's Choice)
- ✅ Generate ceremony agendas
- ✅ Generate comprehensive reports (Schedule, Evaluation, Summary)
- ✅ Export reports to files

### System Features
- ✅ Role-based authentication
- ✅ Automatic data persistence (serialization)
- ✅ Auto-save functionality
- ✅ Conflict detection for double-booking
- ✅ Input validation and error handling
- ✅ Property-based testing (42 tests, 100% passing)

---

## 🚀 Quick Start

### Run the Application

**Double-click:** `run-seminar-app.bat`

**Steps:**
1. Double-click the batch file
2. Wait for compilation (first time takes longer)
3. The GUI window will open
4. Click **Yes** when prompted to load sample data
5. Log in with a sample account (see below)

**Important:** Keep the CMD window open while using the application!

### Sample Accounts

| Role | Username | Password |
|------|----------|----------|
| Coordinator | `admin` | `admin123` |
| Evaluator | `eval1` or `eval2` | `eval123` |
| Student | `student1` to `student4` | `stud123` |

---

## 💻 System Requirements

### Runtime Requirements
- **Java:** Version 17 or higher
- **Operating System:** Windows (configured for Windows, adaptable to other OS)
- **Memory:** Minimum 512MB RAM
- **Disk Space:** 50MB

### Build Requirements
- **Maven:** 3.9.12 (included in project)
- **Java Development Kit (JDK):** Version 17 or higher

---

## 📦 Installation

### Prerequisites

1. **Install Java 17 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation: `java -version`

2. **Verify Maven** (included in project)
   - Maven is bundled in `apache-maven-3.9.12` directory
   - No separate installation needed

### Setup

1. **Clone or download** this repository
2. **Navigate** to the project directory
3. **Compile** the project (first time only):
   ```cmd
   apache-maven-3.9.12\bin\mvn.cmd compile
   ```

---

## 🎮 Running the Application

### Method 1: Batch File (Recommended)

Simply **double-click** `run-seminar-app.bat`

### Method 2: Maven Command

```cmd
apache-maven-3.9.12\bin\mvn.cmd exec:java -Dexec.mainClass="com.fci.seminar.ui.SeminarApp"
```

### Method 3: Build JAR and Run

```cmd
apache-maven-3.9.12\bin\mvn.cmd clean package
java -jar target\seminar-management-system-1.0-SNAPSHOT.jar
```

### Method 4: Direct Java Execution

```cmd
apache-maven-3.9.12\bin\mvn.cmd compile
java -cp target\classes com.fci.seminar.ui.SeminarApp
```

---

## 👥 User Roles

### Student
Students register for the seminar, provide research details, and upload presentation materials.

**Key Actions:**
- Register with research title, abstract, supervisor
- Select presentation type (Oral or Poster)
- Upload presentation files
- View registration status

### Evaluator
Evaluators assess presentations using standardized rubric criteria.

**Key Actions:**
- View assigned sessions
- Evaluate presentations with 4 rubric criteria (1-10 scale):
  - Problem Clarity
  - Methodology
  - Results
  - Presentation
- Provide feedback comments
- View evaluation history

### Coordinator
Coordinators manage all aspects of the seminar organization.

**Key Actions:**
- Create and manage sessions (date, venue, type)
- Assign presenters and evaluators to sessions
- Manage poster board assignments
- Compute award winners
- Generate ceremony agendas
- Generate and export reports

---

## 🧪 Testing

### Run All Tests

```cmd
apache-maven-3.9.12\bin\mvn.cmd test
```

### Test Coverage

- **Total Tests:** 42 property-based tests
- **Pass Rate:** 100%
- **Framework:** jqwik (Java property-based testing)
- **Iterations:** Minimum 100 per test

### Test Categories

- ✅ Authentication and authorization
- ✅ Registration validation
- ✅ Session management
- ✅ Conflict detection
- ✅ Score validation
- ✅ Award computation
- ✅ Data persistence (round-trip)
- ✅ Report completeness
- ✅ Unique ID generation
- ✅ Board assignment uniqueness

---

## 📁 Project Structure

```
seminar-management-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/fci/seminar/
│   │           ├── model/          # Data models (User, Session, Evaluation, etc.)
│   │           ├── service/        # Business logic services
│   │           ├── ui/             # Swing UI components
│   │           └── util/           # Utility classes
│   └── test/
│       └── java/
│           └── com/fci/seminar/
│               └── property/       # Property-based tests
├── apache-maven-3.9.12/           # Bundled Maven
├── .kiro/
│   └── specs/
│       └── seminar-management-system/
│           ├── requirements.md     # System requirements
│           ├── design.md          # Design document
│           └── tasks.md           # Implementation tasks
├── pom.xml                        # Maven configuration
├── run-seminar-app.bat           # Launch script
├── README.md                      # This file
├── QUICK_START_GUIDE.md          # Quick start instructions
└── SYSTEM_VERIFICATION_REPORT.md # Verification report
```

---

## 📚 Documentation

### Core Documentation

- **[README.md](README.md)** - This file (overview and setup)
- **[QUICK_START_GUIDE.md](QUICK_START_GUIDE.md)** - Step-by-step usage guide
- **[SYSTEM_VERIFICATION_REPORT.md](SYSTEM_VERIFICATION_REPORT.md)** - Complete verification report

### Specification Documents

- **[requirements.md](.kiro/specs/seminar-management-system/requirements.md)** - Detailed requirements (EARS format)
- **[design.md](.kiro/specs/seminar-management-system/design.md)** - System design and architecture
- **[tasks.md](.kiro/specs/seminar-management-system/tasks.md)** - Implementation task breakdown

---

## 🔧 Troubleshooting

### Application Won't Start

**Problem:** Batch file shows error or application doesn't launch

**Solutions:**
1. Verify Java installation: `java -version`
2. Ensure Java 17 or higher is installed
3. Try compiling first: `apache-maven-3.9.12\bin\mvn.cmd compile`
4. Check console output for specific error messages

### Login Fails

**Problem:** Cannot log in with credentials

**Solutions:**
1. Ensure you selected the correct **role** from dropdown
2. Verify username and password (case-sensitive)
3. If using sample data, ensure you clicked "Yes" on first launch
4. Try restarting the application

### Data Not Persisting

**Problem:** Changes are lost after restart

**Solutions:**
1. Check for `seminar_data.ser` file in application directory
2. Verify file permissions (read/write access)
3. Try manual save from File menu
4. Check console for error messages

### Can't See Assigned Sessions (Evaluator)

**Problem:** Evaluator dashboard shows no assignments

**Solutions:**
1. Ensure coordinator has assigned you to sessions
2. Logout and login again to refresh
3. Verify assignments in coordinator view

### Evaluation Submission Fails

**Problem:** Cannot submit evaluation

**Solutions:**
1. Verify all scores are between 1-10
2. Ensure you selected a presenter
3. Check that you're assigned to evaluate that presenter
4. Verify all required fields are filled

### Build Errors

**Problem:** Maven build fails

**Solutions:**
1. Clean and rebuild: `apache-maven-3.9.12\bin\mvn.cmd clean compile`
2. Verify Java version: `java -version` (must be 17+)
3. Check internet connection (Maven may need to download dependencies)
4. Delete `target` folder and rebuild

---

## 🏗️ Architecture

### Design Pattern
- **MVC-inspired architecture** with clear separation of concerns
- **Model:** Data classes (User, Session, Evaluation, Award, etc.)
- **View:** Java Swing panels and frames
- **Controller:** Service classes handling business logic

### Key Components

#### Model Layer
- User hierarchy (Student, Evaluator, Coordinator)
- Session and PosterBoard management
- Evaluation and RubricScores
- Award and CeremonyAgenda

#### Service Layer
- UserService - Authentication and registration
- SessionService - Session CRUD and assignments
- EvaluationService - Evaluation management
- PosterBoardService - Board assignments
- AwardService - Winner computation
- ReportService - Report generation

#### UI Layer
- LoginPanel - Authentication
- Role-specific dashboards (Student, Evaluator, Coordinator)
- Feature-specific panels (Registration, Evaluation, Sessions, etc.)

#### Data Layer
- DataStore - Central data repository
- Serialization-based persistence
- Auto-save functionality

---

## 🎓 Usage Examples

### Example 1: Coordinator Creates a Session

1. Log in as coordinator (`admin`/`admin123`)
2. Click **"Session Management"**
3. Fill in session details:
   - Date: `2026-01-20`
   - Venue: `Conference Room A`
   - Type: `Oral`
4. Click **"Create Session"**
5. Session appears in the table

### Example 2: Coordinator Assigns Presenter

1. From coordinator dashboard, click **"Assignments"**
2. Select session from dropdown
3. Select student from "Available Presenters" list
4. Click **"Assign Presenter"**
5. Student appears in "Assigned Presenters" list

### Example 3: Evaluator Submits Evaluation

1. Log in as evaluator (`eval1`/`eval123`)
2. Click **"Evaluate"** from dashboard
3. Select presenter from dropdown
4. Enter scores (1-10) for each criterion:
   - Problem Clarity: `8`
   - Methodology: `7`
   - Results: `9`
   - Presentation: `8`
5. Add comments: `"Excellent research with clear methodology"`
6. View total score: `32`
7. Click **"Submit Evaluation"**

### Example 4: Coordinator Generates Report

1. Log in as coordinator
2. Click **"Reports"**
3. Click **"Schedule Report"** to view all sessions
4. Click **"Export"** to save to file
5. Choose location and filename
6. Report saved successfully

---

## 🔒 Data Persistence

### Storage Format
- **File:** `seminar_data.ser` (Java serialized object)
- **Location:** Application root directory
- **Format:** Binary serialization

### What's Saved
- All user accounts (Students, Evaluators, Coordinators)
- All sessions with assignments
- All evaluations and scores
- All poster board assignments
- All awards

### Auto-Save
- Data is automatically saved when changes occur
- Data is saved when application closes
- Manual save available from File menu

---

## 🧩 Dependencies

### Runtime Dependencies
- Java Swing (included in JDK)
- Java Serialization (included in JDK)

### Test Dependencies
- JUnit 5 (5.10.0)
- jqwik (1.7.4) - Property-based testing

### Build Tools
- Maven 3.9.12 (bundled)
- Maven Compiler Plugin 3.11.0
- Maven Surefire Plugin 3.1.2

---

## 📊 Statistics

- **Lines of Code:** ~3,500+ (excluding tests)
- **Classes:** 36 source files
- **Tests:** 42 property-based tests
- **Test Coverage:** 100% of correctness properties
- **Build Time:** ~5 seconds
- **Test Execution Time:** ~25 seconds

---

## 🤝 Contributing

This project was developed as part of the FCI academic program. For modifications or enhancements:

1. Review the specification documents in `.kiro/specs/`
2. Follow the existing architecture and design patterns
3. Ensure all tests pass before committing changes
4. Update documentation as needed

---

## 📝 License

This project is developed for academic purposes at the Faculty of Computing and Informatics (FCI).

---

## 👨‍💻 Development

### Build Commands

```cmd
# Clean build
apache-maven-3.9.12\bin\mvn.cmd clean

# Compile
apache-maven-3.9.12\bin\mvn.cmd compile

# Run tests
apache-maven-3.9.12\bin\mvn.cmd test

# Package JAR
apache-maven-3.9.12\bin\mvn.cmd package

# Clean and package
apache-maven-3.9.12\bin\mvn.cmd clean package
```

### Code Quality

- Property-based testing ensures correctness across all inputs
- Input validation on all user inputs
- Error handling with user-friendly messages
- Consistent code style and documentation
- Serializable data models for persistence

---

## 📞 Support

For issues or questions:

1. Check the [QUICK_START_GUIDE.md](QUICK_START_GUIDE.md) for usage instructions
2. Review the [SYSTEM_VERIFICATION_REPORT.md](SYSTEM_VERIFICATION_REPORT.md) for system status
3. Check the [Troubleshooting](#troubleshooting) section above
4. Review specification documents in `.kiro/specs/`

---

## 🎉 Acknowledgments

- Faculty of Computing and Informatics (FCI)
- Java Swing framework
- jqwik property-based testing library
- Maven build system

---

## 📅 Version History

### Version 1.0-SNAPSHOT (Current)
- ✅ Complete implementation of all requirements
- ✅ All 42 property-based tests passing
- ✅ Full UI implementation with 12 panels
- ✅ Data persistence with serialization
- ✅ Sample data for testing
- ✅ Comprehensive documentation

---

**Ready to get started? Double-click `run-seminar-app.bat` to launch the application!**

---

*Last Updated: January 9, 2026*
