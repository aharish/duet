package com.gadgetroid.duet.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by gadgetroid on 18/07/17.
 */

public class Task extends RealmObject {

    @PrimaryKey
    private int taskId;

    private String taskTitle;

    private String taskDescription;

    private boolean isTaskComplete;

    private long taskDueOn;

    private Project project;

    public Task() {
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public boolean isTaskComplete() {
        return isTaskComplete;
    }

    public void setTaskComplete(boolean taskComplete) {
        isTaskComplete = taskComplete;
    }

    public long getTaskDueOn() {
        return taskDueOn;
    }

    public void setTaskDueOn(long taskDueOn) {
        this.taskDueOn = taskDueOn;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
