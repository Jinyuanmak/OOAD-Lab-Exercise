package com.fci.seminar.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the ceremony agenda with awards and ceremony date.
 */
public class CeremonyAgenda implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Award> awards;
    private LocalDateTime ceremonyDate;

    public CeremonyAgenda() {
        this.awards = new ArrayList<>();
    }

    public CeremonyAgenda(List<Award> awards, LocalDateTime ceremonyDate) {
        this.awards = awards != null ? awards : new ArrayList<>();
        this.ceremonyDate = ceremonyDate;
    }

    public List<Award> getAwards() {
        return awards;
    }

    public void setAwards(List<Award> awards) {
        this.awards = awards != null ? awards : new ArrayList<>();
    }

    public void addAward(Award award) {
        if (award != null) {
            awards.add(award);
        }
    }

    public LocalDateTime getCeremonyDate() {
        return ceremonyDate;
    }

    public void setCeremonyDate(LocalDateTime ceremonyDate) {
        this.ceremonyDate = ceremonyDate;
    }

    /**
     * Generates a formatted agenda string for the ceremony.
     * @return formatted agenda with ceremony date and award sequence
     */
    public String generateFormattedAgenda() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");

        sb.append("===========================================\n");
        sb.append("    FCI POSTGRADUATE RESEARCH SEMINAR\n");
        sb.append("         AWARD CEREMONY AGENDA\n");
        sb.append("===========================================\n\n");

        if (ceremonyDate != null) {
            sb.append("Date: ").append(ceremonyDate.format(formatter)).append("\n\n");
        }

        sb.append("AWARD PRESENTATIONS:\n");
        sb.append("-------------------------------------------\n\n");

        int order = 1;
        for (Award award : awards) {
            sb.append(order).append(". ").append(award.getType().getDisplayName()).append("\n");
            sb.append("   Winner ID: ").append(award.getWinnerId()).append("\n");
            sb.append("   Score: ").append(String.format("%.2f", award.getScore())).append("\n\n");
            order++;
        }

        if (awards.isEmpty()) {
            sb.append("   No awards have been determined yet.\n\n");
        }

        sb.append("===========================================\n");
        sb.append("    Congratulations to all winners!\n");
        sb.append("===========================================\n");

        return sb.toString();
    }
}
