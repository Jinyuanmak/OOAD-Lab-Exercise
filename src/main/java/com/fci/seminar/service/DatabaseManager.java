package com.fci.seminar.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.AwardType;
import com.fci.seminar.model.Coordinator;
import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.PosterBoard;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.model.UserRole;

/**
 * Database manager for MySQL operations.
 * Handles all database connections and CRUD operations.
 */
public class DatabaseManager {
    
    // Laragon default MySQL settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/seminar_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Laragon default is empty password
    
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        connect();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Establishes database connection.
     */
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Make sure Laragon MySQL is running and database 'seminar_db' exists.");
        }
    }
    
    /**
     * Gets the database connection, reconnecting if necessary.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }
    
    /**
     * Checks if database is connected.
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    // ==================== USER OPERATIONS ====================
    
    public void saveUser(User user) {
        // Check if this is a new user (no ID) or existing user
        boolean isNewUser = (user.getId() == null || user.getId().isEmpty());
        
        String sql;
        if (isNewUser) {
            // INSERT without ID - let database auto-generate
            sql = """
                INSERT INTO users (username, password, role, student_id, research_title, abstract_text, 
                                  supervisor_name, presentation_type, file_path, presenter_id, evaluator_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        } else {
            // UPDATE existing user
            sql = """
                INSERT INTO users (id, username, password, role, student_id, research_title, abstract_text, 
                                  supervisor_name, presentation_type, file_path, presenter_id, evaluator_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    username = VALUES(username),
                    password = VALUES(password),
                    role = VALUES(role),
                    student_id = VALUES(student_id),
                    research_title = VALUES(research_title),
                    abstract_text = VALUES(abstract_text),
                    supervisor_name = VALUES(supervisor_name),
                    presentation_type = VALUES(presentation_type),
                    file_path = VALUES(file_path),
                    presenter_id = VALUES(presenter_id),
                    evaluator_id = VALUES(evaluator_id)
                """;
        }
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql, 
                isNewUser ? PreparedStatement.RETURN_GENERATED_KEYS : PreparedStatement.NO_GENERATED_KEYS)) {
            
            int paramIndex = 1;
            
            // Set ID only for existing users
            if (!isNewUser) {
                stmt.setString(paramIndex++, user.getId());
            }
            
            stmt.setString(paramIndex++, user.getUsername());
            stmt.setString(paramIndex++, user.getPassword());
            stmt.setString(paramIndex++, user.getRole().name());
            
            if (user instanceof Student student) {
                stmt.setString(paramIndex++, student.getStudentId());
                stmt.setString(paramIndex++, student.getResearchTitle());
                stmt.setString(paramIndex++, student.getAbstractText());
                stmt.setString(paramIndex++, student.getSupervisorName());
                stmt.setString(paramIndex++, student.getPresentationType() != null ? 
                    student.getPresentationType().name() : null);
                stmt.setString(paramIndex++, student.getFilePath());
                stmt.setString(paramIndex++, student.getPresenterId());
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
            } else if (user instanceof Evaluator evaluator) {
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setString(paramIndex++, evaluator.getEvaluatorId());
            } else {
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
            }
            
            stmt.executeUpdate();
            
            // Get the generated ID for new users
            if (isNewUser) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(String.valueOf(generatedKeys.getInt(1)));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }
    
    public User getUser(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
        }
        return null;
    }
    
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by username: " + e.getMessage());
        }
        return null;
    }
    
    public Map<String, User> getAllUsers() {
        Map<String, User> users = new HashMap<>();
        String sql = "SELECT * FROM users";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.put(user.getId(), user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }
    
    public void deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;
        
        switch (UserRole.valueOf(role)) {
            case PRESENTER -> {
                Student student = new Student();
                student.setStudentId(rs.getString("student_id"));
                student.setResearchTitle(rs.getString("research_title"));
                student.setAbstractText(rs.getString("abstract_text"));
                student.setSupervisorName(rs.getString("supervisor_name"));
                String presType = rs.getString("presentation_type");
                if (presType != null) {
                    student.setPresentationType(PresentationType.valueOf(presType));
                }
                student.setFilePath(rs.getString("file_path"));
                student.setPresenterId(rs.getString("presenter_id"));
                user = student;
            }
            case PANEL_MEMBER -> {
                Evaluator evaluator = new Evaluator();
                evaluator.setEvaluatorId(rs.getString("evaluator_id"));
                // Load assigned session IDs
                evaluator.setAssignedSessionIds(getEvaluatorSessionIds(rs.getString("id")));
                user = evaluator;
            }
            case COORDINATOR -> user = new Coordinator();
            default -> throw new SQLException("Unknown role: " + role);
        }
        
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.valueOf(role));
        
        return user;
    }
    
    private List<String> getEvaluatorSessionIds(String evaluatorId) {
        List<String> sessionIds = new ArrayList<>();
        String sql = "SELECT session_id FROM session_evaluators WHERE evaluator_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, evaluatorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sessionIds.add(rs.getString("session_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting evaluator sessions: " + e.getMessage());
        }
        return sessionIds;
    }
    
    // ==================== SESSION OPERATIONS ====================
    
    public void saveSession(Session session) {
        String sql = """
            INSERT INTO sessions (session_id, session_date, venue, session_type)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                session_date = VALUES(session_date),
                venue = VALUES(venue),
                session_type = VALUES(session_type)
            """;
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, session.getSessionId());
            stmt.setDate(2, java.sql.Date.valueOf(session.getDate()));
            stmt.setString(3, session.getVenue());
            stmt.setString(4, session.getSessionType().name());
            stmt.executeUpdate();
            
            // Save presenter assignments
            saveSessionPresenters(session);
            // Save evaluator assignments
            saveSessionEvaluators(session);
            
        } catch (SQLException e) {
            System.err.println("Error saving session: " + e.getMessage());
        }
    }
    
    private void saveSessionPresenters(Session session) {
        // Clear existing assignments
        String deleteSql = "DELETE FROM session_presenters WHERE session_id = ?";
        String insertSql = "INSERT INTO session_presenters (session_id, presenter_id) VALUES (?, ?)";
        
        try {
            try (PreparedStatement deleteStmt = getConnection().prepareStatement(deleteSql)) {
                deleteStmt.setString(1, session.getSessionId());
                deleteStmt.executeUpdate();
            }
            
            for (String presenterId : session.getPresenterIds()) {
                try (PreparedStatement insertStmt = getConnection().prepareStatement(insertSql)) {
                    insertStmt.setString(1, session.getSessionId());
                    insertStmt.setString(2, presenterId);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving session presenters: " + e.getMessage());
        }
    }
    
    private void saveSessionEvaluators(Session session) {
        String deleteSql = "DELETE FROM session_evaluators WHERE session_id = ?";
        String insertSql = "INSERT INTO session_evaluators (session_id, evaluator_id) VALUES (?, ?)";
        
        try {
            try (PreparedStatement deleteStmt = getConnection().prepareStatement(deleteSql)) {
                deleteStmt.setString(1, session.getSessionId());
                deleteStmt.executeUpdate();
            }
            
            for (String evaluatorId : session.getEvaluatorIds()) {
                try (PreparedStatement insertStmt = getConnection().prepareStatement(insertSql)) {
                    insertStmt.setString(1, session.getSessionId());
                    insertStmt.setString(2, evaluatorId);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving session evaluators: " + e.getMessage());
        }
    }
    
    public Session getSession(String sessionId) {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting session: " + e.getMessage());
        }
        return null;
    }
    
    public Map<String, Session> getAllSessions() {
        Map<String, Session> sessions = new HashMap<>();
        String sql = "SELECT * FROM sessions";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Session session = mapResultSetToSession(rs);
                sessions.put(session.getSessionId(), session);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all sessions: " + e.getMessage());
        }
        return sessions;
    }
    
    public void deleteSession(String sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting session: " + e.getMessage());
        }
    }
    
    private Session mapResultSetToSession(ResultSet rs) throws SQLException {
        Session session = new Session();
        session.setSessionId(rs.getString("session_id"));
        session.setDate(rs.getDate("session_date").toLocalDate());
        session.setVenue(rs.getString("venue"));
        session.setSessionType(PresentationType.valueOf(rs.getString("session_type")));
        
        // Load presenter IDs
        session.setPresenterIds(getSessionPresenterIds(session.getSessionId()));
        // Load evaluator IDs
        session.setEvaluatorIds(getSessionEvaluatorIds(session.getSessionId()));
        
        return session;
    }
    
    private List<String> getSessionPresenterIds(String sessionId) {
        List<String> presenterIds = new ArrayList<>();
        String sql = "SELECT presenter_id FROM session_presenters WHERE session_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                presenterIds.add(rs.getString("presenter_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting session presenters: " + e.getMessage());
        }
        return presenterIds;
    }
    
    private List<String> getSessionEvaluatorIds(String sessionId) {
        List<String> evaluatorIds = new ArrayList<>();
        String sql = "SELECT evaluator_id FROM session_evaluators WHERE session_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                evaluatorIds.add(rs.getString("evaluator_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting session evaluators: " + e.getMessage());
        }
        return evaluatorIds;
    }
    
    // ==================== EVALUATION OPERATIONS ====================
    
    public void saveEvaluation(Evaluation evaluation) {
        String sql = """
            INSERT INTO evaluations (evaluation_id, presenter_id, evaluator_id, session_id,
                                    problem_clarity, methodology, results, presentation, comments)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                problem_clarity = VALUES(problem_clarity),
                methodology = VALUES(methodology),
                results = VALUES(results),
                presentation = VALUES(presentation),
                comments = VALUES(comments)
            """;
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, evaluation.getEvaluationId());
            stmt.setString(2, evaluation.getPresenterId());
            stmt.setString(3, evaluation.getEvaluatorId());
            stmt.setString(4, evaluation.getSessionId());
            stmt.setInt(5, evaluation.getScores().getProblemClarity());
            stmt.setInt(6, evaluation.getScores().getMethodology());
            stmt.setInt(7, evaluation.getScores().getResults());
            stmt.setInt(8, evaluation.getScores().getPresentation());
            stmt.setString(9, evaluation.getComments());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving evaluation: " + e.getMessage());
        }
    }
    
    public void updateEvaluation(Evaluation evaluation) {
        // Use saveEvaluation since it already handles updates via ON DUPLICATE KEY UPDATE
        saveEvaluation(evaluation);
    }
    
    public Evaluation getEvaluation(String evaluationId) {
        String sql = "SELECT * FROM evaluations WHERE evaluation_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, evaluationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEvaluation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting evaluation: " + e.getMessage());
        }
        return null;
    }
    
    public Map<String, Evaluation> getAllEvaluations() {
        Map<String, Evaluation> evaluations = new HashMap<>();
        String sql = "SELECT * FROM evaluations";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Evaluation evaluation = mapResultSetToEvaluation(rs);
                evaluations.put(evaluation.getEvaluationId(), evaluation);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all evaluations: " + e.getMessage());
        }
        return evaluations;
    }
    
    public void deleteEvaluation(String evaluationId) {
        String sql = "DELETE FROM evaluations WHERE evaluation_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, evaluationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting evaluation: " + e.getMessage());
        }
    }
    
    private Evaluation mapResultSetToEvaluation(ResultSet rs) throws SQLException {
        Evaluation evaluation = new Evaluation();
        evaluation.setEvaluationId(rs.getString("evaluation_id"));
        evaluation.setPresenterId(rs.getString("presenter_id"));
        evaluation.setEvaluatorId(rs.getString("evaluator_id"));
        evaluation.setSessionId(rs.getString("session_id"));
        
        RubricScores scores = new RubricScores(
            rs.getInt("problem_clarity"),
            rs.getInt("methodology"),
            rs.getInt("results"),
            rs.getInt("presentation")
        );
        evaluation.setScores(scores);
        evaluation.setComments(rs.getString("comments"));
        evaluation.setTimestamp(rs.getTimestamp("submitted_at").toLocalDateTime());
        
        return evaluation;
    }
    
    // ==================== POSTER BOARD OPERATIONS ====================
    
    public void savePosterBoard(PosterBoard board) {
        String sql = """
            INSERT INTO poster_boards (board_id, presenter_id, session_id)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
                presenter_id = VALUES(presenter_id),
                session_id = VALUES(session_id)
            """;
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, board.getBoardId());
            stmt.setString(2, board.getPresenterId());
            stmt.setString(3, board.getSessionId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving poster board: " + e.getMessage());
        }
    }
    
    public PosterBoard getPosterBoard(String boardId) {
        String sql = "SELECT * FROM poster_boards WHERE board_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, boardId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPosterBoard(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting poster board: " + e.getMessage());
        }
        return null;
    }
    
    public Map<String, PosterBoard> getAllPosterBoards() {
        Map<String, PosterBoard> boards = new HashMap<>();
        String sql = "SELECT * FROM poster_boards";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PosterBoard board = mapResultSetToPosterBoard(rs);
                boards.put(board.getBoardId(), board);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all poster boards: " + e.getMessage());
        }
        return boards;
    }
    
    public void clearPosterBoard(String boardId) {
        String sql = "DELETE FROM poster_boards WHERE board_id = ?";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, boardId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error clearing poster board: " + e.getMessage());
        }
    }
    
    private PosterBoard mapResultSetToPosterBoard(ResultSet rs) throws SQLException {
        PosterBoard board = new PosterBoard();
        board.setBoardId(rs.getString("board_id"));
        board.setPresenterId(rs.getString("presenter_id"));
        board.setSessionId(rs.getString("session_id"));
        return board;
    }
    
    // ==================== AWARD OPERATIONS ====================
    
    public void saveAward(Award award) {
        String sql = "INSERT INTO awards (award_type, winner_id, score, ceremony_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, award.getType().name());
            stmt.setString(2, award.getWinnerId());
            stmt.setDouble(3, award.getScore());
            stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving award: " + e.getMessage());
        }
    }
    
    public List<Award> getAllAwards() {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT * FROM awards ORDER BY created_at DESC";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Award award = new Award();
                award.setType(AwardType.valueOf(rs.getString("award_type")));
                award.setWinnerId(rs.getString("winner_id"));
                award.setScore(rs.getDouble("score"));
                awards.add(award);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all awards: " + e.getMessage());
        }
        return awards;
    }
    
    public void clearAwards() {
        String sql = "DELETE FROM awards";
        
        try (Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error clearing awards: " + e.getMessage());
        }
    }
    
    // ==================== VENUE OPERATIONS ====================
    
    /**
     * Gets all venues from the database.
     * @return list of venue names
     */
    public List<String> getAllVenues() {
        List<String> venues = new ArrayList<>();
        String sql = "SELECT venue_name FROM venues ORDER BY venue_name";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                venues.add(rs.getString("venue_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting venues: " + e.getMessage());
            // Return default venues if table doesn't exist
            venues.add("Auditorium A");
            venues.add("Auditorium B");
            venues.add("Conference Room 1");
            venues.add("Conference Room 2");
            venues.add("Exhibition Hall A");
            venues.add("Exhibition Hall B");
            venues.add("Lecture Hall 1");
            venues.add("Lecture Hall 2");
        }
        return venues;
    }
    
    /**
     * Checks if venues table exists and has data.
     * @return true if venues exist
     */
    public boolean hasVenues() {
        String sql = "SELECT COUNT(*) FROM venues";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            // Table might not exist
            return false;
        }
        return false;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Checks if sample data exists in database.
     */
    public boolean hasSampleData() {
        String sql = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking sample data: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Tests database connection.
     */
    public boolean testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("SELECT 1");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
        }
        return false;
    }
}
