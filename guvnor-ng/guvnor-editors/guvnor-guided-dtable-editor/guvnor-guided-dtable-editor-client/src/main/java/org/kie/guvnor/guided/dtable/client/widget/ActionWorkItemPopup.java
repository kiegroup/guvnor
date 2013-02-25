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
package org.kie.guvnor.guided.dtable.client.widget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.workitems.IBindingProvider;
import org.kie.guvnor.commons.ui.client.workitems.WorkItemParametersWidget;
import org.drools.guvnor.models.commons.shared.workitems.PortableBooleanParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableEnumParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableFloatParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableIntegerParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableListParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableObjectParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableStringParameterDefinition;
import org.drools.guvnor.models.commons.shared.workitems.PortableWorkDefinition;
import org.kie.guvnor.guided.dtable.client.resources.i18n.Constants;
import org.drools.guvnor.models.guided.dtable.shared.model.ActionCol52;
import org.drools.guvnor.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.guvnor.guided.dtable.service.GuidedDecisionTableEditorService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.FormStylePopup;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A popup to define an Action to execute a Work Item
 */
public class ActionWorkItemPopup extends FormStylePopup {

    private ActionWorkItemCol52 editingCol;
    private GuidedDecisionTable52 model;
    private Path path;
    private WorkItemParametersWidget workItemInputParameters;
    private int workItemInputParametersIndex;
    private Map<String, PortableWorkDefinition> workItemDefinitions;

    private final boolean isReadOnly;

    @Inject
    private Caller<GuidedDecisionTableEditorService> dtableService;

