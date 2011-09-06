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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.CEPWindowOperatorsDropdown;
import org.drools.guvnor.client.modeldriven.ui.OperatorSelection;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.resources.WizardCellListResources;
import org.drools.guvnor.client.widgets.wizards.assets.decisiontable.FactPatternCell.Pattern52Wrapper;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.cell.client.Cell;
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

    private TextCell                   cellText             = new TextCell();
    private Cell<Pattern52Wrapper>     cellPattern          = new FactPatternCell();

    private Set<String>                availableTypesSelections;
    private CellList<String>           availableTypesWidget = new CellList<String>( cellText,
                                                                                    WizardCellListResources.INSTANCE );

    private List<Pattern52Wrapper>     chosenTypes;
    private Pattern52Wrapper           chosenTypeSelection;
    private Set<Pattern52Wrapper>      chosenTypeSelections;
    private CellList<Pattern52Wrapper> chosenTypesWidget    = new CellList<Pattern52Wrapper>( cellPattern,
                                                                                              WizardCellListResources.INSTANCE );

    private static final Constants     constants            = GWT.create( Constants.class );

    private static final Images        images               = GWT.create( Images.class );

    @UiField
    ScrollPanel                        availableTypesContainer;

    @UiField
    ScrollPanel                        chosenTypesContainer;

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
        initialiseChosenTypes();
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

    private void initialiseChosenTypes() {
        chosenTypesContainer.add( chosenTypesWidget );
        chosenTypesWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenTypesWidget.setEmptyListWidget( new Label( constants.DecisionTableWizardNoChosenPatterns() ) );

        final MultiSelectionModel<Pattern52Wrapper> selectionModel = new MultiSelectionModel<Pattern52Wrapper>();
        chosenTypesWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenTypeSelections = new HashSet<Pattern52Wrapper>();
                Set<Pattern52Wrapper> selections = selectionModel.getSelectedSet();
                for ( Pattern52Wrapper pw : selections ) {
                    chosenTypeSelections.add( pw );
                }
                chosenTypesSelected( chosenTypeSelections );
            }

            private void chosenTypesSelected(Set<Pattern52Wrapper> pws) {
                btnRemove.setEnabled( true );
                if ( pws.size() == 1 ) {
                    chosenTypeSelection = pws.iterator().next();
                    txtBinding.setEnabled( true );
                    txtBinding.setText( chosenTypeSelection.getPattern().getBoundName() );
                    txtEntryPoint.setEnabled( true );
                    txtEntryPoint.setText( chosenTypeSelection.getPattern().getEntryPointName() );
                    enableMoveUpButton();
                    enableMoveDownButton();
                    if ( presenter.isPatternEvent( chosenTypeSelection.getPattern() ) ) {
                        ddCEPWindow.setCEPWindow( chosenTypeSelection.getPattern() );
                        cepWindowContainer.setVisible( true );
                    } else {
                        cepWindowContainer.setVisible( false );
                    }
                } else {
                    chosenTypeSelection = null;
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
        if ( chosenTypes == null || chosenTypeSelection == null ) {
            btnMoveUp.setEnabled( false );
            return;
        }
        int index = chosenTypes.indexOf( chosenTypeSelection );
        btnMoveUp.setEnabled( index > 0 );
    }

    private void enableMoveDownButton() {
        if ( chosenTypes == null || chosenTypeSelection == null ) {
            btnMoveDown.setEnabled( false );
            return;
        }
        int index = chosenTypes.indexOf( chosenTypeSelection );
        btnMoveDown.setEnabled( index < chosenTypes.size() - 1 );
    }

    private void initialiseBinding() {
        txtBinding.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                String binding = txtBinding.getText();
                chosenTypeSelection.getPattern().setBoundName( binding );
                chosenTypesWidget.redraw();
                stateChanged();
            }

        } );
    }

    private void initialiseEntryPoint() {
        txtEntryPoint.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                if ( chosenTypeSelection == null ) {
                    return;
                }
                chosenTypeSelection.getPattern().setEntryPointName( event.getValue() );
            }

        } );
    }

    private void initialiseCEPWindow() {
        ddCEPWindow.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

            public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                if ( chosenTypeSelection == null ) {
                    return;
                }
                OperatorSelection selection = event.getValue();
                String selected = selection.getValue();
                chosenTypeSelection.getPattern().getWindow().setOperator( selected );
            }

        } );
    }

    private void initialiseShufflers() {
        btnMoveUp.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenTypes.indexOf( chosenTypeSelection );
                Pattern52Wrapper pw = chosenTypes.remove( index );
                chosenTypes.add( index - 1,
                                 pw );
                setChosenFactTypeWrappers( chosenTypes );
                presenter.setChosenPatterns( unwrapPatterns() );
            }

        } );
        btnMoveDown.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                int index = chosenTypes.indexOf( chosenTypeSelection );
                Pattern52Wrapper pw = chosenTypes.remove( index );
                chosenTypes.add( index + 1,
                                 pw );
                setChosenFactTypeWrappers( chosenTypes );
                presenter.setChosenPatterns( unwrapPatterns() );
            }

        } );
    }

    private void stateChanged() {
        presenter.stateChanged();
        boolean duplicateBindings = false;

        //Store Patterns by their binding
        Map<String, List<Pattern52Wrapper>> bindings = new HashMap<String, List<Pattern52Wrapper>>();
        for ( Pattern52Wrapper pw : chosenTypes ) {
            pw.setDuplicateBinding( false );
            String binding = pw.getPattern().getBoundName();
            if ( binding != null && !binding.equals( "" ) ) {
                List<Pattern52Wrapper> pws = bindings.get( binding );
                if ( pws == null ) {
                    pws = new ArrayList<Pattern52Wrapper>();
                    bindings.put( binding,
                                  pws );
                }
                pws.add( pw );
            }
        }

        //Check if any bindings have multiple Patterns
        for ( List<Pattern52Wrapper> pws : bindings.values() ) {
            if ( pws.size() > 1 ) {
                duplicateBindings = true;
                for ( Pattern52Wrapper pw : pws ) {
                    pw.setDuplicateBinding( true );
                }
            }
        }
        messages.setVisible( duplicateBindings );
    }

    public void setAvailableFactTypes(List<String> types) {
        availableTypesWidget.setRowCount( types.size(),
                                          true );
        availableTypesWidget.setRowData( types );
    }

    public void setChosenFactTypes(List<Pattern52> types) {
        setChosenFactTypeWrappers( wrapPatterns( types ) );
        stateChanged();
    }

    private void setChosenFactTypeWrappers(List<Pattern52Wrapper> types) {
        chosenTypes = types;
        chosenTypesWidget.setRowCount( types.size(),
                                       true );
        chosenTypesWidget.setRowData( types );
        enableMoveUpButton();
        enableMoveDownButton();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private List<Pattern52Wrapper> wrapPatterns(List<Pattern52> patterns) {
        List<Pattern52Wrapper> pws = new ArrayList<Pattern52Wrapper>();
        for ( Pattern52 p : patterns ) {
            pws.add( new Pattern52Wrapper( p ) );
        }
        return pws;
    }

    private List<Pattern52> unwrapPatterns() {
        List<Pattern52> patterns = new ArrayList<Pattern52>();
        for ( Pattern52Wrapper pw : chosenTypes ) {
            patterns.add( pw.getPattern() );
        }
        return patterns;
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( String type : availableTypesSelections ) {
            Pattern52 pattern = new Pattern52();
            pattern.setFactType( type );
            chosenTypes.add( new Pattern52Wrapper( pattern ) );
        }
        setChosenFactTypeWrappers( chosenTypes );
        presenter.setChosenPatterns( unwrapPatterns() );
        stateChanged();
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        for ( Pattern52Wrapper pw : chosenTypeSelections ) {
            chosenTypes.remove( pw );
        }
        setChosenFactTypeWrappers( chosenTypes );
        presenter.setChosenPatterns( unwrapPatterns() );
        stateChanged();

        txtBinding.setText( "" );
        txtBinding.setEnabled( false );
        txtEntryPoint.setText( "" );
        txtEntryPoint.setEnabled( false );
        btnRemove.setEnabled( false );
    }

}
