# Design Document: Seminar Management System

## Overview

The Seminar Management System is a standalone Java Swing desktop application that manages the FCI Postgraduate Academic Research Seminar. The system follows a layered architecture with clear separation between UI (Swing components), business logic (services), and data persistence (file-based serialization).

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │ LoginPanel  │ │StudentPanel │ │  CoordinatorPanel   │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │EvaluatorPanel│ │SessionPanel │ │    ReportPanel      │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│                    Service Layer                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │ UserService │ │SessionService│ │  EvaluationService  │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
│  ┌─────────────┐ ┌─────────────┐                           │
│  │AwardService │ │ReportService│                           │
│  └─────────────┘ └─────────────┘                           │
├─────────────────────────────────────────────────────────────┤
│                    Data Layer                                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐   │
│  │  DataStore  │ │ FileManager │ │   Serialization     │   │
│  └─────────────┘ └─────────────┘ └─────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Design Pattern: MVC-inspired Architecture

- **Model**: Data classes (User, Student, Evaluator, Session, Evaluation, etc.)
- **View**: Java Swing panels and frames
- **Controller**: Service classes that handle business logic

## Components and Interfaces

### 1. Main Application Frame

```java
public class SeminarApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DataStore dataStore;
    
    public void showPanel(String panelName);
    public void setCurrentUser(User user);
    public DataStore getDataStore();
}
```

### 2. User Management Components

```java
public abstract class User implements Serializable {
    private String id;
    private String username;
    private String password;
    private UserRole role;
}

public class Student extends User {
    private String researchTitle;
    private String abstractText;
    private String supervisorName;
    private PresentationType presentationType;
    private String filePath;
    private String presenterId;
}

public class Evaluator extends User {
    private List<String> assignedSessionIds;
}

public class Coordinator extends User {
    // Coordinator-specific fields if needed
}

public enum UserRole { STUDENT, EVALUATOR, COORDINATOR }
public enum PresentationType { ORAL, POSTER }
```

### 3. Session Management Components

```java
public class Session implements Serializable {
    private String sessionId;
    private LocalDate date;
    private String venue;
    private PresentationType sessionType;
    private List<String> presenterIds;
    private List<String> evaluatorIds;
}

public class PosterBoard implements Serializable {
    private String boardId;
    private String presenterId;
    private String sessionId;
}
```

### 4. Evaluation Components

```java
public class Evaluation implements Serializable {
    private String evaluationId;
    private String presenterId;
    private String evaluatorId;
    private String sessionId;
    private RubricScores scores;
    private String comments;
    private LocalDateTime timestamp;
}

public class RubricScores implements Serializable {
    private int problemClarity;    // 1-10
    private int methodology;       // 1-10
    private int results;           // 1-10
    private int presentation;      // 1-10
    
    public int getTotalScore();
    public double getWeightedScore();
}
```

### 5. Award Components

```java
public class Award implements Serializable {
    private AwardType type;
    private String winnerId;
    private double score;
}

public enum AwardType { BEST_ORAL, BEST_POSTER, PEOPLES_CHOICE }

public class CeremonyAgenda implements Serializable {
    private List<Award> awards;
    private LocalDateTime ceremonyDate;
    
    public String generateFormattedAgenda();
}
```

### 6. Service Interfaces

```java
public interface UserService {
    User authenticate(String username, String password, UserRole role);
    void registerStudent(Student student);
    Student getStudentById(String id);
    List<Student> getAllStudents();
    List<Evaluator> getAllEvaluators();
}

public interface SessionService {
    Session createSession(LocalDate date, String venue, PresentationType type);
    void updateSession(Session session);
    void deleteSession(String sessionId);
    void assignPresenter(String sessionId, String presenterId);
    void assignEvaluator(String sessionId, String evaluatorId);
    List<Session> getAllSessions();
    boolean hasConflict(String userId, LocalDate date);
}

public interface EvaluationService {
    void submitEvaluation(Evaluation evaluation);
    List<Evaluation> getEvaluationsForPresenter(String presenterId);
    List<Evaluation> getEvaluationsByEvaluator(String evaluatorId);
    double calculateAverageScore(String presenterId);
}

public interface AwardService {
    Award computeBestOral();
    Award computeBestPoster();
    Award computePeoplesChoice(Map<String, Integer> votes);
    CeremonyAgenda generateAgenda();
}

public interface ReportService {
    String generateScheduleReport();
    String generateEvaluationReport();
    String generateSummaryReport();
    void exportToFile(String content, String filename);
}
```

