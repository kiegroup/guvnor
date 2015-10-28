/*
 * Copyright 2015 JBoss Inc
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

package org.guvnor.asset.management.backend.service;

import java.util.Set;

import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class AssetManagementServiceCallerMock
    implements Caller<AssetManagementService> {

    protected AssetManagementServiceWrapper assetManagementServiceWrapper;

    protected RemoteCallback remoteCallback;

    public AssetManagementServiceCallerMock( AssetManagementService assetManagementService ) {
        this.assetManagementServiceWrapper = new AssetManagementServiceWrapper( assetManagementService );
    }

    @Override
    public AssetManagementService call() {
        return assetManagementServiceWrapper;
    }

    @Override
    public AssetManagementService call( RemoteCallback<?> remoteCallback ) {
        return call( remoteCallback, null );
    }

    @Override
    public AssetManagementService call( RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback ) {
        this.remoteCallback = remoteCallback;
        return assetManagementServiceWrapper;
    }

    private class AssetManagementServiceWrapper
            implements AssetManagementService {

        AssetManagementService assetManagementService;

        public AssetManagementServiceWrapper( AssetManagementService assetManagementService ) {
            this.assetManagementService = assetManagementService;
        }

        @Override
        public void configureRepository( String repository, String sourceBranch, String devBranch, String releaseBranch, String version ) {
            assetManagementService.configureRepository( repository, sourceBranch, devBranch, releaseBranch, version );
        }

        @Override
        public void buildProject( String repository, String branch, String project, String userName, String password, String serverURL, Boolean deployToRuntime ) {
            assetManagementService.buildProject( repository, branch, project, userName, password, serverURL, deployToRuntime );
        }

        @Override
        public void promoteChanges( String repository, String sourceBranch, String destBranch ) {
            assetManagementService.promoteChanges( repository, sourceBranch, destBranch );
        }

        @Override
        public void releaseProject( String repository, String branch, String userName, String password, String serverURL, Boolean deployToRuntime, String version ) {
            assetManagementService.releaseProject( repository, branch, userName, password, serverURL, deployToRuntime, version );
        }

        @Override
        public boolean supportRuntimeDeployment() {
            boolean result = assetManagementService.supportRuntimeDeployment();
            remoteCallback.callback( result );
            return result;
        }

        @Override
        public Set<Project> getProjects( Repository repository, String branch ) {
            Set<Project> result = assetManagementService.getProjects( repository, branch );
            remoteCallback.callback( result );
            return result;
        }
    }
}
