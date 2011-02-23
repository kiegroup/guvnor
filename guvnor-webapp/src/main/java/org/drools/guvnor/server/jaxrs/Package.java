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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Date;
import java.util.Set;

@XmlRootElement()
public class Package {

    private String id;

    private String title;

    private String description;

    private String checkInComment;

    private String type;

    private String snapshot;

    private Category category;

    private long version;

    private URI binaryLink, sourceLink;

    private Set<URI> assets;

    private Date lastModified;

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
    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    @XmlElement()
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public String getCheckInComment() {
        return checkInComment;
    }

    public void setCheckInComment(String checkInComment) {
        this.checkInComment = checkInComment;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @XmlElement
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Set<URI> getAssets() {
        return assets;
    }

    public void setAssets(Set<URI> assets) {
        this.assets = assets;
    }
}
