-- Migration script to add meeting_link column to sessions table
-- Run this in phpMyAdmin if you already have an existing database

USE seminar_db;

-- Add meeting_link column to sessions table
ALTER TABLE sessions 
ADD COLUMN meeting_link VARCHAR(500) AFTER venue;

-- For existing ORAL sessions, you may want to set a default meeting link
-- UPDATE sessions SET meeting_link = 'https://teams.microsoft.com/l/meetup-join/19%3ameeting_OTg1NTM4OGUtN2Y0OC00NjMwLWJiMjYtYTljMDVkM2E2NTFl%40thread.v2/0?context=%7b%22Tid%22%3a%227e0b5fcf-12c4-4eff-96b6-4664f25dc7da%22%2c%22Oid%22%3a%229f19ed86-c108-4857-ae8e-9c72364e6f5d%22%7d' 
-- WHERE session_type = 'ORAL';
