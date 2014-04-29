package org.guvnor.udc.model;

import java.util.Arrays;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GfsSummary {
    
    private String[] inboxUsers;
    
    private String path;
    
    private String uriPath;
    
    private String fileSystem;
    
    
    public GfsSummary(){
        
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public String getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(String fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public String[] getInboxUsers() {
        return inboxUsers;
    }

    public void setInboxUsers(String[] inboxUsers) {
        this.inboxUsers = inboxUsers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileSystem == null) ? 0 : fileSystem.hashCode());
        result = prime * result + Arrays.hashCode(inboxUsers);
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((uriPath == null) ? 0 : uriPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GfsSummary other = (GfsSummary) obj;
        if (fileSystem == null) {
            if (other.fileSystem != null)
                return false;
        } else if (!fileSystem.equals(other.fileSystem))
            return false;
        if (!Arrays.equals(inboxUsers, other.inboxUsers))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (uriPath == null) {
            if (other.uriPath != null)
                return false;
        } else if (!uriPath.equals(other.uriPath))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GfsSummary [inboxUsers=" + Arrays.toString(inboxUsers) + ", path=" + path + ", uriPath=" + uriPath
                + ", fileSystem=" + fileSystem + "]";
    }

}
