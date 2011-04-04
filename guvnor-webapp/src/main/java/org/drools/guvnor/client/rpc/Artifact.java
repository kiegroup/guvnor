package org.drools.guvnor.client.rpc;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Artifact implements IsSerializable, Serializable {
    public String uuid;
    //public String header;
    //public String externalURI;
    public String name;
    public String description;
    public Date   lastModified;
    public String lastContributor;
    public String state;
    //public boolean archived = false;
    //public boolean isSnapshot = false;
    //public String snapshotName;
    public Date dateCreated;
    public String checkinComment;
    //public HashMap<String,String> catRules;
    //public String[] workspaces;
    //public String[] dependencies;
    public long versionNumber;
    public boolean isreadonly = false;

}
