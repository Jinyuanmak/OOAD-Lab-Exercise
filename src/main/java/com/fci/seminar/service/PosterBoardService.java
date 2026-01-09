package com.fci.seminar.service;

import java.util.ArrayList;
import java.util.List;

import com.fci.seminar.model.PosterBoard;

/**
 * Service class for poster board management operations.
 * Handles board assignments and availability checks.
 */
public class PosterBoardService {
    
    private final DataStore dataStore;
    private static final int MAX_BOARDS = 100; // Maximum number of boards available

    public PosterBoardService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Assigns a poster board to a presenter for a session.
     * @param boardId the board ID
     * @param presenterId the presenter ID
     * @param sessionId the session ID
     * @throws IllegalArgumentException if board is already assigned
     */
    public void assignBoard(String boardId, String presenterId, String sessionId) {
        if (boardId == null || boardId.trim().isEmpty()) {
            throw new IllegalArgumentException("Board ID is required");
        }
        if (presenterId == null || presenterId.trim().isEmpty()) {
            throw new IllegalArgumentException("Presenter ID is required");
        }
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID is required");
        }
        
        // Check if board is already assigned
        if (isBoardAssigned(boardId)) {
            PosterBoard existing = dataStore.getPosterBoard(boardId);
            throw new IllegalArgumentException(
                "Board " + boardId + " is already assigned to presenter " + existing.getPresenterId());
        }
        
        PosterBoard posterBoard = new PosterBoard(boardId, presenterId, sessionId);
        dataStore.addPosterBoard(posterBoard);
    }

    /**
     * Checks if a board is already assigned.
     * @param boardId the board ID to check
     * @return true if the board is assigned, false otherwise
     */
    public boolean isBoardAssigned(String boardId) {
        return dataStore.getPosterBoard(boardId) != null;
    }

    /**
     * Gets a list of available (unassigned) board IDs.
     * @return list of available board IDs
     */
    public List<String> getAvailableBoards() {
        List<String> available = new ArrayList<>();
        for (int i = 1; i <= MAX_BOARDS; i++) {
            String boardId = String.format("B%03d", i);
            if (!isBoardAssigned(boardId)) {
                available.add(boardId);
            }
        }
        return available;
    }

    /**
     * Gets all poster board assignments.
     * @return list of all poster board assignments
     */
    public List<PosterBoard> getAllAssignments() {
        return new ArrayList<>(dataStore.getPosterBoards().values());
    }

    /**
     * Gets a poster board by ID.
     * @param boardId the board ID
     * @return the poster board if found, null otherwise
     */
    public PosterBoard getBoardById(String boardId) {
        return dataStore.getPosterBoard(boardId);
    }

    /**
     * Removes a board assignment.
     * @param boardId the board ID to unassign
     */
    public void unassignBoard(String boardId) {
        dataStore.removePosterBoard(boardId);
    }

    /**
     * Gets all board assignments for a specific session.
     * @param sessionId the session ID
     * @return list of poster boards for the session
     */
    public List<PosterBoard> getBoardsForSession(String sessionId) {
        List<PosterBoard> result = new ArrayList<>();
        for (PosterBoard board : dataStore.getPosterBoards().values()) {
            if (sessionId.equals(board.getSessionId())) {
                result.add(board);
            }
        }
        return result;
    }
}
