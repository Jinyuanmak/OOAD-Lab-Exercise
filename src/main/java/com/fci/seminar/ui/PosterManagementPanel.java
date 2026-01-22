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
    private JComboBox<PresenterItem> presenterCombo;
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
        
        presenterCombo = new JComboBox<>();
        gbc.gridy = row++;
        panel.add(presenterCombo, gbc);
        
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
     * Handles session selection and updates presenter list.
     */
    private void onSessionSelected() {
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        
        // Clear and update presenter combo based on selected session
        presenterCombo.removeAllItems();
        
        if (sessionItem != null) {
            Session session = sessionItem.session;
            
            // Only show presenters who are assigned to this session
            for (String presenterId : session.getPresenterIds()) {
                Student student = userService.getStudentByPresenterId(presenterId);
                if (student != null && student.getPresentationType() == PresentationType.POSTER) {
                    presenterCombo.addItem(new PresenterItem(student));
                }
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
        // Use JFrame instead of JDialog to get maximize/minimize buttons
        javax.swing.JFrame frame = new javax.swing.JFrame("Presentation Materials - " + student.getUsername());
        frame.setLayout(new BorderLayout(10, 10));
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        
        // Set icon to match parent window if available
        java.awt.Frame parentFrame = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null && parentFrame.getIconImage() != null) {
            frame.setIconImage(parentFrame.getIconImage());
        }
        
        // Create info panel with student details
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Student Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        JLabel nameLabel = new JLabel(student.getUsername());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoPanel.add(nameLabel, gbc);
        
        // Student ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        JLabel idLabel = new JLabel(student.getStudentId());
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoPanel.add(idLabel, gbc);
        
        // Research Title
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Research Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel titleLabel = new JLabel(student.getResearchTitle() != null ? student.getResearchTitle() : "N/A");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoPanel.add(titleLabel, gbc);
        
        frame.add(infoPanel, BorderLayout.NORTH);
        
        // Create materials viewer panel
        JPanel materialsPanel = new JPanel(new BorderLayout());
        materialsPanel.setBorder(BorderFactory.createTitledBorder("Presentation Materials"));
        
        // Check file type and display accordingly
        String filePath = student.getFilePath();
        java.io.File file = new java.io.File(filePath);
        
        if (!file.exists()) {
            JLabel errorLabel = new JLabel("File not found: " + filePath, SwingConstants.CENTER);
            errorLabel.setForeground(java.awt.Color.RED);
            materialsPanel.add(errorLabel, BorderLayout.CENTER);
        } else {
            String fileName = file.getName().toLowerCase();
            
            if (fileName.endsWith(".pdf")) {
                // For PDF files, render pages directly
                try {
                    org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(file);
                    
                    JPanel pdfPanel = new JPanel(new BorderLayout());
                    
                    // Create a panel to hold PDF pages
                    JPanel pagesPanel = new JPanel();
                    pagesPanel.setLayout(new javax.swing.BoxLayout(pagesPanel, javax.swing.BoxLayout.Y_AXIS));
                    pagesPanel.setBackground(java.awt.Color.GRAY);
                    
                    // Render first 5 pages (to avoid performance issues with large PDFs)
                    int pagesToRender = Math.min(5, document.getNumberOfPages());
                    org.apache.pdfbox.rendering.PDFRenderer pdfRenderer = new org.apache.pdfbox.rendering.PDFRenderer(document);
                    
                    for (int i = 0; i < pagesToRender; i++) {
                        // Render at 300 DPI for maximum quality, then scale down to 150 DPI size
                        // This supersampling technique produces much sharper results
                        java.awt.image.BufferedImage highResImage = pdfRenderer.renderImageWithDPI(i, 300, org.apache.pdfbox.rendering.ImageType.RGB);
                        
                        // Calculate target size (50% of 300 DPI = 150 DPI size)
                        int targetWidth = highResImage.getWidth() / 2;
                        int targetHeight = highResImage.getHeight() / 2;
                        
                        // Create high-quality scaled image using multi-step scaling
                        java.awt.image.BufferedImage scaledImage = new java.awt.image.BufferedImage(
                            targetWidth, targetHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
                        
                        java.awt.Graphics2D g2d = scaledImage.createGraphics();
                        
                        // Apply highest quality rendering hints
                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                            java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, 
                            java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, 
                            java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, 
                            java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                        g2d.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, 
                            java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        
                        // Draw the high-res image scaled down
                        g2d.drawImage(highResImage, 0, 0, targetWidth, targetHeight, null);
                        g2d.dispose();
                        g2d.dispose();
                        
                        // Use the high-quality scaled image
                        JLabel pageLabel = new JLabel(new javax.swing.ImageIcon(scaledImage));
                        pageLabel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK, 2));
                        pageLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                        pagesPanel.add(pageLabel);
                        
                        pagesPanel.add(javax.swing.Box.createVerticalStrut(10)); // Space between pages
                    }
                    
                    // Add info label if there are more pages
                    if (document.getNumberOfPages() > pagesToRender) {
                        JLabel moreLabel = new JLabel("Showing " + pagesToRender + " of " + document.getNumberOfPages() + " pages");
                        moreLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                        moreLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
                        pagesPanel.add(moreLabel);
                    }
                    
                    JScrollPane scrollPane = new JScrollPane(pagesPanel);
                    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                    pdfPanel.add(scrollPane, BorderLayout.CENTER);
                    
                    // Add page info at top
                    JLabel infoLabel = new JLabel("PDF Document - " + document.getNumberOfPages() + " page(s) | Tip: Maximize window for better viewing", SwingConstants.CENTER);
                    infoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                    infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    pdfPanel.add(infoLabel, BorderLayout.NORTH);
                    
                    materialsPanel.add(pdfPanel, BorderLayout.CENTER);
                    
                    document.close();
                    
                } catch (Exception ex) {
                    JLabel errorLabel = new JLabel("Failed to load PDF: " + ex.getMessage(), SwingConstants.CENTER);
                    errorLabel.setForeground(java.awt.Color.RED);
                    materialsPanel.add(errorLabel, BorderLayout.CENTER);
                }
                
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                       fileName.endsWith(".png") || fileName.endsWith(".gif")) {
                // For image files, display the image
                try {
                    javax.swing.ImageIcon imageIcon = new javax.swing.ImageIcon(filePath);
                    // Scale image to fit
                    java.awt.Image image = imageIcon.getImage();
                    java.awt.Image scaledImage = image.getScaledInstance(650, 350, java.awt.Image.SCALE_SMOOTH);
                    JLabel imageLabel = new JLabel(new javax.swing.ImageIcon(scaledImage));
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    
                    JScrollPane scrollPane = new JScrollPane(imageLabel);
                    materialsPanel.add(scrollPane, BorderLayout.CENTER);
                } catch (Exception ex) {
                    JLabel errorLabel = new JLabel("Failed to load image: " + ex.getMessage(), SwingConstants.CENTER);
                    errorLabel.setForeground(java.awt.Color.RED);
                    materialsPanel.add(errorLabel, BorderLayout.CENTER);
                }
                
            } else if (fileName.endsWith(".txt")) {
                // For text files, display content
                try {
                    StringBuilder content = new StringBuilder();
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();
                    
                    javax.swing.JTextArea textArea = new javax.swing.JTextArea(content.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    materialsPanel.add(scrollPane, BorderLayout.CENTER);
                } catch (Exception ex) {
                    JLabel errorLabel = new JLabel("Failed to load text file: " + ex.getMessage(), SwingConstants.CENTER);
                    errorLabel.setForeground(java.awt.Color.RED);
                    materialsPanel.add(errorLabel, BorderLayout.CENTER);
                }
                
            } else {
                // For other file types, show file info and open button
                JPanel filePanel = new JPanel(new BorderLayout(10, 10));
                filePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                JLabel fileLabel = new JLabel("<html><center>File: " + file.getName() + 
                    "<br>Size: " + (file.length() / 1024) + " KB</center></html>", SwingConstants.CENTER);
                fileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                filePanel.add(fileLabel, BorderLayout.CENTER);
                
                JButton openButton = new JButton("Open in External Application");
                openButton.addActionListener(e -> {
                    try {
                        java.awt.Desktop.getDesktop().open(file);
                    } catch (Exception ex) {
                        ErrorHandler.showError(frame, "Failed to open file: " + ex.getMessage());
                    }
                });
                filePanel.add(openButton, BorderLayout.SOUTH);
                
                materialsPanel.add(filePanel, BorderLayout.CENTER);
            }
        }
        
        frame.add(materialsPanel, BorderLayout.CENTER);
        
        // Add close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(closeButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }
    
    /**
     * Assigns a board to a presenter.
     */
    private void assignBoard() {
        String boardId = (String) boardIdCombo.getSelectedItem();
        PresenterItem presenterItem = (PresenterItem) presenterCombo.getSelectedItem();
        SessionItem sessionItem = (SessionItem) sessionCombo.getSelectedItem();
        
        if (boardId == null || boardId.isEmpty()) {
            ErrorHandler.showError(this, "Please select a board ID");
            return;
        }
        if (presenterItem == null) {
            ErrorHandler.showError(this, "Please select a presenter");
            return;
        }
        if (sessionItem == null) {
            ErrorHandler.showError(this, "Please select a session");
            return;
        }
        
        try {
            posterBoardService.assignBoard(boardId, presenterItem.student.getPresenterId(), 
                                           sessionItem.session.getSessionId());
            
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
        
        // Trigger presenter combo population if there's a session selected
        if (sessionCombo.getItemCount() > 0) {
            sessionCombo.setSelectedIndex(0);
            onSessionSelected(); // Manually trigger to populate presenters
        } else {
            // No sessions available, clear presenter combo
            presenterCombo.removeAllItems();
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
     * Wrapper class for Presenter items.
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
    
    public JComboBox<PresenterItem> getPresenterCombo() {
        return presenterCombo;
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
