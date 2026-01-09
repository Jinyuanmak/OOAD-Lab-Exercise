package com.fci.seminar.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.User;
import com.fci.seminar.util.IdGenerator;

/**
 * Service class for session management operations.
 * Handles session creation, updates, deletion, and assignments.
 */
public class SessionService {
    
    private final DataStore dataStore;

    public SessionService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Creates a new session with validation.
     * @param date the session date
     * @param venue the session venue
     * @param type the presentation type (ORAL or POSTER)
     * @return the created session
     * @throws IllegalArgumentException if validation fails
     */
    public Session createSession(LocalDate date, String venue, PresentationType type) {
        validateSessionData(date, venue, type);
        
        String sessionId = IdGenerator.generateSessionId();
        Session session = new Session(sessionId, date, venue, type);
        dataStore.addSession(session);
        
        return session;
    }

    /**
     * Validates session data.
     */
    private void validateSessionData(LocalDate date, String venue, PresentationType type) {
        if (date == null) {
            throw new IllegalArgumentException("Session date is required");
        }
        if (venue == null || venue.trim().isEmpty()) {
            throw new IllegalArgumentException("Session venue is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Session type is required");
        }
    }


    /**
     * Updates an existing session.
     * @param session the session with updated data
     * @throws IllegalArgumentException if session is null or doesn't exist
     */
    public void updateSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        if (session.getSessionId() == null || dataStore.getSession(session.getSessionId()) == null) {
            throw new IllegalArgumentException("Session does not exist");
        }
        
        validateSessionData(session.getDate(), session.getVenue(), session.getSessionType());
        dataStore.addSession(session); // Overwrites existing session
    }

    /**
     * Deletes a session and removes all linked presenter and evaluator assignments.
     * @param sessionId the session ID to delete
     * @throws IllegalArgumentException if session doesn't exist
     */
    public void deleteSession(String sessionId) {
        Session session = dataStore.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session does not exist");
        }
        
        // Remove evaluator assignments
        for (String evaluatorId : session.getEvaluatorIds()) {
            User user = dataStore.getUser(evaluatorId);
            if (user instanceof Evaluator) {
                ((Evaluator) user).removeAssignedSession(sessionId);
            }
        }
        
        // Remove the session
        dataStore.removeSession(sessionId);
    }

    /**
     * Assigns a presenter to a session.
     * @param sessionId the session ID
     * @param presenterId the presenter ID
     * @throws IllegalArgumentException if session doesn't exist or conflict detected
     */
    public void assignPresenter(String sessionId, String presenterId) {
        Session session = dataStore.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session does not exist");
        }
        
        // Check for conflicts
        if (hasConflict(presenterId, session.getDate())) {
            throw new IllegalArgumentException("Presenter already assigned to another session on this date");
        }
        
        session.addPresenter(presenterId);
    }

    /**
     * Assigns an evaluator to a session.
     * @param sessionId the session ID
     * @param evaluatorId the evaluator ID
     * @throws IllegalArgumentException if session doesn't exist or conflict detected
     */
    public void assignEvaluator(String sessionId, String evaluatorId) {
        Session session = dataStore.getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session does not exist");
        }
        
        // Check for conflicts
        if (hasConflict(evaluatorId, session.getDate())) {
            throw new IllegalArgumentException("Evaluator already assigned to another session on this date");
        }
        
        session.addEvaluator(evaluatorId);
        
        // Update evaluator's assigned sessions
        User user = dataStore.getUser(evaluatorId);
        if (user instanceof Evaluator) {
            ((Evaluator) user).addAssignedSession(sessionId);
        }
    }


    /**
     * Checks if a user (presenter or evaluator) has a conflict on the given date.
     * @param userId the user ID (presenter ID or evaluator ID)
     * @param date the date to check
     * @return true if there's a conflict, false otherwise
     */
    public boolean hasConflict(String userId, LocalDate date) {
        if (userId == null || date == null) {
            return false;
        }
        
        for (Session session : dataStore.getSessions().values()) {
            if (session.getDate().equals(date)) {
                // Check if user is already a presenter in this session
                if (session.getPresenterIds().contains(userId)) {
                    return true;
                }
                // Check if user is already an evaluator in this session
                if (session.getEvaluatorIds().contains(userId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves all sessions.
     * @return list of all sessions
     */
    public List<Session> getAllSessions() {
        return new ArrayList<>(dataStore.getSessions().values());
    }

    /**
     * Retrieves a session by ID.
     * @param sessionId the session ID
     * @return the session if found, null otherwise
     */
    public Session getSessionById(String sessionId) {
        return dataStore.getSession(sessionId);
    }

    /**
     * Removes a presenter from a session.
     * @param sessionId the session ID
     * @param presenterId the presenter ID to remove
     */
    public void removePresenter(String sessionId, String presenterId) {
        Session session = dataStore.getSession(sessionId);
        if (session != null) {
            session.removePresenter(presenterId);
        }
    }

    /**
     * Removes an evaluator from a session.
     * @param sessionId the session ID
     * @param evaluatorId the evaluator ID to remove
     */
    public void removeEvaluator(String sessionId, String evaluatorId) {
        Session session = dataStore.getSession(sessionId);
        if (session != null) {
            session.removeEvaluator(evaluatorId);
            
            // Update evaluator's assigned sessions
            User user = dataStore.getUser(evaluatorId);
            if (user instanceof Evaluator) {
                ((Evaluator) user).removeAssignedSession(sessionId);
            }
        }
    }
}
