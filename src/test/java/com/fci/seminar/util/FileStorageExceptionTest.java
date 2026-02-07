package com.fci.seminar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.fci.seminar.util.FileStorageException.ErrorType;

/**
 * Unit tests for FileStorageException class.
 */
class FileStorageExceptionTest {
    
    @Test
    void testExceptionCreationWithMessageAndErrorType() {
        String message = "Test error message";
        ErrorType errorType = ErrorType.FILE_NOT_FOUND;
        
        FileStorageException exception = new FileStorageException(message, errorType);
        
        assertEquals(message, exception.getMessage());
        assertEquals(errorType, exception.getErrorType());
        assertNull(exception.getCause());
    }
    
    @Test
    void testExceptionCreationWithCause() {
        String message = "Test error message";
        ErrorType errorType = ErrorType.COPY_FAILED;
        Throwable cause = new RuntimeException("Original cause");
        
        FileStorageException exception = new FileStorageException(message, errorType, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(errorType, exception.getErrorType());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testGetUserFriendlyMessage_FileNotFound() {
        FileStorageException exception = new FileStorageException(
            "Technical message", 
            ErrorType.FILE_NOT_FOUND
        );
        
        String userMessage = exception.getUserFriendlyMessage();
        
        assertNotNull(userMessage);
        assertTrue(userMessage.contains("file could not be found"));
        assertFalse(userMessage.contains("Technical message"));
    }
    
    @Test
    void testGetUserFriendlyMessage_InvalidFileType() {
        FileStorageException exception = new FileStorageException(
            "Technical message", 
            ErrorType.INVALID_FILE_TYPE
        );
        
        String userMessage = exception.getUserFriendlyMessage();
        
        assertNotNull(userMessage);
        assertTrue(userMessage.contains("Unsupported file type"));
        assertTrue(userMessage.contains("PDF") || userMessage.contains("image"));
    }
    
    @Test
    void testGetUserFriendlyMessage_PermissionDenied() {
        FileStorageException exception = new FileStorageException(
            "Technical message", 
            ErrorType.PERMISSION_DENIED
        );
        
        String userMessage = exception.getUserFriendlyMessage();
        
        assertNotNull(userMessage);
        assertTrue(userMessage.contains("Permission denied"));
    }
    
    @Test
    void testGetUserFriendlyMessage_InsufficientSpace() {
        FileStorageException exception = new FileStorageException(
            "Technical message", 
            ErrorType.INSUFFICIENT_SPACE
        );
        
        String userMessage = exception.getUserFriendlyMessage();
        
        assertNotNull(userMessage);
        assertTrue(userMessage.contains("Insufficient disk space"));
    }
    
    @Test
    void testGetUserFriendlyMessage_CopyFailed() {
        FileStorageException exception = new FileStorageException(
            "Technical message", 
            ErrorType.COPY_FAILED
        );
        
        String userMessage = exception.getUserFriendlyMessage();
        
        assertNotNull(userMessage);
        assertTrue(userMessage.contains("Failed to copy file"));
    }
    
    @Test
    void testGetUserFriendlyMessage_DirectoryCreationFailed() {
        FileStorageException exception = new FileStorageException(
            "Technical message", 
            ErrorType.DIRECTORY_CREATION_FAILED
        );
        
        String userMessage = exception.getUserFriendlyMessage();
        
        assertNotNull(userMessage);
        assertTrue(userMessage.contains("Failed to create storage directory"));
    }
    
    @Test
    void testAllErrorTypesHaveUserFriendlyMessages() {
        // Ensure every error type has a user-friendly message
        for (ErrorType errorType : ErrorType.values()) {
            FileStorageException exception = new FileStorageException(
                "Technical message", 
                errorType
            );
            
            String userMessage = exception.getUserFriendlyMessage();
            
            assertNotNull(userMessage, "Error type " + errorType + " should have a user-friendly message");
            assertFalse(userMessage.isEmpty(), "User-friendly message should not be empty for " + errorType);
        }
    }
    
    @Test
    void testExceptionChaining() {
        RuntimeException originalCause = new RuntimeException("Original error");
        FileStorageException exception = new FileStorageException(
            "Wrapper message", 
            ErrorType.COPY_FAILED, 
            originalCause
        );
        
        assertNotNull(exception.getCause());
        assertEquals(originalCause, exception.getCause());
        assertEquals("Original error", exception.getCause().getMessage());
    }
}
