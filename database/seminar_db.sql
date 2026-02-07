-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for seminar_db
CREATE DATABASE IF NOT EXISTS `seminar_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `seminar_db`;

-- Dumping structure for table seminar_db.awards
CREATE TABLE IF NOT EXISTS `awards` (
  `id` int NOT NULL AUTO_INCREMENT,
  `award_type` enum('BEST_ORAL','BEST_POSTER','PEOPLES_CHOICE') NOT NULL,
  `winner_id` varchar(50) NOT NULL,
  `score` double NOT NULL,
  `ceremony_date` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.awards: ~0 rows (approximately)
DELETE FROM `awards`;

-- Dumping structure for table seminar_db.evaluations
CREATE TABLE IF NOT EXISTS `evaluations` (
  `evaluation_id` varchar(50) NOT NULL,
  `presenter_id` varchar(50) NOT NULL,
  `evaluator_id` varchar(50) NOT NULL,
  `session_id` varchar(50) NOT NULL,
  `problem_clarity` int NOT NULL,
  `methodology` int NOT NULL,
  `results` int NOT NULL,
  `presentation` int NOT NULL,
  `comments` text,
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`evaluation_id`),
  KEY `session_id` (`session_id`),
  KEY `idx_evaluations_presenter` (`presenter_id`),
  KEY `idx_evaluations_evaluator` (`evaluator_id`),
  CONSTRAINT `evaluations_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE CASCADE,
  CONSTRAINT `evaluations_chk_1` CHECK ((`problem_clarity` between 1 and 10)),
  CONSTRAINT `evaluations_chk_2` CHECK ((`methodology` between 1 and 10)),
  CONSTRAINT `evaluations_chk_3` CHECK ((`results` between 1 and 10)),
  CONSTRAINT `evaluations_chk_4` CHECK ((`presentation` between 1 and 10))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.evaluations: ~0 rows (approximately)
DELETE FROM `evaluations`;

-- Dumping structure for table seminar_db.poster_boards
CREATE TABLE IF NOT EXISTS `poster_boards` (
  `board_id` varchar(20) NOT NULL,
  `presenter_id` varchar(50) DEFAULT NULL,
  `session_id` varchar(50) DEFAULT NULL,
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`board_id`),
  KEY `session_id` (`session_id`),
  CONSTRAINT `poster_boards_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.poster_boards: ~0 rows (approximately)
DELETE FROM `poster_boards`;

-- Dumping structure for table seminar_db.sessions
CREATE TABLE IF NOT EXISTS `sessions` (
  `session_id` varchar(50) NOT NULL,
  `session_date` date NOT NULL,
  `venue` varchar(200) NOT NULL,
  `meeting_link` varchar(500) DEFAULT NULL,
  `session_type` enum('ORAL','POSTER') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`session_id`),
  KEY `idx_sessions_date` (`session_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.sessions: ~3 rows (approximately)
DELETE FROM `sessions`;
INSERT INTO `sessions` (`session_id`, `session_date`, `venue`, `meeting_link`, `session_type`, `created_at`, `updated_at`) VALUES
	('3db6eac7', '2026-02-07', 'ONLINE', 'https://teams.microsoft.com/l/meetup-join/19%3ameeting_OTg1NTM4OGUtN2Y0OC00NjMwLWJiMjYtYTljMDVkM2E2NTFl%40thread.v2/0?context=%7b%22Tid%22%3a%227e0b5fcf-12c4-4eff-96b6-4664f25dc7da%22%2c%22Oid%22%3a%229f19ed86-c108-4857-ae8e-9c72364e6f5d%22%7d', 'ORAL', '2026-02-07 02:15:59', '2026-02-07 02:15:59'),
	('4e2a6ca6', '2026-02-07', 'Auditorium C', NULL, 'POSTER', '2026-02-07 02:16:24', '2026-02-07 02:16:24'),
	('8c23f153', '2026-02-07', 'Auditorium A', NULL, 'POSTER', '2026-02-07 02:16:07', '2026-02-07 02:16:07');

