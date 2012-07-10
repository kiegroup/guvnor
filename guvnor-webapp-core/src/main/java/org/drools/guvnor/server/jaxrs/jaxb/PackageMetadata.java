/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.drools.guvnor.server.jaxrs.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name="metadata")
public class PackageMetadata {

    private String uuid;
    private Date created;
    private String format;
    private String state;    
    private boolean archived;
    private long versionNumber;
    private String checkinComment;

    @XmlElement
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @XmlElement
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
    
    @XmlElement
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @XmlElement
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

    @XmlElement
    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @XmlElement
    public long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(long versionNumber) {
        this.versionNumber = versionNumber;
    }

    @XmlElement
    public String getCheckinComment() {
        return checkinComment;
    }

    public void setCheckinComment(String checkinComment) {
        this.checkinComment = checkinComment;
    }

    @Override
    public String toString() {
        return "PackageMetadata{" +
                "uuid='" + uuid + '\'' +
                ", created=" + created +
                ", format='" + format + '\'' +
                ", state='" + state + '\'' +
                ", archived=" + archived +
                ", versionNumber=" + versionNumber +
                ", checkinComment='" + checkinComment + '\'' +
                '}';
    }

}
