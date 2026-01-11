package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.fci.seminar.model.User;
import com.fci.seminar.service.AwardService;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.PosterBoardService;
import com.fci.seminar.service.ReportService;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Main application frame for the Seminar Management System.
 * Uses CardLayout for panel switching between different views.
 */
public class SeminarApp extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private static final String DATA_FILE = "seminar_data.ser";
    private static final String DEFAULT_TITLE = "FCI Seminar Management System";
    
    // Panel names for CardLayout
    public static final String LOGIN_PANEL = "login";
    public static final String STUDENT_DASHBOARD = "studentDashboard";
    public static final String STUDENT_REGISTRATION = "studentRegistration";
    public static final String FILE_UPLOAD = "fileUpload";
    public static final String COORDINATOR_DASHBOARD = "coordinatorDashboard";
    public static final String SESSION_MANAGEMENT = "sessionManagement";
    public static final String ASSIGNMENT_PANEL = "assignmentPanel";
    public static final String POSTER_MANAGEMENT = "posterManagement";
    public static final String AWARD_PANEL = "awardPanel";
    public static final String REPORT_PANEL = "reportPanel";
    public static final String EVALUATOR_DASHBOARD = "evaluatorDashboard";
    public static final String EVALUATION_FORM = "evaluationForm";
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DataStore dataStore;
    private User currentUser;
    private JMenuBar menuBar;
    private UserService userService;
    private SessionService sessionService;
    private EvaluationService evaluationService;
    private PosterBoardService posterBoardService;
    private AwardService awardService;
    private ReportService reportService;
    private LoginPanel loginPanel;
    private StudentRegistrationPanel studentRegistrationPanel;
    private StudentDashboard studentDashboard;
    private EvaluatorDashboard evaluatorDashboard;
    private FileUploadPanel fileUploadPanel;

    /**
     * Creates the main application frame.
     */
    public SeminarApp() {
        super(DEFAULT_TITLE);
        initializeDataStore();
        initializeServices();
        initializeUI();
        initializePanels();
        setupWindowListener();
        
        // Sample data is now loaded via schema.sql
        // Users should run schema.sql manually in phpMyAdmin/MySQL
    }
    
    /**
     * Initializes the DataStore by loading existing data or creating new.
     */
    private void initializeDataStore() {
        dataStore = DataStore.load(DATA_FILE);
    }
    
    /**
     * Initializes service layer components.
     */
    private void initializeServices() {
        userService = new UserService(dataStore);
        sessionService = new SessionService(dataStore);
        evaluationService = new EvaluationService(dataStore);
        posterBoardService = new PosterBoardService(dataStore);
        awardService = new AwardService(dataStore, evaluationService, userService);
        reportService = new ReportService(dataStore);
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Set up CardLayout for panel switching
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        
        // Create and set up menu bar
        createMenuBar();
        setJMenuBar(menuBar);
        
        // Initially show login panel
        updateMenuVisibility();
    }
    
    /**
     * Initializes and registers all UI panels.
     */
    private void initializePanels() {
        // Create and register login panel
        loginPanel = new LoginPanel(this, userService);
        addPanel(loginPanel, LOGIN_PANEL);
        
        // Create and register student panels
        studentDashboard = new StudentDashboard(this);
        addPanel(studentDashboard, STUDENT_DASHBOARD);
        
        studentRegistrationPanel = new StudentRegistrationPanel(this, userService);
        addPanel(studentRegistrationPanel, STUDENT_REGISTRATION);
        
        fileUploadPanel = new FileUploadPanel(this);
        addPanel(fileUploadPanel, FILE_UPLOAD);
        
        // Create and register coordinator panels
        CoordinatorDashboard coordinatorDashboard = new CoordinatorDashboard(this);
        addPanel(coordinatorDashboard, COORDINATOR_DASHBOARD);
        
        SessionManagementPanel sessionManagement = new SessionManagementPanel(this, sessionService);
        addPanel(sessionManagement, SESSION_MANAGEMENT);
        
        AssignmentPanel assignmentPanel = new AssignmentPanel(this, sessionService, userService);
        addPanel(assignmentPanel, ASSIGNMENT_PANEL);
        
        PosterManagementPanel posterManagement = new PosterManagementPanel(this, posterBoardService, sessionService, userService);
        addPanel(posterManagement, POSTER_MANAGEMENT);
        
        AwardPanel awardPanel = new AwardPanel(this, awardService, userService);
        addPanel(awardPanel, AWARD_PANEL);
        
        ReportPanel reportPanel = new ReportPanel(this, reportService);
        addPanel(reportPanel, REPORT_PANEL);
        
        // Create and register evaluator panels
        evaluatorDashboard = new EvaluatorDashboard(this, sessionService, userService);
        addPanel(evaluatorDashboard, EVALUATOR_DASHBOARD);
        
        EvaluationFormPanel evaluationForm = new EvaluationFormPanel(this, evaluationService, userService);
        addPanel(evaluationForm, EVALUATION_FORM);
        
        // Show login panel initially
        showPanel(LOGIN_PANEL);
    }
    
    /**
     * Creates the menu bar with navigation options.
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Data");
        saveItem.addActionListener(e -> saveData());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Navigation menu
        JMenu navMenu = new JMenu("Navigation");
        JMenuItem homeItem = new JMenuItem("Home");
        homeItem.addActionListener(e -> navigateToHome());
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        navMenu.add(homeItem);
        navMenu.addSeparator();
        navMenu.add(logoutItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(navMenu);
        menuBar.add(helpMenu);
    }

    /**
     * Sets up window listener for save on close.
     */
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });
    }
    
    /**
     * Shows a panel by name using CardLayout.
     * Calls refresh() on panels that need to update their data.
     * @param panelName the name of the panel to show
     */
    public void showPanel(String panelName) {
        // Refresh panels that need current user data
        if (STUDENT_REGISTRATION.equals(panelName) && studentRegistrationPanel != null) {
            studentRegistrationPanel.refresh();
        } else if (STUDENT_DASHBOARD.equals(panelName) && studentDashboard != null) {
            studentDashboard.refresh();
        } else if (EVALUATOR_DASHBOARD.equals(panelName) && evaluatorDashboard != null) {
            evaluatorDashboard.refresh();
        } else if (FILE_UPLOAD.equals(panelName) && fileUploadPanel != null) {
            fileUploadPanel.refresh();
        }
        cardLayout.show(mainPanel, panelName);
    }
    
    /**
     * Adds a panel to the CardLayout with the specified name.
     * @param panel the panel to add
     * @param name the name to identify the panel
     */
    public void addPanel(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }
    
    /**
     * Sets the current logged-in user.
     * @param user the user to set as current
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateMenuVisibility();
        if (user != null) {
            setTitle(DEFAULT_TITLE + " - " + user.getUsername() + " (" + user.getRole() + ")");
        } else {
            setTitle(DEFAULT_TITLE);
        }
    }
    
    /**
     * Gets the current logged-in user.
     * @return the current user, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the DataStore instance.
     * @return the DataStore
     */
    public DataStore getDataStore() {
        return dataStore;
    }
    
    /**
     * Gets the UserService instance.
     * @return the UserService
     */
    public UserService getUserService() {
        return userService;
    }
    
    /**
     * Gets the SessionService instance.
     * @return the SessionService
     */
    public SessionService getSessionService() {
        return sessionService;
    }
    
    /**
     * Gets the EvaluationService instance.
     * @return the EvaluationService
     */
    public EvaluationService getEvaluationService() {
        return evaluationService;
    }
    
    /**
     * Gets the PosterBoardService instance.
     * @return the PosterBoardService
     */
    public PosterBoardService getPosterBoardService() {
        return posterBoardService;
    }
    
    /**
     * Gets the AwardService instance.
     * @return the AwardService
     */
    public AwardService getAwardService() {
        return awardService;
    }
    
    /**
     * Gets the ReportService instance.
     * @return the ReportService
     */
    public ReportService getReportService() {
        return reportService;
    }
    
    /**
     * Updates menu visibility based on login state.
     */
    private void updateMenuVisibility() {
        boolean loggedIn = currentUser != null;
        // Navigation menu items depend on login state
        if (menuBar != null && menuBar.getMenuCount() > 1) {
            menuBar.getMenu(1).setEnabled(loggedIn); // Navigation menu
        }
    }
    
    /**
     * Navigates to the appropriate home dashboard based on user role.
     */
    private void navigateToHome() {
        if (currentUser == null) {
            showPanel(LOGIN_PANEL);
            return;
        }
        
        switch (currentUser.getRole()) {
            case STUDENT:
                showPanel(STUDENT_DASHBOARD);
                break;
            case EVALUATOR:
                showPanel(EVALUATOR_DASHBOARD);
                break;
            case COORDINATOR:
                showPanel(COORDINATOR_DASHBOARD);
                break;
            default:
                showPanel(LOGIN_PANEL);
        }
    }

    /**
     * Logs out the current user and returns to login screen.
     */
    private void logout() {
        setCurrentUser(null);
        if (loginPanel != null) {
            loginPanel.reset();
        }
        showPanel(LOGIN_PANEL);
    }
    
    /**
     * Saves data to file.
     */
    private void saveData() {
        try {
            dataStore.save(DATA_FILE);
        } catch (Exception e) {
            ErrorHandler.showError(this, "Failed to save data: " + e.getMessage());
        }
    }
    
    /**
     * Triggers auto-save when data changes occur.
     * This method can be called by panels after modifying data.
     */
    public void autoSave() {
        try {
            dataStore.save(DATA_FILE);
        } catch (Exception e) {
            // Silent auto-save failure - don't interrupt user workflow
            System.err.println("Auto-save failed: " + e.getMessage());
        }
    }
    
    /**
     * Exits the application after saving data.
     */
    private void exitApplication() {
        saveData();
        dispose();
        System.exit(0);
    }
    
    /**
     * Shows the about dialog.
     */
    private void showAbout() {
        javax.swing.JOptionPane.showMessageDialog(this,
            "FCI Seminar Management System\n" +
            "Version 1.0\n\n" +
            "Faculty of Computing and Informatics\n" +
            "Postgraduate Academic Research Seminar",
            "About",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Loads sample data with user confirmation.
     * @deprecated Sample data is now loaded via schema.sql
     * Users should run database/schema.sql manually in phpMyAdmin/MySQL
     */
    @Deprecated
    private void loadSampleDataWithConfirmation() {
        // This method is no longer used
        // Sample data is now included in database/schema.sql
        // Run schema.sql in phpMyAdmin to set up the database with sample users
    }
    
    /**
     * Gets the main panel containing all cards.
     * @return the main panel
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    /**
     * Gets the CardLayout manager.
     * @return the CardLayout
     */
    public CardLayout getCardLayout() {
        return cardLayout;
    }
    
    /**
     * Main entry point for the application.
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            SeminarApp app = new SeminarApp();
            app.setVisible(true);
        });
    }
}
