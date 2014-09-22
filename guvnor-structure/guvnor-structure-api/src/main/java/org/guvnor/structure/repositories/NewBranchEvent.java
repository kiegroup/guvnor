package org.guvnor.structure.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class NewBranchEvent {

    private String branchName;
    private Path branchPath;

    private String alias;


    public NewBranchEvent() {
    }

    public NewBranchEvent(String alias, String branchName, Path branchPath) {
        this.alias = alias;
        this.branchName = branchName;
        this.branchPath = branchPath;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getAlias() {
        return alias;
    }

    public Path getBranchPath() {
        return branchPath;
    }
}