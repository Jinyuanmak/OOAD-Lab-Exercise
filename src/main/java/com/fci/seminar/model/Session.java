package com.fci.seminar.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a scheduled session for presentations at a specific venue.
 */
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sessionId;
    private LocalDate date;
    private String venue;
    private PresentationType sessionType;
    private List<String> presenterIds;
    private List<String> evaluatorIds;

    public Session() {
        this.presenterIds = new ArrayList<>();
        this.evaluatorIds = new ArrayList<>();
    }

    public Session(String sessionId, LocalDate date, String venue, PresentationType sessionType) {
        this.sessionId = sessionId;
        this.date = date;
        this.venue = venue;
        this.sessionType = sessionType;
        this.presenterIds = new ArrayList<>();
        this.evaluatorIds = new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public PresentationType getSessionType() {
        return sessionType;
    }

    public void setSessionType(PresentationType sessionType) {
        this.sessionType = sessionType;
    }

    public List<String> getPresenterIds() {
        return presenterIds;
    }

    public void setPresenterIds(List<String> presenterIds) {
        this.presenterIds = presenterIds != null ? presenterIds : new ArrayList<>();
    }

    public List<String> getEvaluatorIds() {
        return evaluatorIds;
    }

    public void setEvaluatorIds(List<String> evaluatorIds) {
        this.evaluatorIds = evaluatorIds != null ? evaluatorIds : new ArrayList<>();
    }

    public void addPresenter(String presenterId) {
        if (!presenterIds.contains(presenterId)) {
            presenterIds.add(presenterId);
        }
    }

    public void removePresenter(String presenterId) {
        presenterIds.remove(presenterId);
    }

    public void addEvaluator(String evaluatorId) {
        if (!evaluatorIds.contains(evaluatorId)) {
            evaluatorIds.add(evaluatorId);
        }
    }

    public void removeEvaluator(String evaluatorId) {
        evaluatorIds.remove(evaluatorId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(sessionId, session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
