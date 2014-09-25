package org.guvnor.asset.management.social;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AssetsPromotedEvent {

    private String processName;

    private String user;

    private String repositoryAlias;

    private String sourceBranch;

    private String targetBranch;

    private Long timestamp;

    List<String> assets = new ArrayList<String>(  );

    String error;

    public AssetsPromotedEvent() {
    }

    public AssetsPromotedEvent( String processName,
            String repositoryAlias,
            String sourceBranch,
            String targetBranch,
            List<String> assets,
            String user,
            Long timestamp ) {
        this.processName = processName;
        this.repositoryAlias = repositoryAlias;
        this.sourceBranch = sourceBranch;
        this.targetBranch = targetBranch;
        this.assets = assets;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName( String processName ) {
        this.processName = processName;
    }

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    public String getRepositoryAlias() {
        return repositoryAlias;
    }

    public void setRepositoryAlias( String repositoryAlias ) {
        this.repositoryAlias = repositoryAlias;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch( String sourceBranch ) {
        this.sourceBranch = sourceBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch( String targetBranch ) {
        this.targetBranch = targetBranch;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( Long timestamp ) {
        this.timestamp = timestamp;
    }

    public List<String> getAssets() {
        return assets;
    }

    public void setAssets( List<String> assets ) {
        this.assets = assets;
    }

    public String getError() {
        return error;
    }

    public void setError( String error ) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }
}
