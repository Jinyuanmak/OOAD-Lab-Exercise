package com.fci.seminar.service;

import java.util.ArrayList;
import java.util.List;

import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.model.UserRole;
import com.fci.seminar.util.IdGenerator;

/**
 * Service class for user management operations.
 * Handles authentication, registration, and user retrieval.
 */
public class UserService {
    
    private final DataStore dataStore;

    public UserService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Authenticates a user with the given credentials and role.
     * @param username the username
     * @param password the password
     * @param role the expected user role
     * @return the authenticated User if credentials match, null otherwise
     */
    public User authenticate(String username, String password, UserRole role) {
        if (username == null || password == null || role == null) {
            return null;
        }
        
        for (User user : dataStore.getUsers().values()) {
            if (user.getUsername().equals(username) 
                    && user.getPassword().equals(password)
                    && user.getRole() == role) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Authenticates a user with the given credentials (auto-detects role).
     * Supports login with username or student ID for students.
     * Student ID comparison is case-insensitive.
     * @param usernameOrStudentId the username or student ID
     * @param password the password
     * @return the authenticated User if credentials match, null otherwise
     */
    public User authenticate(String usernameOrStudentId, String password) {
        if (usernameOrStudentId == null || password == null) {
            return null;
        }
        
        for (User user : dataStore.getUsers().values()) {
            // Check username match
            if (user.getUsername().equals(usernameOrStudentId) 
                    && user.getPassword().equals(password)) {
                return user;
            }
            
            // For students, also check student ID match (case-insensitive)
            if (user instanceof Student) {
                Student student = (Student) user;
                if (student.getStudentId() != null 
                        && student.getStudentId().equalsIgnoreCase(usernameOrStudentId)
                        && student.getPassword().equals(password)) {
                    return student;
                }
            }
        }
        return null;
    }

    /**
     * Registers a new student with validation.
     * @param student the student to register
     * @throws IllegalArgumentException if validation fails
     */
    public void registerStudent(Student student) {
        validateStudent(student);
        
        // Generate presenter ID if not set
        if (student.getPresenterId() == null || student.getPresenterId().isEmpty()) {
            student.setPresenterId(IdGenerator.generatePresenterId());
        }
        
        // Generate user ID if not set
        if (student.getId() == null || student.getId().isEmpty()) {
            student.setId(IdGenerator.generateUserId());
        }
        
        dataStore.addUser(student);
    }


    /**
     * Validates student registration data.
     * @param student the student to validate
     * @throws IllegalArgumentException if any required field is invalid
     */
    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        // Validate username
        if (isNullOrEmpty(student.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!isValidUsername(student.getUsername())) {
            throw new IllegalArgumentException("Username must be alphanumeric, 3-20 characters");
        }
        
        // Check for duplicate username
        if (isUsernameTaken(student.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Validate password
        if (isNullOrEmpty(student.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }
        if (student.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        
        // Validate research title
        if (isNullOrEmpty(student.getResearchTitle())) {
            throw new IllegalArgumentException("Research title is required");
        }
        if (student.getResearchTitle().length() > 200) {
            throw new IllegalArgumentException("Research title must not exceed 200 characters");
        }
        
        // Validate abstract
        if (isNullOrEmpty(student.getAbstractText())) {
            throw new IllegalArgumentException("Abstract is required");
        }
        if (student.getAbstractText().length() > 2000) {
            throw new IllegalArgumentException("Abstract must not exceed 2000 characters");
        }
        
        // Validate supervisor name
        if (isNullOrEmpty(student.getSupervisorName())) {
            throw new IllegalArgumentException("Supervisor name is required");
        }
        
        // Validate presentation type
        if (student.getPresentationType() == null) {
            throw new IllegalArgumentException("Presentation type is required");
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidUsername(String username) {
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9]+$");
    }

    private boolean isUsernameTaken(String username) {
        for (User user : dataStore.getUsers().values()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a student by their ID.
     * @param id the student ID
     * @return the Student if found, null otherwise
     */
    public Student getStudentById(String id) {
        User user = dataStore.getUser(id);
        if (user instanceof Student) {
            return (Student) user;
        }
        return null;
    }

    /**
     * Retrieves all registered students.
     * @return list of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User user : dataStore.getUsers().values()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }

    /**
     * Retrieves all evaluators.
     * @return list of all evaluators
     */
    public List<Evaluator> getAllEvaluators() {
        List<Evaluator> evaluators = new ArrayList<>();
        for (User user : dataStore.getUsers().values()) {
            if (user instanceof Evaluator) {
                evaluators.add((Evaluator) user);
            }
        }
        return evaluators;
    }

    /**
     * Retrieves a student by their presenter ID.
     * @param presenterId the presenter ID
     * @return the Student if found, null otherwise
     */
    public Student getStudentByPresenterId(String presenterId) {
        if (presenterId == null) {
            return null;
        }
        for (User user : dataStore.getUsers().values()) {
            if (user instanceof Student student) {
                if (presenterId.equals(student.getPresenterId())) {
                    return student;
                }
            }
        }
        return null;
    }
    
    /**
     * Updates an existing student's information.
     * Used when a logged-in student updates their registration details.
     * @param student the student to update
     * @throws IllegalArgumentException if validation fails
     */
    public void updateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        if (student.getId() == null || student.getId().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required for update");
        }
        
        // Generate presenter ID if not set (needed for seminar registration)
        if (student.getPresenterId() == null || student.getPresenterId().isEmpty()) {
            student.setPresenterId(IdGenerator.generatePresenterId());
        }
        
        // Validate research details if provided
        if (student.getResearchTitle() != null && student.getResearchTitle().length() > 200) {
            throw new IllegalArgumentException("Research title must not exceed 200 characters");
        }
        
        if (student.getAbstractText() != null && student.getAbstractText().length() > 2000) {
            throw new IllegalArgumentException("Abstract must not exceed 2000 characters");
        }
        
        // Update the student in the data store
        dataStore.updateUser(student);
    }
    
    /**
     * Checks if a username already exists.
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        for (User user : dataStore.getUsers().values()) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if a student ID already exists.
     * Case-insensitive comparison.
     * @param studentId the student ID to check
     * @return true if student ID exists, false otherwise
     */
    public boolean isStudentIdExists(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            return false;
        }
        String normalizedId = studentId.trim().toUpperCase();
        for (User user : dataStore.getUsers().values()) {
            if (user instanceof Student) {
                Student student = (Student) user;
                if (student.getStudentId() != null 
                        && student.getStudentId().toUpperCase().equals(normalizedId)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Creates a new student account.
     * @param name the student's full name
     * @param studentId the student ID (10 digits)
     * @param password the password
     * @throws IllegalArgumentException if validation fails
     */
    public void createStudent(String name, String studentId, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (studentId == null || !studentId.matches("[A-Za-z0-9]{10}")) {
            throw new IllegalArgumentException("Student ID must be exactly 10 characters (letters and numbers)");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        // Check for duplicates
        if (isUsernameExists(name)) {
            throw new IllegalArgumentException("An account with this name already exists");
        }
        if (isStudentIdExists(studentId)) {
            throw new IllegalArgumentException("An account with this Student ID already exists");
        }
        
        // Create new student - ID will be auto-generated by database
        Student student = new Student(
            null,  // ID will be set by database AUTO_INCREMENT
            name.trim(),
            password,
            UserRole.PRESENTER
        );
        student.setStudentId(studentId.toUpperCase()); // Store in uppercase for consistency
        
        // Don't set presenter_id yet - only when they register for seminar
        
        dataStore.addUser(student);
    }
    
    /**
     * Creates a new evaluator account.
     * @param name the evaluator's full name
     * @param password the password
     * @throws IllegalArgumentException if validation fails
     */
    public void createEvaluator(String name, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        // Check for duplicates
        if (isUsernameExists(name)) {
            throw new IllegalArgumentException("An account with this name already exists");
        }
        
        // Create new evaluator - ID will be auto-generated by database
        Evaluator evaluator = new Evaluator(
            null,  // ID will be set by database AUTO_INCREMENT
            name.trim(),
            password
        );
        evaluator.setRole(UserRole.PANEL_MEMBER);
        evaluator.setEvaluatorId(IdGenerator.generateEvaluatorId());
        
        dataStore.addUser(evaluator);
    }
}

