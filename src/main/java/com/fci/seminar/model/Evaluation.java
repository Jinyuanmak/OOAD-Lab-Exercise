package com.fci.seminar.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an evaluation of a presenter by an evaluator.
 */
public class Evaluation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String evaluationId;
    private String presenterId;
    private String evaluatorId;
    private String sessionId;
    private RubricScores scores;
    private String comments;
    private LocalDateTime timestamp;

    public Evaluation() {
        this.timestamp = LocalDateTime.now();
    }

    public Evaluation(String evaluationId, String presenterId, String evaluatorId, 
                      String sessionId, RubricScores scores, String comments) {
        this.evaluationId = evaluationId;
        this.presenterId = presenterId;
        this.evaluatorId = evaluatorId;
        this.sessionId = sessionId;
        this.scores = scores;
        this.comments = comments;
        this.timestamp = LocalDateTime.now();
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    public String getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(String evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public RubricScores getScores() {
        return scores;
    }

    public void setScores(RubricScores scores) {
        this.scores = scores;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation that = (Evaluation) o;
        return Objects.equals(evaluationId, that.evaluationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evaluationId);
    }
}
