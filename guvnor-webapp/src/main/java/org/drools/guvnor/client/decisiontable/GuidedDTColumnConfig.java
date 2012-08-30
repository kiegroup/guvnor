/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.ui.BindingTextBox;
import org.drools.guvnor.client.modeldriven.ui.CEPOperatorsDropdown;
import org.drools.guvnor.client.modeldriven.ui.CEPWindowOperatorsDropdown;
import org.drools.guvnor.client.modeldriven.ui.OperatorSelection;
import org.drools.guvnor.client.resources.GuvnorImages;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.HasCEPWindow;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a configuration editor for a column in a the guided decision table.
 */
public class GuidedDTColumnConfig extends FormStylePopup {

    /**
     * An editor for setting the default value.
     */
    public static HorizontalPanel getDefaultEditor(final DTColumnConfig52 editingCol) {
        final TextBox defaultValue = new TextBox();
        defaultValue.setText( editingCol.getDefaultValue() );
        final CheckBox hide = new CheckBox( ((Constants) GWT.create( Constants.class )).HideThisColumn() );
        hide.setValue( editingCol.isHideColumn() );
        hide.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent sender) {
                editingCol.setHideColumn( hide.getValue() );
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

    private static Constants           constants                   = ((Constants) GWT.create( Constants.class ));
    private static Images              images                      = (Images) GWT.create( Images.class );

    private GuidedDecisionTable52      model;
    private SuggestionCompletionEngine sce;
    private Pattern52                  editingPattern;
    private ConditionCol52             editingCol;
    private Label                      patternLabel                = new Label();
    private TextBox                    fieldLabel                  = getFieldLabel();
    private Label                      operatorLabel               = new Label();
    private ImageButton                editField;
    private ImageButton                editOp;

    private CEPWindowOperatorsDropdown cwo;
    private TextBox                    entryPointName;
    private int                        cepWindowRowIndex;

    private InfoPopup                  fieldLabelInterpolationInfo = getPredicateHint();

    /**
     * Pass in a null col and it will create a new one.
     */
    public GuidedDTColumnConfig(SuggestionCompletionEngine sce,
                                final GuidedDecisionTable52 model,
                                final ConditionColumnCommand refreshGrid,
                                final ConditionCol52 col,
                                final boolean isNew) {
        super();
        this.setModal( false );
        this.model = model;
        this.sce = sce;
        this.editingPattern = model.getPattern( col );
        this.editingCol = new ConditionCol52();
        editingCol.setConstraintValueType( col.getConstraintValueType() );
        editingCol.setFactField( col.getFactField() );
        editingCol.setFieldType( col.getFieldType() );
        editingCol.setHeader( col.getHeader() );
        editingCol.setOperator( col.getOperator() );
        editingCol.setValueList( col.getValueList() );
        editingCol.setDefaultValue( col.getDefaultValue() );
        editingCol.setHideColumn( col.isHideColumn() );
        editingCol.setParameters( col.getParameters() );
        editingCol.setWidth( col.getWidth() );

        setTitle( constants.ConditionColumnConfiguration() );

        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( patternLabel );
        doPatternLabel();

        ImageButton changePattern = new ImageButton(GuvnorImages.INSTANCE.Edit(),
                                                     constants.ChooseAnExistingPatternThatThisColumnAddsTo(),
                                                     new ClickHandler() {
                                                         public void onClick(ClickEvent w) {
                                                             showChangePattern( w );
                                                         }
                                                     } );
        pattern.add( changePattern );

        addAttribute( constants.Pattern(),
                      pattern );

        // now a radio button with the type
        RadioButton literal = new RadioButton( "constraintValueType",
                                               constants.LiteralValue() );// NON-NLS
        RadioButton formula = new RadioButton( "constraintValueType",
                                               constants.Formula() ); // NON-NLS
        RadioButton predicate = new RadioButton( "constraintValueType",
                                                 constants.Predicate() ); // NON-NLS

        HorizontalPanel valueTypes = new HorizontalPanel();
        valueTypes.add( literal );
        valueTypes.add( formula );
        valueTypes.add( predicate );
        addAttribute( constants.CalculationType(),
                      valueTypes );

        switch ( editingCol.getConstraintValueType() ) {
            case BaseSingleFieldConstraint.TYPE_LITERAL :
                literal.setValue( true );
                break;
            case BaseSingleFieldConstraint.TYPE_RET_VALUE :
                formula.setValue( true );
                break;
            case BaseSingleFieldConstraint.TYPE_PREDICATE :
                predicate.setValue( true );
        }

        literal.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                editingCol.setFactField( null );
                applyConsTypeChange( BaseSingleFieldConstraint.TYPE_LITERAL );
            }
        } );

        formula.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                editingCol.setFactField( null );
                applyConsTypeChange( BaseSingleFieldConstraint.TYPE_RET_VALUE );
            }
        } );
        predicate.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                editingCol.setFactField( null );
                applyConsTypeChange( BaseSingleFieldConstraint.TYPE_PREDICATE );
            }
        } );

        HorizontalPanel field = new HorizontalPanel();
        field.add( fieldLabel );
        field.add( fieldLabelInterpolationInfo );
        this.editField = new ImageButton( GuvnorImages.INSTANCE.Edit(),
                                          GuvnorImages.INSTANCE.EditDisabled(),
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
        this.editOp = new ImageButton( GuvnorImages.INSTANCE.Edit(),
                                       GuvnorImages.INSTANCE.EditDisabled(),
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
        doImageButtons();

        //Add CEP fields for patterns containing Facts declared as Events
        cepWindowRowIndex = addAttribute( constants.DTLabelOverCEPWindow(),
                                          createCEPWindowWidget( editingPattern ) );
        displayCEPOperators();

        //Entry point
        entryPointName = new TextBox();
        entryPointName.setText( editingPattern.getEntryPointName() );
        entryPointName.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                editingPattern.setEntryPointName( entryPointName.getText() );
            }
        } );
        addAttribute( constants.DTLabelFromEntryPoint(),
                      entryPointName );

        //Optional value list
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

        //Default value (and "hide column" check-box for some reason)
        addAttribute( constants.DefaultValue(),
                      getDefaultEditor( editingCol ) );

        //Apply button
        Button apply = new Button( constants.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( null == editingCol.getHeader()
                        || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( constants.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE ) {

                    //Field mandatory for Literals and Formulae
                    if ( null == editingCol.getFactField() || "".equals( editingCol.getFactField() ) ) {
                        Window.alert( constants.PleaseSelectOrEnterField() );
                        return;
                    }

                    //Operator optional for Literals and Formulae
                    if ( null == editingCol.getOperator() || "".equals( editingCol.getOperator() ) ) {
                        Window.alert( constants.NotifyNoSelectedOperator() );
                    }

                } else {

                    //Clear operator for predicates, but leave field intact for interpolation of $param values
                    editingCol.setOperator( null );
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
                refreshGrid.execute( editingPattern,
                                     editingCol );
                hide();

            }
        } );
        addAttribute( "",
                      apply );

    }

    private void applyConsTypeChange(int newType) {
        editingCol.setConstraintValueType( newType );
        doFieldLabel();
        doOperatorLabel();
        doImageButtons();
    }

    private void doImageButtons() {
        int constraintType = editingCol.getConstraintValueType();
        this.editField.setEnabled( constraintType != BaseSingleFieldConstraint.TYPE_PREDICATE );
        this.editOp.setEnabled( constraintType != BaseSingleFieldConstraint.TYPE_PREDICATE );
    }

    private boolean checkUnique(String fn,
                                List<Pattern52> patterns) {
        for ( Pattern52 p : patterns ) {
            if ( p.getBoundName().equals( fn ) ) return false;
        }
        return true;
    }

    private void doFieldLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            if ( this.editingCol.getFactField() == null || this.editingCol.getFactField().equals( "" ) ) {
                fieldLabel.setText( constants.notNeededForPredicate() );
            } else {
                fieldLabel.setText( this.editingCol.getFactField() );
            }
            fieldLabelInterpolationInfo.setVisible( true );
        } else if ( nil( editingPattern.getFactType() ) ) {
            fieldLabel.setText( constants.pleaseSelectAPatternFirst() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else if ( nil( editingCol.getFactField() ) ) {
            fieldLabel.setText( constants.pleaseSelectAField() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else {
            fieldLabel.setText( this.editingCol.getFactField() );
        }
    }

    private void doOperatorLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            operatorLabel.setText( constants.notNeededForPredicate() );
        } else if ( nil( editingPattern.getFactType() ) ) {
            operatorLabel.setText( constants.pleaseSelectAPatternFirst() );
        } else if ( nil( editingCol.getFactField() ) ) {
            operatorLabel.setText( constants.pleaseChooseAFieldFirst() );
        } else if ( nil( editingCol.getOperator() ) ) {
            operatorLabel.setText( constants.pleaseSelectAField() );
        } else {
            operatorLabel.setText( HumanReadable.getOperatorDisplayName( editingCol.getOperator() ) );
        }
    }

    private void doPatternLabel() {
        if ( this.editingPattern.getFactType() != null ) {
            this.patternLabel.setText( (this.editingPattern.isNegated() ? constants.negatedPattern() + " " : "")
                                       + this.editingPattern.getFactType() + " ["
                                       + this.editingPattern.getBoundName() + "]" );
        }
        doFieldLabel();
        doOperatorLabel();

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

    private InfoPopup getPredicateHint() {
        return new InfoPopup( constants.Predicates(),
                              constants.PredicatesInfo() );
    }

    private ListBox loadPatterns() {
        Set<String> vars = new HashSet<String>();
        ListBox patterns = new ListBox();
        for ( int i = 0; i < model.getConditionPatterns().size(); i++ ) {
            Pattern52 p = model.getConditionPatterns().get( i );
            if ( !vars.contains( p.getBoundName() ) ) {
                patterns.addItem( (p.isNegated() ? constants.negatedPattern() + " " : "")
                                          + p.getFactType()
                                          + " [" + p.getBoundName() + "]",
                                  p.getFactType()
                                          + " " + p.getBoundName()
                                          + " " + p.isNegated() );
                vars.add( p.getBoundName() );
            }
        }

        return patterns;

    }

    private boolean nil(String s) {
        return s == null || s.equals( "" );
    }

    private void showOperatorChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( constants.SetTheOperator() );
        pop.setModal( false );
        String[] ops = this.sce.getOperatorCompletions( editingPattern.getFactType(),
                                                        editingCol.getFactField() );
        final CEPOperatorsDropdown box = new CEPOperatorsDropdown( ops,
                                                                   editingCol );

        if ( BaseSingleFieldConstraint.TYPE_LITERAL == this.editingCol
                .getConstraintValueType() ) {
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

    private boolean unique(String header) {
        for ( Pattern52 p : model.getConditionPatterns() ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                if ( c.getHeader().equals( header ) ) return false;
            }
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
                editingPattern = model.getConditionPattern( val[1] );

                //Clear Field and Operator when pattern changes
                editingCol.setFactField( null );
                editingCol.setOperator( null );

                //Set-up UI
                entryPointName.setText( editingPattern.getEntryPointName() );
                cwo.selectItem( editingPattern.getWindow().getOperator() );
                displayCEPOperators();
                doPatternLabel();
                doOperatorLabel();

                pop.hide();
            }
        } );

        pop.show();
    }

    protected void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setModal( false );
        String[] fields = this.sce.getFieldCompletions( FieldAccessorsAndMutators.ACCESSOR,
                                                        this.editingPattern.getFactType() );

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
                editingCol.setFieldType( sce.getFieldType( editingPattern.getFactType(),
                                                           editingCol.getFactField() ) );
                doFieldLabel();
                doOperatorLabel();
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
        final TextBox binding = new BindingTextBox();
        binding.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                binding.setText( binding.getText().replace( " ",
                                                            "" ) );
            }
        } );
        pop.addAttribute( constants.Binding(),
                          binding );

        //Patterns can be negated, i.e. "not Pattern(...)"
        final CheckBox chkNegated = new CheckBox();
        pop.addAttribute( constants.negatePattern(),
                          chkNegated );

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
                                          model.getConditionPatterns() ) ) {
                    Window.alert( constants.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                    return;
                }

                //Create new pattern
                editingPattern = new Pattern52();
                editingPattern.setFactType( ft );
                editingPattern.setBoundName( fn );
                editingPattern.setNegated( chkNegated.getValue() );

                //Clear Field and Operator when pattern changes
                editingCol.setFactField( null );
                editingCol.setOperator( null );

                //Set-up UI
                entryPointName.setText( editingPattern.getEntryPointName() );
                cwo.selectItem( editingPattern.getWindow().getOperator() );
                displayCEPOperators();
                doPatternLabel();
                doOperatorLabel();

                pop.hide();
            }
        } );
        pop.addAttribute( "",
                          ok );

        pop.show();

    }

    //Widget for CEP 'windows'
    private Widget createCEPWindowWidget(final HasCEPWindow c) {
        HorizontalPanel hp = new HorizontalPanel();
        Label lbl = new Label( constants.OverCEPWindow() );
        lbl.setStyleName( "paddedLabel" );
        hp.add( lbl );

        cwo = new CEPWindowOperatorsDropdown( c );
        cwo.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

            public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                OperatorSelection selection = event.getValue();
                String selected = selection.getValue();
                c.getWindow().setOperator( selected );
            }
        } );

        hp.add( cwo );
        return hp;
    }

    private void displayCEPOperators() {
        boolean isVisible = sce.isFactTypeAnEvent( editingPattern.getFactType() );
        setAttributeVisibility( cepWindowRowIndex,
                                isVisible );
    }

}
