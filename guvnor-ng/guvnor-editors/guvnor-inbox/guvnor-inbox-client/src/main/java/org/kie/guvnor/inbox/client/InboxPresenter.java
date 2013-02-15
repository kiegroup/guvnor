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

package org.kie.guvnor.inbox.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;

import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.inbox.service.InboxService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Inbox")
public class InboxPresenter {
    
    public static final String RECENT_EDITED_ID = "recentEdited";
    public static final String RECENT_VIEWED_ID = "recentViewed";
    public static final String INCOMING_ID = "incoming";
    
    public interface View
            extends
            IsWidget {
        void setContent( final String inboxName );
    }

    @Inject
    private View view;

    private String inboxName = INCOMING_ID;

    @Inject
    private Caller<InboxService> m2RepoService;
    
    @PostConstruct
    public void init() {
    }

    @OnStart
    public void onStart( final PlaceRequest place  ) {
        this.inboxName = place.getParameter( "inboxname", INCOMING_ID );
        view.setContent(inboxName);
    }

    @OnSave
    public void onSave() {
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @OnClose
    public void onClose() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        //TODO: this does not work. 
        if(INCOMING_ID.equals(inboxName)) {
            return "Incoming Changes";
        } else if(RECENT_EDITED_ID.equals(inboxName)) {
            return "Recently Edited";
        } else if(RECENT_VIEWED_ID.equals(inboxName)) {
            return "Recently Opened";
        }
        
        return "Incoming Changes";
    }

}
