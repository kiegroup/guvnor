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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

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

public class ActionSetColumn extends FormStylePopup {

    private Constants                  constants    = GWT.create( Constants.class );

    private ActionSetFieldCol52        editingCol;
    private SmallLabel                 bindingLabel = new SmallLabel();
    private TextBox                    fieldLabel   = getFieldLabel();
    private GuidedDecisionTable52      model;
    private SuggestionCompletionEngine sce;

    public ActionSetColumn(SuggestionCompletionEngine sce,
                           final GuidedDecisionTable52 model,
                           final GenericColumnCommand refreshGrid,
                           final ActionSetFieldCol52 col,
                           final boolean isNew) {
        this.editingCol = new ActionSetFieldCol52();
        this.model = model;
        this.sce = sce;

        editingCol.setBoundName( col.getBoundName() );
        editingCol.setFactField( col.getFactField() );
        editingCol.setHeader( col.getHeader() );
        editingCol.setType( col.getType() );
        editingCol.setValueList( col.getValueList() );
        editingCol.setUpdate( col.isUpdate() );
        editingCol.setDefaultValue( col.getDefaultValue() );
        editingCol.setHideColumn( col.isHideColumn() );

        super.setModal( false );
        setTitle( constants.ColumnConfigurationSetAFieldOnAFact() );

        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( bindingLabel );
        doBindingLabel();

        Image changePattern = GuvnorImages.INSTANCE.Edit();
        changePattern.setAltText(constants.ChooseABoundFactThatThisColumnPertainsTo());
        changePattern.setTitle(constants.ChooseABoundFactThatThisColumnPertainsTo());
        changePattern.addClickHandler(
                new ClickHandler() {
                    public void onClick(ClickEvent w) {
                        showChangeFact(w);
                    }
                });
        pattern.add( changePattern );
        addAttribute( constants.Fact(),
                      pattern );

        HorizontalPanel field = new HorizontalPanel();
        field.add( fieldLabel );
        Image editField = GuvnorImages.INSTANCE.Edit();
        editField.setAltText(constants.EditTheFieldThatThisColumnOperatesOn());
        editField.setTitle(constants.EditTheFieldThatThisColumnOperatesOn());
        editField.addClickHandler(
                new ClickHandler() {
                    public void onClick(ClickEvent w) {
                        showFieldChange();
                    }
                });
        field.add( editField );
        addAttribute( constants.Field(),
                      field );
        doFieldLabel();

        final TextBox valueList = new TextBox();
        valueList.setText( editingCol.getValueList() );
        valueList.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                editingCol.setValueList( valueList.getText() );
            }
        } );
        HorizontalPanel vl = new HorizontalPanel();
        vl.add( valueList );
        vl.add( new InfoPopup( constants.ValueList(),
                               constants
                                       .ValueListsExplanation() ) );
        addAttribute( constants.optionalValueList(),
                      vl );

        final TextBox header = new TextBox();
        header.setText( col.getHeader() );
        header.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                editingCol.setHeader( header.getText() );
            }
        } );
        addAttribute( constants.ColumnHeaderDescription(),
                      header );

        addAttribute( constants.UpdateEngineWithChanges(),
                      doUpdate() );

        addAttribute( constants.DefaultValue(),
                      GuidedDTColumnConfig.getDefaultEditor( editingCol ) );

        Button apply = new Button( constants.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( null == editingCol.getHeader()
                        || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( constants
                            .YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( isNew ) {
                    if ( !unique( editingCol.getHeader() ) ) {
                        Window.alert( constants
                                .ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                        return;
                    }

                } else {
                    if ( !col.getHeader().equals( editingCol.getHeader() ) ) {
                        if ( !unique( editingCol.getHeader() ) ) {
                            Window.alert( constants
                                    .ThatColumnNameIsAlreadyInUsePleasePickAnother() );
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

    private void doBindingLabel() {
        if ( this.editingCol.getBoundName() != null ) {
            this.bindingLabel.setText( ""
                                       + this.editingCol.getBoundName() );
        } else {
            this.bindingLabel.setText( constants
                    .pleaseChooseABoundFactForThisColumn() );
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
                               constants
                                       .UpdateDescription() ) );
        return hp;
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