### 7. Data Store

```java
public class DataStore implements Serializable {
    private Map<String, User> users;
    private Map<String, Session> sessions;
    private Map<String, Evaluation> evaluations;
    private Map<String, PosterBoard> posterBoards;
    private List<Award> awards;
    
    public void save(String filepath);
    public static DataStore load(String filepath);
}
```

## Data Models

### Entity Relationship Diagram

```
┌──────────┐       ┌───────────┐       ┌────────────┐
│  User    │       │  Session  │       │ Evaluation │
├──────────┤       ├───────────┤       ├────────────┤
│ id       │──┐    │ sessionId │──┐    │evaluationId│
│ username │  │    │ date      │  │    │presenterId │
│ password │  │    │ venue     │  │    │evaluatorId │
│ role     │  │    │ type      │  │    │sessionId   │
└──────────┘  │    │presenterIds│  │    │scores      │
     ▲        │    │evaluatorIds│  │    │comments    │
     │        │    └───────────┘  │    └────────────┘
┌────┴─────┐  │         │         │          │
│ Student  │──┼─────────┘         │          │
├──────────┤  │                   │          │
│title     │  │    ┌──────────────┴──────────┘
│abstract  │  │    │
│supervisor│  │    ▼
│type      │  │ ┌─────────────┐
│filePath  │  │ │ PosterBoard │
│presenterId│ │ ├─────────────┤
└──────────┘  │ │ boardId     │
              │ │ presenterId │
┌──────────┐  │ │ sessionId   │
│Evaluator │──┘ └─────────────┘
├──────────┤
│sessionIds│
└──────────┘
```

### Validation Rules

| Field | Validation |
|-------|------------|
| Username | Non-empty, alphanumeric, 3-20 characters |
| Password | Non-empty, minimum 4 characters |
| Research Title | Non-empty, maximum 200 characters |
| Abstract | Non-empty, maximum 2000 characters |
| Supervisor Name | Non-empty, alphabetic with spaces |
| Rubric Scores | Integer 1-10 inclusive |
| Venue | Non-empty string |
| Board ID | Format: "B001", "B002", etc. |



## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Authentication Correctness

*For any* user credentials (username, password, role), if the credentials match an existing user with that role, authentication SHALL return that user; otherwise, authentication SHALL return null/failure.

**Validates: Requirements 1.2, 1.3**

### Property 2: Registration Validation

*For any* student registration data, if any required field (title, abstract, supervisor, type) is empty or invalid, the registration SHALL be rejected; if all fields are valid, registration SHALL succeed.

**Validates: Requirements 2.3, 2.4**

### Property 3: Unique Presenter ID Generation

*For any* number of registered students, all generated presenter IDs SHALL be unique (no two students share the same presenter ID).

**Validates: Requirements 2.5**

### Property 4: Session Validation

*For any* session creation attempt, if date, venue, or session type is missing/invalid, creation SHALL fail; if all required fields are valid, creation SHALL succeed.

**Validates: Requirements 4.2, 4.3**

### Property 5: Assignment Conflict Detection

*For any* user (presenter or evaluator) already assigned to a session on a given date, attempting to assign them to another session on the same date SHALL be rejected.

**Validates: Requirements 5.4**

### Property 6: Score Range Validation

*For any* rubric score input, if the value is outside the range [1, 10], validation SHALL reject it; if within range, validation SHALL accept it.

**Validates: Requirements 6.4**

### Property 7: Total Score Calculation

*For any* set of rubric scores (problemClarity, methodology, results, presentation), the total score SHALL equal the sum of all four scores, and weighted score SHALL be calculated consistently.

**Validates: Requirements 6.6**

### Property 8: Board Assignment Uniqueness

