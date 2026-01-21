-- Clear all data from Seminar Management System
-- Run this in phpMyAdmin or MySQL command line

USE seminar_db;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Clear all tables
TRUNCATE TABLE evaluations;
TRUNCATE TABLE poster_boards;
TRUNCATE TABLE session_evaluators;
TRUNCATE TABLE session_presenters;
TRUNCATE TABLE awards;
TRUNCATE TABLE sessions;
TRUNCATE TABLE venues;
TRUNCATE TABLE users;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Re-initialize venues
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

-- Re-initialize admin user only
-- Coordinator (admin)
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'COORDINATOR');

-- Students and Evaluators will sign up through the application

SELECT 'Database cleared and admin user reloaded successfully!' AS message;
