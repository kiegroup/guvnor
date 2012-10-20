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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseHeaderView;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseHeaderViewImpl;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeView;
import org.drools.guvnor.client.explorer.navigation.browse.BrowseTreeViewImpl;
import org.drools.guvnor.client.explorer.navigation.modules.*;
import org.drools.guvnor.client.moduleeditor.AssetViewerActivityView;
import org.drools.guvnor.client.moduleeditor.AssetViewerActivityViewImpl;
import org.drools.guvnor.client.widgets.wizards.WizardActivityView;
import org.drools.guvnor.client.widgets.wizards.WizardActivityViewImpl;
import org.drools.guvnor.client.widgets.wizards.WizardContext;

public class NavigationViewFactoryImpl
        implements
        NavigationViewFactory {

    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    private NavigationPanelView navigationPanelView;
    private ModulesTreeViewImpl modulesTreeView;
    private BrowseTreeViewImpl browseTreeView;
    private ModulesTreeItemViewImpl modulesTreeItemView;

    public NavigationViewFactoryImpl(ClientFactory clientFactory,
                                     EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    public NavigationPanelView getNavigationPanelView() {
        if (navigationPanelView == null) {
            navigationPanelView = new NavigationPanelViewImpl();
        }
        return navigationPanelView;
    }

    public BrowseHeaderView getBrowseHeaderView() {
        return new BrowseHeaderViewImpl();
    }

    public BrowseTreeView getBrowseTreeView() {
        if (browseTreeView == null) {
            browseTreeView = new BrowseTreeViewImpl(clientFactory);
        }
        return browseTreeView;
    }

    public ModulesTreeView getModulesTreeView() {
        if (modulesTreeView == null) {
            modulesTreeView = new ModulesTreeViewImpl();
        }
        return modulesTreeView;
    }

    public IsWidget getModulesHeaderView(String perspectiveType) {
        return clientFactory.getPerspectiveFactory().getModulesHeaderView(perspectiveType);
    }

    public SafeHtml getModulesTreeRootNodeHeader(String perspectiveType) {
        return clientFactory.getPerspectiveFactory().getModulesTreeRootNodeHeader(perspectiveType);
    }

    public ModulesTreeItemView getModulesTreeItemView() {
        if (modulesTreeItemView == null) {
            modulesTreeItemView = new ModulesTreeItemViewImpl();
        }
        return modulesTreeItemView;
    }

    public GlobalAreaTreeItemView getGlobalAreaTreeItemView() {
        return new GlobalAreaTreeItemViewImpl();
    }

    public ModuleTreeItemView getModuleTreeItemView() {
        return new ModuleTreeItemViewImpl();
    }


    public Widget getModulesNewAssetMenu(String perspectiveType) {
        return clientFactory.getPerspectiveFactory().getModulesNewAssetMenu(perspectiveType,
                clientFactory,
                eventBus);
    }

    public WizardActivityView getWizardView(WizardContext context) {
        return new WizardActivityViewImpl(context,
                eventBus);
    }

    public AssetViewerActivityView getAssetViewerActivityView() {
        return new AssetViewerActivityViewImpl();
    }

}
