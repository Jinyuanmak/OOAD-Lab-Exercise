package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
import javax.swing.table.DefaultTableModel;

import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Session;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.util.ErrorHandler;
import com.toedter.calendar.JDateChooser;

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
    private JDateChooser dateChooser;
    private JLabel venueLabel;
    private JComboBox<String> venueCombo;
    private JLabel customVenueLabel;
    private JTextField customVenueField;
    private JLabel meetingLinkLabel;
    private JTextField meetingLinkField;
    private JComboBox<PresentationType> sessionTypeCombo;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    private JButton clearButton;
    
    private Session selectedSession;

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
        
        // Set column widths for better display of IDs
        sessionTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Session ID
        sessionTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        sessionTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Venue
        sessionTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Type
        sessionTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Presenters
        sessionTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Evaluators
        
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
        panel.setPreferredSize(new Dimension(280, 350));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Date picker
        JLabel dateLabel = new JLabel("Date:");
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(dateLabel, gbc);
        
        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(200, 28));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new Date()); // Set to today's date by default
        gbc.gridy = row++;
        panel.add(dateChooser, gbc);
        
        // Session type combo (moved before venue)
        JLabel typeLabel = new JLabel("Session Type:");
        gbc.gridy = row++;
        panel.add(typeLabel, gbc);
        
        sessionTypeCombo = new JComboBox<>(PresentationType.values());
        sessionTypeCombo.addActionListener(e -> updateVenueVisibility());
        gbc.gridy = row++;
        panel.add(sessionTypeCombo, gbc);
        
        // Venue dropdown (conditionally visible for POSTER)
        venueLabel = new JLabel("Venue:");
        gbc.gridy = row++;
        panel.add(venueLabel, gbc);
        
        venueCombo = new JComboBox<>();
        loadVenues();
        venueCombo.setPreferredSize(new Dimension(200, 28));
        venueCombo.addActionListener(e -> updateCustomVenueVisibility());
        gbc.gridy = row++;
        panel.add(venueCombo, gbc);
        
        // Custom venue text field (only visible when "Others" is selected)
        customVenueLabel = new JLabel("Custom Venue:");
        gbc.gridy = row++;
        panel.add(customVenueLabel, gbc);
        
        customVenueField = new JTextField(15);
        customVenueField.setPreferredSize(new Dimension(200, 28));
        gbc.gridy = row++;
        panel.add(customVenueField, gbc);
        
        // Meeting link field (only visible for ORAL sessions)
        meetingLinkLabel = new JLabel("Meeting Link:");
        gbc.gridy = row++;
        panel.add(meetingLinkLabel, gbc);
        
        meetingLinkField = new JTextField(15);
        meetingLinkField.setPreferredSize(new Dimension(200, 28));
        meetingLinkField.setText("https://teams.microsoft.com/l/meetup-join/19%3ameeting_OTg1NTM4OGUtN2Y0OC00NjMwLWJiMjYtYTljMDVkM2E2NTFl%40thread.v2/0?context=%7b%22Tid%22%3a%227e0b5fcf-12c4-4eff-96b6-4664f25dc7da%22%2c%22Oid%22%3a%229f19ed86-c108-4857-ae8e-9c72364e6f5d%22%7d");
        gbc.gridy = row++;
        panel.add(meetingLinkField, gbc);
        
        // Initially hide custom venue and meeting link fields
        customVenueLabel.setVisible(false);
        customVenueField.setVisible(false);
        meetingLinkLabel.setVisible(false);
        meetingLinkField.setVisible(false);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        createButton = new JButton("Create");
        createButton.addActionListener(e -> createSession());
        actionPanel.add(createButton);
        
        editButton = new JButton("Edit");
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
        
        // Set initial visibility based on default session type
        updateVenueVisibility();
        
        return panel;
    }
    
    /**
     * Updates venue field visibility based on session type.
     * ORAL sessions show meeting link field, POSTER sessions show venue dropdown.
     */
    private void updateVenueVisibility() {
        PresentationType type = (PresentationType) sessionTypeCombo.getSelectedItem();
        boolean isPoster = (type == PresentationType.POSTER);
        
        // Show venue fields for POSTER, hide for ORAL
        venueLabel.setVisible(isPoster);
        venueCombo.setVisible(isPoster);
        
        // Show meeting link for ORAL, hide for POSTER
        meetingLinkLabel.setVisible(!isPoster);
        meetingLinkField.setVisible(!isPoster);
        
        // Update custom venue visibility if POSTER
        if (isPoster) {
            updateCustomVenueVisibility();
        } else {
            customVenueLabel.setVisible(false);
            customVenueField.setVisible(false);
        }
    }
    
    /**
     * Updates custom venue field visibility based on dropdown selection.
     * Shows custom field when "Others" is selected.
     */
    private void updateCustomVenueVisibility() {
        String selectedVenue = (String) venueCombo.getSelectedItem();
        boolean showCustom = "Others".equals(selectedVenue);
        
        customVenueLabel.setVisible(showCustom);
        customVenueField.setVisible(showCustom);
        
        if (showCustom) {
            customVenueField.requestFocus();
        }
    }
    
    /**
     * Gets the date from the date chooser.
     * @return the LocalDate from the date chooser
     */
    private LocalDate getDateFromChooser() {
        Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            throw new IllegalArgumentException("Please select a date");
        }
        return selectedDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
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
        
        // Always add "Others" option at the end
        venueCombo.addItem("Others");
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
                // Set date in date chooser
                LocalDate localDate = selectedSession.getDate();
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                dateChooser.setDate(date);
                
                // Set session type first (this will update venue visibility)
                sessionTypeCombo.setSelectedItem(selectedSession.getSessionType());
                
                String venue = selectedSession.getVenue();
                
                // Set venue or meeting link based on session type
                if (selectedSession.getSessionType() == PresentationType.POSTER) {
                    boolean foundVenue = false;
                    
                    // Check if venue exists in dropdown
                    for (int i = 0; i < venueCombo.getItemCount(); i++) {
                        if (venueCombo.getItemAt(i).equals(venue)) {
                            venueCombo.setSelectedIndex(i);
                            foundVenue = true;
                            break;
                        }
                    }
                    
                    // If venue not found in dropdown, it's a custom venue
                    if (!foundVenue) {
                        venueCombo.setSelectedItem("Others");
                        customVenueField.setText(venue);
                    }
                } else {
                    // ORAL session - set meeting link
                    String link = selectedSession.getMeetingLink();
                    meetingLinkField.setText(link != null ? link : "");
                }
                
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
     * Creates a new session.
     */
    private void createSession() {
        try {
            LocalDate date = getDateFromChooser();
            PresentationType type = (PresentationType) sessionTypeCombo.getSelectedItem();
            
            String venue;
            String meetingLink = null;
            
            if (type == PresentationType.ORAL) {
                // ORAL sessions use meeting link
                meetingLink = meetingLinkField.getText().trim();
                if (meetingLink.isEmpty()) {
                    ErrorHandler.showError(this, "Please enter a meeting link for ORAL session");
                    return;
                }
                venue = "ONLINE"; // Set venue to ONLINE for ORAL sessions
            } else {
                // POSTER sessions need physical venue
                String selectedVenue = (String) venueCombo.getSelectedItem();
                
                if (selectedVenue == null || selectedVenue.isEmpty()) {
                    ErrorHandler.showError(this, "Please select a venue for POSTER session");
                    return;
                }
                
                if ("Others".equals(selectedVenue)) {
                    // Use custom venue
                    venue = customVenueField.getText().trim();
                    if (venue.isEmpty()) {
                        ErrorHandler.showError(this, "Please enter a custom venue name");
                        return;
                    }
                } else {
                    venue = selectedVenue;
                }
            }
            
            Session session = new Session();
            session.setSessionId(java.util.UUID.randomUUID().toString().substring(0, 8));
            session.setDate(date);
            session.setVenue(venue);
            session.setMeetingLink(meetingLink);
            session.setSessionType(type);
            
            sessionService.createSession(session);
            
            // Auto-save after creating session
            app.autoSave();
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "Session created successfully!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            refresh();
            
        } catch (IllegalArgumentException e) {
            ErrorHandler.showError(this, e.getMessage());
        } catch (Exception e) {
            ErrorHandler.showError(this, "Failed to create session: " + e.getMessage());
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
            LocalDate date = getDateFromChooser();
            PresentationType type = (PresentationType) sessionTypeCombo.getSelectedItem();
            
            String venue;
            String meetingLink = null;
            
            if (type == PresentationType.ORAL) {
                // ORAL sessions use meeting link
                meetingLink = meetingLinkField.getText().trim();
                if (meetingLink.isEmpty()) {
                    ErrorHandler.showError(this, "Please enter a meeting link for ORAL session");
                    return;
                }
                venue = "ONLINE"; // Set venue to ONLINE for ORAL sessions
            } else {
                // POSTER sessions need physical venue
                String selectedVenue = (String) venueCombo.getSelectedItem();
                
                if (selectedVenue == null || selectedVenue.isEmpty()) {
                    ErrorHandler.showError(this, "Please select a venue for POSTER session");
                    return;
                }
                
                if ("Others".equals(selectedVenue)) {
                    // Use custom venue
                    venue = customVenueField.getText().trim();
                    if (venue.isEmpty()) {
                        ErrorHandler.showError(this, "Please enter a custom venue name");
                        return;
                    }
                } else {
                    venue = selectedVenue;
                }
            }
            
            selectedSession.setDate(date);
            selectedSession.setVenue(venue);
            selectedSession.setMeetingLink(meetingLink);
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
            
        } catch (IllegalArgumentException e) {
            ErrorHandler.showError(this, e.getMessage());
        } catch (Exception e) {
            ErrorHandler.showError(this, "Failed to update session: " + e.getMessage());
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
        dateChooser.setDate(new Date()); // Reset to today's date
        if (venueCombo.getItemCount() > 0) {
            venueCombo.setSelectedIndex(0);
        }
        customVenueField.setText("");
        meetingLinkField.setText("https://teams.microsoft.com/l/meetup-join/19%3ameeting_OTg1NTM4OGUtN2Y0OC00NjMwLWJiMjYtYTljMDVkM2E2NTFl%40thread.v2/0?context=%7b%22Tid%22%3a%227e0b5fcf-12c4-4eff-96b6-4664f25dc7da%22%2c%22Oid%22%3a%229f19ed86-c108-4857-ae8e-9c72364e6f5d%22%7d");
        sessionTypeCombo.setSelectedIndex(0);
        selectedSession = null;
        sessionTable.clearSelection();
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Reset visibility
        updateVenueVisibility();
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
            // Format presenter IDs as comma-separated string or "-" if empty
            String presenterIds = session.getPresenterIds().isEmpty() 
                ? "-" 
                : String.join(", ", session.getPresenterIds());
            
            // Format evaluator IDs as comma-separated string or "-" if empty
            String evaluatorIds = session.getEvaluatorIds().isEmpty() 
                ? "-" 
                : String.join(", ", session.getEvaluatorIds());
            
            Object[] row = {
                session.getSessionId(),
                session.getDate().format(DATE_FORMAT),
                session.getVenue(),
                session.getSessionType(),
                presenterIds,
                evaluatorIds
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
    
    public JDateChooser getDateChooser() {
        return dateChooser;
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
