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
package org.guvnor.inbox.client;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.guvnor.inbox.client.editor.InboxPagedTable;
import org.guvnor.inbox.service.InboxService;
import org.jboss.errai.common.client.api.Caller;

public class InboxView
        extends Composite
        implements InboxPresenter.View {

    private VerticalPanel layout;

    @Inject
    public InboxView() {
        layout = new VerticalPanel();
        layout.setWidth( "100%" );
        initWidget( layout );
        setWidth( "100%" );
    }

    @Override
    public void init( final String inboxName,
                      final Caller<InboxService> inboxService,
                      final InboxPresenter presenter ) {
        final InboxPagedTable table = new InboxPagedTable( inboxService,
                                                           inboxName,
                                                           presenter );
        layout.add( table );
    }

}
