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
import javax.swing.SwingConstants;

import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;

/**
 * Dashboard panel for students.
 * Displays welcome message and navigation buttons for Registration and Upload.
 * Requirements: 2.1
 */
public class StudentDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private JLabel welcomeLabel;
    private JButton registrationButton;
    private JButton mySessionButton;
    private JButton voteButton;

    /**
     * Creates a new StudentDashboard.
     * @param app the main application frame
     */
    public StudentDashboard(SeminarApp app) {
        this.app = app;
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
        
        // Create center panel with navigation buttons
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the title panel with welcome message.
     * @return the title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        
        welcomeLabel = new JLabel("Welcome, Student!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(welcomeLabel);
        
        return panel;
    }
    
    /**
     * Creates the center panel with navigation buttons.
     * @return the center panel
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Dashboard subtitle
        JLabel subtitleLabel = new JLabel("Student Dashboard");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(subtitleLabel, gbc);
        
        // Registration button
        registrationButton = new JButton("Register for Seminar");
        registrationButton.setPreferredSize(new Dimension(250, 50));
        registrationButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        registrationButton.addActionListener(e -> navigateToRegistration());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(registrationButton, gbc);
        
        // My Session button
        mySessionButton = new JButton("My Session");
        mySessionButton.setPreferredSize(new Dimension(250, 50));
        mySessionButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mySessionButton.addActionListener(e -> navigateToMySession());
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(mySessionButton, gbc);
        
        // Vote button
        voteButton = new JButton("Vote for People's Choice");
        voteButton.setPreferredSize(new Dimension(250, 50));
        voteButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        voteButton.addActionListener(e -> navigateToVoting());
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(voteButton, gbc);
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(250, 50));
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutButton.addActionListener(e -> logout());
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(logoutButton, gbc);
        
        return panel;
    }
    
    /**
     * Logs out the current user and returns to login screen.
     */
    private void logout() {
        app.setCurrentUser(null);
        app.showPanel(SeminarApp.LOGIN_PANEL);
    }
    
    /**
     * Navigates to the student registration panel.
     */
    private void navigateToRegistration() {
        app.showPanel(SeminarApp.STUDENT_REGISTRATION);
    }
    
    /**
     * Navigates to the my session panel.
     */
    private void navigateToMySession() {
        app.showPanel(SeminarApp.MY_SESSION);
    }
    
    /**
     * Navigates to the voting panel.
     */
    private void navigateToVoting() {
        app.showPanel(SeminarApp.VOTING_PANEL);
    }
    
    /**
     * Refreshes the dashboard with current user information.
     * Should be called when the panel becomes visible.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        if (currentUser instanceof Student student) {
            String name = student.getUsername();
            welcomeLabel.setText("Welcome, " + name + "!");
            
            // Update registration button text based on registration status
            boolean isRegistered = student.getPresenterId() != null && !student.getPresenterId().isEmpty();
            if (isRegistered) {
                registrationButton.setText("Update Registration");
            } else {
                registrationButton.setText("Register for Seminar");
            }
            
            // Update vote button text based on voting status
            if (student.hasVoted()) {
                voteButton.setText("Already Voted");
                voteButton.setEnabled(false);
            } else {
                voteButton.setText("Vote for People's Choice");
                voteButton.setEnabled(true);
            }
        } else {
            welcomeLabel.setText("Welcome, Student!");
            registrationButton.setText("Register for Seminar");
            voteButton.setText("Vote for People's Choice");
            voteButton.setEnabled(true);
        }
    }
    
    /**
     * Gets the welcome label for testing purposes.
     * @return the welcome label
     */
    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }
    
    /**
     * Gets the registration button for testing purposes.
     * @return the registration button
     */
    public JButton getRegistrationButton() {
        return registrationButton;
    }
    
    /**
     * Gets the my session button for testing purposes.
     * @return the my session button
     */
    public JButton getMySessionButton() {
        return mySessionButton;
    }
    
    /**
     * Gets the vote button for testing purposes.
     * @return the vote button
     */
    public JButton getVoteButton() {
        return voteButton;
    }
}
