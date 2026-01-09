package com.fci.seminar.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a poster board assignment for poster presentations.
 */
public class PosterBoard implements Serializable {
    private static final long serialVersionUID = 1L;

    private String boardId;
    private String presenterId;
    private String sessionId;

    public PosterBoard() {
    }

    public PosterBoard(String boardId, String presenterId, String sessionId) {
        this.boardId = boardId;
        this.presenterId = presenterId;
        this.sessionId = sessionId;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosterBoard that = (PosterBoard) o;
        return Objects.equals(boardId, that.boardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId);
    }
}
