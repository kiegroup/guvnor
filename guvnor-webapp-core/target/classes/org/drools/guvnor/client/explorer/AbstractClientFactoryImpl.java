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

import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactoryImpl;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.ConfigurationService;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SecurityServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;

public abstract class AbstractClientFactoryImpl
        implements
        ClientFactory {

	protected final PlaceController     placeController;
    protected NavigationViewFactoryImpl navigationViewFactory;

    protected AssetEditorFactory        assetEditorFactory;
    protected PerspectiveFactory        perspectiveFactory;
    protected PlaceHistoryHandler       placeHistoryHandler;
    protected GuvnorPlaceHistoryMapper  guvnorPlaceHistoryMapper;
    protected final EventBus            eventBus;

    public AbstractClientFactoryImpl(EventBus eventBus) {
        this.eventBus = eventBus;
        this.placeController = new PlaceController( eventBus );
    }

    public PlaceController getPlaceController() {
        return placeController;
    }

    public NavigationViewFactory getNavigationViewFactory() {
        if ( navigationViewFactory == null ) {
            navigationViewFactory = new NavigationViewFactoryImpl( this,
                                                                   eventBus );
        }
        return navigationViewFactory;
    }

    public ConfigurationServiceAsync getConfigurationService() {
        return GWT.create( ConfigurationService.class );
    }

    public MultiActivityManager getActivityManager() {
        return new MultiActivityManager(
                eventBus, this.getActivityMapper());
    }

    public PlaceHistoryHandler getPlaceHistoryHandler() {
        if ( placeHistoryHandler == null ) {
            placeHistoryHandler = new PlaceHistoryHandler( getPlaceHistoryMapper() );
        }
        return placeHistoryHandler;
    }

    public ModuleServiceAsync getModuleService() {
        return RepositoryServiceFactory.getPackageService();
    }

    public RepositoryServiceAsync getService() {
        return RepositoryServiceFactory.getService();
    }

    public AssetEditorFactory getAssetEditorFactory() {
        if ( assetEditorFactory == null ) {
            assetEditorFactory = GWT.create( AssetEditorFactory.class );
        }
        return assetEditorFactory;
    }

    public PerspectiveFactory getPerspectiveFactory() {
        if ( perspectiveFactory == null ) {
            perspectiveFactory = GWT.create( PerspectiveFactory.class );
        }
        return perspectiveFactory;
    }

    public RepositoryServiceAsync getRepositoryService() {
        return RepositoryServiceFactory.getService();
    }

    public CategoryServiceAsync getCategoryService() {
        return RepositoryServiceFactory.getCategoryService();
    }

    public AssetServiceAsync getAssetService() {
        return RepositoryServiceFactory.getAssetService();
    }

    public SecurityServiceAsync getSecurityService() {
        return RepositoryServiceFactory.getSecurityService();
    }
}
