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

import org.drools.guvnor.client.asseteditor.RefreshAssetEditorEvent;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.ConfigurationServiceAsync;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.util.ActivityMapper;
import org.drools.guvnor.client.widgets.wizards.WizardContext;
import org.drools.guvnor.client.widgets.wizards.WizardFactory;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.security.Identity;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Map;

public interface ClientFactory {

    PlaceController getDeprecatedPlaceController();

    PlaceManager getPlaceManager();

    MultiActivityManager getActivityManager();

    ActivityMapper getActivityMapper();

    NavigationViewFactory getNavigationViewFactory();

    AssetEditorFactory getAssetEditorFactory();

    PerspectiveFactory getPerspectiveFactory();

    WizardFactory getWizardFactory();

    ConfigurationServiceAsync getConfigurationService();

    ModuleServiceAsync getModuleService();

    RepositoryServiceAsync getService();

    RepositoryServiceAsync getRepositoryService();

    CategoryServiceAsync getCategoryService();

    AssetServiceAsync getAssetService();

    Event<RefreshAssetEditorEvent> getRefreshAssetEditorEvents();

    Event<NotificationEvent> getNotificationEvents();

    WizardContext makeContext(Map<String, String> context);

    Identity getIdentity();
}
