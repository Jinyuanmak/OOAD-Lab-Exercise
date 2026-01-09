package com.fci.seminar.util;

import java.util.UUID;

/**
 * Utility class for generating unique IDs for various entities.
 * Uses UUID-based approach for guaranteed uniqueness.
 */
public final class IdGenerator {
    
    private IdGenerator() {
        // Prevent instantiation
    }

    /**
     * Generates a unique presenter ID.
     * Format: P-{UUID}
     * @return a unique presenter ID
     */
    public static String generatePresenterId() {
        return "P-" + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique session ID.
     * Format: S-{UUID}
     * @return a unique session ID
     */
    public static String generateSessionId() {
        return "S-" + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique evaluation ID.
     * Format: E-{UUID}
     * @return a unique evaluation ID
     */
    public static String generateEvaluationId() {
        return "E-" + UUID.randomUUID().toString();
    }

    /**
     * Generates a unique user ID.
     * Format: U-{UUID}
     * @return a unique user ID
     */
    public static String generateUserId() {
        return "U-" + UUID.randomUUID().toString();
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
