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

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox;
import org.drools.guvnor.client.decisiontable.DTCellValueWidgetFactory;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.WizardCellListResources;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.ActionInsertFactFieldCell;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.ActionInsertFactFieldPatternCell;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.AvailableFieldCell;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryActionInsertFactCol52;

import com.google.gwt.cell.client.TextCell;
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

/**
 * An implementation of the ActionInsertFactFields page
 */
public class ActionInsertFactFieldsPageViewImpl extends Composite
    implements
    ActionInsertFactFieldsPageView {

    private Presenter                                           presenter;

    private Validator                                           validator;

    private Set<String>                                         availableFactTypesSelections;
    private MinimumWidthCellList<String>                        availableFactTypesWidget;

    private List<ActionInsertFactFieldsPattern>                 chosenPatterns;
    private ActionInsertFactFieldsPattern                       chosenPatternsSelection;
    private Set<ActionInsertFactFieldsPattern>                  chosenPatternsSelections;
    private MinimumWidthCellList<ActionInsertFactFieldsPattern> chosenPatternsWidget;

    private Set<AvailableField>                                 availableFieldsSelections;
    private MinimumWidthCellList<AvailableField>                availableFieldsWidget;

    private List<ActionInsertFactCol52>                         chosenFields;
    private ActionInsertFactCol52                               chosenFieldsSelection;
    private Set<ActionInsertFactCol52>                          chosenFieldsSelections;
    private MinimumWidthCellList<ActionInsertFactCol52>         chosenFieldsWidget;

    private DTCellValueWidgetFactory                            factory;

    private static final Constants                              constants = GWT.create( Constants.class );

    @UiField
    protected ScrollPanel                                       availableFactTypesContainer;

    @UiField
    protected ScrollPanel                                       chosenPatternsContainer;

    @UiField
    protected ScrollPanel                                       availableFieldsContainer;

    @UiField
    protected ScrollPanel                                       chosenFieldsContainer;

    @UiField
    protected PushButton                                        btnAddFactTypes;

    @UiField
    protected PushButton                                        btnRemoveFactTypes;

    @UiField
    protected PushButton                                        btnAdd;

    @UiField
    protected PushButton                                        btnRemove;

    @UiField
    VerticalPanel                                               patternDefinition;

    @UiField
    HorizontalPanel                                             bindingContainer;

    @UiField
    BindingTextBox                                              txtBinding;

    @UiField
    VerticalPanel                                               fieldDefinition;

    @UiField
    TextBox                                                     txtColumnHeader;

    @UiField
    HorizontalPanel                                             columnHeaderContainer;

    @UiField
    TextBox                                                     txtValueList;

    @UiField
    TextBox                                                     txtDefaultValue;

    @UiField
    CheckBox                                                    chkLogicalInsert;

    @UiField
    HorizontalPanel                                             msgDuplicateBindings;

    @UiField
    HorizontalPanel                                             msgIncompleteActions;

    @UiField
    VerticalPanel                                               criteriaExtendedEntry;

    @UiField
    VerticalPanel                                               criteriaLimitedEntry;

    @UiField
    HorizontalPanel                                             limitedEntryValueContainer;

    @UiField
    SimplePanel                                                 limitedEntryValueWidgetContainer;

    interface ActionInsertFactFieldsPageWidgetBinder
        extends
        UiBinder<Widget, ActionInsertFactFieldsPageViewImpl> {
    }

    private static ActionInsertFactFieldsPageWidgetBinder uiBinder = GWT.create( ActionInsertFactFieldsPageWidgetBinder.class );

    public ActionInsertFactFieldsPageViewImpl(Validator validator) {
        this.validator = validator;
        this.availableFactTypesWidget = new MinimumWidthCellList<String>( new TextCell(),
                                                                          WizardCellListResources.INSTANCE );
        this.chosenPatternsWidget = new MinimumWidthCellList<ActionInsertFactFieldsPattern>( new ActionInsertFactFieldPatternCell( validator ),
                                                                                             WizardCellListResources.INSTANCE );
        this.availableFieldsWidget = new MinimumWidthCellList<AvailableField>( new AvailableFieldCell(),
                                                                               WizardCellListResources.INSTANCE );
        this.chosenFieldsWidget = new MinimumWidthCellList<ActionInsertFactCol52>( new ActionInsertFactFieldCell( validator ),
                                                                                   WizardCellListResources.INSTANCE );

        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseAvailableFactTypes();
        initialiseChosenPatterns();
        initialiseAvailableFields();
        initialiseChosenFields();
        initialiseBinding();
        initialiseLogicalInsert();
        initialiseColumnHeader();
        initialiseDefaultValue();
        initialiseValueList();
    }

    private void initialiseAvailableFactTypes() {
        availableFactTypesContainer.add( availableFactTypesWidget );
        availableFactTypesWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableFactTypesWidget.setMinimumWidth( 155 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoAvailablePatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availableFactTypesWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<String> selectionModel = new MultiSelectionModel<String>();
        availableFactTypesWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                availableFactTypesSelections = selectionModel.getSelectedSet();
                btnAddFactTypes.setEnabled( availableFactTypesSelections.size() > 0 );
            }

        } );
    }

    private void initialiseChosenPatterns() {
        chosenPatternsContainer.add( chosenPatternsWidget );
        chosenPatternsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenPatternsWidget.setMinimumWidth( 155 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoChosenPatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenPatternsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<ActionInsertFactFieldsPattern> selectionModel = new MultiSelectionModel<ActionInsertFactFieldsPattern>();
        chosenPatternsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenPatternsSelections = selectionModel.getSelectedSet();
                chosenPatternsSelected( chosenPatternsSelections );
            }

            private void chosenPatternsSelected(Set<ActionInsertFactFieldsPattern> cps) {
                btnRemoveFactTypes.setEnabled( cps.size() > 0 );
                fieldDefinition.setVisible( false );
                if ( cps.size() == 1 ) {
                    chosenPatternsSelection = cps.iterator().next();
                    presenter.selectPattern( chosenPatternsSelection );
                    patternDefinition.setVisible( true );
                    validateBinding();
                    txtBinding.setEnabled( true );
                    txtBinding.setVisible( true );
                    txtBinding.setText( chosenPatternsSelection.getBoundName() );
                    chkLogicalInsert.setEnabled( true );
                    chkLogicalInsert.setVisible( true );
                    chkLogicalInsert.setValue( chosenPatternsSelection.isInsertedLogically() );

                } else {
                    chosenPatternsSelection = null;
                    setAvailableFields( new ArrayList<AvailableField>() );
                    setChosenFields( new ArrayList<ActionInsertFactCol52>() );
                    patternDefinition.setVisible( false );
                    txtBinding.setEnabled( false );
                    txtBinding.setVisible( false );
                    txtBinding.setText( "" );
                    chkLogicalInsert.setEnabled( false );
                    chkLogicalInsert.setVisible( false );
                }
            }

        } );

    }

    private void initialiseAvailableFields() {
        availableFieldsContainer.add( availableFieldsWidget );
        availableFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableFieldsWidget.setMinimumWidth( 155 );

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
        chosenFieldsWidget.setMinimumWidth( 155 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoChosenFields() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenFieldsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<ActionInsertFactCol52> selectionModel = new MultiSelectionModel<ActionInsertFactCol52>();
        chosenFieldsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenFieldsSelections = new HashSet<ActionInsertFactCol52>();
                Set<ActionInsertFactCol52> selections = selectionModel.getSelectedSet();
                for ( ActionInsertFactCol52 a : selections ) {
                    chosenFieldsSelections.add( a );
                }
                chosenConditionsSelected( chosenFieldsSelections );
            }

            private void chosenConditionsSelected(Set<ActionInsertFactCol52> cws) {
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
                }
            }

            private void populateFieldDefinition() {

                // Fields common to all table formats
                txtColumnHeader.setEnabled( true );
                txtColumnHeader.setText( chosenFieldsSelection.getHeader() );

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
                if ( !(chosenFieldsSelection instanceof LimitedEntryActionInsertFactCol52) ) {
                    return;
                }
                LimitedEntryActionInsertFactCol52 lea = (LimitedEntryActionInsertFactCol52) chosenFieldsSelection;
                if ( lea.getValue() == null ) {
                    lea.setValue( factory.makeNewValue( chosenFieldsSelection ) );
                }
                limitedEntryValueWidgetContainer.setWidget( factory.getWidget( chosenFieldsSelection,
                                                                               lea.getValue() ) );
            }

        } );
    }

    private void initialiseBinding() {
        txtBinding.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String binding = txtBinding.getText();
                chosenPatternsSelection.setBoundName( binding );
                presenter.stateChanged();
                validateBinding();
            }

        } );
    }

    private void validateBinding() {
        if ( chosenPatternsSelection == null ) {
            return;
        }
        if ( validator.isPatternBindingUnique( chosenPatternsSelection ) ) {
            bindingContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerValid() );
        } else {
            bindingContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerInvalid() );
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

    private void validateFieldHeader() {
        if ( validator.isActionHeaderValid( chosenFieldsSelection ) ) {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerValid() );
        } else {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerInvalid() );
        }
    }

    private void initialiseDefaultValue() {
        txtDefaultValue.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String defaultValue = txtDefaultValue.getText();
                chosenFieldsSelection.setDefaultValue( defaultValue );
                //DefaultValue is optional, no need to advise of state change
            }

        } );
    }

    private void initialiseValueList() {
        txtValueList.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String valueList = txtValueList.getText();
                chosenFieldsSelection.setValueList( valueList );
                //ValueList is optional, no need to advise of state change
            }

        } );

    }

    private void initialiseLogicalInsert() {
        chkLogicalInsert.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                chosenPatternsSelection.setInsertedLogically( chkLogicalInsert.getValue() );
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
        chosenPatternsWidget.redraw();
        validateBinding();
    }

    public void setAreActionInsertFactFieldsDefined(boolean areActionInsertFactFieldsDefined) {
        msgIncompleteActions.setVisible( !areActionInsertFactFieldsDefined );
        chosenPatternsWidget.redraw();
        chosenFieldsWidget.redraw();
    }

    public void setAvailableFactTypes(List<String> types) {
        availableFactTypesWidget.setRowCount( types.size(),
                                              true );
        availableFactTypesWidget.setRowData( types );
    }

    public void setChosenPatterns(List<ActionInsertFactFieldsPattern> patterns) {
        chosenPatterns = patterns;
        chosenPatternsWidget.setRowCount( chosenPatterns.size(),
                                          true );
        chosenPatternsWidget.setRowData( chosenPatterns );
    }

    public void setAvailableFields(List<AvailableField> fields) {
        availableFieldsWidget.setRowCount( fields.size(),
                                           true );
        availableFieldsWidget.setRowData( fields );
    }

    public void setChosenFields(List<ActionInsertFactCol52> fields) {
        chosenFields = fields;
        chosenFieldsWidget.setRowCount( fields.size(),
                                        true );
        chosenFieldsWidget.setRowData( fields );
        fieldDefinition.setVisible( fields.contains( chosenFieldsSelection ) );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnAddFactTypes")
    public void btnAddFactTypesClick(ClickEvent event) {
        for ( String type : availableFactTypesSelections ) {
            ActionInsertFactFieldsPattern pattern = new ActionInsertFactFieldsPattern();
            pattern.setFactType( type );
            chosenPatterns.add( pattern );
            presenter.addPattern( pattern );
        }
        setChosenPatterns( chosenPatterns );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnRemoveFactTypes")
    public void btnRemoveFactTypesClick(ClickEvent event) {
        for ( ActionInsertFactFieldsPattern p : chosenPatternsSelections ) {
            chosenPatterns.remove( p );
            presenter.removePattern( p );
        }
        chosenPatternsSelection = null;
        setChosenPatterns( chosenPatterns );
        setAvailableFields( new ArrayList<AvailableField>() );
        setChosenFields( new ArrayList<ActionInsertFactCol52>() );
        presenter.stateChanged();

        txtBinding.setText( "" );
        txtBinding.setEnabled( false );
        btnRemoveFactTypes.setEnabled( false );
        patternDefinition.setVisible( false );
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( AvailableField f : availableFieldsSelections ) {
            chosenFields.add( makeNewActionColumn( f ) );
        }
        setChosenFields( chosenFields );
        presenter.stateChanged();
    }

    private ActionInsertFactCol52 makeNewActionColumn(AvailableField f) {
        TableFormat format = presenter.getTableFormat();
        if ( format == TableFormat.EXTENDED_ENTRY ) {
            ActionInsertFactCol52 a = new ActionInsertFactCol52();
            a.setBoundName( chosenPatternsSelection.getBoundName() );
            a.setFactType( chosenPatternsSelection.getFactType() );
            a.setFactField( f.getName() );
            a.setType( f.getType() );
            return a;
        } else {
            LimitedEntryActionInsertFactCol52 a = new LimitedEntryActionInsertFactCol52();
            a.setBoundName( chosenPatternsSelection.getBoundName() );
            a.setFactType( chosenPatternsSelection.getFactType() );
            a.setFactField( f.getName() );
            a.setType( f.getType() );
            return a;
        }

    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        for ( ActionInsertFactCol52 a : chosenFieldsSelections ) {
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
