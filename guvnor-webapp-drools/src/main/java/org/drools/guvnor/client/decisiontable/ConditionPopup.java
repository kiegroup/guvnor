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
import java.util.Set;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.HumanReadable;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPOperatorsDropdown;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPWindowOperatorsDropdown;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.OperatorSelection;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.HasCEPWindow;
import org.drools.ide.common.client.modeldriven.dt52.BRLRuleModel;
import org.drools.ide.common.client.modeldriven.dt52.CompositeColumn;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a configuration editor for a column in a the guided decision table.
 */
public class ConditionPopup extends FormStylePopup {

    private SmallLabel                 patternLabel                     = new SmallLabel();
    private TextBox                    fieldLabel                       = getFieldLabel();
    private TextBox                    binding                          = new BindingTextBox();
    private Label                      operatorLabel                    = new Label();
    private SimplePanel                limitedEntryValueWidgetContainer = new SimplePanel();
    private int                        limitedEntryValueAttributeIndex  = 0;
    private ImageButton                editField;
    private ImageButton                editOp;

    private CEPWindowOperatorsDropdown cwo;
    private TextBox                    entryPointName;
    private int                        cepWindowRowIndex;

    private GuidedDecisionTable52      model;
    private SuggestionCompletionEngine sce;
    private Pattern52                  editingPattern;
    private ConditionCol52             editingCol;
    private DTCellValueWidgetFactory   factory;
    private Validator                  validator;
    private BRLRuleModel               rm;

    private final boolean              isReadOnly;

    private InfoPopup                  fieldLabelInterpolationInfo      = getPredicateHint();

