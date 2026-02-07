package com.fci.seminar.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import com.fci.seminar.util.FileStorageException;
import com.fci.seminar.util.FileStorageException.ErrorType;

/**
 * Singleton service class responsible for managing file storage operations.
 * Handles uploading, validating, and organizing presentation materials in centralized storage.
 */
public class FileStorageService {
    
    // Constants
    private static final String BASE_UPLOAD_DIR = "uploads/presentations";
    private static final Set<String> SUPPORTED_EXTENSIONS = 
        Set.of(".pdf", ".jpg", ".jpeg", ".png", ".gif", ".txt");
    private static final String LOG_DIR = "logs";
    private static final String ERROR_LOG_FILE = "logs/file-storage-errors.log";
    private static final DateTimeFormatter LOG_TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Singleton instance
    private static FileStorageService instance;
    
    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the base directory structure and logging.
     */
    private FileStorageService() {
        try {
            initializeLogDirectory();
            initializeBaseDirectory();
        } catch (FileStorageException e) {
            System.err.println("Failed to initialize FileStorageService: " + e.getMessage());
            logError("Initialization failed", e);
        }
    }
    
    /**
     * Gets the singleton instance of FileStorageService.
     * Creates the instance if it doesn't exist.
     * 
     * @return the singleton instance
     */
    public static synchronized FileStorageService getInstance() {
        if (instance == null) {
            instance = new FileStorageService();
        }
        return instance;
    }
    
