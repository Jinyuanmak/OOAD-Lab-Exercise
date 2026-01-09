package com.fci.seminar.property;

import java.time.LocalDate;
import java.util.List;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.model.Session;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.ReportService;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for ReportService.
 * Feature: seminar-management-system
 */
class ReportServicePropertyTest {

    // ========================================================================
    // Property 11: Report Completeness
    // For any set of sessions and evaluations, the generated schedule report
    // SHALL include all sessions, and the evaluation report SHALL include all
    // evaluations.
    // Validates: Requirements 9.2, 9.3
    // ========================================================================

    @Property(tries = 100)
    void scheduleReportIncludesAllSessions(
            @ForAll("sessionList") List<Session> sessions) {
        
        DataStore dataStore = new DataStore();
        ReportService reportService = new ReportService(dataStore);
        
        // Add all sessions
        for (Session session : sessions) {
            dataStore.addSession(session);
        }
        
        // Generate report
        String report = reportService.generateScheduleReport();
        
        // Verify all sessions are included
        for (Session session : sessions) {
            assert report.contains(session.getSessionId()) :
                "Schedule report should include session " + session.getSessionId();
            assert report.contains(session.getVenue()) :
                "Schedule report should include venue " + session.getVenue();
        }
    }

    @Property(tries = 100)
    void evaluationReportIncludesAllEvaluations(
            @ForAll("evaluationList") List<Evaluation> evaluations) {
        
        DataStore dataStore = new DataStore();
        ReportService reportService = new ReportService(dataStore);
        
        // Add all evaluations
        for (Evaluation eval : evaluations) {
            dataStore.addEvaluation(eval);
        }
        
        // Generate report
        String report = reportService.generateEvaluationReport();
        
        // Verify all evaluations are included
        for (Evaluation eval : evaluations) {
            assert report.contains(eval.getEvaluationId()) :
                "Evaluation report should include evaluation " + eval.getEvaluationId();
            assert report.contains(eval.getPresenterId()) :
                "Evaluation report should include presenter " + eval.getPresenterId();
        }
    }


    @Property(tries = 100)
    void summaryReportShowsCorrectCounts(
            @ForAll("sessionList") List<Session> sessions,
            @ForAll("evaluationList") List<Evaluation> evaluations) {
        
        DataStore dataStore = new DataStore();
        ReportService reportService = new ReportService(dataStore);
        
        // Add sessions and evaluations
        for (Session session : sessions) {
            dataStore.addSession(session);
        }
        for (Evaluation eval : evaluations) {
            dataStore.addEvaluation(eval);
        }
        
        // Verify counts match
        assert reportService.getSessionCount() == sessions.size() :
            "Session count should match number of sessions added";
        assert reportService.getEvaluationCount() == evaluations.size() :
            "Evaluation count should match number of evaluations added";
    }

    // Providers

    @Provide
    Arbitrary<List<Session>> sessionList() {
        return Arbitraries.integers().between(0, 5).flatMap(size -> {
            return Arbitraries.just(size).map(s -> {
                java.util.List<Session> result = new java.util.ArrayList<>();
                for (int i = 0; i < s; i++) {
                    String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
                    result.add(new Session("S-" + uniqueId, 
                        LocalDate.of(2025, 1, 1).plusDays(i), 
                        "Venue" + uniqueId, 
                        PresentationType.ORAL));
                }
                return result;
            });
        });
    }

    @Provide
    Arbitrary<Session> session() {
        return Arbitraries.just(1).map(ignored -> {
            String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
            return new Session("S-" + uniqueId, LocalDate.of(2025, 1, 1), "Venue" + uniqueId, PresentationType.ORAL);
        });
    }

    @Provide
    Arbitrary<List<Evaluation>> evaluationList() {
        return Arbitraries.integers().between(0, 5).flatMap(size -> {
            return Arbitraries.just(size).map(s -> {
                java.util.List<Evaluation> result = new java.util.ArrayList<>();
                for (int i = 0; i < s; i++) {
                    String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
                    Evaluation e = new Evaluation();
                    e.setEvaluationId("E-" + uniqueId);
                    e.setPresenterId("P-" + uniqueId);
                    e.setEvaluatorId("U-" + uniqueId);
                    e.setSessionId("S-" + uniqueId);
                    e.setScores(new RubricScores(5, 5, 5, 5));
                    e.setComments("Comment " + uniqueId);
                    result.add(e);
                }
                return result;
            });
        });
    }

    @Provide
    Arbitrary<Evaluation> evaluation() {
        return Arbitraries.just(1).map(ignored -> {
            String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
            Evaluation e = new Evaluation();
            e.setEvaluationId("E-" + uniqueId);
            e.setPresenterId("P-" + uniqueId);
            e.setEvaluatorId("U-" + uniqueId);
            e.setSessionId("S-" + uniqueId);
            e.setScores(new RubricScores(5, 5, 5, 5));
            e.setComments("Comment " + uniqueId);
            return e;
        });
    }

    @Provide
    Arbitrary<RubricScores> rubricScores() {
        return Combinators.combine(
            Arbitraries.integers().between(1, 10),
            Arbitraries.integers().between(1, 10),
            Arbitraries.integers().between(1, 10),
            Arbitraries.integers().between(1, 10)
        ).as(RubricScores::new);
    }
}
