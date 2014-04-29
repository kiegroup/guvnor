/*
 * Copyright 2011 JBoss Inc
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

package org.guvnor.udc.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.paging.AbstractPageRow;

/**
 * A single row of a paged data
 */
@Portable
public class InboxPageRow extends AbstractPageRow {

    private String note;
    private Date timestamp;
    private String format;

    public String getNote() {
        return note;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setNote( String note ) {
        this.note = note;
    }

    public void setTimestamp( Date timestamp ) {
        this.timestamp = timestamp;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat( String format ) {
        this.format = format;
    }

}
