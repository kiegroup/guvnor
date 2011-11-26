package org.drools.guvnor.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Artifact
    implements
    IsSerializable {
    public String  uuid;
    public String  name;
    public String  description;
    public Date    lastModified;
    public String  lastContributor;
    public String  state      = "";
    public Date    dateCreated;
    public String  checkinComment;
    public long    versionNumber;
    public boolean readonly = false;
    public String   format = "";
    
    public String getUuid() {
        return uuid;
    }

    public Artifact setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getName() {
        return name;
    }

    public Artifact setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Artifact setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Artifact setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public Artifact setLastContributor(String lastContributor) {
        this.lastContributor = lastContributor;
        return this;
    }

    public String getState() {
        return state;
    }

    public Artifact setState(String state) {
        this.state = state;
        return this;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public Artifact setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public String getCheckinComment() {
        return checkinComment;
    }

    public Artifact setCheckinComment(String checkinComment) {
        this.checkinComment = checkinComment;
        return this;
    }

    public long getVersionNumber() {
        return versionNumber;
    }

    public Artifact setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Artifact setReadonly(boolean isreadonly) {
        this.readonly = isreadonly;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public Artifact setFormat(String format) {
        this.format = format;
        return this;
    }

}
