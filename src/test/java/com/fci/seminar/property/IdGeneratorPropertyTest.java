package com.fci.seminar.property;

import java.util.HashSet;
import java.util.Set;

import com.fci.seminar.util.IdGenerator;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for IdGenerator uniqueness.
 * Feature: seminar-management-system, Property 3: Unique Presenter ID Generation
 * Validates: Requirements 2.5
 */
class IdGeneratorPropertyTest {

    /**
     * Property 3: Unique Presenter ID Generation
     * For any number of registered students, all generated presenter IDs SHALL be unique
     * (no two students share the same presenter ID).
     * Validates: Requirements 2.5
     */
    @Property(tries = 100)
    void allGeneratedPresenterIdsAreUnique(@ForAll("idCount") int count) {
        Set<String> generatedIds = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String id = IdGenerator.generatePresenterId();
            assert !generatedIds.contains(id) : 
                "Generated presenter ID should be unique, but found duplicate: " + id;
            generatedIds.add(id);
        }
        
        assert generatedIds.size() == count : 
            "All generated IDs should be unique";
    }

    /**
     * Property: All generated session IDs are unique.
     * Validates: Requirements 4.2
     */
    @Property(tries = 100)
    void allGeneratedSessionIdsAreUnique(@ForAll("idCount") int count) {
        Set<String> generatedIds = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String id = IdGenerator.generateSessionId();
            assert !generatedIds.contains(id) : 
                "Generated session ID should be unique, but found duplicate: " + id;
            generatedIds.add(id);
        }
        
        assert generatedIds.size() == count : 
            "All generated IDs should be unique";
    }

    /**
     * Property: All generated evaluation IDs are unique.
     * Validates: Requirements 6.5
     */
    @Property(tries = 100)
    void allGeneratedEvaluationIdsAreUnique(@ForAll("idCount") int count) {
        Set<String> generatedIds = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String id = IdGenerator.generateEvaluationId();
            assert !generatedIds.contains(id) : 
                "Generated evaluation ID should be unique, but found duplicate: " + id;
            generatedIds.add(id);
        }
        
        assert generatedIds.size() == count : 
            "All generated IDs should be unique";
    }

    /**
     * Property: All generated user IDs are unique.
     * Validates: Requirements 1.2
     */
    @Property(tries = 100)
    void allGeneratedUserIdsAreUnique(@ForAll("idCount") int count) {
        Set<String> generatedIds = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String id = IdGenerator.generateUserId();
            assert !generatedIds.contains(id) : 
                "Generated user ID should be unique, but found duplicate: " + id;
            generatedIds.add(id);
        }
        
        assert generatedIds.size() == count : 
            "All generated IDs should be unique";
    }

    /**
     * Property: Generated IDs have correct prefix format.
     * Validates: Requirements 2.5
     */
    @Property(tries = 100)
    void generatedIdsHaveCorrectPrefix() {
        String presenterId = IdGenerator.generatePresenterId();
        String sessionId = IdGenerator.generateSessionId();
        String evaluationId = IdGenerator.generateEvaluationId();
        String userId = IdGenerator.generateUserId();
        
        assert presenterId.startsWith("P-") : 
            "Presenter ID should start with 'P-'";
        assert sessionId.startsWith("S-") : 
            "Session ID should start with 'S-'";
        assert evaluationId.startsWith("E-") : 
            "Evaluation ID should start with 'E-'";
        assert userId.startsWith("U-") : 
            "User ID should start with 'U-'";
    }

    @Provide
    Arbitrary<Integer> idCount() {
        return Arbitraries.integers().between(10, 100);
    }
}
