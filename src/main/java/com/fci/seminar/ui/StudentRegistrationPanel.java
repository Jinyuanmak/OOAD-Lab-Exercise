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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Student;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for student registration.
 * Provides form fields for research title, abstract, supervisor name, and presentation type.
 * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5
 */
public class StudentRegistrationPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final UserService userService;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField titleField;
    private JTextArea abstractArea;
    private JTextField supervisorField;
    private JComboBox<PresentationType> presentationTypeCombo;
    private JButton submitButton;
    private JButton backButton;

    /**
     * Creates a new StudentRegistrationPanel.
     * @param app the main application frame
     * @param userService the user service for registration
     */
    public StudentRegistrationPanel(SeminarApp app, UserService userService) {
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
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Student Registration");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the form panel with registration fields.
     * @return the form panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(30);
        usernameField.setPreferredSize(new Dimension(300, 28));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(30);
        passwordField.setPreferredSize(new Dimension(300, 28));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, gbc);

        // Research title field
        JLabel titleLabel = new JLabel("Research Title:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(titleLabel, gbc);
        
        titleField = new JTextField(30);
        titleField.setPreferredSize(new Dimension(300, 28));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleField, gbc);
        
        // Abstract text area
        JLabel abstractLabel = new JLabel("Abstract:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(abstractLabel, gbc);
        
        abstractArea = new JTextArea(6, 30);
        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);
        JScrollPane abstractScroll = new JScrollPane(abstractArea);
        abstractScroll.setPreferredSize(new Dimension(300, 120));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(abstractScroll, gbc);
        
        // Supervisor name field
        JLabel supervisorLabel = new JLabel("Supervisor Name:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(supervisorLabel, gbc);
        
        supervisorField = new JTextField(30);
        supervisorField.setPreferredSize(new Dimension(300, 28));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(supervisorField, gbc);
        
        // Presentation type combo box
        JLabel typeLabel = new JLabel("Presentation Type:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(typeLabel, gbc);
        
        presentationTypeCombo = new JComboBox<>(PresentationType.values());
        presentationTypeCombo.setPreferredSize(new Dimension(300, 28));
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(presentationTypeCombo, gbc);
        
        return panel;
    }

    /**
     * Creates the button panel with submit and back buttons.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        
        backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        submitButton = new JButton("Register");
        submitButton.setPreferredSize(new Dimension(120, 35));
        submitButton.addActionListener(e -> performRegistration());
        panel.add(submitButton);
        
        return panel;
    }
    
    /**
     * Performs the registration action.
     * Validates input and registers the student.
     */
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String title = titleField.getText().trim();
        String abstractText = abstractArea.getText().trim();
        String supervisor = supervisorField.getText().trim();
        PresentationType type = (PresentationType) presentationTypeCombo.getSelectedItem();
        
        // Create student object
        Student student = new Student();
        student.setUsername(username);
        student.setPassword(password);
        student.setResearchTitle(title);
        student.setAbstractText(abstractText);
        student.setSupervisorName(supervisor);
        student.setPresentationType(type);
        
        try {
            userService.registerStudent(student);
            
            // Auto-save after successful registration
            app.autoSave();
            
            // Show success message
            javax.swing.JOptionPane.showMessageDialog(this,
                "Registration successful!\nYour Presenter ID: " + student.getPresenterId(),
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            clearForm();
            
            // Navigate back to dashboard
            navigateBack();
            
        } catch (IllegalArgumentException e) {
            ErrorHandler.showError(this, e.getMessage());
        }
    }
    
    /**
     * Navigates back to the student dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.STUDENT_DASHBOARD);
    }
    
    /**
     * Clears all form fields.
     */
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        titleField.setText("");
        abstractArea.setText("");
        supervisorField.setText("");
        presentationTypeCombo.setSelectedIndex(0);
    }
    
    /**
     * Resets the panel to initial state.
     */
    public void reset() {
        clearForm();
        usernameField.requestFocus();
    }

    // Getter methods for testing purposes
    
    /**
     * Gets the username field.
     * @return the username text field
     */
    public JTextField getUsernameField() {
        return usernameField;
    }
    
    /**
     * Gets the password field.
     * @return the password field
     */
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    
    /**
     * Gets the title field.
     * @return the title text field
     */
    public JTextField getTitleField() {
        return titleField;
    }
    
    /**
     * Gets the abstract text area.
     * @return the abstract text area
     */
    public JTextArea getAbstractArea() {
        return abstractArea;
    }
    
    /**
     * Gets the supervisor field.
     * @return the supervisor text field
     */
    public JTextField getSupervisorField() {
        return supervisorField;
    }
    
    /**
     * Gets the presentation type combo box.
     * @return the presentation type combo box
     */
    public JComboBox<PresentationType> getPresentationTypeCombo() {
        return presentationTypeCombo;
    }
    
    /**
     * Gets the submit button.
     * @return the submit button
     */
    public JButton getSubmitButton() {
        return submitButton;
    }
    
    /**
     * Gets the back button.
     * @return the back button
     */
    public JButton getBackButton() {
        return backButton;
    }
}
