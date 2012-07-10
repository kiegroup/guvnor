/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.drools.guvnor.server.jaxrs.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name="metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomPackageMetadata {

    @XmlElement
    private Uuid uuid;
    @XmlElement
    private Created created;
    @XmlElement
    private Format format;
    @XmlElement
    private State state;
    @XmlElement
    private Archived archived;
    @XmlElement
    private VersionNumber versionNumber;
    @XmlElement
    private CheckinComment checkinComment;


    public String getUuid() {
        return uuid != null?uuid.getValue():"";
    }

    public void setUuid(String uuid) {
        if (this.uuid == null) {
            this.uuid = new Uuid();
        }
        this.uuid.setValue(uuid);
    }

    public Date getCreated() {
        return created != null ?created.getValue():null;
    }

    public void setCreated(Date created) {
        if (this.created == null) {
            this.created = new Created();
        }
        this.created.setValue(created);
    }

    public String getFormat() {
        return format != null ? format.getValue() : "";
    }

    public void setFormat(String format) {
        if (this.format == null) {
            this.format = new Format();
        }
        this.format.setValue(format);
    }

  	public String getState() {
		return state != null ? state.getValue() : "";
	}

	public void setState(String state) {
        if (this.state == null) {
            this.state = new State();
        }
		this.state.setValue(state);
	}

    public boolean isArchived() {
        return archived != null ? archived.getValue() : false;
    }

    public void setArchived(boolean archived) {
        if (this.archived == null) {
            this.archived = new Archived();
        }
        this.archived.setValue(archived);
    }

    public long getVersionNumber() {
        return versionNumber != null ? versionNumber.getValue() : -1L;
    }

    public void setVersionNumber(long versionNumber) {
        if (this.versionNumber == null) {
            this.versionNumber = new VersionNumber();
        }
        this.versionNumber.setValue(versionNumber);
    }

    public String getCheckinComment() {
        return checkinComment != null ? checkinComment.getValue() : "";
    }

    public void setCheckinComment(String checkinComment) {
        if (this.checkinComment == null ) {
            this.checkinComment = new CheckinComment();
        }
        this.checkinComment.setValue(checkinComment);
    }

}
