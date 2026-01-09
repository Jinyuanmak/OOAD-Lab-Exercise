package com.fci.seminar.util;

import java.time.LocalDate;

import com.fci.seminar.model.Coordinator;
import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.Evaluator;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.model.UserRole;
import com.fci.seminar.service.DataStore;
import com.fci.seminar.service.EvaluationService;
import com.fci.seminar.service.SessionService;
import com.fci.seminar.service.UserService;

/**
 * Utility class to load sample data for testing the Seminar Management System.
 * Creates sample users (students, evaluators, coordinator), sessions, and evaluations.
 */
public class SampleDataLoader {
    
    /**
     * Loads sample data into the provided DataStore.
     * @param dataStore the DataStore to populate
     * @param userService the UserService for registering users
     * @param sessionService the SessionService for creating sessions
     * @param evaluationService the EvaluationService for submitting evaluations
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
        dataStore.addUser(evaluator1);
        
        Evaluator evaluator2 = new Evaluator();
        evaluator2.setUsername("eval2");
        evaluator2.setPassword("eval123");
        evaluator2.setRole(UserRole.EVALUATOR);
        evaluator2.setId(IdGenerator.generateUserId());
        dataStore.addUser(evaluator2);
        
        // Create sample students
        Student student1 = new Student();
        student1.setUsername("student1");
        student1.setPassword("stud123");
        student1.setRole(UserRole.STUDENT);
        student1.setResearchTitle("Machine Learning for Healthcare");
        student1.setAbstractText("This research explores the application of machine learning algorithms in healthcare diagnostics, focusing on early disease detection and personalized treatment recommendations.");
        student1.setSupervisorName("Dr. Smith");
        student1.setPresentationType(PresentationType.ORAL);
        try {
            userService.registerStudent(student1);
        } catch (IllegalArgumentException e) {
            // Student already exists
        }
        
        Student student2 = new Student();
        student2.setUsername("student2");
        student2.setPassword("stud123");
        student2.setRole(UserRole.STUDENT);
        student2.setResearchTitle("Blockchain Security in IoT");
        student2.setAbstractText("An investigation into blockchain-based security mechanisms for Internet of Things devices, addressing vulnerabilities and proposing novel consensus protocols.");
        student2.setSupervisorName("Dr. Johnson");
        student2.setPresentationType(PresentationType.POSTER);
        try {
            userService.registerStudent(student2);
        } catch (IllegalArgumentException e) {
            // Student already exists
        }
        
        Student student3 = new Student();
        student3.setUsername("student3");
        student3.setPassword("stud123");
        student3.setRole(UserRole.STUDENT);
        student3.setResearchTitle("Natural Language Processing for Legal Documents");
        student3.setAbstractText("Development of NLP models for automated analysis and summarization of legal documents, improving efficiency in legal research and case preparation.");
        student3.setSupervisorName("Dr. Williams");
        student3.setPresentationType(PresentationType.ORAL);
        try {
            userService.registerStudent(student3);
        } catch (IllegalArgumentException e) {
            // Student already exists
        }
        
        Student student4 = new Student();
        student4.setUsername("student4");
        student4.setPassword("stud123");
        student4.setRole(UserRole.STUDENT);
        student4.setResearchTitle("Quantum Computing Algorithms");
        student4.setAbstractText("Research on quantum algorithms for optimization problems, with applications in logistics, finance, and cryptography.");
        student4.setSupervisorName("Dr. Brown");
        student4.setPresentationType(PresentationType.POSTER);
        try {
            userService.registerStudent(student4);
        } catch (IllegalArgumentException e) {
            // Student already exists
        }
        
        // Create sample sessions
        Session session1 = null;
        Session session2 = null;
        try {
            session1 = sessionService.createSession(
                LocalDate.now().plusDays(7), 
                "Conference Hall A", 
                PresentationType.ORAL
            );
            
            session2 = sessionService.createSession(
                LocalDate.now().plusDays(8), 
                "Exhibition Hall B", 
                PresentationType.POSTER
            );
        } catch (IllegalArgumentException e) {
            // Sessions might already exist
        }
        
        // Assign presenters and evaluators to sessions
        if (session1 != null && student1.getPresenterId() != null && student3.getPresenterId() != null) {
            try {
                sessionService.assignPresenter(session1.getSessionId(), student1.getPresenterId());
                sessionService.assignPresenter(session1.getSessionId(), student3.getPresenterId());
                sessionService.assignEvaluator(session1.getSessionId(), evaluator1.getId());
                sessionService.assignEvaluator(session1.getSessionId(), evaluator2.getId());
            } catch (IllegalArgumentException e) {
                // Assignments might already exist
            }
        }
        
        if (session2 != null && student2.getPresenterId() != null && student4.getPresenterId() != null) {
            try {
                sessionService.assignPresenter(session2.getSessionId(), student2.getPresenterId());
                sessionService.assignPresenter(session2.getSessionId(), student4.getPresenterId());
                sessionService.assignEvaluator(session2.getSessionId(), evaluator1.getId());
                sessionService.assignEvaluator(session2.getSessionId(), evaluator2.getId());
            } catch (IllegalArgumentException e) {
                // Assignments might already exist
            }
        }
        
        // Create sample evaluations
        if (session1 != null && student1.getPresenterId() != null) {
            Evaluation eval1 = new Evaluation();
            eval1.setPresenterId(student1.getPresenterId());
            eval1.setEvaluatorId(evaluator1.getId());
            eval1.setSessionId(session1.getSessionId());
            eval1.setScores(new RubricScores(9, 8, 9, 8));
            eval1.setComments("Excellent research with strong methodology. Clear presentation and well-structured arguments.");
            try {
                evaluationService.submitEvaluation(eval1);
            } catch (IllegalArgumentException e) {
                // Evaluation might already exist
            }
            
            Evaluation eval2 = new Evaluation();
            eval2.setPresenterId(student1.getPresenterId());
            eval2.setEvaluatorId(evaluator2.getId());
            eval2.setSessionId(session1.getSessionId());
            eval2.setScores(new RubricScores(8, 9, 8, 9));
            eval2.setComments("Very good work. The methodology is particularly impressive and results are convincing.");
            try {
                evaluationService.submitEvaluation(eval2);
            } catch (IllegalArgumentException e) {
                // Evaluation might already exist
            }
        }
        
        if (session1 != null && student3.getPresenterId() != null) {
            Evaluation eval3 = new Evaluation();
            eval3.setPresenterId(student3.getPresenterId());
            eval3.setEvaluatorId(evaluator1.getId());
            eval3.setSessionId(session1.getSessionId());
            eval3.setScores(new RubricScores(7, 8, 7, 8));
            eval3.setComments("Good research with practical applications. Could benefit from more detailed analysis.");
            try {
                evaluationService.submitEvaluation(eval3);
            } catch (IllegalArgumentException e) {
                // Evaluation might already exist
            }
        }
        
        if (session2 != null && student2.getPresenterId() != null) {
            Evaluation eval4 = new Evaluation();
            eval4.setPresenterId(student2.getPresenterId());
            eval4.setEvaluatorId(evaluator1.getId());
            eval4.setSessionId(session2.getSessionId());
            eval4.setScores(new RubricScores(8, 8, 7, 8));
            eval4.setComments("Innovative approach to blockchain security. Well-presented poster with clear visuals.");
            try {
                evaluationService.submitEvaluation(eval4);
            } catch (IllegalArgumentException e) {
                // Evaluation might already exist
            }
        }
        
        if (session2 != null && student4.getPresenterId() != null) {
            Evaluation eval5 = new Evaluation();
            eval5.setPresenterId(student4.getPresenterId());
            eval5.setEvaluatorId(evaluator2.getId());
            eval5.setSessionId(session2.getSessionId());
            eval5.setScores(new RubricScores(9, 9, 8, 8));
            eval5.setComments("Outstanding work on quantum algorithms. Excellent theoretical foundation and practical implications.");
            try {
                evaluationService.submitEvaluation(eval5);
            } catch (IllegalArgumentException e) {
                // Evaluation might already exist
            }
        }
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
