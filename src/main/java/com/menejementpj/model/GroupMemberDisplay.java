package com.menejementpj.model;

import java.time.LocalDate;
import javafx.beans.property.*;
import com.menejementpj.db.DatabaseManager; // Import DatabaseManager

public class GroupMemberDisplay {
    private IntegerProperty userId;
    private StringProperty username;
    private StringProperty roleName;
    private ObjectProperty<LocalDate> joinedAt;

    // Existing constructor (for when roleName is already known as a String)
    public GroupMemberDisplay(int userId, String username, String roleName, LocalDate joinedAt) {
        this.userId = new SimpleIntegerProperty(userId);
        this.username = new SimpleStringProperty(username);
        this.roleName = new SimpleStringProperty(roleName);
        this.joinedAt = new SimpleObjectProperty<>(joinedAt);
    }

    // NEW CONSTRUCTOR: Takes roleId (int) and resolves roleName (String)
    public GroupMemberDisplay(int userId, String username, int roleId, LocalDate joinedAt) {
        this.userId = new SimpleIntegerProperty(userId);
        this.username = new SimpleStringProperty(username);
        // Resolve roleName from roleId using DatabaseManager
        this.roleName = new SimpleStringProperty(DatabaseManager.getRoleName(roleId));
        this.joinedAt = new SimpleObjectProperty<>(joinedAt);
    }

    // Getters
    public int getUserId() {
        return userId.get();
    }

    public String getUsername() {
        return username.get();
    }

    public String getRoleName() {
        return roleName.get();
    }

    public LocalDate getJoinedAt() {
        return joinedAt.get();
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
    }

    // Property Getters
    public IntegerProperty userIdProperty() {
        return userId;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    public ObjectProperty<LocalDate> joinedAtProperty() {
        return joinedAt;
    }
}
