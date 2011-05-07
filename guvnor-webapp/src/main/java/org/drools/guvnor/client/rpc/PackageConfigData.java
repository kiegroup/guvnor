/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.rpc;

import java.util.HashMap;

/**
 * This contains data for a package configuration.
 *
 */
public class PackageConfigData extends Artifact {
    
    public String header;
    public String externalURI;
    public boolean archived = false;
    public boolean isSnapshot = false;
    public String snapshotName;
    public HashMap<String,String> catRules;
    public String[] workspaces;
    public String[] dependencies;
    
    public PackageConfigData[] subPackages;

    public PackageConfigData() {
    }

    public PackageConfigData(String name) {
        super.setName(name);
    }

    public String getHeader() {
        return header;
    }

    public PackageConfigData setHeader(String header) {
        this.header = header;
        return this;
    }

    public String getExternalURI() {
        return externalURI;
    }

    public PackageConfigData setExternalURI(String externalURI) {
        this.externalURI = externalURI;
        return this;
    }

    public boolean isArchived() {
        return archived;
    }

    public PackageConfigData setArchived(boolean archived) {
        this.archived = archived;
        return this;
    }

    public boolean isSnapshot() {
        return isSnapshot;
    }

    public PackageConfigData setSnapshot(boolean isSnapshot) {
        this.isSnapshot = isSnapshot;
        return this;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public PackageConfigData setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
        return this;
    }

    public HashMap<String, String> getCatRules() {
        return catRules;
    }

    public PackageConfigData setCatRules(HashMap<String, String> catRules) {
        this.catRules = catRules;
        return this;
    }

    public String[] getWorkspaces() {
        return workspaces;
    }

    public PackageConfigData setWorkspaces(String[] workspaces) {
        this.workspaces = workspaces;
        return this;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public PackageConfigData setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public PackageConfigData[] getSubPackages() {
        return subPackages;
    }

    public PackageConfigData setSubPackages(PackageConfigData[] subPackages) {
        this.subPackages = subPackages;
        return this;
    }

    

    public boolean isGlobal() {
        return "global".equals(super.getName()); // TODO kills i18n
    }

}
