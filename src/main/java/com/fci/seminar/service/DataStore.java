package com.fci.seminar.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
 * Manages all entities and provides persistence through serialization.
 */
public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;

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
    }

    // User operations
    public void addUser(User user) {
        if (user != null && user.getId() != null) {
            users.put(user.getId(), user);
        }
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public void removeUser(String id) {
        users.remove(id);
    }

    public Map<String, User> getUsers() {
        return new HashMap<>(users);
    }

    // Session operations
    public void addSession(Session session) {
        if (session != null && session.getSessionId() != null) {
            sessions.put(session.getSessionId(), session);
        }
    }

    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public Map<String, Session> getSessions() {
        return new HashMap<>(sessions);
    }

    // Evaluation operations
    public void addEvaluation(Evaluation evaluation) {
        if (evaluation != null && evaluation.getEvaluationId() != null) {
            evaluations.put(evaluation.getEvaluationId(), evaluation);
        }
    }

    public Evaluation getEvaluation(String evaluationId) {
        return evaluations.get(evaluationId);
    }

    public void removeEvaluation(String evaluationId) {
        evaluations.remove(evaluationId);
    }

    public Map<String, Evaluation> getEvaluations() {
        return new HashMap<>(evaluations);
    }

    // PosterBoard operations
    public void addPosterBoard(PosterBoard posterBoard) {
        if (posterBoard != null && posterBoard.getBoardId() != null) {
            posterBoards.put(posterBoard.getBoardId(), posterBoard);
        }
    }

    public PosterBoard getPosterBoard(String boardId) {
        return posterBoards.get(boardId);
    }

    public void removePosterBoard(String boardId) {
        posterBoards.remove(boardId);
    }

    public Map<String, PosterBoard> getPosterBoards() {
        return new HashMap<>(posterBoards);
    }

    // Award operations
    public void addAward(Award award) {
        if (award != null) {
            awards.add(award);
        }
    }

    public void clearAwards() {
        awards.clear();
    }

    public List<Award> getAwards() {
        return new ArrayList<>(awards);
    }

    /**
     * Saves the DataStore to a file using ObjectOutputStream.
     * @param filepath the path to save the data
     * @throws IOException if an I/O error occurs
     */
    public void save(String filepath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath))) {
            oos.writeObject(this);
        }
    }

    /**
     * Loads a DataStore from a file using ObjectInputStream.
     * @param filepath the path to load the data from
     * @return the loaded DataStore, or a new empty DataStore if loading fails
     */
    public static DataStore load(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            return new DataStore();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath))) {
            return (DataStore) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new DataStore();
        }
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
