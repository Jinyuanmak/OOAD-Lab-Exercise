package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for students to vote for People's Choice award.
 * Each student can vote once for another student (not themselves).
 */
public class VotingPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final UserService userService;
    
    private JPanel candidatesPanel;
    private ButtonGroup candidatesGroup;
    private JButton submitButton;
    private JButton backButton;
    private JLabel statusLabel;

    /**
     * Creates a new VotingPanel.
     * @param app the main application frame
     * @param userService the user service
     */
    public VotingPanel(SeminarApp app, UserService userService) {
        this.app = app;
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
        
        // Create content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the title panel.
     * @return the title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Vote for People's Choice Award");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel instructionLabel = new JLabel("<html><center>Select one presenter to vote for.<br>" +
            "You can only vote once and cannot vote for yourself.</center></html>");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(instructionLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the content panel with candidate list.
     * @return the content panel
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create candidates panel
        candidatesPanel = new JPanel(new GridBagLayout());
        candidatesPanel.setBorder(BorderFactory.createTitledBorder("Select a Presenter"));
        
        JScrollPane scrollPane = new JScrollPane(candidatesPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * Creates the button panel.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        submitButton = new JButton("Submit Vote");
        submitButton.setPreferredSize(new Dimension(150, 35));
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.addActionListener(e -> submitVote());
        panel.add(submitButton);
        
        backButton = new JButton("Back to Dashboard");
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        return panel;
    }
    
    /**
     * Refreshes the panel with current candidates.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            return;
        }
        
        Student currentStudent = (Student) currentUser;
        
        // Check if student has already voted
        if (currentStudent.hasVoted()) {
            showAlreadyVotedMessage();
            return;
        }
        
        // Load candidates (all students except current user)
        loadCandidates(currentStudent);
    }
    
    /**
     * Shows message when student has already voted.
     */
    private void showAlreadyVotedMessage() {
        candidatesPanel.removeAll();
        candidatesPanel.setLayout(new BorderLayout());
        
        JLabel messageLabel = new JLabel("<html><center>You have already voted!<br><br>" +
            "Thank you for participating in the People's Choice voting.</center></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        candidatesPanel.add(messageLabel, BorderLayout.CENTER);
        
        submitButton.setEnabled(false);
        statusLabel.setText("Voting closed for you");
        
        candidatesPanel.revalidate();
        candidatesPanel.repaint();
    }
    
    /**
     * Loads candidates for voting.
     * @param currentStudent the current logged-in student
     */
    private void loadCandidates(Student currentStudent) {
        candidatesPanel.removeAll();
        candidatesPanel.setLayout(new GridBagLayout());
        candidatesGroup = new ButtonGroup();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        List<Student> allStudents = userService.getAllStudents();
        int row = 0;
        int candidateCount = 0;
        
        for (Student student : allStudents) {
            // Skip current user and students without presenter ID
            if (student.getId().equals(currentStudent.getId()) || 
                student.getPresenterId() == null || 
                student.getPresenterId().isEmpty()) {
                continue;
            }
            
            // Create radio button for this candidate
            JRadioButton radioButton = new JRadioButton();
            radioButton.setActionCommand(student.getPresenterId());
            candidatesGroup.add(radioButton);
            
            // Create candidate info panel
            JPanel candidatePanel = new JPanel(new BorderLayout(10, 5));
            candidatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            candidatePanel.add(radioButton, BorderLayout.WEST);
            
            // Candidate details
            JPanel detailsPanel = new JPanel(new GridBagLayout());
            GridBagConstraints detailGbc = new GridBagConstraints();
            detailGbc.anchor = GridBagConstraints.WEST;
            detailGbc.insets = new Insets(2, 5, 2, 5);
            
            // Name
            JLabel nameLabel = new JLabel(student.getUsername());
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            detailGbc.gridx = 0;
            detailGbc.gridy = 0;
            detailsPanel.add(nameLabel, detailGbc);
            
            // Student ID
            JLabel idLabel = new JLabel("ID: " + student.getStudentId());
            idLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            detailGbc.gridx = 1;
            detailGbc.gridy = 0;
            detailsPanel.add(idLabel, detailGbc);
            
            // Research Title
            if (student.getResearchTitle() != null && !student.getResearchTitle().isEmpty()) {
                JLabel titleLabel = new JLabel("Research: " + student.getResearchTitle());
                titleLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
                detailGbc.gridx = 0;
                detailGbc.gridy = 1;
                detailGbc.gridwidth = 2;
                detailsPanel.add(titleLabel, detailGbc);
            }
            
            candidatePanel.add(detailsPanel, BorderLayout.CENTER);
            
            // Add "View Materials" button if student has uploaded materials
            if (student.getFilePath() != null && !student.getFilePath().isEmpty()) {
                JButton viewButton = new JButton("View Materials");
                viewButton.setPreferredSize(new Dimension(130, 30));
                viewButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
                viewButton.addActionListener(e -> viewPresentationMaterials(student));
                candidatePanel.add(viewButton, BorderLayout.EAST);
            }
            
            gbc.gridy = row++;
            candidatesPanel.add(candidatePanel, gbc);
            candidateCount++;
        }
        
        if (candidateCount == 0) {
            JLabel noCandidatesLabel = new JLabel("No other presenters available to vote for.");
            noCandidatesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy = 0;
            candidatesPanel.add(noCandidatesLabel, gbc);
            submitButton.setEnabled(false);
        } else {
            statusLabel.setText(candidateCount + " presenter(s) available");
            submitButton.setEnabled(true);
        }
        
        candidatesPanel.revalidate();
        candidatesPanel.repaint();
    }
    
    /**
     * Submits the vote.
     */
    private void submitVote() {
        User currentUser = app.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            return;
        }
        
        Student currentStudent = (Student) currentUser;
        
        // Check if already voted
        if (currentStudent.hasVoted()) {
            ErrorHandler.showError(this, "You have already voted!");
            return;
        }
        
        // Get selected candidate
        if (candidatesGroup.getSelection() == null) {
            ErrorHandler.showError(this, "Please select a presenter to vote for.");
            return;
        }
        
        String votedForPresenterId = candidatesGroup.getSelection().getActionCommand();
        
        // Confirm vote
        Student votedForStudent = userService.getStudentByPresenterId(votedForPresenterId);
        if (votedForStudent == null) {
            ErrorHandler.showError(this, "Selected presenter not found.");
            return;
        }
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to vote for:\n" + votedForStudent.getUsername() + "?\n\n" +
            "You can only vote once and cannot change your vote.",
            "Confirm Vote",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }
        
        // Submit vote
        try {
            // Record vote in database
            recordVote(currentStudent.getStudentId(), votedForPresenterId);
            
            // Update vote count for voted student
            votedForStudent.setVoteCount(votedForStudent.getVoteCount() + 1);
            userService.updateStudent(votedForStudent);
            
            // Mark current student as voted
            currentStudent.setHasVoted(true);
            userService.updateStudent(currentStudent);
            
            // Update current user in app
            app.setCurrentUser(currentStudent);
            
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Your vote has been recorded successfully!\n" +
                "Thank you for participating!",
                "Vote Submitted",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            
            // Refresh to show "already voted" message
            refresh();
            
        } catch (Exception e) {
            ErrorHandler.showError(this, "Failed to submit vote: " + e.getMessage());
        }
    }
    
    /**
     * Views presentation materials for a student.
     * @param student the student whose materials to view
     */
    private void viewPresentationMaterials(Student student) {
        // Check if student has uploaded materials
        if (student.getFilePath() == null || student.getFilePath().isEmpty()) {
            ErrorHandler.showError(this, "No presentation materials uploaded for this student");
            return;
        }
        
        // Show the presentation materials viewer dialog
        showPresentationViewer(student);
    }
    
    /**
     * Shows a frame to view presentation materials.
     * @param student the student whose materials to view
     */
    private void showPresentationViewer(Student student) {
        PresentationViewerDialog viewer = new PresentationViewerDialog(this, student);
        viewer.setVisible(true);
    }
    
    /**
     * Records vote in the database.
     * @param voterStudentId the student ID of the voter
     * @param votedForPresenterId the presenter ID being voted for
     */
    private void recordVote(String voterStudentId, String votedForPresenterId) {
        String sql = "INSERT INTO votes (voter_student_id, voted_for_presenter_id) VALUES (?, ?)";
        
        try (java.sql.PreparedStatement stmt = app.getDataStore().getDatabaseManager()
                .getConnection().prepareStatement(sql)) {
            stmt.setString(1, voterStudentId);
            stmt.setString(2, votedForPresenterId);
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to record vote: " + e.getMessage(), e);
        }
    }
    
    /**
     * Navigates back to the student dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.STUDENT_DASHBOARD);
    }
}
