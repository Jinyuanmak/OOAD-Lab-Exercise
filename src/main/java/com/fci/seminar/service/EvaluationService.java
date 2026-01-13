package com.fci.seminar.service;

import java.util.ArrayList;
import java.util.List;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.util.IdGenerator;

/**
 * Service class for evaluation operations.
 * Handles evaluation submission, retrieval, and score calculations.
 */
public class EvaluationService {
    
    private final DataStore dataStore;

    public EvaluationService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Submits an evaluation with score validation.
     * Checks if an evaluation already exists for this evaluator-presenter pair.
     * If exists, updates it; otherwise creates a new one.
     * @param evaluation the evaluation to submit
     * @throws IllegalArgumentException if validation fails
     */
    public void submitEvaluation(Evaluation evaluation) {
        validateEvaluation(evaluation);
        
        // Check if evaluation already exists for this evaluator-presenter pair
        Evaluation existingEvaluation = getEvaluationByEvaluatorAndPresenter(
            evaluation.getEvaluatorId(), 
            evaluation.getPresenterId()
        );
        
        if (existingEvaluation != null) {
            // Update existing evaluation
            evaluation.setEvaluationId(existingEvaluation.getEvaluationId());
            dataStore.updateEvaluation(evaluation);
        } else {
            // Create new evaluation
            if (evaluation.getEvaluationId() == null || evaluation.getEvaluationId().isEmpty()) {
                evaluation.setEvaluationId(IdGenerator.generateEvaluationId());
            }
            dataStore.addEvaluation(evaluation);
        }
    }
    
    /**
     * Gets an evaluation by evaluator and presenter.
     * @param evaluatorId the evaluator ID
     * @param presenterId the presenter ID
     * @return the evaluation if found, null otherwise
     */
    public Evaluation getEvaluationByEvaluatorAndPresenter(String evaluatorId, String presenterId) {
        for (Evaluation evaluation : dataStore.getEvaluations().values()) {
            if (evaluatorId.equals(evaluation.getEvaluatorId()) && 
                presenterId.equals(evaluation.getPresenterId())) {
                return evaluation;
            }
        }
        return null;
    }

    /**
     * Validates an evaluation.
     */
    private void validateEvaluation(Evaluation evaluation) {
        if (evaluation == null) {
            throw new IllegalArgumentException("Evaluation cannot be null");
        }
        if (evaluation.getPresenterId() == null || evaluation.getPresenterId().isEmpty()) {
            throw new IllegalArgumentException("Presenter ID is required");
        }
        if (evaluation.getEvaluatorId() == null || evaluation.getEvaluatorId().isEmpty()) {
            throw new IllegalArgumentException("Evaluator ID is required");
        }
        if (evaluation.getScores() == null) {
            throw new IllegalArgumentException("Scores are required");
        }
        
        validateScores(evaluation.getScores());
    }

    /**
     * Validates that all rubric scores are within the acceptable range (1-10).
     */
    private void validateScores(RubricScores scores) {
        if (!isValidScore(scores.getProblemClarity())) {
            throw new IllegalArgumentException("Problem Clarity score must be between 1 and 10");
        }
        if (!isValidScore(scores.getMethodology())) {
            throw new IllegalArgumentException("Methodology score must be between 1 and 10");
        }
        if (!isValidScore(scores.getResults())) {
            throw new IllegalArgumentException("Results score must be between 1 and 10");
        }
        if (!isValidScore(scores.getPresentation())) {
            throw new IllegalArgumentException("Presentation score must be between 1 and 10");
        }
    }

    /**
     * Checks if a score is within the valid range (1-10).
     */
    public boolean isValidScore(int score) {
        return score >= 1 && score <= 10;
    }


    /**
     * Retrieves all evaluations for a specific presenter.
     * @param presenterId the presenter ID
     * @return list of evaluations for the presenter
     */
    public List<Evaluation> getEvaluationsForPresenter(String presenterId) {
        List<Evaluation> result = new ArrayList<>();
        for (Evaluation evaluation : dataStore.getEvaluations().values()) {
            if (presenterId.equals(evaluation.getPresenterId())) {
                result.add(evaluation);
            }
        }
        return result;
    }

    /**
     * Retrieves all evaluations submitted by a specific evaluator.
     * @param evaluatorId the evaluator ID
     * @return list of evaluations by the evaluator
     */
    public List<Evaluation> getEvaluationsByEvaluator(String evaluatorId) {
        List<Evaluation> result = new ArrayList<>();
        for (Evaluation evaluation : dataStore.getEvaluations().values()) {
            if (evaluatorId.equals(evaluation.getEvaluatorId())) {
                result.add(evaluation);
            }
        }
        return result;
    }

    /**
     * Calculates the average score for a presenter based on all their evaluations.
     * @param presenterId the presenter ID
     * @return the average score, or 0.0 if no evaluations exist
     */
    public double calculateAverageScore(String presenterId) {
        List<Evaluation> evaluations = getEvaluationsForPresenter(presenterId);
        if (evaluations.isEmpty()) {
            return 0.0;
        }
        
        double totalScore = 0.0;
        for (Evaluation evaluation : evaluations) {
            totalScore += evaluation.getScores().getTotalScore();
        }
        
        return totalScore / evaluations.size();
    }

    /**
     * Retrieves all evaluations.
     * @return list of all evaluations
     */
    public List<Evaluation> getAllEvaluations() {
        return new ArrayList<>(dataStore.getEvaluations().values());
    }

    /**
     * Retrieves an evaluation by ID.
     * @param evaluationId the evaluation ID
     * @return the evaluation if found, null otherwise
     */
    public Evaluation getEvaluationById(String evaluationId) {
        return dataStore.getEvaluation(evaluationId);
    }
}
