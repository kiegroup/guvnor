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
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.resources.WizardCellListResources;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
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

    private Presenter                presenter;

    private Validator                validator               = new Validator();

    private List<Pattern52>          availablePatterns;
    private Pattern52                availablePatternsSelection;
    private CellList<Pattern52>      availablePatternsWidget = new CellList<Pattern52>( new PatternCell( validator ),
                                                                                        WizardCellListResources.INSTANCE );

    private Set<AvailableField>      availableFieldsSelections;
    private CellList<AvailableField> availableFieldsWidget   = new CellList<AvailableField>( new AvailableFieldCell(),
                                                                                             WizardCellListResources.INSTANCE );

    private List<ConditionCol52>     chosenConditions;
    private ConditionCol52           chosenConditionsSelection;
    private Set<ConditionCol52>      chosenConditionsSelections;
    private CellList<ConditionCol52> chosenConditionsWidget  = new CellList<ConditionCol52>( new ConditionCell(),
                                                                                             WizardCellListResources.INSTANCE );

    private static final Constants   constants               = GWT.create( Constants.class );

    private static final Images      images                  = GWT.create( Images.class );

    @UiField
    protected ScrollPanel            availablePatternsContainer;

    @UiField
    protected ScrollPanel            availableFieldsContainer;

    @UiField
    protected ScrollPanel            chosenConditionsContainer;

    @UiField
    protected PushButton             btnAdd;

    @UiField
    protected PushButton             btnRemove;

    @UiField(provided = true)
    PushButton                       btnMoveUp               = new PushButton( AbstractImagePrototype.create( images.shuffleUp() ).createImage() );

    @UiField(provided = true)
    PushButton                       btnMoveDown             = new PushButton( AbstractImagePrototype.create( images.shuffleDown() ).createImage() );

    interface FactPatternConstraintsPageWidgetBinder
        extends
        UiBinder<Widget, FactPatternConstraintsPageViewImpl> {
    }

    private static FactPatternConstraintsPageWidgetBinder uiBinder = GWT.create( FactPatternConstraintsPageWidgetBinder.class );

    public FactPatternConstraintsPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseAvailablePatterns();
        initialiseAvailableFields();
        initialiseChosenFields();
        initialiseShufflers();
    }

    private void initialiseAvailablePatterns() {
        availablePatternsContainer.add( availablePatternsWidget );
        availablePatternsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availablePatternsWidget.setEmptyListWidget( new Label( constants.DecisionTableWizardNoAvailablePatterns() ) );

        final SingleSelectionModel<Pattern52> selectionModel = new SingleSelectionModel<Pattern52>();
        availablePatternsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                availablePatternsSelection = selectionModel.getSelectedObject();
                presenter.patternSelected( availablePatternsSelection );
            }

        } );
    }

    private void initialiseAvailableFields() {
        availableFieldsContainer.add( availableFieldsWidget );
        availableFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableFieldsWidget.setEmptyListWidget( new Label( constants.DecisionTableWizardNoAvailableFields() ) );

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
        chosenConditionsWidget.setEmptyListWidget( new Label( constants.DecisionTableWizardNoChosenFields() ) );

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
                    //TODO
                    //                    txtBinding.setEnabled( true );
                    //                    txtBinding.setText( chosenPatternSelection.getPattern().getBoundName() );
                    //                    txtEntryPoint.setEnabled( true );
                    //                    txtEntryPoint.setText( chosenPatternSelection.getPattern().getEntryPointName() );
                    //                    if ( presenter.isPatternEvent( chosenPatternSelection.getPattern() ) ) {
                    //                        ddCEPWindow.setCEPWindow( chosenPatternSelection.getPattern() );
                    //                        cepWindowContainer.setVisible( true );
                    //                    } else {
                    //                        cepWindowContainer.setVisible( false );
                    //                    }
                    enableMoveUpButton();
                    enableMoveDownButton();
                } else {
                    chosenConditionsSelection = null;
                    //TODO
                    //                    txtBinding.setEnabled( false );
                    //                    txtBinding.setText( "" );
                    //                    txtEntryPoint.setEnabled( false );
                    //                    txtEntryPoint.setText( "" );
                    //                    cepWindowContainer.setVisible( false );
                    btnMoveUp.setEnabled( false );
                    btnMoveDown.setEnabled( false );
                }
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

    public void setAvailablePatterns(List<Pattern52> patterns) {
        availablePatterns = patterns;
        validator.setPatterns( availablePatterns );
        availablePatternsWidget.setRowCount( availablePatterns.size(),
                                             true );
        availablePatternsWidget.setRowData( availablePatterns );

        if ( availablePatternsSelection != null ) {

            //If the currently selected pattern is no longer available clear selections
            if ( !availablePatterns.contains( availablePatternsSelection ) ) {
                setAvailableFields( new ArrayList<AvailableField>() );
                availablePatternsSelection = null;
                setChosenConditions( new ArrayList<ConditionCol52>() );
                chosenConditionsSelection = null;
            }
        } else {

            //If no available pattern is selected clear fields
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
        enableMoveUpButton();
        enableMoveDownButton();
        presenter.stateChanged();
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( AvailableField f : availableFieldsSelections ) {
            ConditionCol52 c = new ConditionCol52();
            c.setFactField( f.getName() );
            c.setFieldType( f.getType() );
            chosenConditions.add( c );
        }
        setChosenConditions( chosenConditions );
        availablePatternsSelection.setConditions( chosenConditions );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        for ( ConditionCol52 c : chosenConditionsSelections ) {
            chosenConditions.remove( c );
        }
        setChosenConditions( chosenConditions );
        availablePatternsSelection.setConditions( chosenConditions );
        presenter.stateChanged();

        //TODO
        //        txtBinding.setText( "" );
        //        txtBinding.setEnabled( false );
        //        txtEntryPoint.setText( "" );
        //        txtEntryPoint.setEnabled( false );
        btnRemove.setEnabled( false );
    }

}
