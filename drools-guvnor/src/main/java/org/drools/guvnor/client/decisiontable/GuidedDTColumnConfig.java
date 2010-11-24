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

package org.drools.guvnor.client.decisiontable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is a configuration editor for a column in a the guided decision table.
 * @author Michael Neale
 *
 */
public class GuidedDTColumnConfig extends FormStylePopup {

    private Constants                  constants                   = ((Constants) GWT.create( Constants.class ));
    private static Images              images                      = (Images) GWT.create( Images.class );

    private GuidedDecisionTable        dt;
    private SuggestionCompletionEngine sce;
    private ConditionCol               editingCol;
    private SmallLabel                 patternLabel                = new SmallLabel();
    private TextBox                    fieldLabel                  = getFieldLabel();
    private SmallLabel                 operatorLabel               = new SmallLabel();
    private InfoPopup                  fieldLabelInterpolationInfo = getPredicateHint();

    private InfoPopup getPredicateHint() {
        return new InfoPopup( constants.Predicates(),
                              constants.PredicatesInfo() );
    }

    /**
     * Pass in a null col and it will create a new one.
     */
    public GuidedDTColumnConfig(SuggestionCompletionEngine sce,
                                final GuidedDecisionTable dt,
                                final Command refreshGrid,
                                final ConditionCol col,
                                final boolean isNew) {
        super();
        this.setModal( false );
        this.dt = dt;
        this.sce = sce;
        this.editingCol = new ConditionCol();
        editingCol.setBoundName( col.getBoundName() );
        editingCol.setConstraintValueType( col.getConstraintValueType() );
        editingCol.setFactField( col.getFactField() );
        editingCol.setFactType( col.getFactType() );
        editingCol.setHeader( col.getHeader() );
        editingCol.setOperator( col.getOperator() );
        editingCol.setValueList( col.getValueList() );
        editingCol.setDefaultValue( col.getDefaultValue() );
        editingCol.setHideColumn( col.isHideColumn() );

        setTitle( constants.ConditionColumnConfiguration() );

        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( patternLabel );
        doPatternLabel();

        Image changePattern = new ImageButton( images.edit(),
                                               constants.ChooseAnExistingPatternThatThisColumnAddsTo(),
                                               new ClickHandler() { 
                                                   public void onClick(ClickEvent w) {
                                                       showChangePattern( w );
                                                   }
                                               } );
        pattern.add( changePattern );

        addAttribute( constants.Pattern(),
                      pattern );

        //now a radio button with the type
        RadioButton literal = new RadioButton( "constraintValueType",
                                               constants.LiteralValue() );//NON-NLS
        RadioButton formula = new RadioButton( "constraintValueType",
                                               constants.Formula() ); //NON-NLS
        RadioButton predicate = new RadioButton( "constraintValueType",
                                                 constants.Predicate() ); //NON-NLS

        HorizontalPanel valueTypes = new HorizontalPanel();
        valueTypes.add( literal );
        valueTypes.add( formula );
        valueTypes.add( predicate );
        addAttribute( constants.CalculationType(),
                      valueTypes );

        switch ( editingCol.getConstraintValueType() ) {
            case BaseSingleFieldConstraint.TYPE_LITERAL :
                literal.setEnabled( true );
                break;
            case BaseSingleFieldConstraint.TYPE_RET_VALUE :
                formula.setEnabled( true );
                break;
            case BaseSingleFieldConstraint.TYPE_PREDICATE :
                predicate.setEnabled( true );
        }

        literal.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                applyConsTypeChange( BaseSingleFieldConstraint.TYPE_LITERAL );
            }
        } );

        formula.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                applyConsTypeChange( BaseSingleFieldConstraint.TYPE_RET_VALUE );
            }
        } );
        predicate.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                applyConsTypeChange( BaseSingleFieldConstraint.TYPE_PREDICATE );
            }
        } );

        HorizontalPanel field = new HorizontalPanel();
        field.add( fieldLabel );
        field.add( fieldLabelInterpolationInfo );
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

        HorizontalPanel operator = new HorizontalPanel();
        operator.add( operatorLabel );
        Image editOp = new ImageButton( images.edit(),
                                        constants.EditTheOperatorThatIsUsedToCompareDataWithThisField(),
                                        new ClickHandler() {
                                            public void onClick(ClickEvent w) {
                                                showOperatorChange();
                                            }
                                        } );
        operator.add( editOp );
        addAttribute( constants.Operator(),
                      operator );
        doOperatorLabel();

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

        final TextBox header = new TextBox();
        header.setText( col.getHeader() );
        header.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                editingCol.setHeader( header.getText() );
            }
        } );
        addAttribute( constants.ColumnHeaderDescription(),
                      header );

        addAttribute( constants.DefaultValue(),
                      getDefaultEditor( editingCol ) );

        Button apply = new Button( constants.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( constants.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                    if ( null == editingCol.getFactField() || "".equals( editingCol.getFactField() ) ) {
                        Window.alert( constants.PleaseSelectOrEnterField() );
                        return;
                    }
                    if ( null == editingCol.getOperator() || "".equals( editingCol.getOperator() ) ) {
                        // Operator field optional
                        Window.alert( constants.NotifyNoSelectedOperator() );
                    }

                }
                if ( isNew ) {
                    if ( !unique( editingCol.getHeader() ) ) {
                        Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                        return;
                    }
                    dt.getConditionCols().add( editingCol );
                } else {
                    if ( !col.getHeader().equals( editingCol.getHeader() ) ) {
                        if ( !unique( editingCol.getHeader() ) ) {
                            Window.alert( constants.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                            return;
                        }
                    }
                    col.setBoundName( editingCol.getBoundName() );
                    col.setConstraintValueType( editingCol.getConstraintValueType() );
                    col.setFactField( editingCol.getFactField() );
                    col.setFactType( editingCol.getFactType() );

                    col.setHeader( editingCol.getHeader() );
                    col.setOperator( editingCol.getOperator() );
                    col.setValueList( editingCol.getValueList() );
                    col.setDefaultValue( editingCol.getDefaultValue() );
                    col.setHideColumn( editingCol.isHideColumn() );
                }
                refreshGrid.execute();
                hide();

            }
        } );
        addAttribute( "",
                      apply );

    }

    /**
     * An editor for setting the default value.
     */
    public static HorizontalPanel getDefaultEditor(final DTColumnConfig editingCol) {
        final TextBox defaultValue = new TextBox();
        defaultValue.setText( editingCol.getDefaultValue() );
        final CheckBox hide = new CheckBox( ((Constants) GWT.create( Constants.class )).HideThisColumn() );
        hide.setEnabled( editingCol.isHideColumn() );
        hide.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent sender) {
                editingCol.setHideColumn( hide.isEnabled() );
            }
        } );
        defaultValue.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                editingCol.setDefaultValue( defaultValue.getText() );
            }
        } );
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( defaultValue );
        hp.add( hide );
        return hp;
    }

    private boolean unique(String header) {
        for ( ConditionCol o : dt.getConditionCols() ) {
            if ( o.getHeader().equals( header ) ) return false;
        }
        return true;
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

    private void applyConsTypeChange(int newType) {
        editingCol.setConstraintValueType( newType );
        doFieldLabel();
        doOperatorLabel();
    }

    private void doOperatorLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            operatorLabel.setText( constants.notNeededForPredicate() );
        } else if ( nil( editingCol.getFactType() ) ) {
            operatorLabel.setText( constants.pleaseSelectAPatternFirst() );
        } else if ( nil( editingCol.getFactField() ) ) {
            operatorLabel.setText( constants.pleaseChooseAFieldFirst() );
        } else if ( nil( editingCol.getOperator() ) ) {
            operatorLabel.setText( constants.pleaseSelectAField() );
        } else {
            operatorLabel.setText( HumanReadable.getOperatorDisplayName( editingCol.getOperator() ) );
        }
    }

    private void showOperatorChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( constants.SetTheOperator() );
        pop.setModal( false );
        String[] ops = this.sce.getOperatorCompletions( editingCol.getFactType(),
                                                        editingCol.getFactField() );
        final ListBox box = new ListBox();
        for ( int i = 0; i < ops.length; i++ ) {
            box.addItem( HumanReadable.getOperatorDisplayName( ops[i] ),
                         ops[i] );
        }

        if ( BaseSingleFieldConstraint.TYPE_LITERAL == this.editingCol.getConstraintValueType() ) {
            box.addItem( HumanReadable.getOperatorDisplayName( "in" ),
                         "in" );
        }

        box.addItem( constants.noOperator(),
                     "" );
        pop.addAttribute( constants.Operator(),
                          box );
        Button b = new Button( constants.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                editingCol.setOperator( box.getValue( box.getSelectedIndex() ) );
                doOperatorLabel();
                pop.hide();
            }
        } );
        pop.show();

    }

    private void doFieldLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            fieldLabel.setText( constants.notNeededForPredicate() );
            fieldLabelInterpolationInfo.setVisible( true );
        } else if ( nil( editingCol.getFactType() ) ) {
            fieldLabel.setText( constants.pleaseSelectAPatternFirst() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else if ( nil( editingCol.getFactField() ) ) {
            fieldLabel.setText( constants.pleaseSelectAField() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else {
            fieldLabel.setText( this.editingCol.getFactField() );
        }
    }

    private boolean nil(String s) {
        return s == null || s.equals( "" );
    }

    protected void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setModal( false );
        String[] fields = this.sce.getFieldCompletions( FieldAccessorsAndMutators.ACCESSOR,
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
                doFieldLabel();
                doOperatorLabel();
                pop.hide();
            }
        } );
        pop.show();
    }

    private void doPatternLabel() {
        if ( this.editingCol.getFactType() != null ) {
            this.patternLabel.setText( this.editingCol.getFactType() + " [" + editingCol.getBoundName() + "]" );
        }
        doFieldLabel();
        doOperatorLabel();

    }

    protected void showChangePattern(ClickEvent w) {

        final ListBox pats = this.loadPatterns();
        if ( pats.getItemCount() == 0 ) {
            showNewPatternDialog();
            return;
        }
        final FormStylePopup pop = new FormStylePopup();
        Button ok = new Button( constants.OK() );
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
                String[] val = pats.getValue( pats.getSelectedIndex() ).split( "\\s" );
                editingCol.setFactType( val[0] );
                editingCol.setBoundName( val[1] );
                doPatternLabel();
                pop.hide();
            }
        } );

        pop.show();
    }

    protected void showNewPatternDialog() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( constants.CreateANewFactPattern() );
        final ListBox types = new ListBox();
        for ( int i = 0; i < sce.getFactTypes().length; i++ ) {
            types.addItem( sce.getFactTypes()[i] );
        }
        pop.addAttribute( constants.FactType(),
                          types );
        final TextBox binding = new TextBox();
        binding.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                binding.setText( binding.getText().replace( " ",
                                                            "" ) );
            }
        } );
        pop.addAttribute( constants.name(),
                          binding );

        Button ok = new Button( constants.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {

                String ft = types.getItemText( types.getSelectedIndex() );
                String fn = binding.getText();
                if ( fn.equals( "" ) ) {
                    Window.alert( constants.PleaseEnterANameForFact() );
                    return;
                } else if ( fn.equals( ft ) ) {
                    Window.alert( constants.PleaseEnterANameThatIsNotTheSameAsTheFactType() );
                    return;
                } else if ( !checkUnique( fn,
                                          dt.getConditionCols() ) ) {
                    Window.alert( constants.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                    return;
                }
                editingCol.setBoundName( fn );
                editingCol.setFactType( ft );
                doPatternLabel();
                pop.hide();
            }
        } );
        pop.addAttribute( "",
                          ok );

        pop.show();

    }

    private boolean checkUnique(String fn,
                                List<ConditionCol> conditionCols) {
        for ( ConditionCol c : conditionCols ) {
            if ( c.getBoundName().equals( fn ) ) return false;
        }
        return true;
    }

    private ListBox loadPatterns() {
        Set<String> vars = new HashSet<String>();
        ListBox patterns = new ListBox();
        for ( int i = 0; i < dt.getConditionCols().size(); i++ ) {
            ConditionCol c = dt.getConditionCols().get( i );
            if ( !vars.contains( c.getBoundName() ) ) {
                patterns.addItem( c.getFactType() + " [" + c.getBoundName() + "]",
                                  c.getFactType() + " " + c.getBoundName() );
                vars.add( c.getBoundName() );
            }
        }

        return patterns;

    }

}
