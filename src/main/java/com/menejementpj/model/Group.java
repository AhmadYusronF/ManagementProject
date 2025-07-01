package com.menejementpj.model;

import java.time.LocalDate;

public class Group {
    public String nama;
    public String describ;
    public LocalDate createAt;
    public String news;

    public Group(String nama, String describ, LocalDate createAt, String news) {
        this.nama = nama;
        this.describ = describ;
        this.createAt = createAt;
        this.news = news;
    }

}
