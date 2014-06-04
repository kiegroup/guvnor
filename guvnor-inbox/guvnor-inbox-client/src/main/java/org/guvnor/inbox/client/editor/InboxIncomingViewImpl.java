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

package org.guvnor.inbox.client.editor;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import org.guvnor.inbox.client.InboxPresenter;
import org.guvnor.inbox.client.resources.i18n.InboxConstants;
import org.guvnor.inbox.model.InboxIncomingPageRow;
import org.guvnor.inbox.model.InboxPageRow;
import org.guvnor.inbox.service.InboxService;
import org.jboss.errai.common.client.api.Caller;

/**
 * Widget with a table of inbox entries results.
 */
public class InboxIncomingViewImpl extends InboxViewImpl implements InboxIncomingView {

    public InboxIncomingViewImpl( final Caller<InboxService> inboxService,
                                  final String inboxName,
                                  final InboxPresenter presenter ) {
        super( inboxService,
               inboxName,
               presenter );
        Column<InboxPageRow, String> fromColumn = new TextColumn<InboxPageRow>() {
            public String getValue( InboxPageRow row ) {
                return ( (InboxIncomingPageRow) row ).getFrom();
            }
        };
        addColumn( fromColumn,
                   InboxConstants.INSTANCE.from() );

    }

}
