package com.fci.seminar.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.AwardType;
import com.fci.seminar.model.CeremonyAgenda;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Student;

/**
 * Service class for award computation and ceremony agenda generation.
 * Handles computing winners for different award categories.
 */
public class AwardService {
    
    private final DataStore dataStore;
    private final EvaluationService evaluationService;
    private final UserService userService;

    public AwardService(DataStore dataStore, EvaluationService evaluationService, UserService userService) {
        this.dataStore = dataStore;
        this.evaluationService = evaluationService;
        this.userService = userService;
    }

    /**
     * Computes the Best Oral Presentation award winner.
     * @return the Award for best oral presenter, or null if no oral presenters exist
     */
    public Award computeBestOral() {
        return computeBestByType(PresentationType.ORAL, AwardType.BEST_ORAL);
    }

    /**
     * Computes the Best Poster Presentation award winner.
     * @return the Award for best poster presenter, or null if no poster presenters exist
     */
    public Award computeBestPoster() {
        return computeBestByType(PresentationType.POSTER, AwardType.BEST_POSTER);
    }

    /**
     * Computes the best presenter for a given presentation type.
     */
    private Award computeBestByType(PresentationType type, AwardType awardType) {
        List<Student> students = userService.getAllStudents();
        
        String bestPresenterId = null;
        double bestScore = -1;
        
        for (Student student : students) {
            if (student.getPresentationType() == type && student.getPresenterId() != null) {
                double avgScore = evaluationService.calculateAverageScore(student.getPresenterId());
                if (avgScore > bestScore) {
                    bestScore = avgScore;
                    bestPresenterId = student.getPresenterId();
                }
            }
        }
        
        if (bestPresenterId == null || bestScore <= 0) {
            return null;
        }
        
        return new Award(awardType, bestPresenterId, bestScore);
    }


    /**
     * Computes the People's Choice award based on vote counts.
     * @param votes a map of presenter IDs to vote counts
     * @return the Award for people's choice, or null if no votes exist
     */
    public Award computePeoplesChoice(Map<String, Integer> votes) {
        if (votes == null || votes.isEmpty()) {
            return null;
        }
        
        String winnerId = null;
        int maxVotes = -1;
        
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winnerId = entry.getKey();
            }
        }
        
        if (winnerId == null || maxVotes <= 0) {
            return null;
        }
        
        return new Award(AwardType.PEOPLES_CHOICE, winnerId, maxVotes);
    }

    /**
     * Generates the ceremony agenda with all computed awards.
     * @return the CeremonyAgenda with awards
     */
    public CeremonyAgenda generateAgenda() {
        CeremonyAgenda agenda = new CeremonyAgenda();
        agenda.setCeremonyDate(LocalDateTime.now());
        
        // Compute and add awards
        Award bestOral = computeBestOral();
        if (bestOral != null) {
            agenda.addAward(bestOral);
            dataStore.addAward(bestOral);
        }
        
        Award bestPoster = computeBestPoster();
        if (bestPoster != null) {
            agenda.addAward(bestPoster);
            dataStore.addAward(bestPoster);
        }
        
        return agenda;
    }

    /**
     * Generates the ceremony agenda with all awards including People's Choice.
     * @param votes the vote counts for People's Choice
     * @return the CeremonyAgenda with all awards
     */
    public CeremonyAgenda generateAgenda(Map<String, Integer> votes) {
        CeremonyAgenda agenda = generateAgenda();
        
        Award peoplesChoice = computePeoplesChoice(votes);
        if (peoplesChoice != null) {
            agenda.addAward(peoplesChoice);
            dataStore.addAward(peoplesChoice);
        }
        
        return agenda;
    }

    /**
     * Gets all computed awards.
     * @return list of all awards
     */
    public List<Award> getAllAwards() {
        return dataStore.getAwards();
    }

    /**
     * Clears all awards (for recomputation).
     */
    public void clearAwards() {
        dataStore.clearAwards();
    }
}
