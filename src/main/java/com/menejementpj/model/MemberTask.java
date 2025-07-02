package com.menejementpj.model;

public class MemberTask {
    private int id;

    private String taskName;
    private int status; // Status as an integer (0-100)

    public MemberTask(int id, String taskName, int status) {
        this.taskName = taskName;
        this.status = status;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}