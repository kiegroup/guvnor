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



import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.DirtyableVerticalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.CompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;

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
    private Constants constants = ((Constants) GWT.create(Constants.class));

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
        Image edit = new ImageButton( "images/edit_tiny.gif" );
        ClickListener click =  new ClickListener() {
            public void onClick(Widget w) {
                showFactTypeSelector( w );
            }
        };
        edit.setTitle(constants.AddFactToContraint());
        edit.addClickListener(click);

        horiz.add( new ClickableLabel( HumanReadable.getCEDisplayName( pattern.type ), click ) );
        horiz.add( edit );
        horiz.setStyleName( "modeller-composite-Label" );     //NON-NLS
        return horiz;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showFactTypeSelector(final Widget w) {
        final ListBox box = new ListBox();
        String[] facts = completions.getFactTypes();

        box.addItem(constants.Choose());
        for ( int i = 0; i < facts.length; i++ ) {
            box.addItem( facts[i] );
        }
        box.setSelectedIndex( 0 );

        final FormStylePopup popup = new FormStylePopup( "images/new_fact.gif", //NON-NLS
                constants.NewFactPattern());
        popup.addAttribute(constants.chooseFactType(),
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