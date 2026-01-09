package com.fci.seminar.util;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Utility class for displaying error, warning, and confirmation dialogs.
 * Provides consistent error handling across the application.
 */
public final class ErrorHandler {
    
    private ErrorHandler() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Shows an error dialog with the specified message.
     * @param parent the parent component for the dialog
     * @param message the error message to display
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Shows a warning dialog with the specified message.
     * @param parent the parent component for the dialog
     * @param message the warning message to display
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Shows a confirmation dialog and returns the user's choice.
     * @param parent the parent component for the dialog
     * @param message the confirmation message to display
     * @return true if the user confirms (clicks Yes), false otherwise
     */
    public static boolean confirmAction(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "Confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Shows an information dialog with the specified message.
     * @param parent the parent component for the dialog
     * @param message the information message to display
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Shows a success dialog with the specified message.
     * @param parent the parent component for the dialog
     * @param message the success message to display
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
