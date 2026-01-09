package com.fci.seminar.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the rubric scores for evaluating a presentation.
 * Each criterion is scored from 1 to 10.
 */
public class RubricScores implements Serializable {
    private static final long serialVersionUID = 1L;

    private int problemClarity;   // 1-10
    private int methodology;      // 1-10
    private int results;          // 1-10
    private int presentation;     // 1-10

    // Default weights for weighted score calculation
    private static final double PROBLEM_CLARITY_WEIGHT = 0.25;
    private static final double METHODOLOGY_WEIGHT = 0.25;
    private static final double RESULTS_WEIGHT = 0.25;
    private static final double PRESENTATION_WEIGHT = 0.25;

    public RubricScores() {
    }

    public RubricScores(int problemClarity, int methodology, int results, int presentation) {
        this.problemClarity = problemClarity;
        this.methodology = methodology;
        this.results = results;
        this.presentation = presentation;
    }

    public int getProblemClarity() {
        return problemClarity;
    }

    public void setProblemClarity(int problemClarity) {
        this.problemClarity = problemClarity;
    }

    public int getMethodology() {
        return methodology;
    }

    public void setMethodology(int methodology) {
        this.methodology = methodology;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    public int getPresentation() {
        return presentation;
    }

    public void setPresentation(int presentation) {
        this.presentation = presentation;
    }

    /**
     * Calculates the total score as the sum of all four rubric scores.
     * @return the sum of problemClarity, methodology, results, and presentation scores
     */
    public int getTotalScore() {
        return problemClarity + methodology + results + presentation;
    }

    /**
     * Calculates the weighted score using predefined weights.
     * @return the weighted average score
     */
    public double getWeightedScore() {
        return (problemClarity * PROBLEM_CLARITY_WEIGHT) +
               (methodology * METHODOLOGY_WEIGHT) +
               (results * RESULTS_WEIGHT) +
               (presentation * PRESENTATION_WEIGHT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RubricScores that = (RubricScores) o;
        return problemClarity == that.problemClarity &&
               methodology == that.methodology &&
               results == that.results &&
               presentation == that.presentation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemClarity, methodology, results, presentation);
    }
}
