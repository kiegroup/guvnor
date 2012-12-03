/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.guvnor.datamodel.model.auditlog;

import java.util.Date;

/**
 * An entry in an Audit Log
 */
public abstract class AuditLogEntry {

    private static final long serialVersionUID = -6751253344147726552L;

    private Date              dateOfEntry;
    private String            userName;
    private String            userComment;
    private boolean           isDeleted;

    public AuditLogEntry() {
        this.dateOfEntry = new Date();
        this.userName = "";
        this.userComment = "";
        this.isDeleted = false;
    }

    public AuditLogEntry(final String userName) {
        this.dateOfEntry = new Date();
        this.userName = userName;
        this.userComment = "";
        this.isDeleted = false;
    }

    public Date getDateOfEntry() {
        return new Date( dateOfEntry.getTime() );
    }

    public String getUserName() {
        return userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public abstract String getGenericType();

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
