package com.fci.seminar.property;

import com.fci.seminar.model.RubricScores;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

/**
 * Property-based tests for RubricScores calculation.
 * Feature: seminar-management-system, Property 7: Total Score Calculation
 * Validates: Requirements 6.6
 */
class RubricScoresPropertyTest {

    /**
     * Property 7: Total Score Calculation
     * For any set of rubric scores (problemClarity, methodology, results, presentation),
     * the total score SHALL equal the sum of all four scores.
     * Validates: Requirements 6.6
     */
    @Property(tries = 100)
    void totalScoreEqualsSum(
            @ForAll @IntRange(min = 1, max = 10) int problemClarity,
            @ForAll @IntRange(min = 1, max = 10) int methodology,
            @ForAll @IntRange(min = 1, max = 10) int results,
            @ForAll @IntRange(min = 1, max = 10) int presentation) {
        
        RubricScores scores = new RubricScores(problemClarity, methodology, results, presentation);
        
        int expectedTotal = problemClarity + methodology + results + presentation;
        
        assert scores.getTotalScore() == expectedTotal : 
            "Total score should equal sum of all four scores";
    }

    /**
     * Property 7: Weighted Score Calculation
     * For any set of rubric scores, the weighted score SHALL be calculated consistently
     * using equal weights (0.25 each).
     * Validates: Requirements 6.6
     */
    @Property(tries = 100)
    void weightedScoreCalculatedConsistently(
            @ForAll @IntRange(min = 1, max = 10) int problemClarity,
            @ForAll @IntRange(min = 1, max = 10) int methodology,
            @ForAll @IntRange(min = 1, max = 10) int results,
            @ForAll @IntRange(min = 1, max = 10) int presentation) {
        
        RubricScores scores = new RubricScores(problemClarity, methodology, results, presentation);
        
        double expectedWeighted = (problemClarity * 0.25) + (methodology * 0.25) + 
                                  (results * 0.25) + (presentation * 0.25);
        
        assert Math.abs(scores.getWeightedScore() - expectedWeighted) < 0.0001 : 
            "Weighted score should be calculated using equal weights";
    }

    /**
     * Property: Weighted score equals total score divided by 4 (with equal weights)
     * Validates: Requirements 6.6
     */
    @Property(tries = 100)
    void weightedScoreEqualsAverageWithEqualWeights(
            @ForAll @IntRange(min = 1, max = 10) int problemClarity,
            @ForAll @IntRange(min = 1, max = 10) int methodology,
            @ForAll @IntRange(min = 1, max = 10) int results,
            @ForAll @IntRange(min = 1, max = 10) int presentation) {
        
        RubricScores scores = new RubricScores(problemClarity, methodology, results, presentation);
        
        double expectedAverage = scores.getTotalScore() / 4.0;
        
        assert Math.abs(scores.getWeightedScore() - expectedAverage) < 0.0001 : 
            "With equal weights, weighted score should equal total/4";
    }
}
