package org.drools.brms.client.modeldriven.ui;
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



import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.DirtyableFlexTable;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.SmallLabel;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.ActionFieldValue;
import org.drools.brms.client.modeldriven.brl.ActionInsertFact;
import org.drools.brms.client.modeldriven.brl.ActionInsertLogicalFact;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

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

    public ActionInsertFactWidget(RuleModeller mod, ActionInsertFact set, SuggestionCompletionEngine com) {
        this.model = set;
        this.completions = com;
        this.layout = new DirtyableFlexTable();
        this.modeller = mod;
        this.factType = set.factType;
        this.fieldCompletions = this.completions.getFieldCompletions( set.factType );

        layout.setStyleName( "model-builderInner-Background" );

        doLayout();

        initWidget(this.layout);
    }

    private void doLayout() {
        layout.clear();
        layout.setWidget( 0, 0, getAssertLabel() );

        DirtyableFlexTable inner = new DirtyableFlexTable();

        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];

            inner.setWidget( i, 0, fieldSelector(val) );
            inner.setWidget( i, 1, valueEditor(val) );
            final int idx = i;
            Image remove = new ImageButton("images/delete_item_small.gif");
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                	if (Window.confirm("Remove this item?")) {
                            model.removeField( idx );
                            modeller.refreshWidget();
                	};
                }
            });
            inner.setWidget( i, 2, remove );

        }

        layout.setWidget( 0, 1, inner );


    }

    private Widget valueEditor(final ActionFieldValue val) {
    	String[] enums = this.completions.getEnums(this.factType, this.model.fieldValues, val.field);
    	return new ActionValueEditor(val, enums);
    }

    private Widget fieldSelector(final ActionFieldValue val) {
        return new SmallLabel(val.field);
    }

    private Widget getAssertLabel() {
        HorizontalPanel horiz = new HorizontalPanel();


        Image edit = new ImageButton("images/add_field_to_fact.gif");
        edit.setTitle( "Add another field to this so you can set its value." );
        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showAddFieldPopup(w);
            }
        } );


        String assertType = "assert";
        if (this.model instanceof ActionInsertLogicalFact) {
            assertType = "assertLogical";
        }
        horiz.add( new SmallLabel("<i>" + HumanReadable.getActionDisplayName(assertType) + " " + this.model.factType + "</i>") );
        horiz.add( edit );
        return horiz;

    }

    protected void showAddFieldPopup(Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/newex_wiz.gif", "Add a field");
        final ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletions.length; i++ ) {
            box.addItem( fieldCompletions[i] );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( "Add field", box );
        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                String fieldName = box.getItemText( box.getSelectedIndex() );
                String fieldType = completions.getFieldType( model.factType, fieldName );
                model.addFieldValue( new ActionFieldValue( fieldName, "", fieldType ) );
                modeller.refreshWidget();
                popup.hide();
            }
        });

        popup.show();

    }

}