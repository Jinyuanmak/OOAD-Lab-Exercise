package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.service.UserService;

/**
 * Panel for students to view their assigned session details.
 * Shows venue/meeting link, date, and evaluator information.
 */
public class MySessionPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
    
    private final SeminarApp app;
    private final SessionService sessionService;
    private final UserService userService;
    
    private JLabel sessionTypeLabel;
    private JLabel dateLabel;
    private JLabel venueLabel;
    private JTextArea meetingLinkArea;
    private JLabel evaluatorLabel;
    private JLabel venueTitleLabel;
    private JLabel meetingLinkTitleLabel;
    private JPanel meetingLinkScrollPane;
    private String currentMeetingLink;

    /**
     * Creates a new MySessionPanel.
     * @param app the main application frame
     * @param sessionService the session service
     * @param userService the user service
     */
    public MySessionPanel(SeminarApp app, SessionService sessionService, UserService userService) {
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
        
        // Create content panel
        JPanel contentPanel = createContentPanel();
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
        
        JLabel titleLabel = new JLabel("My Session Details");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the content panel with session details.
     * @return the content panel
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Session Type
        JLabel sessionTypeTitle = new JLabel("Session Type:");
        sessionTypeTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(sessionTypeTitle, gbc);
        
        sessionTypeLabel = new JLabel("Not assigned");
        sessionTypeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(sessionTypeLabel, gbc);
        
        row++;
        
        // Date
        JLabel dateTitle = new JLabel("Date:");
        dateTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        panel.add(dateTitle, gbc);
        
        dateLabel = new JLabel("Not assigned");
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(dateLabel, gbc);
        
        row++;
        
        // Venue (for POSTER sessions)
        venueTitleLabel = new JLabel("Venue:");
        venueTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panel.add(venueTitleLabel, gbc);
        
        venueLabel = new JLabel("Not assigned");
        venueLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(venueLabel, gbc);
        
        row++;
        
        // Meeting Link (for ORAL sessions)
        meetingLinkTitleLabel = new JLabel("Meeting Link:");
        meetingLinkTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panel.add(meetingLinkTitleLabel, gbc);
        
        row++;
        
        // Create a panel to hold the meeting link and join button
        JPanel meetingLinkPanel = new JPanel(new BorderLayout(10, 5));
        
        meetingLinkArea = new JTextArea(2, 40);
        meetingLinkArea.setEditable(false);
        meetingLinkArea.setLineWrap(true);
        meetingLinkArea.setWrapStyleWord(true);
        meetingLinkArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        meetingLinkArea.setBackground(java.awt.Color.LIGHT_GRAY);
        JScrollPane textScrollPane = new JScrollPane(meetingLinkArea);
        textScrollPane.setPreferredSize(new Dimension(400, 50));
        meetingLinkPanel.add(textScrollPane, BorderLayout.CENTER);
        
        // Add "Join Meeting" button
        JButton joinMeetingButton = new JButton("Join Meeting");
        joinMeetingButton.setPreferredSize(new Dimension(120, 30));
        joinMeetingButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        joinMeetingButton.addActionListener(e -> openMeetingLink());
        meetingLinkPanel.add(joinMeetingButton, BorderLayout.SOUTH);
        
        meetingLinkScrollPane = meetingLinkPanel;
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        panel.add(meetingLinkScrollPane, gbc);
        
        row++;
        
        // Evaluator
        JLabel evaluatorTitle = new JLabel("Evaluator:");
        evaluatorTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panel.add(evaluatorTitle, gbc);
        
        evaluatorLabel = new JLabel("Not assigned");
        evaluatorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(evaluatorLabel, gbc);
        
        // Initially hide venue and meeting link fields
        venueTitleLabel.setVisible(false);
        venueLabel.setVisible(false);
        meetingLinkTitleLabel.setVisible(false);
        meetingLinkScrollPane.setVisible(false);
        
        return panel;
    }
    
    /**
     * Creates the button panel.
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        return panel;
    }
    
    /**
     * Refreshes the panel with current session data.
     */
    public void refresh() {
        User currentUser = app.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            return;
        }
        
        Student student = (Student) currentUser;
        String presenterId = student.getPresenterId();
        
        if (presenterId == null || presenterId.isEmpty()) {
            showNoSessionMessage();
            return;
        }
        
        // Find the session this student is assigned to
        Session assignedSession = null;
        for (Session session : sessionService.getAllSessions()) {
            if (session.getPresenterIds().contains(presenterId)) {
                assignedSession = session;
                break;
            }
        }
        
        if (assignedSession == null) {
            showNoSessionMessage();
            return;
        }
        
        // Display session details
        displaySessionDetails(assignedSession);
    }
    
    /**
     * Shows a message when no session is assigned.
     */
    private void showNoSessionMessage() {
        sessionTypeLabel.setText("Not assigned");
        dateLabel.setText("You are not assigned to any session yet");
        venueLabel.setText("N/A");
        meetingLinkArea.setText("");
        evaluatorLabel.setText("N/A");
        venueTitleLabel.setVisible(false);
        venueLabel.setVisible(false);
        meetingLinkTitleLabel.setVisible(false);
        meetingLinkScrollPane.setVisible(false);
    }
    
    /**
     * Displays the session details.
     * @param session the session to display
     */
    private void displaySessionDetails(Session session) {
        // Session type
        sessionTypeLabel.setText(session.getSessionType().toString());
        
        // Date
        dateLabel.setText(session.getDate().format(DATE_FORMAT));
        
        // Venue or Meeting Link based on session type
        if (session.getSessionType() == PresentationType.POSTER) {
            venueLabel.setText(session.getVenue());
            venueTitleLabel.setVisible(true);
            venueLabel.setVisible(true);
            meetingLinkTitleLabel.setVisible(false);
            meetingLinkScrollPane.setVisible(false);
        } else if (session.getSessionType() == PresentationType.ORAL) {
            String meetingLink = session.getMeetingLink();
            if (meetingLink != null && !meetingLink.isEmpty()) {
                meetingLinkArea.setText(meetingLink);
                currentMeetingLink = meetingLink;
            } else {
                meetingLinkArea.setText("Meeting link not available");
                currentMeetingLink = null;
            }
            venueTitleLabel.setVisible(false);
            venueLabel.setVisible(false);
            meetingLinkTitleLabel.setVisible(true);
            meetingLinkScrollPane.setVisible(true);
        }
        
        // Evaluator
        if (!session.getEvaluatorIds().isEmpty()) {
            String evaluatorId = session.getEvaluatorIds().get(0);
            Evaluator evaluator = findEvaluatorByEvaluatorId(evaluatorId);
            if (evaluator != null) {
                evaluatorLabel.setText(evaluator.getUsername());
            } else {
                evaluatorLabel.setText("Not assigned");
            }
        } else {
            evaluatorLabel.setText("Not assigned");
        }
    }
    
    /**
     * Opens the meeting link in the default browser.
     */
    private void openMeetingLink() {
        if (currentMeetingLink == null || currentMeetingLink.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "No meeting link available",
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if Desktop is supported
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    // Open the link in the default browser
                    desktop.browse(new java.net.URI(currentMeetingLink));
                    
                    // Show confirmation message
                    javax.swing.JOptionPane.showMessageDialog(this,
                        "Opening Teams meeting in your browser...\n" +
                        "If Teams doesn't open automatically, please click 'Open Microsoft Teams' in your browser.",
                        "Joining Meeting",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showBrowserNotSupportedMessage();
                }
            } else {
                showBrowserNotSupportedMessage();
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Failed to open meeting link: " + ex.getMessage() + "\n\n" +
                "Please copy the link and paste it in your browser.",
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows a message when browser is not supported.
     */
    private void showBrowserNotSupportedMessage() {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Cannot open browser automatically.\n" +
            "Please copy the meeting link and paste it in your browser.",
            "Browser Not Supported",
            javax.swing.JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Finds an evaluator by their evaluator ID.
     * @param evaluatorId the evaluator ID
     * @return the evaluator if found, null otherwise
     */
    private Evaluator findEvaluatorByEvaluatorId(String evaluatorId) {
        for (Evaluator evaluator : userService.getAllEvaluators()) {
            if (evaluatorId.equals(evaluator.getEvaluatorId())) {
                return evaluator;
            }
        }
        return null;
    }
    
    /**
     * Navigates back to the student dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.STUDENT_DASHBOARD);
    }
}
