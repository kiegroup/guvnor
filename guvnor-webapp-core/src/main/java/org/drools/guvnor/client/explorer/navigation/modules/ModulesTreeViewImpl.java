/*
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

package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ModulesTreeViewImpl extends Composite
    implements
    ModulesTreeView {

    interface ModulesTreeViewImplBinder
            extends
            UiBinder<Widget, ModulesTreeViewImpl> {
    }

    private Presenter                                 presenter;

    private static ModulesTreeViewImplBinder uiBinder = GWT.create( ModulesTreeViewImplBinder.class );

    @UiField
    SimplePanel                                       menuContainer;

    @UiField
    Image                                             imgFlatView;

    @UiField
    Image                                             imgHierarchicalView;

    @UiField
    Image                                             imgExpandAll;

    @UiField
    Image                                             imgCollapseAll;

    @UiField
    SimplePanel                                       modulesTreeContainer;

    @UiField
    SimplePanel                                       globalModulesTreeContainer;

    public ModulesTreeViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setNewAssetMenu(Widget modulesNewAssetMenu) {
        menuContainer.setWidget( modulesNewAssetMenu );
    }

    public void setGlobalAreaTreeItem(GlobalAreaTreeItem globalAreaTreeItem) {
        globalModulesTreeContainer.setWidget( globalAreaTreeItem.asWidget() );
    }

    public void setModulesTreeItem(ModulesTreeItem knowledgeModulesTreeItem) {
        modulesTreeContainer.setWidget( knowledgeModulesTreeItem );
    }

    @UiHandler("imgHierarchicalView")
    public void doOnClickHierarchyView(ClickEvent event) {
        presenter.setHierarchyView();
    }

    @UiHandler("imgFlatView")
    public void doOnClickFlatView(ClickEvent event) {
        presenter.setFlatView();
    }

    @UiHandler("imgCollapseAll")
    public void doOnClickCollapseAll(ClickEvent event) {
        presenter.collapseAll();
    }

    @UiHandler("imgExpandAll")
    public void doOnClickExpandAll(ClickEvent event) {
        presenter.expandAll();
    }

}
