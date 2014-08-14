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
package org.guvnor.inbox.backend.server;

import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.guvnor.inbox.model.InboxPageRequest;
import org.guvnor.inbox.model.InboxPageRow;
import org.guvnor.inbox.service.InboxService;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.paging.PageResponse;

@Service
@ApplicationScoped
public class InboxServiceImpl
        implements InboxService {

    @Inject
    @SessionScoped
    private User identity;

    @Inject
    private InboxBackend inboxBackend;

    @Inject
    private InboxPageRowBuilder inboxPageRowBuilder;

    public PageResponse<InboxPageRow> loadInbox( InboxPageRequest request ) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        String inboxName = request.getInboxName();
        PageResponse<InboxPageRow> response = new PageResponse<InboxPageRow>();

        List<InboxEntry> entries = loadEntries( inboxName );
        Iterator<InboxEntry> iterator = entries.iterator();
        List<InboxPageRow> rowList = inboxPageRowBuilder
                .withPageRequest( request )
                .withIdentity( identity )
                .withContent( iterator )
                .build();

        response = new PageResponseBuilder<InboxPageRow>()
                .withStartRowIndex( request.getStartRowIndex() )
                .withTotalRowSize( entries.size() )
                .withTotalRowSizeExact()
                .withPageRowList( rowList )
                .withLastPage( !iterator.hasNext() )
                .build();

        return response;
    }

    private List<InboxEntry> loadEntries( final String inboxName ) {
        List<InboxEntry> entries;
        if ( inboxName.equals( RECENT_VIEWED_ID ) ) {
            entries = loadRecentOpened();
        } else if ( inboxName.equals( RECENT_EDITED_ID ) ) {
            entries = loadRecentEdited();
        } else {
            entries = loadIncoming();

        }
        return entries;
    }

    private List<InboxEntry> loadRecentEdited() {
        return inboxBackend.readEntries( identity.getIdentifier(),
                                         RECENT_EDITED_ID );
    }

    private List<InboxEntry> loadRecentOpened() {
        return inboxBackend.readEntries( identity.getIdentifier(),
                                         RECENT_VIEWED_ID );
    }

    private List<InboxEntry> loadIncoming() {
        return inboxBackend.readEntries( identity.getIdentifier(),
                                         INCOMING_ID );
    }

}
