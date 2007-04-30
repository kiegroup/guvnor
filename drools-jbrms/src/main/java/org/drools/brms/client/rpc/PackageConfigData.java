package org.drools.brms.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This contains data for a package configuration.
 * @author Michael Neale
 *
 */
public class PackageConfigData
    implements
    IsSerializable {

    public String uuid;
    public String header;    
    public String externalURI;
    public String name;
    public String description;
    public Date   lastModified;
    public String lasContributor;
    public String state;
    public boolean archived = false;
    public boolean isSnapshot = false;
    public String snapshotName;
    public Date dateCreated;
    public String checkinComment;
}
