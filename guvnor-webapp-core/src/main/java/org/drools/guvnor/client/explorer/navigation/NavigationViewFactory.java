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

package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import org.drools.guvnor.client.explorer.ModuleEditorActivityView;
import org.drools.guvnor.client.explorer.MultiAssetView;
import org.drools.guvnor.client.explorer.navigation.admin.AdminTreeView;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseHeaderView;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeView;
import org.drools.guvnor.client.explorer.navigation.modules.GlobalAreaTreeItemView;
import org.drools.guvnor.client.explorer.navigation.modules.ModuleTreeItemView;
import org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeItemView;
import org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeView;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessesHeaderView;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessesTreeView;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingHeaderView;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingTreeView;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsHeaderView;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsTreeView;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksHeaderView;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksTreeView;
import org.drools.guvnor.client.moduleeditor.AssetViewerActivityView;
import org.drools.guvnor.client.perspective.PerspectivesPanelView;
import org.drools.guvnor.client.widgets.wizards.WizardActivityView;

public interface NavigationViewFactory {

    NavigationPanelView getNavigationPanelView();

    BrowseHeaderView getBrowseHeaderView();

    BrowseTreeView getBrowseTreeView();

    AdminTreeView getAdminTreeView();

    ModulesTreeView getModulesTreeView();

    IsWidget getModulesHeaderView(String perspectiveType);

    SafeHtml getModulesTreeRootNodeHeader(String perspectiveType);

    ModulesTreeItemView getModulesTreeItemView();

    GlobalAreaTreeItemView getGlobalAreaTreeItemView();

    ModuleTreeItemView getModuleTreeItemView();

    MultiAssetView getMultiAssetView();

    Widget getModulesNewAssetMenu(String perspectiveType);

    SettingsHeaderView getSettingsHeaderView();

    SettingsTreeView getSettingsTreeView();

    ProcessesHeaderView getProcessesHeaderView();

    ProcessesTreeView getProcessesTreeView();

    ReportingHeaderView getReportingHeaderView();

    ReportingTreeView getReportingTreeView();

    TasksHeaderView getTasksHeaderView();

    TasksTreeView getTasksTreeView();
    
    WizardActivityView getWizardView();
    
    ModuleEditorActivityView getModuleEditorActivityView();

    AssetViewerActivityView getAssetViewerActivityView();
        
    PerspectivesPanelView getPerspectivesPanelView();

}
