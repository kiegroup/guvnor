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
package org.drools.guvnor.client.asseteditor.drools.workitem;

import java.util.Map;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WorkitemDefinitionElementsBrowser extends Composite {
    private Constants constants = GWT.create(Constants.class);
    private Map<String, String> workitemDefinitionElements;
    private WorkitemDefinitionElementSelectedListener elementSelectedItem;
    private final VerticalPanel mainPanel;

    private class PanelButton extends Button {
        public PanelButton(String html, String pasteValue) {
            super(html);
            this.setWidth("100px");
            this.addClickHandler(new LeafClickHandler(html, pasteValue));
        }
    }

    private class LeafClickHandler implements ClickHandler {
        final private String title;
        final private String pasteValue;

        public LeafClickHandler(String title, String pasteValue) {
            this.title = title;
            this.pasteValue = pasteValue;
        }

        public void onClick(ClickEvent event) {
            if (elementSelectedItem != null) {
                elementSelectedItem.onElementSelected(this.title,
                        this.pasteValue);
            }
        }
    }

    public WorkitemDefinitionElementsBrowser(
            WorkitemDefinitionElementSelectedListener elementSelectedItem) {
        mainPanel = new VerticalPanel();
        this.elementSelectedItem = elementSelectedItem;

        // load Workitem Definition Element data from server
        RepositoryServiceFactory.getService()
                .loadWorkitemDefinitionElementData(
                        new AsyncCallback<Map<String, String>>() {
                            public void onFailure(Throwable caught) {
                            }

                            public void onSuccess(Map<String, String> result) {
                                workitemDefinitionElements = result;
                                // now do the layout
                                doLayout();
                            }
                        });

        initWidget(mainPanel);
    }

    private void doLayout() {
        mainPanel.add(new HTML("<b>Palette</b>"));

        for (Map.Entry<String, String> entry : workitemDefinitionElements.entrySet()) {
            mainPanel.add(new PanelButton(entry.getKey(), entry.getValue()));
        }
        
        final ListBox importsList = new ListBox();
        importsList.addItem( constants.ChooseImportClass() );
        importsList.addItem( "BooleanDataType", "import org.drools.process.core.datatype.impl.type.BooleanDataType;" );
        importsList.addItem( "EnumDataType", "import org.drools.process.core.datatype.impl.type.EnumDataType;" );
        importsList.addItem( "FloatDataType", "import org.drools.process.core.datatype.impl.type.FloatDataType;" );
        importsList.addItem( "IntegerDataType", "import org.drools.process.core.datatype.impl.type.IntegerDataType;" );
        importsList.addItem( "ListDataType", "import org.drools.process.core.datatype.impl.type.ListDataType;" );
        importsList.addItem( "ObjectDataType", "import org.drools.process.core.datatype.impl.type.ObjectDataType;" );
        importsList.addItem( "StringDataType", "import org.drools.process.core.datatype.impl.type.StringDataType;" );
        importsList.addItem( "UndefinedDataType", "import org.drools.process.core.datatype.impl.type.UndefinedDataType;" );
        
        importsList.setVisibleItemCount( 9 );
        importsList.setSelectedIndex( 0 );
        
        importsList.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                if (elementSelectedItem != null) {
                    elementSelectedItem.onElementSelected( importsList.getItemText( importsList.getSelectedIndex() ),
                            importsList.getValue( importsList.getSelectedIndex() ));
                }
            }
        } );
        
        mainPanel.add(importsList);
        
        final ListBox imagesList = new ListBox();
        imagesList.addItem( constants.ChooseIcon() );
        imagesList.setVisibleItemCount( 1 );
        imagesList.setSelectedIndex( 0 );
        
        imagesList.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                if (elementSelectedItem != null) {
                    elementSelectedItem.onElementSelected( imagesList.getItemText( imagesList.getSelectedIndex() ),
                            imagesList.getValue( imagesList.getSelectedIndex() ));
                }
            }
        } );
        
        mainPanel.add(imagesList);

        // Global Area Images
        RepositoryServiceFactory.getPackageService().loadGlobalModule(
                new AsyncCallback<Module>() {
                    public void onFailure(Throwable caught) {
                        ErrorPopup
                                .showMessage("Error listing Global Area information!");
                    }
                    public void onSuccess(Module result) {
                        final Module presults = result;
                        
                        RepositoryServiceFactory.getPackageService().listImagesInModule(
                                result.getName(), new AsyncCallback<String[]>() {

                                    public void onFailure(Throwable caught) {
                                        ErrorPopup
                                                .showMessage("Error listing images information!");
                                    }
                                    public void onSuccess(String[] images) {
                                        for (int i = 0; i < images.length; i++) {
                                            imagesList.addItem(presults.getName() + " : " + images[i], 
                                                    "http://localhost:8080/drools-guvnor/rest/packages/" + 
                                                    presults.getName() + "/assets/" + images[i] + "/binary");
                                        }
                                    }
                                });
                    }
                });

        // Images in Packages
        RepositoryServiceFactory.getPackageService().listModules(
                new AsyncCallback<Module[]>() {

                    public void onFailure(Throwable caught) {
                        ErrorPopup
                                .showMessage("Error listing images information!");
                    }
                    public void onSuccess(Module[] result) {
                        for (int i = 0; i < result.length; i++) {
                            final Module packageConfigData = result[i];
                            
                            RepositoryServiceFactory.getPackageService().listImagesInModule(
                                    packageConfigData.getName(), new AsyncCallback<String[]>() {

                                        public void onFailure(Throwable caught) {
                                            ErrorPopup
                                                    .showMessage("Error listing images information!");
                                        }
                                        public void onSuccess(String[] images) {
                                            for (int i = 0; i < images.length; i++) {
                                                imagesList.addItem(packageConfigData.getName() + " : " + images[i], 
                                                        "http://localhost:8080/drools-guvnor/rest/packages/" + 
                                                        packageConfigData.getName() + "/assets/" + images[i] + "/binary");
                                            }
                                        }
                                    });
                        }
                    }
                });

        mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        mainPanel.setSpacing(10);

    }
}
