package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for managing users (students and evaluators).
 * Allows admin to view, edit, and delete user information.
 */
public class UserManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final UserService userService;
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> userTypeFilter;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;
    
    private User selectedUser;

    public UserManagementPanel(SeminarApp app, UserService userService) {
        this.app = app;
        this.userService = userService;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Filter panel
        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Table
        createUserTable();
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = createActionPanel();
        panel.add(actionPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filter"));
        
        JLabel filterLabel = new JLabel("User Type:");
        panel.add(filterLabel);
        
        String[] types = {"All Users", "Presenters", "Panel Members"};
        userTypeFilter = new JComboBox<>(types);
        userTypeFilter.addActionListener(e -> loadUsers());
        panel.add(userTypeFilter);
        
        return panel;
    }
    
    private void createUserTable() {
        String[] columns = {"ID", "Name", "Role", "Student ID", "Presenter ID", "Evaluator ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(25);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedUser();
            }
        });
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Actions"));
        panel.setPreferredSize(new Dimension(150, 300));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        
        int row = 0;
        
        editButton = new JButton("Edit User");
        editButton.setPreferredSize(new Dimension(120, 35));
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editUser());
        gbc.gridy = row++;
        panel.add(editButton, gbc);
        
        deleteButton = new JButton("Delete User");
        deleteButton.setPreferredSize(new Dimension(120, 35));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteUser());
        gbc.gridy = row++;
        panel.add(deleteButton, gbc);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.addActionListener(e -> refresh());
        gbc.gridy = row++;
        panel.add(refreshButton, gbc);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        backButton = new JButton("Back to Dashboard");
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        return panel;
    }
    
    private void loadUsers() {
        tableModel.setRowCount(0);
        
        String filterType = (String) userTypeFilter.getSelectedItem();
        List<User> users = new ArrayList<>(app.getDataStore().getUsers().values());
        
        for (User user : users) {
            // Skip coordinator
            if (user.getRole().name().equals("COORDINATOR")) {
                continue;
            }
            
            // Apply filter
            if (filterType.equals("Presenters") && !(user instanceof Student)) {
                continue;
            }
            if (filterType.equals("Panel Members") && !(user instanceof Evaluator)) {
                continue;
            }
            
            Object[] row = new Object[6];
            row[0] = user.getId();
            row[1] = user.getUsername();
            row[2] = user.getRole().name();
            
            if (user instanceof Student) {
                Student student = (Student) user;
                row[3] = student.getStudentId() != null ? student.getStudentId() : "";
                row[4] = student.getPresenterId() != null ? student.getPresenterId() : "";
                row[5] = "";
            } else if (user instanceof Evaluator) {
                Evaluator evaluator = (Evaluator) user;
                row[3] = "";
                row[4] = "";
                row[5] = evaluator.getEvaluatorId() != null ? evaluator.getEvaluatorId() : "";
            }
            
            tableModel.addRow(row);
        }
    }
    
    private void updateSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            String userId = (String) tableModel.getValueAt(selectedRow, 0);
            selectedUser = app.getDataStore().getUser(userId);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            selectedUser = null;
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
    private void editUser() {
        if (selectedUser == null) {
            return;
        }
        
        // Open edit dialog
        UserEditDialog dialog = new UserEditDialog(app, userService, selectedUser);
        dialog.setVisible(true);
        
        if (dialog.isUpdated()) {
            refresh();
        }
    }
    
    private void deleteUser() {
        if (selectedUser == null) {
            return;
        }
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete user: " + selectedUser.getUsername() + "?",
            "Confirm Delete",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            try {
                app.getDataStore().removeUser(selectedUser.getId());
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "User deleted successfully!",
                    "Success",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
                refresh();
            } catch (Exception e) {
                ErrorHandler.showError(this, "Failed to delete user: " + e.getMessage());
            }
        }
    }
    
    public void refresh() {
        loadUsers();
        selectedUser = null;
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void navigateBack() {
        app.showPanel(SeminarApp.COORDINATOR_DASHBOARD);
    }
}

/**
 * Dialog for editing user information.
 */
class UserEditDialog extends javax.swing.JDialog {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final UserService userService;
    private final User user;
    private boolean updated = false;
    
    private JTextField nameField;
    private JTextField passwordField;
    private JTextField studentIdField;
    private JTextField researchTitleField;
    private javax.swing.JTextArea abstractArea;
    private JComboBox<String> supervisorCombo;
    private JComboBox<PresentationType> presentationTypeCombo;
    private JButton downloadButton;
    
