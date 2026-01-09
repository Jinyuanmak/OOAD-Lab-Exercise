package com.fci.seminar.property;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.EvaluationService;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for EvaluationService.
 * Feature: seminar-management-system
 */
class EvaluationServicePropertyTest {

    // ========================================================================
    // Property 6: Score Range Validation
    // For any rubric score input, if the value is outside the range [1, 10],
    // validation SHALL reject it; if within range, validation SHALL accept it.
    // Validates: Requirements 6.4
    // ========================================================================

    @Property(tries = 100)
    void validScoresAreAccepted(
            @ForAll("validScore") int problemClarity,
            @ForAll("validScore") int methodology,
            @ForAll("validScore") int results,
            @ForAll("validScore") int presentation) {
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        
        RubricScores scores = new RubricScores(problemClarity, methodology, results, presentation);
        Evaluation evaluation = createValidEvaluation(scores);
        
        // Valid scores should be accepted
        evaluationService.submitEvaluation(evaluation);
        
        assert dataStore.getEvaluations().containsKey(evaluation.getEvaluationId()) :
            "Evaluation with valid scores should be saved";
    }

    @Property(tries = 100)
    void scoresAbove10AreRejected(
            @ForAll("invalidHighScore") int invalidScore) {
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        
        // Test with invalid problem clarity score
        RubricScores scores = new RubricScores(invalidScore, 5, 5, 5);
        Evaluation evaluation = createValidEvaluation(scores);
        
        try {
            evaluationService.submitEvaluation(evaluation);
            assert false : "Scores above 10 should be rejected";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Property(tries = 100)
    void scoresBelow1AreRejected(
            @ForAll("invalidLowScore") int invalidScore) {
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        
        // Test with invalid methodology score
        RubricScores scores = new RubricScores(5, invalidScore, 5, 5);
        Evaluation evaluation = createValidEvaluation(scores);
        
        try {
            evaluationService.submitEvaluation(evaluation);
            assert false : "Scores below 1 should be rejected";
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }


    @Property(tries = 100)
    void isValidScoreReturnsTrueForValidRange(
            @ForAll("validScore") int score) {
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        
        assert evaluationService.isValidScore(score) :
            "Score " + score + " should be valid (within 1-10)";
    }

    @Property(tries = 100)
    void isValidScoreReturnsFalseForInvalidRange(
            @ForAll("invalidScore") int score) {
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        
        assert !evaluationService.isValidScore(score) :
            "Score " + score + " should be invalid (outside 1-10)";
    }

    // Helper method to create a valid evaluation
    private Evaluation createValidEvaluation(RubricScores scores) {
        Evaluation evaluation = new Evaluation();
        evaluation.setPresenterId("P-test");
        evaluation.setEvaluatorId("U-test");
        evaluation.setSessionId("S-test");
        evaluation.setScores(scores);
        evaluation.setComments("Test comments");
        return evaluation;
    }

    // Providers

    @Provide
    Arbitrary<Integer> validScore() {
        return Arbitraries.integers().between(1, 10);
    }

    @Provide
    Arbitrary<Integer> invalidHighScore() {
        return Arbitraries.integers().between(11, 100);
    }

    @Provide
    Arbitrary<Integer> invalidLowScore() {
        return Arbitraries.integers().between(-100, 0);
    }

    @Provide
    Arbitrary<Integer> invalidScore() {
        return Arbitraries.oneOf(
            Arbitraries.integers().between(-100, 0),
            Arbitraries.integers().between(11, 100)
        );
    }
}
