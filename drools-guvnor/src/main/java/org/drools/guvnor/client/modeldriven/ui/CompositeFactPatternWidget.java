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
import org.drools.guvnor.client.common.DirtyableVerticalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.CompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import java.util.ArrayList;
import java.util.List;
import org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IFactPattern;

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

    private List<RuleModellerWidget> childWidgets;

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
            if (this.pattern != null && this.pattern.getPatterns() != null){
                IFactPattern[] patterns = this.pattern.getPatterns();
                for (int i = 0; i < patterns.length; i++) {
                    IFactPattern p = patterns[i];
                    
                    //for empty FROM / ACCUMULATE / COLLECT patterns
                    if (p.getFactType() == null){
                        continue;
                    }
                    
                    if (!completions.containsFactType(p.getFactType())){
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

        this.childWidgets = new ArrayList<RuleModellerWidget>();

        this.layout.setWidget( 0,
                               0,
                               getCompositeLabel() );
        this.layout.getFlexCellFormatter().setColSpan(0, 0, 2);
        
        //this.layout.getFlexCellFormatter().setWidth(0, 0, "15%");
        this.layout.setWidget(1, 0, new HTML("&nbsp;&nbsp;&nbsp;&nbsp;"));


        if ( this.pattern.getPatterns() != null ) {
            DirtyableVerticalPane vert = new DirtyableVerticalPane();
            IFactPattern[] facts = pattern.getPatterns();
            for ( int i = 0; i < facts.length; i++ ) {
                RuleModellerWidget widget = this.getModeller().getWidgetFactory().getWidget(this.getModeller(), facts[i], this.readOnly);
                widget.addOnModifiedCommand(new Command() {
                    public void execute() {
                        setModified(true);
                    }
                });
                childWidgets.add(widget);
                vert.add(widget);
            }
            this.layout.setWidget( 1,
                                   1,
                                   vert );
        }
    }

    protected Widget getCompositeLabel() {
        ClickHandler click = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget w = (Widget)event.getSource();
                showFactTypeSelector( w );
            }
        };
        String lbl = HumanReadable.getCEDisplayName( pattern.type );

        if (pattern.getPatterns() == null || pattern.getPatterns().length ==0) {
            lbl += " <font color='red'>" + constants.clickToAddPatterns() + "</font>";
        }

        return new ClickableLabel( lbl + ":", click, !this.readOnly ) ;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showFactTypeSelector(final Widget w) {
        final ListBox box = new ListBox();
        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        String[] facts = completions.getFactTypes();

        box.addItem(constants.Choose());
        for (int i = 0; i < facts.length; i++) {
            box.addItem(facts[i]);
        }
        box.setSelectedIndex(0);

        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle(constants.NewFactPattern());
        popup.addAttribute(constants.chooseFactType(),
                box);
        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                pattern.addFactPattern( new FactPattern( box.getItemText( box.getSelectedIndex() ) ) );
                setModified(true);
                getModeller().refreshWidget();
                popup.hide();            }
        });

        final Button fromBtn = new Button(constants.From());
        final Button fromAccumulateBtn = new Button(constants.FromAccumulate());
        final Button fromCollectBtn = new Button(constants.FromCollect());
        ClickHandler btnsClickHandler = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget sender = (Widget) event.getSource();
                if (sender == fromBtn) {
                    pattern.addFactPattern(
                            new FromCompositeFactPattern());
                } else if (sender == fromAccumulateBtn) {
                    pattern.addFactPattern(
                            new FromAccumulateCompositeFactPattern());
                } else if (sender == fromCollectBtn) {
                    pattern.addFactPattern(
                            new FromCollectCompositeFactPattern());
                } else {
                    throw new IllegalArgumentException("Unknown sender: "
                            + sender);
                }

                setModified(true);
                getModeller().refreshWidget();
                popup.hide();

            }
        };

        fromBtn.addClickHandler(btnsClickHandler);
        fromAccumulateBtn.addClickHandler(btnsClickHandler);
        fromCollectBtn.addClickHandler(btnsClickHandler);
        popup.addAttribute("", fromBtn);
        popup.addAttribute("", fromAccumulateBtn);
        popup.addAttribute("", fromCollectBtn);

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
