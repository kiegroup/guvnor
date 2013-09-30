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

package org.guvnor.inbox.service;

import org.guvnor.inbox.model.InboxPageRequest;
import org.guvnor.inbox.model.InboxPageRow;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.paging.PageResponse;

@Remote
public interface InboxService {

    static final String RECENT_EDITED_ID = "recentEdited";
    static final String RECENT_VIEWED_ID = "recentViewed";
    static final String INCOMING_ID = "incoming";

    /**
     * Load the data for a given inbox for the currently logged in user.
     */
    public PageResponse<InboxPageRow> loadInbox( InboxPageRequest request );

}
