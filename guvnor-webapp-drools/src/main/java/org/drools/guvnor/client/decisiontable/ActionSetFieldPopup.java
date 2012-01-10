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

import java.util.List;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.BRLRuleModel;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;

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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActionSetFieldPopup extends FormStylePopup {

    private static Constants           constants                        = GWT.create( Constants.class );
    private static Images              images                           = (Images) GWT.create( Images.class );

    private SmallLabel                 bindingLabel                     = new SmallLabel();
    private TextBox                    fieldLabel                       = getFieldLabel();
    private SimplePanel                limitedEntryValueWidgetContainer = new SimplePanel();
    private int                        limitedEntryValueAttributeIndex  = 0;

    private ActionSetFieldCol52        editingCol;
    private GuidedDecisionTable52      model;
    private SuggestionCompletionEngine sce;
    private DTCellValueWidgetFactory   factory;
    private BRLRuleModel               rm;

    public ActionSetFieldPopup(final SuggestionCompletionEngine sce,
                               final GuidedDecisionTable52 model,
                               final GenericColumnCommand refreshGrid,
                               final ActionSetFieldCol52 col,
                               final boolean isNew) {
        this.rm = new BRLRuleModel( model );
        this.editingCol = cloneActionSetColumn( col );
        this.model = model;
        this.sce = sce;

        //Set-up factory for common widgets
        factory = new DTCellValueWidgetFactory( model,
                                                sce );

        setTitle( constants.ColumnConfigurationSetAFieldOnAFact() );
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

        //Update Engine with changes
        addAttribute( constants.UpdateEngineWithChanges(),
                      doUpdate() );

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

    private ActionSetFieldCol52 cloneActionSetColumn(ActionSetFieldCol52 col) {
        ActionSetFieldCol52 clone = null;
        if ( col instanceof LimitedEntryActionSetFieldCol52 ) {
            clone = new LimitedEntryActionSetFieldCol52();
            DTCellValue52 dcv = cloneLimitedEntryValue( ((LimitedEntryCol) col).getValue() );
            ((LimitedEntryCol) clone).setValue( dcv );
        } else {
            clone = new ActionSetFieldCol52();
        }
        clone.setBoundName( col.getBoundName() );
        clone.setFactField( col.getFactField() );
        clone.setHeader( col.getHeader() );
        clone.setType( col.getType() );
        clone.setValueList( col.getValueList() );
        clone.setUpdate( col.isUpdate() );
        clone.setDefaultValue( col.getDefaultValue() );
        clone.setHideColumn( col.isHideColumn() );
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
        if ( !(editingCol instanceof LimitedEntryActionSetFieldCol52) ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            return;
        }
        if ( nil( editingCol.getFactField() ) ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            return;
        }
        LimitedEntryActionSetFieldCol52 lea = (LimitedEntryActionSetFieldCol52) editingCol;
        setAttributeVisibility( limitedEntryValueAttributeIndex,
                                true );
        if ( lea.getValue() == null ) {
            lea.setValue( factory.makeNewValue( editingCol ) );
        }
        limitedEntryValueWidgetContainer.setWidget( factory.getWidget( editingCol,
                                                                       lea.getValue() ) );
    }

    private void doBindingLabel() {
        if ( this.editingCol.getBoundName() != null ) {
            this.bindingLabel.setText( "" + this.editingCol.getBoundName() );
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
        return rm.getLHSBoundFact( boundName ).getFactType();
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

    private ListBox loadBoundFacts(String binding) {
        ListBox listBox = new ListBox();
        listBox.addItem( constants.Choose() );
        List<String> factBindings = rm.getLHSBoundFacts();

        for ( int index = 0; index < factBindings.size(); index++ ) {
            String boundName = factBindings.get( index );
            if ( !"".equals( boundName ) ) {
                listBox.addItem( boundName );
                if ( boundName.equals( binding ) ) {
                    listBox.setSelectedIndex( index + 1 );
                }
            }
        }

        String[] globs = this.sce.getGlobalVariables();
        for ( int i = 0; i < globs.length; i++ ) {
            listBox.addItem( globs[i] );
        }

        listBox.setEnabled( listBox.getItemCount() > 1 );
        if ( listBox.getItemCount() == 1 ) {
            listBox.clear();
            listBox.addItem( constants.NoPatternBindingsAvailable() );
        }

        return listBox;
    }

    private boolean nil(String s) {
        return s == null || s.equals( "" );
    }

    private void showChangeFact(ClickEvent w) {
        final FormStylePopup pop = new FormStylePopup();

        final ListBox pats = this.loadBoundFacts( editingCol.getBoundName() );
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
                makeLimitedValueWidget();
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

}
