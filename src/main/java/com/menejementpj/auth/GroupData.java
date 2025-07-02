package com.menejementpj.auth;

public class GroupData {
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupData(String name) {
        this.groupName = name;
    }
     public void setNull() {
        this.groupName = null;
    }

}