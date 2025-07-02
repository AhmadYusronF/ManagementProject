-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 30, 2025 at 04:34 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

TRUNCATE Table role_permission;

TRUNCATE Table permission;

ALTER TABLE permission AUTO_INCREMENT = 1;

ALTER TABLE role_permission AUTO_INCREMENT = 1;

UPDATE `roles` SET `roles_name` = 'Owner' WHERE `roles`.`roles_id` = 1;
UPDATE `roles` SET `roles_name` = 'Admin' WHERE `roles`.`roles_id` = 2;

UPDATE `roles` SET `roles_name` = 'Member' WHERE `roles`.`roles_id` = 3

INSERT INTO `role_permission` (`role_permission_id`, `fk_roles_id`, `fk_permission_id`) VALUES (NULL, '1', '1'), (NULL, '2', '2'), (NULL, '3', '3');

INSERT INTO `permission` (`permission_id`, `permission_description`) VALUES (NULL, 'Memiliki semua hak akses'), (NULL, ' Akses penuh untuk membaca, menambah, mengubah, dan menghapus project.'),(NULL, 'Pengguna umum yang hanya dapat melihat project tanpa hak akses lainnya.');

UPDATE `users` SET `fk_roles_id` = '3' WHERE `users`.`users_id` = 2413; UPDATE `users` SET `fk_roles_id` = '3' WHERE `users`.`users_id` = 2414;

DELETE FROM `roles` WHERE `roles`.`roles_id` = 4;
DELETE FROM `roles` WHERE `roles`.`roles_id` = 5;

ALTER TABLE groups_member
ADD COLUMN fk_roles_id INT,
ADD CONSTRAINT fk_roles_id_fk
FOREIGN KEY (fk_roles_id) REFERENCES roles(roles_id);

ALTER TABLE `groups_member` DROP FOREIGN KEY `fk_roles_id_fk`; ALTER TABLE `groups_member` ADD CONSTRAINT `fk_roles_id_fk` FOREIGN KEY (`fk_roles_id`) REFERENCES `roles`(`roles_id`) ON DELETE CASCADE ON UPDATE CASCADE; ALTER TABLE `groups_member` DROP FOREIGN KEY `groups_member_ibfk_1`; ALTER TABLE `groups_member` ADD CONSTRAINT `groups_member_ibfk_1` FOREIGN KEY (`fk_users_id`) REFERENCES `users`(`users_id`) ON DELETE CASCADE ON UPDATE CASCADE; ALTER TABLE `groups_member` DROP FOREIGN KEY `groups_member_ibfk_2`; ALTER TABLE `groups_member` ADD CONSTRAINT `groups_member_ibfk_2` FOREIGN KEY (`fk_groups_id`) REFERENCES `groups`(`groups_id`) ON DELETE CASCADE ON UPDATE CASCADE;