    public ConditionPopup(SuggestionCompletionEngine sce,
                          final GuidedDecisionTable52 model,
                          final ConditionColumnCommand refreshGrid,
                          final ConditionCol52 col,
                          final boolean isNew,
                          final boolean isReadOnly) {
        this.rm = new BRLRuleModel( model );
        this.editingPattern = model.getPattern( col );
        this.editingCol = cloneConditionColumn( col );
        this.model = model;
        this.sce = sce;
        this.isReadOnly = isReadOnly;

        //Set-up factory for common widgets
        factory = new DTCellValueWidgetFactory( model,
                                                sce,
                                                isReadOnly );

        validator = new Validator( model.getConditions() );

        setTitle( Constants.INSTANCE.ConditionColumnConfiguration() );
        setModal( false );

        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( patternLabel );
        doPatternLabel();

        //Pattern selector
        ImageButton changePattern = new ImageButton( DroolsGuvnorImages.INSTANCE.edit(),
                                                     DroolsGuvnorImages.INSTANCE.editDisabled(),
                                                     Constants.INSTANCE.ChooseAnExistingPatternThatThisColumnAddsTo(),
                                                     new ClickHandler() {
                                                         public void onClick(ClickEvent w) {
                                                             showChangePattern( w );
                                                         }
                                                     } );
        changePattern.setEnabled( !isReadOnly );
        pattern.add( changePattern );

        addAttribute( Constants.INSTANCE.Pattern(),
                      pattern );

        //Radio buttons for Calculation Type
        switch ( model.getTableFormat() ) {
            case EXTENDED_ENTRY :
                RadioButton literal = new RadioButton( "constraintValueType",
                                                       Constants.INSTANCE.LiteralValue() );// NON-NLS
                RadioButton formula = new RadioButton( "constraintValueType",
                                                       Constants.INSTANCE.Formula() ); // NON-NLS
                RadioButton predicate = new RadioButton( "constraintValueType",
                                                         Constants.INSTANCE.Predicate() ); // NON-NLS

                HorizontalPanel valueTypes = new HorizontalPanel();
                valueTypes.add( literal );
                valueTypes.add( formula );
                valueTypes.add( predicate );
                addAttribute( Constants.INSTANCE.CalculationType(),
                              valueTypes );

                switch ( editingCol.getConstraintValueType() ) {
                    case BaseSingleFieldConstraint.TYPE_LITERAL :
                        literal.setValue( true );
                        binding.setEnabled( true && !isReadOnly );
                        break;
                    case BaseSingleFieldConstraint.TYPE_RET_VALUE :
                        formula.setValue( true );
                        binding.setEnabled( false );
                        break;
                    case BaseSingleFieldConstraint.TYPE_PREDICATE :
                        predicate.setValue( true );
                        binding.setEnabled( false );
                }

                literal.setEnabled( !isReadOnly );
                if ( !isReadOnly ) {
                    literal.addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent w) {
                            editingCol.setFactField( null );
                            applyConsTypeChange( BaseSingleFieldConstraint.TYPE_LITERAL );
                        }
                    } );
                }

                formula.setEnabled( !isReadOnly );
                if ( !isReadOnly ) {
                    formula.addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent w) {
                            editingCol.setFactField( null );
                            applyConsTypeChange( BaseSingleFieldConstraint.TYPE_RET_VALUE );
                        }
                    } );
                }

                predicate.setEnabled( !isReadOnly );
                if ( !isReadOnly ) {
                    predicate.addClickHandler( new ClickHandler() {
                        public void onClick(ClickEvent w) {
                            editingCol.setFactField( null );
                            applyConsTypeChange( BaseSingleFieldConstraint.TYPE_PREDICATE );
                        }
                    } );
                }
                break;

            case LIMITED_ENTRY :
                binding.setEnabled( !isReadOnly );
        }

        //Fact field
        HorizontalPanel field = new HorizontalPanel();
        fieldLabel.setEnabled( !isReadOnly );
        field.add( fieldLabel );
        field.add( fieldLabelInterpolationInfo );
        this.editField = new ImageButton( DroolsGuvnorImages.INSTANCE.edit(),
                                          DroolsGuvnorImages.INSTANCE.editDisabled(),
                                          Constants.INSTANCE.EditTheFieldThatThisColumnOperatesOn(),
                                          new ClickHandler() {
                                              public void onClick(ClickEvent w) {
                                                  showFieldChange();
                                              }
                                          } );
        editField.setEnabled( !isReadOnly );
        field.add( editField );
        addAttribute( Constants.INSTANCE.Field(),
                      field );
        doFieldLabel();

        //Operator
        HorizontalPanel operator = new HorizontalPanel();
        operator.add( operatorLabel );
        this.editOp = new ImageButton( DroolsGuvnorImages.INSTANCE.edit(),
                                       DroolsGuvnorImages.INSTANCE.editDisabled(),
                                       Constants.INSTANCE.EditTheOperatorThatIsUsedToCompareDataWithThisField(),
                                       new ClickHandler() {
                                           public void onClick(ClickEvent w) {
                                               showOperatorChange();
                                           }
                                       } );
        editOp.setEnabled( !isReadOnly );
        operator.add( editOp );
        addAttribute( Constants.INSTANCE.Operator(),
                      operator );
        doOperatorLabel();
        doImageButtons();

        //Add CEP fields for patterns containing Facts declared as Events
        cepWindowRowIndex = addAttribute( Constants.INSTANCE.DTLabelOverCEPWindow(),
                                          createCEPWindowWidget( editingPattern ) );
        displayCEPOperators();

        //Entry point
        entryPointName = new TextBox();
        entryPointName.setText( editingPattern.getEntryPointName() );
        entryPointName.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            entryPointName.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    editingPattern.setEntryPointName( entryPointName.getText() );
                }
            } );
        }
        addAttribute( Constants.INSTANCE.DTLabelFromEntryPoint(),
                      entryPointName );

        //Column header
        final TextBox header = new TextBox();
        header.setText( col.getHeader() );
        header.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            header.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    editingCol.setHeader( header.getText() );
                }
            } );
        }
        addAttribute( Constants.INSTANCE.ColumnHeaderDescription(),
                      header );

        //Optional value list
        if ( model.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            final TextBox valueList = new TextBox();
            valueList.setText( editingCol.getValueList() );
            valueList.setEnabled( !isReadOnly );
            if ( !isReadOnly ) {
                valueList.addChangeHandler( new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        editingCol.setValueList( valueList.getText() );
                    }
                } );
            }
            HorizontalPanel vl = new HorizontalPanel();
            vl.add( valueList );
            vl.add( new InfoPopup( Constants.INSTANCE.ValueList(),
                                   Constants.INSTANCE.ValueListsExplanation() ) );
            addAttribute( Constants.INSTANCE.optionalValueList(),
                          vl );
        }

        //Default value
        if ( model.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            addAttribute( Constants.INSTANCE.DefaultValue(),
                          DTCellValueWidgetFactory.getDefaultEditor( editingCol,
                                                                     isReadOnly ) );
        }

        //Limited entry value widget
        limitedEntryValueAttributeIndex = addAttribute( Constants.INSTANCE.LimitedEntryValue(),
                                                        limitedEntryValueWidgetContainer );
        makeLimitedValueWidget();

        //Field Binding
        binding.setText( col.getBinding() );
        if ( !isReadOnly ) {
            binding.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent event) {
                    editingCol.setBinding( binding.getText() );
                }
            } );
        }
        addAttribute( Constants.INSTANCE.Binding(),
                      binding );

        //Hide column tick-box
        addAttribute( Constants.INSTANCE.HideThisColumn(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        //Apply button
        Button apply = new Button( Constants.INSTANCE.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( Constants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE ) {

                    //Field mandatory for Literals and Formulae
                    if ( null == editingCol.getFactField() || "".equals( editingCol.getFactField() ) ) {
                        Window.alert( Constants.INSTANCE.PleaseSelectOrEnterField() );
                        return;
                    }

                    //Operator optional for Literals and Formulae
                    if ( null == editingCol.getOperator() || "".equals( editingCol.getOperator() ) ) {
                        Window.alert( Constants.INSTANCE.NotifyNoSelectedOperator() );
                    }

                } else {

                    //Clear operator for predicates, but leave field intact for interpolation of $param values
                    editingCol.setOperator( null );
                }

                //Check for unique binding
                if ( editingCol.isBound() && !isBindingUnique( editingCol.getBinding() ) ) {
                    Window.alert( Constants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                    return;
                }

                //Check column header is unique
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

                //Clear binding if column is not a literal
                if ( editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL ) {
                    editingCol.setBinding( null );
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

    private ConditionCol52 cloneConditionColumn(ConditionCol52 col) {
        ConditionCol52 clone = null;
        if ( col instanceof LimitedEntryConditionCol52 ) {
            clone = new LimitedEntryConditionCol52();
            DTCellValue52 dcv = cloneLimitedEntryValue( ((LimitedEntryCol) col).getValue() );
            ((LimitedEntryCol) clone).setValue( dcv );
        } else {
            clone = new ConditionCol52();
        }
        clone.setConstraintValueType( col.getConstraintValueType() );
        clone.setFactField( col.getFactField() );
        clone.setFieldType( col.getFieldType() );
        clone.setHeader( col.getHeader() );
        clone.setOperator( col.getOperator() );
        clone.setValueList( col.getValueList() );
        clone.setDefaultValue( col.getDefaultValue() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setParameters( col.getParameters() );
        clone.setWidth( col.getWidth() );
        clone.setBinding( col.getBinding() );
        return clone;
    }

    private DTCellValue52 cloneLimitedEntryValue(DTCellValue52 dcv) {
        if ( dcv == null ) {
            return null;
        }
        DTCellValue52 clone = new DTCellValue52( dcv );
        return clone;
    }

    private void makeLimitedValueWidget() {
        if ( !(editingCol instanceof LimitedEntryConditionCol52) ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            return;
        }
        LimitedEntryConditionCol52 lec = (LimitedEntryConditionCol52) editingCol;
        boolean doesOperatorNeedValue = validator.doesOperatorNeedValue( editingCol );
        if ( !doesOperatorNeedValue ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            lec.setValue( null );
            return;
        }
        setAttributeVisibility( limitedEntryValueAttributeIndex,
                                true );
        if ( lec.getValue() == null ) {
            lec.setValue( factory.makeNewValue( editingPattern,
                                                editingCol ) );
        }
        limitedEntryValueWidgetContainer.setWidget( factory.getWidget( editingPattern,
                                                                       editingCol,
                                                                       lec.getValue() ) );
    }

    private void applyConsTypeChange(int newType) {
        editingCol.setConstraintValueType( newType );
        binding.setEnabled( newType == BaseSingleFieldConstraint.TYPE_LITERAL && !isReadOnly );
        doFieldLabel();
        doOperatorLabel();
        doImageButtons();
    }

    private void doImageButtons() {
        int constraintType = editingCol.getConstraintValueType();
        this.editField.setEnabled( constraintType != BaseSingleFieldConstraint.TYPE_PREDICATE && !isReadOnly );
        this.editOp.setEnabled( constraintType != BaseSingleFieldConstraint.TYPE_PREDICATE && !isReadOnly );
    }

    private boolean isBindingUnique(String binding) {
        return !rm.isVariableNameUsed( binding );
    }

    private void doFieldLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            if ( this.editingCol.getFactField() == null || this.editingCol.getFactField().equals( "" ) ) {
                fieldLabel.setText( Constants.INSTANCE.notNeededForPredicate() );
            } else {
                fieldLabel.setText( this.editingCol.getFactField() );
            }
            fieldLabelInterpolationInfo.setVisible( true );
        } else if ( nil( editingPattern.getFactType() ) ) {
            fieldLabel.setText( Constants.INSTANCE.pleaseSelectAPatternFirst() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else if ( nil( editingCol.getFactField() ) ) {
            fieldLabel.setText( Constants.INSTANCE.pleaseSelectAField() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else {
            fieldLabel.setText( this.editingCol.getFactField() );
        }
    }

    private void doOperatorLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            operatorLabel.setText( Constants.INSTANCE.notNeededForPredicate() );
        } else if ( nil( editingPattern.getFactType() ) ) {
            operatorLabel.setText( Constants.INSTANCE.pleaseSelectAPatternFirst() );
        } else if ( nil( editingCol.getFactField() ) ) {
            operatorLabel.setText( Constants.INSTANCE.pleaseChooseAFieldFirst() );
        } else if ( nil( editingCol.getOperator() ) ) {
            operatorLabel.setText( Constants.INSTANCE.pleaseSelectAField() );
        } else {
            operatorLabel.setText( HumanReadable.getOperatorDisplayName( editingCol.getOperator() ) );
        }
    }

    private void doPatternLabel() {
        if ( editingPattern.getFactType() != null ) {
            StringBuilder patternLabel = new StringBuilder();
            String factType = editingPattern.getFactType();
            String boundName = editingPattern.getBoundName();
            if ( factType != null && factType.length() > 0 ) {
                if ( editingPattern.isNegated() ) {
                    patternLabel.append( Constants.INSTANCE.negatedPattern() ).append( " " ).append( factType );
                } else {
                    patternLabel.append( factType ).append( " [" ).append( boundName ).append( "]" );
                }
            }
            this.patternLabel.setText( patternLabel.toString() );
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
        return new InfoPopup( Constants.INSTANCE.Predicates(),
                              Constants.INSTANCE.PredicatesInfo() );
    }

    private ListBox loadPatterns() {
        Set<String> vars = new HashSet<String>();
        ListBox patterns = new ListBox();
        for ( Pattern52 p : model.getPatterns() ) {
            if ( !vars.contains( p.getBoundName() ) ) {
                patterns.addItem( (p.isNegated() ? Constants.INSTANCE.negatedPattern() + " " : "")
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
        pop.setTitle( Constants.INSTANCE.SetTheOperator() );
        pop.setModal( false );
        String[] ops = this.sce.getOperatorCompletions( editingPattern.getFactType(),
                                                        editingCol.getFactField() );
        final CEPOperatorsDropdown box = new CEPOperatorsDropdown( ops,
                                                                   editingCol );

        if ( BaseSingleFieldConstraint.TYPE_LITERAL == this.editingCol.getConstraintValueType() ) {
            box.addItem( HumanReadable.getOperatorDisplayName( "in" ),
                         "in" );
        }

        box.addItem( Constants.INSTANCE.noOperator(),
                     "" );
        pop.addAttribute( Constants.INSTANCE.Operator(),
                          box );
        Button b = new Button( Constants.INSTANCE.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                editingCol.setOperator( box.getValue( box.getSelectedIndex() ) );
                makeLimitedValueWidget();
                doOperatorLabel();
                pop.hide();
            }
        } );
        pop.show();

    }

    private boolean unique(String header) {
        for ( CompositeColumn< ? > cc : model.getConditions() ) {
            for ( int iChild = 0; iChild < cc.getChildColumns().size(); iChild++ ) {
                if ( cc.getChildColumns().get( iChild ).getHeader().equals( header ) ) return false;
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
        Button ok = new Button( Constants.INSTANCE.OK() );
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( pats );
        hp.add( ok );

        pop.addAttribute( Constants.INSTANCE.ChooseExistingPatternToAddColumnTo(),
                          hp );
        pop.addAttribute( "",
                          new HTML( Constants.INSTANCE.ORwithEmphasis() ) );

        Button createPattern = new Button( Constants.INSTANCE.CreateNewFactPattern() );
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
                makeLimitedValueWidget();
                displayCEPOperators();
                doPatternLabel();

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
        pop.addAttribute( Constants.INSTANCE.Field(),
                          box );
        Button b = new Button( Constants.INSTANCE.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                editingCol.setFactField( box.getItemText( box.getSelectedIndex() ) );
                editingCol.setFieldType( sce.getFieldType( editingPattern.getFactType(),
                                                           editingCol.getFactField() ) );

                //Clear Operator when field changes
                editingCol.setOperator( null );

                //Setup UI
                doFieldLabel();
                makeLimitedValueWidget();
                doOperatorLabel();
                pop.hide();

            }
        } );
        pop.show();
    }

    protected void showNewPatternDialog() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( Constants.INSTANCE.CreateANewFactPattern() );
        final ListBox types = new ListBox();
        for ( int i = 0; i < sce.getFactTypes().length; i++ ) {
            types.addItem( sce.getFactTypes()[i] );
        }
        pop.addAttribute( Constants.INSTANCE.FactType(),
                          types );
        final TextBox binding = new BindingTextBox();
        binding.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                binding.setText( binding.getText().replace( " ",
                                                            "" ) );
            }
        } );
        pop.addAttribute( Constants.INSTANCE.Binding(),
                          binding );

        //Patterns can be negated, i.e. "not Pattern(...)"
        final CheckBox chkNegated = new CheckBox();
        chkNegated.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                boolean isPatternNegated = chkNegated.getValue();
                binding.setEnabled( !isPatternNegated );
            }

        } );
        pop.addAttribute( Constants.INSTANCE.negatePattern(),
                          chkNegated );

        Button ok = new Button( Constants.INSTANCE.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {

                boolean isPatternNegated = chkNegated.getValue();
                String ft = types.getItemText( types.getSelectedIndex() );
                String fn = isPatternNegated ? "" : binding.getText();
                if ( !isPatternNegated ) {
                    if ( fn.equals( "" ) ) {
                        Window.alert( Constants.INSTANCE.PleaseEnterANameForFact() );
                        return;
                    } else if ( fn.equals( ft ) ) {
                        Window.alert( Constants.INSTANCE.PleaseEnterANameThatIsNotTheSameAsTheFactType() );
                        return;
                    } else if ( !isBindingUnique( fn ) ) {
                        Window.alert( Constants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                        return;
                    }
                }

                //Create new pattern
                editingPattern = new Pattern52();
                editingPattern.setFactType( ft );
                editingPattern.setBoundName( fn );
                editingPattern.setNegated( isPatternNegated );

                //Clear Field and Operator when pattern changes
                editingCol.setFactField( null );
                editingCol.setOperator( null );

                //Set-up UI
                entryPointName.setText( editingPattern.getEntryPointName() );
                cwo.selectItem( editingPattern.getWindow().getOperator() );
                makeLimitedValueWidget();
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
        Label lbl = new Label( Constants.INSTANCE.OverCEPWindow() );
        lbl.setStyleName( "paddedLabel" );
        hp.add( lbl );

        cwo = new CEPWindowOperatorsDropdown( c,
                                              isReadOnly );
        if ( !isReadOnly ) {
            cwo.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                    OperatorSelection selection = event.getValue();
                    String selected = selection.getValue();
                    c.getWindow().setOperator( selected );
                }
            } );
        }

        hp.add( cwo );
        return hp;
    }

    private void displayCEPOperators() {
        boolean isVisible = sce.isFactTypeAnEvent( editingPattern.getFactType() );
        setAttributeVisibility( cepWindowRowIndex,
                                isVisible );
    }

}
