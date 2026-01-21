-- Migration script to change evaluator_id from INT to VARCHAR(50)
-- Run this script in phpMyAdmin or MySQL command line to update existing database

USE seminar_db;

-- Step 1: Drop foreign key constraints
ALTER TABLE session_evaluators DROP FOREIGN KEY session_evaluators_ibfk_2;
ALTER TABLE evaluations DROP FOREIGN KEY evaluations_ibfk_1;

-- Step 2: Modify session_evaluators table
ALTER TABLE session_evaluators MODIFY COLUMN evaluator_id VARCHAR(50) NOT NULL;

-- Step 3: Modify evaluations table
ALTER TABLE evaluations MODIFY COLUMN evaluator_id VARCHAR(50) NOT NULL;

-- Step 4: Clear existing data (since we need to convert INT IDs to VARCHAR evaluator IDs)
-- WARNING: This will delete all existing session evaluator assignments and evaluations
DELETE FROM session_evaluators;
DELETE FROM evaluations;

-- Done! The tables are now ready to use evaluator IDs (e.g., "EV-3c0c4fbd")
SELECT 'Migration completed successfully!' AS message;
