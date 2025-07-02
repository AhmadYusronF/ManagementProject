package com.menejementpj.auth;

public class Session {
    private int currentLoggedInHidden_uid = -1;
    private int currentLoggedInUserID = -1;
    private int currentLoggedInGroupID = -1;
    private int currentProjectID = -1;

    public int getCurrentProjectID() {
        return currentProjectID;
    }

    public void setCurrentProjectID(int currentProjectID) {
        this.currentProjectID = currentProjectID;
    }

    public int getCurrentLoggedInHidden_uid() {
        return currentLoggedInHidden_uid;
    }

    public void setCurrentLoggedInHidden_uid(int currentLoggedInHidden_uid) {
        this.currentLoggedInHidden_uid = currentLoggedInHidden_uid;
    }

    public int getCurrentLoggedInUserID() {
        return currentLoggedInUserID;
    }

    public void setCurrentLoggedInUserID(int currentLoggedInUserID) {
        this.currentLoggedInUserID = currentLoggedInUserID;
    }

    public int getCurrentLoggedInGroupID() {
        return currentLoggedInGroupID;
    }

    public void setCurrentLoggedInGroupID(int currentLoggedInGroupID) {
        this.currentLoggedInGroupID = currentLoggedInGroupID;
    }

    public void setUserSession(int hiddenUid, int userId, int groupId) {
        currentLoggedInHidden_uid = hiddenUid;
        currentLoggedInUserID = userId;
        currentLoggedInGroupID = groupId;
    }
}
