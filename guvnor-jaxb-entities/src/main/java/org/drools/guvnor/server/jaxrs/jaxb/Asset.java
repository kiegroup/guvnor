/*
 * Copyright 2011 JBoss Inc
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

import java.net.URI;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class Asset {

    private String title;
    private String binaryContentAttachmentFileName;
    private String description;
    private String author;    
    private Date published;
    private URI binaryLink, sourceLink, refLink;
    
    private AssetMetadata metadata;

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
    
    @XmlElement
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement
    public String getBinaryContentAttachmentFileName() {
        return binaryContentAttachmentFileName;
    }

    public void setBinaryContentAttachmentFileName(String binaryContentAttachmentFileName) {
        this.binaryContentAttachmentFileName = binaryContentAttachmentFileName;
    }

    @XmlElement()
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
    @XmlElement
    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }
    
    @XmlElement
    public URI getRefLink() {
        return refLink;
    }

    public void setRefLink(URI refLink) {
        this.refLink = refLink;
    }

    @XmlElement
    public AssetMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AssetMetadata metadata) {
        this.metadata = metadata;
    }

}
