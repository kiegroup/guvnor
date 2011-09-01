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
import org.drools.guvnor.client.explorer.perspectives.PerspectivesPanelView;
import org.drools.guvnor.client.packages.AbstractModuleEditor;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PackageServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.util.ActivityMapper;
import org.drools.guvnor.client.widgets.assetviewer.AssetViewerActivityView;
import org.drools.guvnor.client.widgets.wizards.WizardFactory;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Command;

public interface ClientFactory {

    PlaceController getPlaceController();

    PerspectivesPanelView getPerspectivesPanelView();

    NavigationViewFactory getNavigationViewFactory();

    ConfigurationServiceAsync getConfigurationService();

    MultiActivityManager getActivityManager();

    PlaceHistoryHandler getPlaceHistoryHandler();

    PlaceHistoryMapper getPlaceHistoryMapper();

    ModuleEditorActivityView getModuleEditorActivityView();

    AssetViewerActivityView getAssetViewerActivityView();

    PackageServiceAsync getPackageService();

    AssetEditorFactory getAssetEditorFactory();

    RepositoryServiceAsync getRepositoryService();

    CategoryServiceAsync getCategoryService();

    AssetServiceAsync getAssetService();

    ActivityMapper getActivityMapper();
    
    AbstractModuleEditor getModuleEditor(PackageConfigData packageConfigData, ClientFactory clientFactory, EventBus eventBus, boolean historicalReadOnly, Command refreshCommand);
    
    WizardFactory getWizardFactory();
    
}
