package com.fci.seminar.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an award given to a presenter at the seminar ceremony.
 */
public class Award implements Serializable {
    private static final long serialVersionUID = 1L;

    private AwardType type;
    private String winnerId;
    private double score;

    public Award() {
    }

    public Award(AwardType type, String winnerId, double score) {
        this.type = type;
        this.winnerId = winnerId;
        this.score = score;
    }

    public AwardType getType() {
        return type;
    }

    public void setType(AwardType type) {
        this.type = type;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Award award = (Award) o;
        return type == award.type && Objects.equals(winnerId, award.winnerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, winnerId);
    }
}
