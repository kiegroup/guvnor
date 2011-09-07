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

package org.drools.guvnor.client.widgets.wizards.assets.decisiontable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.CEPWindowOperatorsDropdown;
import org.drools.guvnor.client.modeldriven.ui.OperatorSelection;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.resources.WizardCellListResources;
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
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * The generic Wizard view implementation
 */
public class FactPatternsPageViewImpl extends Composite
    implements
    FactPatternsPageView {

    private Presenter                  presenter;

    private GuidedDecisionTable52      dtable;

    private Set<String>                availableTypesSelections;
    private CellList<String>           availableTypesWidget = new CellList<String>( new TextCell(),
                                                                                    WizardCellListResources.INSTANCE );

    private List<DecoratedPattern>     chosenPatterns;
    private DecoratedPattern           chosenPatternSelection;
    private Set<DecoratedPattern>      chosenPatternSelections;
    private CellList<DecoratedPattern> chosenPatternWidget  = new CellList<DecoratedPattern>( new DecoratedPatternCell(),
                                                                                              WizardCellListResources.INSTANCE );

    private static final Constants     constants            = GWT.create( Constants.class );

    private static final Images        images               = GWT.create( Images.class );

    @UiField
    ScrollPanel                        availableTypesContainer;

    @UiField
    ScrollPanel                        chosenPatternsContainer;

    @UiField
    PushButton                         btnAdd;

    @UiField
    PushButton                         btnRemove;

    @UiField
    TextBox                            txtBinding;

    @UiField
    TextBox                            txtEntryPoint;

    @UiField
    CEPWindowOperatorsDropdown         ddCEPWindow;

    @UiField
    HorizontalPanel                    cepWindowContainer;

    @UiField
    HorizontalPanel                    messages;

    @UiField(provided = true)
    PushButton                         btnMoveUp            = new PushButton( AbstractImagePrototype.create( images.shuffleUp() ).createImage() );

    @UiField(provided = true)
    PushButton                         btnMoveDown          = new PushButton( AbstractImagePrototype.create( images.shuffleDown() ).createImage() );

    interface FactPatternsPageWidgetBinder
        extends
        UiBinder<Widget, FactPatternsPageViewImpl> {
    }

    private static FactPatternsPageWidgetBinder uiBinder = GWT.create( FactPatternsPageWidgetBinder.class );

    public FactPatternsPageViewImpl() {
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
        availableTypesWidget.setEmptyListWidget( new Label( constants.DecisionTableWizardNoAvailablePatterns() ) );

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
        chosenPatternWidget.setEmptyListWidget( new Label( constants.DecisionTableWizardNoChosenPatterns() ) );

        final MultiSelectionModel<DecoratedPattern> selectionModel = new MultiSelectionModel<DecoratedPattern>();
        chosenPatternWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenPatternSelections = new HashSet<DecoratedPattern>();
                Set<DecoratedPattern> selections = selectionModel.getSelectedSet();
                for ( DecoratedPattern pw : selections ) {
                    chosenPatternSelections.add( pw );
                }
                chosenTypesSelected( chosenPatternSelections );
            }

            private void chosenTypesSelected(Set<DecoratedPattern> pws) {
                btnRemove.setEnabled( true );
                if ( pws.size() == 1 ) {
                    chosenPatternSelection = pws.iterator().next();
                    txtBinding.setEnabled( true );
                    txtBinding.setText( chosenPatternSelection.getPattern().getBoundName() );
                    txtEntryPoint.setEnabled( true );
                    txtEntryPoint.setText( chosenPatternSelection.getPattern().getEntryPointName() );
                    enableMoveUpButton();
                    enableMoveDownButton();
                    if ( presenter.isPatternEvent( chosenPatternSelection.getPattern() ) ) {
                        ddCEPWindow.setCEPWindow( chosenPatternSelection.getPattern() );
                        cepWindowContainer.setVisible( true );
                    } else {
                        cepWindowContainer.setVisible( false );
                    }
                } else {
                    chosenPatternSelection = null;
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
                chosenPatternSelection.getPattern().setBoundName( binding );
                chosenPatternWidget.redraw();
                presenter.stateChanged();
            }

        } );
    }

    private void initialiseEntryPoint() {
        txtEntryPoint.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                if ( chosenPatternSelection == null ) {
                    return;
                }
                chosenPatternSelection.getPattern().setEntryPointName( event.getValue() );
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
                chosenPatternSelection.getPattern().getWindow().setOperator( selected );
            }

        } );
    }

    private void initialiseShufflers() {
        btnMoveUp.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenPatterns.indexOf( chosenPatternSelection );
                DecoratedPattern pw = chosenPatterns.remove( index );
                chosenPatterns.add( index - 1,
                                    pw );
                setDecoratedChosenPatterns( chosenPatterns );
                dtable.setConditionPatterns( unwrapPatterns() );
            }

        } );
        btnMoveDown.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenPatterns.indexOf( chosenPatternSelection );
                DecoratedPattern pw = chosenPatterns.remove( index );
                chosenPatterns.add( index + 1,
                                    pw );
                setDecoratedChosenPatterns( chosenPatterns );
                dtable.setConditionPatterns( unwrapPatterns() );
            }

        } );
    }

    public boolean isComplete() {

        //Have patterns been defined?
        if ( chosenPatterns == null || chosenPatterns.size() == 0 ) {
            return false;
        }

        //Are the patterns valid?
        boolean hasValidationErrors = Validator.validateDecoratedPatterns( chosenPatterns );
        messages.setVisible( hasValidationErrors );
        return !hasValidationErrors;
    }

    public void setDecisionTable(GuidedDecisionTable52 dtable) {
        this.dtable = dtable;
    }

    public void setAvailableFactTypes(List<String> types) {
        availableTypesWidget.setRowCount( types.size(),
                                          true );
        availableTypesWidget.setRowData( types );
    }

    public void setChosenFactTypes(List<Pattern52> types) {
        setDecoratedChosenPatterns( wrapPatterns( types ) );
        presenter.stateChanged();
    }

    private void setDecoratedChosenPatterns(List<DecoratedPattern> types) {
        chosenPatterns = types;
        chosenPatternWidget.setRowCount( types.size(),
                                         true );
        chosenPatternWidget.setRowData( types );
        enableMoveUpButton();
        enableMoveDownButton();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private List<DecoratedPattern> wrapPatterns(List<Pattern52> patterns) {
        List<DecoratedPattern> pws = new ArrayList<DecoratedPattern>();
        for ( Pattern52 p : patterns ) {
            pws.add( new DecoratedPattern( p ) );
        }
        return pws;
    }

    private List<Pattern52> unwrapPatterns() {
        List<Pattern52> patterns = new ArrayList<Pattern52>();
        for ( DecoratedPattern pw : chosenPatterns ) {
            patterns.add( pw.getPattern() );
        }
        return patterns;
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( String type : availableTypesSelections ) {
            Pattern52 pattern = new Pattern52();
            pattern.setFactType( type );
            chosenPatterns.add( new DecoratedPattern( pattern ) );
        }
        setDecoratedChosenPatterns( chosenPatterns );
        dtable.setConditionPatterns( unwrapPatterns() );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        for ( DecoratedPattern pw : chosenPatternSelections ) {
            chosenPatterns.remove( pw );
        }
        setDecoratedChosenPatterns( chosenPatterns );
        dtable.setConditionPatterns( unwrapPatterns() );
        presenter.stateChanged();

        txtBinding.setText( "" );
        txtBinding.setEnabled( false );
        txtEntryPoint.setText( "" );
        txtEntryPoint.setEnabled( false );
        btnRemove.setEnabled( false );
    }

}
