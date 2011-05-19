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

import java.util.Collection;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AuthorPerspectivePlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.GuvnorActivityMapper;
import org.drools.guvnor.client.explorer.GuvnorPlaceHistoryMapper;
import org.drools.guvnor.client.explorer.LoadPerspectives;
import org.drools.guvnor.client.explorer.Perspective;
import org.drools.guvnor.client.explorer.PerspectiveLoader;
import org.drools.guvnor.client.explorer.PerspectivesPanel;
import org.drools.guvnor.client.explorer.Preferences;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.OperatorsResource;
import org.drools.guvnor.client.resources.RoundedCornersResource;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.ruleeditor.StandaloneEditorManager;
import org.drools.guvnor.client.security.CapabilitiesManager;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the main launching/entry point for the JBRMS web console. It
 * essentially sets the initial layout.
 * <p/>
 * If you hadn't noticed, this is using GWT from google. Refer to GWT docs if
 * GWT is new to you (it is quite a different way of building web apps).
 */
public class JBRMSEntryPoint
        implements
        EntryPoint {

    private Constants         constants = GWT.create( Constants.class );
    private PerspectivesPanel perspectivesPanel;

    public void onModuleLoad() {
        loadStyles();
        hideLoadingPopup();
        checkLogIn();
    }

    private void loadStyles() {
        GuvnorResources.INSTANCE.headerCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
        OperatorsResource.INSTANCE.operatorsCss().ensureInjected();
    }

    /**
     * Check if user is logged in, if not, then show prompt. If it is, then we
     * show the app, in all its glory !
     */
    private void checkLogIn() {
        RepositoryServiceFactory.getSecurityService().getCurrentUser( new GenericCallback<UserSecurityContext>() {
            public void onSuccess(UserSecurityContext userSecurityContext) {
                String userName = userSecurityContext.getUserName();
                if ( userName != null ) {
                    showMain( userName );
                } else {
                    logIn();
                }
            }
        } );
    }

    private void logIn() {
        final LoginWidget loginWidget = new LoginWidget();
        loginWidget.setLoggedInEvent( new Command() {
            public void execute() {
                showMain( loginWidget.getUserName() );
            }
        } );
        loginWidget.show();
    }

    private void showMain(final String userName) {

        Window.setStatus( constants.LoadingUserPermissions() );

        CapabilitiesManager.getInstance().refreshAllowedCapabilities( new Command() {
            public void execute() {

                Preferences.INSTANCE.loadPrefs( CapabilitiesManager.getInstance().getCapabilities() );

                Window.setStatus( " " );

                createMain();

                perspectivesPanel.setUserName( userName );
            }
        } );
    }

    /**
     * Creates the main view of Guvnor. The path used to invoke guvnor is used
     * to identify the view to show: If the path contains
     * "StandaloneEditor.html" then the StandaloneGuidedEditorManager is used to
     * render the view. If not, the default view is shown.
     */
    private void createMain() {
        if ( Window.Location.getPath().contains( "StandaloneEditor.html" ) ) {
            RootLayoutPanel.get().add( new StandaloneEditorManager().getBaseLayout() );
        } else {

            ClientFactory clientFactory = GWT.create( ClientFactory.class );
            EventBus eventBus = clientFactory.getEventBus();
            PlaceController placeController = clientFactory.getPlaceController();
            Perspective defaultPlace = new AuthorPerspectivePlace();

            perspectivesPanel = new PerspectivesPanel( clientFactory.getPerspectivesPanelView( hideTitle() ),
                                                       placeController );

            loadPerspectives();

            // TODo: Hide the dropdown if the default one is the only one -Rikkola-

            ActivityMapper activityMapper = new GuvnorActivityMapper( clientFactory );
            ActivityManager activityManager = new ActivityManager( activityMapper,
                                                                   eventBus );
            activityManager.setDisplay( perspectivesPanel );

            GuvnorPlaceHistoryMapper historyMapper = GWT.create( GuvnorPlaceHistoryMapper.class );
            PlaceHistoryHandler historyHandler = new PlaceHistoryHandler( historyMapper );
            historyHandler.register( placeController,
                                     eventBus,
                                     defaultPlace );

            historyHandler.handleCurrentHistory();

            RootLayoutPanel.get().add( perspectivesPanel.getView() );
        }

    }

    private void loadPerspectives() {
        ConfigurationServiceAsync configurationServiceAsync = GWT.create( ConfigurationService.class );

        PerspectiveLoader perspectiveLoader = new PerspectiveLoader( configurationServiceAsync );
        perspectiveLoader.loadPerspectives( new LoadPerspectives() {
            public void loadPerspectives(Collection<Perspective> perspectives) {
                for ( Perspective perspective : perspectives ) {
                    perspectivesPanel.addPerspective( perspective );
                }
            }
        } );
    }

    private boolean hideTitle() {
        String parameter = Window.Location.getParameter( "nochrome" );

        if ( parameter == null ) {
            return true;
        } else {
            return parameter.equals( "true" );
        }
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        Animation r = new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Visibility.HIDDEN );
            }

        };

        r.run( 500 );

    }

}
