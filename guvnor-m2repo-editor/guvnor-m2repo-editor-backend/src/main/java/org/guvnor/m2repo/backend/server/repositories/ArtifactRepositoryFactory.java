/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.guvnor.m2repo.backend.server.repositories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.uberfire.apache.commons.io.FilenameUtils;
import org.uberfire.backend.server.cdi.workspace.WorkspaceNameResolver;

@ApplicationScoped
public class ArtifactRepositoryFactory {

    public static final String LOCAL_M2_REPO_NAME = "local-m2-repo";
    public static final String GLOBAL_M2_REPO_NAME = "global-m2-repo";
    public static final String WORKSPACE_M2_REPO_NAME = "workspace-m2-repo";
    public static final String DISTRIBUTION_MANAGEMENT_REPO_NAME = "distribution-management-repo";
    public static final String ORG_GUVNOR_M2REPO_DIR_PROPERTY = "org.guvnor.m2repo.dir";

    private ArtifactRepositoryPreference preferences;
    private WorkspaceNameResolver workspaceNameResolver;
    private List<ArtifactRepository> repositories;

    public ArtifactRepositoryFactory() {
    }

    @Inject
    public ArtifactRepositoryFactory(ArtifactRepositoryPreference preferences,
                                     WorkspaceNameResolver resolver) {
        this.preferences = preferences;
        this.workspaceNameResolver = resolver;
    }

    @PostConstruct
    public void initialize() {
        this.preferences.load();
        this.repositories = new ArrayList<>();

        this.repositories.add(this.produceLocalRepository());
        if (this.preferences.isGlobalM2RepoDirEnabled()) {
            this.repositories.add(this.produceGlobalRepository());
        }
        if (this.preferences.isWorkspaceM2RepoDirEnabled()) {
            this.repositories.add(this.produceWorkspaceRepository());
        }
        if (this.preferences.isDistributionManagementM2RepoDirEnabled()) {
            this.repositories.add(this.produceDistributionManagementRepository());
        }
    }

    public LocalArtifactRepository produceLocalRepository() {
        return new LocalArtifactRepository(LOCAL_M2_REPO_NAME);
    }

    public FileSystemArtifactRepository produceGlobalRepository() {
        return new FileSystemArtifactRepository(GLOBAL_M2_REPO_NAME,
                                                this.getGlobalM2RepoDir());
    }

    public FileSystemArtifactRepository produceWorkspaceRepository() {
        String repoDir = getWorkspaceRepoDir();
        return new FileSystemArtifactRepository(WORKSPACE_M2_REPO_NAME,
                                                repoDir);
    }

    public DistributionManagementArtifactRepository produceDistributionManagementRepository() {
        return new DistributionManagementArtifactRepository(DISTRIBUTION_MANAGEMENT_REPO_NAME);
    }

    private String getGlobalM2RepoDir() {
        final String repoRoot = FilenameUtils.separatorsToSystem(preferences.getGlobalM2RepoDir());

        final String meReposDir = System.getProperty(ORG_GUVNOR_M2REPO_DIR_PROPERTY);

        String repoDir;
        if (meReposDir == null || meReposDir.trim().isEmpty()) {
            repoDir = repoRoot;
        } else {
            repoDir = meReposDir.trim();
        }
        return repoDir;
    }

    private String getWorkspaceRepoDir() {
        String workspace = this.getWorkspaceName();
        final String repoRoot = FilenameUtils.separatorsToSystem(preferences.getWorkspaceM2RepoDir());
        String repoDir;
        if (repoRoot == null || repoRoot.trim().isEmpty()) {
            repoDir = this.getGlobalM2RepoDir() + File.separator + "workspaces";
        } else {
            repoDir = repoRoot;
        }
        return repoDir + File.separator + workspace;
    }

    private String getWorkspaceName() {
        return workspaceNameResolver.getWorkspaceName();
    }

    public List<? extends ArtifactRepository> getRepositories() {
        return this.repositories.stream().filter(ArtifactRepository::isRepository).collect(Collectors.toList());
    }

    public List<? extends ArtifactRepository> getPomRepositories() {
        return this.repositories.stream().filter(ArtifactRepository::isPomRepository).collect(Collectors.toList());
    }
}
