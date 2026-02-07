package com.fci.seminar.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fci.seminar.util.FileStorageException;
import com.fci.seminar.util.FileStorageException.ErrorType;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

/**
 * Property-based tests for FileStorageService.
 * Uses jqwik for property-based testing with random input generation.
 * 
 * Feature: centralized-file-storage
 * Properties: 1 (Directory Structure), 2 (File Type Validation)
 */
class FileStorageServicePropertyTest {
    
    private static final FileStorageService fileStorageService = FileStorageService.getInstance();
    private static final Set<String> SUPPORTED_EXTENSIONS = 
        Set.of(".pdf", ".jpg", ".jpeg", ".png", ".gif", ".txt");
    private static final Set<String> UNSUPPORTED_EXTENSIONS = 
        Set.of(".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".zip", ".rar", ".exe", ".mp4");
    
    /**
     * Property 1: Directory Structure Consistency
     * 
     * For any presenter ID, when a directory is created, the system should create
     * the directory structure following the pattern uploads/presentations/{presenter_id}/
     * 
     * Validates: Requirements 1.1, 1.3
     */
    @Property(tries = 100)
    void property1_directoryStructureConsistency(
            @ForAll("presenterId") String presenterId) throws Exception {
        
        // Create presenter directory
        Path presenterDir = fileStorageService.createPresenterDirectory(presenterId);
        
        // Verify directory exists
        assertTrue(Files.exists(presenterDir), 
            "Presenter directory should exist after creation");
        assertTrue(Files.isDirectory(presenterDir), 
            "Presenter path should be a directory");
        
        // Verify directory structure follows pattern
        String expectedPattern = "uploads" + File.separator + "presentations" + File.separator + presenterId;
        assertTrue(presenterDir.toString().endsWith(expectedPattern) || 
                   presenterDir.toString().replace("/", File.separator).endsWith(expectedPattern),
            "Directory should follow pattern uploads/presentations/{presenter_id}/");
        
        // Test idempotency - calling again should not fail
        Path presenterDir2 = fileStorageService.createPresenterDirectory(presenterId);
        assertEquals(presenterDir, presenterDir2, 
            "Multiple calls should return the same directory path");
        
        // Clean up
        Files.deleteIfExists(presenterDir);
    }
    
    /**
     * Property 1: Directory Structure Consistency - Only Created When Needed
     * 
     * Verifies that directories are only created when they don't already exist.
     */
    @Property(tries = 50)
    void property1_directoryCreatedOnlyWhenNeeded(
            @ForAll("presenterId") String presenterId) throws Exception {
        
        Path presenterDir = Paths.get(fileStorageService.getBaseUploadDir(), presenterId);
        
        // Ensure directory doesn't exist initially
        if (Files.exists(presenterDir)) {
            Files.delete(presenterDir);
        }
        
        assertFalse(Files.exists(presenterDir), 
            "Directory should not exist before creation");
        
        // Create directory
        fileStorageService.createPresenterDirectory(presenterId);
        
        assertTrue(Files.exists(presenterDir), 
            "Directory should exist after creation");
        
        // Clean up
        Files.deleteIfExists(presenterDir);
    }
    
    /**
     * Property 2: File Type Validation - Supported Extensions
     * 
     * For any file with a supported extension, the system should accept the file.
     * 
     * Validates: Requirements 2.1, 4.1, 4.2, 4.3, 4.4
     */
    @Property(tries = 100)
    void property2_fileTypeValidation_supportedExtensions(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String filename,
            @ForAll("supportedExtension") String extension) throws IOException {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create a file with supported extension
        File testFile = tempDirectory.resolve(filename + extension).toFile();
        testFile.createNewFile();
        
        // Verify the file is accepted
        assertTrue(fileStorageService.isFileTypeSupported(testFile),
            "File with supported extension " + extension + " should be accepted");
        
        // Clean up
        testFile.delete();
        Files.deleteIfExists(tempDirectory);
    }
    
    /**
     * Property 2: File Type Validation - Unsupported Extensions
     * 
     * For any file with an unsupported extension, the system should reject the file.
     */
    @Property(tries = 100)
    void property2_fileTypeValidation_unsupportedExtensions(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String filename,
            @ForAll("unsupportedExtension") String extension) throws IOException {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create a file with unsupported extension
        File testFile = tempDirectory.resolve(filename + extension).toFile();
        testFile.createNewFile();
        
        // Verify the file is rejected
        assertFalse(fileStorageService.isFileTypeSupported(testFile),
            "File with unsupported extension " + extension + " should be rejected");
        
        // Clean up
        testFile.delete();
        Files.deleteIfExists(tempDirectory);
    }
    
    /**
     * Property 2: File Type Validation - Case Insensitive
     * 
     * For any file with a supported extension in any case (upper, lower, mixed),
     * the system should accept the file.
     */
    @Property(tries = 100)
    void property2_fileTypeValidation_caseInsensitive(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String filename,
            @ForAll("supportedExtension") String extension,
            @ForAll("caseVariation") CaseVariation caseVariation) throws IOException {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Apply case variation to extension
        String variedExtension = applyCaseVariation(extension, caseVariation);
        
        // Create a file with case-varied extension
        File testFile = tempDirectory.resolve(filename + variedExtension).toFile();
        testFile.createNewFile();
        
        // Verify the file is accepted regardless of case
        assertTrue(fileStorageService.isFileTypeSupported(testFile),
            "File with extension " + variedExtension + " should be accepted (case-insensitive)");
        
        // Clean up
        testFile.delete();
        Files.deleteIfExists(tempDirectory);
    }
    
    /**
     * Property 2: File Type Validation - No Extension
     * 
     * For any file without an extension, the system should reject the file.
     */
    @Property(tries = 50)
    void property2_fileTypeValidation_noExtension(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String filename) throws IOException {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create a file without extension
        File testFile = tempDirectory.resolve(filename).toFile();
        testFile.createNewFile();
        
        // Verify the file is rejected
        assertFalse(fileStorageService.isFileTypeSupported(testFile),
            "File without extension should be rejected");
        
        // Clean up
        testFile.delete();
        Files.deleteIfExists(tempDirectory);
    }
    
    /**
     * Property 2: File Type Validation - Multiple Dots in Filename
     * 
     * For any file with multiple dots in the filename, the system should validate
     * based on the last extension.
     */
    @Property(tries = 50)
    void property2_fileTypeValidation_multipleDotsInFilename(
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String part1,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String part2,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String part3,
            @ForAll("supportedExtension") String extension) throws IOException {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create filename with multiple dots
        String filename = part1 + "." + part2 + "." + part3 + extension;
        File testFile = tempDirectory.resolve(filename).toFile();
        testFile.createNewFile();
        
        // Verify the file is accepted (should use last extension)
        assertTrue(fileStorageService.isFileTypeSupported(testFile),
            "File with multiple dots should be validated by last extension");
        
        // Clean up
        testFile.delete();
        Files.deleteIfExists(tempDirectory);
    }
    
    /**
     * Property 3: File Copy Preservation
     * 
     * For any valid file uploaded by a presenter, the file should be copied to centralized storage
     * with the original filename preserved, and the original file should remain in the student's local system.
     * 
     * Validates: Requirements 2.2, 2.3, 2.4
     */
    @Property(tries = 100)
    void property3_fileCopyPreservation(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create a source file with some content
        File sourceFile = tempDirectory.resolve(filename + extension).toFile();
        Files.writeString(sourceFile.toPath(), "Test content for " + filename);
        
        assertTrue(sourceFile.exists(), "Source file should exist before upload");
        long originalSize = sourceFile.length();
        
        // Upload the file
        String storagePath = fileStorageService.uploadFile(sourceFile, presenterId);
        
        // Verify original file still exists (copy, not move)
        assertTrue(sourceFile.exists(), "Original file should still exist after upload");
        
        // Verify file was copied to centralized storage
        File copiedFile = new File(storagePath);
        assertTrue(copiedFile.exists(), "Copied file should exist in centralized storage");
        
        // Verify original filename is preserved
        assertTrue(copiedFile.getName().equals(filename + extension),
            "Original filename should be preserved");
        
        // Verify file content was copied correctly
        assertEquals(originalSize, copiedFile.length(),
            "Copied file should have same size as original");
        
        // Clean up
        sourceFile.delete();
        copiedFile.delete();
        Files.deleteIfExists(tempDirectory);
        Files.deleteIfExists(Paths.get(fileStorageService.getBaseUploadDir(), presenterId));
    }
    
    /**
     * Property 5: Relative Path Generation
     * 
     * For any successfully uploaded file, the generated storage path should be relative
     * to the project root and use forward slashes for cross-platform compatibility.
     * 
     * Validates: Requirements 3.1, 3.3
     */
    @Property(tries = 100)
    void property5_relativePathGeneration(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create a source file
        File sourceFile = tempDirectory.resolve(filename + extension).toFile();
        Files.writeString(sourceFile.toPath(), "Test content");
        
        // Upload the file
        String storagePath = fileStorageService.uploadFile(sourceFile, presenterId);
        
        // Verify path is relative (doesn't start with / or drive letter)
        assertFalse(storagePath.startsWith("/"), 
            "Storage path should not start with /");
        assertFalse(storagePath.matches("^[A-Za-z]:.*"), 
            "Storage path should not start with drive letter");
        
        // Verify path uses forward slashes
        assertFalse(storagePath.contains("\\"), 
            "Storage path should use forward slashes, not backslashes");
        
        // Verify path follows expected pattern
        String expectedPattern = "uploads/presentations/" + presenterId + "/" + filename + extension;
        assertEquals(expectedPattern, storagePath,
            "Storage path should follow pattern uploads/presentations/{presenter_id}/{filename}");
        
        // Clean up
        sourceFile.delete();
        new File(storagePath).delete();
        Files.deleteIfExists(tempDirectory);
        Files.deleteIfExists(Paths.get(fileStorageService.getBaseUploadDir(), presenterId));
    }
    
    /**
     * Property 9: Non-Existent File Rejection
     * 
     * For any file path that does not exist on the file system, attempting to upload
     * that file should result in an error and no changes to the storage system or database.
     * 
     * Validates: Requirements 5.1
     */
    @Property(tries = 50)
    void property9_nonExistentFileRejection(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        // Create a file reference that doesn't exist
        File nonExistentFile = new File("nonexistent_dir/" + filename + extension);
        
        assertFalse(nonExistentFile.exists(), "File should not exist");
        
        // Attempt to upload should throw FILE_NOT_FOUND exception
        FileStorageException exception = assertThrows(FileStorageException.class, () -> {
            fileStorageService.uploadFile(nonExistentFile, presenterId);
        });
        
        assertEquals(ErrorType.FILE_NOT_FOUND, exception.getErrorType(),
            "Exception should be FILE_NOT_FOUND type");
        
        // Verify no file was created in storage
        Path presenterDir = Paths.get(fileStorageService.getBaseUploadDir(), presenterId);
        if (Files.exists(presenterDir)) {
            // If directory exists, verify it's empty or doesn't contain our file
            File[] files = presenterDir.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    assertFalse(file.getName().equals(filename + extension),
                        "File should not exist in storage after failed upload");
                }
            }
        }
    }
    
    /**
     * Property 4: Same-Name File Overwrite
     * 
     * For any presenter who uploads a file with the same filename twice, only one copy
     * of that file should exist in their centralized storage directory after the second upload.
     * 
     * Validates: Requirements 2.5, 6.1
     */
    @Property(tries = 100)
    void property4_sameNameFileOverwrite(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create first file with some content
        File firstFile = tempDirectory.resolve(filename + extension).toFile();
        Files.writeString(firstFile.toPath(), "First version of content");
        
        // Upload first file
        String firstStoragePath = fileStorageService.uploadFile(firstFile, presenterId);
        
        // Verify first file exists in storage
        File storedFile1 = new File(firstStoragePath);
        assertTrue(storedFile1.exists(), "First uploaded file should exist");
        
        // Create second file with same name but different content
        File secondFile = tempDirectory.resolve(filename + extension).toFile();
        Files.writeString(secondFile.toPath(), "Second version of content - updated");
        
        // Upload second file (same name)
        String secondStoragePath = fileStorageService.uploadFile(secondFile, presenterId);
        
        // Verify paths are the same (same filename)
        assertEquals(firstStoragePath, secondStoragePath,
            "Storage paths should be the same for same filename");
        
        // Verify only one file exists in presenter directory
        Path presenterDir = Paths.get(fileStorageService.getBaseUploadDir(), presenterId);
        File[] files = presenterDir.toFile().listFiles();
        
        assertNotNull(files, "Presenter directory should contain files");
        assertEquals(1, files.length,
            "Only one file should exist after uploading same filename twice");
        
        // Verify the file has the updated content (second version)
        File storedFile2 = new File(secondStoragePath);
        assertTrue(storedFile2.exists(), "Second uploaded file should exist");
        String storedContent = Files.readString(storedFile2.toPath());
        assertEquals("Second version of content - updated", storedContent,
            "File should contain the updated content from second upload");
        
        // Clean up
        firstFile.delete();
        secondFile.delete();
        storedFile2.delete();
        Files.deleteIfExists(tempDirectory);
        Files.deleteIfExists(presenterDir);
    }
    
    /**
     * Property 8: Old File Cleanup on Different Filename
     * 
     * For any presenter who uploads a file with filename A, then uploads a file with filename B,
     * only file B should exist in their centralized storage directory.
     * 
     * Validates: Requirements 6.2
     */
    @Property(tries = 100)
    void property8_oldFileCleanupOnDifferentFilename(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String filenameA,
            @ForAll @AlphaChars @StringLength(min = 1, max = 10) String filenameB,
            @ForAll("supportedExtension") String extensionA,
            @ForAll("supportedExtension") String extensionB) throws Exception {
        
        // Skip if filenames are the same (case-insensitive, since Windows treats them as same)
        String fullFilenameA = (filenameA + extensionA).toLowerCase();
        String fullFilenameB = (filenameB + extensionB).toLowerCase();
        if (fullFilenameA.equals(fullFilenameB)) {
            return;
        }
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create and upload file A
        File fileA = tempDirectory.resolve(filenameA + extensionA).toFile();
        Files.writeString(fileA.toPath(), "Content of file A");
        String storagePathA = fileStorageService.uploadFile(fileA, presenterId);
        
        // Verify file A exists in storage
        File storedFileA = new File(storagePathA);
        assertTrue(storedFileA.exists(), "File A should exist after first upload");
        
        // Create and upload file B (different filename)
        File fileB = tempDirectory.resolve(filenameB + extensionB).toFile();
        Files.writeString(fileB.toPath(), "Content of file B");
        String storagePathB = fileStorageService.uploadFile(fileB, presenterId);
        
        // Verify file B exists in storage
        File storedFileB = new File(storagePathB);
        assertTrue(storedFileB.exists(), "File B should exist after second upload");
        
        // Verify file A no longer exists (was cleaned up)
        assertFalse(storedFileA.exists(), 
            "File A should be deleted after uploading file B with different name");
        
        // Verify only one file exists in presenter directory
        Path presenterDir = Paths.get(fileStorageService.getBaseUploadDir(), presenterId);
        File[] files = presenterDir.toFile().listFiles();
        
        assertNotNull(files, "Presenter directory should contain files");
        assertEquals(1, files.length,
            "Only one file (file B) should exist after uploading different filename");
        
        // Verify the remaining file is file B
        assertEquals(filenameB + extensionB, files[0].getName(),
            "The remaining file should be file B");
        
        // Clean up
        fileA.delete();
        fileB.delete();
        storedFileB.delete();
        Files.deleteIfExists(tempDirectory);
        Files.deleteIfExists(presenterDir);
    }
    
    /**
     * Property 7: Filename Isolation by Presenter
     * 
     * For any two different presenters who upload files with the same filename,
     * both files should exist in the system without conflict (stored in separate presenter directories).
     * 
     * Validates: Requirements 1.4
     */
    @Property(tries = 100)
    void property7_filenameIsolationByPresenter(
            @ForAll("presenterId") String presenterIdA,
            @ForAll("presenterId") String presenterIdB,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        // Skip if presenter IDs are the same (case-insensitive, since Windows treats them as same)
        if (presenterIdA.equalsIgnoreCase(presenterIdB)) {
            return;
        }
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create and upload file for presenter A
        File fileA = tempDirectory.resolve(filename + extension).toFile();
        Files.writeString(fileA.toPath(), "Content from presenter A");
        String storagePathA = fileStorageService.uploadFile(fileA, presenterIdA);
        
        // Verify file A exists in storage
        File storedFileA = new File(storagePathA);
        assertTrue(storedFileA.exists(), "File for presenter A should exist");
        
        // Create and upload file with same name for presenter B
        File fileB = tempDirectory.resolve(filename + extension).toFile();
        Files.writeString(fileB.toPath(), "Content from presenter B");
        String storagePathB = fileStorageService.uploadFile(fileB, presenterIdB);
        
        // Verify file B exists in storage
        File storedFileB = new File(storagePathB);
        assertTrue(storedFileB.exists(), "File for presenter B should exist");
        
        // Verify file A still exists (no conflict)
        assertTrue(storedFileA.exists(), 
            "File for presenter A should still exist after presenter B uploads same filename");
        
        // Verify storage paths are different (different presenter directories)
        assertFalse(storagePathA.equals(storagePathB),
            "Storage paths should be different for different presenters");
        
        // Verify both files have correct content
        String contentA = Files.readString(storedFileA.toPath());
        String contentB = Files.readString(storedFileB.toPath());
        assertEquals("Content from presenter A", contentA,
            "File A should have presenter A's content");
        assertEquals("Content from presenter B", contentB,
            "File B should have presenter B's content");
        
        // Verify files are in separate directories
        Path presenterDirA = Paths.get(fileStorageService.getBaseUploadDir(), presenterIdA);
        Path presenterDirB = Paths.get(fileStorageService.getBaseUploadDir(), presenterIdB);
        
        assertTrue(Files.exists(presenterDirA), "Presenter A directory should exist");
        assertTrue(Files.exists(presenterDirB), "Presenter B directory should exist");
        assertFalse(presenterDirA.equals(presenterDirB),
            "Presenter directories should be different");
        
        // Clean up
        fileA.delete();
        fileB.delete();
        storedFileA.delete();
        storedFileB.delete();
        Files.deleteIfExists(tempDirectory);
        Files.deleteIfExists(presenterDirA);
        Files.deleteIfExists(presenterDirB);
    }
    
    // Arbitraries (data generators)
    
    @Provide
    Arbitrary<String> supportedExtension() {
        return Arbitraries.of(SUPPORTED_EXTENSIONS);
    }
    
    @Provide
    Arbitrary<String> unsupportedExtension() {
        return Arbitraries.of(UNSUPPORTED_EXTENSIONS);
    }
    
    @Provide
    Arbitrary<CaseVariation> caseVariation() {
        return Arbitraries.of(CaseVariation.LOWERCASE, CaseVariation.UPPERCASE, CaseVariation.MIXED_CASE);
    }
    
    @Provide
    Arbitrary<String> presenterId() {
        // Generate presenter IDs in the format P-xxxxxxxx
        return Arbitraries.strings()
            .alpha().numeric()
            .ofLength(8)
            .map(id -> "P-" + id);
    }
    
    // Helper enum for case variations
    enum CaseVariation {
        LOWERCASE,
        UPPERCASE,
        MIXED_CASE
    }
    
    // Helper method to apply case variation
    private String applyCaseVariation(String extension, CaseVariation variation) {
        return switch (variation) {
            case LOWERCASE -> extension.toLowerCase();
            case UPPERCASE -> extension.toUpperCase();
            case MIXED_CASE -> {
                // Mix case: alternate between upper and lower
                StringBuilder mixed = new StringBuilder();
                for (int i = 0; i < extension.length(); i++) {
                    char c = extension.charAt(i);
                    mixed.append(i % 2 == 0 ? Character.toUpperCase(c) : Character.toLowerCase(c));
                }
                yield mixed.toString();
            }
        };
    }
    
    /**
     * Property 10: Atomic Operation on Failure
     * 
     * For any file upload operation that fails (for any reason), the system state should remain
     * unchanged with no partial updates to either the file system or database.
     * 
     * Validates: Requirements 5.4
     */
    @Property(tries = 50)
    void property10_atomicOperationOnFailure(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Test 1: Non-existent file - should not create presenter directory
        File nonExistentFile = new File(tempDirectory.toFile(), "nonexistent_" + filename + extension);
        
        // Verify presenter directory doesn't exist before
        Path presenterDir = Paths.get(fileStorageService.getBaseUploadDir(), presenterId);
        boolean dirExistedBefore = Files.exists(presenterDir);
        
        // Attempt upload with non-existent file
        try {
            fileStorageService.uploadFile(nonExistentFile, presenterId);
            // Should not reach here
            throw new AssertionError("Upload should have failed for non-existent file");
        } catch (FileStorageException e) {
            // Expected exception
            assertEquals(ErrorType.FILE_NOT_FOUND, e.getErrorType(),
                "Should throw FILE_NOT_FOUND exception");
        }
        
        // Verify no state changes occurred
        if (!dirExistedBefore) {
            assertFalse(Files.exists(presenterDir),
                "Presenter directory should not be created after failed upload");
        }
        
        // Test 2: Invalid file type - should not create presenter directory or copy file
        File invalidTypeFile = tempDirectory.resolve(filename + ".exe").toFile();
        invalidTypeFile.createNewFile();
        
        presenterDir = Paths.get(fileStorageService.getBaseUploadDir(), presenterId);
        dirExistedBefore = Files.exists(presenterDir);
        
        // Attempt upload with invalid file type
        try {
            fileStorageService.uploadFile(invalidTypeFile, presenterId);
            // Should not reach here
            throw new AssertionError("Upload should have failed for invalid file type");
        } catch (FileStorageException e) {
            // Expected exception
            assertEquals(ErrorType.INVALID_FILE_TYPE, e.getErrorType(),
                "Should throw INVALID_FILE_TYPE exception");
        }
        
        // Verify no state changes occurred
        if (!dirExistedBefore) {
            assertFalse(Files.exists(presenterDir),
                "Presenter directory should not be created after failed upload");
        }
        
        // Verify no files were copied
        if (Files.exists(presenterDir)) {
            File[] files = presenterDir.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    assertFalse(file.getName().equals(filename + ".exe"),
                        "Invalid file should not be copied to storage");
                }
            }
        }
        
        // Clean up
        invalidTypeFile.delete();
        Files.deleteIfExists(tempDirectory);
        if (Files.exists(presenterDir)) {
            File[] files = presenterDir.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            Files.deleteIfExists(presenterDir);
        }
    }
    
    /**
     * Property 11: Error Logging
     * 
     * For any file operation that results in an error, an error log entry should be created
     * with details about the failure.
     * 
     * Validates: Requirements 5.5
     */
    @Property(tries = 50)
    void property11_errorLogging(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        Path logFile = Paths.get("logs/file-storage-errors.log");
        
        // Get initial log file size (or 0 if doesn't exist)
        long initialLogSize = Files.exists(logFile) ? Files.size(logFile) : 0;
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Test 1: Trigger FILE_NOT_FOUND error
        File nonExistentFile = new File(tempDirectory.toFile(), "nonexistent_" + filename + extension);
        
        try {
            fileStorageService.uploadFile(nonExistentFile, presenterId);
            // Should not reach here
            throw new AssertionError("Upload should have failed");
        } catch (FileStorageException e) {
            // Expected exception
        }
        
        // Verify log file was updated
        assertTrue(Files.exists(logFile), "Error log file should exist after error");
        long afterFirstErrorSize = Files.size(logFile);
        assertTrue(afterFirstErrorSize > initialLogSize,
            "Log file should grow after error (initial: " + initialLogSize + ", after: " + afterFirstErrorSize + ")");
        
        // Test 2: Trigger INVALID_FILE_TYPE error
        File invalidTypeFile = tempDirectory.resolve(filename + ".exe").toFile();
        invalidTypeFile.createNewFile();
        
        long beforeSecondError = Files.size(logFile);
        
        try {
            fileStorageService.uploadFile(invalidTypeFile, presenterId);
            // Should not reach here
            throw new AssertionError("Upload should have failed");
        } catch (FileStorageException e) {
            // Expected exception
        }
        
        // Verify log file was updated again
        long afterSecondErrorSize = Files.size(logFile);
        assertTrue(afterSecondErrorSize > beforeSecondError,
            "Log file should grow after second error (before: " + beforeSecondError + ", after: " + afterSecondErrorSize + ")");
        
        // Verify log contains error details
        String logContent = Files.readString(logFile);
        assertTrue(logContent.contains("ERROR:"), "Log should contain ERROR marker");
        assertTrue(logContent.contains("FILE_NOT_FOUND") || logContent.contains("INVALID_FILE_TYPE"),
            "Log should contain error type information");
        
        // Clean up
        invalidTypeFile.delete();
        Files.deleteIfExists(tempDirectory);
        
        // Clean up presenter directory if created
        Path presenterDir = Paths.get(fileStorageService.getBaseUploadDir(), presenterId);
        if (Files.exists(presenterDir)) {
            File[] files = presenterDir.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            Files.deleteIfExists(presenterDir);
        }
    }
    
    /**
     * Property 12: Storage Path Resolution
     * 
     * For any relative storage path stored in the database, resolving that path should produce
     * an absolute file path pointing to the correct location in centralized storage.
     * 
     * Validates: Requirements 7.1
     */
    @Property(tries = 100)
    void property12_storagePathResolution(
            @ForAll("presenterId") String presenterId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String filename,
            @ForAll("supportedExtension") String extension) throws Exception {
        
        Path tempDirectory = Files.createTempDirectory("file-storage-test");
        
        // Create and upload a file
        File sourceFile = tempDirectory.resolve(filename + extension).toFile();
        Files.writeString(sourceFile.toPath(), "Test content");
        
        String storagePath = fileStorageService.uploadFile(sourceFile, presenterId);
        
        // Resolve the storage path
        File resolvedFile = fileStorageService.resolveStoragePath(storagePath);
        
        // Verify the resolved file is not null
        assertNotNull(resolvedFile, "Resolved file should not be null");
        
        // Verify the resolved file is absolute
        assertTrue(resolvedFile.isAbsolute(), "Resolved file should be absolute");
        
        // Verify the resolved file exists
        assertTrue(resolvedFile.exists(), "Resolved file should exist");
        
        // Verify the resolved file points to the correct location
        String resolvedPath = resolvedFile.getAbsolutePath();
        assertTrue(resolvedPath.contains(presenterId), 
            "Resolved path should contain presenter ID");
        assertTrue(resolvedPath.endsWith(filename + extension),
            "Resolved path should end with the filename");
        
        // Verify we can read the file content
        String content = Files.readString(resolvedFile.toPath());
        assertEquals("Test content", content, "File content should match");
        
        // Clean up
        sourceFile.delete();
        resolvedFile.delete();
        Files.deleteIfExists(tempDirectory);
        Files.deleteIfExists(Paths.get(fileStorageService.getBaseUploadDir(), presenterId));
    }
    
    /**
     * Property 15: Backward Compatibility with Absolute Paths
     * 
     * For any absolute file path (legacy format), the system should correctly identify it
     * as absolute and handle it appropriately during the transition period.
     * 
     * Validates: Requirements 7.4
     */
    @Property(tries = 100)
    void property15_backwardCompatibilityWithAbsolutePaths(
            @ForAll("absolutePath") String absolutePath) {
        
        // Verify the path is correctly identified as absolute
        assertTrue(fileStorageService.isAbsolutePath(absolutePath),
            "Path should be identified as absolute: " + absolutePath);
        
        // Verify relative paths are not identified as absolute
        String relativePath = "uploads/presentations/P-12345678/file.pdf";
        assertFalse(fileStorageService.isAbsolutePath(relativePath),
            "Relative path should not be identified as absolute");
        
        // Verify null and empty paths are not identified as absolute
        assertFalse(fileStorageService.isAbsolutePath(null),
            "Null path should not be identified as absolute");
        assertFalse(fileStorageService.isAbsolutePath(""),
            "Empty path should not be identified as absolute");
    }
    
    // Additional arbitraries for new properties
    
    @Provide
    Arbitrary<String> absolutePath() {
        return Arbitraries.oneOf(
            // Windows absolute paths
            Arbitraries.strings()
                .alpha()
                .ofLength(1)
                .map(drive -> drive.toUpperCase() + ":\\Users\\Student\\Documents\\file.pdf"),
            
            // Unix absolute paths
            Arbitraries.strings()
                .alpha().numeric()
                .ofMinLength(5).ofMaxLength(20)
                .map(path -> "/home/student/" + path + "/file.pdf"),
            
            // Windows UNC paths
            Arbitraries.strings()
                .alpha()
                .ofMinLength(5).ofMaxLength(15)
                .map(server -> "\\\\server\\" + server + "\\file.pdf")
        );
    }
}
