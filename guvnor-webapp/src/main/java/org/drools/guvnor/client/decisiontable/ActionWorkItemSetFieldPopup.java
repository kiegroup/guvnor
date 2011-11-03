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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.shared.workitems.PortableParameterDefinition;
import org.drools.ide.common.shared.workitems.PortableWorkDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * A popup to define an Action to set a field on an existing Fact to the value
 * of a Work Item Result parameter
 */
public class ActionWorkItemSetFieldPopup extends FormStylePopup {

    private static Constants               constants                   = GWT.create( Constants.class );
    private static Images                  images                      = (Images) GWT.create( Images.class );

    private SmallLabel                     bindingLabel                = new SmallLabel();
    private TextBox                        fieldLabel                  = getFieldLabel();
    private ListBox                        workItemResultParameters    = new ListBox();
    private Map<String, WorkItemParameter> workItemResultParametersMap = new HashMap<String, WorkItemParameter>();

    private ActionWorkItemSetFieldCol52    editingCol;
    private GuidedDecisionTable52          model;
    private SuggestionCompletionEngine     sce;

    //Container to contain WorkItem and WorkItem Parameters associations
    private static class WorkItemParameter {

        WorkItemParameter(PortableWorkDefinition workDefinition,
                          PortableParameterDefinition workParameterDefinition) {
            this.workDefinition = workDefinition;
            this.workParameterDefinition = workParameterDefinition;
        }

        PortableWorkDefinition      workDefinition;
        PortableParameterDefinition workParameterDefinition;
    }

