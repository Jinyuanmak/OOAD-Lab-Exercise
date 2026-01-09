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
    private JButton uploadButton;

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
        
        // Upload button
        uploadButton = new JButton("Upload Presentation Materials");
        uploadButton.setPreferredSize(new Dimension(250, 50));
        uploadButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        uploadButton.addActionListener(e -> navigateToUpload());
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(uploadButton, gbc);
        
        return panel;
    }
    
    /**
     * Navigates to the student registration panel.
     */
    private void navigateToRegistration() {
        app.showPanel(SeminarApp.STUDENT_REGISTRATION);
    }
    
    /**
     * Navigates to the file upload panel.
     */
    private void navigateToUpload() {
        app.showPanel(SeminarApp.FILE_UPLOAD);
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
        } else {
            welcomeLabel.setText("Welcome, Student!");
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
     * Gets the upload button for testing purposes.
     * @return the upload button
     */
    public JButton getUploadButton() {
        return uploadButton;
    }
}
