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
package org.guvnor.inbox.backend.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.inbox.model.InboxIncomingPageRow;
import org.guvnor.inbox.model.InboxPageRequest;
import org.guvnor.inbox.model.InboxPageRow;
import org.kie.commons.io.IOService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.security.Identity;


public class InboxPageRowBuilder
    implements
    PageRowBuilder<InboxPageRequest, Iterator<InboxServiceImpl.InboxEntry>> {

    private InboxPageRequest     pageRequest;
    private Iterator<InboxServiceImpl.InboxEntry> iterator;
    private Identity identity;
    
    @Inject
    private Paths paths;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    
    public List<InboxPageRow> build() {
        validate();
        int skipped = 0;
        Integer pageSize = pageRequest.getPageSize();
        int startRowIndex = pageRequest.getStartRowIndex();
        List<InboxPageRow> rowList = new ArrayList<InboxPageRow>();
        while ( iterator.hasNext() && (pageSize == null || rowList.size() < pageSize) ) {
            InboxServiceImpl.InboxEntry ie = iterator.next();

            if ( skipped >= startRowIndex ) {
                rowList.add( createInboxPageRow( ie,
                                                 pageRequest ) );
            }
            skipped++;
        }
        return rowList;
    }

    private InboxPageRow createInboxPageRow(InboxServiceImpl.InboxEntry inboxEntry,
                                            InboxPageRequest request) {
        InboxPageRow row = null;
        if ( request.getInboxName().equals( InboxServiceImpl.INCOMING_ID ) ) {
            InboxIncomingPageRow tr = new InboxIncomingPageRow();
            //tr.setUuid( inboxEntry.assetUUID );
            //tr.setFormat( AssetFormats.BUSINESS_RULE );
            tr.setNote( inboxEntry.note );
            Path path = convertPath(inboxEntry.itemPath);
            tr.setPath(path);
            tr.setTimestamp( new Date( inboxEntry.timestamp ) );
            tr.setFrom( inboxEntry.from );
            row = tr;

        } else {
            InboxPageRow tr = new InboxPageRow();
            //tr.setUuid( inboxEntry.assetUUID );
            //tr.setFormat( AssetFormats.BUSINESS_RULE );
            tr.setNote( inboxEntry.note );
            Path path = convertPath(inboxEntry.itemPath);
            tr.setPath(path);
            tr.setTimestamp( new Date( inboxEntry.timestamp ) );
            row = tr;
        }
        return row;
    }

	protected Path convertPath(final String fullPath) {
		try {
			final org.kie.commons.java.nio.file.Path path = ioService
					.get(new URI(fullPath));
			return paths.convert(path, false);
		} catch (URISyntaxException e) {
			//Ignore
		}
		return null;
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

    public InboxPageRowBuilder withContent(Iterator<InboxServiceImpl.InboxEntry> iterator) {
        this.iterator = iterator;
        return this;
    }

}
