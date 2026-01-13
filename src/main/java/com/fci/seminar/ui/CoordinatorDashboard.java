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

import com.fci.seminar.model.User;

/**
 * Dashboard panel for coordinators.
 * Displays navigation buttons for Sessions, Assignments, Posters, Awards, and Reports.
 * Requirements: 4.1
 */
public class CoordinatorDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private JLabel welcomeLabel;
    private JButton sessionsButton;
    private JButton assignmentsButton;
    private JButton postersButton;
    private JButton awardsButton;
    private JButton reportsButton;

    /**
     * Creates a new CoordinatorDashboard.
     * @param app the main application frame
     */
    public CoordinatorDashboard(SeminarApp app) {
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
        
        welcomeLabel = new JLabel("Welcome, Coordinator!");
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
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Dashboard subtitle
        JLabel subtitleLabel = new JLabel("Coordinator Dashboard");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(subtitleLabel, gbc);
        
        // Reset gridwidth for buttons
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Sessions button
        sessionsButton = createNavButton("Manage Sessions");
        sessionsButton.addActionListener(e -> navigateToSessions());
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(sessionsButton, gbc);
        
        // Assignments button
        assignmentsButton = createNavButton("Manage Assignments");
        assignmentsButton.addActionListener(e -> navigateToAssignments());
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(assignmentsButton, gbc);
        
        // Posters button
        postersButton = createNavButton("Manage Posters");
        postersButton.addActionListener(e -> navigateToPosters());
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(postersButton, gbc);
        
        // Awards button
        awardsButton = createNavButton("Awards & Ceremony");
        awardsButton.addActionListener(e -> navigateToAwards());
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(awardsButton, gbc);
        
        // Reports button
        reportsButton = createNavButton("Generate Reports");
        reportsButton.addActionListener(e -> navigateToReports());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(reportsButton, gbc);
        
        // Logout button
        JButton logoutButton = createNavButton("Logout");
        logoutButton.addActionListener(e -> logout());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
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
     * Creates a navigation button with consistent styling.
     * @param text the button text
     * @return the styled button
     */
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return button;
    }
    
    /**
     * Navigates to the session management panel.
     */
    private void navigateToSessions() {
        app.showPanel(SeminarApp.SESSION_MANAGEMENT);
    }
    
    /**
     * Navigates to the assignment panel.
     */
    private void navigateToAssignments() {
        app.showPanel(SeminarApp.ASSIGNMENT_PANEL);
    }
    
    /**
     * Navigates to the poster management panel.
     */
    private void navigateToPosters() {
        app.showPanel(SeminarApp.POSTER_MANAGEMENT);
    }
    
    /**
     * Navigates to the award panel.
     */
    private void navigateToAwards() {
        app.showPanel(SeminarApp.AWARD_PANEL);
    }
    
    /**
     * Navigates to the report panel.
     */
    private void navigateToReports() {
        app.showPanel(SeminarApp.REPORT_PANEL);
    }
    
    /**
     * Refreshes the dashboard with current user information.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        } else {
            welcomeLabel.setText("Welcome, Coordinator!");
        }
    }

    // Getter methods for testing purposes
    
    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }
    
    public JButton getSessionsButton() {
        return sessionsButton;
    }
    
    public JButton getAssignmentsButton() {
        return assignmentsButton;
    }
    
    public JButton getPostersButton() {
        return postersButton;
    }
    
    public JButton getAwardsButton() {
        return awardsButton;
    }
    
    public JButton getReportsButton() {
        return reportsButton;
    }
}
