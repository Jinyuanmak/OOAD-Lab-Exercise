package com.fci.seminar.util;

/**
 * Custom exception class for file storage operations.
 * Provides categorized error types and user-friendly error messages.
 */
public class FileStorageException extends Exception {
    
    /**
     * Enumeration of possible file storage error types.
     */
    public enum ErrorType {
        /** The source file could not be found */
        FILE_NOT_FOUND,
        
        /** The file type is not supported */
        INVALID_FILE_TYPE,
        
        /** Permission denied for file operation */
        PERMISSION_DENIED,
        
        /** Insufficient disk space for file operation */
        INSUFFICIENT_SPACE,
        
        /** File copy operation failed */
        COPY_FAILED,
        
        /** Directory creation failed */
        DIRECTORY_CREATION_FAILED
    }
    
    private final ErrorType errorType;
    
    /**
     * Constructs a new FileStorageException with the specified message and error type.
     * 
     * @param message the detail message
     * @param errorType the type of error that occurred
     */
    public FileStorageException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    /**
     * Constructs a new FileStorageException with the specified message, error type, and cause.
     * 
     * @param message the detail message
     * @param errorType the type of error that occurred
     * @param cause the cause of this exception
     */
    public FileStorageException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    /**
     * Gets the error type of this exception.
     * 
     * @return the error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Gets a user-friendly error message based on the error type.
     * This message is suitable for display in the UI.
     * 
     * @return a user-friendly error message
     */
    public String getUserFriendlyMessage() {
        switch (errorType) {
            case FILE_NOT_FOUND:
                return "The selected file could not be found. Please ensure the file exists and try again.";
            
            case INVALID_FILE_TYPE:
                return "Unsupported file type. Please upload PDF, image (JPG, PNG, GIF), or text files.";
            
            case PERMISSION_DENIED:
                return "Permission denied. Please check file permissions and try again.";
            
            case INSUFFICIENT_SPACE:
                return "Insufficient disk space. Please free up space and try again.";
            
            case COPY_FAILED:
                return "Failed to copy file. Please try again or contact support.";
            
            case DIRECTORY_CREATION_FAILED:
                return "Failed to create storage directory. Please contact support.";
            
            default:
                return "An unexpected error occurred during file storage operation.";
        }
    }
}
