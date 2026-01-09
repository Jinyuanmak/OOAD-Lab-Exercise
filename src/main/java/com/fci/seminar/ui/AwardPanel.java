package com.fci.seminar.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.CeremonyAgenda;
import com.fci.seminar.model.Student;
import com.fci.seminar.service.AwardService;
import com.fci.seminar.service.UserService;
import com.fci.seminar.util.ErrorHandler;

/**
 * Panel for managing awards and ceremony agenda.
 * Requirements: 8.1, 8.2, 8.3, 8.4, 8.5
 */
public class AwardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final SeminarApp app;
    private final AwardService awardService;
    private final UserService userService;
    
    private JLabel bestOralLabel;
    private JLabel bestPosterLabel;
    private JLabel peoplesChoiceLabel;
    private JComboBox<PresenterItem> votingCombo;
    private JSpinner voteCountSpinner;
    private JTextArea agendaArea;
    private JButton computeButton;
    private JButton addVoteButton;
    private JButton generateAgendaButton;
    private JButton backButton;
    
    private Map<String, Integer> peoplesChoiceVotes;

    /**
     * Creates a new AwardPanel.
     * @param app the main application frame
     * @param awardService the award service
     * @param userService the user service
     */
    public AwardPanel(SeminarApp app, AwardService awardService, UserService userService) {
        this.app = app;
        this.awardService = awardService;
        this.userService = userService;
        this.peoplesChoiceVotes = new HashMap<>();
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
        
        // Create left panel with awards and voting
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.add(createAwardsPanel(), BorderLayout.NORTH);
        leftPanel.add(createVotingPanel(), BorderLayout.CENTER);
        contentPanel.add(leftPanel, BorderLayout.WEST);
        
        // Create agenda panel
        JPanel agendaPanel = createAgendaPanel();
        contentPanel.add(agendaPanel, BorderLayout.CENTER);
        
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
        
        JLabel titleLabel = new JLabel("Awards & Ceremony");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the awards display panel.
     * @return the awards panel
     */
    private JPanel createAwardsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Award Winners"));
        panel.setPreferredSize(new Dimension(350, 180));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Best Oral
        JLabel oralTitle = new JLabel("Best Oral Presentation:");
        oralTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(oralTitle, gbc);
        
        bestOralLabel = new JLabel("Not computed");
        gbc.gridy = row++;
        panel.add(bestOralLabel, gbc);
        
        // Best Poster
        JLabel posterTitle = new JLabel("Best Poster Presentation:");
        posterTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = row++;
        panel.add(posterTitle, gbc);
        
        bestPosterLabel = new JLabel("Not computed");
        gbc.gridy = row++;
        panel.add(bestPosterLabel, gbc);
        
        // People's Choice
        JLabel choiceTitle = new JLabel("People's Choice:");
        choiceTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridy = row++;
        panel.add(choiceTitle, gbc);
        
        peoplesChoiceLabel = new JLabel("Not computed");
        gbc.gridy = row++;
        panel.add(peoplesChoiceLabel, gbc);
        
        // Compute button
        computeButton = new JButton("Compute Winners");
        computeButton.addActionListener(e -> computeWinners());
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(computeButton, gbc);
        
        return panel;
    }
    
    /**
     * Creates the voting panel for People's Choice.
     * @return the voting panel
     */
    private JPanel createVotingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("People's Choice Voting"));
        panel.setPreferredSize(new Dimension(350, 150));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Presenter selector
        JLabel presenterLabel = new JLabel("Presenter:");
        gbc.gridx = 0;
        gbc.gridy = row++;
        panel.add(presenterLabel, gbc);
        
        votingCombo = new JComboBox<>();
        gbc.gridy = row++;
        panel.add(votingCombo, gbc);
        
        // Vote count
        JLabel voteLabel = new JLabel("Vote Count:");
        gbc.gridy = row++;
        panel.add(voteLabel, gbc);
        
        voteCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        gbc.gridy = row++;
        panel.add(voteCountSpinner, gbc);
        
        // Add vote button
        addVoteButton = new JButton("Add/Update Votes");
        addVoteButton.addActionListener(e -> addVote());
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addVoteButton, gbc);
        
        return panel;
    }
    
    /**
     * Creates the agenda display panel.
     * @return the agenda panel
     */
    private JPanel createAgendaPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Ceremony Agenda"));
        
        agendaArea = new JTextArea();
        agendaArea.setEditable(false);
        agendaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        agendaArea.setText("Click 'Generate Agenda' to create the ceremony agenda.");
        
        JScrollPane scrollPane = new JScrollPane(agendaArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Generate agenda button
        generateAgendaButton = new JButton("Generate Agenda");
        generateAgendaButton.addActionListener(e -> generateAgenda());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateAgendaButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> refresh());
        panel.add(refreshButton);
        
        JButton clearVotesButton = new JButton("Clear Votes");
        clearVotesButton.setPreferredSize(new Dimension(100, 35));
        clearVotesButton.addActionListener(e -> clearVotes());
        panel.add(clearVotesButton);
        
        return panel;
    }
    
    /**
     * Computes award winners.
     */
    private void computeWinners() {
        // Clear previous awards
        awardService.clearAwards();
        
        // Compute Best Oral
        Award bestOral = awardService.computeBestOral();
        if (bestOral != null) {
            Student student = userService.getStudentByPresenterId(bestOral.getWinnerId());
            String name = student != null ? student.getUsername() : bestOral.getWinnerId();
            bestOralLabel.setText(name + " (Score: " + String.format("%.2f", bestOral.getScore()) + ")");
        } else {
            bestOralLabel.setText("No eligible presenters");
        }
        
        // Compute Best Poster
        Award bestPoster = awardService.computeBestPoster();
        if (bestPoster != null) {
            Student student = userService.getStudentByPresenterId(bestPoster.getWinnerId());
            String name = student != null ? student.getUsername() : bestPoster.getWinnerId();
            bestPosterLabel.setText(name + " (Score: " + String.format("%.2f", bestPoster.getScore()) + ")");
        } else {
            bestPosterLabel.setText("No eligible presenters");
        }
        
        // Compute People's Choice
        if (!peoplesChoiceVotes.isEmpty()) {
            Award peoplesChoice = awardService.computePeoplesChoice(peoplesChoiceVotes);
            if (peoplesChoice != null) {
                Student student = userService.getStudentByPresenterId(peoplesChoice.getWinnerId());
                String name = student != null ? student.getUsername() : peoplesChoice.getWinnerId();
                peoplesChoiceLabel.setText(name + " (Votes: " + (int) peoplesChoice.getScore() + ")");
            } else {
                peoplesChoiceLabel.setText("No votes recorded");
            }
        } else {
            peoplesChoiceLabel.setText("No votes recorded");
        }
        
        javax.swing.JOptionPane.showMessageDialog(this,
            "Award winners computed successfully!",
            "Success",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Adds or updates votes for a presenter.
     */
    private void addVote() {
        PresenterItem selected = (PresenterItem) votingCombo.getSelectedItem();
        if (selected == null) {
            ErrorHandler.showError(this, "Please select a presenter");
            return;
        }
        
        int votes = (Integer) voteCountSpinner.getValue();
        peoplesChoiceVotes.put(selected.student.getPresenterId(), votes);
        
        javax.swing.JOptionPane.showMessageDialog(this,
            "Votes recorded for " + selected.student.getUsername() + ": " + votes,
            "Success",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Generates the ceremony agenda.
     */
    private void generateAgenda() {
        CeremonyAgenda agenda;
        
        if (!peoplesChoiceVotes.isEmpty()) {
            agenda = awardService.generateAgenda(peoplesChoiceVotes);
        } else {
            agenda = awardService.generateAgenda();
        }
        
        String formattedAgenda = agenda.generateFormattedAgenda();
        
        // Enhance the agenda with presenter names
        StringBuilder enhanced = new StringBuilder();
        enhanced.append("===========================================\n");
        enhanced.append("     FCI SEMINAR CLOSING CEREMONY\n");
        enhanced.append("===========================================\n\n");
        enhanced.append("Date: ").append(agenda.getCeremonyDate() != null ? 
            agenda.getCeremonyDate().toLocalDate() : "TBD").append("\n\n");
        enhanced.append("AWARD PRESENTATIONS:\n");
        enhanced.append("-------------------------------------------\n\n");
        
        List<Award> awards = agenda.getAwards();
        int order = 1;
        for (Award award : awards) {
            Student student = userService.getStudentByPresenterId(award.getWinnerId());
            String name = student != null ? student.getUsername() : award.getWinnerId();
            
            enhanced.append(order++).append(". ").append(award.getType()).append("\n");
            enhanced.append("   Winner: ").append(name).append("\n");
            enhanced.append("   Score: ").append(String.format("%.2f", award.getScore())).append("\n\n");
        }
        
        if (awards.isEmpty()) {
            enhanced.append("No awards to present.\n");
            enhanced.append("Please compute winners first.\n");
        }
        
        enhanced.append("-------------------------------------------\n");
        enhanced.append("Congratulations to all winners!\n");
        
        agendaArea.setText(enhanced.toString());
    }
    
    /**
     * Clears all People's Choice votes.
     */
    private void clearVotes() {
        if (ErrorHandler.confirmAction(this, "Are you sure you want to clear all votes?")) {
            peoplesChoiceVotes.clear();
            peoplesChoiceLabel.setText("Not computed");
            javax.swing.JOptionPane.showMessageDialog(this,
                "All votes cleared.",
                "Success",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Refreshes the panel data.
     */
    public void refresh() {
        // Refresh voting combo
        votingCombo.removeAllItems();
        List<Student> students = userService.getAllStudents();
        for (Student student : students) {
            if (student.getPresenterId() != null) {
                votingCombo.addItem(new PresenterItem(student));
            }
        }
        
        // Reset labels
        bestOralLabel.setText("Not computed");
        bestPosterLabel.setText("Not computed");
        if (peoplesChoiceVotes.isEmpty()) {
            peoplesChoiceLabel.setText("Not computed");
        }
    }
    
    /**
     * Navigates back to the coordinator dashboard.
     */
    private void navigateBack() {
        app.showPanel(SeminarApp.COORDINATOR_DASHBOARD);
    }
    
    // Inner class for combo box items
    
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
            return student.getPresenterId() + " - " + student.getUsername();
        }
    }

    // Getter methods for testing
    
    public JLabel getBestOralLabel() {
        return bestOralLabel;
    }
    
    public JLabel getBestPosterLabel() {
        return bestPosterLabel;
    }
    
    public JLabel getPeoplesChoiceLabel() {
        return peoplesChoiceLabel;
    }
    
    public JComboBox<PresenterItem> getVotingCombo() {
        return votingCombo;
    }
    
    public JSpinner getVoteCountSpinner() {
        return voteCountSpinner;
    }
    
    public JTextArea getAgendaArea() {
        return agendaArea;
    }
    
    public JButton getComputeButton() {
        return computeButton;
    }
    
    public JButton getAddVoteButton() {
        return addVoteButton;
    }
    
    public JButton getGenerateAgendaButton() {
        return generateAgendaButton;
    }
    
    public JButton getBackButton() {
        return backButton;
    }
    
    public Map<String, Integer> getPeoplesChoiceVotes() {
        return peoplesChoiceVotes;
    }
}
