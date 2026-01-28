package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.RubricScores;
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
    
    private JLabel presenterNameLabel;
    private JLabel presenterTitleLabel;
    private JButton viewMaterialsButton;
    private JButton downloadMaterialsButton;
    private JSpinner problemClaritySpinner;
    private JSpinner methodologySpinner;
    private JSpinner resultsSpinner;
    private JSpinner presentationSpinner;
    private JTextArea commentsArea;
    private JLabel totalScoreLabel;
    private JButton submitButton;
    private JButton backButton;
    
    private Student selectedPresenter;
    private String selectedSessionId;

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
        gbc.weightx = 0.0;
        
        int row = 0;
        
        // Presenter info (read-only labels)
        JLabel presenterLabel = new JLabel("Presenter:");
        presenterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panel.add(presenterLabel, gbc);
        
        presenterNameLabel = new JLabel("No presenter selected");
        presenterNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        panel.add(presenterNameLabel, gbc);
        row++;
        
        // Research title
        JLabel titleLabel = new JLabel("Research Title:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panel.add(titleLabel, gbc);
        
        presenterTitleLabel = new JLabel("");
        presenterTitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        panel.add(presenterTitleLabel, gbc);
        row++;
        
        // View and Download materials buttons
        JLabel materialsLabel = new JLabel("Materials:");
        materialsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(materialsLabel, gbc);
        
        viewMaterialsButton = new JButton("View Materials");
        viewMaterialsButton.setPreferredSize(new Dimension(180, 35));
        viewMaterialsButton.addActionListener(e -> viewMaterials());
        viewMaterialsButton.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(viewMaterialsButton, gbc);
        
        downloadMaterialsButton = new JButton("Download Materials");
        downloadMaterialsButton.setPreferredSize(new Dimension(180, 35));
        downloadMaterialsButton.addActionListener(e -> downloadMaterials());
        downloadMaterialsButton.setEnabled(false);
        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(downloadMaterialsButton, gbc);
        row++;
        
        // Add spacing
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 0.0;
        panel.add(new JLabel(" "), gbc);
        row++;
        
        // Rubric criteria section
        JLabel rubricLabel = new JLabel("Rubric Scores (1-10):");
        rubricLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.weightx = 0.0;
        panel.add(rubricLabel, gbc);
        row++;
        
        // Row 1: Problem Clarity and Methodology (2x2 layout)
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.25;
        
        JLabel problemClarityLabel = new JLabel("Problem Clarity:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(problemClarityLabel, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.25;
        problemClaritySpinner = createScoreSpinner();
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(problemClaritySpinner, gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.25;
        JLabel methodologyLabel = new JLabel("Methodology:");
        gbc.gridx = 2;
        gbc.gridy = row;
        panel.add(methodologyLabel, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.25;
        methodologySpinner = createScoreSpinner();
        gbc.gridx = 3;
        gbc.gridy = row;
        panel.add(methodologySpinner, gbc);
        row++;
        
        // Row 2: Results and Presentation
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.25;
        JLabel resultsLabel = new JLabel("Results:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(resultsLabel, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.25;
        resultsSpinner = createScoreSpinner();
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(resultsSpinner, gbc);
        
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.25;
        JLabel presentationLabel = new JLabel("Presentation:");
        gbc.gridx = 2;
        gbc.gridy = row;
        panel.add(presentationLabel, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.25;
        presentationSpinner = createScoreSpinner();
        gbc.gridx = 3;
        gbc.gridy = row;
        panel.add(presentationSpinner, gbc);
        row++;
        
        // Total score display
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.25;
        JLabel totalLabel = new JLabel("Total Score:");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(totalLabel, gbc);
        
        gbc.weightx = 0.75;
        totalScoreLabel = new JLabel("20 / 40");
        totalScoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        panel.add(totalScoreLabel, gbc);
        row++;
        
        // Add spacing
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 0.0;
        panel.add(new JLabel(" "), gbc);
        row++;
        
        // Comments section
        JLabel commentsLabel = new JLabel("Comments:");
        commentsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.weightx = 0.0;
        panel.add(commentsLabel, gbc);
        row++;
        
        commentsArea = new JTextArea(8, 40);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        JScrollPane commentsScrollPane = new JScrollPane(commentsArea);
        commentsScrollPane.setPreferredSize(new Dimension(500, 150));
        commentsScrollPane.setMinimumSize(new Dimension(400, 120));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
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
        spinner.setPreferredSize(new Dimension(100, 35));
        spinner.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Make the spinner editor larger
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Add change listener to update total score
        spinner.addChangeListener(e -> updateTotalScore());
        
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
            if (selectedPresenter == null) {
                ErrorHandler.showWarning(this, "No presenter selected. Please go back and select a presenter.");
                return;
            }
            
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
            evaluation.setPresenterId(selectedPresenter.getPresenterId());
            evaluation.setEvaluatorId(evaluator.getId());
            evaluation.setSessionId(selectedSessionId);
            evaluation.setScores(scores);
            evaluation.setComments(comments);
            
            // Submit evaluation
            boolean isUpdate = evaluationService.getEvaluationByEvaluatorAndPresenter(
                evaluator.getId(), 
                selectedPresenter.getPresenterId()
            ) != null;
            
            evaluationService.submitEvaluation(evaluation);
            
            // Auto-save after submitting evaluation
            app.autoSave();
            
            // Show success message
            String action = isUpdate ? "updated" : "submitted";
            javax.swing.JOptionPane.showMessageDialog(this,
                "Evaluation " + action + " successfully!\nTotal Score: " + scores.getTotalScore() + " / 40",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            // Navigate back to dashboard
            navigateBack();
            
        } catch (IllegalArgumentException ex) {
            ErrorHandler.showError(this, "Validation Error: " + ex.getMessage());
        } catch (Exception ex) {
            ErrorHandler.showError(this, "Failed to submit evaluation: " + ex.getMessage());
        }
    }
    
    /**
     * Views the presentation materials for the selected presenter.
     */
    private void viewMaterials() {
        if (selectedPresenter == null) {
            ErrorHandler.showError(this, "No presenter selected.");
            return;
        }
        
        String filePath = selectedPresenter.getFilePath();
        
        if (filePath == null || filePath.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "No materials have been uploaded by this presenter.",
                "No Materials",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        // Show the presentation materials viewer dialog
        PresentationViewerDialog viewer = new PresentationViewerDialog(this, selectedPresenter);
        viewer.setVisible(true);
    }
    
    /**
     * Downloads the presentation materials for the selected presenter.
     */
    private void downloadMaterials() {
        if (selectedPresenter == null) {
            ErrorHandler.showError(this, "No presenter selected.");
            return;
        }
        
        String filePath = selectedPresenter.getFilePath();
        
        if (filePath == null || filePath.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "No materials have been uploaded by this presenter.",
                "No Materials",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        java.io.File sourceFile = new java.io.File(filePath);
        
        if (!sourceFile.exists()) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "The uploaded file could not be found at:\n" + filePath,
                "File Not Found",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Open file chooser to select download location
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Presentation Materials");
        fileChooser.setSelectedFile(new java.io.File(sourceFile.getName()));
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File destFile = fileChooser.getSelectedFile();
            
            try {
                // Copy file to selected location
                java.nio.file.Files.copy(
                    sourceFile.toPath(),
                    destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Materials downloaded successfully to:\n" + destFile.getAbsolutePath(),
                    "Download Complete",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (java.io.IOException e) {
                ErrorHandler.showError(this, "Failed to download materials: " + e.getMessage());
            }
        }
    }
    
    /**
     * Navigates back to the evaluator dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.EVALUATOR_DASHBOARD);
    }
    
    /**
     * Sets the presenter to evaluate.
     * Called from EvaluatorDashboard when navigating to this form.
     * Loads existing evaluation if one exists.
     * @param presenter the selected presenter
     * @param sessionId the session ID
     */
    public void setPresenter(Student presenter, String sessionId) {
        this.selectedPresenter = presenter;
        this.selectedSessionId = sessionId;
        
        if (presenter != null) {
            presenterNameLabel.setText(presenter.getUsername());
            presenterTitleLabel.setText(presenter.getResearchTitle() != null ? presenter.getResearchTitle() : "");
            
            // Enable/disable view and download buttons based on whether materials exist
            boolean hasMaterials = presenter.getFilePath() != null && !presenter.getFilePath().trim().isEmpty();
            viewMaterialsButton.setEnabled(hasMaterials);
            downloadMaterialsButton.setEnabled(hasMaterials);
            
            // Check if evaluation already exists for this evaluator-presenter pair
            User currentUser = app.getCurrentUser();
            if (currentUser instanceof Evaluator) {
                Evaluator evaluator = (Evaluator) currentUser;
                Evaluation existingEvaluation = evaluationService.getEvaluationByEvaluatorAndPresenter(
                    evaluator.getId(), 
                    presenter.getPresenterId()
                );
                
                if (existingEvaluation != null) {
                    // Load existing evaluation data
                    loadEvaluationData(existingEvaluation);
                    submitButton.setText("Update Evaluation");
                } else {
                    // Reset to default values
                    resetForm();
                    submitButton.setText("Submit Evaluation");
                }
            }
        } else {
            presenterNameLabel.setText("No presenter selected");
            presenterTitleLabel.setText("");
            viewMaterialsButton.setEnabled(false);
            downloadMaterialsButton.setEnabled(false);
            resetForm();
            submitButton.setText("Submit Evaluation");
        }
    }
    
    /**
     * Loads existing evaluation data into the form.
     * @param evaluation the existing evaluation
     */
    private void loadEvaluationData(Evaluation evaluation) {
        problemClaritySpinner.setValue(evaluation.getScores().getProblemClarity());
        methodologySpinner.setValue(evaluation.getScores().getMethodology());
        resultsSpinner.setValue(evaluation.getScores().getResults());
        presentationSpinner.setValue(evaluation.getScores().getPresentation());
        commentsArea.setText(evaluation.getComments() != null ? evaluation.getComments() : "");
        updateTotalScore();
    }
    
    /**
     * Refreshes the form - resets to default values.
     */
    public void refresh() {
        // If no presenter is set, clear the display
        if (selectedPresenter == null) {
            presenterNameLabel.setText("No presenter selected");
            presenterTitleLabel.setText("");
        }
        resetForm();
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
    
    // Getter methods for testing purposes
    
    public JLabel getPresenterNameLabel() {
        return presenterNameLabel;
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
