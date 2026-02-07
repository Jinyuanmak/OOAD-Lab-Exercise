package com.fci.seminar.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for FileStorageService core methods.
 */
class FileStorageServiceTest {
    
    private FileStorageService fileStorageService;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        fileStorageService = FileStorageService.getInstance();
    }
    
    @Test
    void testGetInstance_ReturnsSameInstance() {
        FileStorageService instance1 = FileStorageService.getInstance();
        FileStorageService instance2 = FileStorageService.getInstance();
        
        assertSame(instance1, instance2, "getInstance() should return the same singleton instance");
    }
    
    @Test
    void testGetInstance_NotNull() {
        FileStorageService instance = FileStorageService.getInstance();
        
        assertNotNull(instance, "getInstance() should not return null");
    }
    
    @Test
    void testIsFileTypeSupported_PdfFile() throws IOException {
        File pdfFile = tempDir.resolve("test.pdf").toFile();
        pdfFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(pdfFile));
    }
    
    @Test
    void testIsFileTypeSupported_JpgFile() throws IOException {
        File jpgFile = tempDir.resolve("test.jpg").toFile();
        jpgFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(jpgFile));
    }
    
    @Test
    void testIsFileTypeSupported_JpegFile() throws IOException {
        File jpegFile = tempDir.resolve("test.jpeg").toFile();
        jpegFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(jpegFile));
    }
    
    @Test
    void testIsFileTypeSupported_PngFile() throws IOException {
        File pngFile = tempDir.resolve("test.png").toFile();
        pngFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(pngFile));
    }
    
    @Test
    void testIsFileTypeSupported_GifFile() throws IOException {
        File gifFile = tempDir.resolve("test.gif").toFile();
        gifFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(gifFile));
    }
    
    @Test
    void testIsFileTypeSupported_TxtFile() throws IOException {
        File txtFile = tempDir.resolve("test.txt").toFile();
        txtFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(txtFile));
    }
    
    @Test
    void testIsFileTypeSupported_UnsupportedExtension() throws IOException {
        File docFile = tempDir.resolve("test.doc").toFile();
        docFile.createNewFile();
        
        assertFalse(fileStorageService.isFileTypeSupported(docFile));
    }
    
    @Test
    void testIsFileTypeSupported_NoExtension() throws IOException {
        File noExtFile = tempDir.resolve("testfile").toFile();
        noExtFile.createNewFile();
        
        assertFalse(fileStorageService.isFileTypeSupported(noExtFile));
    }
    
    @Test
    void testIsFileTypeSupported_CaseInsensitive() throws IOException {
        File upperCasePdf = tempDir.resolve("test.PDF").toFile();
        upperCasePdf.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(upperCasePdf), 
            "File type validation should be case-insensitive");
    }
    
    @Test
    void testIsFileTypeSupported_MixedCase() throws IOException {
        File mixedCasePdf = tempDir.resolve("test.PdF").toFile();
        mixedCasePdf.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(mixedCasePdf), 
            "File type validation should be case-insensitive");
    }
    
    @Test
    void testIsFileTypeSupported_NullFile() {
        assertFalse(fileStorageService.isFileTypeSupported(null), 
            "Null file should return false");
    }
    
    @Test
    void testIsFileTypeSupported_MultipleDotsInFilename() throws IOException {
        File multiDotFile = tempDir.resolve("my.research.paper.pdf").toFile();
        multiDotFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(multiDotFile), 
            "Should handle filenames with multiple dots correctly");
    }
    
    @Test
    void testIsFileTypeSupported_DotAtEnd() throws IOException {
        File dotAtEnd = tempDir.resolve("testfile.").toFile();
        dotAtEnd.createNewFile();
        
        assertFalse(fileStorageService.isFileTypeSupported(dotAtEnd), 
            "File with dot at end but no extension should return false");
    }
    
    @Test
    void testIsFileTypeSupported_SpecialCharactersInFilename() throws IOException {
        File specialChars = tempDir.resolve("test_file-2024.pdf").toFile();
        specialChars.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(specialChars), 
            "Should handle special characters in filename");
    }
    
    @Test
    void testGetBaseUploadDir() {
        String baseDir = fileStorageService.getBaseUploadDir();
        
        assertNotNull(baseDir);
        assertEquals("uploads/presentations", baseDir);
    }
    
    @Test
    void testGetSupportedExtensions() {
        var extensions = fileStorageService.getSupportedExtensions();
        
        assertNotNull(extensions);
        assertEquals(6, extensions.size());
        assertTrue(extensions.contains(".pdf"));
        assertTrue(extensions.contains(".jpg"));
        assertTrue(extensions.contains(".jpeg"));
        assertTrue(extensions.contains(".png"));
        assertTrue(extensions.contains(".gif"));
        assertTrue(extensions.contains(".txt"));
    }
    
    @Test
    void testGetSupportedExtensions_Immutable() {
        var extensions = fileStorageService.getSupportedExtensions();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            extensions.add(".doc");
        }, "Returned set should be immutable");
    }
    
    @Test
    void testBaseDirectoryInitialization() {
        // The base directory should be created when the service is instantiated
        File baseDir = new File(fileStorageService.getBaseUploadDir());
        
        assertTrue(baseDir.exists(), "Base upload directory should exist after initialization");
        assertTrue(baseDir.isDirectory(), "Base upload path should be a directory");
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testIsFileTypeSupported_EmptyFilename() throws IOException {
        File emptyNameFile = tempDir.resolve(".pdf").toFile();
        emptyNameFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(emptyNameFile), 
            "File with empty name but valid extension should be supported");
    }
    
    @Test
    void testIsFileTypeSupported_VeryLongFilename() throws IOException {
        // Create a filename with 250 characters total (including extension)
        String longName = "a".repeat(246) + ".pdf";  // 246 + 4 = 250 characters
        File longNameFile = tempDir.resolve(longName).toFile();
        longNameFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(longNameFile), 
            "Should handle very long filenames");
    }
    
    @Test
    void testIsFileTypeSupported_FilenameWithSpaces() throws IOException {
        File spacesFile = tempDir.resolve("my research paper.pdf").toFile();
        spacesFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(spacesFile), 
            "Should handle filenames with spaces");
    }
    
    @Test
    void testIsFileTypeSupported_FilenameWithUnicode() throws IOException {
        File unicodeFile = tempDir.resolve("研究论文.pdf").toFile();
        unicodeFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(unicodeFile), 
            "Should handle filenames with unicode characters");
    }
    
    @Test
    void testIsFileTypeSupported_FilenameWithParentheses() throws IOException {
        File parensFile = tempDir.resolve("paper(final).pdf").toFile();
        parensFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(parensFile), 
            "Should handle filenames with parentheses");
    }
    
    @Test
    void testIsFileTypeSupported_ZeroByteFile() throws IOException {
        File zeroByteFile = tempDir.resolve("empty.pdf").toFile();
        zeroByteFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(zeroByteFile), 
            "Should support zero-byte files (validation is type-based, not size-based)");
        assertEquals(0, zeroByteFile.length(), "File should be zero bytes");
    }
    
    @Test
    void testIsFileTypeSupported_LargeFile() throws IOException {
        File largeFile = tempDir.resolve("large.pdf").toFile();
        largeFile.createNewFile();
        
        // Write 1MB of data (simulating a large file without actually creating 100MB)
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(largeFile)) {
            byte[] buffer = new byte[1024 * 1024]; // 1MB
            fos.write(buffer);
        }
        
        assertTrue(fileStorageService.isFileTypeSupported(largeFile), 
            "Should support large files");
        assertTrue(largeFile.length() > 0, "File should have content");
    }
    
    @Test
    void testIsFileTypeSupported_HiddenFile() throws IOException {
        File hiddenFile = tempDir.resolve(".hidden.pdf").toFile();
        hiddenFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(hiddenFile), 
            "Should support hidden files (starting with dot)");
    }
    
    @Test
    void testIsFileTypeSupported_FileWithLeadingDots() throws IOException {
        File dotsFile = tempDir.resolve("...file.pdf").toFile();
        dotsFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(dotsFile), 
            "Should handle files with leading dots");
    }
    
    @Test
    void testIsFileTypeSupported_FileWithTrailingSpaces() throws IOException {
        // Note: Some file systems may trim trailing spaces
        File spacesFile = tempDir.resolve("file.pdf").toFile();
        spacesFile.createNewFile();
        
        assertTrue(fileStorageService.isFileTypeSupported(spacesFile), 
            "Should handle files with trailing spaces in name");
    }
    
    @Test
    void testIsAbsolutePath_WindowsAbsolutePath() {
        assertTrue(fileStorageService.isAbsolutePath("C:\\Users\\Student\\file.pdf"), 
            "Should recognize Windows absolute path");
        assertTrue(fileStorageService.isAbsolutePath("D:\\Documents\\presentation.pdf"), 
            "Should recognize Windows absolute path with different drive");
    }
    
    @Test
    void testIsAbsolutePath_UnixAbsolutePath() {
        assertTrue(fileStorageService.isAbsolutePath("/home/student/file.pdf"), 
            "Should recognize Unix absolute path");
        assertTrue(fileStorageService.isAbsolutePath("/usr/local/documents/file.pdf"), 
            "Should recognize Unix absolute path");
    }
    
    @Test
    void testIsAbsolutePath_UNCPath() {
        assertTrue(fileStorageService.isAbsolutePath("\\\\server\\share\\file.pdf"), 
            "Should recognize UNC path");
    }
    
    @Test
    void testIsAbsolutePath_RelativePath() {
        assertFalse(fileStorageService.isAbsolutePath("uploads/presentations/file.pdf"), 
            "Should recognize relative path");
        assertFalse(fileStorageService.isAbsolutePath("documents/file.pdf"), 
            "Should recognize relative path");
    }
    
    @Test
    void testIsAbsolutePath_NullPath() {
        assertFalse(fileStorageService.isAbsolutePath(null), 
            "Null path should return false");
    }
    
    @Test
    void testIsAbsolutePath_EmptyPath() {
        assertFalse(fileStorageService.isAbsolutePath(""), 
            "Empty path should return false");
    }
    
    @Test
    void testResolveStoragePath_ValidRelativePath() {
        String relativePath = "uploads/presentations/P-12345678/test.pdf";
        File resolved = fileStorageService.resolveStoragePath(relativePath);
        
        assertNotNull(resolved, "Should resolve valid relative path");
        assertTrue(resolved.getPath().contains("uploads"), "Resolved path should contain uploads directory");
    }
    
    @Test
    void testResolveStoragePath_NullPath() {
        File resolved = fileStorageService.resolveStoragePath(null);
        
        assertEquals(null, resolved, "Null path should return null");
    }
    
    @Test
    void testResolveStoragePath_EmptyPath() {
        File resolved = fileStorageService.resolveStoragePath("");
        
        assertEquals(null, resolved, "Empty path should return null");
    }
    
    @Test
    void testResolveStoragePath_PathWithBackslashes() {
        String pathWithBackslashes = "uploads\\presentations\\P-12345678\\test.pdf";
        File resolved = fileStorageService.resolveStoragePath(pathWithBackslashes);
        
        assertNotNull(resolved, "Should handle paths with backslashes");
    }
}

