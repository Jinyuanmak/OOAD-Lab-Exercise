-- Seminar Management System Database Schema
-- For MySQL (Laragon)
-- Run this script in phpMyAdmin or MySQL command line

-- Create database
CREATE DATABASE IF NOT EXISTS seminar_db;
USE seminar_db;

-- Drop existing tables (for clean setup)
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS evaluations;
DROP TABLE IF EXISTS poster_boards;
DROP TABLE IF EXISTS session_evaluators;
DROP TABLE IF EXISTS session_presenters;
DROP TABLE IF EXISTS awards;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS venues;
DROP TABLE IF EXISTS users;

-- Users table (base for all user types)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('PRESENTER', 'PANEL_MEMBER', 'COORDINATOR') NOT NULL,
    -- Student-specific fields
    student_id VARCHAR(10) UNIQUE,
    research_title VARCHAR(200),
    abstract_text TEXT,
    supervisor_name VARCHAR(100),
    presentation_type ENUM('ORAL', 'POSTER'),
    file_path VARCHAR(500),
    presenter_id VARCHAR(50),
    vote_count INT DEFAULT 0,
    has_voted BOOLEAN DEFAULT FALSE,
    -- Evaluator-specific fields
    evaluator_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Venues table
CREATE TABLE venues (
    venue_id INT AUTO_INCREMENT PRIMARY KEY,
    venue_name VARCHAR(200) NOT NULL UNIQUE,
    capacity INT,
    venue_type ENUM('AUDITORIUM', 'CONFERENCE_ROOM', 'EXHIBITION_HALL', 'LECTURE_HALL', 'MEETING_ROOM') DEFAULT 'CONFERENCE_ROOM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sessions table
CREATE TABLE sessions (
    session_id VARCHAR(50) PRIMARY KEY,
    session_date DATE NOT NULL,
    venue VARCHAR(200) NOT NULL,
    meeting_link VARCHAR(500),
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

-- Votes table (for People's Choice voting)
CREATE TABLE votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voter_student_id VARCHAR(10) NOT NULL,
    voted_for_presenter_id VARCHAR(50) NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_voter (voter_student_id)
);

-- Initialize venues
INSERT INTO venues (venue_name, capacity, venue_type) VALUES 
('Auditorium A', 500, 'AUDITORIUM'),
('Auditorium B', 300, 'AUDITORIUM'),
('Conference Room 1', 50, 'CONFERENCE_ROOM'),
('Conference Room 2', 50, 'CONFERENCE_ROOM'),
('Conference Room 3', 30, 'CONFERENCE_ROOM'),
('Exhibition Hall A', 200, 'EXHIBITION_HALL'),
('Exhibition Hall B', 150, 'EXHIBITION_HALL'),
('Lecture Hall 1', 100, 'LECTURE_HALL'),
('Lecture Hall 2', 100, 'LECTURE_HALL'),
('Meeting Room A', 20, 'MEETING_ROOM'),
('Meeting Room B', 20, 'MEETING_ROOM');

-- Initialize admin user only
-- Coordinator (admin)
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'COORDINATOR');

-- Students and Evaluators will sign up through the application

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_sessions_date ON sessions(session_date);
CREATE INDEX idx_evaluations_presenter ON evaluations(presenter_id);
CREATE INDEX idx_evaluations_evaluator ON evaluations(evaluator_id);

-- Show tables created
SHOW TABLES;

-- Display admin user
SELECT 'Admin user created successfully!' AS message;
SELECT username, role FROM users WHERE role = 'COORDINATOR';
