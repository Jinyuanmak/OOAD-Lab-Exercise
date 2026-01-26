-- Migration script to add voting system to existing database
-- Run this if you already have data and don't want to recreate the database

USE seminar_db;

-- Add vote_count and has_voted columns to users table
ALTER TABLE users 
ADD COLUMN vote_count INT DEFAULT 0 AFTER presenter_id,
ADD COLUMN has_voted BOOLEAN DEFAULT FALSE AFTER vote_count;

-- Create votes table
CREATE TABLE IF NOT EXISTS votes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voter_student_id VARCHAR(10) NOT NULL,
    voted_for_presenter_id VARCHAR(50) NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_voter (voter_student_id)
);

-- Show success message
SELECT 'Voting system added successfully!' AS message;
