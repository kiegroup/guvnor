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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.PathChangeEvent;
import org.guvnor.inbox.client.editor.InboxEditor;
import org.guvnor.inbox.service.InboxService;

import javax.enterprise.event.Event;
import javax.inject.Inject;


public class InboxView
        extends Composite
        implements InboxPresenter.View {

    private VerticalPanel       layout;
    
/*    @Inject*/
    private Caller<InboxService> inboxService;
    
    private PlaceManager placeManager;
    
    private Event<PathChangeEvent> pathChangeEvent;
    
    @Inject
    public InboxView(Caller<InboxService> s, PlaceManager placeManager, Event<PathChangeEvent> pathChangeEvent) {
        this.inboxService = s;
        this.placeManager = placeManager;
        this.pathChangeEvent = pathChangeEvent;

        layout = new VerticalPanel();
        layout.setWidth( "100%" );
        initWidget( layout );
        setWidth( "100%" );
    }
    
    @Override
    public void setContent( final String inboxName ) {
        InboxEditor table = new InboxEditor(inboxService, inboxName, placeManager, pathChangeEvent);    
        layout.add(table);
    }

 }
