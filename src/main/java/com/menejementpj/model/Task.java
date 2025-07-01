package com.menejementpj.model;

public class Task {
    private String title;
    private String subString;
    private String proggres;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubString() {
        return subString;
    }

    public void setSubString(String subString) {
        this.subString = subString;
    }

    public String getProggres() {
        return proggres;
    }

    public void setProggres(String proggres) {
        this.proggres = proggres;
    }

    public Task(String title, String subString, String proggres) {
        this.title = title;
        this.subString = subString;
        this.proggres = proggres;
    }
}
