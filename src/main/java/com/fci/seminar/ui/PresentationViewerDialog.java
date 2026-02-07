package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.fci.seminar.model.Student;
import com.fci.seminar.service.FileStorageService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Reusable dialog for viewing student presentation materials with zoom functionality.
 * Supports PDF, images, and text files with zoom in/out and Ctrl+Scroll zoom.
 */
public class PresentationViewerDialog extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private final Student student;
    private JPanel contentPanel;
    private JScrollPane contentScrollPane;
    private JLabel zoomLabel;
    private double zoomLevel = 1.0;
    private static final double ZOOM_STEP = 0.1;
    private static final double MIN_ZOOM = 0.5;
    private static final double MAX_ZOOM = 1.2;
    
    private java.io.File currentFile;
    private String currentFileType;

    /**
     * Creates a new PresentationViewerDialog.
     * @param parent the parent component
     * @param student the student whose materials to view
     */
    public PresentationViewerDialog(java.awt.Component parent, Student student) {
        super("Presentation Materials - " + student.getUsername());
        this.student = student;
        
        setLayout(new BorderLayout(10, 10));
        setSize(900, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Set icon to match parent window if available
        java.awt.Frame parentFrame = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(parent);
        if (parentFrame != null && parentFrame.getIconImage() != null) {
            setIconImage(parentFrame.getIconImage());
        }
        
        initializeUI();
        loadMaterials();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        // Create info panel with student details
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.NORTH);
        
        // Create materials viewer panel with zoom controls
        JPanel viewerPanel = createViewerPanel();
        add(viewerPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the info panel with student details.
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Student Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        JLabel nameLabel = new JLabel(student.getUsername());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(nameLabel, gbc);
        
        // Student ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        JLabel idLabel = new JLabel(student.getStudentId());
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(idLabel, gbc);
        
        // Research Title
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Research Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JLabel titleLabel = new JLabel(student.getResearchTitle() != null ? student.getResearchTitle() : "N/A");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(titleLabel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the viewer panel with zoom controls.
     */
    private JPanel createViewerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Presentation Materials"));
        
        // Zoom control panel
        JPanel zoomPanel = createZoomPanel();
        panel.add(zoomPanel, BorderLayout.NORTH);
        
        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentScrollPane = new JScrollPane(contentPanel);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add mouse wheel listener for Ctrl+Scroll zoom
        contentScrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    if (e.getWheelRotation() < 0) {
                        zoomIn();
                    } else {
                        zoomOut();
                    }
                    e.consume();
                }
            }
        });
        
        panel.add(contentScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the zoom control panel.
     */
    private JPanel createZoomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Zoom out button
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        zoomOutButton.setPreferredSize(new Dimension(50, 35));
        zoomOutButton.setToolTipText("Zoom Out");
        zoomOutButton.addActionListener(e -> zoomOut());
        panel.add(zoomOutButton);
        
        // Zoom in button
        JButton zoomInButton = new JButton("+");
        zoomInButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        zoomInButton.setPreferredSize(new Dimension(50, 35));
        zoomInButton.setToolTipText("Zoom In");
        zoomInButton.addActionListener(e -> zoomIn());
        panel.add(zoomInButton);
        
        // Zoom percentage label
        zoomLabel = new JLabel("100%");
        zoomLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        zoomLabel.setPreferredSize(new Dimension(80, 35));
        zoomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(zoomLabel);
        
        // Reset zoom button
        JButton resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(70, 35));
        resetButton.setToolTipText("Reset Zoom to 100%");
        resetButton.addActionListener(e -> resetZoom());
        panel.add(resetButton);
        
        // Instruction label
        JLabel instructionLabel = new JLabel("(Ctrl + Mouse Scroll to zoom)");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        panel.add(instructionLabel);
        
        return panel;
    }
    
    /**
     * Creates the button panel.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        
        JButton closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);
        
        return panel;
    }
    
    /**
     * Zooms in the content.
     */
    private void zoomIn() {
        if (zoomLevel < MAX_ZOOM) {
            zoomLevel += ZOOM_STEP;
            updateZoom();
        }
    }
    
    /**
     * Zooms out the content.
     */
    private void zoomOut() {
        if (zoomLevel > MIN_ZOOM) {
            zoomLevel -= ZOOM_STEP;
            updateZoom();
        }
    }
    
    /**
     * Resets zoom to 100%.
     */
    private void resetZoom() {
        zoomLevel = 1.0;
        updateZoom();
    }
    
    /**
     * Updates the zoom level and refreshes the content.
     */
    private void updateZoom() {
        zoomLabel.setText(String.format("%d%%", (int)(zoomLevel * 100)));
        
        // Reload content with new zoom level
        if (currentFile != null && currentFileType != null) {
            renderContent(currentFile, currentFileType);
        }
    }
    
    /**
     * Loads the presentation materials.
     */
    private void loadMaterials() {
        String filePath = student.getFilePath();
        
        if (filePath == null || filePath.isEmpty()) {
            showError("No presentation materials uploaded for this student");
            return;
        }
        
        FileStorageService fileService = FileStorageService.getInstance();
        
        // Resolve the path (handles both relative and absolute paths)
        if (fileService.isAbsolutePath(filePath)) {
            // Backward compatibility: handle old absolute paths
            currentFile = new java.io.File(filePath);
        } else {
            // New relative paths
            currentFile = fileService.resolveStoragePath(filePath);
        }
        
        if (currentFile == null || !currentFile.exists()) {
            showError("File not found: " + filePath);
            return;
        }
        
        String fileName = currentFile.getName().toLowerCase();
        
        if (fileName.endsWith(".pdf")) {
            currentFileType = "pdf";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                   fileName.endsWith(".png") || fileName.endsWith(".gif")) {
            currentFileType = "image";
        } else if (fileName.endsWith(".txt")) {
            currentFileType = "text";
        } else {
            currentFileType = "other";
        }
        
        renderContent(currentFile, currentFileType);
    }
    
    /**
     * Renders the content based on file type and zoom level.
     */
    private void renderContent(java.io.File file, String fileType) {
        contentPanel.removeAll();
        
        switch (fileType) {
            case "pdf":
                renderPDF(file);
                break;
            case "image":
                renderImage(file);
                break;
            case "text":
                renderText(file);
                break;
            default:
                renderOther(file);
                break;
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Renders PDF content.
     */
    private void renderPDF(java.io.File file) {
        try {
            org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(file);
            
            JPanel pagesPanel = new JPanel();
            pagesPanel.setLayout(new javax.swing.BoxLayout(pagesPanel, javax.swing.BoxLayout.Y_AXIS));
            pagesPanel.setBackground(java.awt.Color.GRAY);
            
            // Render first 5 pages
            int pagesToRender = Math.min(5, document.getNumberOfPages());
            org.apache.pdfbox.rendering.PDFRenderer pdfRenderer = new org.apache.pdfbox.rendering.PDFRenderer(document);
            
            // Calculate DPI based on zoom level (base 150 DPI)
            float dpi = (float)(150 * zoomLevel);
            
            for (int i = 0; i < pagesToRender; i++) {
                java.awt.image.BufferedImage image = pdfRenderer.renderImageWithDPI(i, dpi, org.apache.pdfbox.rendering.ImageType.RGB);
                
                JLabel pageLabel = new JLabel(new javax.swing.ImageIcon(image));
                pageLabel.setBorder(BorderFactory.createLineBorder(java.awt.Color.BLACK, 2));
                pageLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                pagesPanel.add(pageLabel);
                
                pagesPanel.add(javax.swing.Box.createVerticalStrut(10));
            }
            
            // Add info label if there are more pages
            if (document.getNumberOfPages() > pagesToRender) {
                JLabel moreLabel = new JLabel("Showing " + pagesToRender + " of " + document.getNumberOfPages() + " pages");
                moreLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
                moreLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
                pagesPanel.add(moreLabel);
            }
            
            contentPanel.add(pagesPanel, BorderLayout.CENTER);
            
            document.close();
            
        } catch (Exception ex) {
            showError("Failed to load PDF: " + ex.getMessage());
        }
    }
    
    /**
     * Renders image content.
     */
    private void renderImage(java.io.File file) {
        try {
            javax.swing.ImageIcon imageIcon = new javax.swing.ImageIcon(file.getAbsolutePath());
            java.awt.Image image = imageIcon.getImage();
            
            // Scale image based on zoom level
            int scaledWidth = (int)(image.getWidth(null) * zoomLevel);
            int scaledHeight = (int)(image.getHeight(null) * zoomLevel);
            
            java.awt.Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new javax.swing.ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            contentPanel.add(imageLabel, BorderLayout.CENTER);
            
        } catch (Exception ex) {
            showError("Failed to load image: " + ex.getMessage());
        }
    }
    
    /**
     * Renders text content.
     */
    private void renderText(java.io.File file) {
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
            
            // Scale font based on zoom level
            int fontSize = (int)(12 * zoomLevel);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, fontSize));
            
            contentPanel.add(textArea, BorderLayout.CENTER);
            
        } catch (Exception ex) {
            showError("Failed to load text file: " + ex.getMessage());
        }
    }
    
    /**
     * Renders other file types.
     */
    private void renderOther(java.io.File file) {
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
                ErrorHandler.showError(this, "Failed to open file: " + ex.getMessage());
            }
        });
        filePanel.add(openButton, BorderLayout.SOUTH);
        
        contentPanel.add(filePanel, BorderLayout.CENTER);
    }
    
    /**
     * Shows an error message.
     */
    private void showError(String message) {
        JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
        errorLabel.setForeground(java.awt.Color.RED);
        contentPanel.add(errorLabel, BorderLayout.CENTER);
    }
}
