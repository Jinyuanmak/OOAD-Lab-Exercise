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
TRUNCATE TABLE users;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Re-initialize poster boards (B001 to B020)
INSERT INTO poster_boards (board_id) VALUES 
('B001'), ('B002'), ('B003'), ('B004'), ('B005'),
('B006'), ('B007'), ('B008'), ('B009'), ('B010'),
('B011'), ('B012'), ('B013'), ('B014'), ('B015'),
('B016'), ('B017'), ('B018'), ('B019'), ('B020');

SELECT 'Database cleared successfully! Run the application to load fresh sample data.' AS message;
