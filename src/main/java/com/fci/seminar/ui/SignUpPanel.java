package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Sign up panel for new user registration.
 * Allows students and evaluators to create accounts.
 */
public class SignUpPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final UserService userService;
    
    private JComboBox<String> roleComboBox;
    private JTextField nameField;
    private JTextField studentIdField;
    private JLabel studentIdLabel;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signUpButton;
    private JButton backToLoginButton;

    public SignUpPanel(SeminarApp app, UserService userService) {
        this.app = app;
        this.userService = userService;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new java.awt.Color(245, 245, 250));
        
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(new java.awt.Color(245, 245, 250));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(java.awt.Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        panel.setPreferredSize(new Dimension(450, 650));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new java.awt.Color(41, 98, 255));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign up as Student or Evaluator");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new java.awt.Color(100, 100, 100));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        panel.add(subtitleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Role selection
        JLabel roleLabel = new JLabel("I am a");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(roleLabel, gbc);
        
        String[] roles = {"Presenter (Student)", "Panel Member (Evaluator)"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setPreferredSize(new Dimension(300, 38));
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleComboBox.addActionListener(e -> updateFieldsForRole());
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 15, 10);
        panel.add(roleComboBox, gbc);
        
        // Name field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridy = 4;
        gbc.insets = new Insets(8, 10, 8, 10);
        panel.add(nameLabel, gbc);
        
        nameField = new JTextField(20);
        nameField.setPreferredSize(new Dimension(300, 38));
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 15, 10);
        panel.add(nameField, gbc);
        
        // Student ID field (only for students)
        studentIdLabel = new JLabel("Student ID (10 characters)");
        studentIdLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridy = 6;
        gbc.insets = new Insets(8, 10, 8, 10);
        panel.add(studentIdLabel, gbc);
        
        studentIdField = new JTextField(20);
        studentIdField.setPreferredSize(new Dimension(300, 38));
        studentIdField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        studentIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 10, 15, 10);
        panel.add(studentIdField, gbc);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridy = 8;
        gbc.insets = new Insets(8, 10, 8, 10);
        panel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 38));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 10, 15, 10);
        panel.add(passwordField, gbc);
        
        // Confirm password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridy = 10;
        gbc.insets = new Insets(8, 10, 8, 10);
        panel.add(confirmPasswordLabel, gbc);
        
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setPreferredSize(new Dimension(300, 38));
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 11;
        gbc.insets = new Insets(5, 10, 20, 10);
        panel.add(confirmPasswordField, gbc);
        
        // Sign up button
        signUpButton = new JButton("Sign Up");
        signUpButton.setPreferredSize(new Dimension(300, 42));
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        signUpButton.setBackground(new java.awt.Color(41, 98, 255));
        signUpButton.setForeground(java.awt.Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        signUpButton.addActionListener(e -> performSignUp());
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 5, 10);
        panel.add(signUpButton, gbc);
        
        // Back to login button
        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setPreferredSize(new Dimension(300, 38));
        backToLoginButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backToLoginButton.setBackground(java.awt.Color.WHITE);
        backToLoginButton.setForeground(new java.awt.Color(41, 98, 255));
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setBorder(BorderFactory.createLineBorder(new java.awt.Color(41, 98, 255), 1));
        backToLoginButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backToLoginButton.addActionListener(e -> backToLogin());
        gbc.gridy = 13;
        gbc.insets = new Insets(5, 10, 10, 10);
        panel.add(backToLoginButton, gbc);
        
        outerPanel.add(panel);
        return outerPanel;
    }
    
    private void updateFieldsForRole() {
        boolean isStudent = roleComboBox.getSelectedIndex() == 0;
        studentIdLabel.setVisible(isStudent);
        studentIdField.setVisible(isStudent);
    }
    
    private void performSignUp() {
        try {
            // Get values
            String name = nameField.getText().trim();
            String studentId = studentIdField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            boolean isStudent = roleComboBox.getSelectedIndex() == 0;
            
            // Validate inputs
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Please enter your full name.");
            }
            
            if (isStudent && studentId.isEmpty()) {
                throw new IllegalArgumentException("Please enter your student ID.");
            }
            
            if (isStudent && !studentId.matches("[A-Za-z0-9]{10}")) {
                throw new IllegalArgumentException("Student ID must be exactly 10 characters (letters and numbers).");
            }
            
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Please enter a password.");
            }
            
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
            
            // Check if account already exists
            if (isStudent) {
                if (userService.isStudentIdExists(studentId)) {
                    throw new IllegalArgumentException("An account with this Student ID already exists.");
                }
                if (userService.isUsernameExists(name)) {
                    throw new IllegalArgumentException("An account with this name already exists.");
                }
            } else {
                if (userService.isUsernameExists(name)) {
                    throw new IllegalArgumentException("An account with this name already exists.");
                }
            }
            
            // Create account
            if (isStudent) {
                userService.createStudent(name, studentId, password);
            } else {
                userService.createEvaluator(name, password);
            }
            
            // Show success message
            javax.swing.JOptionPane.showMessageDialog(this,
                "Account created successfully! You can now login.",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields and go back to login
            clearFields();
            backToLogin();
            
        } catch (IllegalArgumentException ex) {
            ErrorHandler.showError(this, ex.getMessage());
        } catch (Exception ex) {
            ErrorHandler.showError(this, "Failed to create account: " + ex.getMessage());
        }
    }
    
    private void backToLogin() {
        clearFields();
        app.showPanel(SeminarApp.LOGIN_PANEL);
    }
    
    private void clearFields() {
        nameField.setText("");
        studentIdField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleComboBox.setSelectedIndex(0);
        updateFieldsForRole();
    }
    
    public void refresh() {
        clearFields();
    }
}
