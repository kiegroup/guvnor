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

package org.kie.guvnor.services.metadata.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Portable
public class Metadata {

    private Path path;
    private Date lastModified = new Date();
    private Date dateCreated  = new Date();

    private boolean disabled;

    private String lastContributor  = "manstis";
    private String creator          = "porcelli";
    private String format           = "model.drl";
    private String subject          = "subject here";
    private String type             = "some content";
    private String externalRelation = "external relation";
    private String externalSource   = "external content";
    private String description;

    //not dcore
    private String                 checkinComment = "cool";
    private List<String>           categories     = new ArrayList<String>();
    private List<DiscussionRecord> discussion     = new ArrayList<DiscussionRecord>();

    public Path getPath() {
        return path;
    }

    public void setPath( final Path path ) {
        this.path = path;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public String getCheckinComment() {
        return checkinComment;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getCreator() {
        return creator;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled( final boolean disabled ) {
        this.disabled = disabled;
    }

    public String getFormat() {
        return format;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject( final String subject ) {
        this.subject = subject;
    }

    public String getType() {
        return type;
    }

    public void setType( final String type ) {
        this.type = type;
    }

    public String getExternalRelation() {
        return externalRelation;
    }

    public void setExternalRelation( final String externalRelation ) {
        this.externalRelation = externalRelation;
    }

    public String getExternalSource() {
        return externalSource;
    }

    public void setExternalSource( final String externalSource ) {
        this.externalSource = externalSource;
    }

    public void addCategory( final String category ) {
        categories.add( category );
    }

    public List<String> getCategories() {
        return categories;
    }

    public void removeCategory( final int idx ) {
        categories.remove( idx );
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( final String description ) {
        this.description = description;
    }

    public List<DiscussionRecord> getDiscussion() {
        return discussion;
    }

    public void addDiscussion( final DiscussionRecord discussionRecord ) {
        discussion.add( discussionRecord );
    }

    public void eraseDiscussion() {
        discussion.clear();
    }
}
