package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.table.DefaultTableModel;

import com.fci.seminar.model.PosterBoard;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.service.PosterBoardService;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for managing poster board assignments.
 * Requirements: 7.1, 7.2, 7.3, 7.4
 */
public class PosterManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final SeminarApp app;
    private final PosterBoardService posterBoardService;
    private final SessionService sessionService;
    private final UserService userService;
    
    private JTable boardTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> boardIdCombo;
    private JTextField presenterField;
    private JComboBox<SessionItem> sessionCombo;
    private JButton assignButton;
    private JButton unassignButton;
    private JButton backButton;

    /**
     * Creates a new PosterManagementPanel.
     * @param app the main application frame
     * @param posterBoardService the poster board service
     * @param sessionService the session service
     * @param userService the user service
     */
    public PosterManagementPanel(SeminarApp app, PosterBoardService posterBoardService, 
                                  SessionService sessionService, UserService userService) {
        this.app = app;
        this.posterBoardService = posterBoardService;
        this.sessionService = sessionService;
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
        
        JLabel titleLabel = new JLabel("Poster Board Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the table panel displaying board assignments.
     * @return the table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Poster Schedule"));
        
        // Add instruction label
        JLabel instructionLabel = new JLabel("Double-click a row to view presentation materials");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(instructionLabel, BorderLayout.NORTH);
        
        // Create table model
        String[] columns = {"Board", "Presenter", "Session Venue", "Session Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        boardTable = new JTable(tableModel);
        boardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boardTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onBoardSelected();
            }
        });
        
        // Add double-click listener to view presentation materials
        boardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = boardTable.getSelectedRow();
                    if (row >= 0) {
                        viewPresentationMaterials(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(boardTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the form panel for board assignment.
     * @return the form panel
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Assign Board"));
        panel.setPreferredSize(new Dimension(280, 300));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Session selector (should be selected first)
        JLabel sessionLabel = new JLabel("Session:");
        gbc.gridy = row++;
        panel.add(sessionLabel, gbc);
        
        sessionCombo = new JComboBox<>();
        sessionCombo.addActionListener(e -> onSessionSelected());
        gbc.gridy = row++;
        panel.add(sessionCombo, gbc);
        
        // Board ID selector
        JLabel boardLabel = new JLabel("Board ID:");
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(boardLabel, gbc);
        
        boardIdCombo = new JComboBox<>();
        gbc.gridy = row++;
        panel.add(boardIdCombo, gbc);
        
        // Presenter selector (filtered by selected session)
        JLabel presenterLabel = new JLabel("Presenter:");
        gbc.gridy = row++;
        panel.add(presenterLabel, gbc);
        
        presenterField = new JTextField(15);
        presenterField.setPreferredSize(new Dimension(200, 28));
        presenterField.setEditable(false); // Read-only, auto-populated from session
        presenterField.setBackground(java.awt.Color.LIGHT_GRAY);
        gbc.gridy = row++;
        panel.add(presenterField, gbc);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        assignButton = new JButton("Assign");
        assignButton.addActionListener(e -> assignBoard());
        actionPanel.add(assignButton);
        
        unassignButton = new JButton("Unassign");
        unassignButton.setEnabled(false);
        unassignButton.addActionListener(e -> unassignBoard());
        actionPanel.add(unassignButton);
        
        gbc.gridy = row++;
        panel.add(actionPanel, gbc);
        
        return panel;
    }

    /**
     * Creates the button panel.
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
     * Handles session selection and updates presenter field.
     */
    private void onSessionSelected() {
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        
        // Clear presenter field
        presenterField.setText("");
        
        if (sessionItem != null) {
            Session session = sessionItem.session;
            
            // Show the presenter assigned to this session (should be only one)
            if (!session.getPresenterIds().isEmpty()) {
                String presenterId = session.getPresenterIds().get(0); // Get first (and only) presenter
                Student student = userService.getStudentByPresenterId(presenterId);
                if (student != null && student.getPresentationType() == PresentationType.POSTER) {
                    presenterField.setText(student.getUsername() + " (" + student.getStudentId() + ")");
                }
            } else {
                presenterField.setText("No presenter assigned to this session");
            }
        }
    }
    
    /**
     * Handles board selection in the table.
     */
    private void onBoardSelected() {
        int selectedRow = boardTable.getSelectedRow();
        unassignButton.setEnabled(selectedRow >= 0);
    }
    
    /**
     * Views presentation materials for the selected board.
     * @param row the selected row in the table
     */
    private void viewPresentationMaterials(int row) {
        String boardId = (String) tableModel.getValueAt(row, 0);
        
        // Get the poster board assignment
        PosterBoard board = posterBoardService.getBoardById(boardId);
        if (board == null) {
            ErrorHandler.showError(this, "Board not found");
            return;
        }
        
        // Get the student information
        Student student = userService.getStudentByPresenterId(board.getPresenterId());
        if (student == null) {
            ErrorHandler.showError(this, "Student not found");
            return;
        }
        
        // Check if student has uploaded materials
        if (student.getFilePath() == null || student.getFilePath().isEmpty()) {
            ErrorHandler.showError(this, "No presentation materials uploaded for this student");
            return;
        }
        
        // Show the presentation materials viewer dialog
        showPresentationViewer(student);
    }
    
    /**
     * Shows a dialog to view presentation materials.
     * @param student the student whose materials to view
     */
    private void showPresentationViewer(Student student) {
        PresentationViewerDialog viewer = new PresentationViewerDialog(this, student);
        viewer.setVisible(true);
    }
    
    /**
     * Assigns a board to a presenter.
     */
    private void assignBoard() {
        String boardId = (String) boardIdCombo.getSelectedItem();
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        
        if (boardId == null || boardId.isEmpty()) {
            ErrorHandler.showError(this, "Please select a board ID");
            return;
        }
        if (sessionItem == null) {
            ErrorHandler.showError(this, "Please select a session");
            return;
        }
        
        // Get the presenter from the session
        Session session = sessionItem.session;
        if (session.getPresenterIds().isEmpty()) {
            ErrorHandler.showError(this, "This session has no presenter assigned. Please assign a presenter in Session Management first.");
            return;
        }
        
        String presenterId = session.getPresenterIds().get(0); // Get the assigned presenter
        
        try {
            posterBoardService.assignBoard(boardId, presenterId, session.getSessionId());
            
            app.autoSave();
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "Board assigned successfully!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            refresh();
            
        } catch (IllegalArgumentException e) {
            ErrorHandler.showError(this, e.getMessage());
        }
    }
    
    /**
     * Unassigns the selected board.
     */
    private void unassignBoard() {
        int selectedRow = boardTable.getSelectedRow();
        if (selectedRow < 0) {
            ErrorHandler.showError(this, "Please select a board to unassign");
            return;
        }
        
        String boardId = (String) tableModel.getValueAt(selectedRow, 0);
        
        if (ErrorHandler.confirmAction(this, "Are you sure you want to unassign board " + boardId + "?")) {
            posterBoardService.unassignBoard(boardId);
            
            app.autoSave();
            
            javax.swing.JOptionPane.showMessageDialog(this,
                "Board unassigned successfully!",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            refresh();
        }
    }
    
    /**
     * Refreshes the panel data.
     */
    public void refresh() {
        // Refresh board table
        tableModel.setRowCount(0);
        List<PosterBoard> assignments = posterBoardService.getAllAssignments();
        
        for (PosterBoard board : assignments) {
            Student student = userService.getStudentByPresenterId(board.getPresenterId());
            Session session = sessionService.getSessionById(board.getSessionId());
            
            String presenterName = student != null ? student.getUsername() : "Unknown";
            String sessionVenue = session != null ? session.getVenue() : "Unknown";
            String sessionDate = session != null ? session.getDate().format(DATE_FORMAT) : "Unknown";
            
            Object[] row = {
                board.getBoardId(),
                presenterName,
                sessionVenue,
                sessionDate
            };
            tableModel.addRow(row);
        }
        
        // Refresh available boards combo
        boardIdCombo.removeAllItems();
        List<String> availableBoards = posterBoardService.getAvailableBoards();
        // Limit to first 20 available boards for UI performance
        int limit = Math.min(20, availableBoards.size());
        for (int i = 0; i < limit; i++) {
            boardIdCombo.addItem(availableBoards.get(i));
        }
        
        // Refresh session combo (only poster sessions)
        sessionCombo.removeAllItems();
        List<Session> sessions = sessionService.getAllSessions();
        for (Session session : sessions) {
            if (session.getSessionType() == PresentationType.POSTER) {
                sessionCombo.addItem(new SessionItem(session));
            }
        }
        
        // Trigger presenter field population if there's a session selected
        if (sessionCombo.getItemCount() > 0) {
            sessionCombo.setSelectedIndex(0);
            onSessionSelected(); // Manually trigger to populate presenter field
        } else {
            // No sessions available, clear presenter field
            presenterField.setText("");
        }
        
        unassignButton.setEnabled(false);
    }
    
    /**
     * Navigates back to the coordinator dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.COORDINATOR_DASHBOARD);
    }
    
    // Inner classes for combo box items
    
    /**
     * Wrapper class for Session items.
     */
    private static class SessionItem {
        final Session session;
        
        SessionItem(Session session) {
            this.session = session;
        }
        
        @Override
        public String toString() {
            return session.getVenue() + " - " + session.getDate().format(DATE_FORMAT);
        }
    }

    // Getter methods for testing
    
    public JTable getBoardTable() {
        return boardTable;
    }
    
    public JComboBox<String> getBoardIdCombo() {
        return boardIdCombo;
    }
    
    public JTextField getPresenterField() {
        return presenterField;
    }
    
    public JComboBox<SessionItem> getSessionCombo() {
        return sessionCombo;
    }
    
    public JButton getAssignButton() {
        return assignButton;
    }
    
    public JButton getUnassignButton() {
        return unassignButton;
    }
    
    public JButton getBackButton() {
        return backButton;
    }
}
