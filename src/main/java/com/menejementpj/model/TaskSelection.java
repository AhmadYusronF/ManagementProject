package com.menejementpj.model;

/**
 * A simple data model to hold a task's ID and name for use in a ChoiceBox.
 */
public class TaskSelection {
    private final int taskId;
    private final String taskName;

    public TaskSelection(int taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    // This is crucial: The ChoiceBox uses this method to display the object as
    // text.
    @Override
    public String toString() {
        return this.taskName;
    }
}