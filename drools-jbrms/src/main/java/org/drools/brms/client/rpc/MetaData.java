package org.drools.brms.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the DTO for a versionable asset's meta data.
 * ie basically everything except the payload.
 */
public class MetaData
    implements
    IsSerializable {

    public String name = "";
    public String description = "";
    
    public String title = "";
    public String state = "";

    public Date lastModifiedDate;
    public String lastContributor = "";
    public String versionNumber;
    public String   lastCheckinComment = "";
    public Date createdDate;
    
    public String packageName = "";
    public String[] categories = new String[0];
    
    public String format = "";
    public String type = "";
    public String creator = "";
    public String externalSource = "";
    public String subject = "";
    public String externalRelation = "";
    public String rights = ""; 
    public String coverage = "";
    public String publisher = "";   
    
    public Date dateEffective;
    public Date dateExpired;
    
    /** used to flag dirty - ie needs to be spanked. Or saved to the repo, whatever */
    public boolean dirty = false;

}
