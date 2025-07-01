package com.menejementpj.type;

import java.time.LocalDate;

public class Project {
    public int id;
    public final String title;
    public final String description;
    public final LocalDate createdAt;

    public Project(String title, String description, LocalDate createdAt, int id) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.id = id;
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