package com.gadgetroid.duet.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by gadgetroid on 16/07/17.
 */

public class Project extends RealmObject {

    @PrimaryKey
    private int projectId;

    private String projectName;
    private String projectDescription;

    public Project() {}

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}
