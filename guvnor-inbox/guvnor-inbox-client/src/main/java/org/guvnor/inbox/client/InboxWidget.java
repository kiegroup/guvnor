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

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.inbox.client.editor.InboxViewImpl;
import org.guvnor.inbox.service.InboxService;
import org.jboss.errai.common.client.api.Caller;

public class InboxWidget
        extends VerticalPanel
        implements InboxPresenter.View,
                   ProvidesResize,
                   RequiresResize {

    private InboxViewImpl table;

    @Inject
    public InboxWidget() {
        setWidth( "100%" );
    }

    @Override
    public void init( final String inboxName,
                      final Caller<InboxService> inboxService,
                      final InboxPresenter presenter ) {
        table = new InboxViewImpl( inboxService,
                                   inboxName,
                                   presenter );
        add( table );
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent == null ) {
            return;
        }
        final int width = parent.getOffsetWidth();
        final int height = parent.getOffsetHeight();
        if ( width < 0 ) {
            return;
        }
        if ( height < 0 ) {
            return;
        }
        setPixelSize( width,
                      height );

        if ( table == null ) {
            return;
        }
        table.onResize();
    }
}
