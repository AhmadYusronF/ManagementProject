package com.menejementpj.auth;

public class UserData {
    private String username;
    private String roleName;

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

}
