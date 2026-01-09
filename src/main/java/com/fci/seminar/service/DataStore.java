package com.fci.seminar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.PosterBoard;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.User;

/**
 * Central data store for the Seminar Management System.
 * Uses MySQL database exclusively for persistence via DatabaseManager.
 * Requires MySQL to be running - no file-based fallback.
 */
public class DataStore {
    
    private DatabaseManager dbManager;
    private boolean databaseConnected = false;

    private Map<String, User> users;
    private Map<String, Session> sessions;
    private Map<String, Evaluation> evaluations;
    private Map<String, PosterBoard> posterBoards;
    private List<Award> awards;

    public DataStore() {
        this.users = new HashMap<>();
        this.sessions = new HashMap<>();
        this.evaluations = new HashMap<>();
        this.posterBoards = new HashMap<>();
        this.awards = new ArrayList<>();
        
        initDatabase();
    }
    
    /**
     * Initializes database connection and loads data.
     * @throws RuntimeException if database connection fails
     */
    private void initDatabase() {
        try {
            dbManager = DatabaseManager.getInstance();
            if (dbManager.isConnected()) {
                databaseConnected = true;
                loadFromDatabase();
                System.out.println("Connected to MySQL database successfully.");
            } else {
                databaseConnected = false;
                throw new RuntimeException("MySQL database is not available. Please start Laragon MySQL.");
            }
        } catch (Exception e) {
            databaseConnected = false;
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Please ensure:");
            System.err.println("1. Laragon is running");
            System.err.println("2. MySQL service is started (green indicator)");
            System.err.println("3. Database 'seminar_db' exists");
        }
    }
    
    /**
     * Loads all data from database into memory.
     */
    private void loadFromDatabase() {
        if (dbManager != null && dbManager.isConnected()) {
            this.users = dbManager.getAllUsers();
            this.sessions = dbManager.getAllSessions();
            this.evaluations = dbManager.getAllEvaluations();
            this.posterBoards = dbManager.getAllPosterBoards();
            this.awards = dbManager.getAllAwards();
        }
    }
    
    /**
     * Checks if database is connected.
     */
    public boolean isDatabaseConnected() {
        return databaseConnected && dbManager != null && dbManager.isConnected();
    }

    // User operations
    public void addUser(User user) {
        if (user != null && user.getId() != null) {
            users.put(user.getId(), user);
            if (dbManager != null && dbManager.isConnected()) {
                dbManager.saveUser(user);
            }
        }
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public void removeUser(String id) {
        users.remove(id);
        if (dbManager != null && dbManager.isConnected()) {
            dbManager.deleteUser(id);
        }
    }

    public Map<String, User> getUsers() {
        return new HashMap<>(users);
    }

    // Session operations
    public void addSession(Session session) {
        if (session != null && session.getSessionId() != null) {
            sessions.put(session.getSessionId(), session);
            if (dbManager != null && dbManager.isConnected()) {
                dbManager.saveSession(session);
            }
        }
    }

    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        if (dbManager != null && dbManager.isConnected()) {
            dbManager.deleteSession(sessionId);
        }
    }

    public Map<String, Session> getSessions() {
        return new HashMap<>(sessions);
    }
    
    /**
     * Updates a session in both memory and database.
     */
    public void updateSession(Session session) {
        if (session != null && session.getSessionId() != null) {
            sessions.put(session.getSessionId(), session);
            if (dbManager != null && dbManager.isConnected()) {
                dbManager.saveSession(session);
            }
        }
    }

    // Evaluation operations
    public void addEvaluation(Evaluation evaluation) {
        if (evaluation != null && evaluation.getEvaluationId() != null) {
            evaluations.put(evaluation.getEvaluationId(), evaluation);
            if (dbManager != null && dbManager.isConnected()) {
                dbManager.saveEvaluation(evaluation);
            }
        }
    }

    public Evaluation getEvaluation(String evaluationId) {
        return evaluations.get(evaluationId);
    }

    public void removeEvaluation(String evaluationId) {
        evaluations.remove(evaluationId);
        if (dbManager != null && dbManager.isConnected()) {
            dbManager.deleteEvaluation(evaluationId);
        }
    }

    public Map<String, Evaluation> getEvaluations() {
        return new HashMap<>(evaluations);
    }

    // PosterBoard operations
    public void addPosterBoard(PosterBoard posterBoard) {
        if (posterBoard != null && posterBoard.getBoardId() != null) {
            posterBoards.put(posterBoard.getBoardId(), posterBoard);
            if (dbManager != null && dbManager.isConnected()) {
                dbManager.savePosterBoard(posterBoard);
            }
        }
    }

    public PosterBoard getPosterBoard(String boardId) {
        return posterBoards.get(boardId);
    }

    public void removePosterBoard(String boardId) {
        posterBoards.remove(boardId);
        if (dbManager != null && dbManager.isConnected()) {
            dbManager.clearPosterBoard(boardId);
        }
    }

    public Map<String, PosterBoard> getPosterBoards() {
        return new HashMap<>(posterBoards);
    }

    // Award operations
    public void addAward(Award award) {
        if (award != null) {
            awards.add(award);
            if (dbManager != null && dbManager.isConnected()) {
                dbManager.saveAward(award);
            }
        }
    }

    public void clearAwards() {
        awards.clear();
        if (dbManager != null && dbManager.isConnected()) {
            dbManager.clearAwards();
        }
    }

    public List<Award> getAwards() {
        return new ArrayList<>(awards);
    }

    /**
     * Saves data to MySQL database.
     * Data is automatically saved on each operation, this method is for compatibility.
     * @param filepath ignored - MySQL only
     */
    public void save(String filepath) {
        if (dbManager != null && dbManager.isConnected()) {
            System.out.println("Data saved to MySQL database.");
        } else {
            System.err.println("Warning: Database not connected. Data not saved.");
        }
    }

    /**
     * Loads a DataStore from MySQL database.
     * @param filepath ignored - MySQL only
     * @return the loaded DataStore
     */
    public static DataStore load(String filepath) {
        return new DataStore();
    }
    
    /**
     * Checks if using database storage (always true now).
     */
    public static boolean isUsingDatabase() {
        return true;
    }
    
    /**
     * Gets the database manager instance.
     */
    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataStore dataStore = (DataStore) o;
        return Objects.equals(users, dataStore.users) &&
               Objects.equals(sessions, dataStore.sessions) &&
               Objects.equals(evaluations, dataStore.evaluations) &&
               Objects.equals(posterBoards, dataStore.posterBoards) &&
               Objects.equals(awards, dataStore.awards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, sessions, evaluations, posterBoards, awards);
    }
}
