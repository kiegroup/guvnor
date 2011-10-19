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
package org.drools.guvnor.client.asseteditor.drools.springcontext;

import com.google.gwt.event.dom.client.ClickEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.PackageBuilderWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.Map;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;

public class SpringContextElementsBrowser extends Composite {

    private Constants constants = GWT.create(Constants.class);
    
    private Map<String,String> springContextElements;
    
    private SpringContextElementSelectedListener elementSelectedItem;
    
    private final VerticalPanel mainPanel;

    private class PanelButton extends Button {
        

        public PanelButton(String html, String pasteValue) {
            super(html);
            
            this.setWidth("100px");

            this.addClickHandler(new LeafClickHandler(html, pasteValue));

        }
    }
    
    private class LeafClickHandler implements ClickHandler{

        final private String title;
        final private String pasteValue;

        public LeafClickHandler(String title, String pasteValue) {
            this.title = title;
            this.pasteValue = pasteValue;
        }
        
        public void onClick(ClickEvent event) {
            if (elementSelectedItem != null){
                elementSelectedItem.onElementSelected(this.title, this.pasteValue);
            }
        }
        
    }

    public SpringContextElementsBrowser(SpringContextElementSelectedListener elementSelectedItem) {
        
        mainPanel = new VerticalPanel();
        
        this.elementSelectedItem = elementSelectedItem;

        //load Spring Context Element data from server
        RepositoryServiceFactory.getService().loadSpringContextElementData(new AsyncCallback<Map<String, String>>() {

            public void onFailure(Throwable caught) {
            }

            public void onSuccess(Map<String, String> result) {
                springContextElements = result;

                //now do the layout
                doLayout();
            }
        });
        
        initWidget(mainPanel);
    }

    private void doLayout(){
        
        mainPanel.add(new HTML("<b>Palette</b>"));
        
        for (Map.Entry<String,String> entry : springContextElements.entrySet()) {
            mainPanel.add(new PanelButton(entry.getKey(), entry.getValue()));
        }

        final Tree resourcesTree = new Tree();
        mainPanel.add(resourcesTree);

        final TreeItem rootItem = new TreeItem(constants.Packages());

        //Global Area Data
        RepositoryServiceFactory.getPackageService().loadGlobalPackage(new AsyncCallback<PackageConfigData>()   {

            public void onFailure(Throwable caught) {
                ErrorPopup.showMessage("Error listing Global Area information!");
            }

            public void onSuccess(PackageConfigData result) {
                populatePackageTree(result, rootItem);
            }
        });

        //Packages Data
        RepositoryServiceFactory.getPackageService().listPackages(new AsyncCallback<PackageConfigData[]>()    {

            public void onFailure(Throwable caught) {
                ErrorPopup.showMessage("Error listing package information!");
            }

            public void onSuccess(PackageConfigData[] result) {
                for (int i = 0; i < result.length; i++) {
                    final PackageConfigData packageConfigData = result[i];
                    populatePackageTree(packageConfigData, rootItem);
                }
            }
        });

        resourcesTree.addItem(rootItem);
        resourcesTree.setStyleName("category-explorer-Tree"); //NON-NLS
        resourcesTree.addSelectionHandler(new SelectionHandler<TreeItem>()    {

            public void onSelection(SelectionEvent<TreeItem> event) {
                Object o = event.getSelectedItem().getUserObject();
                if (o instanceof String) {
                }
            }
        });


        ScrollPanel scrollPanel = new ScrollPanel(resourcesTree);

        scrollPanel.setHeight("150px");
        scrollPanel.setWidth("130px");

        mainPanel.add(scrollPanel);

        mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        mainPanel.setSpacing(10);

    }
    
    private void populatePackageTree(final PackageConfigData packageConfigData, final TreeItem rootItem) {

        final String resourceElement = "<drools:resource type=\"PKG\" source=\"{url}\" basicAuthentication='enabled' username='|' password=''/>";
        
        final TreeItem packageItem = new TreeItem(packageConfigData.getName());

        TreeItem leafItem = new TreeItem(new ClickableLabel("LATEST", new LeafClickHandler(packageConfigData.getName(), resourceElement.replace("{url}", PackageBuilderWidget.getDownloadLink(packageConfigData)))));
        
        packageItem.addItem(leafItem);

        RepositoryServiceFactory.getPackageService().listSnapshots(packageConfigData.getName(), new AsyncCallback<SnapshotInfo[]>()    {

            public void onFailure(Throwable caught) {
                ErrorPopup.showMessage("Error listing snapshots information!");
            }

            public void onSuccess(SnapshotInfo[] result) {
                for (int j = 0; j < result.length; j++) {
                    final SnapshotInfo snapshotInfo = result[j];
                    RepositoryServiceFactory.getPackageService().loadPackageConfig( snapshotInfo.getUuid(), new AsyncCallback<PackageConfigData>()    {

                        public void onFailure(Throwable caught) {
                            ErrorPopup.showMessage("Error listing snapshots information!");
                        }

                        public void onSuccess(PackageConfigData result) {
                            TreeItem leafItem = new TreeItem(new ClickableLabel( snapshotInfo.getName(), new LeafClickHandler(packageConfigData.getName(), resourceElement.replace("{url}", PackageBuilderWidget.getDownloadLink(result)))));
                            packageItem.addItem(leafItem);
                        }
                    });

                }
            }
        });

        rootItem.addItem(packageItem);
    }
}