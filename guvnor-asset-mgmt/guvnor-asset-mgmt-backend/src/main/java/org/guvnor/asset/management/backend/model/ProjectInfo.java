/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
