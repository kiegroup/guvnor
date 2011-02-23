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

package org.drools.guvnor.server.jaxrs;

import java.net.URI;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class Asset {

    private String id;

    private String title;

    private String description;

    private Date lastmodified;

    private String checkInComment;
    
    private long version;

    private String type;

    private URI binaryLink, sourceLink, refLink;

    @XmlElement
    public URI getBinaryLink() {
        return binaryLink;
    }

    public void setBinaryLink(URI binaryLink) {
        this.binaryLink = binaryLink;
    }

    @XmlElement
    public URI getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(URI sourceLink) {
        this.sourceLink = sourceLink;
    }

    @XmlElement()
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement()
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement()
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement()
    public Date getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(Date lastmodified) {
        this.lastmodified = lastmodified;
    }

    @XmlElement()
    public String getCheckInComment() {
        return checkInComment;
    }
   
    public void setCheckInComment(String checkInComment) {
        this.checkInComment = checkInComment;
    }

    @XmlElement()
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public URI getRefLink() {
        return refLink;
    }

    public void setRefLink(URI refLink) {
        this.refLink = refLink;
    }
}
