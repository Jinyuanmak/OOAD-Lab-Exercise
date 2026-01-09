package com.fci.seminar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an evaluator who reviews and scores presentations.
 */
public class Evaluator extends User {
    private static final long serialVersionUID = 1L;

    private List<String> assignedSessionIds;

    public Evaluator() {
        super();
        setRole(UserRole.EVALUATOR);
        this.assignedSessionIds = new ArrayList<>();
    }

    public Evaluator(String id, String username, String password) {
        super(id, username, password, UserRole.EVALUATOR);
        this.assignedSessionIds = new ArrayList<>();
    }

    public List<String> getAssignedSessionIds() {
        return assignedSessionIds;
    }

    public void setAssignedSessionIds(List<String> assignedSessionIds) {
        this.assignedSessionIds = assignedSessionIds != null ? assignedSessionIds : new ArrayList<>();
    }

    public void addAssignedSession(String sessionId) {
        if (!assignedSessionIds.contains(sessionId)) {
            assignedSessionIds.add(sessionId);
        }
    }

    public void removeAssignedSession(String sessionId) {
        assignedSessionIds.remove(sessionId);
    }
}
