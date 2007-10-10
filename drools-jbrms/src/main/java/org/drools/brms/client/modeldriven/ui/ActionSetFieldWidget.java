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
import org.drools.brms.client.common.Lbl;
import org.drools.brms.client.common.YesNoDialog;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.ActionFieldValue;
import org.drools.brms.client.modeldriven.brl.ActionSetField;
import org.drools.brms.client.modeldriven.brl.ActionUpdateField;
import org.drools.brms.client.modeldriven.brl.FactPattern;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is for setting fields on a bound fact or global variable.
 *
 * @author Michael Neale
 */
public class ActionSetFieldWidget extends DirtyableComposite {

    final private ActionSetField model;
    final private SuggestionCompletionEngine completions;
    final private DirtyableFlexTable layout;
    private boolean isBoundFact = false;

    final private String[] fieldCompletions;
    final private RuleModeller modeller;
    final private String variableClass;


    public ActionSetFieldWidget(RuleModeller mod,  ActionSetField set, SuggestionCompletionEngine com) {
        this.model = set;
        this.completions = com;
        this.layout = new DirtyableFlexTable();
        this.modeller = mod;

        layout.setStyleName( "model-builderInner-Background" );
        if (completions.isGlobalVariable( set.variable )) {
            this.fieldCompletions = completions.getFieldCompletionsForGlobalVariable( set.variable );
            this.variableClass = (String) completions.globalTypes.get( set.variable );
        } else {
            FactPattern pattern = mod.getModel().getBoundFact( set.variable );
            this.fieldCompletions = completions.getFieldCompletions( pattern.factType );
            this.variableClass = pattern.factType;
            this.isBoundFact = true;
        }

        doLayout();

        initWidget( this.layout );
    }


    private void doLayout() {
        layout.clear();
        layout.setWidget( 0, 0, getSetterLabel() );

        DirtyableFlexTable inner = new DirtyableFlexTable();

        for ( int i = 0; i < model.fieldValues.length; i++ ) {
            ActionFieldValue val = model.fieldValues[i];

            inner.setWidget( i, 0, fieldSelector(val) );
            inner.setWidget( i, 1, valueEditor(val) );
            final int idx = i;
            Image remove = new ImageButton("images/delete_item_small.gif");
            remove.addClickListener( new ClickListener() {
                public void onClick(Widget w) {
                    YesNoDialog diag = new YesNoDialog("Remove this item?", new Command() {
                        public void execute() {
                            model.removeField( idx );
                            modeller.refreshWidget();
                        }
                    });
                    diag.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
                    diag.show();
                }
            });
            inner.setWidget( i, 2, remove );
        }

        layout.setWidget( 0, 1, inner );


    }


    private Widget getSetterLabel() {

        HorizontalPanel horiz = new HorizontalPanel();


        Image edit = new ImageButton("images/add_field_to_fact.gif");
        edit.setTitle( "Add another field to this so you can set its value." );
        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showAddFieldPopup(w);
            }
        } );
        String modifyType = "set";
        if (this.model instanceof ActionUpdateField) {
            modifyType = "modify";
        }
        horiz.add( new Lbl(HumanReadable.getActionDisplayName(modifyType) + " [" + model.variable + "]", "modeller-action-Label") );
        horiz.add( edit );

        return horiz;
    }


    protected void showAddFieldPopup(Widget w) {
        final FormStylePopup popup = new FormStylePopup("images/newex_wiz.gif", "Add a field");
        popup.setStyleName( "ks-popups-Popup" );
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

                String fieldType = completions.getFieldType( variableClass, fieldName );
                model.addFieldValue( new ActionFieldValue( fieldName, "", fieldType ) );
                modeller.refreshWidget();
                popup.hide();
            }
        });



        popup.setPopupPosition( w.getAbsoluteLeft(), w.getAbsoluteTop() );
        popup.show();

    }


    private Widget valueEditor(final ActionFieldValue val) {

    	String type = "";
    	if (this.completions.isGlobalVariable(this.model.variable)) {
    		type = (String) this.completions.globalTypes.get(this.model.variable);
    	} else {
    		type = this.modeller.getModel().getBoundFact(this.model.variable).factType;
    	}

    	String[] enums = this.completions.getEnums(type, this.model.fieldValues, val.field);
    	return new ActionValueEditor(val, enums);
    }





    private Widget fieldSelector(final ActionFieldValue val) {
        return new Label(val.field);
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

}