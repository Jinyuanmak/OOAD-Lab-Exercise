package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.service.FileStorageService;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;
import com.fci.seminar.util.FileStorageException;

/**
 * Panel for student seminar registration.
 * Uses the currently logged-in student's account - no new username/password needed.
 * Provides form fields for research title, abstract, supervisor name, and presentation type.
 * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5
 */
public class StudentRegistrationPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final UserService userService;
    
    private JLabel welcomeLabel;
    private JLabel titleLabel;
    private JTextField titleField;
    private JTextArea abstractArea;
    private JComboBox<String> supervisorCombo;
    private JComboBox<PresentationType> presentationTypeCombo;
    private JTextField filePathField;
    private JLabel currentFileLabel;
    private JButton browseButton;
    private JButton submitButton;
    private JButton backButton;
    private boolean isAlreadyRegistered = false;
    private File selectedFile;

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 10, 50));
        
        titleLabel = new JLabel("Seminar Registration");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Welcome message showing current user
        welcomeLabel = new JLabel("Registering as: ");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(welcomeLabel, BorderLayout.CENTER);
        
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

        // Research title field
        JLabel researchTitleLabel = new JLabel("Research Title:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(researchTitleLabel, gbc);
        
        titleField = new JTextField(30);
        titleField.setPreferredSize(new Dimension(350, 28));
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
        abstractScroll.setPreferredSize(new Dimension(350, 120));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(abstractScroll, gbc);
        
        // Supervisor name dropdown
        JLabel supervisorLabel = new JLabel("Supervisor Name:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(supervisorLabel, gbc);
        
        supervisorCombo = new JComboBox<>();
        supervisorCombo.setPreferredSize(new Dimension(350, 28));
        loadSupervisors(); // Load supervisors from database
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(supervisorCombo, gbc);
        
        // Presentation type combo box
        JLabel typeLabel = new JLabel("Presentation Type:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(typeLabel, gbc);
        
        presentationTypeCombo = new JComboBox<>(PresentationType.values());
        presentationTypeCombo.setPreferredSize(new Dimension(350, 28));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(presentationTypeCombo, gbc);
        
        // Current file label
        JLabel currentLabel = new JLabel("Current File:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(currentLabel, gbc);
        
        currentFileLabel = new JLabel("No file uploaded");
        currentFileLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(currentFileLabel, gbc);
        
        // File upload field
        JLabel fileLabel = new JLabel("Upload Materials:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(fileLabel, gbc);
        
        // Create a sub-panel for file path field and browse button
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filePathField = new JTextField(22);
        filePathField.setPreferredSize(new Dimension(240, 28));
        filePathField.setEditable(false);
        filePanel.add(filePathField);
        
        browseButton = new JButton("Browse...");
        browseButton.setPreferredSize(new Dimension(100, 28));
        browseButton.addActionListener(e -> browseFile());
        filePanel.add(browseButton);
        
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(filePanel, gbc);
        
        // File format instructions
        JLabel instructionsLabel = new JLabel("<html><i>Supported: PDF, PPT, PPTX, PNG, JPG</i></html>");
        instructionsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(instructionsLabel, gbc);
        
        return panel;
    }
    
    /**
     * Opens file chooser dialog for file selection.
     */
    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Presentation File");
        
        // Set file filters
        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
        FileNameExtensionFilter pptFilter = new FileNameExtensionFilter("PowerPoint Files (*.ppt, *.pptx)", "ppt", "pptx");
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image Files (*.png, *.jpg)", "png", "jpg", "jpeg");
        
        fileChooser.addChoosableFileFilter(pdfFilter);
        fileChooser.addChoosableFileFilter(pptFilter);
        fileChooser.addChoosableFileFilter(imageFilter);
        fileChooser.setAcceptAllFileFilterUsed(true);
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getName()); // Show only filename, not full path
        }
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
     * Performs the registration or update action.
     * Updates the currently logged-in student's research details.
     */
    private void performRegistration() {
        // Get current logged-in user
        User currentUser = app.getCurrentUser();
        
        if (currentUser == null || !(currentUser instanceof Student)) {
            ErrorHandler.showError(this, "You must be logged in as a student to register.");
            return;
        }
        
        Student student = (Student) currentUser;
        
        String title = titleField.getText().trim();
        String abstractText = abstractArea.getText().trim();
        String supervisor = (String) supervisorCombo.getSelectedItem();
        PresentationType type = (PresentationType) presentationTypeCombo.getSelectedItem();
        
        // Validate required fields
        if (title.isEmpty()) {
            ErrorHandler.showError(this, "Research title is required.");
            titleField.requestFocus();
            return;
        }
        
        if (abstractText.isEmpty()) {
            ErrorHandler.showError(this, "Abstract is required.");
            abstractArea.requestFocus();
            return;
        }
        
        if (supervisor == null || supervisor.isEmpty()) {
            ErrorHandler.showError(this, "Supervisor name is required.");
            supervisorCombo.requestFocus();
            return;
        }
        
        if (type == null) {
            ErrorHandler.showError(this, "Presentation type is required.");
            return;
        }
        
        // Update student's research details
        student.setResearchTitle(title);
        student.setAbstractText(abstractText);
        student.setSupervisorName(supervisor);
        student.setPresentationType(type);
        
        // Handle file upload if a file was selected
        String storagePath = null;
        if (selectedFile != null && selectedFile.exists()) {
            try {
                FileStorageService fileService = FileStorageService.getInstance();
                
                // Validate file type
                if (!fileService.isFileTypeSupported(selectedFile)) {
                    ErrorHandler.showError(this, 
                        "Unsupported file type. Please upload PDF, image (JPG, PNG, GIF), or text files.");
                    return;
                }
                
                // Upload file to centralized storage
                storagePath = fileService.uploadFile(selectedFile, student.getPresenterId());
                
            } catch (FileStorageException e) {
                ErrorHandler.showError(this, e.getUserFriendlyMessage());
                return;
            }
        }
        
        // Update student's file path with storage path
        if (storagePath != null) {
            student.setFilePath(storagePath);
        }
        
        try {
            // Update the student in the system
            userService.updateStudent(student);
            
            // Auto-save after successful registration
            app.autoSave();
            
            // Show appropriate success message
            String message;
            String dialogTitle;
            if (isAlreadyRegistered) {
                message = "Registration updated successfully!\n\n" +
                    "Username: " + student.getUsername() + "\n" +
                    "Presenter ID: " + student.getPresenterId() + "\n" +
                    "Presentation Type: " + type;
                dialogTitle = "Update Complete";
            } else {
                message = "Registration successful!\n\n" +
                    "Username: " + student.getUsername() + "\n" +
                    "Presenter ID: " + student.getPresenterId() + "\n" +
                    "Presentation Type: " + type;
                dialogTitle = "Registration Complete";
            }
            
            javax.swing.JOptionPane.showMessageDialog(this,
                message,
                dialogTitle,
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
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
        titleField.setText("");
        abstractArea.setText("");
        if (supervisorCombo.getItemCount() > 0) {
            supervisorCombo.setSelectedIndex(0);
        }
        presentationTypeCombo.setSelectedIndex(0);
        filePathField.setText("");
        currentFileLabel.setText("No file uploaded");
        selectedFile = null;
    }
    
    /**
     * Loads supervisors from database into the dropdown.
     */
    private void loadSupervisors() {
        supervisorCombo.removeAllItems();
        
        // Get all evaluators (who are supervisors)
        List<com.fci.seminar.model.Evaluator> evaluators = userService.getAllEvaluators();
        
        for (com.fci.seminar.model.Evaluator evaluator : evaluators) {
            supervisorCombo.addItem(evaluator.getUsername());
        }
        
        // Set first item as default if available
        if (supervisorCombo.getItemCount() > 0) {
            supervisorCombo.setSelectedIndex(0);
        }
    }
    
    /**
     * Refreshes the panel with current user data.
     * Call this when navigating to this panel.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        
        // Reload supervisors from database
        loadSupervisors();
        
        // Always clear form first to prevent showing other student's data
        clearForm();
        
        if (currentUser != null && currentUser instanceof Student) {
            Student student = (Student) currentUser;
            
            // Check if student is already registered (has presenter_id)
            isAlreadyRegistered = student.getPresenterId() != null && !student.getPresenterId().isEmpty();
            
            // Update UI based on registration status
            if (isAlreadyRegistered) {
                titleLabel.setText("Update Seminar Registration");
                welcomeLabel.setText("Updating registration for: " + student.getUsername() + " (ID: " + student.getPresenterId() + ")");
                submitButton.setText("Update");
            } else {
                titleLabel.setText("Seminar Registration");
                welcomeLabel.setText("Registering as: " + student.getUsername());
                submitButton.setText("Register");
            }
            
            // Pre-fill form if student has registration data
            if (student.getResearchTitle() != null && !student.getResearchTitle().isEmpty()) {
                titleField.setText(student.getResearchTitle());
            }
            if (student.getAbstractText() != null && !student.getAbstractText().isEmpty()) {
                abstractArea.setText(student.getAbstractText());
            }
            if (student.getSupervisorName() != null && !student.getSupervisorName().isEmpty()) {
                // Select the supervisor in dropdown
                supervisorCombo.setSelectedItem(student.getSupervisorName());
            }
            if (student.getPresentationType() != null) {
                presentationTypeCombo.setSelectedItem(student.getPresentationType());
            }
            if (student.getFilePath() != null && !student.getFilePath().isEmpty()) {
                File existingFile = new File(student.getFilePath());
                if (existingFile.exists()) {
                    currentFileLabel.setText(existingFile.getName());
                } else {
                    currentFileLabel.setText(student.getFilePath() + " (file not found)");
                }
            }
        } else {
            welcomeLabel.setText("Please log in as a student first.");
            isAlreadyRegistered = false;
        }
        
        titleField.requestFocus();
    }
    
    /**
     * Resets the panel to initial state.
     */
    public void reset() {
        clearForm();
        refresh();
    }

    // Getter methods for testing purposes
    
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
     * Gets the supervisor combo box.
     * @return the supervisor combo box
     */
    public JComboBox<String> getSupervisorCombo() {
        return supervisorCombo;
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
