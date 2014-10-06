package org.guvnor.asset.management.backend.model;

import java.io.Serializable;

public class ProjectInfo implements Serializable {

    private String repository;
    private String branch;
    private String name;
    private boolean isKieProject;

    public ProjectInfo(String repository, String branch, String name, boolean kieProject) {
        this.repository = repository;
        this.branch = branch;
        this.name = name;
        isKieProject = kieProject;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isKieProject() {
        return isKieProject;
    }

    public void setKieProject(boolean kieProject) {
        isKieProject = kieProject;
    }

    public String getProjectURI() {
        return repository + "/" + name;
    }
}
