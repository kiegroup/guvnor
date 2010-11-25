/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ExplorerLayoutManager;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.RoundedCornersResource;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.ruleeditor.StandaloneEditorManager;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.security.CapabilitiesManager;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * This is the main launching/entry point for the JBRMS web console.
 * It essentially sets the initial layout.
 *
 * If you hadn't noticed, this is using GWT from google. Refer to GWT docs
 * if GWT is new to you (it is quite a different way of building web apps).
 */
public class JBRMSEntryPoint
    implements
    EntryPoint {

    private LoggedInUserInfo loggedInUserInfo;

    public void onModuleLoad() {

        GuvnorResources.INSTANCE.headerCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();

        loggedInUserInfo = new LoggedInUserInfo();
        loggedInUserInfo.setVisible( false );
        checkLoggedIn();
    }

    /**
     * Creates the main view of Guvnor.
     * The path used to invoke guvnor is used to identify the 
     * view to show:
     * If the path contains "StandaloneEditor.html" then the StandaloneGuidedEditorManager is used
     * to render the view.
     * If not, the default view (created by ExplorerLayoutManager) is shown.
     * @return Guvnor's main view.
     */
    private Panel createMain() {
        if ( Window.Location.getPath().contains( "StandaloneEditor.html" ) ) {
            return (new StandaloneEditorManager().getBaseLayout());
        }
        return (new ExplorerLayoutManager( loggedInUserInfo )).getBaseLayout();
    }

    /**
     * Check if user is logged in, if not, then show prompt.
     * If it is, then we show the app, in all its glory !
     */
    private void checkLoggedIn() {
        RepositoryServiceFactory.getSecurityService().getCurrentUser( new GenericCallback<UserSecurityContext>() {
            public void onSuccess(UserSecurityContext ctx) {
                if ( ctx.userName != null ) {
                    showMain();
                    loggedInUserInfo.setUserName( ctx.userName );
                    loggedInUserInfo.setVisible( true );
                } else {
                    final LoginWidget lw = new LoginWidget();
                    lw.setLoggedInEvent( new Command() {
                        public void execute() {
                            showMain();
                            loggedInUserInfo.setUserName( lw.getUserName() );
                            loggedInUserInfo.setVisible( true );
                        }
                    } );
                    lw.show();
                }
            }
        } );
    }

    private void showMain() {
        Window.setStatus( ((Constants) GWT.create( Constants.class )).LoadingUserPermissions() );

        CapabilitiesManager.getInstance().refreshAllowedCapabilities( new Command() {

            public void execute() {
                Window.setStatus( " " );
                RootLayoutPanel.get().add( createMain() );
            }
        } );

        // Setup a history handler to reselect the associate menu item
        final ValueChangeHandler<String> historyHandler = new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                //TODO: Handle History
            }
        };
        History.addValueChangeHandler( historyHandler );
    }
}
