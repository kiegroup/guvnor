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
package org.drools.guvnor.client.rpc;

import org.drools.guvnor.client.explorer.ExplorerNodeConfig;

/**
 * A Query request.
 * 
 * @author manstis
 */
public class InboxPageRequest extends PageRequest {

    private String mode;

    // For GWT serialisation
    public InboxPageRequest() {
    }

    public InboxPageRequest(String mode,
                            int startRowIndex,
                            Integer pageSize) {
        super( startRowIndex,
               pageSize );
        validateMode( mode );
        this.mode = mode;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getMode() {
        return mode;
    }

    public void setInboxMode(String mode) {
        validateMode( mode );
        this.mode = mode;
    }

    private void validateMode(String mode) {
        // An enum would have been nice but existing inbox handling "generally"
        // uses these constants extensively throughout existing code
        if ( !mode.equals( ExplorerNodeConfig.RECENT_EDITED_ID )
             && !mode.equals( ExplorerNodeConfig.RECENT_VIEWED_ID )
             && !mode.equals( ExplorerNodeConfig.INCOMING_ID ) ) {
            throw new IllegalArgumentException( "Unexpected value for 'mode'" );
        }
    }

}
