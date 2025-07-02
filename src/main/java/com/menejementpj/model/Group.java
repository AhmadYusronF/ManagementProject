package com.menejementpj.model;

import java.time.LocalDate;


public class Group {
    public int id;
    public String nama;
    public String describ;
    public LocalDate createAt;
    public String news;

    public Group(String nama, String describ, LocalDate createAt, String news, int id) {
        this.nama = nama;
        this.describ = describ;
        this.createAt = createAt;
        this.news = news;
        this.id = id;
    }
       public void setNul() {
        this.id = 0;
        this.nama = null;
        this.describ = null;
        this.createAt = null;
        this.news = null;
    }

}
