package com.gadgetroid.duet.model;

import io.realm.RealmObject;

/**
 * Created by gadgetroid on 16/07/17.
 */

public class Project extends RealmObject {
    private String projectName;
    private String projectDescription;

    public Project() {}

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
