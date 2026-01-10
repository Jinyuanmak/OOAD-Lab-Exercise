package com.fci.seminar.util;

import java.util.UUID;

/**
 * Utility class for generating unique IDs for various entities.
 * Uses shortened UUID-based approach for uniqueness with readability.
 */
public final class IdGenerator {
    
    private IdGenerator() {
        // Prevent instantiation
    }
    
    /**
     * Generates a short unique ID (8 characters from UUID).
     * @return a short unique ID string
     */
    private static String shortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generates a unique presenter ID.
     * Format: P-{8chars}
     * @return a unique presenter ID
     */
    public static String generatePresenterId() {
        return "P-" + shortId();
    }

    /**
     * Generates a unique session ID.
     * Format: S-{8chars}
     * @return a unique session ID
     */
    public static String generateSessionId() {
        return "S-" + shortId();
    }

    /**
     * Generates a unique evaluation ID.
     * Format: E-{8chars}
     * @return a unique evaluation ID
     */
    public static String generateEvaluationId() {
        return "E-" + shortId();
    }

    /**
     * Generates a unique user ID.
     * Format: U-{8chars}
     * @return a unique user ID
     */
    public static String generateUserId() {
        return "U-" + shortId();
    }

    /**
     * Generates a unique board ID.
     * Format: B{number} (e.g., B001, B002)
     * @param number the board number
     * @return a formatted board ID
     */
    public static String generateBoardId(int number) {
        return String.format("B%03d", number);
    }
}
