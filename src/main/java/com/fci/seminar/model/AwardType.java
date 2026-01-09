package com.fci.seminar.model;

/**
 * Enum representing the types of awards given at the seminar ceremony.
 */
public enum AwardType {
    BEST_ORAL("Best Oral Presentation"),
    BEST_POSTER("Best Poster Presentation"),
    PEOPLES_CHOICE("People's Choice Award");

    private final String displayName;

    AwardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
