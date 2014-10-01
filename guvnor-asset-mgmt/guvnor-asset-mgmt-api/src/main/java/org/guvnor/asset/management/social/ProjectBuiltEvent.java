/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.asset.management.social;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectBuiltEvent extends AssetManagementEvent {

    private String branchName;

    private String projectName;

    public ProjectBuiltEvent() {
    }

    public ProjectBuiltEvent( String processName, String repositoryAlias, String rootURI, String user, Long timestamp ) {
        super( processName, repositoryAlias, rootURI, user, timestamp );
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName( String branchName ) {
        this.branchName = branchName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName( String projectName ) {
        this.projectName = projectName;
    }
}
