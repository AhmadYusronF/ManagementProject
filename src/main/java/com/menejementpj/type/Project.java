package com.menejementpj.type;

import java.time.LocalDate;

public class Project {
    private final String title;
    private final String description;
    private final LocalDate createdAt;

    public Project(String title, String description, LocalDate createdAt) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}