--
-- Database: `managementprojectdb`
--
-- --------------------------------------------------------
--
-- Table structure for table `account`
--
CREATE TABLE
    `account` (
        `hidden_uid` int (11) NOT NULL,
        `email` varchar(254) NOT NULL,
        `account_password` text NOT NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `account`
--
INSERT INTO
    `account` (`hidden_uid`, `email`, `account_password`)
VALUES
    (1052389471, 'alice@example.com', 'password123'),
    (1209483745, 'clara@example.com', 'claraPass789'),
    (1640293847, 'eva@example.com', 'ev@1234!'),
    (1723948573, 'david@example.com', 'd@v1dpw'),
    (1892376542, 'bob@example.com', 'securepass456'),
    (1892376543, 'ada@gmail.com', '1234'),
    (1892376546, 'Dontol', 'dontol@gmail.com'),
    (1892376547, 'ada', 'dontol1@gmail.com');

-- --------------------------------------------------------
--
-- Table structure for table `activity_logs`
--
CREATE TABLE
    `activity_logs` (
        `activity_logs_id` int (11) NOT NULL,
        `fk_groups_project_id` int (11) DEFAULT NULL,
        `fk_users_id` int (11) DEFAULT NULL,
        `activity_logs_message` text DEFAULT NULL,
        `times` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
        `action_type` varchar(10) DEFAULT NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- --------------------------------------------------------
--
-- Table structure for table `groups`
--
CREATE TABLE
    `groups` (
        `groups_id` int (11) NOT NULL,
        `fk_user_id` int (11) DEFAULT NULL,
        `group_description` text DEFAULT NULL,
        `group_name` varchar(15) DEFAULT NULL,
        `group_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
        `news_body` text DEFAULT NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `groups`
--
INSERT INTO
    `groups` (
        `groups_id`,
        `fk_user_id`,
        `group_description`,
        `group_name`,
        `group_created_at`,
        `news_body`
    )
VALUES
    (
        1,
        2414,
        'A student-run coding competition team',
        'ByteRunners',
        '2025-06-21 06:40:53',
        NULL
    );

-- --------------------------------------------------------
--
-- Table structure for table `groups_member`
--
CREATE TABLE
    `groups_member` (
        `groups_member_id` int (11) NOT NULL,
        `fk_users_id` int (11) DEFAULT NULL,
        `fk_groups_id` int (11) DEFAULT NULL,
        `joined_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP()
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `groups_member`
--
INSERT INTO
    `groups_member` (
        `groups_member_id`,
        `fk_users_id`,
        `fk_groups_id`,
        `joined_at`
    )
VALUES
    (1, 2410, 1, '2025-06-21 06:41:48'),
    (2, 2411, 1, '2025-06-21 06:41:48');

-- --------------------------------------------------------
--
-- Table structure for table `groups_project`
--
CREATE TABLE
    `groups_project` (
        `groups_project_id` int (11) NOT NULL,
        `title` varchar(150) DEFAULT NULL,
        `groups_project_description` text DEFAULT NULL,
        `fk_groups_id` int (11) DEFAULT NULL,
        `repo_url` text DEFAULT NULL,
        `groups_project_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
        `groups_project_updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP()
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `groups_project`
--
INSERT INTO
    `groups_project` (
        `groups_project_id`,
        `title`,
        `groups_project_description`,
        `fk_groups_id`,
        `repo_url`,
        `groups_project_created_at`,
        `groups_project_updated_at`
    )
VALUES
    (
        1,
        'Code Arena',
        'Platform for organizing student coding challenges',
        1,
        'https://github.com/ByteRunners/code-arena',
        '2025-06-21 06:43:43',
        '2025-06-21 06:43:43'
    ),
    (
        2,
        'Task Manager App',
        'A web-based app to manage personal and team tasks efficiently.',
        1,
        'https://github.com/group1/task-manager',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        3,
        'Weather Forecast System',
        'A system to predict and display weather information using APIs.',
        1,
        'https://github.com/group1/weather-forecast',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        4,
        'E-Commerce Website',
        'An online shopping platform with user accounts, carts, and checkout.',
        1,
        'https://github.com/group1/ecommerce-site',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        5,
        'AI Chatbot',
        'A chatbot powered by NLP for customer support automation.',
        1,
        'https://github.com/group1/ai-chatbot',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        6,
        'Library Management System',
        'A system to handle book checkouts, returns, and inventory.',
        1,
        'https://github.com/group1/library-system',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        7,
        'Online Quiz App',
        'An interactive quiz application for students and teachers.',
        1,
        'https://github.com/group1/quiz-app',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        8,
        'Fitness Tracker',
        'A mobile-friendly site to track workouts and nutrition logs.',
        1,
        'https://github.com/group1/fitness-tracker',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        9,
        'Portfolio Website',
        'A personal portfolio site for developers to showcase projects.',
        1,
        'https://github.com/group1/portfolio-site',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        10,
        'Inventory Tracker',
        'A tool for managing product inventory with alert system.',
        1,
        'https://github.com/group1/inventory-tracker',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    ),
    (
        11,
        'Expense Tracker App',
        'An application to track and visualize daily expenses.',
        1,
        'https://github.com/group1/expense-tracker',
        '2025-06-25 04:11:35',
        '2025-06-25 04:11:35'
    );

-- --------------------------------------------------------
--
-- Table structure for table `permission`
--
CREATE TABLE
    `permission` (
        `permission_id` int (11) NOT NULL,
        `permission_description` text DEFAULT NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `permission`
--
INSERT INTO
    `permission` (`permission_id`, `permission_description`)
VALUES
    (
        1,
        'Full access to all system features. Can manage users, roles, settings, and data.'
    ),
    (
        2,
        'Can create, edit, and publish content. Cannot manage users or system settings.'
    ),
    (
        3,
        'Read-only access. Can view content but cannot make any changes.'
    ),
    (
        4,
        'Can create and edit their own content but cannot publish. Needs approval.'
    ),
    (
        5,
        'Can review, approve, or reject user-generated content (e.g. comments, posts).\r\n'
    );

-- --------------------------------------------------------
--
-- Table structure for table `project_chat`
--
CREATE TABLE
    `project_chat` (
        `project_chat_id` int (11) NOT NULL,
        `fk_groups_project_id` int (11) DEFAULT NULL,
        `fk_users_id` int (11) DEFAULT NULL,
        `send_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP(),
        `project_chat_message` text DEFAULT NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

-- --------------------------------------------------------
--
-- Table structure for table `project_task`
--
CREATE TABLE
    `project_task` (
        `project_task_id` int (11) NOT NULL,
        `fk_groups_project_id` int (11) DEFAULT NULL,
        `fk_users_id` int (11) DEFAULT NULL,
        `task` varchar(200) DEFAULT NULL,
        `project_task_description` text DEFAULT NULL,
        `status` enum (
            'completed',
            'in progress',
            'pending',
            'review',
            'not started'
        ) DEFAULT NULL,
        `project_task_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP()
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `project_task`
--
INSERT INTO
    `project_task` (
        `project_task_id`,
        `fk_groups_project_id`,
        `fk_users_id`,
        `task`,
        `project_task_description`,
        `status`,
        `project_task_created_at`
    )
VALUES
    (
        1,
        1,
        2411,
        'why space',
        'Create a responsive leaderboard interface for challenge results',
        'review',
        '2025-06-21 06:45:57'
    ),
    (
        2,
        1,
        2411,
        'Eat everyone',
        'Good',
        'pending',
        '2025-06-21 11:20:51'
    ),
    (
        3,
        1,
        2411,
        'Everyone Eat Me',
        'What ?',
        'not started',
        '2025-06-21 11:26:11'
    );

-- --------------------------------------------------------
--
-- Table structure for table `roles`
--
CREATE TABLE
    `roles` (
        `roles_id` int (11) NOT NULL,
        `roles_name` varchar(100) DEFAULT NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `roles`
--
INSERT INTO
    `roles` (`roles_id`, `roles_name`)
VALUES
    (1, 'Admin'),
    (2, 'editor'),
    (3, 'Viewer'),
    (4, 'Contributor'),
    (5, 'Moderator');

-- --------------------------------------------------------
--
-- Table structure for table `role_permission`
--
CREATE TABLE
    `role_permission` (
        `role_permission_id` int (11) NOT NULL,
        `fk_roles_id` int (11) DEFAULT NULL,
        `fk_permission_id` int (11) DEFAULT NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `role_permission`
--
INSERT INTO
    `role_permission` (
        `role_permission_id`,
        `fk_roles_id`,
        `fk_permission_id`
    )
VALUES
    (1, 1, 1),
    (2, 2, 2),
    (3, 3, 3),
    (4, 4, 4),
    (5, 5, 5);

-- --------------------------------------------------------
--
-- Table structure for table `users`
--
CREATE TABLE
    `users` (
        `users_id` int (11) NOT NULL,
        `username` varchar(15) DEFAULT NULL,
        `fk_hidden_uid` int (11) DEFAULT NULL,
        `fk_roles_id` int (11) DEFAULT NULL,
        `users_created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP()
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;

--
-- Dumping data for table `users`
--
INSERT INTO
    `users` (
        `users_id`,
        `username`,
        `fk_hidden_uid`,
        `fk_roles_id`,
        `users_created_at`
    )
VALUES
    (
        2410,
        'emma.carter',
        1052389471,
        1,
        '2025-06-06 15:33:59'
    ),
    (
        2411,
        'liam.james',
        1892376542,
        2,
        '2025-06-06 15:33:59'
    ),
    (
        2412,
        'olivia.lee',
        1209483745,
        3,
        '2025-06-06 15:33:59'
    ),
    (
        2413,
        'noah.davis',
        1723948573,
        4,
        '2025-06-06 15:33:59'
    ),
    (
        2414,
        'ava.thompson',
        1640293847,
        5,
        '2025-06-06 15:33:59'
    ),
    (2416, 'ada', NULL, NULL, '2025-06-30 14:04:27'),
    (
        2417,
        '123',
        1892376547,
        NULL,
        '2025-06-30 14:32:12'
    );

--
-- Indexes for dumped tables
--
--
-- Indexes for table `account`
--
ALTER TABLE `account` ADD PRIMARY KEY (`hidden_uid`),
ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `activity_logs`
--
ALTER TABLE `activity_logs` ADD PRIMARY KEY (`activity_logs_id`),
ADD KEY `fk_groups_project_id` (`fk_groups_project_id`),
ADD KEY `fk_users_id` (`fk_users_id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups` ADD PRIMARY KEY (`groups_id`),
ADD KEY `fk_user_id` (`fk_user_id`);

--
-- Indexes for table `groups_member`
--
ALTER TABLE `groups_member` ADD PRIMARY KEY (`groups_member_id`),
ADD KEY `fk_users_id` (`fk_users_id`),
ADD KEY `fk_groups_id` (`fk_groups_id`);

--
-- Indexes for table `groups_project`
--
ALTER TABLE `groups_project` ADD PRIMARY KEY (`groups_project_id`),
ADD KEY `fk_groups_id` (`fk_groups_id`);

--
-- Indexes for table `permission`
--
ALTER TABLE `permission` ADD PRIMARY KEY (`permission_id`);

--
-- Indexes for table `project_chat`
--
ALTER TABLE `project_chat` ADD PRIMARY KEY (`project_chat_id`),
ADD KEY `fk_groups_project_id` (`fk_groups_project_id`),
ADD KEY `fk_users_id` (`fk_users_id`);

--
-- Indexes for table `project_task`
--
ALTER TABLE `project_task` ADD PRIMARY KEY (`project_task_id`),
ADD KEY `fk_groups_project_id` (`fk_groups_project_id`),
ADD KEY `fk_users_id` (`fk_users_id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles` ADD PRIMARY KEY (`roles_id`);

--
-- Indexes for table `role_permission`
--
ALTER TABLE `role_permission` ADD PRIMARY KEY (`role_permission_id`),
ADD KEY `fk_roles_id` (`fk_roles_id`),
ADD KEY `fk_permission_id` (`fk_permission_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users` ADD PRIMARY KEY (`users_id`),
ADD KEY `fk_hidden_uid` (`fk_hidden_uid`),
ADD KEY `fk_roles_id` (`fk_roles_id`);

--
-- AUTO_INCREMENT for dumped tables
--
--
-- AUTO_INCREMENT for table `account`
--
ALTER TABLE `account` MODIFY `hidden_uid` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 1892376548;

--
-- AUTO_INCREMENT for table `activity_logs`
--
ALTER TABLE `activity_logs` MODIFY `activity_logs_id` int (11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups` MODIFY `groups_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 2;

--
-- AUTO_INCREMENT for table `groups_member`
--
ALTER TABLE `groups_member` MODIFY `groups_member_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 3;

--
-- AUTO_INCREMENT for table `groups_project`
--
ALTER TABLE `groups_project` MODIFY `groups_project_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 12;

--
-- AUTO_INCREMENT for table `permission`
--
ALTER TABLE `permission` MODIFY `permission_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 6;

--
-- AUTO_INCREMENT for table `project_chat`
--
ALTER TABLE `project_chat` MODIFY `project_chat_id` int (11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `project_task`
--
ALTER TABLE `project_task` MODIFY `project_task_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 4;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles` MODIFY `roles_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 6;

--
-- AUTO_INCREMENT for table `role_permission`
--
ALTER TABLE `role_permission` MODIFY `role_permission_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users` MODIFY `users_id` int (11) NOT NULL AUTO_INCREMENT,
AUTO_INCREMENT = 2418;

--
-- Constraints for dumped tables
--
--
-- Constraints for table `activity_logs`
--
ALTER TABLE `activity_logs` ADD CONSTRAINT `activity_logs_ibfk_1` FOREIGN KEY (`fk_groups_project_id`) REFERENCES `groups_project` (`groups_project_id`),
ADD CONSTRAINT `activity_logs_ibfk_2` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`);

--
-- Constraints for table `groups`
--
ALTER TABLE `groups` ADD CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`fk_user_id`) REFERENCES `users` (`users_id`);

--
-- Constraints for table `groups_member`
--
ALTER TABLE `groups_member` ADD CONSTRAINT `groups_member_ibfk_1` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`),
ADD CONSTRAINT `groups_member_ibfk_2` FOREIGN KEY (`fk_groups_id`) REFERENCES `groups` (`groups_id`);

--
-- Constraints for table `groups_project`
--
ALTER TABLE `groups_project` ADD CONSTRAINT `groups_project_ibfk_1` FOREIGN KEY (`fk_groups_id`) REFERENCES `groups` (`groups_id`);

--
-- Constraints for table `project_chat`
--
ALTER TABLE `project_chat` ADD CONSTRAINT `project_chat_ibfk_1` FOREIGN KEY (`fk_groups_project_id`) REFERENCES `groups_project` (`groups_project_id`),
ADD CONSTRAINT `project_chat_ibfk_2` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`);

--
-- Constraints for table `project_task`
--
ALTER TABLE `project_task` ADD CONSTRAINT `project_task_ibfk_1` FOREIGN KEY (`fk_groups_project_id`) REFERENCES `groups_project` (`groups_project_id`),
ADD CONSTRAINT `project_task_ibfk_2` FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`);

--
-- Constraints for table `role_permission`
--
ALTER TABLE `role_permission` ADD CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`fk_roles_id`) REFERENCES `roles` (`roles_id`),
ADD CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`fk_permission_id`) REFERENCES `permission` (`permission_id`);

--
-- Constraints for table `users`
--
ALTER TABLE `users` ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`fk_hidden_uid`) REFERENCES `account` (`hidden_uid`),
ADD CONSTRAINT `users_ibfk_2` FOREIGN KEY (`fk_roles_id`) REFERENCES `roles` (`roles_id`);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;

/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- update baru data base 30 jun
ALTER TABLE `account` CHANGE `hidden_uid` `hidden_uid` int (11) NOT NULL AUTO_INCREMENT;

ALTER TABLE users
DROP COLUMN avatar;

CREATE TABLE
    `group_chat` (
        `group_chat_id` INT (11) NOT NULL AUTO_INCREMENT,
        `fk_groups_id` INT (11) DEFAULT NULL,
        `fk_users_id` INT (11) DEFAULT NULL,
        `message` TEXT DEFAULT NULL,
        `sent_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
        PRIMARY KEY (`group_chat_id`),
        FOREIGN KEY (`fk_groups_id`) REFERENCES `groups` (`groups_id`),
        FOREIGN KEY (`fk_users_id`) REFERENCES `users` (`users_id`)
    );

DROP TABLE project_chat;

ALTER TABLE `groups`
ADD COLUMN group_news TEXT COMMENT 'add news in group';

-- update 3juli
ALTER TABLE `activity_logs`
DROP `action_type`;

ALTER TABLE activity_logs
ADD COLUMN project_task_id INT;

ALTER TABLE activity_logs ADD CONSTRAINT fk_task_id FOREIGN KEY (project_task_id) REFERENCES project_task (project_task_id);

ALTER TABLE `project_task` CHANGE `status` `status` INT NULL DEFAULT NULL;