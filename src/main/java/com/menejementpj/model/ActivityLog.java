package com.menejementpj.model;

public class ActivityLog {
    public int id;
    public String title;
    public String subTitle;
    public String progres;

    public ActivityLog(int id, String title, String subTitle, String progres) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.progres = progres;
    }
}
