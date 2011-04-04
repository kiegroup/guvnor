package org.drools.guvnor.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Artifact implements IsSerializable {
    public String uuid;
    public String name;
    public String description;
    public Date   lastModified;
    public String lastContributor;
    public String state = "";
    public Date dateCreated;
    public String checkinComment;
    public long versionNumber;
    public boolean isreadonly = false;
}
