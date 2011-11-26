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

import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.BindingTextBox;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.CEPWindowOperatorsDropdown;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.OperatorSelection;
import org.drools.guvnor.client.decisiontable.Validator;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.resources.WizardCellListResources;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable.cells.PatternCell;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * An implementation of the Fact Patterns page
 */
public class FactPatternsPageViewImpl extends Composite
    implements
    FactPatternsPageView {

    private Presenter                       presenter;

    private Validator                       validator;

    private GuidedDecisionTable52           dtable;

    private Set<String>                     availableTypesSelections;
    private MinimumWidthCellList<String>    availableTypesWidget;

    private List<Pattern52>                 chosenPatterns;
    private Pattern52                       chosenPatternSelection;
    private Set<Pattern52>                  chosenPatternSelections;
    private MinimumWidthCellList<Pattern52> chosenPatternWidget;

    private static final Constants          constants   = GWT.create( Constants.class );

    private static final Images             images      = GWT.create( Images.class );

    @UiField
    ScrollPanel                             availableTypesContainer;

    @UiField
    ScrollPanel                             chosenPatternsContainer;

    @UiField
    PushButton                              btnAdd;

    @UiField
    PushButton                              btnRemove;

    @UiField
    VerticalPanel                           patternDefinition;

    @UiField
    BindingTextBox                          txtBinding;

    @UiField
    HorizontalPanel                         bindingContainer;

    @UiField
    TextBox                                 txtEntryPoint;

    @UiField
    CEPWindowOperatorsDropdown              ddCEPWindow;

    @UiField
    HorizontalPanel                         cepWindowContainer;

    @UiField
    HorizontalPanel                         msgDuplicateBindings;

    @UiField(provided = true)
    PushButton                              btnMoveUp   = new PushButton( AbstractImagePrototype.create( images.shuffleUp() ).createImage() );

    @UiField(provided = true)
    PushButton                              btnMoveDown = new PushButton( AbstractImagePrototype.create( images.shuffleDown() ).createImage() );

    interface FactPatternsPageWidgetBinder
        extends
        UiBinder<Widget, FactPatternsPageViewImpl> {
    }

    private static FactPatternsPageWidgetBinder uiBinder = GWT.create( FactPatternsPageWidgetBinder.class );

    public FactPatternsPageViewImpl(Validator validator) {
        this.validator = validator;
        this.availableTypesWidget = new MinimumWidthCellList<String>( new TextCell(),
                                                                      WizardCellListResources.INSTANCE );
        this.chosenPatternWidget = new MinimumWidthCellList<Pattern52>( new PatternCell( validator ),
                                                                        WizardCellListResources.INSTANCE );

        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseAvailableTypes();
        initialiseChosenPatterns();
        initialiseBinding();
        initialiseEntryPoint();
        initialiseCEPWindow();
        initialiseShufflers();
    }

    private void initialiseAvailableTypes() {
        availableTypesContainer.add( availableTypesWidget );
        availableTypesWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableTypesWidget.setMinimumWidth( 275 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoAvailablePatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availableTypesWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<String> selectionModel = new MultiSelectionModel<String>();
        availableTypesWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                availableTypesSelections = selectionModel.getSelectedSet();
                btnAdd.setEnabled( availableTypesSelections.size() > 0 );
            }

        } );
    }

    private void initialiseChosenPatterns() {
        chosenPatternsContainer.add( chosenPatternWidget );
        chosenPatternWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenPatternWidget.setMinimumWidth( 275 );

        Label lstEmpty = new Label( constants.DecisionTableWizardNoChosenPatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenPatternWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<Pattern52> selectionModel = new MultiSelectionModel<Pattern52>();
        chosenPatternWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenPatternSelections = selectionModel.getSelectedSet();
                chosenTypesSelected( chosenPatternSelections );
            }

            private void chosenTypesSelected(Set<Pattern52> ps) {
                btnRemove.setEnabled( true );
                if ( ps.size() == 1 ) {
                    chosenPatternSelection = ps.iterator().next();
                    patternDefinition.setVisible( true );
                    validateBinding();
                    txtBinding.setEnabled( true );
                    txtBinding.setText( chosenPatternSelection.getBoundName() );

                    txtEntryPoint.setEnabled( true );
                    txtEntryPoint.setText( chosenPatternSelection.getEntryPointName() );
                    enableMoveUpButton();
                    enableMoveDownButton();
                    if ( presenter.isPatternEvent( chosenPatternSelection ) ) {
                        ddCEPWindow.setCEPWindow( chosenPatternSelection );
                        cepWindowContainer.setVisible( true );
                    } else {
                        cepWindowContainer.setVisible( false );
                    }
                } else {
                    chosenPatternSelection = null;
                    patternDefinition.setVisible( false );
                    txtBinding.setEnabled( false );
                    txtBinding.setText( "" );
                    txtEntryPoint.setEnabled( false );
                    txtEntryPoint.setText( "" );
                    btnMoveUp.setEnabled( false );
                    btnMoveDown.setEnabled( false );
                    cepWindowContainer.setVisible( false );
                }
            }

        } );
    }

    private void validateBinding() {
        //        if ( validator.isPatternBindingUnique( chosenPatternSelection ) && validator.isPatternValid( chosenPatternSelection ) ) {
        if ( validator.isPatternBindingUnique( chosenPatternSelection ) ) {
            bindingContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerValid() );
        } else {
            bindingContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerInvalid() );
        }
    }

    private void enableMoveUpButton() {
        if ( chosenPatterns == null || chosenPatternSelection == null ) {
            btnMoveUp.setEnabled( false );
            return;
        }
        int index = chosenPatterns.indexOf( chosenPatternSelection );
        btnMoveUp.setEnabled( index > 0 );
    }

    private void enableMoveDownButton() {
        if ( chosenPatterns == null || chosenPatternSelection == null ) {
            btnMoveDown.setEnabled( false );
            return;
        }
        int index = chosenPatterns.indexOf( chosenPatternSelection );
        btnMoveDown.setEnabled( index < chosenPatterns.size() - 1 );
    }

    private void initialiseBinding() {
        txtBinding.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String binding = txtBinding.getText();
                chosenPatternSelection.setBoundName( binding );
                presenter.stateChanged();
                validateBinding();
            }

        } );
    }

    private void initialiseEntryPoint() {
        txtEntryPoint.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                if ( chosenPatternSelection == null ) {
                    return;
                }
                chosenPatternSelection.setEntryPointName( event.getValue() );
            }

        } );
    }

    private void initialiseCEPWindow() {
        ddCEPWindow.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

            public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                if ( chosenPatternSelection == null ) {
                    return;
                }
                OperatorSelection selection = event.getValue();
                String selected = selection.getValue();
                chosenPatternSelection.getWindow().setOperator( selected );
            }

        } );
    }

    private void initialiseShufflers() {
        btnMoveUp.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenPatterns.indexOf( chosenPatternSelection );
                Pattern52 p = chosenPatterns.remove( index );
                chosenPatterns.add( index - 1,
                                    p );
                setChosenPatterns( chosenPatterns );
                dtable.setConditionPatterns( chosenPatterns );
            }

        } );
        btnMoveDown.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenPatterns.indexOf( chosenPatternSelection );
                Pattern52 p = chosenPatterns.remove( index );
                chosenPatterns.add( index + 1,
                                    p );
                setChosenPatterns( chosenPatterns );
                dtable.setConditionPatterns( chosenPatterns );
            }

        } );
    }

    public void setDecisionTable(GuidedDecisionTable52 dtable) {
        this.dtable = dtable;
    }

    public void setAvailableFactTypes(List<String> types) {
        availableTypesWidget.setRowCount( types.size(),
                                          true );
        availableTypesWidget.setRowData( types );
    }

    public void setChosenPatterns(List<Pattern52> types) {
        chosenPatterns = types;
        chosenPatternWidget.setRowCount( types.size(),
                                         true );
        chosenPatternWidget.setRowData( types );
        enableMoveUpButton();
        enableMoveDownButton();
        presenter.stateChanged();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setArePatternBindingsUnique(boolean arePatternBindingsUnique) {
        msgDuplicateBindings.setVisible( !arePatternBindingsUnique );
        chosenPatternWidget.redraw();
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( String type : availableTypesSelections ) {
            Pattern52 pattern = new Pattern52();
            pattern.setFactType( type );
            chosenPatterns.add( pattern );
        }
        setChosenPatterns( chosenPatterns );
        dtable.setConditionPatterns( chosenPatterns );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        for ( Pattern52 p : chosenPatternSelections ) {
            chosenPatterns.remove( p );

            //Raise an Event so ActionSetFieldPage can synchronise Patterns
            presenter.signalRemovalOfPattern( p );
        }

        chosenPatternSelections.clear();
        setChosenPatterns( chosenPatterns );
        dtable.setConditionPatterns( chosenPatterns );
        presenter.stateChanged();

        txtBinding.setText( "" );
        txtBinding.setEnabled( false );
        txtEntryPoint.setText( "" );
        txtEntryPoint.setEnabled( false );
        btnRemove.setEnabled( false );
        patternDefinition.setVisible( false );
    }

}
