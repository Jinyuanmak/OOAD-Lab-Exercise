package com.fci.seminar.property;

import java.time.LocalDate;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Session;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.SessionService;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for SessionService.
 * Feature: seminar-management-system
 */
class SessionServicePropertyTest {

    // ========================================================================
    // Property 4: Session Validation
    // For any session creation attempt, if date, venue, or session type is
    // missing/invalid, creation SHALL fail; if all required fields are valid,
    // creation SHALL succeed.
    // Validates: Requirements 4.2, 4.3
    // ========================================================================

    @Property(tries = 100)
    void sessionCreationSucceedsWithValidData(
            @ForAll("validSessionData") SessionData data) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        Session session = sessionService.createSession(data.date, data.venue, data.type);
        
        assert session != null : "Session should be created with valid data";
        assert session.getSessionId() != null : "Session ID should be generated";
        assert session.getDate().equals(data.date) : "Session date should match";
        assert session.getVenue().equals(data.venue) : "Session venue should match";
        assert session.getSessionType() == data.type : "Session type should match";
    }

    @Property(tries = 100)
    void sessionCreationFailsWithNullDate(
            @ForAll("validVenue") String venue,
            @ForAll("presentationType") PresentationType type) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        try {
            sessionService.createSession(null, venue, type);
            assert false : "Session creation should fail with null date";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("date") || e.getMessage().contains("Date") :
                "Error message should mention date";
        }
    }


    @Property(tries = 100)
    void sessionCreationFailsWithEmptyVenue(
            @ForAll("validDate") LocalDate date,
            @ForAll("presentationType") PresentationType type) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        try {
            sessionService.createSession(date, "", type);
            assert false : "Session creation should fail with empty venue";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("venue") || e.getMessage().contains("Venue") :
                "Error message should mention venue";
        }
    }

    @Property(tries = 100)
    void sessionCreationFailsWithNullType(
            @ForAll("validDate") LocalDate date,
            @ForAll("validVenue") String venue) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        try {
            sessionService.createSession(date, venue, null);
            assert false : "Session creation should fail with null type";
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("type") || e.getMessage().contains("Type") :
                "Error message should mention type";
        }
    }

    // ========================================================================
    // Property 5: Assignment Conflict Detection
    // For any user (presenter or evaluator) already assigned to a session on a
    // given date, attempting to assign them to another session on the same date
    // SHALL be rejected.
    // Validates: Requirements 5.4
    // ========================================================================

    @Property(tries = 100)
    void presenterConflictDetectedOnSameDate(
            @ForAll("validDate") LocalDate date,
            @ForAll("validVenue") String venue1,
            @ForAll("validVenue") String venue2,
            @ForAll("presenterId") String presenterId) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        // Create first session and assign presenter
        Session session1 = sessionService.createSession(date, venue1, PresentationType.ORAL);
        sessionService.assignPresenter(session1.getSessionId(), presenterId);
        
        // Create second session on same date
        Session session2 = sessionService.createSession(date, venue2, PresentationType.ORAL);
        
        // Attempt to assign same presenter to second session should fail
        try {
            sessionService.assignPresenter(session2.getSessionId(), presenterId);
            assert false : "Should detect conflict when assigning presenter to same date";
        } catch (IllegalArgumentException e) {
            // Expected - conflict detected
        }
    }

    @Property(tries = 100)
    void evaluatorConflictDetectedOnSameDate(
            @ForAll("validDate") LocalDate date,
            @ForAll("validVenue") String venue1,
            @ForAll("validVenue") String venue2,
            @ForAll("evaluatorId") String evaluatorId) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        // Add evaluator to data store
        Evaluator evaluator = new Evaluator(evaluatorId, "eval", "pass");
        dataStore.addUser(evaluator);
        
        // Create first session and assign evaluator
        Session session1 = sessionService.createSession(date, venue1, PresentationType.ORAL);
        sessionService.assignEvaluator(session1.getSessionId(), evaluatorId);
        
        // Create second session on same date
        Session session2 = sessionService.createSession(date, venue2, PresentationType.ORAL);
        
        // Attempt to assign same evaluator to second session should fail
        try {
            sessionService.assignEvaluator(session2.getSessionId(), evaluatorId);
            assert false : "Should detect conflict when assigning evaluator to same date";
        } catch (IllegalArgumentException e) {
            // Expected - conflict detected
        }
    }


    @Property(tries = 100)
    void noConflictOnDifferentDates(
            @ForAll("validDate") LocalDate date1,
            @ForAll("validVenue") String venue1,
            @ForAll("validVenue") String venue2,
            @ForAll("presenterId") String presenterId) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        // Create first session and assign presenter
        Session session1 = sessionService.createSession(date1, venue1, PresentationType.ORAL);
        sessionService.assignPresenter(session1.getSessionId(), presenterId);
        
        // Create second session on different date
        LocalDate date2 = date1.plusDays(1);
        Session session2 = sessionService.createSession(date2, venue2, PresentationType.ORAL);
        
        // Assigning same presenter to different date should succeed
        sessionService.assignPresenter(session2.getSessionId(), presenterId);
        
        assert session2.getPresenterIds().contains(presenterId) :
            "Presenter should be assigned to session on different date";
    }

    // ========================================================================
    // Property 12: Session Deletion Cascade
    // For any deleted session, all presenter and evaluator assignments to that
    // session SHALL be removed, and the session SHALL not be retrievable.
    // Validates: Requirements 4.5
    // ========================================================================

    @Property(tries = 100)
    void sessionDeletionRemovesSession(
            @ForAll("validSessionData") SessionData data) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        Session session = sessionService.createSession(data.date, data.venue, data.type);
        String sessionId = session.getSessionId();
        
        // Delete the session
        sessionService.deleteSession(sessionId);
        
        // Session should not be retrievable
        assert sessionService.getSessionById(sessionId) == null :
            "Deleted session should not be retrievable";
    }

    @Property(tries = 100)
    void sessionDeletionRemovesEvaluatorAssignments(
            @ForAll("validSessionData") SessionData data,
            @ForAll("evaluatorId") String evaluatorId) {
        
        DataStore dataStore = new DataStore();
        SessionService sessionService = new SessionService(dataStore);
        
        // Add evaluator to data store
        Evaluator evaluator = new Evaluator(evaluatorId, "eval", "pass");
        dataStore.addUser(evaluator);
        
        // Create session and assign evaluator
        Session session = sessionService.createSession(data.date, data.venue, data.type);
        String sessionId = session.getSessionId();
        sessionService.assignEvaluator(sessionId, evaluatorId);
        
        // Verify evaluator has the session assigned
        assert evaluator.getAssignedSessionIds().contains(sessionId) :
            "Evaluator should have session assigned before deletion";
        
        // Delete the session
        sessionService.deleteSession(sessionId);
        
        // Evaluator should no longer have the session assigned
        assert !evaluator.getAssignedSessionIds().contains(sessionId) :
            "Evaluator should not have deleted session assigned";
    }

    // Providers
    
    @Provide
    Arbitrary<SessionData> validSessionData() {
        return Combinators.combine(
            validDate(),
            validVenue(),
            presentationType()
        ).as(SessionData::new);
    }

    @Provide
    Arbitrary<LocalDate> validDate() {
        return Arbitraries.integers().between(2024, 2030)
            .flatMap(year -> Arbitraries.integers().between(1, 12)
                .flatMap(month -> Arbitraries.integers().between(1, 28)
                    .map(day -> LocalDate.of(year, month, day))));
    }

    @Provide
    Arbitrary<String> validVenue() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20);
    }

    @Provide
    Arbitrary<PresentationType> presentationType() {
        return Arbitraries.of(PresentationType.values());
    }

    @Provide
    Arbitrary<String> presenterId() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(8)
            .map(s -> "P-" + s);
    }

    @Provide
    Arbitrary<String> evaluatorId() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(8)
            .map(s -> "U-" + s);
    }

    // Helper class for session data
    static class SessionData {
        final LocalDate date;
        final String venue;
        final PresentationType type;

        SessionData(LocalDate date, String venue, PresentationType type) {
            this.date = date;
            this.venue = venue;
            this.type = type;
        }
    }
}
