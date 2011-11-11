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
package org.drools.guvnor.server.builder.pagerow;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ExplorerNodeConfig;
import org.drools.guvnor.client.rpc.InboxIncomingPageRow;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.repository.UserInfo.InboxEntry;
import org.jboss.seam.security.Identity;

public class InboxPageRowBuilder
    implements
    PageRowBuilder<InboxPageRequest, Iterator<InboxEntry>> {

    private InboxPageRequest     pageRequest;
    private Iterator<InboxEntry> iterator;
    private Identity identity;

    public List<InboxPageRow> build() {
        validate();
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        List<InboxPageRow> rowList = new ArrayList<InboxPageRow>();
        while ( iterator.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            InboxEntry ie = iterator.next();

            if ( skipped >= startRowIndex ) {
                rowList.add( createInboxPageRow( ie,
                                                 pageRequest ) );
            }
            skipped++;
        }
        return rowList;
    }

    private InboxPageRow createInboxPageRow(InboxEntry inboxEntry,
                                            InboxPageRequest request) {
        InboxPageRow row = null;
        if ( request.getInboxName().equals( ExplorerNodeConfig.INCOMING_ID ) ) {
            InboxIncomingPageRow tr = new InboxIncomingPageRow();
            tr.setUuid( inboxEntry.assetUUID );
            tr.setFormat( AssetFormats.BUSINESS_RULE );
            tr.setNote( inboxEntry.note );
            tr.setName( inboxEntry.note );
            tr.setTimestamp( new Date( inboxEntry.timestamp ) );
            tr.setFrom( inboxEntry.from );
            row = tr;

        } else {
            InboxPageRow tr = new InboxPageRow();
            tr.setUuid( inboxEntry.assetUUID );
            tr.setFormat( AssetFormats.BUSINESS_RULE );
            tr.setNote( inboxEntry.note );
            tr.setName( inboxEntry.note );
            tr.setTimestamp( new Date( inboxEntry.timestamp ) );
            row = tr;
        }
        return row;
    }

    public void validate() {
        if ( pageRequest == null ) {
            throw new IllegalArgumentException( "PageRequest cannot be null" );
        }

        if ( iterator == null ) {
            throw new IllegalArgumentException( "Content cannot be null" );
        }

    }

    public InboxPageRowBuilder withPageRequest(InboxPageRequest pageRequest) {
        this.pageRequest = pageRequest;
        return this;
    }

    public InboxPageRowBuilder withIdentity(Identity identity) {
        this.identity = identity;
        return this;
    }

    public InboxPageRowBuilder withContent(Iterator<InboxEntry> iterator) {
        this.iterator = iterator;
        return this;
    }

}
