package com.menejementpj.model;

import java.time.LocalDateTime;

public class Group {
    public int id;
    public String nama;
    public String describ;
    public LocalDateTime createAt;
    public String news;

    public Group(String nama, String describ, LocalDateTime createAt, String news, int id) {
        this.nama = nama;
        this.describ = describ;
        this.createAt = createAt;
        this.news = news;
        this.id = id;
    }

}
