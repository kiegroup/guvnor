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
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.DirtyableVerticalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents a top level CE, like an OR, NOT, EXIST etc...
 * Contains a list of FactPatterns.
 *
 * @author Michael Neale
 *
 */
public class CompositeFactPatternWidget extends RuleModellerWidget {

    protected final SuggestionCompletionEngine completions;
    protected CompositeFactPattern             pattern;
    protected DirtyableFlexTable                             layout;
    protected Constants constants = ((Constants) GWT.create(Constants.class));
    protected boolean readOnly;

    private List<FactPatternWidget> childWidgets;

    public CompositeFactPatternWidget(RuleModeller modeller,
                                      CompositeFactPattern pattern) {
        this(modeller, pattern, null);
    }

    public CompositeFactPatternWidget(RuleModeller modeller,
                                      CompositeFactPattern pattern,
                                      Boolean readOnly) {
        super(modeller);
        this.completions = modeller.getSuggestionCompletions();
        this.pattern = pattern;

        this.layout = new DirtyableFlexTable();
        this.layout.setStyleName( "model-builderInner-Background" );

        if (readOnly != null){
            this.readOnly = readOnly;
        }else{
            this.readOnly = false;
            if (this.pattern != null && this.pattern.patterns != null){
                for (int i = 0; i < this.pattern.patterns.length; i++) {
                    FactPattern factPattern = this.pattern.patterns[i];
                    if (!completions.containsFactType(factPattern.factType)){
                        this.readOnly = true;
                        break;
                    }
                }
            }
        }

        if (this.readOnly){
            layout.addStyleName("editor-disabled-widget");
        }

        doLayout();
        initWidget( layout );
    }

    protected void doLayout() {

        this.childWidgets = new ArrayList<FactPatternWidget>();

        this.layout.setWidget( 0,
                               0,
                               getCompositeLabel() );
        this.layout.getFlexCellFormatter().setColSpan(0, 0, 2);
        
        //this.layout.getFlexCellFormatter().setWidth(0, 0, "15%");
        this.layout.setWidget(1, 0, new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));


        if ( pattern.patterns != null ) {
            DirtyableVerticalPane vert = new DirtyableVerticalPane();
            FactPattern[] facts = pattern.patterns;
            for ( int i = 0; i < facts.length; i++ ) {
                FactPatternWidget factPatternWidget = new FactPatternWidget(this.getModeller(), facts[i], false, this.readOnly);
                factPatternWidget.addOnModifiedCommand(new Command() {
                    public void execute() {
                        setModified(true);
                    }
                });
                childWidgets.add(factPatternWidget);
                vert.add(factPatternWidget);
            }
            this.layout.setWidget( 1,
                                   1,
                                   vert );
        }
    }

    protected Widget getCompositeLabel() {

        ClickListener click =  new ClickListener() {
            public void onClick(Widget w) {
                showFactTypeSelector( w );
            }
        };
        String lbl = HumanReadable.getCEDisplayName( pattern.type );

        if (pattern.patterns == null || pattern.patterns.length ==0) {
            lbl += " <font color='red'>" + constants.clickToAddPatterns() + "</font>";
        }

        return new ClickableLabel( lbl + ":", click, !this.readOnly ) ;
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

        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle(constants.NewFactPattern());
        popup.addAttribute(constants.chooseFactType(),
                            box );

        box.addChangeListener( new ChangeListener() {
            public void onChange(Widget w) {
                pattern.addFactPattern( new FactPattern( box.getItemText( box.getSelectedIndex() ) ) );
                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        popup.show();
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

}