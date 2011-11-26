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
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.decisiontable.DTCellValueWidgetFactory;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.WizardCellListResources;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.ActionSetFieldCell;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.ActionSetFieldPatternCell;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.AvailableFieldCell;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionSetFieldCol52;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * An implementation of the ActionSetFields page
 */
public class ActionSetFieldsPageViewImpl extends Composite
    implements
    ActionSetFieldsPageView {

    private Presenter                                 presenter;

    private Validator                                 validator;

    private List<Pattern52>                           availablePatterns;
    private Pattern52                                 availablePatternsSelection;
    private MinimumWidthCellList<Pattern52>           availablePatternsWidget;

    private Set<AvailableField>                       availableFieldsSelections;
    private MinimumWidthCellList<AvailableField>      availableFieldsWidget;

    private List<ActionSetFieldCol52>                 chosenFields;
    private ActionSetFieldCol52                       chosenFieldsSelection;
    private Set<ActionSetFieldCol52>                  chosenFieldsSelections;
    private MinimumWidthCellList<ActionSetFieldCol52> chosenFieldsWidget;

    private DTCellValueWidgetFactory                  factory;

    private static final Constants                    constants = GWT.create( Constants.class );

    @UiField
    protected ScrollPanel                             availablePatternsContainer;

    @UiField
    protected ScrollPanel                             availableFieldsContainer;

    @UiField
    protected ScrollPanel                             chosenFieldsContainer;

    @UiField
    protected PushButton                              btnAdd;

    @UiField
    protected PushButton                              btnRemove;

    @UiField
    VerticalPanel                                     fieldDefinition;

    @UiField
    TextBox                                           txtColumnHeader;

    @UiField
    HorizontalPanel                                   columnHeaderContainer;

    @UiField
    TextBox                                           txtValueList;

    @UiField
    TextBox                                           txtDefaultValue;

    @UiField
    CheckBox                                          chkUpdateEngine;

    @UiField
    HorizontalPanel                                   msgDuplicateBindings;

    @UiField
    HorizontalPanel                                   msgIncompleteActionSetFields;

    @UiField
    VerticalPanel                                     criteriaExtendedEntry;

    @UiField
    VerticalPanel                                     criteriaLimitedEntry;

    @UiField
    HorizontalPanel                                   limitedEntryValueContainer;

    @UiField
    SimplePanel                                       limitedEntryValueWidgetContainer;

    interface ActionSetFieldPageWidgetBinder
        extends
        UiBinder<Widget, ActionSetFieldsPageViewImpl> {
    }

    private static ActionSetFieldPageWidgetBinder uiBinder = GWT.create( ActionSetFieldPageWidgetBinder.class );

    public ActionSetFieldsPageViewImpl(Validator validator) {
        this.validator = validator;
        this.availablePatternsWidget = new MinimumWidthCellList<Pattern52>( new ActionSetFieldPatternCell( validator ),
                                                                            WizardCellListResources.INSTANCE );
        this.availableFieldsWidget = new MinimumWidthCellList<AvailableField>( new AvailableFieldCell(),
                                                                               WizardCellListResources.INSTANCE );
        this.chosenFieldsWidget = new MinimumWidthCellList<ActionSetFieldCol52>( new ActionSetFieldCell( validator ),
                                                                                 WizardCellListResources.INSTANCE );

        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseAvailablePatterns();
        initialiseAvailableFields();
        initialiseChosenFields();
        initialiseColumnHeader();
        initialiseDefaultValue();
        initialiseValueList();
        initialiseUpdateEngine();
    }

    private void initialiseAvailablePatterns() {
        availablePatternsContainer.add( availablePatternsWidget );
        availablePatternsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availablePatternsWidget.setMinimumWidth( 180 );

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
        chosenFieldsContainer.add( chosenFieldsWidget );
        chosenFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenFieldsWidget.setMinimumWidth( 175 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoChosenFields() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenFieldsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<ActionSetFieldCol52> selectionModel = new MultiSelectionModel<ActionSetFieldCol52>();
        chosenFieldsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenFieldsSelections = selectionModel.getSelectedSet();
                chosenConditionsSelected( chosenFieldsSelections );
            }

            private void chosenConditionsSelected(Set<ActionSetFieldCol52> cws) {
                btnRemove.setEnabled( true );
                if ( cws.size() == 1 ) {
                    chosenFieldsSelection = cws.iterator().next();
                    fieldDefinition.setVisible( true );
                    validateFieldHeader();
                    populateFieldDefinition();
                } else {
                    chosenFieldsSelection = null;
                    fieldDefinition.setVisible( false );
                    txtColumnHeader.setEnabled( false );
                    txtValueList.setEnabled( false );
                    txtDefaultValue.setEnabled( false );
                    chkUpdateEngine.setEnabled( false );
                }
            }

            private void populateFieldDefinition() {

                // Fields common to all table formats
                txtColumnHeader.setEnabled( true );
                chkUpdateEngine.setEnabled( true );
                txtColumnHeader.setText( chosenFieldsSelection.getHeader() );
                chkUpdateEngine.setValue( chosenFieldsSelection.isUpdate() );

                criteriaExtendedEntry.setVisible( presenter.getTableFormat() == TableFormat.EXTENDED_ENTRY );
                criteriaLimitedEntry.setVisible( presenter.getTableFormat() == TableFormat.LIMITED_ENTRY );

                // Fields specific to the table format
                switch ( presenter.getTableFormat() ) {
                    case EXTENDED_ENTRY :
                        txtDefaultValue.setEnabled( true );
                        txtValueList.setEnabled( true );
                        txtDefaultValue.setText( chosenFieldsSelection.getDefaultValue() );
                        txtValueList.setText( chosenFieldsSelection.getValueList() );
                        break;
                    case LIMITED_ENTRY :
                        makeLimitedValueWidget();
                        limitedEntryValueContainer.setVisible( true );
                        break;
                }
            }

            private void makeLimitedValueWidget() {
                if ( !(chosenFieldsSelection instanceof LimitedEntryActionSetFieldCol52) ) {
                    return;
                }
                LimitedEntryActionSetFieldCol52 lea = (LimitedEntryActionSetFieldCol52) chosenFieldsSelection;
                if ( lea.getValue() == null ) {
                    lea.setValue( factory.makeNewValue( availablePatternsSelection,
                                                        chosenFieldsSelection ) );
                }
                limitedEntryValueWidgetContainer.setWidget( factory.getWidget( availablePatternsSelection,
                                                                               chosenFieldsSelection,
                                                                               lea.getValue() ) );
            }

        } );
    }

    private void validateFieldHeader() {
        if ( validator.isActionHeaderValid( chosenFieldsSelection ) ) {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerValid() );
        } else {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerInvalid() );
        }
    }

    private void initialiseColumnHeader() {
        txtColumnHeader.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String header = txtColumnHeader.getText();
                chosenFieldsSelection.setHeader( header );
                presenter.stateChanged();
                validateFieldHeader();
            }

        } );
    }

    private void initialiseDefaultValue() {
        txtDefaultValue.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String defaultValue = txtDefaultValue.getText();
                chosenFieldsSelection.setDefaultValue( defaultValue );
                // DefaultValue is optional, no need to advise of state change
            }

        } );
    }

    private void initialiseValueList() {
        txtValueList.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String valueList = txtValueList.getText();
                chosenFieldsSelection.setValueList( valueList );
                // ValueList is optional, no need to advise of state change
            }

        } );

    }

    private void initialiseUpdateEngine() {
        chkUpdateEngine.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                chosenFieldsSelection.setUpdate( chkUpdateEngine.getValue() );
            }

        } );
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setDTCellValueWidgetFactory(DTCellValueWidgetFactory factory) {
        this.factory = factory;
    }

    public void setArePatternBindingsUnique(boolean arePatternBindingsUnique) {
        msgDuplicateBindings.setVisible( !arePatternBindingsUnique );
        availablePatternsWidget.redraw();
    }

    public void setAreActionSetFieldsDefined(boolean areActionSetFieldsDefined) {
        msgIncompleteActionSetFields.setVisible( !areActionSetFieldsDefined );
        chosenFieldsWidget.redraw();
    }

    public void setHasIncompleteFieldDefinitions(boolean hasIncompleteFieldDefinitions) {
    }

    public void setAvailablePatterns(List<Pattern52> patterns) {
        availablePatterns = patterns;
        availablePatternsWidget.setRowCount( availablePatterns.size(),
                                             true );
        availablePatternsWidget.setRowData( availablePatterns );

        if ( availablePatternsSelection != null ) {

            // If the currently selected pattern is no longer available clear selections
            if ( !availablePatterns.contains( availablePatternsSelection ) ) {
                setAvailableFields( new ArrayList<AvailableField>() );
                availablePatternsSelection = null;
                setChosenFields( new ArrayList<ActionSetFieldCol52>() );
                chosenFieldsSelection = null;
                fieldDefinition.setVisible( false );
                msgIncompleteActionSetFields.setVisible( false );
            }
        } else {

            // If no available pattern is selected clear fields
            setAvailableFields( new ArrayList<AvailableField>() );
            setChosenFields( new ArrayList<ActionSetFieldCol52>() );
        }
    }

    public void setAvailableFields(List<AvailableField> fields) {
        availableFieldsWidget.setRowCount( fields.size(),
                                           true );
        availableFieldsWidget.setRowData( fields );
    }

    public void setChosenFields(List<ActionSetFieldCol52> fields) {
        chosenFields = fields;
        chosenFieldsWidget.setRowCount( fields.size(),
                                        true );
        chosenFieldsWidget.setRowData( fields );
        fieldDefinition.setVisible( fields.contains( chosenFieldsSelection ) );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( AvailableField f : availableFieldsSelections ) {
            chosenFields.add( makeNewActionColumn( f ) );
        }
        setChosenFields( chosenFields );
        presenter.stateChanged();
    }

    private ActionSetFieldCol52 makeNewActionColumn(AvailableField f) {
        TableFormat format = presenter.getTableFormat();
        if ( format == TableFormat.EXTENDED_ENTRY ) {
            ActionSetFieldCol52 a = new ActionSetFieldCol52();
            a.setBoundName( availablePatternsSelection.getBoundName() );
            a.setFactField( f.getName() );
            a.setType( f.getType() );
            return a;
        } else {
            LimitedEntryActionSetFieldCol52 a = new LimitedEntryActionSetFieldCol52();
            a.setBoundName( availablePatternsSelection.getBoundName() );
            a.setFactField( f.getName() );
            a.setType( f.getType() );
            return a;
        }

    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        for ( ActionSetFieldCol52 a : chosenFieldsSelections ) {
            chosenFields.remove( a );
        }
        chosenFieldsSelections.clear();
        setChosenFields( chosenFields );
        presenter.stateChanged();

        txtColumnHeader.setText( "" );
        txtValueList.setText( "" );
        txtDefaultValue.setText( "" );
        fieldDefinition.setVisible( false );
        btnRemove.setEnabled( false );
    }

}
