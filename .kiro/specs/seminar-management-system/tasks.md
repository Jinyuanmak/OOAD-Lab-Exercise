# Implementation Plan: Seminar Management System

## Overview

This implementation plan breaks down the Seminar Management System into incremental coding tasks. Each task builds on previous work, ensuring no orphaned code. The system uses Java Swing for UI and file-based serialization for persistence.

## Tasks

- [x] 1. Set up project structure and core model classes
  - [x] 1.1 Create project directory structure and base packages
    - Create `src/main/java/com/fci/seminar/` with subpackages: model, service, ui, util
    - Create `src/test/java/com/fci/seminar/` with matching test packages
    - _Requirements: 10.4_

  - [x] 1.2 Implement User class hierarchy
    - Create abstract `User` class with id, username, password, role fields
    - Create `Student` class extending User with researchTitle, abstractText, supervisorName, presentationType, filePath, presenterId
    - Create `Evaluator` class extending User with assignedSessionIds list
    - Create `Coordinator` class extending User
    - Create `UserRole` enum (STUDENT, EVALUATOR, COORDINATOR)
    - Create `PresentationType` enum (ORAL, POSTER)
    - _Requirements: 1.1, 2.1, 2.2_

  - [x] 1.3 Implement Session and PosterBoard models
    - Create `Session` class with sessionId, date, venue, sessionType, presenterIds, evaluatorIds
    - Create `PosterBoard` class with boardId, presenterId, sessionId
    - _Requirements: 4.2, 7.1_

  - [x] 1.4 Implement Evaluation and RubricScores models
    - Create `RubricScores` class with problemClarity, methodology, results, presentation (1-10 each)
    - Implement `getTotalScore()` and `getWeightedScore()` methods
    - Create `Evaluation` class with evaluationId, presenterId, evaluatorId, sessionId, scores, comments, timestamp
    - _Requirements: 6.3, 6.6_

  - [x] 1.5 Implement Award and CeremonyAgenda models
    - Create `AwardType` enum (BEST_ORAL, BEST_POSTER, PEOPLES_CHOICE)
    - Create `Award` class with type, winnerId, score
    - Create `CeremonyAgenda` class with awards list and ceremonyDate
    - Implement `generateFormattedAgenda()` method
    - _Requirements: 8.1, 8.5_

  - [x] 1.6 Write property test for RubricScores calculation
    - **Property 7: Total Score Calculation**
    - **Validates: Requirements 6.6**

- [x] 2. Implement DataStore and persistence layer
  - [x] 2.1 Create DataStore class
    - Implement DataStore with Maps for users, sessions, evaluations, posterBoards
    - Add List for awards
    - Implement `save(String filepath)` method using ObjectOutputStream
    - Implement static `load(String filepath)` method using ObjectInputStream
    - _Requirements: 10.1, 10.2, 10.4_

  - [x] 2.2 Implement ID generation utilities
    - Create `IdGenerator` utility class
    - Implement unique ID generation for presenters, sessions, evaluations
    - Use UUID or timestamp-based approach
    - _Requirements: 2.5_

  - [x] 2.3 Write property test for data persistence round-trip
    - **Property 10: Data Persistence Round-Trip**
    - **Validates: Requirements 10.1, 10.2, 10.4**

  - [x] 2.4 Write property test for unique ID generation
    - **Property 3: Unique Presenter ID Generation**
    - **Validates: Requirements 2.5**

