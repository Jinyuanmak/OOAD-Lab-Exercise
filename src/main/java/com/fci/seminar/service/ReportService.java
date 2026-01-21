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
                        // Get student username by presenter ID
                        String presenterName = getPresenterName(presenterId);
                        sb.append("    - ").append(presenterName).append("\n");
                    }
                }
                
                sb.append("  Evaluators: ");
                if (session.getEvaluatorIds().isEmpty()) {
                    sb.append("None assigned\n");
                } else {
                    sb.append("\n");
                    for (String evaluatorId : session.getEvaluatorIds()) {
                        // Get evaluator username by ID
                        String evaluatorName = getEvaluatorName(evaluatorId);
                        sb.append("    - ").append(evaluatorName).append("\n");
                    }
                }
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Gets presenter name by presenter ID.
     */
    private String getPresenterName(String presenterId) {
        for (com.fci.seminar.model.User user : dataStore.getUsers().values()) {
            if (user instanceof Student student) {
                if (presenterId.equals(student.getPresenterId())) {
                    return student.getUsername();
                }
            }
        }
        return presenterId; // Fallback to ID if not found
    }
    
    /**
     * Gets evaluator name by user ID.
     */
    private String getEvaluatorName(String evaluatorId) {
        com.fci.seminar.model.User user = dataStore.getUsers().get(evaluatorId);
        if (user != null) {
            return user.getUsername();
        }
        return evaluatorId; // Fallback to ID if not found
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
                sb.append("  Presenter: ").append(getPresenterName(eval.getPresenterId())).append("\n");
                sb.append("  Evaluator: ").append(getEvaluatorName(eval.getEvaluatorId())).append("\n");
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
     * Exports report content to a PDF file.
     * Creates a simple PDF with the report content.
     * @param content the report content to export
     * @param filename the target filename
     * @param reportType the type of report (for title)
     * @throws IOException if file writing fails
     */
    public void exportToPDF(String content, String filename, String reportType) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // Simple PDF generation - write as text with PDF header
            // For a real PDF, you would use a library like iText or Apache PDFBox
            // This is a simplified version that creates a text-based PDF
            writer.write("%PDF-1.4\n");
            writer.write("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
            writer.write("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
            writer.write("3 0 obj\n<< /Type /Page /Parent 2 0 R /Resources 4 0 R /MediaBox [0 0 612 792] /Contents 5 0 R >>\nendobj\n");
            writer.write("4 0 obj\n<< /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Courier >> >> >>\nendobj\n");
            
            // Escape special characters and format content for PDF
            String pdfContent = content.replace("\\", "\\\\")
                                      .replace("(", "\\(")
                                      .replace(")", "\\)")
                                      .replace("\n", "\\n");
            
            writer.write("5 0 obj\n<< /Length " + (pdfContent.length() + 50) + " >>\nstream\n");
            writer.write("BT\n/F1 10 Tf\n50 750 Td\n");
            writer.write("(" + pdfContent + ") Tj\n");
            writer.write("ET\nendstream\nendobj\n");
            writer.write("xref\n0 6\n");
            writer.write("0000000000 65535 f\n0000000009 00000 n\n0000000058 00000 n\n");
            writer.write("0000000115 00000 n\n0000000214 00000 n\n0000000308 00000 n\n");
            writer.write("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n400\n%%EOF\n");
        }
    }
    
    /**
     * Exports report content to a CSV file.
     * Converts the report into a structured CSV table format.
     * @param content the report content to export
     * @param filename the target filename
     * @param reportType the type of report
     * @throws IOException if file writing fails
     */
    public void exportToCSV(String content, String filename, String reportType) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            switch (reportType.toLowerCase()) {
                case "schedule":
                    exportScheduleToCSV(writer);
                    break;
                case "evaluation":
                    exportEvaluationToCSV(writer);
                    break;
                case "summary":
                    exportSummaryToCSV(writer);
                    break;
                default:
                    // Fallback: simple CSV format
                    writer.write("Content\n");
                    String[] lines = content.split("\n");
                    for (String line : lines) {
                        writer.write("\"" + line.replace("\"", "\"\"") + "\"\n");
                    }
            }
        }
    }
    
    /**
     * Exports schedule report as CSV table.
     */
    private void exportScheduleToCSV(FileWriter writer) throws IOException {
        writer.write("Session ID,Date,Venue,Type,Presenters,Evaluators\n");
        
        List<Session> sessions = dataStore.getSessions().values().stream().toList();
        
        for (Session session : sessions) {
            writer.write("\"" + session.getSessionId() + "\",");
            writer.write("\"" + session.getDate().format(DATE_FORMAT) + "\",");
            writer.write("\"" + session.getVenue() + "\",");
            writer.write("\"" + session.getSessionType() + "\",");
            
            // Presenters
            StringBuilder presenters = new StringBuilder();
            for (String presenterId : session.getPresenterIds()) {
                if (presenters.length() > 0) presenters.append("; ");
                presenters.append(getPresenterName(presenterId));
            }
            writer.write("\"" + presenters.toString() + "\",");
            
            // Evaluators
            StringBuilder evaluators = new StringBuilder();
            for (String evaluatorId : session.getEvaluatorIds()) {
                if (evaluators.length() > 0) evaluators.append("; ");
                evaluators.append(getEvaluatorName(evaluatorId));
            }
            writer.write("\"" + evaluators.toString() + "\"\n");
        }
    }
    
    /**
     * Exports evaluation report as CSV table.
     */
    private void exportEvaluationToCSV(FileWriter writer) throws IOException {
        writer.write("Evaluation ID,Presenter,Evaluator,Session,Problem Clarity,Methodology,Results,Presentation,Total Score,Comments\n");
        
        List<Evaluation> evaluations = dataStore.getEvaluations().values().stream().toList();
        
        for (Evaluation eval : evaluations) {
            writer.write("\"" + eval.getEvaluationId() + "\",");
            writer.write("\"" + getPresenterName(eval.getPresenterId()) + "\",");
            writer.write("\"" + getEvaluatorName(eval.getEvaluatorId()) + "\",");
            writer.write("\"" + eval.getSessionId() + "\",");
            writer.write(eval.getScores().getProblemClarity() + ",");
            writer.write(eval.getScores().getMethodology() + ",");
            writer.write(eval.getScores().getResults() + ",");
            writer.write(eval.getScores().getPresentation() + ",");
            writer.write(eval.getScores().getTotalScore() + ",");
            
            String comments = eval.getComments() != null ? eval.getComments().replace("\"", "\"\"") : "";
            writer.write("\"" + comments + "\"\n");
        }
    }
    
    /**
     * Exports summary report as CSV table.
     */
    private void exportSummaryToCSV(FileWriter writer) throws IOException {
        // Summary statistics table
        writer.write("Metric,Value\n");
        writer.write("\"Total Sessions\"," + dataStore.getSessions().size() + "\n");
        writer.write("\"Total Presenters\"," + countPresenters() + "\n");
        writer.write("\"Total Evaluators\"," + countEvaluators() + "\n");
        writer.write("\"Total Evaluations\"," + dataStore.getEvaluations().size() + "\n");
        
        // Average scores
        List<Evaluation> evaluations = dataStore.getEvaluations().values().stream().toList();
        if (!evaluations.isEmpty()) {
            double avgProblemClarity = evaluations.stream()
                .mapToInt(e -> e.getScores().getProblemClarity()).average().orElse(0);
            double avgMethodology = evaluations.stream()
                .mapToInt(e -> e.getScores().getMethodology()).average().orElse(0);
            double avgResults = evaluations.stream()
                .mapToInt(e -> e.getScores().getResults()).average().orElse(0);
            double avgPresentation = evaluations.stream()
                .mapToInt(e -> e.getScores().getPresentation()).average().orElse(0);
            double avgTotal = evaluations.stream()
                .mapToDouble(e -> e.getScores().getTotalScore()).average().orElse(0);
            
            writer.write("\"Average Problem Clarity\"," + String.format("%.2f", avgProblemClarity) + "\n");
            writer.write("\"Average Methodology\"," + String.format("%.2f", avgMethodology) + "\n");
            writer.write("\"Average Results\"," + String.format("%.2f", avgResults) + "\n");
            writer.write("\"Average Presentation\"," + String.format("%.2f", avgPresentation) + "\n");
            writer.write("\"Average Total Score\"," + String.format("%.2f", avgTotal) + "\n");
        }
        
        // Top performers table
        writer.write("\n\nTop Performers\n");
        writer.write("Rank,Presenter,Average Score,Evaluations Count\n");
        
        var presenterScores = new java.util.HashMap<String, java.util.List<Double>>();
        for (Evaluation eval : evaluations) {
            presenterScores.computeIfAbsent(eval.getPresenterId(), k -> new java.util.ArrayList<>())
                .add((double) eval.getScores().getTotalScore());
        }
        
        var sortedPresenters = presenterScores.entrySet().stream()
            .sorted((a, b) -> {
                double avgA = a.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                double avgB = b.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                return Double.compare(avgB, avgA);
            })
            .limit(10)
            .toList();
        
        int rank = 1;
        for (var entry : sortedPresenters) {
            String presenterName = getPresenterName(entry.getKey());
            double avgScore = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            int evalCount = entry.getValue().size();
            
            writer.write(rank + ",\"" + presenterName + "\"," + 
                String.format("%.2f", avgScore) + "," + evalCount + "\n");
            rank++;
        }
    }
    
    /**
     * Counts total number of presenters.
     */
    private int countPresenters() {
        return (int) dataStore.getUsers().values().stream()
            .filter(u -> u instanceof Student)
            .filter(u -> ((Student) u).getPresenterId() != null)
            .count();
    }
    
    /**
     * Counts total number of evaluators.
     */
    private int countEvaluators() {
        return (int) dataStore.getUsers().values().stream()
            .filter(u -> u instanceof com.fci.seminar.model.Evaluator)
            .count();
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
