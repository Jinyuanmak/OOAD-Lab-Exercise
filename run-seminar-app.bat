@echo off
REM Seminar Management System Launcher
title FCI Seminar Management System

echo ========================================
echo FCI Seminar Management System
echo ========================================
echo.

REM Check Java installation
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    echo.
    pause
    exit /b 1
)

echo Compiling application...
call apache-maven-3.9.12\bin\mvn.cmd compile -q

if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed
    echo Please check the error messages above
    echo.
    pause
    exit /b 1
)

echo.
echo Starting application...
echo (GUI window will open - keep this window open)
echo.

REM Run the application
call apache-maven-3.9.12\bin\mvn.cmd exec:java "-Dexec.mainClass=com.fci.seminar.ui.SeminarApp"

echo.
echo Application closed.
pause
