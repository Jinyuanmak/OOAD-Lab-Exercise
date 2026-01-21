package com.fci.seminar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an evaluator who reviews and scores presentations.
 */
public class Evaluator extends User {
    private static final long serialVersionUID = 1L;

    private List<String> assignedSessionIds;
    private String evaluatorId;

    public Evaluator() {
        super();
        setRole(UserRole.PANEL_MEMBER);
        this.assignedSessionIds = new ArrayList<>();
    }

    public Evaluator(String id, String username, String password) {
        super(id, username, password, UserRole.PANEL_MEMBER);
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
    
    public String getEvaluatorId() {
        return evaluatorId;
    }
    
    public void setEvaluatorId(String evaluatorId) {
        this.evaluatorId = evaluatorId;
    }
}