    public ActionWorkItemPopup( final Path path,
                                final GuidedDecisionTable52 model,
                                final IBindingProvider bindingProvider,
                                final GenericColumnCommand refreshGrid,
                                final ActionWorkItemCol52 col,
                                final boolean isNew,
                                final boolean isReadOnly ) {
        this.editingCol = cloneActionWorkItemColumn( col );
        this.path = path;
        this.model = model;
        this.isReadOnly = isReadOnly;

        this.workItemInputParameters = new WorkItemParametersWidget( bindingProvider,
                                                                     isReadOnly );

        setTitle( Constants.INSTANCE.ColumnConfigurationWorkItem() );
        setModal( false );

        //Column header
        final TextBox header = new TextBox();
        header.setText( col.getHeader() );
        header.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            header.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    editingCol.setHeader( header.getText() );
                }
            } );
        }
        addAttribute( Constants.INSTANCE.ColumnHeaderDescription(),
                      header );

        //Work Item Definitions
        final ListBox workItemsListBox = new ListBox();
        addAttribute( Constants.INSTANCE.WorkItemNameColon(),
                      workItemsListBox );
        setupWorkItems( workItemsListBox );
        workItemsListBox.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            workItemsListBox.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    int index = workItemsListBox.getSelectedIndex();
                    if ( index >= 0 ) {
                        String selectedWorkItemName = workItemsListBox.getValue( index );
                        editingCol.setWorkItemDefinition( workItemDefinitions.get( selectedWorkItemName ) );
                        showWorkItemParameters();
                        center();
                    }
                }

            } );
        }

        //Work Item Input Parameters
        workItemInputParametersIndex = addAttribute( Constants.INSTANCE.WorkItemInputParameters(),
                                                     workItemInputParameters,
                                                     false );

        //Hide column tick-box
        addAttribute( Constants.INSTANCE.HideThisColumn(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        Button apply = new Button( Constants.INSTANCE.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                if ( null == editingCol.getHeader()
                        || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( Constants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( isNew ) {
                    if ( !unique( editingCol.getHeader() ) ) {
                        Window.alert( Constants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                        return;
                    }

                } else {
                    if ( !col.getHeader().equals( editingCol.getHeader() ) ) {
                        if ( !unique( editingCol.getHeader() ) ) {
                            Window.alert( Constants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
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

    private boolean unique( String header ) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) {
                return false;
            }
        }
        return true;
    }

    private ActionWorkItemCol52 cloneActionWorkItemColumn( ActionWorkItemCol52 col ) {
        ActionWorkItemCol52 clone = new ActionWorkItemCol52();
        clone.setHeader( col.getHeader() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setWorkItemDefinition( cloneWorkItemDefinition( col.getWorkItemDefinition() ) );
        return clone;
    }

    private PortableWorkDefinition cloneWorkItemDefinition( PortableWorkDefinition pwd ) {
        if ( pwd == null ) {
            return null;
        }
        PortableWorkDefinition clone = new PortableWorkDefinition();
        clone.setName( pwd.getName() );
        clone.setDisplayName( pwd.getDisplayName() );
        clone.setParameters( cloneParameters( pwd.getParameters() ) );
        clone.setResults( cloneParameters( pwd.getResults() ) );
        return clone;
    }

    private Set<PortableParameterDefinition> cloneParameters( Collection<PortableParameterDefinition> parameters ) {
        Set<PortableParameterDefinition> clone = new HashSet<PortableParameterDefinition>();
        for ( PortableParameterDefinition ppd : parameters ) {
            clone.add( cloneParameter( ppd ) );
        }
        return clone;
    }

    private PortableParameterDefinition cloneParameter( PortableParameterDefinition ppd ) {
        PortableParameterDefinition clone = null;
        if ( ppd instanceof PortableBooleanParameterDefinition ) {
            clone = new PortableBooleanParameterDefinition();
            clone.setName( ppd.getName() );
            ( (PortableBooleanParameterDefinition) clone ).setBinding( ( (PortableBooleanParameterDefinition) ppd ).getBinding() );
            ( (PortableBooleanParameterDefinition) clone ).setValue( ( (PortableBooleanParameterDefinition) ppd ).getValue() );
            return clone;
        } else if ( ppd instanceof PortableEnumParameterDefinition ) {
            clone = new PortableEnumParameterDefinition();
            clone.setName( ppd.getName() );
            ( (PortableEnumParameterDefinition) clone ).setClassName( ( (PortableEnumParameterDefinition) ppd ).getClassName() );
            ( (PortableEnumParameterDefinition) clone ).setBinding( ( (PortableEnumParameterDefinition) ppd ).getBinding() );
            ( (PortableEnumParameterDefinition) clone ).setValues( ( (PortableEnumParameterDefinition) ppd ).getValues() );
            ( (PortableEnumParameterDefinition) clone ).setValue( ( (PortableEnumParameterDefinition) ppd ).getValue() );
            return clone;
        } else if ( ppd instanceof PortableFloatParameterDefinition ) {
            clone = new PortableFloatParameterDefinition();
            clone.setName( ppd.getName() );
            ( (PortableFloatParameterDefinition) clone ).setBinding( ( (PortableFloatParameterDefinition) ppd ).getBinding() );
            ( (PortableFloatParameterDefinition) clone ).setValue( ( (PortableFloatParameterDefinition) ppd ).getValue() );
            return clone;
        } else if ( ppd instanceof PortableIntegerParameterDefinition ) {
            clone = new PortableIntegerParameterDefinition();
            clone.setName( ppd.getName() );
            ( (PortableIntegerParameterDefinition) clone ).setBinding( ( (PortableIntegerParameterDefinition) ppd ).getBinding() );
            ( (PortableIntegerParameterDefinition) clone ).setValue( ( (PortableIntegerParameterDefinition) ppd ).getValue() );
            return clone;
        } else if ( ppd instanceof PortableListParameterDefinition ) {
            clone = new PortableListParameterDefinition();
            clone.setName( ppd.getName() );
            ( (PortableListParameterDefinition) clone ).setBinding( ( (PortableListParameterDefinition) ppd ).getBinding() );
            ( (PortableListParameterDefinition) clone ).setClassName( ( (PortableListParameterDefinition) ppd ).getClassName() );
            return clone;
        } else if ( ppd instanceof PortableObjectParameterDefinition ) {
            clone = new PortableObjectParameterDefinition();
            clone.setName( ppd.getName() );
            ( (PortableObjectParameterDefinition) clone ).setBinding( ( (PortableObjectParameterDefinition) ppd ).getBinding() );
            ( (PortableObjectParameterDefinition) clone ).setClassName( ( (PortableObjectParameterDefinition) ppd ).getClassName() );
            return clone;
        } else if ( ppd instanceof PortableStringParameterDefinition ) {
            clone = new PortableStringParameterDefinition();
            clone.setName( ppd.getName() );
            ( (PortableStringParameterDefinition) clone ).setBinding( ( (PortableStringParameterDefinition) ppd ).getBinding() );
            ( (PortableStringParameterDefinition) clone ).setValue( ( (PortableStringParameterDefinition) ppd ).getValue() );
            return clone;
        }
        throw new IllegalArgumentException( "Unrecognized PortableParameterDefinition" );
    }

    private void setupWorkItems( final ListBox workItemsListBox ) {
        workItemsListBox.clear();
        workItemsListBox.addItem( Constants.INSTANCE.NoWorkItemsAvailable() );
        workItemsListBox.setEnabled( false );

        dtableService.call( new RemoteCallback<Set<PortableWorkDefinition>>() {
            @Override
            public void callback( final Set<PortableWorkDefinition> result ) {

                //Add list of Work Item Definitions to list box
                if ( result.size() > 0 ) {
                    workItemsListBox.clear();
                    workItemsListBox.setEnabled( true && !isReadOnly );
                    workItemsListBox.addItem( Constants.INSTANCE.Choose(),
                                              "" );
                    workItemDefinitions = new HashMap<String, PortableWorkDefinition>();

                    String selectedName = null;
                    boolean isWorkItemSelected = false;
                    if ( editingCol.getWorkItemDefinition() != null ) {
                        selectedName = editingCol.getWorkItemDefinition().getName();
                    }

                    //Add items
                    int i = 0;
                    for ( PortableWorkDefinition wid : result ) {
                        workItemsListBox.addItem( wid.getDisplayName(),
                                                  wid.getName() );
                        workItemDefinitions.put( wid.getName(),
                                                 wid );
                        if ( wid.getName().equals( selectedName ) ) {
                            workItemsListBox.setSelectedIndex( i + 1 );
                            isWorkItemSelected = true;
                        }
                        i++;
                    }

                    //Show parameters if a Work Item is pre-selected
                    setAttributeVisibility( workItemInputParametersIndex,
                                            isWorkItemSelected );
                    showWorkItemParameters();
                    center();
                }
            }
        } ).loadWorkItemDefinitions( this.path );

    }

    private void showWorkItemParameters() {

        //Hide parameter selections if a Work Item has not been selected
        PortableWorkDefinition wid = editingCol.getWorkItemDefinition();
        if ( wid == null ) {
            this.setAttributeVisibility( workItemInputParametersIndex,
                                         false );
            return;
        }

        //Show parameters
        this.setAttributeVisibility( workItemInputParametersIndex,
                                     true );

        //Input parameters
        workItemInputParameters.setParameters( wid.getParameters() );

    }

}
