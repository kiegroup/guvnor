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

package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.FieldAccessorsAndMutators;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is for setting fields on a bound fact or global variable.
 */
public class ActionSetFieldWidget extends RuleModellerWidget {

    private Constants                constants   = GWT.create( Constants.class );
    private static Images            images      = GWT.create( Images.class );

    final private ActionSetField     model;
    final private DirtyableFlexTable layout;
    private boolean                  isBoundFact = false;
    private String[]                 fieldCompletions;
    private String                   variableClass;
    private boolean                  readOnly;

    public ActionSetFieldWidget(RuleModeller mod,
                                ActionSetField set) {
        this( mod,
              set,
              null );
    }

    public ActionSetFieldWidget(RuleModeller mod,
                                ActionSetField set,
                                Boolean readOnly) {
        super( mod );
        this.model = set;
        this.layout = new DirtyableFlexTable();

        layout.setStyleName( "model-builderInner-Background" );

        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();

        if ( completions.isGlobalVariable( set.variable ) ) {
            this.fieldCompletions = completions.getFieldCompletionsForGlobalVariable( set.variable );
            this.variableClass = completions.getGlobalVariable( set.variable );
        } else {
            String type = mod.getModel().getLHSBindingType( set.variable );
            if ( type != null ) {
                this.fieldCompletions = completions.getFieldCompletions( FieldAccessorsAndMutators.MUTATOR,
                                                                         type );
                this.variableClass = type;
                this.isBoundFact = true;
            } else {
                ActionInsertFact patternRhs = mod.getModel().getRHSBoundFact( set.variable );
                if ( patternRhs != null ) {
                    this.fieldCompletions = completions.getFieldCompletions( FieldAccessorsAndMutators.MUTATOR,
                                                                             patternRhs.factType );
                    this.variableClass = patternRhs.factType;
                    this.isBoundFact = true;
                }
            }
        }

        if ( this.variableClass == null ) {
            throw new IllegalStateException( "couldn't find type for variable: " + set.variable );
        }

        if ( readOnly == null ) {
            this.readOnly = !completions.containsFactType( this.variableClass );
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

        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];

            layout.setWidget( i,
                              0,
                              getSetterLabel() );
            layout.setWidget( i,
                              1,
                              fieldSelector( val ) );
            layout.setWidget( i,
                              2,
                              valueEditor( val ) );
            final int idx = i;
            Image remove = new ImageButton( images.deleteItemSmall() );
            remove.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    if ( Window.confirm( constants.RemoveThisItem() ) ) {
                        model.removeField( idx );
                        setModified( true );
                        getModeller().refreshWidget();
                    }
                }
            } );
            if ( !this.readOnly ) {
                layout.setWidget( i,
                                  3,
                                  remove );
            }

        }

        if ( model.fieldValues.length == 0 ) {
            HorizontalPanel h = new HorizontalPanel();
            h.add( getSetterLabel() );
            if ( !this.readOnly ) {
                h.add( new ImageButton( images.edit(),
                                        constants.AddFirstNewField(),
                                        new ClickHandler() {

                                            public void onClick(ClickEvent sender) {
                                                showAddFieldPopup( sender );
                                            }
                                        } ) );
            }
            layout.setWidget( 0,
                              0,
                              h );
        }

        //layout.setWidget( 0, 1, inner );

    }

    private Widget getSetterLabel() {

        ClickHandler clk = new ClickHandler() {

            public void onClick(ClickEvent event) {
                //Widget w = (Widget)event.getSource();
                showAddFieldPopup( event );

            }
        };
        String modifyType = "set";
        if ( this.model instanceof ActionUpdateField ) {
            modifyType = "modify";
        }

        String type = this.getModeller().getModel().getLHSBindingType( model.variable );

        String descFact = (type != null) ? type + " <b>[" + model.variable + "]</b>" : model.variable;

        String sl = constants.setterLabel( HumanReadable.getActionDisplayName( modifyType ), descFact );
        return new ClickableLabel( sl,
                                   clk,
                                   !this.readOnly );//HumanReadable.getActionDisplayName(modifyType) + " value of <b>[" + model.variable + "]</b>", clk);
    }

    protected void showAddFieldPopup(ClickEvent w) {
        final SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        final FormStylePopup popup = new FormStylePopup( images.newexWiz(),
                                                         constants.AddAField() );

        final ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletions.length; i++ ) {
            box.addItem( fieldCompletions[i] );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( constants.AddField(),
                            box );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                String fieldName = box.getItemText( box.getSelectedIndex() );

                String fieldType = completions.getFieldType( variableClass,
                                                             fieldName );
                model.addFieldValue( new ActionFieldValue( fieldName,
                                                           "",
                                                           fieldType ) );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        popup.show();

    }

    private Widget valueEditor(final ActionFieldValue val) {
        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        String type = "";
        if ( completions.isGlobalVariable( this.model.variable ) ) {
            type = (String) completions.getGlobalVariable( this.model.variable );
        } else {
            type = this.getModeller().getModel().getLHSBindingType( this.model.variable );
            /*
             * to take in account if the using a rhs bound variable
             */
            if ( type == null ) {
                type = this.getModeller().getModel().getRHSBoundFact( this.model.variable ).factType;
            }
        }

        DropDownData enums = completions.getEnums( type,
                val.field, this.model.fieldValues
        );
        ActionValueEditor actionValueEditor = new ActionValueEditor( val,
                                                                     enums,
                                                                     this.getModeller(),
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

    /**
     * This returns true if the values being set are on a fact.
     */
    public boolean isBoundFact() {
        return isBoundFact;
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }
}
