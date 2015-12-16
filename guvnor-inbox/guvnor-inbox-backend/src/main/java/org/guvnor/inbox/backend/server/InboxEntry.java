/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.inbox.backend.server;

/**
 * And entry in an inbox.
 */
public class InboxEntry {

    private String from;
    private String itemPath;
    private String note;
    private long timestamp;

    public InboxEntry() {
    }

    public InboxEntry( String itemPath,
                       String note,
                       String userFrom ) {
        this.itemPath = itemPath;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
        this.from = userFrom;
    }

    public String getFrom() {
        return from;
    }

    public String getItemPath() {
        return itemPath;
    }

    public String getNote() {
        return note;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
