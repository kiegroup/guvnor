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
import java.util.Set;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLRuleModel;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an editor for columns that are for inserting facts.
 */
public class ActionInsertFactPopup extends FormStylePopup {

    private static Images              images                           = (Images) GWT.create( Images.class );
    private static Constants           constants                        = GWT.create( Constants.class );

    private SmallLabel                 patternLabel                     = new SmallLabel();
    private TextBox                    fieldLabel                       = getFieldLabel();
    private SimplePanel                limitedEntryValueWidgetContainer = new SimplePanel();
    private int                        limitedEntryValueAttributeIndex  = 0;

    private ActionInsertFactCol52      editingCol;
    private GuidedDecisionTable52      model;
    private SuggestionCompletionEngine sce;
    private DTCellValueWidgetFactory   factory;
    private BRLRuleModel               validator;

    public ActionInsertFactPopup(final SuggestionCompletionEngine sce,
                                 final GuidedDecisionTable52 model,
                                 final GenericColumnCommand refreshGrid,
                                 final ActionInsertFactCol52 col,
                                 final boolean isNew) {
        this.validator = new BRLRuleModel( model );
        this.editingCol = cloneActionInsertColumn( col );
        this.model = model;
        this.sce = sce;

        //Set-up factory for common widgets
        factory = new DTCellValueWidgetFactory( model,
                                                sce );

        setTitle( constants.ActionColumnConfigurationInsertingANewFact() );
        setModal( false );

        //Fact being inserted
        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( patternLabel );
        doPatternLabel();

        Image changePattern = new ImageButton( images.edit(),
                                               constants.ChooseAPatternThatThisColumnAddsDataTo(),
                                               new ClickHandler() {
                                                   public void onClick(ClickEvent w) {
                                                       showChangePattern( w );
                                                   }
                                               } );
        pattern.add( changePattern );
        addAttribute( constants.Pattern(),
                      pattern );

        //Fact field being set
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

        //Optional value list
        if ( model.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
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
                                   constants.ValueListsExplanation() ) );
            addAttribute( constants.optionalValueList(),
                          vl );
        }

        //Default Value
        if ( model.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            addAttribute( constants.DefaultValue(),
                          DTCellValueWidgetFactory.getDefaultEditor( editingCol ) );
        }

        //Limited entry value widget
        limitedEntryValueAttributeIndex = addAttribute( constants.LimitedEntryValue(),
                                                        limitedEntryValueWidgetContainer );
        makeLimitedValueWidget();

        //Logical insertion
        addAttribute( constants.LogicallyInsertColon(),
                      doInsertLogical() );

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

    private ActionInsertFactCol52 cloneActionInsertColumn(ActionInsertFactCol52 col) {
        ActionInsertFactCol52 clone = null;
        if ( col instanceof LimitedEntryActionInsertFactCol52 ) {
            clone = new LimitedEntryActionInsertFactCol52();
            DTCellValue52 dcv = cloneLimitedEntryValue( ((LimitedEntryCol) col).getValue() );
            ((LimitedEntryCol) clone).setValue( dcv );
        } else {
            clone = new ActionInsertFactCol52();
        }
        clone.setBoundName( col.getBoundName() );
        clone.setType( col.getType() );
        clone.setFactField( col.getFactField() );
        clone.setFactType( col.getFactType() );
        clone.setHeader( col.getHeader() );
        clone.setValueList( col.getValueList() );
        clone.setDefaultValue( col.getDefaultValue() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setInsertLogical( col.isInsertLogical() );
        return clone;
    }

    private DTCellValue52 cloneLimitedEntryValue(DTCellValue52 dcv) {
        if ( dcv == null ) {
            return null;
        }
        DTCellValue52 clone = new DTCellValue52();
        switch ( dcv.getDataType() ) {
            case BOOLEAN :
                clone.setBooleanValue( dcv.getBooleanValue() );
                break;
            case DATE :
                clone.setDateValue( dcv.getDateValue() );
                break;
            case NUMERIC :
                clone.setNumericValue( dcv.getNumericValue() );
                break;
            case STRING :
                clone.setStringValue( dcv.getStringValue() );
        }
        return clone;
    }

    private void makeLimitedValueWidget() {
        if ( !(editingCol instanceof LimitedEntryActionInsertFactCol52) ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            return;
        }
        if ( nil( editingCol.getFactField() ) ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            return;
        }
        LimitedEntryActionInsertFactCol52 lea = (LimitedEntryActionInsertFactCol52) editingCol;
        setAttributeVisibility( limitedEntryValueAttributeIndex,
                                true );
        if ( lea.getValue() == null ) {
            lea.setValue( factory.makeNewValue( editingCol ) );
        }
        limitedEntryValueWidgetContainer.setWidget( factory.getWidget( editingCol,
                                                                       lea.getValue() ) );
    }

    private void doFieldLabel() {
        if ( nil( this.editingCol.getFactField() ) ) {
            fieldLabel.setText( constants.pleaseChooseFactType() );
        } else {
            fieldLabel.setText( editingCol.getFactField() );
        }

    }

    private void doPatternLabel() {
        if ( this.editingCol.getFactType() != null ) {
            this.patternLabel.setText( this.editingCol.getFactType()
                                       + " ["
                                       + editingCol.getBoundName()
                                       + "]" );
        }
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

    private ListBox loadPatterns() {
        Set<String> vars = new HashSet<String>();
        ListBox patterns = new ListBox();

        for ( Object o : model.getActionCols() ) {
            ActionCol52 col = (ActionCol52) o;
            if ( col instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 c = (ActionInsertFactCol52) col;
                if ( !vars.contains( c.getBoundName() ) ) {
                    patterns.addItem( c.getFactType()
                                              + " ["
                                              + c.getBoundName()
                                              + "]",
                                      c.getFactType()
                                              + " "
                                              + c.getBoundName() );
                    vars.add( c.getBoundName() );
                }
            }

        }

        return patterns;

    }

    private boolean nil(String s) {
        return s == null || s.equals( "" );
    }

    private void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setModal( false );
        String[] fields = this.sce.getFieldCompletions( FieldAccessorsAndMutators.MUTATOR,
                                                        this.editingCol.getFactType() );
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
                editingCol.setType( sce.getFieldType( editingCol.getFactType(),
                                                      editingCol.getFactField() ) );
                makeLimitedValueWidget();
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

    protected void showChangePattern(ClickEvent w) {

        final ListBox pats = this.loadPatterns();
        if ( pats.getItemCount() == 0 ) {
            showNewPatternDialog();
            return;
        }
        final FormStylePopup pop = new FormStylePopup();
        Button ok = new Button( "OK" );
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( pats );
        hp.add( ok );

        pop.addAttribute( constants.ChooseExistingPatternToAddColumnTo(),
                          hp );
        pop.addAttribute( "",
                          new HTML( constants.ORwithEmphasis() ) );

        Button createPattern = new Button( constants.CreateNewFactPattern() );
        createPattern.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                pop.hide();
                showNewPatternDialog();
            }
        } );
        pop.addAttribute( "",
                          createPattern );

        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                String[] val = pats.getValue( pats.getSelectedIndex() ).split( "\\s" ); // NON-NLS
                editingCol.setFactType( val[0] );
                editingCol.setBoundName( val[1] );
                editingCol.setFactField( null );
                makeLimitedValueWidget();
                doPatternLabel();
                doFieldLabel();
                pop.hide();
            }
        } );

        pop.show();
    }

    protected void showNewPatternDialog() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( constants.NewFactSelectTheType() );
        final ListBox types = new ListBox();
        for ( int i = 0; i < sce.getFactTypes().length; i++ ) {
            types.addItem( sce.getFactTypes()[i] );
        }
        pop.addAttribute( constants.FactType(),
                          types );
        final TextBox binding = new BindingTextBox();
        pop.addAttribute( constants.Binding(),
                          binding );

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {

                //Validate column configuration
                String ft = types.getItemText( types.getSelectedIndex() );
                String fn = binding.getText();
                if ( fn.equals( "" ) ) {
                    Window.alert( constants.PleaseEnterANameForFact() );
                    return;
                } else if ( fn.equals( ft ) ) {
                    Window.alert( constants.PleaseEnterANameThatIsNotTheSameAsTheFactType() );
                    return;
                } else if ( !isBindingUnique( fn ) ) {
                    Window.alert( constants.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                    return;
                }

                //Configure column
                editingCol.setBoundName( binding.getText() );
                editingCol.setFactType( types.getItemText( types.getSelectedIndex() ) );
                editingCol.setFactField( null );
                makeLimitedValueWidget();
                doPatternLabel();
                doFieldLabel();
                pop.hide();
            }
        } );
        pop.addAttribute( "",
                          ok );

        pop.show();
    }

    private boolean isBindingUnique(String binding) {
        return !validator.isVariableNameUsed( binding );
    }

    private Widget doInsertLogical() {
        HorizontalPanel hp = new HorizontalPanel();

        final CheckBox cb = new CheckBox();
        cb.setValue( editingCol.isInsertLogical() );
        cb.setText( "" );
        cb.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if ( sce.isGlobalVariable( editingCol.getBoundName() ) ) {
                    cb.setEnabled( false );
                    editingCol.setInsertLogical( false );
                } else {
                    editingCol.setInsertLogical( cb.getValue() );
                }
            }
        } );
        hp.add( cb );
        hp.add( new InfoPopup( constants.LogicallyInsertANewFact(),
                               constants.LogicallyAssertAFactTheFactWillBeRetractedWhenTheSupportingEvidenceIsRemoved() ) );
        return hp;
    }

}
