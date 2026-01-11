package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Session;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for session management.
 * Allows coordinators to create, edit, and delete sessions.
 * Requirements: 4.1, 4.2, 4.3, 4.4, 4.5
 */
public class SessionManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final SeminarApp app;
    private final SessionService sessionService;
    
    private JTable sessionTable;
    private DefaultTableModel tableModel;
    private JTextField dateField;
    private JComboBox<String> venueCombo;
    private JComboBox<PresentationType> sessionTypeCombo;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    private JButton clearButton;
    
    private Session selectedSession;
    private boolean isAutoFormatting = false;

    /**
     * Creates a new SessionManagementPanel.
     * @param app the main application frame
     * @param sessionService the session service
     */
    public SessionManagementPanel(SeminarApp app, SessionService sessionService) {
        this.app = app;
        this.sessionService = sessionService;
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
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create table panel
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        contentPanel.add(formPanel, BorderLayout.EAST);
        
        add(contentPanel, BorderLayout.CENTER);
        
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
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Session Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the table panel displaying existing sessions.
     * @return the table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Existing Sessions"));
        
        // Create table model
        String[] columns = {"Session ID", "Date", "Venue", "Type", "Presenters", "Evaluators"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        sessionTable = new JTable(tableModel);
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onSessionSelected();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(sessionTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the form panel for session data entry.
     * @return the form panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Session Details"));
        panel.setPreferredSize(new Dimension(280, 300));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Date field with auto-formatting
        JLabel dateLabel = new JLabel("Date (YYMMDD):");
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(dateLabel, gbc);
        
        dateField = new JTextField(15);
        dateField.setPreferredSize(new Dimension(200, 28));
        dateField.setToolTipText("Enter date as YYMMDD (e.g., 260215 → 2026-02-15)");
        setupDateAutoFormat();
        gbc.gridy = row++;
        panel.add(dateField, gbc);
        
        // Venue dropdown
        JLabel venueLabel = new JLabel("Venue:");
        gbc.gridy = row++;
        panel.add(venueLabel, gbc);
        
        venueCombo = new JComboBox<>();
        loadVenues();
        venueCombo.setPreferredSize(new Dimension(200, 28));
        gbc.gridy = row++;
        panel.add(venueCombo, gbc);
        
        // Session type combo
        JLabel typeLabel = new JLabel("Session Type:");
        gbc.gridy = row++;
        panel.add(typeLabel, gbc);
        
        sessionTypeCombo = new JComboBox<>(PresentationType.values());
        gbc.gridy = row++;
        panel.add(sessionTypeCombo, gbc);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        createButton = new JButton("Create");
        createButton.addActionListener(e -> createSession());
        actionPanel.add(createButton);
        
        editButton = new JButton("Update");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> updateSession());
        actionPanel.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSession());
        actionPanel.add(deleteButton);
        
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        panel.add(actionPanel, gbc);
        
        // Clear button
        clearButton = new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(clearButton, gbc);
        
        return panel;
    }
    
    /**
     * Sets up auto-formatting for the date field.
     * Converts YYMMDD to YYYY-MM-DD format automatically.
     */
    private void setupDateAutoFormat() {
        dateField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                autoFormatDate();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                // Don't auto-format on removal
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                autoFormatDate();
            }
        });
    }
    
    /**
     * Auto-formats the date input from YYMMDD to YYYY-MM-DD.
     */
    private void autoFormatDate() {
        if (isAutoFormatting) return;
        
        String text = dateField.getText().replaceAll("[^0-9]", "");
        
        // Only format when we have exactly 6 digits
        if (text.length() == 6) {
            isAutoFormatting = true;
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    String yy = text.substring(0, 2);
                    String mm = text.substring(2, 4);
                    String dd = text.substring(4, 6);
                    
                    // Validate month (01-12)
                    int month = Integer.parseInt(mm);
                    if (month < 1 || month > 12) {
                        dateField.setText("");
                        ErrorHandler.showError(dateField, "Invalid month: " + mm + ". Month must be between 01 and 12.");
                        return;
                    }
                    
                    // Validate day (01-31)
                    int day = Integer.parseInt(dd);
                    if (day < 1 || day > 31) {
                        dateField.setText("");
                        ErrorHandler.showError(dateField, "Invalid day: " + dd + ". Day must be between 01 and 31.");
                        return;
                    }
                    
                    // Convert YY to YYYY (assume 20xx for years 00-99)
                    int year = Integer.parseInt(yy);
                    String yyyy = (year >= 0 && year <= 99) ? "20" + yy : yy;
                    
                    // Validate the complete date
                    String formatted = yyyy + "-" + mm + "-" + dd;
                    try {
                        LocalDate.parse(formatted, DATE_FORMAT);
                        dateField.setText(formatted);
                    } catch (Exception e) {
                        dateField.setText("");
                        ErrorHandler.showError(dateField, "Invalid date: " + formatted + ". Please check the day is valid for the month.");
                    }
                } finally {
                    isAutoFormatting = false;
                }
            });
        }
    }

    /**
     * Loads venues from database into the combo box.
     */
    private void loadVenues() {
        venueCombo.removeAllItems();
        
        // Get venues from database
        if (app.getDataStore().getDatabaseManager() != null) {
            List<String> venues = app.getDataStore().getDatabaseManager().getAllVenues();
            for (String venue : venues) {
                venueCombo.addItem(venue);
            }
        }
        
        // If no venues loaded, add defaults
        if (venueCombo.getItemCount() == 0) {
            venueCombo.addItem("Auditorium A");
            venueCombo.addItem("Auditorium B");
            venueCombo.addItem("Conference Room 1");
            venueCombo.addItem("Conference Room 2");
            venueCombo.addItem("Exhibition Hall A");
            venueCombo.addItem("Exhibition Hall B");
            venueCombo.addItem("Lecture Hall 1");
            venueCombo.addItem("Lecture Hall 2");
        }
    }

    /**
     * Creates the button panel with back button.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        backButton = new JButton("Back to Dashboard");
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> refresh());
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Handles session selection in the table.
     */
    private void onSessionSelected() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow >= 0) {
            String sessionId = (String) tableModel.getValueAt(selectedRow, 0);
            selectedSession = sessionService.getSessionById(sessionId);
            
            if (selectedSession != null) {
                // Set date in field
                dateField.setText(selectedSession.getDate().format(DATE_FORMAT));
                
                // Set venue in combo
                String venue = selectedSession.getVenue();
                for (int i = 0; i < venueCombo.getItemCount(); i++) {
                    if (venueCombo.getItemAt(i).equals(venue)) {
                        venueCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                sessionTypeCombo.setSelectedItem(selectedSession.getSessionType());
                
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } else {
            selectedSession = null;
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
    /**
     * Gets the date from the text field.
     * @return the LocalDate parsed from the field
     */
    private LocalDate getDateFromField() {
        String text = dateField.getText().trim();
        return LocalDate.parse(text, DATE_FORMAT);
    }
    
    /**
     * Creates a new session.
     */
    private void createSession() {
        try {
            LocalDate date = getDateFromField();
            String venue = (String) venueCombo.getSelectedItem();
            PresentationType type = (PresentationType) sessionTypeCombo.getSelectedItem();
            
            if (venue == null || venue.isEmpty()) {
                ErrorHandler.showError(this, "Please select a venue");
                return;
            }
            
            sessionService.createSession(date, venue, type);
            
            // Auto-save after creating session
            app.autoSave();
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "Session created successfully!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            refresh();
            
        } catch (Exception e) {
            ErrorHandler.showError(this, "Invalid date format. Use YYMMDD (e.g., 260215)");
        }
    }
    
    /**
     * Updates the selected session.
     */
    private void updateSession() {
        if (selectedSession == null) {
            ErrorHandler.showError(this, "Please select a session to update");
            return;
        }
        
        try {
            LocalDate date = getDateFromField();
            String venue = (String) venueCombo.getSelectedItem();
            PresentationType type = (PresentationType) sessionTypeCombo.getSelectedItem();
            
            if (venue == null || venue.isEmpty()) {
                ErrorHandler.showError(this, "Please select a venue");
                return;
            }
            
            selectedSession.setDate(date);
            selectedSession.setVenue(venue);
            selectedSession.setSessionType(type);
            
            sessionService.updateSession(selectedSession);
            
            // Auto-save after updating session
            app.autoSave();
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "Session updated successfully!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            refresh();
            
        } catch (Exception e) {
            ErrorHandler.showError(this, "Invalid date format. Use YYMMDD (e.g., 260215)");
        }
    }
    
    /**
     * Deletes the selected session with confirmation.
     */
    private void deleteSession() {
        if (selectedSession == null) {
            ErrorHandler.showError(this, "Please select a session to delete");
            return;
        }
        
        // Check if session has assignments
        boolean hasAssignments = !selectedSession.getPresenterIds().isEmpty() 
                              || !selectedSession.getEvaluatorIds().isEmpty();
        
        String message = hasAssignments 
            ? "This session has assigned presenters/evaluators. Deleting will remove all assignments. Continue?"
            : "Are you sure you want to delete this session?";
        
        if (ErrorHandler.confirmAction(this, message)) {
            try {
                sessionService.deleteSession(selectedSession.getSessionId());
                
                // Auto-save after deleting session
                app.autoSave();
                
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Session deleted successfully!",
                    "Success",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                refresh();
                
            } catch (IllegalArgumentException e) {
                ErrorHandler.showError(this, e.getMessage());
            }
        }
    }
    
    /**
     * Clears the form fields.
     */
    private void clearForm() {
        dateField.setText("");
        if (venueCombo.getItemCount() > 0) {
            venueCombo.setSelectedIndex(0);
        }
        sessionTypeCombo.setSelectedIndex(0);
        selectedSession = null;
        sessionTable.clearSelection();
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    /**
     * Refreshes the session table and venue list.
     */
    public void refresh() {
        // Reload venues
        loadVenues();
        
        // Reload sessions
        tableModel.setRowCount(0);
        
        List<Session> sessions = sessionService.getAllSessions();
        for (Session session : sessions) {
            Object[] row = {
                session.getSessionId(),
                session.getDate().format(DATE_FORMAT),
                session.getVenue(),
                session.getSessionType(),
                session.getPresenterIds().size(),
                session.getEvaluatorIds().size()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Navigates back to the coordinator dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.COORDINATOR_DASHBOARD);
    }

    // Getter methods for testing
    
    public JTable getSessionTable() {
        return sessionTable;
    }
    
    public JTextField getDateField() {
        return dateField;
    }
    
    public JComboBox<String> getVenueCombo() {
        return venueCombo;
    }
    
    public JComboBox<PresentationType> getSessionTypeCombo() {
        return sessionTypeCombo;
    }
    
    public JButton getCreateButton() {
        return createButton;
    }
    
    public JButton getEditButton() {
        return editButton;
    }
    
    public JButton getDeleteButton() {
        return deleteButton;
    }
    
    public JButton getBackButton() {
        return backButton;
    }
}
