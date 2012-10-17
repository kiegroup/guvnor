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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactoryImpl;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.rpc.*;

public abstract class AbstractClientFactoryImpl
        implements
        ClientFactory {

    protected final PlaceController placeController;
    protected NavigationViewFactoryImpl navigationViewFactory;

    protected AssetEditorFactory assetEditorFactory;
    protected PerspectiveFactory perspectiveFactory;
    protected PlaceHistoryHandler placeHistoryHandler;
    protected final EventBus eventBus;

    public AbstractClientFactoryImpl(EventBus eventBus) {
        this.eventBus = eventBus;
        this.placeController = new PlaceController(eventBus);
    }

    public PlaceController getDeprecatedPlaceController() {
        return placeController;
    }

    public NavigationViewFactory getNavigationViewFactory() {
        if (navigationViewFactory == null) {
            navigationViewFactory = new NavigationViewFactoryImpl(this,
                    eventBus);
        }
        return navigationViewFactory;
    }

    public ConfigurationServiceAsync getConfigurationService() {
        return GWT.create(ConfigurationService.class);
    }

    public MultiActivityManager getActivityManager() {
        return new MultiActivityManager(
                eventBus, this.getActivityMapper());
    }

    public ModuleServiceAsync getModuleService() {
        return GWT.create(ModuleService.class);
    }

    public RepositoryServiceAsync getService() {
        return GWT.create(RepositoryService.class);
    }

    public AssetEditorFactory getAssetEditorFactory() {
        if (assetEditorFactory == null) {
            assetEditorFactory = GWT.create(AssetEditorFactory.class);
        }
        return assetEditorFactory;
    }

    public PerspectiveFactory getPerspectiveFactory() {
        if (perspectiveFactory == null) {
            perspectiveFactory = GWT.create(PerspectiveFactory.class);
        }
        return perspectiveFactory;
    }

    public RepositoryServiceAsync getRepositoryService() {
        return GWT.create(RepositoryService.class);
    }

    public CategoryServiceAsync getCategoryService() {
        return GWT.create(CategoryService.class);
    }

    public AssetServiceAsync getAssetService() {
        return GWT.create(AssetService.class);
    }

    public SecurityServiceAsync getSecurityService() {
        return GWT.create(SecurityService.class);
    }
}
