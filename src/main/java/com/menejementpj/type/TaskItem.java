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

    // --- Getters for Properties (important for binding) ---
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty statusProperty() {
        return status;
    }

    // --- Standard Getters/Setters (for convenience, not strictly needed for
    // ListView display) ---
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
        // This toString is used as a fallback if you don't use a custom cell factory.
        // With a custom cell factory, it's not directly displayed by default.
        return name.get() + " (" + status.get() + ")";
    }
}