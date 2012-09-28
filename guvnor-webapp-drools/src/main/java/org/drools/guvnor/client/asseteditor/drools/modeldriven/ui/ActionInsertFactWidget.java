/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.HumanReadable;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.events.TemplateVariablesChangedEvent;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is used when asserting a new fact into working memory.
 */
public class ActionInsertFactWidget extends RuleModellerWidget {

    private final DirtyableFlexTable layout;
    private final ActionInsertFact   model;
    private final String[]           fieldCompletions;
    private final String             factType;
    private boolean                  readOnly;

    private boolean                  isFactTypeKnown;

    public ActionInsertFactWidget(RuleModeller mod,
                                  EventBus eventBus,
                                  ActionInsertFact set,
                                  Boolean readOnly) {
        super( mod,
               eventBus );
        this.model = set;
        this.layout = new DirtyableFlexTable();
        this.factType = set.factType;

        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        this.fieldCompletions = completions.getFieldCompletions( FieldAccessorsAndMutators.MUTATOR,
                                                                 set.factType );

        layout.setStyleName( "model-builderInner-Background" ); //NON-NLS

        this.isFactTypeKnown = completions.containsFactType( set.factType );
        if ( readOnly == null ) {
            this.readOnly = !this.isFactTypeKnown;
        } else {
            this.readOnly = readOnly;
        }

        if ( this.readOnly ) {
            layout.addStyleName( "editor-disabled-widget" );
        }

        doLayout();

        initWidget( this.layout );

    }

    private void doLayout() {
        layout.clear();
        layout.setWidget( 0,
                          0,
                          getAssertLabel() );
        layout.setWidget( 1,
                          0,
                          new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
        layout.getFlexCellFormatter().setColSpan( 0,
                                                  0,
                                                  2 );

        DirtyableFlexTable inner = new DirtyableFlexTable();
        int col = 0;

        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];

            inner.setWidget( i,
                             0 + col,
                             fieldSelector( val ) );
            inner.setWidget( i,
                             1 + col,
                             valueEditor( val ) );
            final int idx = i;
            Image remove = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
            remove.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if ( Window.confirm( Constants.INSTANCE.RemoveThisItem() ) ) {
                        model.removeField( idx );
                        setModified( true );
                        getModeller().refreshWidget();

                        //Signal possible change in Template variables
                        TemplateVariablesChangedEvent tvce = new TemplateVariablesChangedEvent( getModeller().getModel() );
                        getEventBus().fireEventFromSource( tvce,
                                                           getModeller().getModel() );
                    }
                }
            } );
            if ( !this.readOnly ) {
                inner.setWidget( i,
                                 2 + col,
                                 remove );
            }

        }

        layout.setWidget( 1,
                          1,
                          inner );

    }

    private Widget valueEditor(final ActionFieldValue val) {
        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        DropDownData enums = completions.getEnums( this.factType,
                                                   val.field,
                                                   this.model.fieldValues
                );

        ActionValueEditor actionValueEditor = new ActionValueEditor( val,
                                                                     enums,
                                                                     this.getModeller(),
                                                                     this.getEventBus(),
                                                                     val.type,
                                                                     this.readOnly );
        actionValueEditor.setOnChangeCommand( new Command() {
            public void execute() {
                setModified( true );
            }
        } );

        return actionValueEditor;
    }

    private Widget fieldSelector(final ActionFieldValue val) {
        return new SmallLabel( val.field );
    }

    private Widget getAssertLabel() {

        ClickHandler cl = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget w = (Widget) event.getSource();
                showAddFieldPopup( w );

            }
        };

        String assertType = "assert"; //NON-NLS
        if ( this.model instanceof ActionInsertLogicalFact ) {
            assertType = "assertLogical"; //NON-NLS
        }

        String lbl = (model.isBound() == false) ? HumanReadable.getActionDisplayName( assertType ) + " <b>" + this.model.factType + "</b>" : HumanReadable.getActionDisplayName( assertType ) + " <b>" + this.model.factType + "</b>" + " <b>["
                                                                                                                                             + model.getBoundName() + "]</b>";
        if ( this.model.fieldValues != null && model.fieldValues.length > 0 ) {
            lbl = lbl + ":";
        }
        return new ClickableLabel( lbl,
                                   cl,
                                   !this.readOnly );

    }

    protected void showAddFieldPopup(Widget w) {
        final SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();

        final FormStylePopup popup = new FormStylePopup( DroolsGuvnorImages.INSTANCE.Wizard(),
                                                         Constants.INSTANCE.AddAField() );
        final ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletions.length; i++ ) {
            box.addItem( fieldCompletions[i] );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( Constants.INSTANCE.AddField(),
                            box );
        box.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                String fieldName = box.getItemText( box.getSelectedIndex() );
                String fieldType = completions.getFieldType( model.factType,
                                                             fieldName );
                model.addFieldValue( new ActionFieldValue( fieldName,
                                                           "",
                                                           fieldType ) );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );
        /*
         * Propose a textBox to the user to make him set a variable name
         */
        final HorizontalPanel vn = new HorizontalPanel();
        final TextBox varName = new TextBox();
        if ( this.model.getBoundName() != null ) {
            varName.setText( this.model.getBoundName() );
        }
        final Button ok = new Button( Constants.INSTANCE.Set() );
        vn.add( varName );
        vn.add( ok );
        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                String var = varName.getText();
                if ( getModeller().isVariableNameUsed( var ) && ((model.getBoundName() != null && model.getBoundName().equals( var ) == false) || model.getBoundName() == null) ) {
                    Window.alert( Constants.INSTANCE.TheVariableName0IsAlreadyTaken( var ) );
                    return;
                }
                model.setBoundName( var );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        popup.addAttribute( Constants.INSTANCE.BoundVariable(),
                            vn );
        popup.show();

    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return this.isFactTypeKnown;
    }

}
