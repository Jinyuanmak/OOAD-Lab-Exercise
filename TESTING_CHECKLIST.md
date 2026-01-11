# Seminar Management System - Testing Checklist

**Date:** _____________  
**Tester:** _____________

Use this checklist to systematically test all features and validations in the system.

---

## 🔐 PART 1: USER MANAGEMENT & AUTHENTICATION

### Test 1.1: Valid Login - Coordinator (Auto-detect Role)
- [ ] Username: `admin`
- [ ] Password: `admin123`
- [ ] Click "Login"
- [ ] **Expected:** Successfully logged in, Coordinator Dashboard appears (role auto-detected)
- [ ] **Result:** ☐ Pass ☐ Fail
- [ ] **Notes:** _______________________________________________

### Test 1.2: Valid Login - Evaluator (Auto-detect Role)
- [ ] Logout first (Navigation → Logout)
- [ ] Username: `eval1`
- [ ] Password: `eval123`
- [ ] Click "Login"
- [ ] **Expected:** Successfully logged in, Evaluator Dashboard appears with "Welcome, eval1!"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 1.3: Valid Login - Student (Auto-detect Role)
- [ ] Logout first
- [ ] Username: `student1`
- [ ] Password: `stud123`
- [ ] Click "Login"
- [ ] **Expected:** Successfully logged in, Student Dashboard appears
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 1.4: Invalid Login - Wrong Password
- [ ] Logout first
- [ ] Username: `admin`
- [ ] Password: `wrongpassword`
- [ ] Click "Login"
- [ ] **Expected:** Error message "Invalid username or password"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 1.5: Invalid Login - Empty Fields
- [ ] Leave username empty
- [ ] Password: `admin123`
- [ ] Click "Login"
- [ ] **Expected:** Error message about empty fields
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 1.6: Invalid Login - Non-existent User
- [ ] Username: `nonexistent`
- [ ] Password: `password123`
- [ ] Click "Login"
- [ ] **Expected:** Error message "Invalid username or password"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 1.7: Switch Between Users
- [ ] Login as `eval1` / `eval123`
- [ ] Verify welcome message shows "Welcome, eval1!"
- [ ] Logout
- [ ] Login as `eval2` / `eval123`
- [ ] **Expected:** Welcome message immediately shows "Welcome, eval2!" (no refresh needed)
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 👨‍🎓 PART 2: STUDENT MODULE

**Login as:** student1 / stud123

