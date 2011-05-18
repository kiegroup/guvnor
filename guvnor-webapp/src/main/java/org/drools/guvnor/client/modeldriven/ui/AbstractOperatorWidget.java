/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.modeldriven.ui;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.ui.AbstractOperatorWidget.OperatorSelection;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract Drop-down Widget for Operators including supplementary controls for
 * CEP operator parameters
 */
public abstract class AbstractOperatorWidget<T extends BaseSingleFieldConstraint> extends Composite
    implements
    HasValueChangeHandlers<OperatorSelection> {

    private Constants     constants = ((Constants) GWT.create( Constants.class ));
    private static Images images    = GWT.create( Images.class );

    private T             bfc;
    private String[]      operators;

    public AbstractOperatorWidget(String[] operators,
                                  T bfc) {
        this.operators = operators;
        this.bfc = bfc;

        HorizontalPanel hp = new HorizontalPanel();
        hp.add( getDropDown( bfc ) );
        hp.add( getOperatorExtension( bfc ) );

        initWidget( hp );
    }

    //Additional widget for CEP operator parameters
    private Widget getOperatorExtension(T bfc) {
        final Image btnAddCEPOperators = new Image( images.clock() );
        btnAddCEPOperators.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                //Window.alert( "woot!" );
            }
        } );
        return btnAddCEPOperators;
    }

    //Template method to retrieve the operator
    protected abstract String getOperator(T bfc);

    //Actual drop-down
    private Widget getDropDown(final T bfc) {

        String selected = "";
        String selectedText = "";
        final ListBox box = new ListBox();

        box.addItem( constants.pleaseChoose(),
                         "" );
        for ( int i = 0; i < operators.length; i++ ) {
            String op = operators[i];
            box.addItem( HumanReadable.getOperatorDisplayName( op ),
                             op );
            if ( op.equals( getOperator( bfc ) ) ) {
                selected = op;
                selectedText = HumanReadable.getOperatorDisplayName( op );
                box.setSelectedIndex( i + 1 );
            }
        }

        //Fire event to ensure parent Widgets correct their state depending on selection
        final HasValueChangeHandlers<OperatorSelection> source = this;
        final OperatorSelection selection = new OperatorSelection( selected,
                                                                   selectedText );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                ValueChangeEvent.fire( source,
                                       selection );
            }

        } );

        //Signal parent Widget whenever a change happens
        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                String selected = box.getValue( box.getSelectedIndex() );
                String selectedText = box.getItemText( box.getSelectedIndex() );
                ValueChangeEvent.fire( source,
                                       new OperatorSelection( selected,
                                                              selectedText ) );
            }
        } );

        return box;
    }

    /**
     * Allow parent Widgets to register for events when the operator changes
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<OperatorSelection> handler) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

    /**
     * Details of section made; wrapping display text and associated value
     */
    public static class OperatorSelection {

        private String value;
        private String displayText;

        OperatorSelection(String value,
                          String displayText) {
            this.value = value;
            this.displayText = displayText;
        }

        public String getValue() {
            return value;
        }

        public String getDisplayText() {
            return displayText;
        }

    }

}
