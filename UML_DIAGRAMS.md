# Seminar Management System - UML Diagrams

This document contains UML diagrams for the Seminar Management System using Mermaid syntax.

## Table of Contents
1. [Class Diagram - Domain Model](#1-class-diagram---domain-model)
2. [Class Diagram - Service Layer](#2-class-diagram---service-layer)
3. [Class Diagram - UI Layer](#3-class-diagram---ui-layer)
4. [Use Case Diagram](#4-use-case-diagram)
5. [Sequence Diagram - Student Registration](#5-sequence-diagram---student-registration)
6. [Sequence Diagram - Evaluation Submission](#6-sequence-diagram---evaluation-submission)
7. [Sequence Diagram - Voting Process](#7-sequence-diagram---voting-process)
8. [Entity Relationship Diagram](#8-entity-relationship-diagram)
9. [Component Diagram](#9-component-diagram)
10. [State Diagram - Session Lifecycle](#10-state-diagram---session-lifecycle)

---

## 1. Class Diagram - Domain Model

```mermaid
classDiagram
    class User {
        <<abstract>>
        -String id
        -String username
        -String password
        -UserRole role
        +getId() String
        +setId(String)
        +getUsername() String
        +getPassword() String
        +getRole() UserRole
    }
    
    class Student {
        -String studentId
        -String researchTitle
        -String abstractText
        -String supervisorName
        -PresentationType presentationType
        -String filePath
        -String presenterId
        -int voteCount
        -boolean hasVoted
        +getStudentId() String
        +getResearchTitle() String
        +getPresentationType() PresentationType
        +getVoteCount() int
        +hasVoted() boolean
    }
    
    class Evaluator {
        -List~String~ assignedSessionIds
        -String evaluatorId
        +getAssignedSessionIds() List~String~
        +addAssignedSession(String)
        +removeAssignedSession(String)
        +getEvaluatorId() String
    }
    
    class Coordinator {
        +Coordinator()
    }
    
    class Session {
        -String sessionId
        -LocalDate date
        -String venue
        -String meetingLink
        -PresentationType sessionType
        -List~String~ presenterIds
        -List~String~ evaluatorIds
        +getSessionId() String
        +getDate() LocalDate
        +getVenue() String
        +getMeetingLink() String
        +getSessionType() PresentationType
        +addPresenter(String)
        +addEvaluator(String)
    }
    
    class Evaluation {
        -String evaluationId
        -String presenterId
        -String evaluatorId
        -String sessionId
        -RubricScores scores
        -String comments
        -LocalDateTime submittedAt
        +getEvaluationId() String
        +getScores() RubricScores
        +getComments() String
    }
    
    class RubricScores {
        -int problemClarity
        -int methodology
        -int results
        -int presentation
        +getTotalScore() int
        +getProblemClarity() int
        +getMethodology() int
        +getResults() int
        +getPresentation() int
    }
    
    class PosterBoard {
        -String boardId
        -String presenterId
        -String sessionId
        +getBoardId() String
        +getPresenterId() String
        +getSessionId() String
    }
    
    class Award {
        -AwardType type
        -String winnerId
        -double score
        -LocalDate ceremonyDate
        +getType() AwardType
        +getWinnerId() String
        +getScore() double
    }
    
    class CeremonyAgenda {
        -List~Award~ awards
        -LocalDateTime ceremonyDate
        +getAwards() List~Award~
        +generateFormattedAgenda() String
    }
    
    class UserRole {
        <<enumeration>>
        PRESENTER
        PANEL_MEMBER
        COORDINATOR
    }
    
    class PresentationType {
        <<enumeration>>
        ORAL
        POSTER
    }
    
    class AwardType {
        <<enumeration>>
        BEST_ORAL
        BEST_POSTER
        PEOPLES_CHOICE
    }
    
    User <|-- Student
    User <|-- Evaluator
    User <|-- Coordinator
    User --> UserRole
    Student --> PresentationType
    Session --> PresentationType
    Evaluation --> RubricScores
    Award --> AwardType
    CeremonyAgenda --> Award
    Session "1" --> "*" Student : presents
    Session "1" --> "*" Evaluator : evaluates
    Evaluation "*" --> "1" Student : evaluates
    Evaluation "*" --> "1" Evaluator : submittedBy
    PosterBoard "1" --> "1" Student : assignedTo
```

---

## 2. Class Diagram - Service Layer

```mermaid
classDiagram
    class DatabaseManager {
        -Connection connection
        -DatabaseManager instance
        +getInstance() DatabaseManager
        +getConnection() Connection
        +saveUser(User)
        +getUser(String) User
        +getUserByUsername(String) User
        +getAllUsers() Map~String,User~
        +saveSession(Session)
        +getSession(String) Session
        +getAllSessions() Map~String,Session~
        +saveEvaluation(Evaluation)
        +getEvaluation(String) Evaluation
        +getAllEvaluations() List~Evaluation~
        +savePosterBoard(PosterBoard)
        +getAllPosterBoards() List~PosterBoard~
        +saveAward(Award)
        +getAllAwards() List~Award~
    }
    
    class DataStore {
        -Map~String,User~ users
        -Map~String,Session~ sessions
        -List~Evaluation~ evaluations
        -List~PosterBoard~ posterBoards
        -List~Award~ awards
        -DatabaseManager dbManager
        +load(String) DataStore
        +save(String)
        +getUser(String) User
        +addUser(User)
        +updateUser(User)
        +getSession(String) Session
        +addSession(Session)
        +updateSession(Session)
        +addEvaluation(Evaluation)
        +getEvaluations() List~Evaluation~
    }
    
    class UserService {
        -DataStore dataStore
        +UserService(DataStore)
        +authenticateUser(String,String,UserRole) User
        +registerStudent(Student) boolean
        +registerEvaluator(Evaluator) boolean
        +getAllStudents() List~Student~
        +getAllEvaluators() List~Evaluator~
        +getStudentByPresenterId(String) Student
        +updateStudent(Student)
    }
    
    class SessionService {
        -DataStore dataStore
        -UserService userService
        +SessionService(DataStore,UserService)
        +createSession(Session)
        +getAllSessions() List~Session~
        +getSessionById(String) Session
        +assignPresenter(String,String)
        +assignEvaluator(String,String)
        +unassignPresenter(String,String)
        +unassignEvaluator(String,String)
        +hasConflict(String,LocalDate) boolean
    }
    
    class EvaluationService {
        -DataStore dataStore
        +EvaluationService(DataStore)
        +submitEvaluation(Evaluation)
        +getEvaluationById(String) Evaluation
        +getEvaluationsByPresenter(String) List~Evaluation~
        +getEvaluationsByEvaluator(String) List~Evaluation~
        +getEvaluationByEvaluatorAndPresenter(String,String) Evaluation
        +calculateAverageScore(String) double
    }
    
    class PosterBoardService {
        -DataStore dataStore
        +PosterBoardService(DataStore)
        +assignBoard(String,String,String)
        +unassignBoard(String)
        +getBoardById(String) PosterBoard
        +getAllAssignments() List~PosterBoard~
        +getAvailableBoards() List~String~
    }
    
    class AwardService {
        -DataStore dataStore
        -EvaluationService evaluationService
        +AwardService(DataStore,EvaluationService)
        +computeBestOral() Award
        +computeBestPoster() Award
        +computePeoplesChoice(Map~String,Integer~) Award
        +clearAwards()
        +generateAgenda() CeremonyAgenda
    }
    
    class ReportService {
        -DataStore dataStore
        +ReportService(DataStore)
        +generateReport() String
        +exportToPDF(String,String)
    }
    
    class FileStorageService {
        -FileStorageService instance
        -String BASE_UPLOAD_DIR
        -Set~String~ SUPPORTED_EXTENSIONS
        +getInstance() FileStorageService
        +uploadFile(File,String) String
        +isFileTypeSupported(File) boolean
        +resolveStoragePath(String) File
        +isAbsolutePath(String) boolean
        -getFileExtension(String) String
        -createPresenterDirectory(String) Path
        -deleteOldFiles(String,String)
        -initializeBaseDirectory()
        -logError(String,Exception)
    }
    
    class FileStorageException {
        <<exception>>
        -ErrorType errorType
        +FileStorageException(String,ErrorType)
        +FileStorageException(String,ErrorType,Throwable)
        +getErrorType() ErrorType
        +getUserFriendlyMessage() String
    }
    
    class ErrorType {
        <<enumeration>>
        FILE_NOT_FOUND
        INVALID_FILE_TYPE
        PERMISSION_DENIED
        INSUFFICIENT_SPACE
        COPY_FAILED
        DIRECTORY_CREATION_FAILED
    }
    
    DataStore --> DatabaseManager
    UserService --> DataStore
    SessionService --> DataStore
    SessionService --> UserService
    EvaluationService --> DataStore
    PosterBoardService --> DataStore
    AwardService --> DataStore
    AwardService --> EvaluationService
    ReportService --> DataStore
    FileStorageService --> FileStorageException
    FileStorageException --> ErrorType
```

---

## 3. Class Diagram - UI Layer

```mermaid
classDiagram
    class SeminarApp {
        -CardLayout cardLayout
        -JPanel mainPanel
        -DataStore dataStore
        -User currentUser
        -UserService userService
        -SessionService sessionService
        -EvaluationService evaluationService
        +SeminarApp()
        +showPanel(String)
        +getCurrentUser() User
        +setCurrentUser(User)
        +autoSave()
        +main(String[])
    }
    
    class LoginPanel {
        -SeminarApp app
        -UserService userService
        -JTextField usernameField
        -JPasswordField passwordField
        -JComboBox roleCombo
        +LoginPanel(SeminarApp,UserService)
        -login()
    }
    
    class SignUpPanel {
        -SeminarApp app
        -UserService userService
        -JTextField usernameField
        -JPasswordField passwordField
        -JComboBox roleCombo
        +SignUpPanel(SeminarApp,UserService)
        -signUp()
    }
    
    class StudentDashboard {
        -SeminarApp app
        -JLabel welcomeLabel
        +StudentDashboard(SeminarApp)
        +refresh()
        -navigateToRegistration()
        -navigateToVoting()
    }
    
    class StudentRegistrationPanel {
        -SeminarApp app
        -UserService userService
        -JTextField studentIdField
        -JTextField researchTitleField
        -JTextArea abstractArea
        -JComboBox presentationTypeCombo
        +StudentRegistrationPanel(SeminarApp,UserService)
        +refresh()
        -register()
        -uploadFile()
    }
    
    class VotingPanel {
        -SeminarApp app
        -UserService userService
        -JPanel candidatesPanel
        -ButtonGroup candidatesGroup
        +VotingPanel(SeminarApp,UserService)
        +refresh()
        -submitVote()
        -viewPresentationMaterials(Student)
    }
    
    class MySessionPanel {
        -SeminarApp app
        -SessionService sessionService
        -UserService userService
        -JLabel sessionTypeLabel
        -JLabel dateLabel
        -JLabel venueLabel
        +MySessionPanel(SeminarApp,SessionService,UserService)
        +refresh()
        -joinMeeting()
    }
    
    class EvaluatorDashboard {
        -SeminarApp app
        -SessionService sessionService
        -UserService userService
        -JTable assignmentsTable
        +EvaluatorDashboard(SeminarApp,SessionService,UserService)
        +refresh()
        -navigateToEvaluationForm()
    }
    
    class EvaluationFormPanel {
        -SeminarApp app
        -EvaluationService evaluationService
        -JSpinner problemClaritySpinner
        -JSpinner methodologySpinner
        -JTextArea commentsArea
        -Student selectedPresenter
        +EvaluationFormPanel(SeminarApp,EvaluationService,UserService)
        +setPresenter(Student,String)
        -submitEvaluation()
        -viewMaterials()
        -downloadMaterials()
    }
    
    class CoordinatorDashboard {
        -SeminarApp app
        +CoordinatorDashboard(SeminarApp)
        -navigateToSessionManagement()
        -navigateToAssignments()
        -navigateToAwards()
    }
    
    class SessionManagementPanel {
        -SeminarApp app
        -SessionService sessionService
        -JTable sessionTable
        -JComboBox venueCombo
        -JTextField meetingLinkField
        +SessionManagementPanel(SeminarApp,SessionService)
        +refresh()
        -createSession()
        -deleteSession()
    }
    
    class AssignmentPanel {
        -SeminarApp app
        -SessionService sessionService
        -UserService userService
        -JComboBox sessionCombo
        -JList availablePresentersList
        -JList assignedPresentersList
        +AssignmentPanel(SeminarApp,SessionService,UserService)
        +refresh()
        -assignPresenter()
        -assignEvaluator()
    }
    
    class PosterManagementPanel {
        -SeminarApp app
        -PosterBoardService posterBoardService
        -JTable boardTable
        -JComboBox boardIdCombo
        +PosterManagementPanel(SeminarApp,PosterBoardService,SessionService,UserService)
        +refresh()
        -assignBoard()
        -viewPresentationMaterials(int)
    }
    
    class AwardPanel {
        -SeminarApp app
        -AwardService awardService
        -JLabel bestOralLabel
        -JLabel bestPosterLabel
        -JLabel peoplesChoiceLabel
        +AwardPanel(SeminarApp,AwardService,UserService)
        +refresh()
        -computeWinners()
        -generateAgenda()
    }
    
    class UserManagementPanel {
        -SeminarApp app
        -UserService userService
        -JTable userTable
        +UserManagementPanel(SeminarApp,UserService)
        +refresh()
    }
    
    class PresentationViewerDialog {
        -Student student
        -JPanel contentPanel
        -JLabel zoomLabel
        -double zoomLevel
        +PresentationViewerDialog(Component,Student)
        -loadMaterials()
        -zoomIn()
        -zoomOut()
        -renderPDF(File)
    }
    
    SeminarApp --> LoginPanel
    SeminarApp --> SignUpPanel
    SeminarApp --> StudentDashboard
    SeminarApp --> StudentRegistrationPanel
    SeminarApp --> VotingPanel
    SeminarApp --> MySessionPanel
    SeminarApp --> EvaluatorDashboard
    SeminarApp --> EvaluationFormPanel
    SeminarApp --> CoordinatorDashboard
    SeminarApp --> SessionManagementPanel
    SeminarApp --> AssignmentPanel
    SeminarApp --> PosterManagementPanel
    SeminarApp --> AwardPanel
    SeminarApp --> UserManagementPanel
    VotingPanel --> PresentationViewerDialog
    PosterManagementPanel --> PresentationViewerDialog
    EvaluationFormPanel --> PresentationViewerDialog
```

---

## 4. Use Case Diagram

```mermaid
graph TB
    Student((Student))
    Evaluator((Evaluator))
    Coordinator((Coordinator))
    
    subgraph "Seminar Management System"
        UC1[Sign Up]
        UC2[Login]
        UC3[Register for Seminar]
        UC4[Upload Materials]
        UC5[View My Session]
        UC6[Vote for People's Choice]
        UC7[View Assigned Sessions]
        UC8[Evaluate Presentation]
        UC9[View/Download Materials]
        UC10[Join Online Meeting]
        UC11[Manage Users]
        UC12[Create Sessions]
        UC13[Assign Presenters/Evaluators]
        UC14[Manage Poster Boards]
        UC15[Compute Awards]
        UC16[Generate Reports]
    end
    
    Student --> UC1
    Student --> UC2
    Student --> UC3
    Student --> UC4
    Student --> UC5
    Student --> UC6
    
    Evaluator --> UC1
    Evaluator --> UC2
    Evaluator --> UC7
    Evaluator --> UC8
    Evaluator --> UC9
    Evaluator --> UC10
    
    Coordinator --> UC2
    Coordinator --> UC11
    Coordinator --> UC12
    Coordinator --> UC13
    Coordinator --> UC14
    Coordinator --> UC15
    Coordinator --> UC16
    
    UC3 -.includes.-> UC4
    UC8 -.includes.-> UC9
    UC8 -.includes.-> UC10
    UC6 -.includes.-> UC9
```

---

## 5. Sequence Diagram - Student Registration

```mermaid
sequenceDiagram
    actor Student
    participant UI as StudentRegistrationPanel
    participant FileSvc as FileStorageService
    participant Service as UserService
    participant Store as DataStore
    participant DB as DatabaseManager
    participant FS as File System
    
    Student->>UI: Fill registration form
    Student->>UI: Browse and select file
    UI->>UI: Display selected filename
    Student->>UI: Click Register
    UI->>UI: Validate input
    
    alt File selected
        UI->>FileSvc: isFileTypeSupported(file)
        FileSvc-->>UI: true/false
        
        alt Valid file type
            UI->>FileSvc: uploadFile(file, presenterId)
            FileSvc->>FileSvc: Validate file exists
            FileSvc->>FileSvc: Create presenter directory
            FileSvc->>FS: Copy file to uploads/presentations/{presenterId}/
            FS-->>FileSvc: Success
            FileSvc->>FileSvc: Generate relative storage path
            FileSvc-->>UI: Return storage path
            UI->>UI: Set student.filePath(storagePath)
        else Invalid file type
            FileSvc-->>UI: Throw FileStorageException
            UI->>Student: Show error message
        end
    end
    
    UI->>Service: registerStudent(student)
    Service->>Service: Generate presenter ID
    Service->>Store: addUser(student)
    Store->>DB: saveUser(student)
    DB->>DB: INSERT INTO users (with relative file_path)
    DB-->>Store: Success
    Store-->>Service: Success
    Service-->>UI: Registration successful
    UI->>UI: Show success message
    UI->>Student: Display confirmation
```

---

## 6. Sequence Diagram - Evaluation Submission

```mermaid
sequenceDiagram
    actor Evaluator
    participant Dashboard as EvaluatorDashboard
    participant Form as EvaluationFormPanel
    participant FileSvc as FileStorageService
    participant EvalService as EvaluationService
    participant Store as DataStore
    participant DB as DatabaseManager
    participant FS as File System
    
    Evaluator->>Dashboard: Double-click session
    Dashboard->>Form: setPresenter(student, sessionId)
    Form->>Store: getSession(sessionId)
    Store-->>Form: Session details
    Form->>Form: Display session info
    
    alt Session is ORAL
        Form->>Form: Show meeting link & join button
    else Session is POSTER
        Form->>Form: Hide meeting link field
    end
    
    Form->>Evaluator: Show evaluation form
    
    Evaluator->>Form: Click "View Materials"
    Form->>FileSvc: isAbsolutePath(student.filePath)
    
    alt Relative path (new format)
        FileSvc-->>Form: false
        Form->>FileSvc: resolveStoragePath(filePath)
        FileSvc->>FS: Resolve to uploads/presentations/{presenterId}/
        FS-->>FileSvc: Absolute File object
        FileSvc-->>Form: File object
    else Absolute path (legacy)
        FileSvc-->>Form: true
        Form->>Form: Use path directly
    end
    
    Form->>Form: Open PresentationViewerDialog
    
    Evaluator->>Form: Fill rubric scores
    Evaluator->>Form: Add comments
    Evaluator->>Form: Click Submit
    
    Form->>Form: Validate scores
    Form->>EvalService: submitEvaluation(evaluation)
    EvalService->>Store: addEvaluation(evaluation)
    Store->>DB: saveEvaluation(evaluation)
    DB->>DB: INSERT INTO evaluations
    DB-->>Store: Success
    Store-->>EvalService: Success
    EvalService-->>Form: Success
    Form->>Evaluator: Show success message
```

---

## 7. Sequence Diagram - Voting Process

```mermaid
sequenceDiagram
    actor Student
    participant Voting as VotingPanel
    participant UserService as UserService
    participant Store as DataStore
    participant DB as DatabaseManager
    
    Student->>Voting: Navigate to voting
    Voting->>UserService: getAllStudents()
    UserService->>Store: getUsers()
    Store-->>UserService: All users
    UserService-->>Voting: Student list
    Voting->>Voting: Filter out self & non-presenters
    Voting->>Student: Display candidates
    
    Student->>Voting: Click "View Materials"
    Voting->>Voting: Open PresentationViewerDialog
    
    Student->>Voting: Select candidate
    Student->>Voting: Click Submit Vote
    Voting->>Voting: Confirm vote
    
    Voting->>DB: INSERT INTO votes
    DB-->>Voting: Success
    
    Voting->>UserService: Update vote count
    UserService->>Store: updateUser(votedStudent)
    Store->>DB: UPDATE users SET vote_count
    
    Voting->>UserService: Mark voter as voted
    UserService->>Store: updateUser(currentStudent)
    Store->>DB: UPDATE users SET has_voted
    
    Voting->>Student: Show success message
    Voting->>Voting: Refresh to show "Already Voted"
```

---

## 8. Entity Relationship Diagram

```mermaid
erDiagram
    USERS ||--o{ SESSION_PRESENTERS : "presents in"
    USERS ||--o{ SESSION_EVALUATORS : "evaluates in"
    USERS ||--o{ EVALUATIONS : "submits"
    USERS ||--o{ EVALUATIONS : "receives"
    USERS ||--o{ POSTER_BOARDS : "assigned to"
    USERS ||--o{ VOTES : "votes"
    USERS ||--o{ VOTES : "receives vote"
    SESSIONS ||--o{ SESSION_PRESENTERS : "has"
    SESSIONS ||--o{ SESSION_EVALUATORS : "has"
    SESSIONS ||--o{ EVALUATIONS : "conducted in"
    SESSIONS ||--o{ POSTER_BOARDS : "uses"
    
    USERS {
        int id PK
        string username UK
        string password
        enum role
        string student_id UK
        string research_title
        text abstract_text
        string supervisor_name
        enum presentation_type
        string file_path
        string presenter_id
        int vote_count
        boolean has_voted
        string evaluator_id
        timestamp created_at
        timestamp updated_at
    }
    
    SESSIONS {
        string session_id PK
        date session_date
        string venue
        string meeting_link
        enum session_type
        timestamp created_at
        timestamp updated_at
    }
    
    SESSION_PRESENTERS {
        int id PK
        string session_id FK
        string presenter_id
        timestamp assigned_at
    }
    
    SESSION_EVALUATORS {
        int id PK
        string session_id FK
        string evaluator_id
        timestamp assigned_at
    }
    
    EVALUATIONS {
        string evaluation_id PK
        string presenter_id FK
        string evaluator_id FK
        string session_id FK
        int problem_clarity
        int methodology
        int results
        int presentation
        text comments
        timestamp submitted_at
    }
    
    POSTER_BOARDS {
        string board_id PK
        string presenter_id
        string session_id FK
        timestamp assigned_at
    }
    
    VENUES {
        int venue_id PK
        string venue_name UK
        int capacity
        enum venue_type
        timestamp created_at
    }
    
    AWARDS {
        int id PK
        enum award_type
        string winner_id
        double score
        date ceremony_date
        timestamp created_at
    }
    
    VOTES {
        int id PK
        string voter_student_id UK
        string voted_for_presenter_id
        timestamp voted_at
    }
```

---

## 9. Component Diagram

```mermaid
graph TB
    subgraph "Presentation Layer"
        UI[UI Components<br/>Swing Panels & Dialogs]
    end
    
    subgraph "Application Layer"
        App[SeminarApp<br/>Main Application Controller]
    end
    
    subgraph "Service Layer"
        UserSvc[UserService]
        SessionSvc[SessionService]
        EvalSvc[EvaluationService]
        PosterSvc[PosterBoardService]
        AwardSvc[AwardService]
        ReportSvc[ReportService]
        FileSvc[FileStorageService]
    end
    
    subgraph "Data Access Layer"
        DataStore[DataStore<br/>In-Memory Cache]
        DBMgr[DatabaseManager<br/>JDBC Connection]
    end
    
    subgraph "Domain Layer"
        Models[Domain Models<br/>User, Session, Evaluation, etc.]
    end
    
    subgraph "External Systems"
        MySQL[(MySQL Database<br/>seminar_db)]
        FileSystem[File System<br/>uploads/presentations/]
        Browser[Web Browser<br/>Teams Meetings]
    end
    
    subgraph "Utilities"
        ErrorHandler[ErrorHandler]
        IdGenerator[IdGenerator]
        PDFBox[Apache PDFBox<br/>PDF Rendering]
        FileStorageEx[FileStorageException]
    end
    
    UI --> App
    App --> UserSvc
    App --> SessionSvc
    App --> EvalSvc
    App --> PosterSvc
    App --> AwardSvc
    App --> ReportSvc
    
    UserSvc --> DataStore
    SessionSvc --> DataStore
    EvalSvc --> DataStore
    PosterSvc --> DataStore
    AwardSvc --> DataStore
    ReportSvc --> DataStore
    
    DataStore --> DBMgr
    DataStore --> Models
    
    DBMgr --> MySQL
    
    UI --> ErrorHandler
    UI --> FileSvc
    UserSvc --> IdGenerator
    UI --> PDFBox
    FileSvc --> FileSystem
    FileSvc --> FileStorageEx
    UI --> Browser
    
    SessionSvc --> UserSvc
    AwardSvc --> EvalSvc
```

---

## 10. State Diagram - Session Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Created: Admin creates session
    
    Created --> AssigningPresenters: Assign presenter
    AssigningPresenters --> PresenterAssigned: Presenter added
    PresenterAssigned --> AssigningEvaluators: Assign evaluator
    AssigningEvaluators --> EvaluatorAssigned: Evaluator added
    
    EvaluatorAssigned --> PosterBoardAssignment: If POSTER type
    PosterBoardAssignment --> ReadyForPresentation: Board assigned
    
    EvaluatorAssigned --> ReadyForPresentation: If ORAL type
    
    ReadyForPresentation --> InProgress: Session date arrives
    InProgress --> MaterialsUploaded: Student uploads materials
    MaterialsUploaded --> UnderEvaluation: Evaluator starts evaluation
    UnderEvaluation --> Evaluated: Evaluation submitted
    
    Evaluated --> VotingOpen: Voting period starts
    VotingOpen --> VotingClosed: Voting period ends
    VotingClosed --> AwardsComputed: Admin computes awards
    AwardsComputed --> Completed: Ceremony held
    
    Completed --> [*]
    
    Created --> Cancelled: Admin cancels
    AssigningPresenters --> Cancelled: Admin cancels
    PresenterAssigned --> Cancelled: Admin cancels
    AssigningEvaluators --> Cancelled: Admin cancels
    EvaluatorAssigned --> Cancelled: Admin cancels
    Cancelled --> [*]
```

---

## Additional Diagrams

### Activity Diagram - Award Computation Process

```mermaid
flowchart TD
    Start([Start Award Computation]) --> LoadEvals[Load All Evaluations]
    LoadEvals --> LoadVotes[Load All Votes]
    
    LoadVotes --> ComputeOral{Compute Best Oral}
    ComputeOral --> FilterOral[Filter ORAL presentations]
    FilterOral --> CalcOralAvg[Calculate average scores]
    CalcOralAvg --> FindMaxOral[Find highest score]
    FindMaxOral --> SaveOral[Save Best Oral Award]
    
    SaveOral --> ComputePoster{Compute Best Poster}
    ComputePoster --> FilterPoster[Filter POSTER presentations]
    FilterPoster --> CalcPosterAvg[Calculate average scores]
    CalcPosterAvg --> FindMaxPoster[Find highest score]
    FindMaxPoster --> SavePoster[Save Best Poster Award]
    
    SavePoster --> ComputeChoice{Compute People's Choice}
    ComputeChoice --> CountVotes[Count votes per presenter]
    CountVotes --> FindMaxVotes[Find highest vote count]
    FindMaxVotes --> SaveChoice[Save People's Choice Award]
    
    SaveChoice --> GenAgenda[Generate Ceremony Agenda]
    GenAgenda --> Display[Display Winners]
    Display --> End([End])
```

### Deployment Diagram

```mermaid
graph TB
    subgraph "Client Machine"
        subgraph "JVM"
            App[Seminar Management<br/>Application<br/>Java Swing]
        end
        Files[Local File System<br/>Presentation Materials]
    end
    
    subgraph "Laragon Server"
        MySQL[MySQL Database<br/>Port 3306<br/>seminar_db]
        phpMyAdmin[phpMyAdmin<br/>Web Interface]
    end
    
    subgraph "External Services"
        Teams[Microsoft Teams<br/>Online Meetings]
    end
    
    App -->|JDBC Connection<br/>localhost:3306| MySQL
    App -->|Read/Write Files| Files
    App -->|Open Browser<br/>Meeting Links| Teams
    phpMyAdmin -->|Manage| MySQL
    
    style App fill:#e1f5ff
    style MySQL fill:#ffe1e1
    style Teams fill:#e1ffe1
```

---

## Diagram Usage Guide

### How to View These Diagrams

1. **GitHub/GitLab**: These platforms render Mermaid diagrams automatically
2. **VS Code**: Install "Markdown Preview Mermaid Support" extension
3. **Online**: Copy diagram code to https://mermaid.live/
4. **Documentation**: Use MkDocs with mermaid2 plugin

### Diagram Descriptions

- **Class Diagrams**: Show the structure of classes and their relationships
- **Use Case Diagram**: Shows what each user role can do
- **Sequence Diagrams**: Show the flow of operations over time
- **ER Diagram**: Shows database table relationships
- **Component Diagram**: Shows system architecture and dependencies
- **State Diagram**: Shows session lifecycle states
- **Activity Diagram**: Shows the award computation workflow
- **Deployment Diagram**: Shows physical deployment architecture

---

## System Architecture Summary

### Layered Architecture
1. **Presentation Layer**: Swing UI components
2. **Application Layer**: SeminarApp controller
3. **Service Layer**: Business logic services
4. **Data Access Layer**: DataStore and DatabaseManager
5. **Domain Layer**: Entity models

### Design Patterns Used
- **Singleton**: DatabaseManager, DataStore
- **MVC**: Model-View-Controller separation
- **Factory**: ID generation for users
- **Observer**: UI refresh on data changes
- **Strategy**: Different evaluation strategies for awards

### Key Technologies
- **UI Framework**: Java Swing
- **Database**: MySQL with JDBC
- **Connection Pool**: HikariCP
- **PDF Rendering**: Apache PDFBox
- **Date Picker**: JCalendar
- **Build Tool**: Maven

---

*Generated for Seminar Management System v1.0*
