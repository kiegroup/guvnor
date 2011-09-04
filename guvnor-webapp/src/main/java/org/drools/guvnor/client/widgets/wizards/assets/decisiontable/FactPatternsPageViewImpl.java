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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * The generic Wizard view implementation
 */
public class FactPatternsPageViewImpl extends Composite
    implements
    FactPatternsPageView {

    private Presenter           presenter;

    private TextCell            cellText             = new TextCell();
    private Cell<Pattern52>     cellPattern          = new AbstractCell<Pattern52>() {

                                                         @Override
                                                         public void render(Context context,
                                                                            Pattern52 value,
                                                                            SafeHtmlBuilder sb) {
                                                             sb.append( SafeHtmlUtils.fromString( value.getBoundName() + " : " + value.getFactType() ) );
                                                         }

                                                     };

    private List<String>        availableTypes;
    private Set<String>         availableTypesSelections;
    private CellList<String>    availableTypesWidget = new CellList<String>( cellText );

    private List<Pattern52>     chosenTypes;
    private Pattern52           chosenTypeSelection;
    private CellList<Pattern52> chosenTypesWidget    = new CellList<Pattern52>( cellPattern );

    @UiField
    protected ScrollPanel       availableTypesContainer;

    @UiField
    protected ScrollPanel       chosenTypesContainer;

    @UiField
    protected Button            btnAdd;

    @UiField
    protected Button            btnRemove;

    @UiField
    protected TextBox           txtBinding;

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
    }

    private void initialiseAvailableTypes() {
        availableTypesContainer.add( availableTypesWidget );
        availableTypesWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );

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

        final SingleSelectionModel<Pattern52> selectionModel = new SingleSelectionModel<Pattern52>();
        chosenTypesWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {
                chosenTypeSelection = selectionModel.getSelectedObject();
                txtBinding.setText( chosenTypeSelection.getBoundName() );
            }

        } );
    }
    
    private void initialiseBinding() {
        txtBinding.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                chosenTypeSelection.setBoundName( event.getValue() );
            }
            
        });
        
        txtBinding.addBlurHandler( new BlurHandler() {

            public void onBlur(BlurEvent event) {
                chosenTypesWidget.redraw();
            }
            
        });
    }

    public void setAvailableFactTypes(List<String> types) {
        availableTypes = types;
        availableTypesWidget.setRowCount( types.size(),
                                          true );
        availableTypesWidget.setRowData( types );
        availableTypesWidget.setEmptyListWidget( new Label( "No types exist" ) );
    }

    public void setChosenFactTypes(List<Pattern52> types) {
        chosenTypes = types;
        chosenTypesWidget.setRowCount( types.size(),
                                       true );
        chosenTypesWidget.setRowData( types );
        chosenTypesWidget.setEmptyListWidget( new Label( "No types chosen" ) );
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick(ClickEvent event) {
        for ( String type : availableTypesSelections ) {
            Pattern52 pattern = new Pattern52();
            pattern.setFactType( type );
            chosenTypes.add( pattern );
        }
        setChosenFactTypes( chosenTypes );
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick(ClickEvent event) {
        chosenTypes.remove( chosenTypeSelection );
        setChosenFactTypes( chosenTypes );
        txtBinding.setText( "" );
    }

}
