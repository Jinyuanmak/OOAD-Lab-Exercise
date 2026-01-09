package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for evaluators to submit evaluations for presenters.
 * Provides scoring fields for rubric criteria and comments.
 * Requirements: 6.2, 6.3, 6.4, 6.5, 6.6
 */
public class EvaluationFormPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final EvaluationService evaluationService;
    private final UserService userService;
    
    private JComboBox<PresenterItem> presenterComboBox;
    private JSpinner problemClaritySpinner;
    private JSpinner methodologySpinner;
    private JSpinner resultsSpinner;
    private JSpinner presentationSpinner;
    private JTextArea commentsArea;
    private JLabel totalScoreLabel;
    private JButton submitButton;
    private JButton backButton;
    
    private List<Student> assignedPresenters;

    /**
     * Creates a new EvaluationFormPanel.
     * @param app the main application frame
     * @param evaluationService the evaluation service
     * @param userService the user service
     */
    public EvaluationFormPanel(SeminarApp app, EvaluationService evaluationService, UserService userService) {
        this.app = app;
        this.evaluationService = evaluationService;
        this.userService = userService;
        this.assignedPresenters = new ArrayList<>();
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the title panel.
     * @return the title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Evaluation Form");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the form panel with all input fields.
     * @return the form panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Presenter selector
        JLabel presenterLabel = new JLabel("Select Presenter:");
        presenterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(presenterLabel, gbc);
        
        presenterComboBox = new JComboBox<>();
        presenterComboBox.setPreferredSize(new Dimension(400, 30));
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(presenterComboBox, gbc);
        row++;
        
        // Add spacing
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(new JLabel(" "), gbc);
        row++;
        
        // Rubric criteria section
        JLabel rubricLabel = new JLabel("Rubric Scores (1-10):");
        rubricLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        panel.add(rubricLabel, gbc);
        row++;
        
        // Problem Clarity
        gbc.gridwidth = 1;
        JLabel problemClarityLabel = new JLabel("Problem Clarity:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(problemClarityLabel, gbc);
        
        problemClaritySpinner = createScoreSpinner();
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(problemClaritySpinner, gbc);
        row++;
        
        // Methodology
        JLabel methodologyLabel = new JLabel("Methodology:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(methodologyLabel, gbc);
        
        methodologySpinner = createScoreSpinner();
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(methodologySpinner, gbc);
        row++;
        
        // Results
        JLabel resultsLabel = new JLabel("Results:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(resultsLabel, gbc);
        
        resultsSpinner = createScoreSpinner();
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(resultsSpinner, gbc);
        row++;
        
        // Presentation
        JLabel presentationLabel = new JLabel("Presentation:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(presentationLabel, gbc);
        
        presentationSpinner = createScoreSpinner();
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(presentationSpinner, gbc);
        row++;
        
        // Total score display
        JLabel totalLabel = new JLabel("Total Score:");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(totalLabel, gbc);
        
        totalScoreLabel = new JLabel("0 / 40");
        totalScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(totalScoreLabel, gbc);
        row++;
        
        // Add spacing
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        panel.add(new JLabel(" "), gbc);
        row++;
        
        // Comments section
        JLabel commentsLabel = new JLabel("Comments:");
        commentsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        panel.add(commentsLabel, gbc);
        row++;
        
        commentsArea = new JTextArea(5, 40);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        JScrollPane commentsScrollPane = new JScrollPane(commentsArea);
        commentsScrollPane.setPreferredSize(new Dimension(500, 100));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(commentsScrollPane, gbc);
        
        return panel;
    }
    
    /**
     * Creates a spinner for score input (1-10).
     * @return the configured spinner
     */
    private JSpinner createScoreSpinner() {
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 10, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(80, 30));
        
        // Add change listener to update total score
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTotalScore();
            }
        });
        
        return spinner;
    }
    
    /**
     * Updates the total score display based on current spinner values.
     */
    private void updateTotalScore() {
        int problemClarity = (Integer) problemClaritySpinner.getValue();
        int methodology = (Integer) methodologySpinner.getValue();
        int results = (Integer) resultsSpinner.getValue();
        int presentation = (Integer) presentationSpinner.getValue();
        
        int total = problemClarity + methodology + results + presentation;
        totalScoreLabel.setText(total + " / 40");
    }
    
    /**
     * Creates the button panel.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        submitButton = new JButton("Submit Evaluation");
        submitButton.setPreferredSize(new Dimension(180, 40));
        submitButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        submitButton.addActionListener(e -> submitEvaluation());
        panel.add(submitButton);
        
        backButton = new JButton("Back to Dashboard");
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        return panel;
    }
    
    /**
     * Submits the evaluation with validation.
     */
    private void submitEvaluation() {
        try {
            // Validate presenter selection
            if (presenterComboBox.getSelectedItem() == null) {
                ErrorHandler.showWarning(this, "Please select a presenter to evaluate.");
                return;
            }
            
            PresenterItem selectedItem = (PresenterItem) presenterComboBox.getSelectedItem();
            Student presenter = selectedItem.getStudent();
            
            // Get current evaluator
            User currentUser = app.getCurrentUser();
            if (!(currentUser instanceof Evaluator)) {
                ErrorHandler.showError(this, "Only evaluators can submit evaluations.");
                return;
            }
            
            Evaluator evaluator = (Evaluator) currentUser;
            
            // Get scores
            int problemClarity = (Integer) problemClaritySpinner.getValue();
            int methodology = (Integer) methodologySpinner.getValue();
            int results = (Integer) resultsSpinner.getValue();
            int presentation = (Integer) presentationSpinner.getValue();
            
            // Create rubric scores
            RubricScores scores = new RubricScores(problemClarity, methodology, results, presentation);
            
            // Get comments
            String comments = commentsArea.getText().trim();
            
            // Create evaluation
            Evaluation evaluation = new Evaluation();
            evaluation.setPresenterId(presenter.getPresenterId());
            evaluation.setEvaluatorId(evaluator.getId());
            evaluation.setSessionId(selectedItem.getSessionId());
            evaluation.setScores(scores);
            evaluation.setComments(comments);
            
            // Submit evaluation
            evaluationService.submitEvaluation(evaluation);
            
            // Auto-save after submitting evaluation
            app.autoSave();
            
            // Show success message
            javax.swing.JOptionPane.showMessageDialog(this,
                "Evaluation submitted successfully!\nTotal Score: " + scores.getTotalScore() + " / 40",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            // Reset form
            resetForm();
            
        } catch (IllegalArgumentException ex) {
            ErrorHandler.showError(this, "Validation Error: " + ex.getMessage());
        } catch (Exception ex) {
            ErrorHandler.showError(this, "Failed to submit evaluation: " + ex.getMessage());
        }
    }
    
    /**
     * Navigates back to the evaluator dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.EVALUATOR_DASHBOARD);
    }
    
    /**
     * Refreshes the form with current evaluator's assigned presenters.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        if (!(currentUser instanceof Evaluator)) {
            return;
        }
        
        Evaluator evaluator = (Evaluator) currentUser;
        loadAssignedPresenters(evaluator);
        resetForm();
    }
    
    /**
     * Loads the evaluator's assigned presenters into the combo box.
     * @param evaluator the current evaluator
     */
    private void loadAssignedPresenters(Evaluator evaluator) {
        assignedPresenters.clear();
        presenterComboBox.removeAllItems();
        
        List<String> assignedSessionIds = evaluator.getAssignedSessionIds();
        if (assignedSessionIds == null || assignedSessionIds.isEmpty()) {
            return;
        }
        
        // For each assigned session, get all presenters
        for (String sessionId : assignedSessionIds) {
            Session session = app.getSessionService().getSessionById(sessionId);
            if (session == null) {
                continue;
            }
            
            List<String> presenterIds = session.getPresenterIds();
            if (presenterIds != null) {
                for (String presenterId : presenterIds) {
                    Student student = userService.getStudentByPresenterId(presenterId);
                    if (student != null) {
                        assignedPresenters.add(student);
                        PresenterItem item = new PresenterItem(student, sessionId);
                        presenterComboBox.addItem(item);
                    }
                }
            }
        }
    }
    
    /**
     * Resets the form to default values.
     */
    private void resetForm() {
        problemClaritySpinner.setValue(5);
        methodologySpinner.setValue(5);
        resultsSpinner.setValue(5);
        presentationSpinner.setValue(5);
        commentsArea.setText("");
        updateTotalScore();
    }
    
    /**
     * Helper class to store presenter information in combo box.
     */
    private static class PresenterItem {
        private final Student student;
        private final String sessionId;
        
        public PresenterItem(Student student, String sessionId) {
            this.student = student;
            this.sessionId = sessionId;
        }
        
        public Student getStudent() {
            return student;
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        @Override
        public String toString() {
            return student.getUsername() + " - " + student.getResearchTitle();
        }
    }
    
    // Getter methods for testing purposes
    
    public JComboBox<PresenterItem> getPresenterComboBox() {
        return presenterComboBox;
    }
    
    public JSpinner getProblemClaritySpinner() {
        return problemClaritySpinner;
    }
    
    public JSpinner getMethodologySpinner() {
        return methodologySpinner;
    }
    
    public JSpinner getResultsSpinner() {
        return resultsSpinner;
    }
    
    public JSpinner getPresentationSpinner() {
        return presentationSpinner;
    }
    
    public JTextArea getCommentsArea() {
        return commentsArea;
    }
    
    public JLabel getTotalScoreLabel() {
        return totalScoreLabel;
    }
    
    public JButton getSubmitButton() {
        return submitButton;
    }
    
    public JButton getBackButton() {
        return backButton;
    }
}
