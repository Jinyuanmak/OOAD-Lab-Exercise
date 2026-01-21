package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.fci.seminar.model.User;
import com.fci.seminar.model.UserRole;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Login panel for user authentication.
 * Provides username and password fields for login with auto role detection.
 * Requirements: 1.1, 1.2, 1.3
 */
public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final UserService userService;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    /**
     * Creates a new LoginPanel.
     * @param app the main application frame
     * @param userService the user service for authentication
     */
    public LoginPanel(SeminarApp app, UserService userService) {
        this.app = app;
        this.userService = userService;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(245, 245, 250));
        
        // Create center panel with form
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the form panel with login fields.
     * @return the form panel
     */
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new java.awt.Color(245, 245, 250));
        
        // Create inner panel with white background and shadow effect
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(java.awt.Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        panel.setPreferredSize(new Dimension(450, 450));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Application title
        JLabel titleLabel = new JLabel("FCI Seminar Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new java.awt.Color(41, 98, 255));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Faculty of Computing and Informatics");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new java.awt.Color(100, 100, 100));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 25, 10);
        panel.add(subtitleLabel, gbc);
        
        // Login form title
        JLabel formTitle = new JLabel("Sign In");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(formTitle, gbc);
        
        // Reset gridwidth and insets for form fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Username label
        JLabel usernameLabel = new JLabel("Username or Student ID");
        usernameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(usernameLabel, gbc);
        
        // Username field
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(300, 38));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 15, 10);
        panel.add(usernameField, gbc);
        
        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 10, 8, 10);
        panel.add(passwordLabel, gbc);
        
        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 38));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 10, 25, 10);
        panel.add(passwordField, gbc);
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(300, 42));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginButton.setBackground(new java.awt.Color(41, 98, 255));
        loginButton.setForeground(java.awt.Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 5, 10);
        panel.add(loginButton, gbc);
        
        // Sign up button
        JButton signUpButton = new JButton("Create Account");
        signUpButton.setPreferredSize(new Dimension(300, 38));
        signUpButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        signUpButton.setBackground(java.awt.Color.WHITE);
        signUpButton.setForeground(new java.awt.Color(41, 98, 255));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createLineBorder(new java.awt.Color(41, 98, 255), 1));
        signUpButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        signUpButton.addActionListener(e -> navigateToSignUp());
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 10, 10, 10);
        panel.add(signUpButton, gbc);
        
        // Add Enter key listener to password field
        passwordField.addActionListener(e -> performLogin());
        
        // Add the panel to outer panel
        outerPanel.add(panel);
        
        return outerPanel;
    }
    
    /**
     * Navigates to the sign up panel.
     */
    private void navigateToSignUp() {
        app.showPanel(SeminarApp.SIGN_UP_PANEL);
    }

    
    /**
     * Performs the login action.
     * Validates input, authenticates user, and navigates to appropriate dashboard.
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validate input
        if (username.isEmpty()) {
            ErrorHandler.showError(this, "Please enter your username.");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            ErrorHandler.showError(this, "Please enter your password.");
            passwordField.requestFocus();
            return;
        }
        
        // Authenticate user (auto-detect role)
        User authenticatedUser = userService.authenticate(username, password);
        
        if (authenticatedUser == null) {
            ErrorHandler.showError(this, "Invalid credentials. Please check your username and password.");
            passwordField.setText("");
            passwordField.requestFocus();
            return;
        }
        
        // Set current user in app
        app.setCurrentUser(authenticatedUser);
        
        // Clear fields for security
        clearFields();
        
        // Navigate to appropriate dashboard based on role
        navigateToDashboard(authenticatedUser.getRole());
    }
    
    /**
     * Navigates to the appropriate dashboard based on user role.
     * @param role the user's role
     */
    private void navigateToDashboard(UserRole role) {
        switch (role) {
            case PRESENTER:
                app.showPanel(SeminarApp.STUDENT_DASHBOARD);
                break;
            case PANEL_MEMBER:
                app.showPanel(SeminarApp.EVALUATOR_DASHBOARD);
                break;
            case COORDINATOR:
                app.showPanel(SeminarApp.COORDINATOR_DASHBOARD);
                break;
            default:
                // Should not happen, but stay on login if it does
                ErrorHandler.showError(this, "Unknown user role.");
        }
    }
    
    /**
     * Clears all input fields.
     */
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
    
    /**
     * Resets the panel to initial state.
     * Called when returning to login screen.
     */
    public void reset() {
        clearFields();
        usernameField.requestFocus();
    }
    
    /**
     * Gets the username field for testing purposes.
     * @return the username text field
     */
    public JTextField getUsernameField() {
        return usernameField;
    }
    
    /**
     * Gets the password field for testing purposes.
     * @return the password field
     */
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    
    /**
     * Gets the login button for testing purposes.
     * @return the login button
     */
    public JButton getLoginButton() {
        return loginButton;
    }
}
