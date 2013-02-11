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

package org.kie.guvnor.inbox.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.guvnor.commons.data.tables.PageRequest;
import org.kie.guvnor.commons.data.tables.PageResponse;
import org.kie.guvnor.inbox.model.InboxPageRequest;
import org.kie.guvnor.inbox.model.InboxPageRow;
import org.kie.guvnor.inbox.service.InboxService;
import org.kie.guvnor.m2repo.model.JarListPageRow;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 *
 */
@Service
@ApplicationScoped
public class InboxServiceImpl
        implements InboxService {
    
    public PageResponse<InboxPageRow> loadInbox(InboxPageRequest request) {
        if ( request == null ) {
            throw new IllegalArgumentException( "request cannot be null" );
        }
        if ( request.getPageSize() != null && request.getPageSize() < 0 ) {
            throw new IllegalArgumentException( "pageSize cannot be less than zero." );
        }

        String inboxName = request.getInboxName();
        PageResponse<InboxPageRow> response = new PageResponse<InboxPageRow>();
        
        List<InboxPageRow> inboxRowList = new ArrayList<InboxPageRow>();
        response.setPageRowList(inboxRowList);
        response.setStartRowIndex(request.getStartRowIndex());
        response.setTotalRowSize(0);
        response.setTotalRowSizeExact(true);
        
        //long start = System.currentTimeMillis();
/*        try {

            List<InboxEntry> entries = new UserInbox( rulesRepository ).loadEntries( inboxName );

            log.debug( "Search time: " + (System.currentTimeMillis() - start) );

            Iterator<InboxEntry> iterator = entries.iterator();
            List<InboxPageRow> rowList = new InboxPageRowBuilder()
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
            long methodDuration = System.currentTimeMillis() - start;
            log.debug( "Queried inbox ('" + inboxName + "') in " + methodDuration + " ms." );

        } catch ( Exception e ) {
            log.error( "Unable to load Inbox: " + e.getMessage() );
            throw new DetailedSerializationException( "Unable to load Inbox",
                                                      e.getMessage() );
        }*/
        return response;
    }

}
