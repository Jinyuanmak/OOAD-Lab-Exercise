package com.fci.seminar.property;

import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.User;
import com.fci.seminar.model.UserRole;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.UserService;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for UserService.
 * Feature: seminar-management-system
 */
class UserServicePropertyTest {

    /**
     * Property 1: Authentication Correctness
     * For any user credentials (username, password, role), if the credentials match
     * an existing user with that role, authentication SHALL return that user;
     * otherwise, authentication SHALL return null/failure.
     * Validates: Requirements 1.2, 1.3
     */
    @Property(tries = 100)
    void authenticationReturnsUserWhenCredentialsMatch(
            @ForAll("validStudent") Student student) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        // Add the student to the data store
        dataStore.addUser(student);
        
        // Authentication with correct credentials should return the user
        User authenticated = userService.authenticate(
            student.getUsername(), 
            student.getPassword(), 
            UserRole.STUDENT
        );
        
        assert authenticated != null : "Authentication should succeed with correct credentials";
        assert authenticated.getId().equals(student.getId()) : 
            "Authenticated user should be the same user";
    }

    @Property(tries = 100)
    void authenticationFailsWithWrongPassword(
            @ForAll("validStudent") Student student,
            @ForAll("wrongPassword") String wrongPassword) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        dataStore.addUser(student);

        
        // Skip if wrong password happens to match
        if (wrongPassword.equals(student.getPassword())) {
            return;
        }
        
        // Authentication with wrong password should fail
        User authenticated = userService.authenticate(
            student.getUsername(), 
            wrongPassword, 
            UserRole.STUDENT
        );
        
        assert authenticated == null : 
            "Authentication should fail with wrong password";
    }

    @Property(tries = 100)
    void authenticationFailsWithWrongRole(
            @ForAll("validStudent") Student student) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        dataStore.addUser(student);
        
        // Authentication with wrong role should fail
        User authenticated = userService.authenticate(
            student.getUsername(), 
            student.getPassword(), 
            UserRole.EVALUATOR  // Wrong role
        );
        
        assert authenticated == null : 
            "Authentication should fail with wrong role";
    }

    @Property(tries = 100)
    void authenticationFailsForNonExistentUser(
            @ForAll("validUsername") String username,
            @ForAll("validPassword") String password,
            @ForAll("userRole") UserRole role) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        // Empty data store - no users exist
        User authenticated = userService.authenticate(username, password, role);
        
        assert authenticated == null : 
            "Authentication should fail for non-existent user";
    }

    @Provide
    Arbitrary<Student> validStudent() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(8),
            Arbitraries.strings().alpha().numeric().ofMinLength(3).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(4).ofMaxLength(10)
        ).as((id, username, password) -> {
            Student s = new Student("U-" + id, username, password);
            s.setPresenterId("P-" + id);
            s.setResearchTitle("Test Research");
            s.setAbstractText("Test Abstract");
            s.setSupervisorName("Test Supervisor");
            s.setPresentationType(PresentationType.ORAL);
            return s;
        });
    }

    @Provide
    Arbitrary<String> wrongPassword() {
        return Arbitraries.strings().alpha().ofMinLength(4).ofMaxLength(15);
    }

    @Provide
    Arbitrary<String> validUsername() {
        return Arbitraries.strings().alpha().numeric().ofMinLength(3).ofMaxLength(10);
    }

    @Provide
    Arbitrary<String> validPassword() {
        return Arbitraries.strings().alpha().ofMinLength(4).ofMaxLength(10);
    }

    @Provide
    Arbitrary<UserRole> userRole() {
        return Arbitraries.of(UserRole.values());
    }

    // ========================================================================
    // Property 2: Registration Validation
    // For any student registration data, if any required field (title, abstract,
    // supervisor, type) is empty or invalid, the registration SHALL be rejected;
    // if all fields are valid, registration SHALL succeed.
    // Validates: Requirements 2.3, 2.4
    // ========================================================================

    @Property(tries = 100)
    void registrationSucceedsWithValidData(
            @ForAll("validRegistrationStudent") Student student) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        // Registration with valid data should succeed
        userService.registerStudent(student);
        
        // Student should be in the data store
        assert dataStore.getUsers().containsKey(student.getId()) :
            "Student should be added to data store after registration";
        
        // Presenter ID should be set
        assert student.getPresenterId() != null && !student.getPresenterId().isEmpty() :
            "Presenter ID should be generated";
    }

    @Property(tries = 100)
    void registrationFailsWithEmptyTitle(
            @ForAll("validRegistrationStudent") Student student) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        // Set empty title
        student.setResearchTitle("");
        
        try {
            userService.registerStudent(student);
            assert false : "Registration should fail with empty title";
        } catch (IllegalArgumentException e) {
            // Expected
            assert e.getMessage().contains("title") || e.getMessage().contains("Title") :
                "Error message should mention title";
        }
    }

    @Property(tries = 100)
    void registrationFailsWithEmptyAbstract(
            @ForAll("validRegistrationStudent") Student student) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        // Set empty abstract
        student.setAbstractText("");
        
        try {
            userService.registerStudent(student);
            assert false : "Registration should fail with empty abstract";
        } catch (IllegalArgumentException e) {
            // Expected
            assert e.getMessage().contains("Abstract") || e.getMessage().contains("abstract") :
                "Error message should mention abstract";
        }
    }

    @Property(tries = 100)
    void registrationFailsWithEmptySupervisor(
            @ForAll("validRegistrationStudent") Student student) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        // Set empty supervisor
        student.setSupervisorName("");
        
        try {
            userService.registerStudent(student);
            assert false : "Registration should fail with empty supervisor";
        } catch (IllegalArgumentException e) {
            // Expected
            assert e.getMessage().contains("Supervisor") || e.getMessage().contains("supervisor") :
                "Error message should mention supervisor";
        }
    }

    @Property(tries = 100)
    void registrationFailsWithNullPresentationType(
            @ForAll("validRegistrationStudent") Student student) {
        
        DataStore dataStore = new DataStore();
        UserService userService = new UserService(dataStore);
        
        // Set null presentation type
        student.setPresentationType(null);
        
        try {
            userService.registerStudent(student);
            assert false : "Registration should fail with null presentation type";
        } catch (IllegalArgumentException e) {
            // Expected
            assert e.getMessage().contains("Presentation") || e.getMessage().contains("type") :
                "Error message should mention presentation type";
        }
    }

    @Provide
    Arbitrary<Student> validRegistrationStudent() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(4).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(200),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),
            Arbitraries.of(PresentationType.values())
        ).as((username, password, title, abstractText, supervisor, type) -> {
            Student s = new Student();
            s.setUsername(username);
            s.setPassword(password);
            s.setResearchTitle(title);
            s.setAbstractText(abstractText);
            s.setSupervisorName(supervisor);
            s.setPresentationType(type);
            return s;
        });
    }
}
