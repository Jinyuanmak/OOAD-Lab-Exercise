package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fci.seminar.service.ReportService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for generating and exporting reports.
 * Requirements: 9.1, 9.2, 9.3, 9.4, 9.5
 */
public class ReportPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final ReportService reportService;
    
    private JTextArea reportArea;
    private JButton scheduleButton;
    private JButton evaluationButton;
    private JButton summaryButton;
    private JButton exportButton;
    private JButton backButton;
    private JLabel statusLabel;
    
    private String currentReportContent;
    private String currentReportType;

    /**
     * Creates a new ReportPanel.
     * @param app the main application frame
     * @param reportService the report service
     */
    public ReportPanel(SeminarApp app, ReportService reportService) {
        this.app = app;
        this.reportService = reportService;
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
        
        // Create report buttons panel
        JPanel buttonsPanel = createReportButtonsPanel();
        contentPanel.add(buttonsPanel, BorderLayout.WEST);
        
        // Create report display panel
        JPanel displayPanel = createReportDisplayPanel();
        contentPanel.add(displayPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Create bottom button panel
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the title panel.
     * @return the title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the report buttons panel.
     * @return the buttons panel
     */
    private JPanel createReportButtonsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Generate Report"));
        panel.setPreferredSize(new Dimension(200, 300));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        
        int row = 0;
        
        // Schedule Report button
        scheduleButton = new JButton("Schedule Report");
        scheduleButton.setPreferredSize(new Dimension(160, 40));
        scheduleButton.addActionListener(e -> generateScheduleReport());
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(scheduleButton, gbc);
        
        // Evaluation Report button
        evaluationButton = new JButton("Evaluation Report");
        evaluationButton.setPreferredSize(new Dimension(160, 40));
        evaluationButton.addActionListener(e -> generateEvaluationReport());
        gbc.gridy = row++;
        panel.add(evaluationButton, gbc);
        
        // Summary Report button
        summaryButton = new JButton("Summary Report");
        summaryButton.setPreferredSize(new Dimension(160, 40));
        summaryButton.addActionListener(e -> generateSummaryReport());
        gbc.gridy = row++;
        panel.add(summaryButton, gbc);
        
        // Separator
        gbc.gridy = row++;
        panel.add(new JLabel(" "), gbc);
        
        // Export button
        exportButton = new JButton("Export to File");
        exportButton.setPreferredSize(new Dimension(160, 40));
        exportButton.setEnabled(false);
        exportButton.addActionListener(e -> exportReport());
        gbc.gridy = row++;
        panel.add(exportButton, gbc);
        
        // Status label
        statusLabel = new JLabel("Select a report type");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        panel.add(statusLabel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the report display panel.
     * @return the display panel
     */
    private JPanel createReportDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Report Content"));
        
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setText("Select a report type from the left panel to generate a report.");
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates the bottom button panel.
     * @return the bottom panel
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        backButton = new JButton("Back to Dashboard");
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.addActionListener(e -> navigateBack());
        panel.add(backButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(100, 35));
        clearButton.addActionListener(e -> clearReport());
        panel.add(clearButton);
        
        return panel;
    }
    
    /**
     * Generates the schedule report.
     */
    private void generateScheduleReport() {
        currentReportContent = reportService.generateScheduleReport();
        currentReportType = "schedule";
        reportArea.setText(currentReportContent);
        reportArea.setCaretPosition(0);
        exportButton.setEnabled(true);
        statusLabel.setText("Schedule report generated");
    }
    
    /**
     * Generates the evaluation report.
     */
    private void generateEvaluationReport() {
        currentReportContent = reportService.generateEvaluationReport();
        currentReportType = "evaluation";
        reportArea.setText(currentReportContent);
        reportArea.setCaretPosition(0);
        exportButton.setEnabled(true);
        statusLabel.setText("Evaluation report generated");
    }
    
    /**
     * Generates the summary report.
     */
    private void generateSummaryReport() {
        currentReportContent = reportService.generateSummaryReport();
        currentReportType = "summary";
        reportArea.setText(currentReportContent);
        reportArea.setCaretPosition(0);
        exportButton.setEnabled(true);
        statusLabel.setText("Summary report generated");
    }
    
    /**
     * Exports the current report to a file.
     * Allows user to choose format: TXT, PDF, or CSV.
     */
    private void exportReport() {
        if (currentReportContent == null || currentReportContent.isEmpty()) {
            ErrorHandler.showError(this, "No report to export. Generate a report first.");
            return;
        }
        
        // Ask user to select format
        String[] options = {"TXT (Text)", "PDF (Document)", "CSV (Spreadsheet)"};
        int choice = javax.swing.JOptionPane.showOptionDialog(
            this,
            "Select export format:",
            "Export Format",
            javax.swing.JOptionPane.DEFAULT_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == javax.swing.JOptionPane.CLOSED_OPTION) {
            return; // User cancelled
        }
        
        String format;
        String extension;
        String filterDescription;
        
        switch (choice) {
            case 0: // TXT
                format = "txt";
                extension = ".txt";
                filterDescription = "Text Files (*.txt)";
                break;
            case 1: // PDF
                format = "pdf";
                extension = ".pdf";
                filterDescription = "PDF Files (*.pdf)";
                break;
            case 2: // CSV
                format = "csv";
                extension = ".csv";
                filterDescription = "CSV Files (*.csv)";
                break;
            default:
                return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter(filterDescription, format));
        
        // Suggest filename based on report type and format
        String suggestedName = currentReportType + "_report" + extension;
        fileChooser.setSelectedFile(new File(suggestedName));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Add extension if not present
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(extension)) {
                filePath += extension;
            }
            
            try {
                switch (format) {
                    case "txt":
                        reportService.exportToFile(currentReportContent, filePath);
                        break;
                    case "pdf":
                        reportService.exportToPDF(currentReportContent, filePath, currentReportType);
                        break;
                    case "csv":
                        reportService.exportToCSV(currentReportContent, filePath, currentReportType);
                        break;
                }
                
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + filePath,
                    "Export Successful",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                
                statusLabel.setText("Report exported as " + format.toUpperCase());
                
            } catch (IOException e) {
                ErrorHandler.showError(this, "Failed to export report: " + e.getMessage());
            }
        }
    }
    
    /**
     * Clears the report display.
     */
    private void clearReport() {
        reportArea.setText("Select a report type from the left panel to generate a report.");
        currentReportContent = null;
        currentReportType = null;
        exportButton.setEnabled(false);
        statusLabel.setText("Select a report type");
    }
    
    /**
     * Refreshes the panel.
     */
    public void refresh() {
        clearReport();
    }
    
    /**
     * Navigates back to the coordinator dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.COORDINATOR_DASHBOARD);
    }

    // Getter methods for testing
    
    public JTextArea getReportArea() {
        return reportArea;
    }
    
    public JButton getScheduleButton() {
        return scheduleButton;
    }
    
    public JButton getEvaluationButton() {
        return evaluationButton;
    }
    
    public JButton getSummaryButton() {
        return summaryButton;
    }
    
    public JButton getExportButton() {
        return exportButton;
    }
    
    public JButton getBackButton() {
        return backButton;
    }
    
    public JLabel getStatusLabel() {
        return statusLabel;
    }
    
    public String getCurrentReportContent() {
        return currentReportContent;
    }
    
    public String getCurrentReportType() {
        return currentReportType;
    }
}
