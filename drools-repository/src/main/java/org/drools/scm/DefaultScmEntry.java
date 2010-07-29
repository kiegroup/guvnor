/**
 * Copyright 2010 JBoss Inc
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

package org.drools.scm;

import java.io.File;
import java.util.Date;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

public class DefaultScmEntry
    implements
    ScmEntry {
//    private SVNDirEntry entry;
    
    private String author;
    private Date date;
    private String name;
    private String path;
    private long revision;
    private long size;
    private int type;
   
    public DefaultScmEntry() {
        
    }       
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public long getRevision() {
        return revision;
    }
    public void setRevision(long revision) {
        this.revision = revision;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
      
    public boolean isFile() {
        return this.type == ScmEntry.FILE;
    }
    
    public boolean isDirectory() {
        return this.type == ScmEntry.DIRECTORY;
    }
    
}