    public UserEditDialog(SeminarApp app, UserService userService, User user) {
        super(app, "Edit User", true);
        this.app = app;
        this.userService = userService;
        this.user = user;
        initializeUI();
        loadUserData();
        setLocationRelativeTo(app);
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 600);
        
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Title
        JLabel titleLabel = new JLabel("Edit User: " + user.getUsername());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // Name
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(nameLabel, gbc);
        
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(nameField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(passwordLabel, gbc);
        
        passwordField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(passwordField, gbc);
        
        // Student-specific fields
        if (user instanceof Student) {
            Student student = (Student) user;
            
            // Student ID
            JLabel studentIdLabel = new JLabel("Student ID:");
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(studentIdLabel, gbc);
            
            studentIdField = new JTextField(20);
            gbc.gridx = 1;
            gbc.gridy = row++;
            panel.add(studentIdField, gbc);
            
            // Research Title
            JLabel titleLabel2 = new JLabel("Research Title:");
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(titleLabel2, gbc);
            
            researchTitleField = new JTextField(20);
            gbc.gridx = 1;
            gbc.gridy = row++;
            panel.add(researchTitleField, gbc);
            
            // Abstract
            JLabel abstractLabel = new JLabel("Abstract:");
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(abstractLabel, gbc);
            
            abstractArea = new javax.swing.JTextArea(5, 20);
            abstractArea.setLineWrap(true);
            abstractArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(abstractArea);
            gbc.gridx = 1;
            gbc.gridy = row++;
            gbc.fill = GridBagConstraints.BOTH;
            panel.add(scrollPane, gbc);
            
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            
            // Supervisor
            JLabel supervisorLabel = new JLabel("Supervisor:");
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(supervisorLabel, gbc);
            
            // Get all unique supervisor names from database
            java.util.Set<String> supervisors = new java.util.HashSet<>();
            for (User u : app.getDataStore().getUsers().values()) {
                if (u instanceof Student) {
                    Student s = (Student) u;
                    if (s.getSupervisorName() != null && !s.getSupervisorName().trim().isEmpty()) {
                        supervisors.add(s.getSupervisorName());
                    }
                }
            }
            
            // Create combo box with existing supervisors + option to add new
            java.util.List<String> supervisorList = new java.util.ArrayList<>(supervisors);
            java.util.Collections.sort(supervisorList);
            supervisorList.add(0, "-- Select or Type New --");
            
            supervisorCombo = new JComboBox<>(supervisorList.toArray(new String[0]));
            supervisorCombo.setEditable(true);
            gbc.gridx = 1;
            gbc.gridy = row++;
            panel.add(supervisorCombo, gbc);
            
            // Presentation Type
            JLabel typeLabel = new JLabel("Presentation Type:");
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(typeLabel, gbc);
            
            presentationTypeCombo = new JComboBox<>(PresentationType.values());
            gbc.gridx = 1;
            gbc.gridy = row++;
            panel.add(presentationTypeCombo, gbc);
            
            // Download Materials button
            JLabel materialsLabel = new JLabel("Materials:");
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(materialsLabel, gbc);
            
            downloadButton = new JButton("Download Materials");
            downloadButton.setPreferredSize(new Dimension(180, 30));
            downloadButton.addActionListener(e -> downloadMaterials());
            
            // Check if student has uploaded materials
            if (student.getFilePath() == null || student.getFilePath().trim().isEmpty()) {
                downloadButton.setEnabled(false);
                downloadButton.setText("No Materials Uploaded");
            }
            
            gbc.gridx = 1;
            gbc.gridy = row++;
            panel.add(downloadButton, gbc);
        }
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(e -> saveUser());
        panel.add(saveButton);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadUserData() {
        nameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        
        if (user instanceof Student) {
            Student student = (Student) user;
            if (student.getStudentId() != null) {
                studentIdField.setText(student.getStudentId());
            }
            if (student.getResearchTitle() != null) {
                researchTitleField.setText(student.getResearchTitle());
            }
            if (student.getAbstractText() != null) {
                abstractArea.setText(student.getAbstractText());
            }
            if (student.getSupervisorName() != null && !student.getSupervisorName().trim().isEmpty()) {
                supervisorCombo.setSelectedItem(student.getSupervisorName());
            } else {
                supervisorCombo.setSelectedIndex(0); // "-- Select or Type New --"
            }
            if (student.getPresentationType() != null) {
                presentationTypeCombo.setSelectedItem(student.getPresentationType());
            }
        }
    }
    
    private void downloadMaterials() {
        if (!(user instanceof Student)) {
            return;
        }
        
        Student student = (Student) user;
        String filePath = student.getFilePath();
        
        if (filePath == null || filePath.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "No materials have been uploaded by this student.",
                "No Materials",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        java.io.File sourceFile = new java.io.File(filePath);
        
        if (!sourceFile.exists()) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "The uploaded file could not be found at:\n" + filePath,
                "File Not Found",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Open file chooser to select download location
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Presentation Materials");
        fileChooser.setSelectedFile(new java.io.File(sourceFile.getName()));
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File destFile = fileChooser.getSelectedFile();
            
            try {
                // Copy file to selected location
                java.nio.file.Files.copy(
                    sourceFile.toPath(),
                    destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Materials downloaded successfully to:\n" + destFile.getAbsolutePath(),
                    "Download Complete",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (java.io.IOException e) {
                ErrorHandler.showError(this, "Failed to download materials: " + e.getMessage());
            }
        }
    }
    
    private void saveUser() {
        try {
            // Update basic info
            user.setUsername(nameField.getText().trim());
            user.setPassword(passwordField.getText());
            
            // Update student-specific info
            if (user instanceof Student) {
                Student student = (Student) user;
                student.setStudentId(studentIdField.getText().trim().toUpperCase());
                student.setResearchTitle(researchTitleField.getText().trim());
                student.setAbstractText(abstractArea.getText().trim());
                
                // Get supervisor from combo box
                String supervisor = (String) supervisorCombo.getSelectedItem();
                if (supervisor != null && !supervisor.equals("-- Select or Type New --")) {
                    student.setSupervisorName(supervisor.trim());
                } else {
                    student.setSupervisorName("");
                }
                
                student.setPresentationType((PresentationType) presentationTypeCombo.getSelectedItem());
                
                userService.updateStudent(student);
            } else {
                // Update evaluator
                app.getDataStore().updateUser(user);
            }
            
            javax.swing.JOptionPane.showMessageDialog(
                this,
                "User updated successfully!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            
            updated = true;
            dispose();
            
        } catch (Exception e) {
            ErrorHandler.showError(this, "Failed to update user: " + e.getMessage());
        }
    }
    
    public boolean isUpdated() {
        return updated;
    }
}
