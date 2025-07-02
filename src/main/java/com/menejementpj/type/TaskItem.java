package com.menejementpj.type;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskItem {
    private final StringProperty name;
    private final StringProperty status;

    public TaskItem(String name, String status) {
        this.name = new SimpleStringProperty(name);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public String toString() {

        return name.get() + " (" + status.get() + ")";
    }
}