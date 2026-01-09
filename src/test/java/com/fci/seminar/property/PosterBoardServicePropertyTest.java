package com.fci.seminar.property;

import com.fci.seminar.model.PosterBoard;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.PosterBoardService;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for PosterBoardService.
 * Feature: seminar-management-system
 */
class PosterBoardServicePropertyTest {

    // ========================================================================
    // Property 8: Board Assignment Uniqueness
    // For any poster board that is already assigned to a presenter, attempting
    // to assign another presenter to the same board SHALL be rejected.
    // Validates: Requirements 7.3
    // ========================================================================

    @Property(tries = 100)
    void boardAssignmentSucceedsForUnassignedBoard(
            @ForAll("boardId") String boardId,
            @ForAll("presenterId") String presenterId,
            @ForAll("sessionId") String sessionId) {
        
        DataStore dataStore = new DataStore();
        PosterBoardService service = new PosterBoardService(dataStore);
        
        // Assign board to presenter
        service.assignBoard(boardId, presenterId, sessionId);
        
        // Board should be assigned
        assert service.isBoardAssigned(boardId) :
            "Board should be assigned after assignment";
        
        PosterBoard board = service.getBoardById(boardId);
        assert board != null : "Board should be retrievable";
        assert board.getPresenterId().equals(presenterId) :
            "Board should be assigned to correct presenter";
    }

    @Property(tries = 100)
    void duplicateBoardAssignmentIsRejected(
            @ForAll("boardId") String boardId,
            @ForAll("presenterId") String presenter1,
            @ForAll("presenterId") String presenter2,
            @ForAll("sessionId") String sessionId) {
        
        DataStore dataStore = new DataStore();
        PosterBoardService service = new PosterBoardService(dataStore);
        
        // First assignment should succeed
        service.assignBoard(boardId, presenter1, sessionId);
        
        // Second assignment to same board should fail
        try {
            service.assignBoard(boardId, presenter2, sessionId);
            assert false : "Duplicate board assignment should be rejected";
        } catch (IllegalArgumentException e) {
            // Expected - board already assigned
            assert e.getMessage().contains(boardId) :
                "Error message should mention the board ID";
        }
    }

    @Property(tries = 100)
    void assignedBoardNotInAvailableList(
            @ForAll("boardNumber") int boardNumber,
            @ForAll("presenterId") String presenterId,
            @ForAll("sessionId") String sessionId) {
        
        DataStore dataStore = new DataStore();
        PosterBoardService service = new PosterBoardService(dataStore);
        
        String boardId = String.format("B%03d", boardNumber);
        
        // Assign the board
        service.assignBoard(boardId, presenterId, sessionId);
        
        // Board should not be in available list
        assert !service.getAvailableBoards().contains(boardId) :
            "Assigned board should not be in available list";
    }

    @Property(tries = 100)
    void unassignedBoardInAvailableList(
            @ForAll("boardNumber") int boardNumber) {
        
        DataStore dataStore = new DataStore();
        PosterBoardService service = new PosterBoardService(dataStore);
        
        String boardId = String.format("B%03d", boardNumber);
        
        // Board should be in available list (nothing assigned yet)
        assert service.getAvailableBoards().contains(boardId) :
            "Unassigned board should be in available list";
    }

    // Providers

    @Provide
    Arbitrary<String> boardId() {
        return Arbitraries.integers().between(1, 100)
            .map(n -> String.format("B%03d", n));
    }

    @Provide
    Arbitrary<Integer> boardNumber() {
        return Arbitraries.integers().between(1, 100);
    }

    @Provide
    Arbitrary<String> presenterId() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(8)
            .map(s -> "P-" + s);
    }

    @Provide
    Arbitrary<String> sessionId() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(8)
            .map(s -> "S-" + s);
    }
}