    public ActionWorkItemSetFieldPopup(final SuggestionCompletionEngine sce,
                                       final GuidedDecisionTable52 model,
                                       final GenericColumnCommand refreshGrid,
                                       final ActionWorkItemSetFieldCol52 col,
                                       final boolean isNew) {
        this.editingCol = cloneActionSetColumn( col );
        this.model = model;
        this.sce = sce;

        setTitle( constants.ColumnConfigurationWorkItemSetField() );
        setModal( false );

        //Fact on which field will be set
        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( bindingLabel );
        doBindingLabel();

        Image changePattern = new ImageButton( images.edit(),
                                               constants.ChooseABoundFactThatThisColumnPertainsTo(),
                                               new ClickHandler() {
                                                   public void onClick(ClickEvent w) {
                                                       showChangeFact( w );
                                                   }
                                               } );
        pattern.add( changePattern );
        addAttribute( constants.Fact(),
                      pattern );

        //Fact Field being set
        HorizontalPanel field = new HorizontalPanel();
        field.add( fieldLabel );
        Image editField = new ImageButton( images.edit(),
                                           constants.EditTheFieldThatThisColumnOperatesOn(),
                                           new ClickHandler() {
                                               public void onClick(ClickEvent w) {
                                                   showFieldChange();
                                               }
                                           } );
        field.add( editField );
        addAttribute( constants.Field(),
                      field );
        doFieldLabel();

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

        //Update Engine with changes
        addAttribute( constants.UpdateEngineWithChanges(),
                      doUpdate() );

        //Bind field to a WorkItem result parameter
        addAttribute( constants.BindActionFieldToWorkItem(),
                      doBindFieldToWorkItem() );
        workItemResultParameters.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                int index = workItemResultParameters.getSelectedIndex();
                if ( index >= 0 ) {
                    String key = workItemResultParameters.getValue( index );
                    WorkItemParameter wip = workItemResultParametersMap.get( key );
                    editingCol.setWorkItemName( wip.workDefinition.getName() );
                    editingCol.setWorkItemResultParameterName( wip.workParameterDefinition.getName() );
                    editingCol.setParameterClassName( wip.workParameterDefinition.getClassName() );
                }
            }

        } );

        //Hide column tick-box
        addAttribute( constants.HideThisColumn(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        Button apply = new Button( constants.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
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

    private ActionWorkItemSetFieldCol52 cloneActionSetColumn(ActionWorkItemSetFieldCol52 col) {
        ActionWorkItemSetFieldCol52 clone = new ActionWorkItemSetFieldCol52();
        clone.setBoundName( col.getBoundName() );
        clone.setFactField( col.getFactField() );
        clone.setHeader( col.getHeader() );
        clone.setType( col.getType() );
        clone.setValueList( col.getValueList() );
        clone.setUpdate( col.isUpdate() );
        clone.setDefaultValue( col.getDefaultValue() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setWorkItemName( col.getWorkItemName() );
        clone.setWorkItemResultParameterName( col.getWorkItemResultParameterName() );
        clone.setParameterClassName( col.getParameterClassName() );
        return clone;
    }

    private void doBindingLabel() {
        if ( this.editingCol.getBoundName() != null ) {
            this.bindingLabel.setText( "" + this.editingCol.getBoundName() );
        } else {
            this.bindingLabel.setText( constants.pleaseChooseABoundFactForThisColumn() );
        }
    }

    private void doFieldLabel() {
        if ( this.editingCol.getFactField() != null ) {
            this.fieldLabel.setText( this.editingCol.getFactField() );
        } else {
            this.fieldLabel.setText( constants.pleaseChooseAFactPatternFirst() );
        }
    }

    private Widget doUpdate() {
        HorizontalPanel hp = new HorizontalPanel();

        final CheckBox cb = new CheckBox();
        cb.setValue( editingCol.isUpdate() );
        cb.setText( "" );
        cb.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if ( sce.isGlobalVariable( editingCol.getBoundName() ) ) {
                    cb.setEnabled( false );
                    editingCol.setUpdate( false );
                } else {
                    editingCol.setUpdate( cb.getValue() );
                }
            }
        } );
        hp.add( cb );
        hp.add( new InfoPopup( constants.UpdateFact(),
                               constants.UpdateDescription() ) );
        return hp;
    }

    //Populate list of WorkItem Result Parameters for the Fact\Fields data-type
    private Widget doBindFieldToWorkItem() {
        workItemResultParameters.clear();
        workItemResultParametersMap.clear();

        //Get list of Work Items executed by Actions
        List<PortableWorkDefinition> actionWorkItems = new ArrayList<PortableWorkDefinition>();
        for ( ActionCol52 ac : model.getActionCols() ) {
            if ( ac instanceof ActionWorkItemCol52 ) {
                PortableWorkDefinition pwd = ((ActionWorkItemCol52) ac).getWorkItemDefinition();
                actionWorkItems.add( pwd );
            }
        }

        //Populate list of available result parameters
        if ( actionWorkItems.size() == 0 ) {
            workItemResultParameters.setEnabled( false );
            workItemResultParameters.addItem( constants.NoWorkItemsAvailable() );
            editingCol.setWorkItemName( null );
            editingCol.setWorkItemResultParameterName( null );
            editingCol.setParameterClassName( null );
        } else {
            int selectedItemIndex = -1;
            String selectedItemKey = editingCol.getWorkItemName() + "." + editingCol.getWorkItemResultParameterName();
            workItemResultParameters.setEnabled( true );
            for ( PortableWorkDefinition pwd : actionWorkItems ) {
                for ( PortableParameterDefinition ppd : pwd.getResults() ) {
                    if ( acceptParameterType( ppd ) ) {
                        String key = pwd.getName() + "." + ppd.getName();
                        String parameterDisplayName = pwd.getDisplayName() + "." + ppd.getName();

                        //Pre-select item if applicable
                        if ( key.equals( selectedItemKey ) ) {
                            selectedItemIndex = workItemResultParameters.getItemCount();
                        }
                        workItemResultParametersMap.put( key,
                                                         new WorkItemParameter( pwd,
                                                                                ppd ) );
                        workItemResultParameters.addItem( parameterDisplayName,
                                                          key );
                    }
                }
            }

            //Disable selection if no suitable parameters were found
            if ( workItemResultParameters.getItemCount() == 0 ) {
                workItemResultParameters.setEnabled( false );
                workItemResultParameters.addItem( constants.NoWorkItemsAvailable() );
                editingCol.setWorkItemName( null );
                editingCol.setWorkItemResultParameterName( null );
                editingCol.setParameterClassName( null );
            } else {

                //Select first item if none were pre-selected
                if ( selectedItemIndex == -1 ) {
                    selectedItemIndex = 0;
                    selectedItemKey = workItemResultParameters.getValue( selectedItemIndex );
                    WorkItemParameter wip = workItemResultParametersMap.get( selectedItemKey );
                    editingCol.setWorkItemName( wip.workDefinition.getName() );
                    editingCol.setWorkItemResultParameterName( wip.workParameterDefinition.getName() );
                    editingCol.setParameterClassName( wip.workParameterDefinition.getClassName() );
                }
                workItemResultParameters.setSelectedIndex( selectedItemIndex );
            }
        }

        return workItemResultParameters;
    }

    private boolean acceptParameterType(PortableParameterDefinition ppd) {
        if ( nil( editingCol.getFactField() ) ) {
            return false;
        }
        if ( ppd.getClassName() == null ) {
            return false;
        }
        Pattern52 p = model.getConditionPattern( editingCol.getBoundName() );
        String fieldClassName = sce.getFieldClassName( p.getFactType(),
                                                       editingCol.getFactField() );
        return fieldClassName.equals( ppd.getClassName() );
    }

    private String getFactType() {
        if ( sce.isGlobalVariable( editingCol.getBoundName() ) ) {
            return sce.getGlobalVariable( editingCol.getBoundName() );
        }
        return getFactType( this.editingCol.getBoundName() );
    }

    private String getFactType(String boundName) {
        for ( Pattern52 p : model.getConditionPatterns() ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p.getFactType();
            }
        }
        return "";
    }

    private TextBox getFieldLabel() {
        final TextBox box = new TextBox();
        box.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                editingCol.setFactField( box.getText() );
            }
        } );
        return box;
    }

    private ListBox loadBoundFacts() {
        Set<String> facts = new HashSet<String>();
        for ( Pattern52 p : model.getConditionPatterns() ) {
            if ( !p.isNegated() ) {
                facts.add( p.getBoundName() );
            }
        }

        ListBox box = new ListBox();
        for ( Iterator<String> iterator = facts.iterator(); iterator.hasNext(); ) {
            String b = (String) iterator.next();
            box.addItem( b );
        }

        String[] globs = this.sce.getGlobalVariables();
        for ( int i = 0; i < globs.length; i++ ) {
            box.addItem( globs[i] );
        }

        return box;
    }

    private boolean nil(String s) {
        return s == null || s.equals( "" );
    }

    private void showChangeFact(ClickEvent w) {
        final FormStylePopup pop = new FormStylePopup();

        final ListBox pats = this.loadBoundFacts();
        pop.addAttribute( constants.ChooseFact(),
                          pats );
        Button ok = new Button( constants.OK() );
        pop.addAttribute( "",
                          ok );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                String val = pats.getValue( pats.getSelectedIndex() );
                editingCol.setBoundName( val );
                editingCol.setFactField( null );
                doBindFieldToWorkItem();
                doBindingLabel();
                doFieldLabel();
                pop.hide();
            }
        } );

        pop.show();

    }

    private void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setModal( false );

        final String factType = getFactType();
        String[] fields = this.sce.getFieldCompletions( factType );
        final ListBox box = new ListBox();
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[i] );
        }
        pop.addAttribute( constants.Field(),
                          box );
        Button b = new Button( constants.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                editingCol.setFactField( box.getItemText( box.getSelectedIndex() ) );
                editingCol.setType( sce.getFieldType( factType,
                                                      editingCol.getFactField() ) );
                doBindFieldToWorkItem();
                doFieldLabel();
                pop.hide();
            }
        } );
        pop.show();

    }

    private boolean unique(String header) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) return false;
        }
        return true;
    }

}
