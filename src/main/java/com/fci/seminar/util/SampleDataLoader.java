package com.fci.seminar.util;

import com.fci.seminar.model.Coordinator;
import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.UserRole;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.service.UserService;

/**
 * Utility class to load sample data for testing the Seminar Management System.
 * Creates sample users (students, evaluators, coordinator) with basic credentials only.
 */
public class SampleDataLoader {
    
    /**
     * Loads sample data into the provided DataStore.
     * Only creates users with username/password - no research data, sessions, or evaluations.
     * @param dataStore the DataStore to populate
     * @param userService the UserService for registering users
     * @param sessionService the SessionService (not used)
     * @param evaluationService the EvaluationService (not used)
     */
    public static void loadSampleData(DataStore dataStore, UserService userService, 
                                     SessionService sessionService, EvaluationService evaluationService) {
        
        // Create sample coordinator
        Coordinator coordinator = new Coordinator();
        coordinator.setUsername("admin");
        coordinator.setPassword("admin123");
        coordinator.setRole(UserRole.COORDINATOR);
        coordinator.setId(IdGenerator.generateUserId());
        dataStore.addUser(coordinator);
        
        // Create sample evaluators
        Evaluator evaluator1 = new Evaluator();
        evaluator1.setUsername("eval1");
        evaluator1.setPassword("eval123");
        evaluator1.setRole(UserRole.EVALUATOR);
        evaluator1.setId(IdGenerator.generateUserId());
        evaluator1.setEvaluatorId(IdGenerator.generateEvaluatorId());
        dataStore.addUser(evaluator1);
        
        Evaluator evaluator2 = new Evaluator();
        evaluator2.setUsername("eval2");
        evaluator2.setPassword("eval123");
        evaluator2.setRole(UserRole.EVALUATOR);
        evaluator2.setId(IdGenerator.generateUserId());
        evaluator2.setEvaluatorId(IdGenerator.generateEvaluatorId());
        dataStore.addUser(evaluator2);
        
        // Create sample students (username/password only, no research data)
        createBasicStudent(dataStore, "student1", "stud123");
        createBasicStudent(dataStore, "student2", "stud123");
        createBasicStudent(dataStore, "student3", "stud123");
        createBasicStudent(dataStore, "student4", "stud123");
    }
    
    /**
     * Creates a basic student with only username and password.
     * @param dataStore the DataStore to add the student to
     * @param username the student username
     * @param password the student password
     */
    private static void createBasicStudent(DataStore dataStore, String username, String password) {
        Student student = new Student();
        student.setUsername(username);
        student.setPassword(password);
        student.setRole(UserRole.STUDENT);
        student.setId(IdGenerator.generateUserId());
        dataStore.addUser(student);
    }
    
    /**
     * Checks if sample data has already been loaded.
     * @param dataStore the DataStore to check
     * @return true if sample data exists, false otherwise
     */
    public static boolean hasSampleData(DataStore dataStore) {
        // Check database for existing admin user
        if (dataStore.getDatabaseManager() != null && dataStore.getDatabaseManager().isConnected()) {
            return dataStore.getDatabaseManager().hasSampleData();
        }
        
        // Check in-memory data as fallback
        for (com.fci.seminar.model.User user : dataStore.getUsers().values()) {
            if ("admin".equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }
}
