package com.fci.seminar.model;

import java.util.Objects;

/**
 * Represents a student who presents research at the seminar.
 */
public class Student extends User {
    private static final long serialVersionUID = 1L;

    private String researchTitle;
    private String abstractText;
    private String supervisorName;
    private PresentationType presentationType;
    private String filePath;
    private String presenterId;

    public Student() {
        super();
        setRole(UserRole.STUDENT);
    }

    public Student(String id, String username, String password) {
        super(id, username, password, UserRole.STUDENT);
    }

    public String getResearchTitle() {
        return researchTitle;
    }

    public void setResearchTitle(String researchTitle) {
        this.researchTitle = researchTitle;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public PresentationType getPresentationType() {
        return presentationType;
    }

    public void setPresentationType(PresentationType presentationType) {
        this.presentationType = presentationType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(presenterId, student.presenterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), presenterId);
    }
}