-- Dumping structure for table seminar_db.session_evaluators
CREATE TABLE IF NOT EXISTS `session_evaluators` (
  `id` int NOT NULL AUTO_INCREMENT,
  `session_id` varchar(50) NOT NULL,
  `evaluator_id` varchar(50) NOT NULL,
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_session_evaluator` (`session_id`,`evaluator_id`),
  CONSTRAINT `session_evaluators_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.session_evaluators: ~2 rows (approximately)
DELETE FROM `session_evaluators`;
INSERT INTO `session_evaluators` (`id`, `session_id`, `evaluator_id`, `assigned_at`) VALUES
	(1, '4e2a6ca6', 'EV-2b028541', '2026-02-07 02:16:43'),
	(2, '3db6eac7', 'EV-001b6eeb', '2026-02-07 02:16:47');

-- Dumping structure for table seminar_db.session_presenters
CREATE TABLE IF NOT EXISTS `session_presenters` (
  `id` int NOT NULL AUTO_INCREMENT,
  `session_id` varchar(50) NOT NULL,
  `presenter_id` varchar(50) NOT NULL,
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_session_presenter` (`session_id`,`presenter_id`),
  CONSTRAINT `session_presenters_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `sessions` (`session_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.session_presenters: ~2 rows (approximately)
DELETE FROM `session_presenters`;
INSERT INTO `session_presenters` (`id`, `session_id`, `presenter_id`, `assigned_at`) VALUES
	(2, '4e2a6ca6', 'P-7caeda11', '2026-02-07 02:16:43'),
	(4, '3db6eac7', 'P-24695d0c', '2026-02-07 02:16:47');

-- Dumping structure for table seminar_db.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('PRESENTER','PANEL_MEMBER','COORDINATOR') NOT NULL,
  `student_id` varchar(10) DEFAULT NULL,
  `research_title` varchar(200) DEFAULT NULL,
  `abstract_text` text,
  `supervisor_name` varchar(100) DEFAULT NULL,
  `presentation_type` enum('ORAL','POSTER') DEFAULT NULL,
  `file_path` varchar(500) DEFAULT NULL,
  `presenter_id` varchar(50) DEFAULT NULL,
  `vote_count` int DEFAULT '0',
  `has_voted` tinyint(1) DEFAULT '0',
  `evaluator_id` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `student_id` (`student_id`),
  KEY `idx_users_username` (`username`),
  KEY `idx_users_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.users: ~7 rows (approximately)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `username`, `password`, `role`, `student_id`, `research_title`, `abstract_text`, `supervisor_name`, `presentation_type`, `file_path`, `presenter_id`, `vote_count`, `has_voted`, `evaluator_id`, `created_at`, `updated_at`) VALUES
	(1, 'admin', 'admin123', 'COORDINATOR', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, '2026-02-07 02:12:22', '2026-02-07 02:12:22'),
	(2, 'student1', 'stud123', 'PRESENTER', '243UC246D6', 'Test', 'Testing', 'eval1', 'ORAL', NULL, 'P-24695d0c', 0, 0, NULL, '2026-02-07 02:13:38', '2026-02-07 02:15:16'),
	(3, 'student2', 'stud123', 'PRESENTER', '243UC245XN', 'Test 2', 'Testing', 'eval2', 'POSTER', NULL, 'P-7caeda11', 0, 0, NULL, '2026-02-07 02:13:58', '2026-02-07 02:15:41'),
	(4, 'student3', 'stud123', 'PRESENTER', '243UC2460X', NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, '2026-02-07 02:14:16', '2026-02-07 02:14:16'),
	(5, 'student4', 'stud123', 'PRESENTER', '243UC246J8', NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, '2026-02-07 02:14:34', '2026-02-07 02:14:34'),
	(6, 'eval1', 'eval123', 'PANEL_MEMBER', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 'EV-001b6eeb', '2026-02-07 02:14:45', '2026-02-07 02:14:45'),
	(7, 'eval2', 'eval123', 'PANEL_MEMBER', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, 'EV-2b028541', '2026-02-07 02:14:57', '2026-02-07 02:14:57');

-- Dumping structure for table seminar_db.venues
CREATE TABLE IF NOT EXISTS `venues` (
  `venue_id` int NOT NULL AUTO_INCREMENT,
  `venue_name` varchar(200) NOT NULL,
  `capacity` int DEFAULT NULL,
  `venue_type` enum('AUDITORIUM','CONFERENCE_ROOM','EXHIBITION_HALL','LECTURE_HALL','MEETING_ROOM') DEFAULT 'CONFERENCE_ROOM',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`venue_id`),
  UNIQUE KEY `venue_name` (`venue_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.venues: ~11 rows (approximately)
DELETE FROM `venues`;
INSERT INTO `venues` (`venue_id`, `venue_name`, `capacity`, `venue_type`, `created_at`) VALUES
	(1, 'Auditorium A', 500, 'AUDITORIUM', '2026-02-07 02:12:22'),
	(2, 'Auditorium B', 300, 'AUDITORIUM', '2026-02-07 02:12:22'),
	(3, 'Conference Room 1', 50, 'CONFERENCE_ROOM', '2026-02-07 02:12:22'),
	(4, 'Conference Room 2', 50, 'CONFERENCE_ROOM', '2026-02-07 02:12:22'),
	(5, 'Conference Room 3', 30, 'CONFERENCE_ROOM', '2026-02-07 02:12:22'),
	(6, 'Exhibition Hall A', 200, 'EXHIBITION_HALL', '2026-02-07 02:12:22'),
	(7, 'Exhibition Hall B', 150, 'EXHIBITION_HALL', '2026-02-07 02:12:22'),
	(8, 'Lecture Hall 1', 100, 'LECTURE_HALL', '2026-02-07 02:12:22'),
	(9, 'Lecture Hall 2', 100, 'LECTURE_HALL', '2026-02-07 02:12:22'),
	(10, 'Meeting Room A', 20, 'MEETING_ROOM', '2026-02-07 02:12:22'),
	(11, 'Meeting Room B', 20, 'MEETING_ROOM', '2026-02-07 02:12:22');

-- Dumping structure for table seminar_db.votes
CREATE TABLE IF NOT EXISTS `votes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `voter_student_id` varchar(10) NOT NULL,
  `voted_for_presenter_id` varchar(50) NOT NULL,
  `voted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_voter` (`voter_student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table seminar_db.votes: ~0 rows (approximately)
DELETE FROM `votes`;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
