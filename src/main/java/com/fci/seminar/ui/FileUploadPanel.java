package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for uploading presentation materials.
 * Provides file chooser for selecting slides or poster files.
 * Requirements: 3.1, 3.2, 3.3, 3.4
 */
public class FileUploadPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    
    private JTextField filePathField;
    private JLabel currentFileLabel;
    private JButton browseButton;
    private JButton uploadButton;
    private JButton backButton;
    private File selectedFile;

    /**
     * Creates a new FileUploadPanel.
     * @param app the main application frame
     */
    public FileUploadPanel(SeminarApp app) {
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
        
        // Create center panel with file selection
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
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
        
        JLabel titleLabel = new JLabel("Upload Presentation Materials");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the center panel with file selection components.
     * @return the center panel
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
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
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(currentFileLabel, gbc);
        
        // Reset gridwidth
        gbc.gridwidth = 1;
        
        // File path field
        JLabel selectLabel = new JLabel("Select File:");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(selectLabel, gbc);
        
        filePathField = new JTextField(30);
        filePathField.setPreferredSize(new Dimension(300, 28));
        filePathField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(filePathField, gbc);
        
        browseButton = new JButton("Browse...");
        browseButton.setPreferredSize(new Dimension(100, 28));
        browseButton.addActionListener(e -> browseFile());
        gbc.gridx = 2;
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(browseButton, gbc);
        
        // Instructions
        JLabel instructionsLabel = new JLabel("<html><i>Supported formats: PDF, PPT, PPTX, PNG, JPG</i></html>");
        instructionsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(instructionsLabel, gbc);
        
        return panel;
    }

    /**
     * Creates the button panel with upload and back buttons.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        
        backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        uploadButton = new JButton("Upload");
        uploadButton.setPreferredSize(new Dimension(120, 35));
        uploadButton.addActionListener(e -> performUpload());
        panel.add(uploadButton);
        
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
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    /**
     * Performs the file upload action.
     * Validates the file and saves the reference to the student.
     */
    private void performUpload() {
        if (selectedFile == null || filePathField.getText().trim().isEmpty()) {
            ErrorHandler.showError(this, "Please select a file to upload.");
            return;
        }
        
        // Validate file exists
        if (!selectedFile.exists()) {
            ErrorHandler.showError(this, "File not found or inaccessible.");
            return;
        }
        
        // Get current user
        User currentUser = app.getCurrentUser();
        if (!(currentUser instanceof Student student)) {
            ErrorHandler.showError(this, "You must be logged in as a student to upload files.");
            return;
        }
        
        // Save file reference to student
        student.setFilePath(selectedFile.getAbsolutePath());
        
        // Show success message
        javax.swing.JOptionPane.showMessageDialog(this,
            "File uploaded successfully!\nFile: " + selectedFile.getName(),
            "Success",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
        
        // Update current file label
        currentFileLabel.setText(selectedFile.getName());
        
        // Clear selection
        selectedFile = null;
        filePathField.setText("");
    }
    
    /**
     * Navigates back to the student dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.STUDENT_DASHBOARD);
    }

    /**
     * Refreshes the panel with current user information.
     * Should be called when the panel becomes visible.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        if (currentUser instanceof Student student) {
            String filePath = student.getFilePath();
            if (filePath != null && !filePath.isEmpty()) {
                File file = new File(filePath);
                currentFileLabel.setText(file.getName());
            } else {
                currentFileLabel.setText("No file uploaded");
            }
        } else {
            currentFileLabel.setText("No file uploaded");
        }
        
        // Clear selection
        selectedFile = null;
        filePathField.setText("");
    }
    
    /**
     * Resets the panel to initial state.
     */
    public void reset() {
        selectedFile = null;
        filePathField.setText("");
        currentFileLabel.setText("No file uploaded");
    }
    
    // Getter methods for testing purposes
    
    /**
     * Gets the file path field.
     * @return the file path text field
     */
    public JTextField getFilePathField() {
        return filePathField;
    }
    
    /**
     * Gets the current file label.
     * @return the current file label
     */
    public JLabel getCurrentFileLabel() {
        return currentFileLabel;
    }
    
    /**
     * Gets the browse button.
     * @return the browse button
     */
    public JButton getBrowseButton() {
        return browseButton;
    }
    
    /**
     * Gets the upload button.
     * @return the upload button
     */
    public JButton getUploadButton() {
        return uploadButton;
    }
    
    /**
     * Gets the back button.
     * @return the back button
     */
    public JButton getBackButton() {
        return backButton;
    }
    
    /**
     * Gets the selected file.
     * @return the selected file, or null if none selected
     */
    public File getSelectedFile() {
        return selectedFile;
    }
    
    /**
     * Sets the selected file (for testing purposes).
     * @param file the file to set
     */
    public void setSelectedFile(File file) {
        this.selectedFile = file;
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        } else {
            filePathField.setText("");
        }
    }
}