- [x] 3. Implement service layer
  - [x] 3.1 Implement UserService
    - Create `UserService` class with DataStore dependency
    - Implement `authenticate(username, password, role)` method
    - Implement `registerStudent(Student)` method with validation
    - Implement `getStudentById(id)`, `getAllStudents()`, `getAllEvaluators()` methods
    - _Requirements: 1.2, 1.3, 2.3, 2.4_

  - [x] 3.2 Write property test for authentication
    - **Property 1: Authentication Correctness**
    - **Validates: Requirements 1.2, 1.3**

  - [x] 3.3 Write property test for registration validation
    - **Property 2: Registration Validation**
    - **Validates: Requirements 2.3, 2.4**

  - [x] 3.4 Implement SessionService
    - Create `SessionService` class with DataStore dependency
    - Implement `createSession(date, venue, type)` with validation
    - Implement `updateSession(session)` and `deleteSession(sessionId)` methods
    - Implement `assignPresenter(sessionId, presenterId)` and `assignEvaluator(sessionId, evaluatorId)`
    - Implement `hasConflict(userId, date)` for double-booking detection
    - Implement `getAllSessions()` method
    - _Requirements: 4.2, 4.3, 4.4, 4.5, 5.2, 5.3, 5.4, 5.5_

  - [x] 3.5 Write property test for session validation
    - **Property 4: Session Validation**
    - **Validates: Requirements 4.2, 4.3**

  - [x] 3.6 Write property test for assignment conflict detection
    - **Property 5: Assignment Conflict Detection**
    - **Validates: Requirements 5.4**

  - [x] 3.7 Write property test for session deletion cascade
    - **Property 12: Session Deletion Cascade**
    - **Validates: Requirements 4.5**

  - [x] 3.8 Implement EvaluationService
    - Create `EvaluationService` class with DataStore dependency
    - Implement `submitEvaluation(evaluation)` with score validation
    - Implement `getEvaluationsForPresenter(presenterId)` method
    - Implement `getEvaluationsByEvaluator(evaluatorId)` method
    - Implement `calculateAverageScore(presenterId)` method
    - _Requirements: 6.4, 6.5_

  - [x] 3.9 Write property test for score range validation
    - **Property 6: Score Range Validation**
    - **Validates: Requirements 6.4**

  - [x] 3.10 Implement PosterBoardService
    - Create `PosterBoardService` class with DataStore dependency
    - Implement `assignBoard(boardId, presenterId, sessionId)` method
    - Implement `isBoardAssigned(boardId)` check
    - Implement `getAvailableBoards()` and `getAllAssignments()` methods
    - _Requirements: 7.2, 7.3_

  - [x] 3.11 Write property test for board assignment uniqueness
    - **Property 8: Board Assignment Uniqueness**
    - **Validates: Requirements 7.3**

  - [x] 3.12 Implement AwardService
    - Create `AwardService` class with DataStore and EvaluationService dependencies
    - Implement `computeBestOral()` - highest average score among oral presenters
    - Implement `computeBestPoster()` - highest average score among poster presenters
    - Implement `computePeoplesChoice(votes)` - based on vote count
    - Implement `generateAgenda()` method
    - _Requirements: 8.2, 8.3, 8.5_

  - [x] 3.13 Write property test for award winner computation
    - **Property 9: Award Winner Computation**
    - **Validates: Requirements 8.2, 8.3**

  - [x] 3.14 Implement ReportService
    - Create `ReportService` class with DataStore dependency
    - Implement `generateScheduleReport()` - all sessions with assignments
    - Implement `generateEvaluationReport()` - all scores and comments
    - Implement `generateSummaryReport()` - analytics with totals and averages
    - Implement `exportToFile(content, filename)` method
    - _Requirements: 9.2, 9.3, 9.4, 9.5_

  - [x] 3.15 Write property test for report completeness
    - **Property 11: Report Completeness**
    - **Validates: Requirements 9.2, 9.3**

- [x] 4. Checkpoint - Verify service layer
  - Ensure all service tests pass
  - Verify data persistence works correctly
  - Ask the user if questions arise

- [x] 5. Implement main application frame and navigation
  - [x] 5.1 Create SeminarApp main frame
    - Create `SeminarApp` class extending JFrame
    - Set up CardLayout for panel switching
    - Initialize DataStore and load existing data
    - Create menu bar with navigation options
    - Implement `showPanel(panelName)` method
    - Implement `setCurrentUser(user)` and `getCurrentUser()` methods
    - _Requirements: 1.1, 1.4_

  - [x] 5.2 Create ErrorHandler utility
    - Create `ErrorHandler` class with static methods
    - Implement `showError(parent, message)` using JOptionPane
    - Implement `showWarning(parent, message)` method
    - Implement `confirmAction(parent, message)` method
    - _Requirements: 1.3, 2.4_

- [x] 6. Implement Login and User Management UI
  - [x] 6.1 Create LoginPanel
    - Create `LoginPanel` class extending JPanel
    - Add username and password text fields
    - Add role selection combo box (Student, Evaluator, Coordinator)
    - Add login button with action listener
    - Integrate with UserService for authentication
    - Navigate to appropriate dashboard on success
    - Display error on failure
    - _Requirements: 1.1, 1.2, 1.3_

