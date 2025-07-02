package com.menejementpj.auth;

public class UserData {
    private String username;
    private String roleName;
    private int userId;

    // getGroupMembers
    public UserData(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.roleName = null;
    }

    // App.java
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

}
