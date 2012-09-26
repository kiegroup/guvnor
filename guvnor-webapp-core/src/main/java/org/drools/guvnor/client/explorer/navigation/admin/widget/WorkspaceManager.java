/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.explorer.navigation.admin.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.*;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceManager extends Composite {
    private ConstantsCore constants = GWT.create( ConstantsCore.class );
    private RepositoryServiceAsync repositoryService = GWT.create( RepositoryService.class );
    private ModuleServiceAsync moduleService = GWT.create( ModuleService.class );

    private ListBox       availableWorkspacesListBox;
    private ListBox       availableModulesListBox = new ListBox( true );
    private ListBox       selectedModulesListBox = new ListBox( true );

    public WorkspaceManager() {
        PrettyFormLayout form = new PrettyFormLayout();
        form.addHeader( GuvnorImages.INSTANCE.WorkspaceManager(),
                        new HTML( "<b>" + constants.ManageWorkspaces() + "</b>" ) );
        form.startSection( constants.Workspaces() );

        form.addAttribute("", buildDoubleList());

        HorizontalPanel hPanel = new HorizontalPanel();
        Button create = new Button( constants.AddWorkspace() );
        create.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                WorkspaceEditor newCat = new WorkspaceEditor( new Command() {
                    public void execute() {
                        refreshWorkspaceList();
                    }
                } );

                newCat.show();
            }
        } );

        Button remove = new Button( constants.DeleteSelectedWorkspace() );
        remove.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( availableWorkspacesListBox.getSelectedIndex() == -1 ) {
                    Window.alert( constants.PleaseSelectAWorkspaceToRemove() );
                    return;
                }
                removeWorkspace();
            }

        } );
        
        Button updateWorkspace = new Button(constants.UpdateSelectedWorkspace());
        updateWorkspace.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( availableWorkspacesListBox.getSelectedIndex() == -1 ) {
                    Window.alert( constants.PleaseSelectAWorkspaceToUpdate() );
                    return;
                }
                
                updateWorkspace();
            }

        } );
        hPanel.add( create );
        hPanel.add( remove );
        hPanel.add( updateWorkspace );

        form.addAttribute( "",
                           hPanel );

        form.endSection();
        initWidget( form );
    }

    private void removeWorkspace() {
        String name = availableWorkspacesListBox.getItemText( availableWorkspacesListBox.getSelectedIndex() );

        repositoryService.removeWorkspace(name,
                new GenericCallback<java.lang.Void>() {
                    public void onSuccess(Void v) {
                        Window.alert(constants.WorkspaceRemoved());
                        refreshWorkspaceList();
                    }
                });
    }
    
    private void updateWorkspace() {
        String name = availableWorkspacesListBox.getItemText( availableWorkspacesListBox.getSelectedIndex() );
        
        List<String> selectedModulesList = new ArrayList<String>( selectedModulesListBox.getItemCount() );
        for ( int i = 0; i < selectedModulesListBox.getItemCount(); i++ ) {
            selectedModulesList.add( selectedModulesListBox.getItemText( i ) );
        }
        List<String> availableModuleList = new ArrayList<String>( availableModulesListBox.getItemCount() );
        for ( int i = 0; i < availableModulesListBox.getItemCount(); i++ ) {
            availableModuleList.add( availableModulesListBox.getItemText( i ) );
        }
        availableModuleList.removeAll(selectedModulesList);
        LoadingPopup.showMessage( constants.LoadingStatuses() );
        
        repositoryService.updateWorkspace(name,
                selectedModulesList.toArray(new String[selectedModulesList.size()]),
                availableModuleList.toArray(new String[availableModuleList.size()]),
                new GenericCallback<java.lang.Void>() {
                    public void onSuccess(Void v) {
                        Window.alert(constants.WorkspaceUpdated());
                        refreshWorkspaceList();
                    }
                });
    }

    private void refreshWorkspaceList() {
        LoadingPopup.showMessage( constants.LoadingWorkspaces() );
        repositoryService.listWorkspaces(new GenericCallback<String[]>() {
            public void onSuccess(String[] workspaces) {
                availableWorkspacesListBox.clear();
                for (String workspace : workspaces) {
                    availableWorkspacesListBox.addItem(workspace);
                }
                LoadingPopup.close();
            }
        });
    }
    
    private void refreshModuleList(String selectedWorkspaceName) {
        if(selectedWorkspaceName == null || "".equals(selectedWorkspaceName)) {
            return;
        }
        
        LoadingPopup.showMessage( constants.LoadingWorkspaces() );
        moduleService.listModules(selectedWorkspaceName,  new GenericCallback<Module[]>() {
            public void onSuccess(Module[] packageConfigData) {
                selectedModulesListBox.clear();
                for ( Module p : packageConfigData) {
                    selectedModulesListBox.addItem( p.getName() );
                }
                LoadingPopup.close();
            }
        } );
        
        LoadingPopup.showMessage( constants.LoadingWorkspaces() );
        moduleService.listModules( new GenericCallback<Module[]>() {
            public void onSuccess(Module[] packageConfigData) {
                availableModulesListBox.clear();
                for ( Module p : packageConfigData) {
                    boolean isSelected = false;
                    for ( int i = 0; i < selectedModulesListBox.getItemCount(); i++ ) {
                        if(p.getName().equals(selectedModulesListBox.getItemText( i ))) {
                            isSelected = true;
                        }
                    }
                    if(!isSelected) {
                        availableModulesListBox.addItem( p.getName() );
                    }
                }
                LoadingPopup.close();
            }
        } );

    }
    
    private Grid buildDoubleList() {
        Grid grid = new Grid( 2,
                              5 );

        availableWorkspacesListBox = new ListBox();
        availableWorkspacesListBox.setVisibleItemCount( 10 );
        availableWorkspacesListBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent arg0) {
                String selectedWorkspaceName = availableWorkspacesListBox.getItemText( availableWorkspacesListBox.getSelectedIndex() );
                refreshModuleList(selectedWorkspaceName);
            }
        } );
        //availableWorkspaces.setWidth( "30%" );

        refreshWorkspaceList();


        try {
            availableModulesListBox.setVisibleItemCount( 10 );
            selectedModulesListBox.setVisibleItemCount( 10 );

            Grid btnsPanel = new Grid( 2,
                                       1 );

            btnsPanel.setWidget( 0,
                                 0,
                                 new Button( ">",
                                             new ClickHandler() {

                                                 public void onClick(ClickEvent sender) {
                                                     copySelected( availableModulesListBox,
                                                             selectedModulesListBox );
                                                 }
                                             } ) );

            btnsPanel.setWidget( 1,
                                 0,
                                 new Button( "&lt;",
                                             new ClickHandler() {

                                                 public void onClick(ClickEvent sender) {
                                                     copySelected( selectedModulesListBox,
                                                             availableModulesListBox );
                                                 }
                                             } ) );


            grid.setWidget( 0,
                            0,
                            new SmallLabel( constants.Workspaces() ) );
            grid.setWidget( 0,
                            2,
                            new SmallLabel( "Workspace: Available Modules" ) );
            grid.setWidget( 0,
                            3,
                            new SmallLabel( "" ) );
            grid.setWidget( 0,
                            4,
                            new SmallLabel( "Workspace: Selected Modules" ) );
            grid.setWidget( 1,
                            0,
                            availableWorkspacesListBox );
            grid.setWidget( 1,
                            2,
                            availableModulesListBox );
            grid.setWidget( 1,
                            3,
                            btnsPanel );
            grid.setWidget( 1,
                            4,
                            selectedModulesListBox );

            grid.getColumnFormatter().setWidth( 0,
            "25%" );
            grid.getColumnFormatter().setWidth( 1,
            "10%" );
            grid.getColumnFormatter().setWidth( 2,
                                                "25%" );
            grid.getColumnFormatter().setWidth( 3,
                                                "15%" );
            grid.getColumnFormatter().setWidth( 4,
                                                "25%" );
            return grid;
        } finally {
            //sce.setFilteringFacts( filteringFact );
        }
    }

    private void copySelected(final ListBox from,
                              final ListBox to) {
        int selected;
        while ( (selected = from.getSelectedIndex()) != -1 ) {
            to.addItem( from.getItemText( selected ) );
            from.removeItem( selected );
        }
    }

}
