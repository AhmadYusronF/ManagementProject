package com.menejementpj.auth;

import java.util.Objects;

public class UserData {
    private String username;
    private String roleName;
    private int userId;

    // Existing constructor (getGroupMembers)
    public UserData(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.roleName = null;
    }

    // Existing constructor (App.java)
    public UserData() {
        this.userId = 0;
        this.username = null;
        this.roleName = null;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserData(String username, String rolename) {
        this.username = username;
        this.roleName = rolename;
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    public void setNull() {
        this.username = null;
        this.userId = 0;
        this.roleName = "Cract";
    }

    // --- NEWLY ADDED METHODS ---

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserData userData = (UserData) o;
        return userId == userData.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}