package com.menejementpj.db;

import com.menejementpj.App;
import com.menejementpj.auth.Session;
import com.menejementpj.components.PopUpAlert;
import com.menejementpj.test.Debug;
import com.menejementpj.type.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3307/fadilmj";
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

    public static int getUserID(int huid) {
        int usersID = 0;
        String sql = "SELECT users_id FROM users where fk_hidden_uid = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, huid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                usersID = rs.getInt("users_id");
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Database error retrieving userID for hidden_uid '" + huid + "': " + e.getMessage());
        }
        return usersID;
    }

    public static int getGroupID(int userID) {
        int groupID = 0;
        String sql = "SELECT fk_groups_id FROM groups_member where fk_users_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                groupID = rs.getInt("fk_groups_id");
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Database error retrieving groupID for user ID '" + userID + "': " + e.getMessage());
        }
        return groupID;
    }

    public static String getUsername(int userID) {
        String username = null;
        String sql = "SELECT username FROM users WHERE users_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Database error retrieving username for ID '" + userID + "': " + e.getMessage());
        }
        return username;
    }

    public static String getGroupName(int groupId) {
        String groupName = null;
        String sql = "SELECT group_name FROM groups WHERE groups_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                groupName = rs.getString("group_name");
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Database error retrieving group name for ID '" + groupId + "': " + e.getMessage());
        }
        return groupName;
    }

    public static List<Project> getProjects(int groupID) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT title, groups_project_description, groups_project_created_at FROM groups_project WHERE fk_groups_id = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String description = rs.getString("groups_project_description");
                LocalDate createdAt = rs.getDate("groups_project_created_at").toLocalDate();
                projects.add(new Project(title, description, createdAt));
            }
        } catch (SQLException | NullPointerException e) {
            System.err
                    .println("Error fetching projects from database for group ID '" + groupID + "': " + e.getMessage());
        }
        return projects;
    }

    public static String getDataGroub() {
        String query = "";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

        } catch (Exception e) {
            // TODO: handle exception
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
                stmtAccount.setInt(3, 3);
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
}
