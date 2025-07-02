package com.menejementpj.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Project {

    public int id;
    public final String title;
    public final String description;
    public final LocalDate createdAt;
    public LocalDate updateAt;
    public String repoUrl;

    public Project(String title, String description, LocalDate createdAt, LocalDate updateAt, String repoUrl,
            int id) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.repoUrl = repoUrl;
        this.id = id;
    }

}
