package com.menejementpj.model;

import com.menejementpj.db.DatabaseManager;

public class User {
    public int id;
    public String userName;
    public String role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public User(int id, String userName, int role) {
        this.id = id;
        this.userName = userName;
        this.role = DatabaseManager.getRoleName(role);
    }
}
