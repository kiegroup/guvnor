/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.HumanReadable;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPOperatorsDropdown;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.OperatorSelection;
import org.drools.guvnor.client.decisiontable.DTCellValueWidgetFactory;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.resources.WizardCellListResources;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.AvailableFieldCell;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.ConditionCell;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.ConditionPatternCell;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * An implementation of the Fact Patterns Constraints page
 */
public class FactPatternConstraintsPageViewImpl extends Composite
    implements
    FactPatternConstraintsPageView {

    private Presenter                            presenter;

    private Validator                            validator;

    private List<Pattern52>                      availablePatterns;
    private Pattern52                            availablePatternsSelection;
    private MinimumWidthCellList<Pattern52>      availablePatternsWidget;

    private Set<AvailableField>                  availableFieldsSelections;
    private MinimumWidthCellList<AvailableField> availableFieldsWidget;

    private List<ConditionCol52>                 chosenConditions;
    private ConditionCol52                       chosenConditionsSelection;
    private Set<ConditionCol52>                  chosenConditionsSelections;
    private MinimumWidthCellList<ConditionCol52> chosenConditionsWidget;

    private boolean                              isOperatorValid;

    private DTCellValueWidgetFactory             factory;

    private static final Constants               constants   = GWT.create( Constants.class );

    private static final Images                  images      = GWT.create( Images.class );

    @UiField
    protected ScrollPanel                        availablePatternsContainer;

    @UiField
    protected ScrollPanel                        availableFieldsContainer;

    @UiField
    protected ScrollPanel                        chosenConditionsContainer;

    @UiField
    protected PushButton                         btnAdd;

    @UiField
    protected PushButton                         btnRemove;

    @UiField
    VerticalPanel                                conditionDefinition;

    @UiField
    HorizontalPanel                              calculationType;

    @UiField
    RadioButton                                  optLiteral;

    @UiField
    RadioButton                                  optFormula;

    @UiField
    RadioButton                                  optPredicate;

    @UiField
    TextBox                                      txtColumnHeader;

    @UiField
    HorizontalPanel                              columnHeaderContainer;

    @UiField
    TextBox                                      txtPredicateExpression;

    @UiField
    HorizontalPanel                              predicateExpressionContainer;

    @UiField
    HorizontalPanel                              operatorContainer;

    @UiField
    SimplePanel                                  ddOperatorContainer;

    @UiField
    TextBox                                      txtValueList;

    @UiField
    TextBox                                      txtDefaultValue;

    @UiField
    HorizontalPanel                              msgDuplicateBindings;

    @UiField
    HorizontalPanel                              msgIncompleteConditions;

    @UiField
    VerticalPanel                                criteriaExtendedEntry;

    @UiField
    VerticalPanel                                criteriaLimitedEntry;

    @UiField
    HorizontalPanel                              limitedEntryValueContainer;

    @UiField
    SimplePanel                                  limitedEntryValueWidgetContainer;

    @UiField(provided = true)
    PushButton                                   btnMoveUp   = new PushButton( AbstractImagePrototype.create( images.shuffleUp() ).createImage() );

    @UiField(provided = true)
    PushButton                                   btnMoveDown = new PushButton( AbstractImagePrototype.create( images.shuffleDown() ).createImage() );

    interface FactPatternConstraintsPageWidgetBinder
        extends
        UiBinder<Widget, FactPatternConstraintsPageViewImpl> {
    }

    private static FactPatternConstraintsPageWidgetBinder uiBinder = GWT.create( FactPatternConstraintsPageWidgetBinder.class );

    public FactPatternConstraintsPageViewImpl(Validator validator) {
        this.validator = validator;
        this.availablePatternsWidget = new MinimumWidthCellList<Pattern52>( new ConditionPatternCell( validator ),
                                                                            WizardCellListResources.INSTANCE );
        this.availableFieldsWidget = new MinimumWidthCellList<AvailableField>( new AvailableFieldCell(),
                                                                               WizardCellListResources.INSTANCE );
        this.chosenConditionsWidget = new MinimumWidthCellList<ConditionCol52>( new ConditionCell( validator ),
                                                                                WizardCellListResources.INSTANCE );

        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseAvailablePatterns();
        initialiseAvailableFields();
        initialiseChosenFields();
        initialiseCalculationTypes();
        initialiseColumnHeader();
        initialisePredicateExpression();
        initialiseDefaultValue();
        initialiseValueList();
        initialiseShufflers();
    }

    private void initialiseAvailablePatterns() {
        availablePatternsContainer.add( availablePatternsWidget );
        availablePatternsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availablePatternsWidget.setMinimumWidth( 175 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoAvailablePatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availablePatternsWidget.setEmptyListWidget( lstEmpty );

        final SingleSelectionModel<Pattern52> selectionModel = new SingleSelectionModel<Pattern52>();
        availablePatternsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                availablePatternsSelection = selectionModel.getSelectedObject();
                presenter.selectPattern( availablePatternsSelection );
            }

        } );
    }

    private void initialiseAvailableFields() {
        availableFieldsContainer.add( availableFieldsWidget );
        availableFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableFieldsWidget.setMinimumWidth( 175 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoAvailableFields() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availableFieldsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<AvailableField> selectionModel = new MultiSelectionModel<AvailableField>();
        availableFieldsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                availableFieldsSelections = selectionModel.getSelectedSet();
                btnAdd.setEnabled( availableFieldsSelections.size() > 0 );
            }

        } );
    }

    private void initialiseChosenFields() {
        chosenConditionsContainer.add( chosenConditionsWidget );
        chosenConditionsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenConditionsWidget.setMinimumWidth( 175 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoChosenFields() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenConditionsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<ConditionCol52> selectionModel = new MultiSelectionModel<ConditionCol52>();
        chosenConditionsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenConditionsSelections = new HashSet<ConditionCol52>();
                Set<ConditionCol52> selections = selectionModel.getSelectedSet();
                for ( ConditionCol52 c : selections ) {
                    chosenConditionsSelections.add( c );
                }
                chosenConditionsSelected( chosenConditionsSelections );
            }

            private void chosenConditionsSelected(Set<ConditionCol52> cws) {
                btnRemove.setEnabled( true );
                if ( cws.size() == 1 ) {
                    chosenConditionsSelection = cws.iterator().next();
                    conditionDefinition.setVisible( true );
                    validateConditionHeader();
                    validateConditionOperator();
                    populateConditionDefinition();
                    enableMoveUpButton();
                    enableMoveDownButton();
                } else {
                    chosenConditionsSelection = null;
                    conditionDefinition.setVisible( false );
                    optLiteral.setEnabled( false );
                    optFormula.setEnabled( false );
                    optPredicate.setEnabled( false );
                    txtColumnHeader.setEnabled( false );
                    txtValueList.setEnabled( false );
                    txtDefaultValue.setEnabled( false );
                    btnMoveUp.setEnabled( false );
                    btnMoveDown.setEnabled( false );
                }
            }

            private void displayCalculationTypes(boolean isPredicate) {
                calculationType.setVisible( !isPredicate );
                optLiteral.setEnabled( !isPredicate );
                optLiteral.setVisible( !isPredicate );
                optFormula.setEnabled( !isPredicate );
                optFormula.setVisible( !isPredicate );
                operatorContainer.setVisible( !isPredicate );
                optPredicate.setEnabled( isPredicate );
                optPredicate.setVisible( isPredicate );
                txtPredicateExpression.setEnabled( isPredicate );
                predicateExpressionContainer.setVisible( isPredicate );
            }

            private void populateConditionDefinition() {

                // Fields common to all table formats
                txtColumnHeader.setEnabled( true );
                txtColumnHeader.setText( chosenConditionsSelection.getHeader() );

                String[] ops = presenter.getOperatorCompletions( availablePatternsSelection,
                                                                 chosenConditionsSelection );
                CEPOperatorsDropdown ddOperator = new CEPOperatorsDropdown( ops,
                                                                            chosenConditionsSelection );
                ddOperatorContainer.setWidget( ddOperator );

                criteriaExtendedEntry.setVisible( presenter.getTableFormat() == TableFormat.EXTENDED_ENTRY );
                criteriaLimitedEntry.setVisible( presenter.getTableFormat() == TableFormat.LIMITED_ENTRY );

                // Fields specific to the table format
                switch ( presenter.getTableFormat() ) {
                    case EXTENDED_ENTRY :
                        txtDefaultValue.setEnabled( true );
                        txtDefaultValue.setText( chosenConditionsSelection.getDefaultValue() );
                        txtValueList.setEnabled( true );
                        txtValueList.setText( chosenConditionsSelection.getValueList() );
                        if ( chosenConditionsSelection.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                            txtPredicateExpression.setText( chosenConditionsSelection.getFactField() );
                        }
                        if ( chosenConditionsSelection.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
                            ddOperator.addItem( HumanReadable.getOperatorDisplayName( "in" ),
                                                "in" );
                        }

                        ddOperator.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                            public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                                chosenConditionsSelection.setOperator( event.getValue().getValue() );
                                presenter.stateChanged();
                                validateConditionOperator();
                            }

                        } );

                        switch ( chosenConditionsSelection.getConstraintValueType() ) {
                            case BaseSingleFieldConstraint.TYPE_LITERAL :
                                optLiteral.setValue( true );
                                displayCalculationTypes( false );
                                break;
                            case BaseSingleFieldConstraint.TYPE_RET_VALUE :
                                optFormula.setValue( true );
                                displayCalculationTypes( false );
                                break;
                            case BaseSingleFieldConstraint.TYPE_PREDICATE :
                                optPredicate.setValue( true );
                                displayCalculationTypes( true );
                        }
                        break;
                    case LIMITED_ENTRY :
                        calculationType.setVisible( false );
                        makeLimitedValueWidget();

                        // If operator changes the widget used to populate the
                        // value can change
                        ddOperator.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                            public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                                chosenConditionsSelection.setOperator( event.getValue().getValue() );
                                validateConditionOperator();
                                makeLimitedValueWidget();
                                presenter.stateChanged();
                            }

                        } );
                        break;
                }

            }

            private void makeLimitedValueWidget() {
                if ( !(chosenConditionsSelection instanceof LimitedEntryConditionCol52) ) {
                    return;
                }
                LimitedEntryConditionCol52 lec = (LimitedEntryConditionCol52) chosenConditionsSelection;
                boolean doesOperatorNeedValue = validator.doesOperatorNeedValue( chosenConditionsSelection );
                if ( !doesOperatorNeedValue ) {
                    limitedEntryValueContainer.setVisible( false );
                    lec.setValue( null );
                    return;
                }
                limitedEntryValueContainer.setVisible( true );
                if ( lec.getValue() == null ) {
                    lec.setValue( factory.makeNewValue( chosenConditionsSelection ) );
                }
                limitedEntryValueWidgetContainer.setWidget( factory.getWidget( chosenConditionsSelection,
                                                                               lec.getValue() ) );
            }

        } );
    }

    private void validateConditionHeader() {
        if ( validator.isConditionHeaderValid( chosenConditionsSelection ) ) {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerValid() );
        } else {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerInvalid() );
        }
    }

    private void validateConditionOperator() {
        isOperatorValid = validator.isConditionOperatorValid( chosenConditionsSelection );
        if ( isOperatorValid ) {
            operatorContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerValid() );
        } else {
            operatorContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerInvalid() );
        }
    }

    private void initialiseCalculationTypes() {
        optLiteral.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                chosenConditionsSelection.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
                chosenConditionsWidget.redraw();
            }
        } );

        optFormula.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                chosenConditionsSelection.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
                chosenConditionsWidget.redraw();
            }
        } );
        optPredicate.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent w) {
                chosenConditionsSelection.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
                chosenConditionsWidget.redraw();
            }
        } );

    }

    private void initialiseColumnHeader() {
        txtColumnHeader.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String header = txtColumnHeader.getText();
                chosenConditionsSelection.setHeader( header );
                presenter.stateChanged();
                validateConditionHeader();
            }

        } );
    }

    private void initialisePredicateExpression() {
        txtPredicateExpression.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String expression = txtPredicateExpression.getText();
                chosenConditionsSelection.setFactField( expression );

                // Redraw list widget that shows Predicate expressions
                chosenConditionsWidget.redraw();

            }

        } );
    }

    private void initialiseDefaultValue() {
        txtDefaultValue.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String defaultValue = txtDefaultValue.getText();
                chosenConditionsSelection.setDefaultValue( defaultValue );
                // DefaultValue is optional, no need to advise of state change
            }

        } );
    }

    private void initialiseValueList() {
        txtValueList.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String valueList = txtValueList.getText();
                chosenConditionsSelection.setValueList( valueList );
                // ValueList is optional, no need to advise of state change
            }

        } );

    }

    private void initialiseShufflers() {
        btnMoveUp.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenConditions.indexOf( chosenConditionsSelection );
                ConditionCol52 c = chosenConditions.remove( index );
                chosenConditions.add( index - 1,
                                      c );
                setChosenConditions( chosenConditions );
                availablePatternsSelection.setConditions( chosenConditions );
            }

        } );
        btnMoveDown.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenConditions.indexOf( chosenConditionsSelection );
                ConditionCol52 c = chosenConditions.remove( index );
                chosenConditions.add( index + 1,
                                      c );
                setChosenConditions( chosenConditions );
                availablePatternsSelection.setConditions( chosenConditions );
            }

        } );
    }

    private void enableMoveUpButton() {
        if ( chosenConditions == null || chosenConditionsSelection == null ) {
            btnMoveUp.setEnabled( false );
            return;
        }
        int index = chosenConditions.indexOf( chosenConditionsSelection );
        btnMoveUp.setEnabled( index > 0 );
    }

    private void enableMoveDownButton() {
        if ( chosenConditions == null || chosenConditionsSelection == null ) {
            btnMoveDown.setEnabled( false );
            return;
        }
        int index = chosenConditions.indexOf( chosenConditionsSelection );
        btnMoveDown.setEnabled( index < chosenConditions.size() - 1 );
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setDTCellValueWidgetFactory(DTCellValueWidgetFactory factory) {
        this.factory = factory;
    }

    public void setAreConditionsDefined(boolean areConditionsDefined) {
        msgIncompleteConditions.setVisible( !areConditionsDefined );
        chosenConditionsWidget.redraw();
        availablePatternsWidget.redraw();
    }

    public void setArePatternBindingsUnique(boolean arePatternBindingsUnique) {
        msgDuplicateBindings.setVisible( !arePatternBindingsUnique );
        availablePatternsWidget.redraw();
    }

    public void setAvailablePatterns(List<Pattern52> patterns) {
        availablePatterns = patterns;
        availablePatternsWidget.setRowCount( availablePatterns.size(),
                                             true );
        availablePatternsWidget.setRowData( availablePatterns );

        if ( availablePatternsSelection != null ) {

            // If the currently selected pattern is no longer available clear
            // selections
            if ( !availablePatterns.contains( availablePatternsSelection ) ) {
                setAvailableFields( new ArrayList<AvailableField>() );
                availablePatternsSelection = null;
                setChosenConditions( new ArrayList<ConditionCol52>() );
                chosenConditionsSelection = null;
                conditionDefinition.setVisible( false );
                msgIncompleteConditions.setVisible( false );
            }
        } else {

            // If no available pattern is selected clear fields
            setAvailableFields( new ArrayList<AvailableField>() );
            setChosenConditions( new ArrayList<ConditionCol52>() );
        }
    }

    public void setAvailableFields(List<AvailableField> fields) {
        availableFieldsWidget.setRowCount( fields.size(),
                                           true );
        availableFieldsWidget.setRowData( fields );
    }

    public void setChosenConditions(List<ConditionCol52> conditions) {
        chosenConditions = conditions;
        chosenConditionsWidget.setRowCount( conditions.size(),
                                            true );
        chosenConditionsWidget.setRowData( conditions );
        conditionDefinition.setVisible( conditions.contains( chosenConditionsSelection ) );
        enableMoveUpButton();
        enableMoveDownButton();
        presenter.stateChanged();
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( AvailableField f : availableFieldsSelections ) {
            chosenConditions.add( makeNewConditionColumn( f ) );
        }
        setChosenConditions( chosenConditions );
        availablePatternsSelection.setConditions( chosenConditions );
        presenter.stateChanged();
    }

    private ConditionCol52 makeNewConditionColumn(AvailableField f) {
        TableFormat format = presenter.getTableFormat();
        if ( format == TableFormat.EXTENDED_ENTRY ) {
            ConditionCol52 c = new ConditionCol52();
            c.setFactField( f.getName() );
            c.setFieldType( f.getType() );
            c.setConstraintValueType( f.getCalculationType() );
            return c;
        } else {
            LimitedEntryConditionCol52 c = new LimitedEntryConditionCol52();
            c.setFactField( f.getName() );
            c.setFieldType( f.getType() );
            c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
            return c;
        }

    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        for ( ConditionCol52 c : chosenConditionsSelections ) {
            chosenConditions.remove( c );
        }
        chosenConditionsSelections.clear();
        setChosenConditions( chosenConditions );
        availablePatternsSelection.setConditions( chosenConditions );
        presenter.stateChanged();

        txtColumnHeader.setText( "" );
        txtValueList.setText( "" );
        txtDefaultValue.setText( "" );
        conditionDefinition.setVisible( false );
        btnRemove.setEnabled( false );
    }

}
