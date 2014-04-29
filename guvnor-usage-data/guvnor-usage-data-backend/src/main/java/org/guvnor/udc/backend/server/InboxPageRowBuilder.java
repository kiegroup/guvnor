/*
 * Copyright 2013 JBoss Inc
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
package org.guvnor.udc.backend.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.guvnor.udc.model.InboxIncomingPageRow;
import org.guvnor.udc.model.InboxPageRequest;
import org.guvnor.udc.model.InboxPageRow;
import org.guvnor.udc.model.UsageEventSummary;
import org.guvnor.udc.service.PageRowBuilder;
import org.uberfire.security.Identity;


public class InboxPageRowBuilder implements PageRowBuilder<InboxPageRequest, Iterator<UsageEventSummary>> {

    private InboxPageRequest     pageRequest;
    private Iterator<UsageEventSummary> iterator;
    private Identity identity;

    public List<InboxPageRow> build() {
        validate();
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        List<InboxPageRow> rowList = new ArrayList<InboxPageRow>();
        while ( iterator.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            UsageEventSummary ie = iterator.next();

            if ( skipped >= startRowIndex ) {
                rowList.add( createInboxPageRow( ie,
                                                 pageRequest ) );
            }
            skipped++;
        }
        return rowList;
    }

    private InboxPageRow createInboxPageRow(UsageEventSummary inboxEntry,
                                            InboxPageRequest request) {
        InboxPageRow row = null;
        if ( request.getInboxName().equals( UDCVfsServiceImpl.INCOMING_ID ) ) {
            InboxIncomingPageRow tr = new InboxIncomingPageRow();
            //tr.setUuid( inboxEntry.assetUUID );
            //tr.setFormat( AssetFormats.BUSINESS_RULE );
            tr.setNote( inboxEntry.getFileName() );
            //tr.setName( inboxEntry.note );
            tr.setTimestamp( new Date( inboxEntry.getTimestampResource() ) );
            tr.setFrom( inboxEntry.getFrom() );
            row = tr;

        } else {
            InboxPageRow tr = new InboxPageRow();
            //tr.setUuid( inboxEntry.assetUUID );
            //tr.setFormat( AssetFormats.BUSINESS_RULE );
            tr.setNote( inboxEntry.getFileName() );
            //tr.setName( inboxEntry.note );
            tr.setTimestamp( new Date( inboxEntry.getTimestampResource() ) );
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

    public InboxPageRowBuilder withContent(Iterator<UsageEventSummary> iterator) {
        this.iterator = iterator;
        return this;
    }

}
