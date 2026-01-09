# Requirements Document

## Introduction

The Faculty of Computing and Informatics (FCI) requires a standalone Java Swing-based Seminar Management System to manage the annual Postgraduate Academic Research Seminar. This system supports three user roles (Student, Evaluator, Coordinator) and provides functionality for seminar registration, session management, evaluation, and award ceremonies.

## Glossary

- **System**: The Seminar Management System application
- **Student**: A master's or PhD student who presents research at the seminar
- **Evaluator**: A panel member who reviews and scores presentations
- **Coordinator**: Faculty staff who manages the seminar operations
- **Session**: A scheduled time slot for presentations at a specific venue
- **Presentation**: A student's research showcase (Oral or Poster type)
- **Rubric**: Predefined evaluation criteria (Problem Clarity, Methodology, Results, Presentation)
- **Board_ID**: Unique identifier for poster presentation boards

## Requirements

### Requirement 1: User Authentication and Role Management

**User Story:** As a user, I want to log in and select my role, so that I can access role-specific functionality.

#### Acceptance Criteria

1. WHEN the application starts, THE System SHALL display a login screen with role selection (Student, Evaluator, Coordinator)
2. WHEN a user selects a role and provides credentials, THE System SHALL authenticate and redirect to the appropriate dashboard
3. WHEN invalid credentials are provided, THE System SHALL display an error message and remain on the login screen
4. THE System SHALL maintain user session state throughout the application lifecycle

### Requirement 2: Student Registration

**User Story:** As a Student, I want to register for the seminar with my research details, so that I can participate as a presenter.

#### Acceptance Criteria

1. WHEN a Student accesses the registration form, THE System SHALL display fields for research title, abstract, supervisor name, and presentation type
2. WHEN a Student selects presentation type, THE System SHALL offer options for Oral or Poster presentation
3. WHEN a Student submits valid registration data, THE System SHALL save the registration and confirm success
4. WHEN a Student submits incomplete or invalid data, THE System SHALL display validation errors and prevent submission
5. THE System SHALL generate a unique presenter ID for each registered student

### Requirement 3: Presentation Material Upload

**User Story:** As a Student, I want to upload my presentation materials, so that evaluators can review my work.

#### Acceptance Criteria

1. WHEN a registered Student accesses the upload section, THE System SHALL display a file chooser for slides or poster files
2. WHEN a Student selects a file, THE System SHALL validate the file path and store the reference
3. WHEN a file upload succeeds, THE System SHALL display confirmation with the uploaded file name
4. IF a Student uploads a new file for an existing registration, THEN THE System SHALL replace the previous file reference

### Requirement 4: Session Management

**User Story:** As a Coordinator, I want to create and manage seminar sessions, so that presentations can be scheduled properly.

#### Acceptance Criteria

1. WHEN a Coordinator accesses session management, THE System SHALL display existing sessions and option to create new ones
2. WHEN a Coordinator creates a session, THE System SHALL require date, venue, and session type (Oral/Poster)
3. WHEN a session is created with valid data, THE System SHALL save and display it in the session list
4. WHEN a Coordinator edits a session, THE System SHALL allow modification of date, venue, and session type
5. WHEN a Coordinator deletes a session, THE System SHALL remove it and unassign all linked presenters and evaluators

### Requirement 5: Presenter and Evaluator Assignment

**User Story:** As a Coordinator, I want to assign presenters and evaluators to sessions, so that the seminar schedule is organized.

#### Acceptance Criteria

1. WHEN a Coordinator selects a session, THE System SHALL display available presenters and evaluators for assignment
2. WHEN a Coordinator assigns a presenter to a session, THE System SHALL link the presenter to that session's time slot
3. WHEN a Coordinator assigns an evaluator to a session, THE System SHALL link the evaluator to review presentations in that session
4. WHEN an assignment conflicts with existing assignments, THE System SHALL warn the Coordinator and prevent double-booking
5. THE System SHALL allow multiple evaluators per session and multiple presenters per session

### Requirement 6: Evaluation Module

**User Story:** As an Evaluator, I want to score presentations using predefined rubrics, so that I can provide structured feedback.

#### Acceptance Criteria

1. WHEN an Evaluator accesses their assigned presentations, THE System SHALL display a list of presenters to evaluate
2. WHEN an Evaluator selects a presenter, THE System SHALL display the evaluation form with rubric categories
3. THE System SHALL provide scoring fields for Problem Clarity, Methodology, Results, and Presentation criteria
4. WHEN an Evaluator enters scores, THE System SHALL validate that scores are within acceptable range (1-10)
5. WHEN an Evaluator submits evaluation, THE System SHALL save scores and comments for that presenter
6. THE System SHALL calculate and display total score based on rubric weights

### Requirement 7: Poster Presentation Management

**User Story:** As a Coordinator, I want to manage poster presentations with board assignments, so that poster sessions are organized.

#### Acceptance Criteria

1. WHEN a Coordinator manages poster presentations, THE System SHALL display available board IDs
2. WHEN a Coordinator assigns a poster presenter to a board, THE System SHALL link the Board_ID to that presenter
3. WHEN a board is already assigned, THE System SHALL prevent duplicate assignment and show warning
4. THE System SHALL display poster schedule with presenter names and Board_IDs

### Requirement 8: Award and Ceremony Module

**User Story:** As a Coordinator, I want to compute award winners and generate ceremony agenda, so that the closing ceremony is organized.

#### Acceptance Criteria

1. WHEN a Coordinator accesses the award module, THE System SHALL display award categories (Best Oral, Best Poster, People's Choice)
2. WHEN a Coordinator requests winner computation, THE System SHALL calculate winners based on evaluation scores
3. THE System SHALL rank presenters by total score within each presentation type for Best Oral and Best Poster
4. WHEN winners are computed, THE System SHALL display the top presenters for each award category
5. WHEN a Coordinator generates ceremony agenda, THE System SHALL create a formatted agenda with award sequence

### Requirement 9: Reports and Summary

**User Story:** As a Coordinator, I want to generate reports and view analytics, so that I can review seminar outcomes.

#### Acceptance Criteria

1. WHEN a Coordinator accesses reports, THE System SHALL display options for schedule report, evaluation report, and summary
2. WHEN a Coordinator generates schedule report, THE System SHALL display all sessions with assigned presenters and evaluators
3. WHEN a Coordinator generates evaluation report, THE System SHALL display all scores and comments per presenter
4. WHEN a Coordinator requests export, THE System SHALL save the report to a text file
5. THE System SHALL display summary analytics including total presenters, sessions, and average scores

### Requirement 10: Data Persistence

**User Story:** As a user, I want my data to be saved, so that I can access it in future sessions.

#### Acceptance Criteria

1. WHEN data is created or modified, THE System SHALL persist it to local storage
2. WHEN the application starts, THE System SHALL load existing data from storage
3. IF data loading fails, THEN THE System SHALL start with empty data and notify the user
4. THE System SHALL use serialization for data persistence to files
