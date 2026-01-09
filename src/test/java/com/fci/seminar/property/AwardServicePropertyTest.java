package com.fci.seminar.property;

import java.util.List;
import java.util.Map;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.AwardType;
import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.model.Student;
import com.fci.seminar.service.AwardService;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.UserService;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for AwardService.
 * Feature: seminar-management-system
 */
class AwardServicePropertyTest {

    // ========================================================================
    // Property 9: Award Winner Computation
    // For any set of presenters with evaluations in a category (Oral/Poster),
    // the computed winner SHALL be the presenter with the highest average
    // evaluation score.
    // Validates: Requirements 8.2, 8.3
    // ========================================================================

    @Property(tries = 100)
    void bestOralWinnerHasHighestAverageScore(
            @ForAll("oralStudentsWithScores") List<StudentWithScore> studentsWithScores) {
        
        if (studentsWithScores.isEmpty()) {
            return; // Skip empty cases
        }
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        UserService userService = new UserService(dataStore);
        AwardService awardService = new AwardService(dataStore, evaluationService, userService);
        
        // Add students and their evaluations
        double expectedHighestScore = -1;
        
        for (StudentWithScore sws : studentsWithScores) {
            dataStore.addUser(sws.student);
            
            // Create evaluation with the specified score
            Evaluation eval = new Evaluation();
            eval.setEvaluationId("E-" + sws.student.getPresenterId());
            eval.setPresenterId(sws.student.getPresenterId());
            eval.setEvaluatorId("U-eval");
            eval.setSessionId("S-test");
            eval.setScores(new RubricScores(sws.score, sws.score, sws.score, sws.score));
            dataStore.addEvaluation(eval);
            
            double avgScore = sws.score * 4; // Total score (4 criteria)
            if (avgScore > expectedHighestScore) {
                expectedHighestScore = avgScore;
            }
        }
        
        // Compute best oral
        Award bestOral = awardService.computeBestOral();
        
        assert bestOral != null : "Best oral award should be computed";
        assert bestOral.getType() == AwardType.BEST_ORAL : "Award type should be BEST_ORAL";
        // The winner should have the highest score (allowing for ties)
        assert bestOral.getScore() == expectedHighestScore :
            "Winner should have the highest score";
    }


    @Property(tries = 100)
    void bestPosterWinnerHasHighestAverageScore(
            @ForAll("posterStudentsWithScores") List<StudentWithScore> studentsWithScores) {
        
        if (studentsWithScores.isEmpty()) {
            return; // Skip empty cases
        }
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        UserService userService = new UserService(dataStore);
        AwardService awardService = new AwardService(dataStore, evaluationService, userService);
        
        // Add students and their evaluations
        double expectedHighestScore = -1;
        
        for (StudentWithScore sws : studentsWithScores) {
            dataStore.addUser(sws.student);
            
            // Create evaluation with the specified score
            Evaluation eval = new Evaluation();
            eval.setEvaluationId("E-" + sws.student.getPresenterId());
            eval.setPresenterId(sws.student.getPresenterId());
            eval.setEvaluatorId("U-eval");
            eval.setSessionId("S-test");
            eval.setScores(new RubricScores(sws.score, sws.score, sws.score, sws.score));
            dataStore.addEvaluation(eval);
            
            double avgScore = sws.score * 4; // Total score (4 criteria)
            if (avgScore > expectedHighestScore) {
                expectedHighestScore = avgScore;
            }
        }
        
        // Compute best poster
        Award bestPoster = awardService.computeBestPoster();
        
        assert bestPoster != null : "Best poster award should be computed";
        assert bestPoster.getType() == AwardType.BEST_POSTER : "Award type should be BEST_POSTER";
        // The winner should have the highest score (allowing for ties)
        assert bestPoster.getScore() == expectedHighestScore :
            "Winner should have the highest score";
    }

    @Property(tries = 100)
    void peoplesChoiceWinnerHasMostVotes(
            @ForAll("votesMap") Map<String, Integer> votes) {
        
        if (votes.isEmpty()) {
            return; // Skip empty cases
        }
        
        DataStore dataStore = new DataStore();
        EvaluationService evaluationService = new EvaluationService(dataStore);
        UserService userService = new UserService(dataStore);
        AwardService awardService = new AwardService(dataStore, evaluationService, userService);
        
        // Find expected winner (highest votes)
        String expectedWinnerId = null;
        int maxVotes = -1;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                expectedWinnerId = entry.getKey();
            }
        }
        
        // Compute people's choice
        Award peoplesChoice = awardService.computePeoplesChoice(votes);
        
        assert peoplesChoice != null : "People's choice award should be computed";
        assert peoplesChoice.getType() == AwardType.PEOPLES_CHOICE : 
            "Award type should be PEOPLES_CHOICE";
        assert peoplesChoice.getWinnerId().equals(expectedWinnerId) :
            "Winner should be presenter with most votes";
        assert peoplesChoice.getScore() == maxVotes :
            "Score should equal vote count";
    }

    // Providers

    @Provide
    Arbitrary<List<StudentWithScore>> oralStudentsWithScores() {
        return Arbitraries.integers().between(1, 5).flatMap(size -> {
            return Arbitraries.integers().between(1, 10).list().ofSize(size).map(scores -> {
                java.util.List<StudentWithScore> result = new java.util.ArrayList<>();
                for (int i = 0; i < scores.size(); i++) {
                    String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
                    Student student = new Student("U-" + uniqueId, "user" + uniqueId, "pass");
                    student.setPresenterId("P-" + uniqueId);
                    student.setPresentationType(PresentationType.ORAL);
                    student.setResearchTitle("Research " + uniqueId);
                    student.setAbstractText("Abstract " + uniqueId);
                    student.setSupervisorName("Supervisor " + uniqueId);
                    result.add(new StudentWithScore(student, scores.get(i)));
                }
                return result;
            });
        });
    }

    @Provide
    Arbitrary<List<StudentWithScore>> posterStudentsWithScores() {
        return Arbitraries.integers().between(1, 5).flatMap(size -> {
            return Arbitraries.integers().between(1, 10).list().ofSize(size).map(scores -> {
                java.util.List<StudentWithScore> result = new java.util.ArrayList<>();
                for (int i = 0; i < scores.size(); i++) {
                    String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
                    Student student = new Student("U-" + uniqueId, "user" + uniqueId, "pass");
                    student.setPresenterId("P-" + uniqueId);
                    student.setPresentationType(PresentationType.POSTER);
                    student.setResearchTitle("Research " + uniqueId);
                    student.setAbstractText("Abstract " + uniqueId);
                    student.setSupervisorName("Supervisor " + uniqueId);
                    result.add(new StudentWithScore(student, scores.get(i)));
                }
                return result;
            });
        });
    }

    @Provide
    Arbitrary<Map<String, Integer>> votesMap() {
        return Arbitraries.maps(
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(8).map(s -> "P-" + s),
            Arbitraries.integers().between(1, 100)
        ).ofMinSize(1).ofMaxSize(5);
    }

    // Helper class
    static class StudentWithScore {
        final Student student;
        final int score;

        StudentWithScore(Student student, int score) {
            this.student = student;
            this.score = score;
        }
    }
}
