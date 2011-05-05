package org.drools.guvnor.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Artifact
    implements
    IsSerializable {
    private String  uuid;
    private String  name;
    private String  description;
    private Date    lastModified;
    private String  lastContributor;
    private String  state      = "";
    private Date    dateCreated;
    private String  checkinComment;
    private long    versionNumber;
    private boolean readonly = false;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public void setLastContributor(String lastContributor) {
        this.lastContributor = lastContributor;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCheckinComment() {
        return checkinComment;
    }

    public void setCheckinComment(String checkinComment) {
        this.checkinComment = checkinComment;
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean isreadonly) {
        this.readonly = isreadonly;
    }

}