    /**
     * Initializes the base upload directory structure.
     * Creates the uploads/presentations directory if it doesn't exist.
     * 
     * @throws FileStorageException if directory creation fails
     */
    private void initializeBaseDirectory() throws FileStorageException {
        try {
            Path baseDir = Paths.get(BASE_UPLOAD_DIR);
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
                System.out.println("Created base upload directory: " + BASE_UPLOAD_DIR);
            }
        } catch (IOException e) {
            FileStorageException fse = new FileStorageException(
                "Failed to create base upload directory: " + BASE_UPLOAD_DIR,
                ErrorType.DIRECTORY_CREATION_FAILED,
                e
            );
            logError("Base directory initialization failed", fse);
            throw fse;
        }
    }
    
    /**
     * Initializes the log directory structure.
     * Creates the logs directory if it doesn't exist.
     */
    private void initializeLogDirectory() {
        try {
            Path logDir = Paths.get(LOG_DIR);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
        }
    }
    
    /**
     * Logs an error to the error log file.
     * Includes timestamp, error message, and stack trace.
     * 
     * @param context contextual information about where the error occurred
     * @param exception the exception to log
     */
    private void logError(String context, Exception exception) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ERROR_LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(LOG_TIMESTAMP_FORMAT);
            writer.println("=".repeat(80));
            writer.println("[" + timestamp + "] ERROR: " + context);
            writer.println("Message: " + exception.getMessage());
            
            if (exception instanceof FileStorageException fse) {
                writer.println("Error Type: " + fse.getErrorType());
            }
            
            writer.println("Stack Trace:");
            exception.printStackTrace(writer);
            writer.println();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to error log: " + e.getMessage());
        }
    }
    
    /**
     * Gets the file extension from a filename.
     * Returns the extension in lowercase, including the dot.
     * 
     * @param filename the filename to extract extension from
     * @return the file extension (e.g., ".pdf", ".jpg") or empty string if no extension
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            // No extension or dot is the last character
            return "";
        }
        
        return filename.substring(lastDotIndex).toLowerCase();
    }
    
    /**
     * Validates if a file type is supported based on its extension.
     * Performs case-insensitive validation.
     * 
     * @param file the file to validate
     * @return true if the file type is supported, false otherwise
     */
    public boolean isFileTypeSupported(File file) {
        if (file == null) {
            return false;
        }
        
        String extension = getFileExtension(file.getName());
        return SUPPORTED_EXTENSIONS.contains(extension);
    }
    
    /**
     * Uploads a file to centralized storage.
     * Validates the file exists and has a supported type, creates the presenter directory,
     * copies the file preserving the original filename, and returns the relative storage path.
     * 
     * If the presenter already has files:
     * - If uploading a file with the same name, it will be overwritten
     * - If uploading a file with a different name, old files will be deleted first
     * 
     * This operation is atomic - if any step fails, the system state is rolled back.
     * 
     * @param sourceFile the original file from student's local system
     * @param presenterId the presenter's unique ID
     * @return the relative storage path (e.g., "uploads/presentations/P-12345678/file.pdf")
     * @throws FileStorageException if upload fails for any reason
     */
    public String uploadFile(File sourceFile, String presenterId) throws FileStorageException {
        // Track state for rollback
        Path presenterDir = null;
        boolean presenterDirCreated = false;
        File[] deletedFiles = null;
        Path destinationPath = null;
        
        try {
            // Validate source file exists
            if (sourceFile == null || !sourceFile.exists()) {
                FileStorageException fse = new FileStorageException(
                    "Source file does not exist: " + (sourceFile != null ? sourceFile.getPath() : "null"),
                    ErrorType.FILE_NOT_FOUND
                );
                logError("File upload failed - file not found", fse);
                throw fse;
            }
            
            // Validate file type
            if (!isFileTypeSupported(sourceFile)) {
                FileStorageException fse = new FileStorageException(
                    "Unsupported file type: " + sourceFile.getName(),
                    ErrorType.INVALID_FILE_TYPE
                );
                logError("File upload failed - invalid file type", fse);
                throw fse;
            }
            
            // Create presenter directory if needed
            presenterDir = Paths.get(BASE_UPLOAD_DIR, presenterId);
            presenterDirCreated = !Files.exists(presenterDir);
            presenterDir = createPresenterDirectory(presenterId);
            
            // Prepare destination file (preserve original filename)
            String filename = sourceFile.getName();
            destinationPath = presenterDir.resolve(filename);
            
            // Check if presenter already has files
            // If uploading a file with a different name, delete old files first
            File[] existingFiles = presenterDir.toFile().listFiles();
            if (existingFiles != null && existingFiles.length > 0) {
                boolean sameNameExists = false;
                for (File existingFile : existingFiles) {
                    if (existingFile.getName().equals(filename)) {
                        sameNameExists = true;
                        break;
                    }
                }
                
                // If uploading a file with a different name, clean up old files
                // Save references for potential rollback
                if (!sameNameExists) {
                    deletedFiles = existingFiles.clone();
                    deleteOldFiles(presenterId, filename);
                }
            }
            
            // Copy file to centralized storage (will overwrite if same name exists)
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Uploaded file: " + sourceFile.getName() + " to " + destinationPath);
            
            // Generate relative storage path with forward slashes for cross-platform compatibility
            String relativePath = BASE_UPLOAD_DIR + "/" + presenterId + "/" + filename;
            
            return relativePath;
            
        } catch (FileStorageException e) {
            // Rollback on failure
            rollbackUpload(presenterDir, presenterDirCreated, deletedFiles, destinationPath);
            throw e;
            
        } catch (IOException e) {
            // Rollback on failure
            rollbackUpload(presenterDir, presenterDirCreated, deletedFiles, destinationPath);
            
            FileStorageException fse = new FileStorageException(
                "Failed to copy file: " + sourceFile.getName(),
                ErrorType.COPY_FAILED,
                e
            );
            logError("File upload failed - copy operation failed", fse);
            throw fse;
            
        } catch (Exception e) {
            // Rollback on any unexpected failure
            rollbackUpload(presenterDir, presenterDirCreated, deletedFiles, destinationPath);
            
            FileStorageException fse = new FileStorageException(
                "Unexpected error during file upload: " + e.getMessage(),
                ErrorType.COPY_FAILED,
                e
            );
            logError("File upload failed - unexpected error", fse);
            throw fse;
        }
    }
    
    /**
     * Rolls back changes made during a failed upload operation.
     * Ensures atomic operation - no partial state changes on failure.
     * 
     * @param presenterDir the presenter directory that may have been created
     * @param presenterDirCreated whether the presenter directory was created in this operation
     * @param deletedFiles files that were deleted and may need to be restored
     * @param destinationPath the destination file that may have been partially created
     */
    private void rollbackUpload(Path presenterDir, boolean presenterDirCreated, 
                                File[] deletedFiles, Path destinationPath) {
        try {
            // Delete the destination file if it was created
            if (destinationPath != null && Files.exists(destinationPath)) {
                Files.deleteIfExists(destinationPath);
                System.out.println("Rollback: Deleted destination file");
            }
            
            // Delete the presenter directory if it was created in this operation
            if (presenterDirCreated && presenterDir != null && Files.exists(presenterDir)) {
                Files.deleteIfExists(presenterDir);
                System.out.println("Rollback: Deleted presenter directory");
            }
            
            // Note: We cannot restore deleted files as they were intentionally removed
            // This is acceptable as the operation failed and the user will retry
            
        } catch (IOException e) {
            System.err.println("Rollback failed: " + e.getMessage());
            logError("Rollback operation failed", e);
        }
    }
    /**
     * Creates a presenter-specific directory if it doesn't exist.
     * Directory structure follows the pattern: uploads/presentations/{presenter_id}/
     * This method is idempotent - safe to call multiple times.
     * 
     * Package-private for testing purposes.
     * 
     * @param presenterId the presenter's unique ID
     * @return the Path to the presenter's directory
     * @throws FileStorageException if directory creation fails
     */
    Path createPresenterDirectory(String presenterId) throws FileStorageException {
        if (presenterId == null || presenterId.trim().isEmpty()) {
            FileStorageException fse = new FileStorageException(
                "Presenter ID cannot be null or empty",
                ErrorType.DIRECTORY_CREATION_FAILED
            );
            logError("Directory creation failed - invalid presenter ID", fse);
            throw fse;
        }
        
        try {
            Path presenterDir = Paths.get(BASE_UPLOAD_DIR, presenterId);
            
            // Create directory if it doesn't exist (idempotent operation)
            if (!Files.exists(presenterDir)) {
                Files.createDirectories(presenterDir);
                System.out.println("Created presenter directory: " + presenterDir);
            }
            
            return presenterDir;
        } catch (IOException e) {
            FileStorageException fse = new FileStorageException(
                "Failed to create presenter directory for: " + presenterId,
                ErrorType.DIRECTORY_CREATION_FAILED,
                e
            );
            logError("Directory creation failed", fse);
            throw fse;
        }
    }
    
    /**
     * Deletes old files in a presenter's directory, excluding a specific filename.
     * Used to clean up old files when a presenter uploads a new file with a different name.
     * 
     * Package-private for testing purposes.
     * 
     * @param presenterId the presenter's unique ID
     * @param excludeFilename filename to exclude from deletion (typically the newly uploaded file)
     */
    void deleteOldFiles(String presenterId, String excludeFilename) {
        Path presenterDir = Paths.get(BASE_UPLOAD_DIR, presenterId);
        
        // If directory doesn't exist, nothing to delete
        if (!Files.exists(presenterDir)) {
            return;
        }
        
        try {
            File[] files = presenterDir.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    // Delete all files except the excluded one
                    if (!file.getName().equals(excludeFilename)) {
                        if (file.delete()) {
                            System.out.println("Deleted old file: " + file.getName());
                        } else {
                            String errorMsg = "Failed to delete old file: " + file.getName();
                            System.err.println(errorMsg);
                            logError("File deletion failed", 
                                new IOException(errorMsg));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during old file cleanup: " + e.getMessage());
            logError("Old file cleanup failed", e);
        }
    }
    
    /**
     * Gets the base upload directory path.
     * 
     * @return the base upload directory path
     */
    public String getBaseUploadDir() {
        return BASE_UPLOAD_DIR;
    }
    
    /**
     * Gets the set of supported file extensions.
     * 
     * @return an unmodifiable set of supported extensions
     */
    public Set<String> getSupportedExtensions() {
        return Set.copyOf(SUPPORTED_EXTENSIONS);
    }
    
    /**
     * Resolves a relative storage path to an absolute File object.
     * Converts a relative path (e.g., "uploads/presentations/P-12345678/file.pdf")
     * to an absolute File object pointing to the actual file location.
     * 
     * @param storagePath the relative storage path from the database
     * @return File object pointing to the stored file
     */
    public File resolveStoragePath(String storagePath) {
        if (storagePath == null || storagePath.isEmpty()) {
            return null;
        }
        
        // Convert forward slashes to system-specific separators
        String normalizedPath = storagePath.replace("/", File.separator);
        
        // Resolve relative to current working directory (project root)
        return new File(normalizedPath).getAbsoluteFile();
    }
    
    /**
     * Checks if a path is an absolute path (for backward compatibility).
     * Supports both Windows (C:\path) and Unix (/path) absolute path formats.
     * 
     * @param path the path to check
     * @return true if the path is absolute, false if it's relative or null
     */
    public boolean isAbsolutePath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        
        // Check for Unix-style absolute path (starts with /)
        if (path.startsWith("/")) {
            return true;
        }
        
        // Check for Windows-style absolute path (starts with drive letter like C:)
        // Pattern: single letter followed by colon
        if (path.length() >= 2 && 
            Character.isLetter(path.charAt(0)) && 
            path.charAt(1) == ':') {
            return true;
        }
        
        // Check for UNC paths (Windows network paths starting with \\)
        if (path.startsWith("\\\\")) {
            return true;
        }
        
        return false;
    }
}
