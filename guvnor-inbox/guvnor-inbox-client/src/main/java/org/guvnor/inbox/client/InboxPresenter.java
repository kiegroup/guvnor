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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.inbox.client.resources.i18n.InboxConstants;
import org.guvnor.inbox.model.InboxPageRow;
import org.guvnor.inbox.service.InboxService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Inbox")
public class InboxPresenter {

    public static final String RECENT_EDITED_ID = "recentEdited";
    public static final String RECENT_VIEWED_ID = "recentViewed";
    public static final String INCOMING_ID = "incoming";

    public interface View
            extends
            IsWidget {

        void init( final String inboxName,
                   final Caller<InboxService> inboxService,
                   final InboxPresenter presenter );

    }

    @Inject
    private View view;

    private String inboxName = INCOMING_ID;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<InboxService> inboxService;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.inboxName = place.getParameter( "inboxname",
                                             INCOMING_ID );
        view.init( inboxName,
                   inboxService,
                   this );
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        //TODO: this does not work. 
        if ( INCOMING_ID.equals( inboxName ) ) {
            return InboxConstants.INSTANCE.incomingChanges();
        } else if ( RECENT_EDITED_ID.equals( inboxName ) ) {
            return InboxConstants.INSTANCE.recentlyEdited();
        } else if ( RECENT_VIEWED_ID.equals( inboxName ) ) {
            return InboxConstants.INSTANCE.recentlyOpened();
        }

        return "Incoming Changes";
    }

    public void open( final InboxPageRow row ) {
        final Path path = row.getPath();
        if ( path != null ) {
            placeManager.goTo( new PathPlaceRequest( path ) );
        }
    }

}
