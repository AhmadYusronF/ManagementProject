-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 02, 2025 at 01:41 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `managementprojectdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `hidden_uid` int(11) NOT NULL,
  `email` varchar(254) NOT NULL,
  `account_password` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `activity_logs`
--

CREATE TABLE `activity_logs` (
  `activity_logs_id` int(11) NOT NULL,
  `fk_groups_project_id` int(11) DEFAULT NULL,
  `fk_users_id` int(11) DEFAULT NULL,
  `activity_logs_message` text DEFAULT NULL,
  `times` timestamp NOT NULL DEFAULT current_timestamp(),
  `project_task_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
  `groups_id` int(11) NOT NULL,
  `fk_user_id` int(11) DEFAULT NULL,
  `group_description` text DEFAULT NULL,
  `group_name` varchar(15) DEFAULT NULL,
  `group_created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `group_news` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `groups_member`
--

CREATE TABLE `groups_member` (
  `groups_member_id` int(11) NOT NULL,
  `fk_users_id` int(11) DEFAULT NULL,
  `fk_groups_id` int(11) DEFAULT NULL,
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `fk_roles_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `groups_project`
--

CREATE TABLE `groups_project` (
  `groups_project_id` int(11) NOT NULL,
  `title` varchar(150) DEFAULT NULL,
  `groups_project_description` text DEFAULT NULL,
  `fk_groups_id` int(11) DEFAULT NULL,
  `repo_url` text DEFAULT NULL,
  `groups_project_created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `groups_project_updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `group_chat`
--

CREATE TABLE `group_chat` (
  `group_chat_id` int(11) NOT NULL,
  `fk_groups_id` int(11) DEFAULT NULL,
  `fk_users_id` int(11) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `permission`
--

CREATE TABLE `permission` (
  `permission_id` int(11) NOT NULL,
  `permission_description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `permission`
--

INSERT INTO `permission` (`permission_id`, `permission_description`) VALUES
(1, 'orang yang membuat proyek atau ruang kerja. Ia memiliki kendali penuh atas seluruh sistem.'),
(2, 'Pengguna yang dipercaya untuk membantu mengelola proyek, namun tidak memiliki kekuasaan penuh seperti Pemilik.'),
(3, 'pengguna biasa yang berperan sebagai kontributor dalam proyek. Hak aksesnya terbatas agar fokus pada tugas dan kolaborasi.');

-- --------------------------------------------------------

--
-- Table structure for table `project_task`
--

CREATE TABLE `project_task` (
  `project_task_id` int(11) NOT NULL,
  `fk_groups_project_id` int(11) DEFAULT NULL,
  `fk_users_id` int(11) DEFAULT NULL,
  `task` varchar(200) DEFAULT NULL,
  `project_task_created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `roles_id` int(11) NOT NULL,
  `roles_name` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`roles_id`, `roles_name`) VALUES
(1, 'Owner'),
(2, 'Admin'),
(3, 'Member');

-- --------------------------------------------------------

--
-- Table structure for table `role_permission`
--

CREATE TABLE `role_permission` (
  `role_permission_id` int(11) NOT NULL,
  `fk_roles_id` int(11) DEFAULT NULL,
  `fk_permission_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `role_permission`
--

INSERT INTO `role_permission` (`role_permission_id`, `fk_roles_id`, `fk_permission_id`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `users_id` int(11) NOT NULL,
  `username` varchar(15) DEFAULT NULL,
  `fk_hidden_uid` int(11) DEFAULT NULL,
  `fk_roles_id` int(11) DEFAULT NULL,
  `users_created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`hidden_uid`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `activity_logs`
--
ALTER TABLE `activity_logs`
  ADD PRIMARY KEY (`activity_logs_id`),
  ADD KEY `fk_groups_project_id` (`fk_groups_project_id`),
  ADD KEY `fk_users_id` (`fk_users_id`),
  ADD KEY `fk_task_id` (`project_task_id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`groups_id`),
  ADD KEY `fk_user_id` (`fk_user_id`);

--
-- Indexes for table `groups_member`
--
ALTER TABLE `groups_member`
  ADD PRIMARY KEY (`groups_member_id`),
  ADD KEY `fk_users_id` (`fk_users_id`),
  ADD KEY `fk_groups_id` (`fk_groups_id`),
  ADD KEY `fk_roles_id_fk` (`fk_roles_id`);

--
-- Indexes for table `groups_project`
--
ALTER TABLE `groups_project`
  ADD PRIMARY KEY (`groups_project_id`),
  ADD KEY `fk_groups_id` (`fk_groups_id`);

--
-- Indexes for table `group_chat`
--
ALTER TABLE `group_chat`
  ADD PRIMARY KEY (`group_chat_id`),
  ADD KEY `fk_groups_id` (`fk_groups_id`),
  ADD KEY `fk_users_id` (`fk_users_id`);

--
-- Indexes for table `permission`
--
ALTER TABLE `permission`
  ADD PRIMARY KEY (`permission_id`);

--
-- Indexes for table `project_task`
--
ALTER TABLE `project_task`
  ADD PRIMARY KEY (`project_task_id`),
  ADD KEY `fk_groups_project_id` (`fk_groups_project_id`),
  ADD KEY `fk_users_id` (`fk_users_id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`roles_id`);

--
-- Indexes for table `role_permission`
--
ALTER TABLE `role_permission`
  ADD PRIMARY KEY (`role_permission_id`),
  ADD KEY `fk_roles_id` (`fk_roles_id`),
  ADD KEY `fk_permission_id` (`fk_permission_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`users_id`),
  ADD KEY `fk_hidden_uid` (`fk_hidden_uid`),
  ADD KEY `fk_roles_id` (`fk_roles_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account`
  MODIFY `hidden_uid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `activity_logs`
--
ALTER TABLE `activity_logs`
  MODIFY `activity_logs_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `groups_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `groups_member`
--
ALTER TABLE `groups_member`
  MODIFY `groups_member_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `groups_project`
--
ALTER TABLE `groups_project`
  MODIFY `groups_project_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `group_chat`
--
ALTER TABLE `group_chat`
  MODIFY `group_chat_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permission`
--
ALTER TABLE `permission`
  MODIFY `permission_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `project_task`
--
ALTER TABLE `project_task`
  MODIFY `project_task_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `roles_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `role_permission`
--
ALTER TABLE `role_permission`
  MODIFY `role_permission_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `users_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activity_logs`
--
ALTER TABLE `activity_logs`
  ADD CONSTRAINT `activity_logs_ibfk_1` FOREIGN KEY (`fk_groups_project_id`) REFERENCES `groups_project` (`groups_project_id`),
  ADD CONSTRAINT `activity_logs_ibfk_2` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`),
  ADD CONSTRAINT `fk_task_id` FOREIGN KEY (`project_task_id`) REFERENCES `project_task` (`project_task_id`);

--
-- Constraints for table `groups`
--
ALTER TABLE `groups`
  ADD CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`fk_user_id`) REFERENCES `users` (`users_id`);

--
-- Constraints for table `groups_member`
--
ALTER TABLE `groups_member`
  ADD CONSTRAINT `fk_roles_id_fk` FOREIGN KEY (`fk_roles_id`) REFERENCES `roles` (`roles_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `groups_member_ibfk_1` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `groups_member_ibfk_2` FOREIGN KEY (`fk_groups_id`) REFERENCES `groups` (`groups_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `groups_project`
--
ALTER TABLE `groups_project`
  ADD CONSTRAINT `groups_project_ibfk_1` FOREIGN KEY (`fk_groups_id`) REFERENCES `groups` (`groups_id`);

--
-- Constraints for table `group_chat`
--
ALTER TABLE `group_chat`
  ADD CONSTRAINT `group_chat_ibfk_1` FOREIGN KEY (`fk_groups_id`) REFERENCES `groups` (`groups_id`),
  ADD CONSTRAINT `group_chat_ibfk_2` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`);

--
-- Constraints for table `project_task`
--
ALTER TABLE `project_task`
  ADD CONSTRAINT `project_task_ibfk_1` FOREIGN KEY (`fk_groups_project_id`) REFERENCES `groups_project` (`groups_project_id`),
  ADD CONSTRAINT `project_task_ibfk_2` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`);

--
-- Constraints for table `role_permission`
--
ALTER TABLE `role_permission`
  ADD CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`fk_roles_id`) REFERENCES `roles` (`roles_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`fk_permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`fk_hidden_uid`) REFERENCES `account` (`hidden_uid`),
  ADD CONSTRAINT `users_ibfk_2` FOREIGN KEY (`fk_roles_id`) REFERENCES `roles` (`roles_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
