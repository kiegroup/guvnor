/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.decisiontable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.widgets.drools.workitems.WorkItemParametersWidget;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A popup to define an Action to execute a Work Item
 */
public class ActionWorkItemPopup extends FormStylePopup {

    private static Constants                    constants                = GWT.create( Constants.class );

    private ActionWorkItemCol52                 editingCol;
    private String                              packageUUID;
    private GuidedDecisionTable52               model;
    private ClientFactory                       clientFactory;
    private WorkItemParametersWidget            workItemInputParameters  = new WorkItemParametersWidget();
    private WorkItemParametersWidget            workItemOutputParameters = new WorkItemParametersWidget();
    private int                                 workItemInputParametersIndex;
    private int                                 workItemOutputParametersIndex;
    private Map<String, PortableWorkDefinition> workItemDefinitions;

    public ActionWorkItemPopup(final ClientFactory clientFactory,
                               final String packageUUID,
                               final GuidedDecisionTable52 model,
                               final GenericColumnCommand refreshGrid,
                               final ActionWorkItemCol52 col,
                               final boolean isNew) {
        this.editingCol = cloneActionWorkItemColumn( col );
        this.clientFactory = clientFactory;
        this.packageUUID = packageUUID;
        this.model = model;

        setTitle( constants.ColumnConfigurationWorkItem() );
        setModal( false );

        //Column header
        final TextBox header = new TextBox();
        header.setText( col.getHeader() );
        header.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                editingCol.setHeader( header.getText() );
            }
        } );
        addAttribute( constants.ColumnHeaderDescription(),
                      header );

        //Work Item Definitions
        final ListBox workItemsListBox = new ListBox();
        addAttribute( constants.WorkItemNameColon(),
                      workItemsListBox );
        setupWorkItems( workItemsListBox );
        workItemsListBox.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                int index = workItemsListBox.getSelectedIndex();
                if ( index > -1 ) {
                    String selectedWorkItemName = workItemsListBox.getValue( index );
                    editingCol.setWorkItemDefinition( workItemDefinitions.get( selectedWorkItemName ) );
                    showWorkItemParameters();
                    center();
                }
            }

        } );

        //Work Item Input Parameters
        workItemInputParametersIndex = addAttribute( constants.WorkItemInputParameters(),
                                                     workItemInputParameters,
                                                     false );

        //Work Item Output Parameters
        workItemOutputParametersIndex = addAttribute( constants.WorkItemOutputParameters(),
                                                      workItemOutputParameters,
                                                      false );

        //Hide column tick-box
        addAttribute( constants.HideThisColumn(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        Button apply = new Button( constants.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( null == editingCol.getHeader()
                        || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( constants.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( isNew ) {
                    if ( !unique( editingCol.getHeader() ) ) {
                        Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                        return;
                    }

                } else {
                    if ( !col.getHeader().equals( editingCol.getHeader() ) ) {
                        if ( !unique( editingCol.getHeader() ) ) {
                            Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                            return;
                        }
                    }
                }

                // Pass new\modified column back for handling
                refreshGrid.execute( editingCol );
                hide();
            }
        } );
        addAttribute( "",
                      apply );

    }

    private boolean unique(String header) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) return false;
        }
        return true;
    }

    private ActionWorkItemCol52 cloneActionWorkItemColumn(ActionWorkItemCol52 col) {
        ActionWorkItemCol52 clone = new ActionWorkItemCol52();
        clone.setHeader( col.getHeader() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setWorkItemDefinition( cloneWorkItemDefinition( col.getWorkItemDefinition() ) );
        return clone;
    }

    private PortableWorkDefinition cloneWorkItemDefinition(PortableWorkDefinition pwd) {
        PortableWorkDefinition clone = new PortableWorkDefinition();
        clone.setName( pwd.getName() );
        clone.setDisplayName( pwd.getDisplayName() );
        return clone;
    }

    private void setupWorkItems(final ListBox workItemsListBox) {
        workItemsListBox.clear();
        workItemsListBox.addItem( constants.NoWorkItemsAvailable() );
        workItemsListBox.setEnabled( false );
        clientFactory.getService().loadWorkItemDefinitions( packageUUID,
                                                            new GenericCallback<List<PortableWorkDefinition>>() {

                                                                public void onSuccess(List<PortableWorkDefinition> result) {

                                                                    //Add list of Work Item Definitions to list box
                                                                    if ( result.size() > 0 ) {
                                                                        workItemsListBox.clear();
                                                                        workItemsListBox.setEnabled( true );
                                                                        workItemsListBox.addItem( constants.pleaseChoose(),
                                                                                                  "" );
                                                                        workItemDefinitions = new HashMap<String, PortableWorkDefinition>();

                                                                        String selectedName = null;
                                                                        boolean isWorkItemSelected = false;
                                                                        if ( editingCol.getWorkItemDefinition() != null ) {
                                                                            selectedName = editingCol.getWorkItemDefinition().getName();
                                                                        }

                                                                        //Add items
                                                                        for ( int i = 0; i < result.size(); i++ ) {
                                                                            PortableWorkDefinition wid = result.get( i );
                                                                            workItemsListBox.addItem( wid.getDisplayName(),
                                                                                                      wid.getName() );
                                                                            workItemDefinitions.put( wid.getName(),
                                                                                                     wid );
                                                                            if ( wid.getName().equals( selectedName ) ) {
                                                                                workItemsListBox.setSelectedIndex( i + 1 );
                                                                                isWorkItemSelected = true;
                                                                            }
                                                                        }

                                                                        //Show parameters if a Work Item is pre-selected
                                                                        setAttributeVisibility( workItemInputParametersIndex,
                                                                                                isWorkItemSelected );
                                                                        setAttributeVisibility( workItemOutputParametersIndex,
                                                                                                isWorkItemSelected );
                                                                        showWorkItemParameters();
                                                                    }
                                                                }

                                                            } );

    }

    private void showWorkItemParameters() {

        //Hide parameter selections if a Work Item has not been selected
        if ( editingCol.getWorkItemDefinition() == null ) {
            this.setAttributeVisibility( workItemInputParametersIndex,
                                         false );
            this.setAttributeVisibility( workItemOutputParametersIndex,
                                         false );
            return;
        }
        String selectedWorkItemName = editingCol.getWorkItemDefinition().getName();
        PortableWorkDefinition wid = workItemDefinitions.get( selectedWorkItemName );
        if ( wid == null ) {
            this.setAttributeVisibility( workItemInputParametersIndex,
                                         false );
            this.setAttributeVisibility( workItemOutputParametersIndex,
                                         false );
            return;
        }

        //Show parameters
        this.setAttributeVisibility( workItemInputParametersIndex,
                                     true );
        this.setAttributeVisibility( workItemOutputParametersIndex,
                                     true );

        //Input parameters
        workItemInputParameters.setParameters( wid.getParameters() );

        //Output parameters
        workItemOutputParameters.setParameters( wid.getResults() );

    }

}
