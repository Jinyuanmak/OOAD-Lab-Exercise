# Implementation Plan: Centralized File Storage

## Overview

This implementation plan breaks down the centralized file storage feature into discrete, incremental coding tasks. Each task builds on previous work, starting with core infrastructure, then adding file operations, integrating with existing components, and finally implementing comprehensive testing. The approach ensures that functionality is validated early and often through automated tests.

## Tasks

- [x] 1. Create FileStorageException class
  - Create `FileStorageException.java` in the appropriate package
  - Implement `ErrorType` enum with all error categories (FILE_NOT_FOUND, INVALID_FILE_TYPE, PERMISSION_DENIED, INSUFFICIENT_SPACE, COPY_FAILED, DIRECTORY_CREATION_FAILED)
  - Implement constructors for message, error type, and optional cause
  - Implement `getUserFriendlyMessage()` method to return user-friendly error messages
  - _Requirements: 5.1, 5.2, 5.3, 5.5_

- [x] 1.1 Write unit tests for FileStorageException
  - Test exception creation with each error type
  - Test user-friendly message generation
  - Test exception chaining with causes
  - _Requirements: 5.1, 5.2, 5.3_

- [x] 2. Create FileStorageService core structure
  - Create `FileStorageService.java` as a singleton class
  - Define constants (BASE_UPLOAD_DIR, SUPPORTED_EXTENSIONS)
  - Implement `getInstance()` singleton method
  - Implement `initializeBaseDirectory()` to create base upload directory structure
  - Implement `getFileExtension()` helper method
  - _Requirements: 1.2, 2.1, 4.3, 4.4_

- [x] 2.1 Write unit tests for FileStorageService core methods
  - Test singleton instance creation
  - Test `getFileExtension()` with various filenames (with/without extensions, multiple dots, special characters)
  - Test base directory initialization
  - _Requirements: 1.2, 4.3_

- [x] 3. Implement file type validation
  - Implement `isFileTypeSupported()` method with case-insensitive extension checking
  - Ensure validation checks against SUPPORTED_EXTENSIONS set
  - _Requirements: 2.1, 4.1, 4.2, 4.3, 4.4_

- [x] 3.1 Write property test for file type validation
  - **Property 2: File Type Validation**
  - Generate random file extensions (valid and invalid)
  - Verify only supported extensions pass validation
  - Test case-insensitive validation (.PDF, .pdf, .Pdf)
  - **Validates: Requirements 2.1, 4.1, 4.2, 4.3, 4.4**

- [x] 4. Implement directory management
  - Implement `createPresenterDirectory()` method
  - Create directory structure following pattern `uploads/presentations/{presenter_id}/`
  - Handle directory creation errors with appropriate FileStorageException
  - Ensure method is idempotent (safe to call multiple times)
  - _Requirements: 1.1, 1.3, 1.4_

- [x] 4.1 Write property test for directory structure consistency
  - **Property 1: Directory Structure Consistency**
  - Generate random presenter IDs
  - Upload files and verify directory structure matches pattern
  - Verify directories are created only when needed
  - **Validates: Requirements 1.1, 1.3**

- [x] 5. Implement file upload core functionality
  - Implement `uploadFile(File sourceFile, String presenterId)` method
  - Validate source file exists (throw FILE_NOT_FOUND if not)
  - Validate file type using `isFileTypeSupported()`
  - Create presenter directory if needed
  - Copy file to centralized storage preserving original filename
  - Generate relative storage path with forward slashes
  - Return the relative storage path
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 3.1, 3.3, 5.1_

- [x] 5.1 Write property test for file copy preservation
  - **Property 3: File Copy Preservation**
  - Generate random valid files
  - Verify files are copied to centralized storage
  - Verify original filename is preserved
  - Verify original file still exists (copy, not move)
  - **Validates: Requirements 2.2, 2.3, 2.4**

