package com.menejementpj.db;

import com.menejementpj.type.*;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        int hidden_uid = -1;
        String sql = "SELECT hidden_uid FROM account WHERE email = ? AND account_password = ?";

        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, account_password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                hidden_uid = rs.getInt("hidden_uid");
            }
        } catch (SQLException | NullPointerException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
        }
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
}
