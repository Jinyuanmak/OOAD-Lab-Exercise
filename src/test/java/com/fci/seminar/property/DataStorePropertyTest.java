package com.fci.seminar.property;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fci.seminar.model.Award;
import com.fci.seminar.model.AwardType;
import com.fci.seminar.model.Evaluation;
import com.fci.seminar.model.PosterBoard;
import com.fci.seminar.model.PresentationType;
import com.fci.seminar.model.RubricScores;
import com.fci.seminar.model.Session;
import com.fci.seminar.model.Student;
import com.fci.seminar.service.DataStore;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

/**
 * Property-based tests for DataStore persistence.
 * Feature: seminar-management-system, Property 10: Data Persistence Round-Trip
 * Validates: Requirements 10.1, 10.2, 10.4
 */
class DataStorePropertyTest {

    /**
     * Property 10: Data Persistence Round-Trip
     * For any DataStore object, serializing to file then deserializing SHALL produce
     * an equivalent DataStore with all users, sessions, evaluations, and awards intact.
     * Validates: Requirements 10.1, 10.2, 10.4
     */
    @Property(tries = 100)
    void dataStoreRoundTripPreservesData(
            @ForAll("validDataStore") DataStore original) throws IOException {
        
        File tempFile = Files.createTempFile("datastore_test_", ".dat").toFile();
        tempFile.deleteOnExit();
        
        try {
            // Save the DataStore
            original.save(tempFile.getAbsolutePath());
            
            // Load it back
            DataStore loaded = DataStore.load(tempFile.getAbsolutePath());
            
            // Verify all data is preserved
            assert original.getUsers().equals(loaded.getUsers()) :
                "Users should be preserved after round-trip";
            assert original.getSessions().equals(loaded.getSessions()) :
                "Sessions should be preserved after round-trip";
            assert original.getEvaluations().equals(loaded.getEvaluations()) :
                "Evaluations should be preserved after round-trip";
            assert original.getPosterBoards().equals(loaded.getPosterBoards()) :
                "PosterBoards should be preserved after round-trip";
            assert original.getAwards().equals(loaded.getAwards()) :
                "Awards should be preserved after round-trip";
        } finally {
            tempFile.delete();
        }
    }

    @Provide
    Arbitrary<DataStore> validDataStore() {
        return Combinators.combine(
            students().list().ofMaxSize(5),
            sessions().list().ofMaxSize(3),
            evaluations().list().ofMaxSize(5),
            posterBoards().list().ofMaxSize(3),
            awards().list().ofMaxSize(3)
        ).as((studentList, sessionList, evalList, boardList, awardList) -> {
            DataStore ds = new DataStore();
            studentList.forEach(ds::addUser);
            sessionList.forEach(ds::addSession);
            evalList.forEach(ds::addEvaluation);
            boardList.forEach(ds::addPosterBoard);
            awardList.forEach(ds::addAward);
            return ds;
        });
    }

    @Provide
    Arbitrary<Student> students() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(4).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
            Arbitraries.of(PresentationType.values())
        ).as((id, username, password, title, abstractText, supervisor, type) -> {
            Student s = new Student("U-" + id, username, password);
            s.setResearchTitle(title);
            s.setAbstractText(abstractText);
            s.setSupervisorName(supervisor);
            s.setPresentationType(type);
            s.setPresenterId("P-" + id);
            return s;
        });
    }

    @Provide
    Arbitrary<Session> sessions() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            Arbitraries.integers().between(2024, 2030),
            Arbitraries.integers().between(1, 12),
            Arbitraries.integers().between(1, 28),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20),
            Arbitraries.of(PresentationType.values())
        ).as((id, year, month, day, venue, type) -> {
            return new Session("S-" + id, LocalDate.of(year, month, day), venue, type);
        });
    }

    @Provide
    Arbitrary<Evaluation> evaluations() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            rubricScores(),
            Arbitraries.strings().alpha().ofMaxLength(100)
        ).as((evalId, presenterId, evaluatorId, sessionId, scores, comments) -> {
            Evaluation e = new Evaluation();
            e.setEvaluationId("E-" + evalId);
            e.setPresenterId("P-" + presenterId);
            e.setEvaluatorId("U-" + evaluatorId);
            e.setSessionId("S-" + sessionId);
            e.setScores(scores);
            e.setComments(comments);
            e.setTimestamp(LocalDateTime.of(2025, 1, 1, 10, 0));
            return e;
        });
    }

    @Provide
    Arbitrary<RubricScores> rubricScores() {
        return Combinators.combine(
            Arbitraries.integers().between(1, 10),
            Arbitraries.integers().between(1, 10),
            Arbitraries.integers().between(1, 10),
            Arbitraries.integers().between(1, 10)
        ).as(RubricScores::new);
    }

    @Provide
    Arbitrary<PosterBoard> posterBoards() {
        return Combinators.combine(
            Arbitraries.integers().between(1, 999),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10)
        ).as((boardNum, presenterId, sessionId) -> {
            return new PosterBoard(
                String.format("B%03d", boardNum),
                "P-" + presenterId,
                "S-" + sessionId
            );
        });
    }

    @Provide
    Arbitrary<Award> awards() {
        return Combinators.combine(
            Arbitraries.of(AwardType.values()),
            Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
            Arbitraries.doubles().between(0.0, 40.0)
        ).as((type, winnerId, score) -> new Award(type, "P-" + winnerId, score));
    }
}