- [x] 5.2 Write property test for relative path generation
  - **Property 5: Relative Path Generation**
  - Generate random file uploads
  - Verify all returned paths are relative (don't start with / or drive letter)
  - Verify all paths use forward slashes
  - **Validates: Requirements 3.1, 3.3**

- [x] 5.3 Write property test for non-existent file rejection
  - **Property 9: Non-Existent File Rejection**
  - Generate paths to non-existent files
  - Verify upload throws FileStorageException with FILE_NOT_FOUND
  - Verify no changes to file system or database
  - **Validates: Requirements 5.1**

- [x] 6. Implement file overwrite and cleanup logic
  - Implement `deleteOldFiles(String presenterId, String excludeFilename)` method
  - In `uploadFile()`, check if presenter already has files
  - If uploading file with same name, allow overwrite
  - If uploading file with different name, delete old files first
  - _Requirements: 2.5, 6.1, 6.2_

- [x] 6.1 Write property test for same-name file overwrite
  - **Property 4: Same-Name File Overwrite**
  - Upload file with same name twice for same presenter
  - Verify only one copy exists after second upload
  - **Validates: Requirements 2.5, 6.1**

- [x] 6.2 Write property test for old file cleanup
  - **Property 8: Old File Cleanup on Different Filename**
  - Upload file A, then file B for same presenter
  - Verify only file B exists in storage
  - **Validates: Requirements 6.2**

- [x] 6.3 Write property test for filename isolation
  - **Property 7: Filename Isolation by Presenter**
  - Generate two different presenter IDs
  - Upload files with same filename for both
  - Verify both files exist without conflict
  - **Validates: Requirements 1.4**

- [x] 7. Implement error handling and logging
  - Add try-catch blocks for IOException in file operations
  - Throw appropriate FileStorageException types for different errors
  - Implement error logging to `logs/file-storage-errors.log`
  - Ensure atomic operations (rollback on failure)
  - _Requirements: 5.2, 5.3, 5.4, 5.5_

- [x] 7.1 Write property test for atomic operations
  - **Property 10: Atomic Operation on Failure**
  - Simulate various failure scenarios (mock file system)
  - Verify no partial state changes occur
  - **Validates: Requirements 5.4**

- [x] 7.2 Write property test for error logging
  - **Property 11: Error Logging**
  - Trigger various error conditions
  - Verify log entries are created with appropriate details
  - **Validates: Requirements 5.5**

- [x] 8. Checkpoint - Ensure core FileStorageService tests pass
  - Run all FileStorageService tests
  - Verify file upload, validation, and error handling work correctly
  - Ensure all tests pass, ask the user if questions arise

- [x] 9. Implement path resolution for file retrieval
  - Implement `resolveStoragePath(String storagePath)` method
  - Convert relative storage path to absolute File object
  - Resolve path relative to project root
  - _Requirements: 7.1_

- [x] 10. Implement backward compatibility support
  - Implement `isAbsolutePath(String path)` method
  - Check for absolute path indicators (starts with / or drive letter like C:)
  - Support both Windows and Unix path formats
  - _Requirements: 7.4_

- [x] 10.1 Write property test for storage path resolution
  - **Property 12: Storage Path Resolution**
  - Generate random storage paths
  - Verify resolution produces correct absolute paths
  - **Validates: Requirements 7.1**

- [x] 10.2 Write property test for backward compatibility
  - **Property 15: Backward Compatibility with Absolute Paths**
  - Generate absolute paths (both Windows and Unix formats)
  - Verify correct identification as absolute paths
  - **Validates: Requirements 7.4**

- [x] 11. Integrate FileStorageService with StudentRegistrationPanel
  - Modify `performRegistration()` method in StudentRegistrationPanel
  - Add file type validation before upload
  - Call `FileStorageService.getInstance().uploadFile()` when file is selected
  - Handle FileStorageException and display user-friendly error messages
  - Update student's file_path with returned storage path
  - Only save to database after successful file upload
  - _Requirements: 8.3, 8.4, 8.5_

- [x] 11.1 Write property test for upload trigger on form submission
  - **Property 16: Upload Trigger on Form Submission**
  - Submit registration forms with files
  - Verify upload operation is triggered
  - **Validates: Requirements 8.3**

- [x] 11.2 Write property test for student record update
  - **Property 17: Student Record Update on Success**
  - Complete successful uploads
  - Verify student records are updated with correct storage paths
  - **Validates: Requirements 8.4**

- [x] 11.3 Write property test for form submission prevention on failure
  - **Property 18: Form Submission Prevention on Upload Failure**
  - Simulate upload failures
  - Verify form submission is prevented
  - Verify error messages are displayed
  - **Validates: Requirements 8.5**

- [x] 12. Integrate FileStorageService with PresentationViewerDialog
  - Modify `loadMaterials()` method in PresentationViewerDialog
  - Use `FileStorageService.getInstance().isAbsolutePath()` to check path type
  - For relative paths, use `resolveStoragePath()` to get absolute file
  - For absolute paths (legacy), use path directly
  - Handle missing files with clear error messages
  - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [x] 12.1 Write property test for missing file error handling
  - **Property 13: Missing File Error Handling**
  - Generate paths to non-existent files
  - Attempt to load materials
  - Verify error messages are displayed without crashes
  - **Validates: Requirements 7.2**

- [x] 12.2 Write property test for multi-format file support
  - **Property 14: Multi-Format File Support**
  - Generate files of each supported type (.pdf, .jpg, .jpeg, .png, .gif, .txt)
  - Store and retrieve each file type
  - Verify all formats work correctly
  - **Validates: Requirements 7.3**

- [x] 13. Update DatabaseManager integration
  - Verify `file_path` column can store relative paths (VARCHAR(500) should be sufficient)
  - Ensure database queries handle both relative and absolute paths
  - Test path storage and retrieval
  - _Requirements: 3.2, 3.4, 6.3_

- [x] 13.1 Write property test for database path update
  - **Property 6: Database Path Update**
  - Upload files and verify database contains correct storage paths
  - Upload new files and verify paths are updated
  - **Validates: Requirements 3.2, 3.4, 6.3**

- [x] 14. Add comprehensive unit tests for edge cases
  - Test empty filename handling
  - Test very long filenames (>255 characters)
  - Test filenames with special characters
  - Test files with multiple dots in name
  - Test files with no extension
  - Test zero-byte files
  - Test very large files (>100MB)
  - _Requirements: 2.1, 4.2, 5.1_

- [x] 15. Final checkpoint - Run all tests and verify integration
  - Run all unit tests and property tests
  - Verify all 18 correctness properties pass
  - Test complete workflow: student registration → file upload → file viewing
  - Test error scenarios manually
  - Ensure all tests pass, ask the user if questions arise

## Notes

- All tasks are required for complete implementation
- Each property test should run minimum 100 iterations
- Use jqwik library for property-based testing in Java
- Each property test must be tagged with: `@Tag("Feature: centralized-file-storage, Property N: [property text]")`
- All file operations should be atomic (no partial updates on failure)
- Test with temporary directories and clean up after tests using JUnit 5 `@TempDir`
- Mock file system operations for permission/space error tests