### Test 2.1: Student Registration - Valid Data
- [ ] Click "Register for Seminar" from Student Dashboard
- [ ] Research Title: `Machine Learning for Healthcare Diagnostics`
- [ ] Abstract: `This research explores the application of machine learning algorithms in healthcare diagnostics to improve early disease detection and patient outcomes.`
- [ ] Supervisor Name: `Dr. Ahmad Ibrahim`
- [ ] Presentation Type: Select "Oral"
- [ ] Click "Submit Registration"
- [ ] **Expected:** Success message, registration confirmed
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.2: Registration - Poster Presentation Type
- [ ] Login as `student2` / `stud123`
- [ ] Click "Register for Seminar"
- [ ] Research Title: `IoT Security Framework`
- [ ] Abstract: `A comprehensive framework for securing Internet of Things devices in smart home environments.`
- [ ] Supervisor Name: `Dr. Sarah Lee`
- [ ] Presentation Type: Select "Poster"
- [ ] Click "Submit Registration"
- [ ] **Expected:** Success message, student registered as poster presenter
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.3: Registration Validation - Empty Title
- [ ] Click "Register for Seminar"
- [ ] Research Title: (leave empty)
- [ ] Abstract: `Valid abstract text`
- [ ] Supervisor Name: `Dr. Smith`
- [ ] Presentation Type: Select "Oral"
- [ ] Click "Submit Registration"
- [ ] **Expected:** Error message "Research title is required"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.4: Registration Validation - Empty Abstract
- [ ] Research Title: `Valid Title`
- [ ] Abstract: (leave empty)
- [ ] Supervisor Name: `Dr. Smith`
- [ ] Presentation Type: Select "Oral"
- [ ] Click "Submit Registration"
- [ ] **Expected:** Error message "Abstract is required"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.5: Registration Validation - Empty Supervisor
- [ ] Research Title: `Valid Title`
- [ ] Abstract: `Valid abstract text`
- [ ] Supervisor Name: (leave empty)
- [ ] Presentation Type: Select "Oral"
- [ ] Click "Submit Registration"
- [ ] **Expected:** Error message "Supervisor name is required"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.6: Registration Validation - No Presentation Type
- [ ] Research Title: `Valid Title`
- [ ] Abstract: `Valid abstract text`
- [ ] Supervisor Name: `Dr. Smith`
- [ ] Presentation Type: (don't select)
- [ ] Click "Submit Registration"
- [ ] **Expected:** Error message "Presentation type is required"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.7: File Upload - Valid File (Slides/Poster)
- [ ] Click "Upload Materials" from Student Dashboard
- [ ] Click "Choose File"
- [ ] Select a presentation file (e.g., .pptx, .pdf)
- [ ] Click "Upload"
- [ ] **Expected:** Success message with filename displayed
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.8: File Upload - No File Selected
- [ ] Click "Upload Materials"
- [ ] Don't select any file
- [ ] Click "Upload"
- [ ] **Expected:** Error message "Please select a file"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.9: File Upload - Replace Existing File
- [ ] Upload a file first
- [ ] Click "Upload Materials" again
- [ ] Select a different file
- [ ] Click "Upload"
- [ ] **Expected:** Success message, old file replaced with new file
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 2.10: View Registration Status
- [ ] After registration, check Student Dashboard
- [ ] **Expected:** Dashboard shows registration details (title, type, presenter ID)
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 👨‍💼 PART 3: SESSION MANAGEMENT (Coordinator)

**Login as:** admin / admin123

### Test 3.1: Create Oral Session - Valid Data
- [ ] Click "Session Management"
- [ ] Date: `2026-02-15` (format: yyyy-MM-dd)
- [ ] Venue: `Auditorium A`
- [ ] Session Type: Select "Oral"
- [ ] Click "Create Session"
- [ ] **Expected:** Success message, session appears in table
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 3.2: Create Poster Session - Valid Data
- [ ] Date: `2026-02-16`
- [ ] Venue: `Exhibition Hall B`
- [ ] Session Type: Select "Poster"
- [ ] Click "Create Session"
- [ ] **Expected:** Success message, poster session created
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 3.3: Create Session - Invalid Date Format
- [ ] Date: `15/02/2026` (wrong format)
- [ ] Venue: `Room A`
- [ ] Session Type: Select "Oral"
- [ ] Click "Create Session"
- [ ] **Expected:** Error message about date format (use yyyy-MM-dd)
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 3.4: Create Session - Empty Venue
- [ ] Date: `2026-02-20`
- [ ] Venue: (leave empty)
- [ ] Session Type: Select "Oral"
- [ ] Click "Create Session"
- [ ] **Expected:** Error message "Venue is required"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 3.5: Create Session - No Session Type
- [ ] Date: `2026-02-20`
- [ ] Venue: `Room B`
- [ ] Session Type: (don't select)
- [ ] Click "Create Session"
- [ ] **Expected:** Error message "Session type is required"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 3.6: Edit Session
- [ ] Select a session from the table
- [ ] Click "Edit"
- [ ] Change venue to `Updated Conference Room`
- [ ] Click "Update"
- [ ] **Expected:** Success message, changes reflected in table
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 3.7: Delete Session - With Confirmation
- [ ] Select a session with NO assignments
- [ ] Click "Delete"
- [ ] **Expected:** Confirmation dialog appears
- [ ] Click "Yes"
- [ ] **Expected:** Session removed from table
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 3.8: Delete Session - Cancel Deletion
- [ ] Select a session
- [ ] Click "Delete"
- [ ] Click "No" on confirmation dialog
- [ ] **Expected:** Session remains in table
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 👥 PART 4: ASSIGNMENT MANAGEMENT (Coordinator)

**Login as:** admin / admin123

### Test 4.1: Assign Presenter to Session
- [ ] Click "Assignments"
- [ ] Select a session from dropdown
- [ ] Select a registered student from "Available Presenters" list
- [ ] Click "Assign Presenter"
- [ ] **Expected:** Student moves to "Assigned Presenters" list
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 4.2: Assign Evaluator to Session
- [ ] Select a session
- [ ] Select an evaluator from "Available Evaluators" list
- [ ] Click "Assign Evaluator"
- [ ] **Expected:** Evaluator moves to "Assigned Evaluators" list
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 4.3: Unassign Presenter
- [ ] Select a session with assigned presenters
- [ ] Select a presenter from "Assigned Presenters" list
- [ ] Click "Unassign Presenter"
- [ ] **Expected:** Presenter moves back to "Available Presenters"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 4.4: Unassign Evaluator
- [ ] Select a session with assigned evaluators
- [ ] Select an evaluator from "Assigned Evaluators" list
- [ ] Click "Unassign Evaluator"
- [ ] **Expected:** Evaluator moves back to "Available Evaluators"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 4.5: Multiple Presenters Per Session
- [ ] Select a session
- [ ] Assign Presenter 1
- [ ] Assign Presenter 2
- [ ] Assign Presenter 3
- [ ] **Expected:** All three presenters assigned successfully
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 4.6: Multiple Evaluators Per Session
- [ ] Select a session
- [ ] Assign eval1
- [ ] Assign eval2
- [ ] **Expected:** Both evaluators assigned successfully
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 4.7: Verify Assignment Display
- [ ] After assignments, check the table
- [ ] **Expected:** Shows session with venue, date, type, and assigned names (not IDs)
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 📋 PART 5: POSTER PRESENTATION MANAGEMENT (Coordinator)

**Login as:** admin / admin123

### Test 5.1: View Poster Boards
- [ ] Click "Poster Management"
- [ ] **Expected:** Table shows available boards (B001-B020)
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 5.2: Assign Poster Board - Valid
- [ ] Board ID: Select `B001`
- [ ] Presenter: Select a poster presenter
- [ ] Session: Select a poster session
- [ ] Click "Assign Board"
- [ ] **Expected:** Success message, assignment appears in table
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 5.3: Duplicate Board Assignment - Rejected
- [ ] Assign Board B001 to Presenter 1
- [ ] Try to assign Board B001 to Presenter 2
- [ ] **Expected:** Error message "Board already assigned"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 5.4: View Available Boards
- [ ] After assigning some boards
- [ ] Check available boards list
- [ ] **Expected:** Assigned boards not shown in available list
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 5.5: Assign Multiple Boards
- [ ] Assign Board B002 to Presenter 2
- [ ] Assign Board B003 to Presenter 3
- [ ] **Expected:** All assignments successful, table updated
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 5.6: Refresh Poster Management
- [ ] Click "Refresh" button
- [ ] **Expected:** Table refreshes without errors
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 🎓 PART 6: EVALUATION MODULE (Evaluator)

**Login as:** eval1 / eval123

### Test 6.1: View Assigned Sessions
- [ ] Check Evaluator Dashboard
- [ ] **Expected:** Welcome message shows "Welcome, eval1!"
- [ ] **Expected:** List of assigned sessions and presenters displayed
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.2: Submit Evaluation - Valid Scores (Rubric-based)
- [ ] Click "Evaluate Selected Presenter"
- [ ] Select a presenter
- [ ] Problem Clarity: `8` (1-10)
- [ ] Methodology: `9` (1-10)
- [ ] Results: `7` (1-10)
- [ ] Presentation: `8` (1-10)
- [ ] Comments: `Excellent research methodology and clear presentation of results.`
- [ ] Click "Submit Evaluation"
- [ ] **Expected:** Success message, total score displayed (32/40)
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.3: Score Validation - Below Minimum (0)
- [ ] Select a presenter
- [ ] Problem Clarity: `0` (invalid - below minimum)
- [ ] Other scores: `5`
- [ ] Click "Submit Evaluation"
- [ ] **Expected:** Error message "Scores must be between 1 and 10"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.4: Score Validation - Above Maximum (11)
- [ ] Select a presenter
- [ ] Problem Clarity: `11` (invalid - above maximum)
- [ ] Other scores: `5`
- [ ] Click "Submit Evaluation"
- [ ] **Expected:** Error message "Scores must be between 1 and 10"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.5: Score Validation - Minimum Valid (1)
- [ ] All scores: `1`
- [ ] Click "Submit Evaluation"
- [ ] **Expected:** Success, total score = 4/40
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.6: Score Validation - Maximum Valid (10)
- [ ] All scores: `10`
- [ ] Click "Submit Evaluation"
- [ ] **Expected:** Success, total score = 40/40
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.7: Total Score Calculation
- [ ] Problem Clarity: `7`
- [ ] Methodology: `8`
- [ ] Results: `6`
- [ ] Presentation: `9`
- [ ] **Expected:** Total score displays as 30/40 (7+8+6+9)
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.8: Submit Evaluation - With Comments
- [ ] Enter valid scores
- [ ] Comments: `Good work on methodology. Consider improving the results visualization.`
- [ ] Click "Submit Evaluation"
- [ ] **Expected:** Success, comments saved with evaluation
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.9: Submit Evaluation - No Presenter Selected
- [ ] Don't select a presenter
- [ ] Enter valid scores
- [ ] Click "Submit Evaluation"
- [ ] **Expected:** Error message "Please select a presenter"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 6.10: Multiple Evaluators - Same Presenter
- [ ] Login as eval1, submit evaluation for Presenter 1
- [ ] Logout, login as eval2
- [ ] Submit evaluation for same Presenter 1
- [ ] **Expected:** Both evaluations recorded separately
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 🏆 PART 7: AWARD & CEREMONY MODULE (Coordinator)

**Login as:** admin / admin123

### Test 7.1: Compute Best Oral Presentation Winner
- [ ] Click "Awards"
- [ ] Click "Compute Winners"
- [ ] **Expected:** Best Oral presenter displayed with highest average score
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 7.2: Compute Best Poster Presentation Winner
- [ ] Click "Compute Winners"
- [ ] **Expected:** Best Poster presenter displayed with highest average score
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 7.3: People's Choice Award - Vote Entry
- [ ] Select a presenter from dropdown
- [ ] Enter vote count (e.g., `25`)
- [ ] Click "Record Vote"
- [ ] **Expected:** Vote recorded successfully
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 7.4: People's Choice Award - Compute Winner
- [ ] Record votes for multiple presenters
- [ ] Click "Compute People's Choice"
- [ ] **Expected:** Winner with most votes displayed
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 7.5: Generate Ceremony Agenda
- [ ] Click "Generate Agenda"
- [ ] **Expected:** Formatted agenda with:
  - Best Oral Presentation winner
  - Best Poster Presentation winner
  - People's Choice Award winner
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 7.6: Awards with No Evaluations
- [ ] Before any evaluations submitted
- [ ] Click "Compute Winners"
- [ ] **Expected:** Message "No evaluations available" or empty results
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 7.7: Verify Winner Accuracy
- [ ] Check evaluation scores manually
- [ ] Compare with computed winner
- [ ] **Expected:** Winner has highest average score for their category
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 📊 PART 8: REPORTS & SUMMARY (Coordinator)

**Login as:** admin / admin123

### Test 8.1: Generate Schedule Report
- [ ] Click "Reports"
- [ ] Click "Schedule Report"
- [ ] **Expected:** Report shows all sessions with:
  - Date, Venue, Session Type
  - Assigned presenters and evaluators
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 8.2: Generate Evaluation Report
- [ ] Click "Evaluation Report"
- [ ] **Expected:** Report shows all evaluations with:
  - Presenter name
  - Evaluator name
  - Individual rubric scores
  - Total score
  - Comments
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 8.3: Generate Summary Report
- [ ] Click "Summary"
- [ ] **Expected:** Report shows:
  - Total number of presenters
  - Total number of sessions (Oral/Poster breakdown)
  - Total number of evaluations
  - Average scores by category
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 8.4: Export Report to File
- [ ] Generate any report
- [ ] Click "Export"
- [ ] Choose save location
- [ ] Enter filename: `seminar_report.txt`
- [ ] Click "Save"
- [ ] **Expected:** Success message, file created
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 8.5: Verify Exported Report Content
- [ ] Open exported file
- [ ] **Expected:** File contains complete report data matching screen display
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 8.6: Data Analytics - Score Distribution
- [ ] Generate Evaluation Report
- [ ] **Expected:** Shows score statistics (average, min, max)
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 💾 PART 9: DATA PERSISTENCE

### Test 9.1: Auto-Save on Data Change
- [ ] Create a new session
- [ ] Check if data is saved automatically
- [ ] **Expected:** Data persisted without manual save
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 9.2: Manual Save
- [ ] Make any change
- [ ] Click File → Save Data
- [ ] **Expected:** Success message or confirmation
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 9.3: Data Persistence Across Restarts
- [ ] Create a unique session (note the details)
- [ ] Close application
- [ ] Restart application
- [ ] Login and check for the session
- [ ] **Expected:** Session still exists with same details
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 9.4: Save on Exit
- [ ] Make a change
- [ ] Close application (X button)
- [ ] Restart application
- [ ] **Expected:** Change is saved
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 9.5: Load Existing Data
- [ ] Ensure data file exists
- [ ] Start application
- [ ] **Expected:** No sample data prompt, existing data loaded
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 9.6: Fresh Start (No Data File)
- [ ] Delete `seminar_data.ser`
- [ ] Start application
- [ ] **Expected:** Sample data prompt appears
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 🔄 PART 10: NAVIGATION & UI

### Test 10.1: Navigation Menu - Home
- [ ] Login as any user
- [ ] Navigate to different panels
- [ ] Click Navigation → Home
- [ ] **Expected:** Returns to appropriate dashboard for user role
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 10.2: Navigation Menu - Logout
- [ ] Login as any user
- [ ] Click Navigation → Logout
- [ ] **Expected:** Returns to login screen, user logged out
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 10.3: File Menu - Save
- [ ] Click File → Save Data
- [ ] **Expected:** Data saved successfully
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 10.4: File Menu - Exit
- [ ] Click File → Exit
- [ ] **Expected:** Application closes, data saved
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 10.5: Help Menu - About
- [ ] Click Help → About
- [ ] **Expected:** About dialog with system information
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 10.6: Window Title Updates
- [ ] Login as admin
- [ ] **Expected:** Title shows "FCI Seminar Management System - admin (COORDINATOR)"
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 10.7: Clean UI Display (No IDs)
- [ ] Check all lists and tables
- [ ] **Expected:** Shows friendly names (usernames, venue names) instead of IDs
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 🎯 PART 11: INTEGRATION TESTS

### Test 11.1: Complete Student Workflow
- [ ] Login as student
- [ ] Register with research title, abstract, supervisor, presentation type
- [ ] Upload presentation file
- [ ] Logout, login as coordinator
- [ ] Verify student appears in available presenters
- [ ] **Expected:** All steps work together seamlessly
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 11.2: Complete Coordinator Workflow
- [ ] Create oral session
- [ ] Create poster session
- [ ] Assign presenters to sessions
- [ ] Assign evaluators to sessions
- [ ] Assign poster boards
- [ ] Generate schedule report
- [ ] **Expected:** All features work together
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 11.3: Complete Evaluator Workflow
- [ ] Login as evaluator
- [ ] View assigned sessions and presenters
- [ ] Submit evaluations with rubric scores and comments
- [ ] Logout, login as coordinator
- [ ] Verify evaluations appear in reports
- [ ] **Expected:** Evaluation data flows correctly
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 11.4: End-to-End Award Workflow
- [ ] Create sessions (oral and poster)
- [ ] Assign presenters (oral and poster types)
- [ ] Assign evaluators
- [ ] Submit evaluations for all presenters
- [ ] Record People's Choice votes
- [ ] Compute all winners
- [ ] Generate ceremony agenda
- [ ] **Expected:** Winners computed correctly based on scores
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 11.5: Multi-User Scenario
- [ ] Login as coordinator, create session
- [ ] Logout, login as student1, register for seminar
- [ ] Logout, login as student2, register for seminar
- [ ] Logout, login as coordinator, assign students to session
- [ ] Logout, login as eval1, submit evaluations
- [ ] Logout, login as eval2, submit evaluations
- [ ] Logout, login as coordinator, view reports and compute awards
- [ ] **Expected:** Data persists across different user sessions
- [ ] **Result:** ☐ Pass ☐ Fail

---

## ⚠️ PART 12: ERROR HANDLING

### Test 12.1: Invalid Date Input
- [ ] Try entering date as `abc123`
- [ ] **Expected:** Error message or validation prevents input
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 12.2: Special Characters in Text Fields
- [ ] Enter special characters in title field
- [ ] **Expected:** Handled gracefully
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 12.3: Very Long Text Input
- [ ] Enter text exceeding limits (>200 chars for title, >2000 for abstract)
- [ ] **Expected:** Validation message about character limit
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 12.4: Null/Empty Selections
- [ ] Try submitting forms without selecting required dropdowns
- [ ] **Expected:** Error message for each required field
- [ ] **Result:** ☐ Pass ☐ Fail

### Test 12.5: Rapid Button Clicking
- [ ] Try rapid clicking on buttons
- [ ] **Expected:** No crashes, operations handled properly
- [ ] **Result:** ☐ Pass ☐ Fail

---

## 📝 SUMMARY

**Total Tests:** 95+  
**Tests Passed:** _____  
**Tests Failed:** _____  
**Pass Rate:** _____%

### Requirements Coverage:

#### Student (Presenter) ✓
- [ ] Registers with research title, abstract, supervisor name, presentation type
- [ ] Uploads presentation materials (slides/poster file path)

#### Evaluator (Panel Member) ✓
- [ ] Reviews assigned presentations
- [ ] Provides evaluation based on rubrics (Problem Clarity, Methodology, Results, Presentation)
- [ ] Adds comments and marks for each presenter

#### Coordinator (Faculty Staff) ✓
- [ ] Creates and manages seminar sessions (date, venue, session type)
- [ ] Assigns evaluators and presenters to sessions
- [ ] Generates seminar schedules and evaluation reports
- [ ] Oversees award nomination (Best Oral, Best Poster, People's Choice)

#### System Requirements ✓
- [ ] User Management with auto-detect role login
- [ ] Student Module for registration, preference, and file upload
- [ ] Session Management for creating and managing sessions
- [ ] Evaluation Module for scoring and commenting using rubrics
- [ ] Poster Presentation Management with board IDs
- [ ] Award & Ceremony Module for computing winners and generating agenda
- [ ] Reports & Summary with export options

### Critical Issues Found:
1. _______________________________________________
2. _______________________________________________
3. _______________________________________________

### Minor Issues Found:
1. _______________________________________________
2. _______________________________________________
3. _______________________________________________

### Recommendations:
_______________________________________________

### Overall Assessment:
☐ Ready for Production  
☐ Needs Minor Fixes  
☐ Needs Major Fixes  
☐ Not Ready

---

**Tester Signature:** _____________  
**Date Completed:** _____________

---

## 📌 LOGIN CREDENTIALS

| Role        | Username | Password |
|-------------|----------|----------|
| Coordinator | admin    | admin123 |
| Evaluator   | eval1    | eval123  |
| Evaluator   | eval2    | eval123  |
| Student     | student1 | stud123  |
| Student     | student2 | stud123  |
| Student     | student3 | stud123  |
| Student     | student4 | stud123  |

**Note:** Login auto-detects user role - no role selection needed.

---

**Good luck with testing! 🚀**
