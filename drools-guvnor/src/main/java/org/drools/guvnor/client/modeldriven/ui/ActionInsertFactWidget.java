package org.drools.guvnor.client.modeldriven.ui;

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



import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.modeldriven.DropDownData;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertFact;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertLogicalFact;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

/**
 * This is used when asserting a new fact into working memory.
 *
 * @author Michael Neale
 *
 */
public class ActionInsertFactWidget extends DirtyableComposite {

    private final DirtyableFlexTable layout;
    private final ActionInsertFact model;
    private final SuggestionCompletionEngine completions;
    private final String[] fieldCompletions;
    private final RuleModeller modeller;
    private final String factType;
    private Constants constants = GWT.create(Constants.class);

    public ActionInsertFactWidget(RuleModeller mod, ActionInsertFact set, SuggestionCompletionEngine com) {
        this.model = set;
        this.completions = com;
        this.layout = new DirtyableFlexTable();
        this.modeller = mod;
        this.factType = set.factType;
        this.fieldCompletions = this.completions.getFieldCompletions( set.factType );

        layout.setStyleName( "model-builderInner-Background" );  //NON-NLS

        doLayout();

        initWidget(this.layout);
    }

    private void doLayout() {
        layout.clear();
        layout.setWidget( 0, 0, getAssertLabel() );
        layout.setWidget( 1, 0, new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));
        layout.getFlexCellFormatter().setColSpan(0, 0, 2);

        DirtyableFlexTable inner = new DirtyableFlexTable();
        int col = 0;

        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];

            inner.setWidget( i, 0 + col, fieldSelector(val) );
            inner.setWidget( i, 1 + col, valueEditor(val) );
            final int idx = i;
            Image remove = new ImageButton("images/delete_item_small.gif");
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                	if (Window.confirm(constants.RemoveThisItem())) {
                            model.removeField( idx );
                            modeller.refreshWidget();
                	};
                }
            });
            inner.setWidget( i, 2 + col, remove );

        }

        layout.setWidget( 1, 1, inner );


    }

    private Widget valueEditor(final ActionFieldValue val) {
    	DropDownData enums = this.completions.getEnums(this.factType, this.model.fieldValues, val.field);
    	return new ActionValueEditor(val, enums,modeller,val.type);
    }

    private Widget fieldSelector(final ActionFieldValue val) {
        return new SmallLabel(val.field);
    }

    private Widget getAssertLabel() {


        ClickListener cl =  new ClickListener() {
            public void onClick(Widget w) {
                showAddFieldPopup(w);
            }
        };


        String assertType = "assert";  //NON-NLS
        if (this.model instanceof ActionInsertLogicalFact) {
            assertType = "assertLogical";  //NON-NLS
        }

        String lbl = (model.isBound() == false) ? HumanReadable.getActionDisplayName( assertType ) + " <b>" + this.model.factType + "</b>" : HumanReadable.getActionDisplayName( assertType ) + " <b>" + this.model.factType + "</b>" + " <b>[" + model.getBoundName() + "]</b>";
        if (this.model.fieldValues != null && model.fieldValues.length > 0 ) {
            lbl = lbl + ":";
        }
        return new ClickableLabel( lbl, cl );

    }

    protected void showAddFieldPopup(Widget w) {
        final FormStylePopup popup = new FormStylePopup( "images/newex_wiz.gif",
                                                         constants.AddAField() );
        final ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletions.length; i++ ) {
            box.addItem( fieldCompletions[i] );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( constants.AddField(),
                            box );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String fieldName = box.getItemText( box.getSelectedIndex() );
                String fieldType = completions.getFieldType( model.factType,
                                                             fieldName );
                model.addFieldValue( new ActionFieldValue( fieldName,
                                                           "",
                                                           fieldType ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        /*
         * Propose a textBox to the user
         * to make him set a variable name 
         */
        final HorizontalPanel vn = new HorizontalPanel();
        final TextBox varName = new TextBox();
        if ( this.model.getBoundName() != null ) {
            varName.setText( this.model.getBoundName() );
        }
        final Button ok = new Button( constants.Set() );
        vn.add( varName );
        vn.add( ok );

        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                String var = varName.getText();
                if ( modeller.isVariableNameUsed( var ) && ((model.getBoundName() != null && model.getBoundName().equals( var ) == false) || model.getBoundName() == null) ) {
                    Window.alert( Format.format( constants.TheVariableName0IsAlreadyTaken(),
                                                 var ) );
                    return;
                }
                model.setBoundName( var );
                modeller.refreshWidget();
                popup.hide();
            }
        } );
        popup.addAttribute( constants.BoundVariable(),
                            vn );
        popup.show();

    }

}