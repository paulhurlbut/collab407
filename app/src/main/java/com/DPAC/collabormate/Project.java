package com.DPAC.collabormate;

/**
 * Created by Daniel on 4/15/2015.
 */
public class Project {
    private long id;
    private String project;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return project;
    }
}

