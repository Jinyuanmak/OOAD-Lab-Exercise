-- Seminar Management System Database Schema
-- For MySQL (Laragon)
-- Run this script in phpMyAdmin or MySQL command line

-- Create database
CREATE DATABASE IF NOT EXISTS seminar_db;
USE seminar_db;

-- Drop existing tables (for clean setup)
DROP TABLE IF EXISTS evaluations;
DROP TABLE IF EXISTS poster_boards;
DROP TABLE IF EXISTS session_evaluators;
DROP TABLE IF EXISTS session_presenters;
DROP TABLE IF EXISTS awards;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS users;

-- Users table (base for all user types)
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('STUDENT', 'EVALUATOR', 'COORDINATOR') NOT NULL,
    -- Student-specific fields
    research_title VARCHAR(200),
    abstract_text TEXT,
    supervisor_name VARCHAR(100),
    presentation_type ENUM('ORAL', 'POSTER'),
    file_path VARCHAR(500),
    presenter_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sessions table
CREATE TABLE sessions (
    session_id VARCHAR(50) PRIMARY KEY,
    session_date DATE NOT NULL,
    venue VARCHAR(200) NOT NULL,
    session_type ENUM('ORAL', 'POSTER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Session-Presenter assignments (many-to-many)
CREATE TABLE session_presenters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL,
    presenter_id VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_session_presenter (session_id, presenter_id)
);

-- Session-Evaluator assignments (many-to-many)
CREATE TABLE session_evaluators (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL,
    evaluator_id VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (evaluator_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_session_evaluator (session_id, evaluator_id)
);

-- Evaluations table
CREATE TABLE evaluations (
    evaluation_id VARCHAR(50) PRIMARY KEY,
    presenter_id VARCHAR(50) NOT NULL,
    evaluator_id VARCHAR(50) NOT NULL,
    session_id VARCHAR(50) NOT NULL,
    problem_clarity INT NOT NULL CHECK (problem_clarity BETWEEN 1 AND 10),
    methodology INT NOT NULL CHECK (methodology BETWEEN 1 AND 10),
    results INT NOT NULL CHECK (results BETWEEN 1 AND 10),
    presentation INT NOT NULL CHECK (presentation BETWEEN 1 AND 10),
    comments TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (evaluator_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE
);

-- Poster boards table
CREATE TABLE poster_boards (
    board_id VARCHAR(20) PRIMARY KEY,
    presenter_id VARCHAR(50),
    session_id VARCHAR(50),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE SET NULL
);

-- Awards table
CREATE TABLE awards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    award_type ENUM('BEST_ORAL', 'BEST_POSTER', 'PEOPLES_CHOICE') NOT NULL,
    winner_id VARCHAR(50) NOT NULL,
    score DOUBLE NOT NULL,
    ceremony_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Initialize poster boards (B001 to B020)
INSERT INTO poster_boards (board_id) VALUES 
('B001'), ('B002'), ('B003'), ('B004'), ('B005'),
('B006'), ('B007'), ('B008'), ('B009'), ('B010'),
('B011'), ('B012'), ('B013'), ('B014'), ('B015'),
('B016'), ('B017'), ('B018'), ('B019'), ('B020');

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_sessions_date ON sessions(session_date);
CREATE INDEX idx_evaluations_presenter ON evaluations(presenter_id);
CREATE INDEX idx_evaluations_evaluator ON evaluations(evaluator_id);

-- Show tables created
SHOW TABLES;
