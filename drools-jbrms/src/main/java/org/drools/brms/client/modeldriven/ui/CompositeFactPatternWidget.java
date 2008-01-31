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
import org.drools.brms.client.common.DirtyableVerticalPane;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.modeldriven.HumanReadable;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brl.FactPattern;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This represents a top level CE, like an OR, NOT, EXIST etc...
 * Contains a list of FactPatterns.
 *
 * @author Michael Neale
 *
 */
public class CompositeFactPatternWidget extends DirtyableComposite {

    private final SuggestionCompletionEngine completions;
    private CompositeFactPattern             pattern;
    private DirtyableFlexTable                             layout;
    private RuleModeller                     modeller;

    public CompositeFactPatternWidget(RuleModeller modeller,
                                      CompositeFactPattern pattern,
                                      SuggestionCompletionEngine completions) {
        this.completions = completions;
        this.pattern = pattern;
        this.modeller = modeller;

        this.layout = new DirtyableFlexTable();
        this.layout.setStyleName( "model-builderInner-Background" );

        doLayout();
        initWidget( layout );
    }

    private void doLayout() {
        this.layout.setWidget( 0,
                               0,
                               getCompositeLabel() );

        if ( pattern.patterns != null ) {
            DirtyableVerticalPane vert = new DirtyableVerticalPane();
            FactPattern[] facts = pattern.patterns;
            for ( int i = 0; i < facts.length; i++ ) {
                vert.add( new FactPatternWidget( modeller,
                                                 facts[i],
                                                 this.completions,
                                                 false ) );
            }
            this.layout.setWidget( 0,
                                   1,
                                   vert );
        }
    }

    private Widget getCompositeLabel() {

        HorizontalPanel horiz = new HorizontalPanel();
        Image edit = new ImageButton( "images/add_field_to_fact.gif" );
        edit.setTitle( "Add a fact to this constraint. If it is an 'or' type, it will need at least 2." );
        edit.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                showFactTypeSelector( w );
            }
        } );

        horiz.add( new Label( HumanReadable.getCEDisplayName( pattern.type ) ) );
        horiz.add( edit );
        horiz.setStyleName( "modeller-composite-Label" );
        return horiz;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showFactTypeSelector(final Widget w) {
        final ListBox box = new ListBox();
        String[] facts = completions.getFactTypes();

        box.addItem( "Choose..." );
        for ( int i = 0; i < facts.length; i++ ) {
            box.addItem( facts[i] );
        }
        box.setSelectedIndex( 0 );

        final FormStylePopup popup = new FormStylePopup( "images/new_fact.gif",
                                                         "New fact pattern..." );
        popup.addAttribute( "choose fact type",
                            box );

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                pattern.addFactPattern( new FactPattern( box.getItemText( box.getSelectedIndex() ) ) );
                modeller.refreshWidget();
                popup.hide();
            }
        } );

        popup.show();
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }



}