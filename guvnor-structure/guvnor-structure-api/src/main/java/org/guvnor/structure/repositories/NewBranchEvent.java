package org.guvnor.structure.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class NewBranchEvent {

    private String repositoryAlias;

    private String branchName;
    private Path branchPath;
    private Long timestamp;


    public NewBranchEvent() {
    }

    public NewBranchEvent(String repositoryAlias, String branchName, Path branchPath, Long timestamp) {
        this.repositoryAlias = checkNotNull("repositoryAlias", repositoryAlias);
        this.branchName = checkNotNull("branchName", branchName);
        this.branchPath = checkNotNull("branchPath", branchPath);
        this.timestamp = checkNotNull("timestamp", timestamp);
    }

    public String getBranchName() {
        return branchName;
    }

    public String getRepositoryAlias() {
        return repositoryAlias;
    }

    public Path getBranchPath() {
        return branchPath;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}