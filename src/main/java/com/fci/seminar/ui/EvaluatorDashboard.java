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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.service.UserService;

/**
 * Dashboard panel for evaluators.
 * Displays assigned sessions and presenters with navigation to evaluation form.
 * Requirements: 6.1
 */
public class EvaluatorDashboard extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final SessionService sessionService;
    private final UserService userService;
    
    private JLabel welcomeLabel;
    private JTable assignmentsTable;
    private DefaultTableModel tableModel;
    private JButton evaluateButton;
    private JButton refreshButton;

    /**
     * Creates a new EvaluatorDashboard.
     * @param app the main application frame
     * @param sessionService the session service
     * @param userService the user service
     */
    public EvaluatorDashboard(SeminarApp app, SessionService sessionService, UserService userService) {
        this.app = app;
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
        
        // Create center panel with assignments table
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the title panel with welcome message.
     * @return the title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        
        welcomeLabel = new JLabel("Welcome, Evaluator!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(welcomeLabel);
        
        return panel;
    }
    
    /**
     * Creates the center panel with assignments table.
     * @return the center panel
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Add subtitle
        JLabel subtitleLabel = new JLabel("Your Assigned Sessions and Presenters");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(subtitleLabel, BorderLayout.NORTH);
        
        // Create table
        String[] columnNames = {"Session ID", "Date", "Venue", "Type", "Presenter", "Research Title"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        assignmentsTable = new JTable(tableModel);
        assignmentsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        assignmentsTable.setRowHeight(25);
        assignmentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        assignmentsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        assignmentsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        assignmentsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        assignmentsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        assignmentsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        assignmentsTable.getColumnModel().getColumn(5).setPreferredWidth(250);
        
        JScrollPane scrollPane = new JScrollPane(assignmentsTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the button panel with action buttons.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Evaluate button
        evaluateButton = new JButton("Evaluate Selected Presenter");
        evaluateButton.setPreferredSize(new Dimension(220, 40));
        evaluateButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        evaluateButton.addActionListener(e -> navigateToEvaluationForm());
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(evaluateButton, gbc);
        
        // Refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refresh());
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(refreshButton, gbc);
        
        return panel;
    }
    
    /**
     * Navigates to the evaluation form for the selected presenter.
     */
    private void navigateToEvaluationForm() {
        int selectedRow = assignmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            com.fci.seminar.util.ErrorHandler.showWarning(this, 
                "Please select a presenter to evaluate.");
            return;
        }
        
        // Get presenter ID from the selected row (stored in column 4)
        String presenterName = (String) tableModel.getValueAt(selectedRow, 4);
        
        // Navigate to evaluation form
        app.showPanel(SeminarApp.EVALUATION_FORM);
    }
    
    /**
     * Refreshes the dashboard with current user information and assignments.
     * Should be called when the panel becomes visible.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        if (currentUser instanceof Evaluator evaluator) {
            String name = evaluator.getUsername();
            welcomeLabel.setText("Welcome, " + name + "!");
            
            // Load assigned sessions and presenters
            loadAssignments(evaluator);
        } else {
            welcomeLabel.setText("Welcome, Evaluator!");
            tableModel.setRowCount(0);
        }
    }
    
    /**
     * Loads the evaluator's assigned sessions and presenters into the table.
     * @param evaluator the current evaluator
     */
    private void loadAssignments(Evaluator evaluator) {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        List<String> assignedSessionIds = evaluator.getAssignedSessionIds();
        if (assignedSessionIds == null || assignedSessionIds.isEmpty()) {
            return;
        }
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // For each assigned session, get all presenters
        for (String sessionId : assignedSessionIds) {
            Session session = sessionService.getSessionById(sessionId);
            if (session == null) {
                continue;
            }
            
            List<String> presenterIds = session.getPresenterIds();
            if (presenterIds == null || presenterIds.isEmpty()) {
                // Show session even if no presenters assigned yet
                Object[] rowData = {
                    session.getSessionId(),
                    session.getDate().format(dateFormatter),
                    session.getVenue(),
                    session.getSessionType().toString(),
                    "No presenters assigned",
                    ""
                };
                tableModel.addRow(rowData);
            } else {
                // Add a row for each presenter in the session
                for (String presenterId : presenterIds) {
                    Student student = userService.getStudentByPresenterId(presenterId);
                    String presenterName = student != null ? student.getUsername() : "Unknown";
                    String researchTitle = student != null ? student.getResearchTitle() : "";
                    
                    Object[] rowData = {
                        session.getSessionId(),
                        session.getDate().format(dateFormatter),
                        session.getVenue(),
                        session.getSessionType().toString(),
                        presenterName,
                        researchTitle
                    };
                    tableModel.addRow(rowData);
                }
            }
        }
    }
    
    /**
     * Gets the welcome label for testing purposes.
     * @return the welcome label
     */
    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }
    
    /**
     * Gets the assignments table for testing purposes.
     * @return the assignments table
     */
    public JTable getAssignmentsTable() {
        return assignmentsTable;
    }
    
    /**
     * Gets the evaluate button for testing purposes.
     * @return the evaluate button
     */
    public JButton getEvaluateButton() {
        return evaluateButton;
    }
    
    /**
     * Gets the refresh button for testing purposes.
     * @return the refresh button
     */
    public JButton getRefreshButton() {
        return refreshButton;
    }
}
