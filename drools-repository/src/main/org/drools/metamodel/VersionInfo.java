package org.drools.metamodel;

/**
 * This represents some information about the version, as it was checked in.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class VersionInfo extends Asset
{
    private String comment;
    private long versionNumber;
    private String status;
    
    public VersionInfo(String comment, long versionNumber, String status) {
        this.comment = comment;
        this.versionNumber = versionNumber;
        this.status = status;
    }
    
    public VersionInfo nextVersion(String comment) {
        return new VersionInfo(comment, versionNumber + 1, this.status);
    }
    public VersionInfo nextVersion(String comment, String newStatus) {
        return new VersionInfo(comment, versionNumber + 1, newStatus);
    }
    
    
    
    public String getComment()
    {
        return comment;
    }
    public String getStatus()
    {
        return status;
    }
    public long getVersionNumber()
    {
        return versionNumber;
    }
    
    
    
}
