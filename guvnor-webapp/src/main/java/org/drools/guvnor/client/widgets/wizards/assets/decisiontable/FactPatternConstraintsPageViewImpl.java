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

import java.util.List;
import java.util.Set;

import org.drools.ide.common.client.modeldriven.dt52.Pattern52;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * The generic Wizard view implementation
 */
public class FactPatternConstraintsPageViewImpl extends Composite
    implements
    FactPatternConstraintsPageView {

    private Presenter           presenter;

    private TextCell            cellText                = new TextCell();
    private Cell<Pattern52>     cellPattern             = new AbstractCell<Pattern52>() {

                                                            @Override
                                                            public void render(Context context,
                                                                               Pattern52 value,
                                                                               SafeHtmlBuilder sb) {
                                                                sb.append( SafeHtmlUtils.fromString( value.getBoundName() + " : " + value.getFactType() ) );
                                                            }

                                                        };

    private List<Pattern52>     availablePatterns;
    private Set<Pattern52>      availablePatternsSelections;
    private CellList<Pattern52> availablePatternsWidget = new CellList<Pattern52>( cellPattern );

    private List<String>        availableFields;
    private Set<String>         availableFieldsSelections;
    private CellList<String>    availableFieldsWidget   = new CellList<String>( cellText );

    private List<String>        chosenFields;
    private Set<String>         chosenFieldsSelections;
    private CellList<String>    chosenFieldsWidget      = new CellList<String>( cellText );

    @UiField
    protected ScrollPanel       availablePatternsContainer;

    @UiField
    protected ScrollPanel       availableFieldsContainer;

    @UiField
    protected ScrollPanel       chosenFieldsContainer;

    @UiField
    protected Button            btnAdd;

    @UiField
    protected Button            btnRemove;

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
    }

    private void initialiseAvailablePatterns() {
        availablePatternsContainer.add( availablePatternsWidget );
        availablePatternsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );

        final MultiSelectionModel<Pattern52> selectionModel = new MultiSelectionModel<Pattern52>();
        availablePatternsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                availablePatternsSelections = selectionModel.getSelectedSet();
                btnAdd.setEnabled( availablePatternsSelections.size() > 0 );
            }

        } );
    }

    private void initialiseAvailableFields() {
        availableFieldsContainer.add( availableFieldsWidget );
        availableFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );

        final MultiSelectionModel<String> selectionModel = new MultiSelectionModel<String>();
        availableFieldsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                availableFieldsSelections = selectionModel.getSelectedSet();
            }

        } );
    }

    private void initialiseChosenFields() {
        chosenFieldsContainer.add( chosenFieldsWidget );
        chosenFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );

        final MultiSelectionModel<String> selectionModel = new MultiSelectionModel<String>();
        chosenFieldsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenFieldsSelections = selectionModel.getSelectedSet();
            }

        } );
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setAvailablePatterns(List<Pattern52> patterns) {
        availablePatterns = patterns;
        availablePatternsWidget.setRowCount( patterns.size(),
                                             true );
        availablePatternsWidget.setRowData( patterns );
        availablePatternsWidget.setEmptyListWidget( new Label( "No patterns defined" ) );
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
    }

}
