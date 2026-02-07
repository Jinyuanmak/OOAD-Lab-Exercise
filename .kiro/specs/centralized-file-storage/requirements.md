# Requirements Document

## Introduction

The Centralized File Storage feature addresses the current limitation where student presentation materials are stored using local file paths. When students upload files, the system currently stores the absolute path to their local folder, which causes broken links when files are moved or accessed from different machines. This feature will implement a centralized storage system within the project directory, ensuring reliable access to presentation materials regardless of the original file location.

## Glossary

- **File_Storage_System**: The centralized file management component responsible for copying, organizing, and storing uploaded presentation materials
- **Upload_Operation**: The process of copying a file from a student's local system to the centralized storage location
- **Centralized_Storage**: A dedicated folder structure within the project directory (`uploads/presentations/`) that stores all presentation materials
- **Presenter_ID**: A unique identifier assigned to each student presenter (format: P-xxxxxxxx)
- **Supported_File_Types**: PDF files (.pdf), image files (.jpg, .jpeg, .png, .gif), and text files (.txt)
- **Storage_Path**: The relative path from the project root to a stored file in the centralized storage
- **Original_File**: The file located in the student's local file system before upload
- **Stored_File**: The copy of the file residing in the centralized storage location

## Requirements

### Requirement 1: Centralized Storage Structure

**User Story:** As a system administrator, I want presentation materials organized in a structured folder hierarchy, so that files are easy to locate and manage.

#### Acceptance Criteria

1. THE File_Storage_System SHALL create a directory structure following the pattern `uploads/presentations/{presenter_id}/`
2. WHEN the system initializes, THE File_Storage_System SHALL ensure the base uploads directory exists
3. WHEN a student uploads a file, THE File_Storage_System SHALL create the presenter-specific subdirectory if it does not exist
4. THE File_Storage_System SHALL organize files by Presenter_ID to prevent naming conflicts between different students

### Requirement 2: File Upload and Copy Operations

**User Story:** As a student, when I upload my presentation materials, the system should copy my file to centralized storage so that my materials are always accessible regardless of where my original file is located.

#### Acceptance Criteria

1. WHEN a student selects a file for upload, THE File_Storage_System SHALL validate the file type against Supported_File_Types
2. WHEN a valid file is selected, THE File_Storage_System SHALL copy the Original_File to the Centralized_Storage location
3. THE File_Storage_System SHALL preserve the original filename when copying to Centralized_Storage
4. THE File_Storage_System SHALL retain the Original_File in the student's local system (copy operation, not move)
5. IF a file with the same name already exists in the presenter's directory, THEN THE File_Storage_System SHALL overwrite the existing file

### Requirement 3: Database Path Management

**User Story:** As a developer, I want the database to store relative paths to centralized storage, so that the application remains portable across different environments.

#### Acceptance Criteria

1. WHEN a file is successfully copied to Centralized_Storage, THE File_Storage_System SHALL generate a Storage_Path relative to the project root
2. THE File_Storage_System SHALL store the Storage_Path in the database file_path column
3. THE File_Storage_System SHALL use forward slashes in Storage_Path for cross-platform compatibility
4. WHEN updating a student's file, THE File_Storage_System SHALL replace the previous Storage_Path with the new Storage_Path

### Requirement 4: File Type Validation

**User Story:** As a system administrator, I want only supported file types to be uploaded, so that the system maintains consistency and security.

#### Acceptance Criteria

1. WHEN a student attempts to upload a file, THE File_Storage_System SHALL check the file extension against Supported_File_Types
2. IF the file extension is not in Supported_File_Types, THEN THE File_Storage_System SHALL reject the upload and display an error message
3. THE File_Storage_System SHALL perform case-insensitive file extension validation
4. THE File_Storage_System SHALL support the following extensions: .pdf, .jpg, .jpeg, .png, .gif, .txt

### Requirement 5: Error Handling and Safety

**User Story:** As a student, I want clear error messages when file operations fail, so that I understand what went wrong and can take corrective action.

#### Acceptance Criteria

1. IF the Original_File does not exist at the time of upload, THEN THE File_Storage_System SHALL display an error message and prevent the upload
2. IF the copy operation fails due to insufficient permissions, THEN THE File_Storage_System SHALL display an error message indicating the permission issue
3. IF the copy operation fails due to insufficient disk space, THEN THE File_Storage_System SHALL display an error message indicating the storage issue
4. WHEN any file operation error occurs, THE File_Storage_System SHALL maintain the previous state without partial updates
5. THE File_Storage_System SHALL log all file operation errors for debugging purposes

### Requirement 6: File Cleanup on Re-upload

**User Story:** As a student, when I upload a new version of my presentation, the system should replace my old file, so that storage space is managed efficiently.

#### Acceptance Criteria

1. WHEN a student uploads a new file with the same filename, THE File_Storage_System SHALL overwrite the existing Stored_File
2. WHEN a student uploads a new file with a different filename, THE File_Storage_System SHALL delete the previous Stored_File before storing the new file
3. THE File_Storage_System SHALL ensure the database Storage_Path is updated to reflect the current file

### Requirement 7: File Retrieval Integration

**User Story:** As an evaluator or coordinator, I should be able to view any student's materials without worrying about broken file paths, so that I can properly assess their work.

#### Acceptance Criteria

1. WHEN the PresentationViewerDialog loads materials, THE File_Storage_System SHALL resolve the Storage_Path relative to the project root
2. WHEN the Storage_Path points to a non-existent file, THE File_Storage_System SHALL display a clear error message
3. THE File_Storage_System SHALL support reading files from the Centralized_Storage location for all Supported_File_Types
4. THE File_Storage_System SHALL maintain backward compatibility with existing absolute paths during a transition period

### Requirement 8: StudentRegistrationPanel Integration

**User Story:** As a student, I want the file upload process to be seamless and transparent, so that I don't need to understand the underlying storage mechanism.

#### Acceptance Criteria

1. WHEN a student clicks the browse button, THE StudentRegistrationPanel SHALL display a file chooser dialog
2. WHEN a student selects a file, THE StudentRegistrationPanel SHALL display the filename in the file path field
3. WHEN a student submits the registration form, THE StudentRegistrationPanel SHALL trigger the Upload_Operation
4. WHEN the Upload_Operation completes successfully, THE StudentRegistrationPanel SHALL update the student record with the new Storage_Path
5. IF the Upload_Operation fails, THEN THE StudentRegistrationPanel SHALL display the error message and prevent form submission
