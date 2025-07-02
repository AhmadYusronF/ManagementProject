package com.menejementpj.db;

import com.menejementpj.App;
import com.menejementpj.auth.UserData;
import com.menejementpj.components.PopUpAlert;
import com.menejementpj.test.Debug;
import com.menejementpj.type.*;
import com.menejementpj.model.ActivityLog;
import com.menejementpj.model.ChatMessage;
import com.menejementpj.model.Group;
import com.menejementpj.model.MemberTask;
import com.menejementpj.model.Project;
import com.menejementpj.model.ProjectTask;
import com.menejementpj.model.Role;
import com.menejementpj.model.TaskSelection;
import com.menejementpj.model.User;
import com.menejementpj.model.GroupMemberDisplay;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3307/menejementproject";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            return null;
        }
    }

    public static int authenticateUser(String email, String account_password) {
        Debug.log("Memulai Menjalankan autentifikator");
        int hidden_uid = -1;
        String sql = "SELECT hidden_uid FROM account WHERE email = ? AND account_password = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, account_password);
            Debug.log("melakukan Hit ke database (account) :" + email + " " + account_password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                hidden_uid = rs.getInt("hidden_uid");
            }
        } catch (SQLException | NullPointerException e) {
            Debug.log("Terjadi kesalahan " + e.getLocalizedMessage());
        }
        Debug.log("Mendapatkan hidden_uid" + hidden_uid);
        return hidden_uid;
    }

    public static String getDataGroub() {
        String query = "";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

        } catch (Exception e) {

        }
        return "tes";
    }

    public static void getUserSession(int hiddenUserID) {
        Debug.log("Memulai mendapatkan UserSession");
        String query = "SELECT ac.hidden_uid, u.users_id, gm.fk_groups_id " +
                "FROM account ac " +
                "INNER JOIN users u ON u.fk_hidden_uid = ac.hidden_uid " +
                "LEFT JOIN groups_member gm ON gm.fk_users_id = u.users_id " +
                "WHERE ac.hidden_uid = ?";

        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, hiddenUserID);
            Debug.log("Melakukan hit Database (account) : " + hiddenUserID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int hiddenUid = rs.getInt("hidden_uid");
                int userId = rs.getInt("users_id");
                int groupId = rs.getInt("fk_groups_id");

                App.userSession.setUserSession(hiddenUid, userId, groupId);
            } else {
                Debug.log("Gagal Mendapat data Tidak Diketahui");
                PopUpAlert.popupInfo("Info", "User Id Not Found", "User ID anda Tidak ditemukan1");
            }
        } catch (Exception e) {
            Debug.log("Gagal Mendapat data : " + e.getMessage());
            PopUpAlert.popupErr("Error", "Error Query", "Error : " + e.getMessage());
        }
    }

    public static void getUserProfile(int userID) {
        String query = "SELECT u.username, r.roles_name FROM users u " +
                "INNER JOIN roles r ON r.roles_id = u.fk_roles_id " +
                "WHERE u.users_id = ?";

        Debug.log("Query getUserProfile() dimulai dengan userID = " + userID);

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userID);
            Debug.log("Parameter userID berhasil diset di PreparedStatement");

            ResultSet rs = null;
            try {
                rs = pstmt.executeQuery();
            } catch (Exception exQuery) {
                Debug.log("Query gagal dijalankan: " + exQuery.getMessage());
                PopUpAlert.popupErr("Error", "Query Error", "Query tidak bisa dijalankan: " + exQuery.getMessage());
                return;
            }

            if (rs != null && rs.next()) {
                String username = rs.getString("username");
                String roleName = rs.getString("roles_name");

                Debug.log("User ditemukan: " + username + " dengan role: " + roleName);

                App.user.setUserData(username, roleName);
                Debug.log("Data user berhasil diset ke App.user");
            } else {
                Debug.log("User ID tidak ditemukan atau ResultSet kosong/null");
                PopUpAlert.popupInfo("Info", "User Id Not Found", "User ID anda Tidak ditemukan!");
            }

        } catch (Exception e) {
            Debug.log("Terjadi error saat query: " + e.getMessage());
            PopUpAlert.popupErr("Error", "Error Query", "Error : " + e.getMessage());
        }

        Debug.log("Selesai menjalankan getUserProfile()");
    }

    public static List<TaskItem> getTaskItems(int userID) {
        List<TaskItem> taskItems = new ArrayList<>();
        String sql = "SELECT task, status FROM project_task WHERE fk_users_id = ?";

        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String task = rs.getString("task");
                String status = rs.getString("status");
                taskItems.add(new TaskItem(task, status));
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Error fetching tasks from database for user ID '" + userID + "': " + e.getMessage());
        }
        return taskItems;
    }

    public static boolean daftarAkunDanPengguna(String email, String password, String username) {
        Connection conn = null;

        String sqlInsertAccount = "INSERT INTO account (email, account_password) VALUES (?, ?)";

        String sqlInsertUser = "INSERT INTO users (username, fk_hidden_uid,fk_roles_id) VALUES (?, ?, ?)";

        try {
            conn = DatabaseManager.connect();

            conn.setAutoCommit(false);

            long generatedHiddenUid = -1;

            try (PreparedStatement stmtAccount = conn.prepareStatement(sqlInsertAccount,
                    Statement.RETURN_GENERATED_KEYS)) {

                stmtAccount.setString(1, email);
                stmtAccount.setString(2, password);

                stmtAccount.executeUpdate();

                try (ResultSet generatedKeys = stmtAccount.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedHiddenUid = generatedKeys.getLong(1);
                        System.out.println("Akun berhasil dibuat dengan hidden_uid: " + generatedHiddenUid);
                    } else {
                        throw new SQLException("Gagal membuat akun, tidak ada ID yang di-generate.");
                    }
                }
            }

            try (PreparedStatement stmtUser = conn.prepareStatement(sqlInsertUser)) {

                stmtUser.setString(1, username);
                stmtUser.setLong(2, generatedHiddenUid);
                stmtUser.setInt(3, 3);
                stmtUser.executeUpdate();
                System.out.println("Data pengguna berhasil dibuat untuk username: " + username);
            }

            conn.commit();
            System.out.println("SUKSES: Pendaftaran selesai.");
            return true;

        } catch (SQLException e) {
            System.err.println("KESALAHAN: Transaksi akan di-rollback.");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    PopUpAlert.popupErr("Error", "Error Rollback SQL", "Error : " + e.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    PopUpAlert.popupErr("Error", "Error Close SQL", "Error : " + ex.getMessage());
                }
            }
        }
    }

    public static List<Project> getProjectGrup() {

        List<Project> projects = new ArrayList<>();

        String query = "SELECT groups_project_id, title, groups_project_description, groups_project_created_at, groups_project_updated_at, repo_url FROM groups_project WHERE fk_groups_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query);) {
            pstmt.setInt(1, App.userSession.getCurrentLoggedInGroupID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("groups_project_id");
                String title = rs.getString("title");
                String describ = rs.getString("groups_project_description");
                LocalDate createAt = rs.getDate("groups_project_created_at").toLocalDate();
                LocalDate updateAt = rs.getDate("groups_project_updated_at").toLocalDate();
                String repo = rs.getString("repo_url");
                projects.add(new Project(title, describ, createAt, updateAt, repo, id));
            }
            Debug.success("Data Project Berhasil: " + projects);
            return projects;
        } catch (Exception e) {
            Debug.error("Gagal dapat project" + e.getMessage());
            return null;
        }
    }

    public static boolean sendMessage(String message) { // Changed return type to boolean
        String sql = "INSERT INTO group_chat (fk_groups_id, fk_users_id, message) VALUES (?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, App.userSession.getCurrentLoggedInGroupID());
            pstmt.setInt(2, App.userSession.getCurrentLoggedInUserID());
            pstmt.setString(3, message);
            int rowsAffected = pstmt.executeUpdate(); // Get rows affected
            return rowsAffected > 0; // Return true on success
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false on error
        }
    }

    public static List<ChatMessage> getChatMessages(int groupId) {

        String sql = "SELECT gc.fk_users_id, u.username, gc.message, gc.sent_at " +
                "FROM group_chat gc " +
                "JOIN users u ON gc.fk_users_id = u.users_id " +
                "WHERE gc.fk_groups_id = ? " +
                "ORDER BY gc.sent_at ASC";

        List<ChatMessage> messages = new ArrayList<>();

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(new ChatMessage(
                        rs.getInt("fk_users_id"),
                        rs.getString("username"),
                        rs.getString("message"),
                        rs.getTimestamp("sent_at").toLocalDateTime()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return messages;
    }

    public static void getDataGroubA() {
        String query = "SELECT * FROM `groups` WHERE groups_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, App.userSession.getCurrentLoggedInGroupID());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                App.mygroup.id = rs.getInt("groups_id");
                App.mygroup.nama = rs.getString("group_name");
                App.mygroup.createAt = rs.getDate("group_created_at").toLocalDate();
                App.mygroup.describ = rs.getString("group_description");
                App.mygroup.news = rs.getString("group_news");
            }
            Debug.success("sukses dapat data");
        } catch (Exception e) {
            Debug.error("Data Groubmy tidak ditemukan" + e.getMessage());
        }
    }

    public static boolean setNews(String message) { // Changed return type to boolean
        String query = "UPDATE `groups` SET group_news = ? WHERE groups_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, message);
            pstmt.setInt(2, App.userSession.getCurrentLoggedInGroupID());

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                Debug.success("Berita grup berhasil diperbarui ");
                return true;
            } else {
                Debug.warn("Tidak ada grup yang cocok untuk diperbarui.");
                return false;
            }

        } catch (SQLException e) {
            Debug.error("Gagal update group_news: " + e.getMessage());
            return false;
        }
    }

    public static List<User> getUserMember() {
        List<User> members = new ArrayList<>();
        int currentGroupId = App.userSession.getCurrentLoggedInGroupID();

        if (currentGroupId <= 0) {
            System.out.println("DEBUG: getUserMember - No valid group ID in session. Returning empty list.");
            return members;
        }

        String sql = "SELECT u.users_id, u.username, gm.fk_roles_id " +
                "FROM users u " +
                "JOIN groups_member gm ON u.users_id = gm.fk_users_id " +
                "WHERE gm.fk_groups_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentGroupId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("users_id");
                    String username = rs.getString("username");
                    int roleId = rs.getInt("fk_roles_id");
                    System.out.println("DEBUG: getUserMember - Fetched User ID: " + userId + ", Username: " + username
                            + ", Role ID from DB: " + roleId);
                    members.add(new User(userId, username, roleId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ERROR: DatabaseManager.getUserMember - Database error: " + e.getMessage());
            return new ArrayList<>();
        }
        System.out.println("DEBUG: getUserMember - Finished fetching. Total members found: " + members.size());
        return members;
    }

    public static String getRoleName(int roleId) {
        String query = "SELECT roles_name FROM roles WHERE roles_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, roleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String roleName = rs.getString("roles_name");
                return roleName;
            } else {
                Debug.error("fail user role: Role ID " + roleId + " not found.");
                return null;
            }
        } catch (SQLException e) {
            Debug.error("Gagal mendapat rolename: " + e.getMessage());
            return null;
        }
    }

    public static List<Group> getAllGroub() {
        List<Group> groups = new ArrayList<>();
        String query = "SELECT * FROM `groups`";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                int id = rs.getInt("groups_id");
                String title = rs.getString("group_name");
                LocalDateTime create = rs.getTimestamp("group_created_at").toLocalDateTime();
                String describ = rs.getString("group_description");
                String news = rs.getString("group_news");

                groups.add(new Group(title, describ, create.toLocalDate(), news, id));
            }
            return groups;
        } catch (SQLException e) {
            Debug.error("Gagal mrdapat rolename: " + e.getMessage());
            return null;
        }
    }

    public static Project getProjectDetails(int projectId) {
        String sql = "SELECT * FROM groups_project WHERE groups_project_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String title = rs.getString("title");
                String describ = rs.getString("groups_project_description");
                LocalDateTime createat = rs.getTimestamp("groups_project_created_at").toLocalDateTime();
                LocalDateTime updateAt = rs.getTimestamp("groups_project_updated_at").toLocalDateTime();
                String url = rs.getString("repo_url");
                int id = rs.getInt("groups_project_id");
                return new Project(title, describ, createat.toLocalDate(), updateAt.toLocalDate(), url, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int createProject(String title, String description, String repoUrl, int groupId) {
        String sql = "INSERT INTO groups_project (title, groups_project_description, fk_groups_id, repo_url) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setInt(3, groupId);
            pstmt.setString(4, repoUrl);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean updateProject(Project project) {
        String sql = "UPDATE groups_project SET title = ?, groups_project_description = ?, repo_url = ?, groups_project_updated_at = NOW() WHERE groups_project_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, project.title);
            pstmt.setString(2, project.description);
            pstmt.setString(3, project.repoUrl);
            pstmt.setInt(4, project.id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProject(int projectId) {
        String deleteLogsSql = "DELETE FROM activity_logs WHERE fk_groups_project_id = ?";
        String deleteTasksSql = "DELETE FROM project_task WHERE fk_groups_project_id = ?";
        String deleteProjectSql = "DELETE FROM groups_project WHERE groups_project_id = ?";
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(deleteLogsSql)) {
                pstmt.setInt(1, projectId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteTasksSql)) {
                pstmt.setInt(1, projectId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteProjectSql)) {
                pstmt.setInt(1, projectId);
                pstmt.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    // <--- NEW: Changed return type from void to boolean
    public static boolean createTask(int projectId, int userId, String taskName) {
        String sql = "INSERT INTO project_task (fk_groups_project_id, fk_users_id, task, status) VALUES (?, ?, ?, 0)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, taskName);
            int rowsAffected = pstmt.executeUpdate(); // Get rows affected
            return rowsAffected > 0; // Return true if task was inserted successfully
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false on error
        }
    }

    public static void updateTaskStatus(int taskId, int progress) {
        String sql = "UPDATE project_task SET status = ? WHERE project_task_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, progress);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inside DatabaseManager.java, updateTask method
    public static boolean updateTask(int projectTaskId, String newTaskName, int newAssignedUserId) {
        String sql = "UPDATE project_task SET task = ?, fk_users_id = ? WHERE project_task_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // --- ADD THESE DEBUG LOGS ---
            System.out.println("DEBUG: DatabaseManager.updateTask - Executing SQL Query:");
            System.out.println("DEBUG: SQL: " + sql);
            System.out.println("DEBUG: Parameter 1 (newTaskName): '" + newTaskName + "'");
            System.out.println("DEBUG: Parameter 2 (newAssignedUserId): " + newAssignedUserId);
            System.out.println("DEBUG: Parameter 3 (projectTaskId): " + projectTaskId);
            // --- END DEBUG LOGS ---

            pstmt.setString(1, newTaskName);
            pstmt.setInt(2, newAssignedUserId);
            pstmt.setInt(3, projectTaskId);

            int rowsAffected = pstmt.executeUpdate();

            // --- ADD THIS DEBUG LOG ---
            System.out.println("DEBUG: DatabaseManager.updateTask - Rows Affected by update: " + rowsAffected);
            // --- END DEBUG LOG ---

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating task: " + e.getMessage());
            return false;
        }
    }

    public static List<ProjectTask> getProjectTasksForTable(int projectId) {
        List<ProjectTask> tasks = new ArrayList<>();
        String sql = "SELECT pt.project_task_id, pt.task, pt.project_task_created_at, u.username, u.users_id " +
                "FROM project_task pt " +
                "JOIN users u ON pt.fk_users_id = u.users_id " +
                "WHERE pt.fk_groups_project_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int taskId = rs.getInt("project_task_id"); // Get the task's ID
                String taskName = rs.getString("task");
                String username = rs.getString("username");
                int userId = rs.getInt("users_id");
                // LocalDate createdAt = rs.getTimestamp("project_task_created_at").toLocalDateTime().toLocalDate();

               
                tasks.add(new ProjectTask(taskId, taskName, username, userId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static List<TaskSelection> getTasksForProject(int projectId) {
        List<TaskSelection> tasks = new ArrayList<>();
        String sql = "SELECT project_task_id, task FROM project_task WHERE fk_groups_project_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tasks.add(new TaskSelection(rs.getInt("project_task_id"), rs.getString("task")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public static Map<UserData, List<MemberTask>> getTasksGroupedByMember(int projectId) {
        Map<UserData, List<MemberTask>> taskMap = new LinkedHashMap<>();
        String sql = "SELECT u.users_id, u.username, pt.project_task_id, pt.task, pt.status " +
                "FROM project_task pt " +
                "JOIN users u ON pt.fk_users_id = u.users_id " +
                "WHERE pt.fk_groups_project_id = ? ORDER BY u.username, pt.project_task_id";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                UserData user = new UserData(rs.getInt("users_id"), rs.getString("username"));
                MemberTask task = new MemberTask(rs.getInt("project_task_id"), rs.getString("task"),
                        rs.getInt("status"));
                taskMap.computeIfAbsent(user, k -> new ArrayList<>()).add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskMap;
    }

    public static void createActivityLog(int projectId, int taskId, int userId, String message) {
        String sql = "INSERT INTO activity_logs (fk_groups_project_id, project_task_id, fk_users_id, activity_logs_message) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, taskId);
            pstmt.setInt(3, userId);
            pstmt.setString(4, message);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ActivityLog> getActivityLogs(int projectId) {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT u.username, pt.task, al.activity_logs_message, al.times, pt.status " +
                "FROM activity_logs al " +
                "JOIN project_task pt ON al.project_task_id = pt.project_task_id " +
                "JOIN users u ON al.fk_users_id = u.users_id " +
                "WHERE al.fk_groups_project_id = ? ORDER BY al.times DESC";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                String usernam = rs.getString("username");
                String task = rs.getString("task");
                String message = rs.getString("activity_logs_message");
                LocalDateTime times = rs.getTimestamp("times").toLocalDateTime();
                int status = rs.getInt("status");
                logs.add(new ActivityLog(usernam, task, message, times, status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    public static UserData findUserByUsername(String username) {
        String sql = "SELECT users_id, username FROM users WHERE username = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new UserData(rs.getInt("users_id"), rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT roles_id, roles_name FROM roles";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                roles.add(new Role(rs.getInt("roles_id"), rs.getString("roles_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public static boolean createGroupWithMembers(String groupName, String description,
            List<GroupMemberDisplay> members) {
        String insertGroupSql = "INSERT INTO groups (group_name, group_description, fk_user_id) VALUES (?, ?, ?)";
        String insertMemberSql = "INSERT INTO groups_member (fk_groups_id, fk_users_id, fk_roles_id) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false);

            int newGroupId = -1;
            try (PreparedStatement pstmtGroup = conn.prepareStatement(insertGroupSql,
                    Statement.RETURN_GENERATED_KEYS)) {
                pstmtGroup.setString(1, groupName);
                pstmtGroup.setString(2, description);
                pstmtGroup.setInt(3, App.userSession.getCurrentLoggedInUserID());

                if (pstmtGroup.executeUpdate() > 0) {
                    try (ResultSet rs = pstmtGroup.getGeneratedKeys()) {
                        if (rs.next())
                            newGroupId = rs.getInt(1);
                    }
                }
                if (newGroupId == -1)
                    throw new SQLException("Creating group failed.");
            }

            List<Role> allRoles = DatabaseManager.getRoles();
            int defaultMemberRoleId = allRoles.stream()
                    .filter(r -> r.getRoleName().equalsIgnoreCase("Member"))
                    .mapToInt(Role::getRoleId)
                    .findFirst()
                    .orElse(2);

            int ownerRoleId = allRoles.stream()
                    .filter(r -> r.getRoleName().equalsIgnoreCase("Owner"))
                    .mapToInt(Role::getRoleId)
                    .findFirst()
                    .orElse(defaultMemberRoleId);

            try (PreparedStatement pstmtMember = conn.prepareStatement(insertMemberSql)) {
                for (GroupMemberDisplay member : members) {
                    if (member.getUserId() == 0) {
                        throw new SQLException("User ID 0 does not exist. Invalid member: " + member.getUsername());
                    }

                    if (!userExists(member.getUserId(), conn)) {
                        throw new SQLException("User ID " + member.getUserId() + " does not exist.");
                    }

                    pstmtMember.setInt(1, newGroupId);
                    pstmtMember.setInt(2, member.getUserId());

                    int roleIdToAssign;
                    if ("Owner".equalsIgnoreCase(member.getRoleName())) {
                        roleIdToAssign = ownerRoleId;
                        Debug.success("Assigning Owner role ID: " + roleIdToAssign + " to " + member.getUsername());
                    } else {
                        roleIdToAssign = allRoles.stream()
                                .filter(r -> r.getRoleName().equalsIgnoreCase(member.getRoleName()))
                                .mapToInt(Role::getRoleId)
                                .findFirst()
                                .orElse(defaultMemberRoleId);
                        Debug.success("Assigning role ID: " + roleIdToAssign + " (" + member.getRoleName() + ") to "
                                + member.getUsername());
                    }

                    pstmtMember.setInt(3, roleIdToAssign);
                    pstmtMember.addBatch();
                }
                pstmtMember.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null)
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            return false;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }

    public static List<UserData> searchAvailableUsers(String usernameQuery) {
        List<UserData> users = new ArrayList<>();
        String sql = "SELECT u.users_id, u.username FROM users u " +
                "LEFT JOIN groups_member gm ON u.users_id = gm.fk_users_id " +
                "WHERE gm.fk_users_id IS NULL AND u.username LIKE ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + usernameQuery + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(new UserData(rs.getInt("users_id"), rs.getString("username")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static boolean userExists(int userId, Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE users_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean addMemberToGroup(int groupId, int userId) {
        String checkMembershipSql = "SELECT COUNT(*) FROM groups_member WHERE fk_groups_id = ? AND fk_users_id = ?";
        String insertMemberSql = "INSERT INTO groups_member (fk_groups_id, fk_users_id, fk_roles_id) VALUES (?, ?, ?)";

        try (Connection conn = connect()) {

            try (PreparedStatement pstmtCheck = conn.prepareStatement(checkMembershipSql)) {
                pstmtCheck.setInt(1, groupId);
                pstmtCheck.setInt(2, userId);
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("User " + userId + " is already a member of group " + groupId);
                        return false;
                    }
                }
            }

            int roleId = getRoles().stream()
                    .filter(r -> r.getRoleName().equalsIgnoreCase("Member"))
                    .mapToInt(Role::getRoleId)
                    .findFirst()
                    .orElse(3);

            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertMemberSql)) {
                pstmtInsert.setInt(1, groupId);
                pstmtInsert.setInt(2, userId);
                pstmtInsert.setInt(3, roleId);

                int rowsAffected = pstmtInsert.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error while adding member to group: " + e.getMessage());
            return false;
        }
    }

    public static boolean leaveGroup(int groupId, int userId) throws SQLException {
        String deleteMemberSql = "DELETE FROM groups_member WHERE fk_groups_id = ? AND fk_users_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(deleteMemberSql)) {

            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static Group getGroupById(int groupId) throws SQLException {

        String sql = "SELECT groups_id, group_name, group_description, group_created_at, group_news FROM groups WHERE groups_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    return new Group(
                            rs.getString("group_name"),
                            rs.getString("group_description"),
                            rs.getDate("group_created_at").toLocalDate(),
                            rs.getString("group_news"),
                            rs.getInt("groups_id"));
                }
            }
        }
        return null;
    }

    public static boolean updateGroup(int groupId, String newName, String newDescription) throws SQLException {
        String sql = "UPDATE `groups` SET group_name = ?, group_description = ? WHERE groups_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newDescription);
            pstmt.setInt(3, groupId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean deleteGroup(int groupId) throws SQLException {
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false); // Start transaction

            List<Integer> projectIds = new ArrayList<>();
            String getProjectIdsSql = "SELECT groups_project_id FROM groups_project WHERE fk_groups_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(getProjectIdsSql)) {
                pstmt.setInt(1, groupId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        projectIds.add(rs.getInt("groups_project_id"));
                    }
                }
            }

            if (!projectIds.isEmpty()) {
                String deleteActivityLogsSql = "DELETE FROM activity_logs WHERE fk_groups_project_id IN (SELECT groups_project_id FROM groups_project WHERE fk_groups_id = ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteActivityLogsSql)) {
                    pstmt.setInt(1, groupId);
                    pstmt.executeUpdate();
                }
            }

            if (!projectIds.isEmpty()) {
                String deleteProjectTasksSql = "DELETE FROM project_task WHERE fk_groups_project_id IN (SELECT groups_project_id FROM groups_project WHERE fk_groups_id = ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteProjectTasksSql)) {
                    pstmt.setInt(1, groupId);
                    pstmt.executeUpdate();
                }
            }

            String deleteProjectsSql = "DELETE FROM groups_project WHERE fk_groups_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteProjectsSql)) {
                pstmt.setInt(1, groupId);
                pstmt.executeUpdate();
            }

            String deleteGroupChatSql = "DELETE FROM group_chat WHERE fk_groups_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteGroupChatSql)) {
                pstmt.setInt(1, groupId);
                pstmt.executeUpdate();
            }

            String deleteMembersSql = "DELETE FROM groups_member WHERE fk_groups_id = ?";
            try (PreparedStatement pstmtMembers = conn.prepareStatement(deleteMembersSql)) {
                pstmtMembers.setInt(1, groupId);
                pstmtMembers.executeUpdate();
            }

            String deleteGroupSql = "DELETE FROM `groups` WHERE groups_id = ?";
            try (PreparedStatement pstmtGroup = conn.prepareStatement(deleteGroupSql)) {
                pstmtGroup.setInt(1, groupId);
                int rowsAffected = pstmtGroup.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public static boolean updateGroupMemberRole(int groupId, int userId, int roleId) throws SQLException {
        String sql = "UPDATE groups_member SET fk_roles_id = ? WHERE fk_groups_id = ? AND fk_users_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);
            pstmt.setInt(2, groupId);
            pstmt.setInt(3, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static boolean removeMemberFromGroup(int groupId, int userId) throws SQLException {
        String sql = "DELETE FROM groups_member WHERE fk_groups_id = ? AND fk_users_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static List<GroupMemberDisplay> getGroupMembers(int groupId) throws SQLException {
        List<GroupMemberDisplay> members = new ArrayList<>();
        String sql = "SELECT u.users_id, u.username, r.roles_name, gm.joined_at " +
                "FROM users u " +
                "JOIN groups_member gm ON u.users_id = gm.fk_users_id " +
                "JOIN roles r ON gm.fk_roles_id = r.roles_id " +
                "WHERE gm.fk_groups_id = ?";

        System.out.println("DEBUG: getGroupMembers - Fetching members for Group ID: " + groupId);

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("users_id");
                    String username = rs.getString("username");
                    String roleName = rs.getString("roles_name");
                    LocalDate joinedAt = rs.getDate("joined_at").toLocalDate();

                    System.out.println("DEBUG: getGroupMembers - Found member: ID=" + userId + ", Name=" + username
                            + ", Role=" + roleName + ", Joined=" + joinedAt);
                    members.add(new GroupMemberDisplay(userId, username, roleName, joinedAt));
                }
            }
        }
        System.out.println("DEBUG: getGroupMembers - Finished fetching. Total members found: " + members.size());
        return members;
    }

    public static boolean updateGroupNews(int groupId, String newNews) {
        String sql = "UPDATE `groups` SET group_news = ? WHERE groups_id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNews);
            pstmt.setInt(2, groupId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err
                    .println("ERROR: DatabaseManager.updateGroupNews - Failed to update group_news: " + e.getMessage());
            return false;
        }
    }

    public static int getLastInsertedGroupId() {
        try (java.sql.Connection conn = connect();
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT MAX(groups_id) FROM groups")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}