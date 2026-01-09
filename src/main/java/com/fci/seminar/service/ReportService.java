package com.fci.seminar.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;

/**
 * Service class for generating reports and analytics.
 * Handles schedule reports, evaluation reports, and summary statistics.
 */
public class ReportService {
    
    private final DataStore dataStore;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ReportService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Generates a schedule report with all sessions and their assignments.
     * @return formatted schedule report string
     */
    public String generateScheduleReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("         SEMINAR SCHEDULE REPORT\n");
        sb.append("===========================================\n\n");

        List<Session> sessions = dataStore.getSessions().values().stream().toList();
        
        if (sessions.isEmpty()) {
            sb.append("No sessions scheduled.\n");
        } else {
            for (Session session : sessions) {
                sb.append("Session: ").append(session.getSessionId()).append("\n");
                sb.append("  Date: ").append(session.getDate().format(DATE_FORMAT)).append("\n");
                sb.append("  Venue: ").append(session.getVenue()).append("\n");
                sb.append("  Type: ").append(session.getSessionType()).append("\n");
                
                sb.append("  Presenters: ");
                if (session.getPresenterIds().isEmpty()) {
                    sb.append("None assigned\n");
                } else {
                    sb.append("\n");
                    for (String presenterId : session.getPresenterIds()) {
                        sb.append("    - ").append(presenterId).append("\n");
                    }
                }
                
                sb.append("  Evaluators: ");
                if (session.getEvaluatorIds().isEmpty()) {
                    sb.append("None assigned\n");
                } else {
                    sb.append("\n");
                    for (String evaluatorId : session.getEvaluatorIds()) {
                        sb.append("    - ").append(evaluatorId).append("\n");
                    }
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }


    /**
     * Generates an evaluation report with all scores and comments per presenter.
     * @return formatted evaluation report string
     */
    public String generateEvaluationReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("         EVALUATION REPORT\n");
        sb.append("===========================================\n\n");

        List<Evaluation> evaluations = dataStore.getEvaluations().values().stream().toList();
        
        if (evaluations.isEmpty()) {
            sb.append("No evaluations submitted.\n");
        } else {
            for (Evaluation eval : evaluations) {
                sb.append("Evaluation: ").append(eval.getEvaluationId()).append("\n");
                sb.append("  Presenter: ").append(eval.getPresenterId()).append("\n");
                sb.append("  Evaluator: ").append(eval.getEvaluatorId()).append("\n");
                sb.append("  Session: ").append(eval.getSessionId()).append("\n");
                
                if (eval.getScores() != null) {
                    sb.append("  Scores:\n");
                    sb.append("    Problem Clarity: ").append(eval.getScores().getProblemClarity()).append("\n");
                    sb.append("    Methodology: ").append(eval.getScores().getMethodology()).append("\n");
                    sb.append("    Results: ").append(eval.getScores().getResults()).append("\n");
                    sb.append("    Presentation: ").append(eval.getScores().getPresentation()).append("\n");
                    sb.append("    Total Score: ").append(eval.getScores().getTotalScore()).append("\n");
                }
                
                if (eval.getComments() != null && !eval.getComments().isEmpty()) {
                    sb.append("  Comments: ").append(eval.getComments()).append("\n");
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }

    /**
     * Generates a summary report with analytics including totals and averages.
     * @return formatted summary report string
     */
    public String generateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("         SEMINAR SUMMARY REPORT\n");
        sb.append("===========================================\n\n");

        // Count presenters
        long presenterCount = dataStore.getUsers().values().stream()
            .filter(u -> u instanceof Student)
            .count();
        
        // Count sessions
        int sessionCount = dataStore.getSessions().size();
        
        // Count evaluations and calculate average score
        List<Evaluation> evaluations = dataStore.getEvaluations().values().stream().toList();
        int evaluationCount = evaluations.size();
        
        double avgScore = 0.0;
        if (!evaluations.isEmpty()) {
            double totalScore = evaluations.stream()
                .filter(e -> e.getScores() != null)
                .mapToDouble(e -> e.getScores().getTotalScore())
                .sum();
            avgScore = totalScore / evaluations.size();
        }

        sb.append("STATISTICS:\n");
        sb.append("-------------------------------------------\n");
        sb.append("Total Presenters: ").append(presenterCount).append("\n");
        sb.append("Total Sessions: ").append(sessionCount).append("\n");
        sb.append("Total Evaluations: ").append(evaluationCount).append("\n");
        sb.append("Average Score: ").append(String.format("%.2f", avgScore)).append("\n");
        sb.append("\n");

        // Count by presentation type
        long oralCount = dataStore.getUsers().values().stream()
            .filter(u -> u instanceof Student)
            .map(u -> (Student) u)
            .filter(s -> s.getPresentationType() == com.fci.seminar.model.PresentationType.ORAL)
            .count();
        
        long posterCount = dataStore.getUsers().values().stream()
            .filter(u -> u instanceof Student)
            .map(u -> (Student) u)
            .filter(s -> s.getPresentationType() == com.fci.seminar.model.PresentationType.POSTER)
            .count();

        sb.append("PRESENTATION BREAKDOWN:\n");
        sb.append("-------------------------------------------\n");
        sb.append("Oral Presentations: ").append(oralCount).append("\n");
        sb.append("Poster Presentations: ").append(posterCount).append("\n");
        
        return sb.toString();
    }

    /**
     * Exports report content to a text file.
     * @param content the report content to export
     * @param filename the target filename
     * @throws IOException if file writing fails
     */
    public void exportToFile(String content, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
        }
    }

    /**
     * Gets the total number of sessions.
     * @return session count
     */
    public int getSessionCount() {
        return dataStore.getSessions().size();
    }

    /**
     * Gets the total number of evaluations.
     * @return evaluation count
     */
    public int getEvaluationCount() {
        return dataStore.getEvaluations().size();
    }
}
