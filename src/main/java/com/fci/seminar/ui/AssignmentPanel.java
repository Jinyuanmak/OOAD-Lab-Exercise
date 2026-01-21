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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for managing presenter and evaluator assignments to sessions.
 * Requirements: 5.1, 5.2, 5.3, 5.4, 5.5
 */
public class AssignmentPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final SeminarApp app;
    private final SessionService sessionService;
    private final UserService userService;
    
    private JComboBox<SessionItem> sessionCombo;
    private JList<PresenterItem> availablePresentersList;
    private JList<PresenterItem> assignedPresentersList;
    private JList<EvaluatorItem> availableEvaluatorsList;
    private JList<EvaluatorItem> assignedEvaluatorsList;
    private DefaultListModel<PresenterItem> availablePresentersModel;
    private DefaultListModel<PresenterItem> assignedPresentersModel;
    private DefaultListModel<EvaluatorItem> availableEvaluatorsModel;
    private DefaultListModel<EvaluatorItem> assignedEvaluatorsModel;
    
    private JButton assignPresenterButton;
    private JButton unassignPresenterButton;
    private JButton assignEvaluatorButton;
    private JButton unassignEvaluatorButton;
    private JButton backButton;

    /**
     * Creates a new AssignmentPanel.
     * @param app the main application frame
     * @param sessionService the session service
     * @param userService the user service
     */
    public AssignmentPanel(SeminarApp app, SessionService sessionService, UserService userService) {
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
        
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create session selector panel
        JPanel selectorPanel = createSessionSelectorPanel();
        contentPanel.add(selectorPanel, BorderLayout.NORTH);
        
        // Create assignment panels
        JPanel assignmentPanel = createAssignmentPanel();
        contentPanel.add(assignmentPanel, BorderLayout.CENTER);
        
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
        
        JLabel titleLabel = new JLabel("Presenter & Evaluator Assignments");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the session selector panel.
     * @return the session selector panel
     */
    private JPanel createSessionSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Select Session"));
        
        JLabel label = new JLabel("Session:");
        panel.add(label);
        
        sessionCombo = new JComboBox<>();
        sessionCombo.setPreferredSize(new Dimension(400, 28));
        sessionCombo.addActionListener(e -> onSessionSelected());
        panel.add(sessionCombo);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Creates the assignment panel with presenter and evaluator lists.
     * @return the assignment panel
     */
    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Presenter assignment section
        JPanel presenterPanel = createPresenterAssignmentPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(presenterPanel, gbc);
        
        // Evaluator assignment section
        JPanel evaluatorPanel = createEvaluatorAssignmentPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(evaluatorPanel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the presenter assignment panel.
     * @return the presenter assignment panel
     */
    private JPanel createPresenterAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Presenter Assignments"));
        
        JPanel listsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Available presenters
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(new JLabel("Available Presenters:"), BorderLayout.NORTH);
        availablePresentersModel = new DefaultListModel<>();
        availablePresentersList = new JList<>(availablePresentersModel);
        availablePresentersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane availableScroll = new JScrollPane(availablePresentersList);
        availableScroll.setPreferredSize(new Dimension(180, 150));
        availablePanel.add(availableScroll, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        listsPanel.add(availablePanel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 5, 5, 5);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        
        assignPresenterButton = new JButton(">>");
        assignPresenterButton.setToolTipText("Assign presenter to session");
        assignPresenterButton.addActionListener(e -> assignPresenter());
        btnGbc.gridy = 0;
        buttonPanel.add(assignPresenterButton, btnGbc);
        
        unassignPresenterButton = new JButton("<<");
        unassignPresenterButton.setToolTipText("Remove presenter from session");
        unassignPresenterButton.addActionListener(e -> unassignPresenter());
        btnGbc.gridy = 1;
        buttonPanel.add(unassignPresenterButton, btnGbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0;
        listsPanel.add(buttonPanel, gbc);
        
        // Assigned presenters
        JPanel assignedPanel = new JPanel(new BorderLayout());
        assignedPanel.add(new JLabel("Assigned Presenters:"), BorderLayout.NORTH);
        assignedPresentersModel = new DefaultListModel<>();
        assignedPresentersList = new JList<>(assignedPresentersModel);
        assignedPresentersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane assignedScroll = new JScrollPane(assignedPresentersList);
        assignedScroll.setPreferredSize(new Dimension(180, 150));
        assignedPanel.add(assignedScroll, BorderLayout.CENTER);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        listsPanel.add(assignedPanel, gbc);
        
        panel.add(listsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the evaluator assignment panel.
     * @return the evaluator assignment panel
     */
    private JPanel createEvaluatorAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Evaluator Assignments"));
        
        JPanel listsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Available evaluators
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(new JLabel("Available Evaluators:"), BorderLayout.NORTH);
        availableEvaluatorsModel = new DefaultListModel<>();
        availableEvaluatorsList = new JList<>(availableEvaluatorsModel);
        availableEvaluatorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane availableScroll = new JScrollPane(availableEvaluatorsList);
        availableScroll.setPreferredSize(new Dimension(180, 150));
        availablePanel.add(availableScroll, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        listsPanel.add(availablePanel, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(5, 5, 5, 5);
        btnGbc.fill = GridBagConstraints.HORIZONTAL;
        
        assignEvaluatorButton = new JButton(">>");
        assignEvaluatorButton.setToolTipText("Assign evaluator to session");
        assignEvaluatorButton.addActionListener(e -> assignEvaluator());
        btnGbc.gridy = 0;
        buttonPanel.add(assignEvaluatorButton, btnGbc);
        
        unassignEvaluatorButton = new JButton("<<");
        unassignEvaluatorButton.setToolTipText("Remove evaluator from session");
        unassignEvaluatorButton.addActionListener(e -> unassignEvaluator());
        btnGbc.gridy = 1;
        buttonPanel.add(unassignEvaluatorButton, btnGbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0;
        listsPanel.add(buttonPanel, gbc);
        
        // Assigned evaluators
        JPanel assignedPanel = new JPanel(new BorderLayout());
        assignedPanel.add(new JLabel("Assigned Evaluators:"), BorderLayout.NORTH);
        assignedEvaluatorsModel = new DefaultListModel<>();
        assignedEvaluatorsList = new JList<>(assignedEvaluatorsModel);
        assignedEvaluatorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane assignedScroll = new JScrollPane(assignedEvaluatorsList);
        assignedScroll.setPreferredSize(new Dimension(180, 150));
        assignedPanel.add(assignedScroll, BorderLayout.CENTER);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        listsPanel.add(assignedPanel, gbc);
        
        panel.add(listsPanel, BorderLayout.CENTER);
        
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
     * Handles session selection.
     */
    private void onSessionSelected() {
        SessionItem selected = (SessionItem) sessionCombo.getSelectedItem();
        if (selected != null) {
            updateAssignmentLists(selected.session);
        }
    }
    
    /**
     * Updates the assignment lists for the selected session.
     * @param session the selected session
     */
    private void updateAssignmentLists(Session session) {
        // Clear all lists
        availablePresentersModel.clear();
        assignedPresentersModel.clear();
        availableEvaluatorsModel.clear();
        assignedEvaluatorsModel.clear();
        
        if (session == null) return;
        
        // Populate presenter lists - only show students with matching presentation type
        List<Student> allStudents = userService.getAllStudents();
        for (Student student : allStudents) {
            // Only include students who are registered (have presenter ID)
            // AND whose presentation type matches the session type
            if (student.getPresenterId() != null && student.getPresentationType() != null) {
                // Check if student's presentation type matches session type
                if (student.getPresentationType() == session.getSessionType()) {
                    PresenterItem item = new PresenterItem(student);
                    if (session.getPresenterIds().contains(student.getPresenterId())) {
                        assignedPresentersModel.addElement(item);
                    } else {
                        availablePresentersModel.addElement(item);
                    }
                }
            }
        }
        
        // Populate evaluator lists
        List<Evaluator> allEvaluators = userService.getAllEvaluators();
        for (Evaluator evaluator : allEvaluators) {
            EvaluatorItem item = new EvaluatorItem(evaluator);
            if (session.getEvaluatorIds().contains(evaluator.getEvaluatorId())) {
                assignedEvaluatorsModel.addElement(item);
            } else {
                availableEvaluatorsModel.addElement(item);
            }
        }
    }
    
    /**
     * Assigns the selected presenter to the session.
     */
    private void assignPresenter() {
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        PresenterItem presenterItem = availablePresentersList.getSelectedValue();
        
        if (sessionItem == null) {
            ErrorHandler.showError(this, "Please select a session");
            return;
        }
        if (presenterItem == null) {
            ErrorHandler.showError(this, "Please select a presenter to assign");
            return;
        }
        
        // Check if session already has a presenter assigned
        if (!sessionItem.session.getPresenterIds().isEmpty()) {
            ErrorHandler.showError(this, "This session already has a presenter assigned. Please unassign the current presenter first.");
            return;
        }
        
        // Validate presentation type matches session type
        if (presenterItem.student.getPresentationType() != sessionItem.session.getSessionType()) {
            ErrorHandler.showError(this, "Cannot assign " + presenterItem.student.getPresentationType() + 
                " presenter to " + sessionItem.session.getSessionType() + " session");
            return;
        }
        
        try {
            sessionService.assignPresenter(sessionItem.session.getSessionId(), 
                                          presenterItem.student.getPresenterId());
            app.autoSave();
            updateAssignmentLists(sessionItem.session);
        } catch (IllegalArgumentException e) {
            ErrorHandler.showWarning(this, "Conflict: " + e.getMessage());
        }
    }
    
    /**
     * Unassigns the selected presenter from the session.
     */
    private void unassignPresenter() {
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        PresenterItem presenterItem = assignedPresentersList.getSelectedValue();
        
        if (sessionItem == null) {
            ErrorHandler.showError(this, "Please select a session");
            return;
        }
        if (presenterItem == null) {
            ErrorHandler.showError(this, "Please select a presenter to unassign");
            return;
        }
        
        sessionService.removePresenter(sessionItem.session.getSessionId(), 
                                       presenterItem.student.getPresenterId());
        app.autoSave();
        updateAssignmentLists(sessionItem.session);
    }
    
    /**
     * Assigns the selected evaluator to the session.
     */
    private void assignEvaluator() {
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        EvaluatorItem evaluatorItem = availableEvaluatorsList.getSelectedValue();
        
        if (sessionItem == null) {
            ErrorHandler.showError(this, "Please select a session");
            return;
        }
        if (evaluatorItem == null) {
            ErrorHandler.showError(this, "Please select an evaluator to assign");
            return;
        }
        
        // Check if session already has an evaluator assigned
        if (!sessionItem.session.getEvaluatorIds().isEmpty()) {
            ErrorHandler.showError(this, "This session already has an evaluator assigned. Please unassign the current evaluator first.");
            return;
        }
        
        try {
            sessionService.assignEvaluator(sessionItem.session.getSessionId(), 
                                          evaluatorItem.evaluator.getEvaluatorId());
            app.autoSave();
            updateAssignmentLists(sessionItem.session);
        } catch (IllegalArgumentException e) {
            ErrorHandler.showWarning(this, "Conflict: " + e.getMessage());
        }
    }
    
    /**
     * Unassigns the selected evaluator from the session.
     */
    private void unassignEvaluator() {
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        EvaluatorItem evaluatorItem = assignedEvaluatorsList.getSelectedValue();
        
        if (sessionItem == null) {
            ErrorHandler.showError(this, "Please select a session");
            return;
        }
        if (evaluatorItem == null) {
            ErrorHandler.showError(this, "Please select an evaluator to unassign");
            return;
        }
        
        sessionService.removeEvaluator(sessionItem.session.getSessionId(), 
                                       evaluatorItem.evaluator.getEvaluatorId());
        app.autoSave();
        updateAssignmentLists(sessionItem.session);
    }
    
    /**
     * Refreshes the panel data.
     */
    public void refresh() {
        // Save current selection
        SessionItem currentSelection = (SessionItem) sessionCombo.getSelectedItem();
        String currentSessionId = currentSelection != null ? currentSelection.session.getSessionId() : null;
        
        // Reload sessions
        sessionCombo.removeAllItems();
        List<Session> sessions = sessionService.getAllSessions();
        SessionItem toSelect = null;
        
        for (Session session : sessions) {
            SessionItem item = new SessionItem(session);
            sessionCombo.addItem(item);
            if (currentSessionId != null && session.getSessionId().equals(currentSessionId)) {
                toSelect = item;
            }
        }
        
        // Restore selection
        if (toSelect != null) {
            sessionCombo.setSelectedItem(toSelect);
        } else if (sessionCombo.getItemCount() > 0) {
            sessionCombo.setSelectedIndex(0);
        }
    }
    
    /**
     * Navigates back to the coordinator dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.COORDINATOR_DASHBOARD);
    }
    
    // Inner classes for list items
    
    /**
     * Wrapper class for Session items in combo box.
     */
    private static class SessionItem {
        final Session session;
        
        SessionItem(Session session) {
            this.session = session;
        }
        
        @Override
        public String toString() {
            return session.getVenue() + " - " + session.getDate().format(DATE_FORMAT) 
                   + " (" + session.getSessionType() + ")";
        }
    }
    
    /**
     * Wrapper class for Presenter items in lists.
     */
    private static class PresenterItem {
        final Student student;
        
        PresenterItem(Student student) {
            this.student = student;
        }
        
        @Override
        public String toString() {
            return student.getUsername();
        }
    }
    
    /**
     * Wrapper class for Evaluator items in lists.
     */
    private static class EvaluatorItem {
        final Evaluator evaluator;
        
        EvaluatorItem(Evaluator evaluator) {
            this.evaluator = evaluator;
        }
        
        @Override
        public String toString() {
            return evaluator.getUsername();
        }
    }

    // Getter methods for testing
    
    public JComboBox<SessionItem> getSessionCombo() {
        return sessionCombo;
    }
    
    public JList<PresenterItem> getAvailablePresentersList() {
        return availablePresentersList;
    }
    
    public JList<PresenterItem> getAssignedPresentersList() {
        return assignedPresentersList;
    }
    
    public JList<EvaluatorItem> getAvailableEvaluatorsList() {
        return availableEvaluatorsList;
    }
    
    public JList<EvaluatorItem> getAssignedEvaluatorsList() {
        return assignedEvaluatorsList;
    }
    
    public JButton getAssignPresenterButton() {
        return assignPresenterButton;
    }
    
    public JButton getUnassignPresenterButton() {
        return unassignPresenterButton;
    }
    
    public JButton getAssignEvaluatorButton() {
        return assignEvaluatorButton;
    }
    
    public JButton getUnassignEvaluatorButton() {
        return unassignEvaluatorButton;
    }
    
    public JButton getBackButton() {
        return backButton;
    }
}
