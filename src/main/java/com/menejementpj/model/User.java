package com.menejementpj.model;

import com.menejementpj.db.DatabaseManager;

public class User {
    public int id;
    public String userName;
    public String role;

    public User(int id, String userName, int role) {
        this.id = id;
        this.userName = userName;
        this.role = DatabaseManager.getRoleName(role);
    }
}
