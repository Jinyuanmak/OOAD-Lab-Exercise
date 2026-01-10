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
        
        // Create center panel with form
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Create title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the title panel with application name.
     * @return the title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("FCI Seminar Management System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the form panel with login fields.
     * @return the form panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Login form title
        JLabel formTitle = new JLabel("Login");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(formTitle, gbc);
        
        // Reset gridwidth for other components
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameField, gbc);
        
        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.addActionListener(e -> performLogin());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(loginButton, gbc);
        
        // Add Enter key listener to password field
        passwordField.addActionListener(e -> performLogin());
        
        return panel;
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
            case STUDENT:
                app.showPanel(SeminarApp.STUDENT_DASHBOARD);
                break;
            case EVALUATOR:
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