*For any* poster board that is already assigned to a presenter, attempting to assign another presenter to the same board SHALL be rejected.

**Validates: Requirements 7.3**

### Property 9: Award Winner Computation

*For any* set of presenters with evaluations in a category (Oral/Poster), the computed winner SHALL be the presenter with the highest average evaluation score.

**Validates: Requirements 8.2, 8.3**

### Property 10: Data Persistence Round-Trip

*For any* DataStore object, serializing to file then deserializing SHALL produce an equivalent DataStore with all users, sessions, evaluations, and awards intact.

**Validates: Requirements 10.1, 10.2, 10.4**

### Property 11: Report Completeness

*For any* set of sessions and evaluations, the generated schedule report SHALL include all sessions, and the evaluation report SHALL include all evaluations.

**Validates: Requirements 9.2, 9.3**

### Property 12: Session Deletion Cascade

*For any* deleted session, all presenter and evaluator assignments to that session SHALL be removed, and the session SHALL not be retrievable.

**Validates: Requirements 4.5**

## Error Handling

### Input Validation Errors

| Error Condition | Handling |
|-----------------|----------|
| Empty required field | Display field-specific error message, highlight field |
| Invalid score range | Display "Score must be between 1 and 10" |
| Duplicate username | Display "Username already exists" |
| Invalid file path | Display "File not found or inaccessible" |

### Business Logic Errors

| Error Condition | Handling |
|-----------------|----------|
| Assignment conflict | Display warning dialog with conflict details |
| Board already assigned | Display "Board [ID] is already assigned to [presenter]" |
| No evaluations for award | Display "No evaluations available for this category" |
| Session has assignments | Confirm before deletion with cascade warning |

### System Errors

| Error Condition | Handling |
|-----------------|----------|
| File save failure | Display error dialog, suggest retry |
| File load failure | Start with empty data, show notification |
| Serialization error | Log error, display user-friendly message |

### Error Display Strategy

```java
public class ErrorHandler {
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    public static boolean confirmAction(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
```

## Testing Strategy

### Dual Testing Approach

This system requires both unit tests and property-based tests for comprehensive coverage:

- **Unit tests**: Verify specific examples, edge cases, and UI component behavior
- **Property tests**: Verify universal properties across randomly generated inputs

### Testing Framework

- **Unit Testing**: JUnit 5
- **Property-Based Testing**: jqwik (Java property-based testing library)
- **UI Testing**: Manual testing for Swing components (automated UI testing optional)

### Property-Based Test Configuration

- Minimum 100 iterations per property test
- Each property test references its design document property
- Tag format: **Feature: seminar-management-system, Property {number}: {property_text}**

### Test Categories

#### Unit Tests

1. **Model Tests**
   - User creation and field validation
   - Session creation with valid/invalid data
   - Evaluation score calculations
   - Award computation with specific examples

2. **Service Tests**
   - UserService authentication with known credentials
   - SessionService CRUD operations
   - EvaluationService score aggregation
   - ReportService output format verification

3. **Edge Case Tests**
   - Empty data scenarios
   - Boundary values (score = 1, score = 10)
   - Maximum field lengths
   - Special characters in text fields

#### Property-Based Tests

1. **Property 1**: Authentication correctness
2. **Property 2**: Registration validation
3. **Property 3**: Unique ID generation
4. **Property 4**: Session validation
5. **Property 5**: Assignment conflict detection
6. **Property 6**: Score range validation
7. **Property 7**: Total score calculation
8. **Property 8**: Board assignment uniqueness
9. **Property 9**: Award winner computation
10. **Property 10**: Data persistence round-trip
11. **Property 11**: Report completeness
12. **Property 12**: Session deletion cascade

### Test File Structure

```
src/
├── main/java/com/fci/seminar/
│   ├── model/
│   ├── service/
│   ├── ui/
│   └── util/
└── test/java/com/fci/seminar/
    ├── model/
    │   └── *Test.java (unit tests)
    ├── service/
    │   └── *Test.java (unit tests)
    └── property/
        └── *PropertyTest.java (property-based tests)
```
