/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationPanelFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactoryImpl;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.PackageServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController( eventBus );
    private PerspectivesPanelView perspectivesPanelView;
    private NavigationViewFactoryImpl authorNavigationViewFactory;
    private AssetEditorFactory assetEditorFactory;

    public PlaceController getPlaceController() {
        return placeController;
    }

    public AuthorPerspectiveView getAuthorPerspectiveView( NavigationPanelFactory navigationPanelFactory ) {
        return new AuthorPerspectiveViewImpl( this, navigationPanelFactory );
    }

    public RuntimePerspectiveView getRuntimePerspectiveView( NavigationPanelFactory navigationPanelFactory ) {
        return null;  //TODO: Generated code -Rikkola-
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public PerspectivesPanelView getPerspectivesPanelView() {
        if ( perspectivesPanelView == null ) {
            perspectivesPanelView = new PerspectivesPanelViewImpl(
                    getAuthorPerspectiveView( new NavigationPanelFactory( getNavigationViewFactory() ) ),
                    new ExplorerViewCenterPanel( this ),
                    hideTitle() );
        }
        return perspectivesPanelView;
    }

    public IFramePerspectiveView getIFramePerspectiveView() {
        return new IFramePerspectiveViewImpl();
    }

    public NavigationViewFactory getNavigationViewFactory() {
        if ( authorNavigationViewFactory == null ) {
            authorNavigationViewFactory = new NavigationViewFactoryImpl();
        }
        return authorNavigationViewFactory;
    }

    public ConfigurationServiceAsync getConfigurationService() {
        return GWT.create( ConfigurationService.class );
    }

    public ActivityManager getActivityManager() {
        return new ActivityManager(
                new GuvnorActivityMapper( this ),
                getEventBus() );
    }

    public PlaceHistoryHandler getPlaceHistoryHandler() {
        GuvnorPlaceHistoryMapper historyMapper = GWT.create( GuvnorPlaceHistoryMapper.class );
        return new PlaceHistoryHandler( historyMapper );
    }

    public ModuleEditorActivityView getModuleEditorActivityView() {
        return new ModuleEditorActivityViewImpl();
    }

    public PackageServiceAsync getPackageService() {
        return RepositoryServiceFactory.getPackageService();
    }

    public AssetEditorFactory getAssetEditorFactory() {
        if ( assetEditorFactory == null ) {
            assetEditorFactory = GWT.create( AssetEditorFactory.class );
        }
        return assetEditorFactory;
    }

    private boolean hideTitle() {
        String parameter = Window.Location.getParameter( "nochrome" );

        if ( parameter == null ) {
            return true;
        } else {
            return parameter.equals( "true" );
        }
    }
}
