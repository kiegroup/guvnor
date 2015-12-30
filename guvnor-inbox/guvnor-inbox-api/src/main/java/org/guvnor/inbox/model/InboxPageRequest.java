/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.inbox.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.paging.PageRequest;

/**
 * A Query request.
 */
@Portable
public class InboxPageRequest extends PageRequest {

    private String inboxName;

    public InboxPageRequest( @MapsTo("inboxName") String inboxName,
                             @MapsTo("startRowIndex") int startRowIndex,
                             @MapsTo("pageSize") Integer pageSize ) {
        super( startRowIndex,
               pageSize );
        validateInboxName( inboxName );
        this.inboxName = inboxName;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getInboxName() {
        return inboxName;
    }

    public void setInboxName( String inboxName ) {
        validateInboxName( inboxName );
        this.inboxName = inboxName;
    }

    private void validateInboxName( String inboxName ) {
        // An enum would have been nice but existing inbox handling "generally"
        // uses these constants extensively throughout existing code
/*        if ( !inboxName.equals( ExplorerNodeConfig.RECENT_EDITED_ID )
             && !inboxName.equals( ExplorerNodeConfig.RECENT_VIEWED_ID )
             && !inboxName.equals( ExplorerNodeConfig.INCOMING_ID ) ) {
            throw new IllegalArgumentException( "Unexpected value for 'inboxName'" );
        }*/
    }

}
