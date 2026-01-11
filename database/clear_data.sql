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

-- Re-initialize poster boards (B001 to B020)
INSERT INTO poster_boards (board_id) VALUES 
('B001'), ('B002'), ('B003'), ('B004'), ('B005'),
('B006'), ('B007'), ('B008'), ('B009'), ('B010'),
('B011'), ('B012'), ('B013'), ('B014'), ('B015'),
('B016'), ('B017'), ('B018'), ('B019'), ('B020');

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

-- Re-initialize sample users
-- Coordinator (admin)
INSERT INTO users (id, username, password, role) VALUES 
('U-admin01', 'admin', 'admin123', 'COORDINATOR');

-- Evaluators/Supervisors (with evaluator_id)
INSERT INTO users (id, username, password, role, evaluator_id) VALUES 
('U-eval001', 'Dr. Sarah Johnson', 'eval123', 'EVALUATOR', 'EV-eval001'),
('U-eval002', 'Prof. Michael Chen', 'eval123', 'EVALUATOR', 'EV-eval002'),
('U-eval003', 'Dr. Emily Rodriguez', 'eval123', 'EVALUATOR', 'EV-eval003'),
('U-eval004', 'Prof. David Kumar', 'eval123', 'EVALUATOR', 'EV-eval004'),
('U-eval005', 'Dr. Lisa Anderson', 'eval123', 'EVALUATOR', 'EV-eval005');

-- Students (no presenter_id initially - added when they register for seminar)
INSERT INTO users (id, username, password, role) VALUES 
('U-stud001', 'student1', 'stud123', 'STUDENT'),
('U-stud002', 'student2', 'stud123', 'STUDENT'),
('U-stud003', 'student3', 'stud123', 'STUDENT'),
('U-stud004', 'student4', 'stud123', 'STUDENT');

SELECT 'Database cleared and sample data reloaded successfully!' AS message;