- [x] 7. Implement Student Module UI
  - [x] 7.1 Create StudentDashboard panel
    - Create `StudentDashboard` class extending JPanel
    - Add welcome message with student name
    - Add navigation buttons for Registration and Upload
    - _Requirements: 2.1_

  - [x] 7.2 Create StudentRegistrationPanel
    - Create `StudentRegistrationPanel` class extending JPanel
    - Add text fields for research title, abstract (JTextArea), supervisor name
    - Add combo box for presentation type (Oral/Poster)
    - Add submit button with validation
    - Integrate with UserService for registration
    - Display success/error messages
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

  - [x] 7.3 Create FileUploadPanel
    - Create `FileUploadPanel` class extending JPanel
    - Add JFileChooser for file selection
    - Display current file path if exists
    - Add upload button to save file reference
    - Show confirmation on success
    - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [x] 8. Implement Coordinator Module UI
  - [x] 8.1 Create CoordinatorDashboard panel
    - Create `CoordinatorDashboard` class extending JPanel
    - Add navigation buttons for Sessions, Assignments, Posters, Awards, Reports
    - _Requirements: 4.1_

  - [x] 8.2 Create SessionManagementPanel
    - Create `SessionManagementPanel` class extending JPanel
    - Add JTable to display existing sessions
    - Add form fields for date (JDatePicker or text), venue, session type
    - Add Create, Edit, Delete buttons
    - Integrate with SessionService
    - Handle cascade deletion with confirmation
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

  - [x] 8.3 Create AssignmentPanel
    - Create `AssignmentPanel` class extending JPanel
    - Add session selector combo box
    - Add two JLists: available presenters and available evaluators
    - Add assign/unassign buttons for both
    - Display current assignments for selected session
    - Show conflict warnings
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

  - [x] 8.4 Create PosterManagementPanel
    - Create `PosterManagementPanel` class extending JPanel
    - Add JTable showing board assignments
    - Add board ID selector and presenter selector
    - Add assign button with duplicate check
    - Display poster schedule
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

  - [x] 8.5 Create AwardPanel
    - Create `AwardPanel` class extending JPanel
    - Display award categories (Best Oral, Best Poster, People's Choice)
    - Add compute winners button
    - Display computed winners with scores
    - Add People's Choice voting input
    - Add generate agenda button
    - Display formatted ceremony agenda
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

  - [x] 8.6 Create ReportPanel
    - Create `ReportPanel` class extending JPanel
    - Add buttons for Schedule Report, Evaluation Report, Summary
    - Add JTextArea to display report content
    - Add export button with file save dialog
    - Display summary analytics
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

- [x] 9. Implement Evaluator Module UI
  - [x] 9.1 Create EvaluatorDashboard panel
    - Create `EvaluatorDashboard` class extending JPanel
    - Display assigned sessions and presenters
    - Add navigation to evaluation form
    - _Requirements: 6.1_

  - [x] 9.2 Create EvaluationFormPanel
    - Create `EvaluationFormPanel` class extending JPanel
    - Add presenter selector from assigned list
    - Add spinner/slider fields for each rubric criterion (1-10)
    - Add comments text area
    - Display total score calculation
    - Add submit button with validation
    - Integrate with EvaluationService
    - _Requirements: 6.2, 6.3, 6.4, 6.5, 6.6_

- [x] 10. Wire all components together
  - [x] 10.1 Integrate all panels into SeminarApp
    - Register all panels with CardLayout
    - Set up navigation between panels
    - Implement logout functionality
    - Add auto-save on data changes
    - _Requirements: 1.4, 10.1_

  - [x] 10.2 Add sample data for testing
    - Create sample users (students, evaluators, coordinator)
    - Create sample sessions
    - Create sample evaluations
    - _Requirements: All_

- [x] 11. Final checkpoint - Complete system verification
  - Ensure all UI components work correctly
  - Verify data persistence across application restarts
  - Test all user workflows end-to-end
  - Ask the user if questions arise

## Notes

- All tasks including property-based tests are required for comprehensive coverage
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties
- Unit tests validate specific examples and edge cases
- All Swing components should follow consistent styling
- Use GridBagLayout or BorderLayout for complex panels
