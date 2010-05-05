package org.drools.guvnor.client.ruleeditor;

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

import java.util.List;

import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;

/**
 * This is a popup list for "content assistance" - although on the web, 
 * its not assistance - its mandatory ;)
 */
public class ChoiceList extends PopupPanel {

    private ListBox             list;
    private final DSLSentence[] sentences;
    private HorizontalPanel     buttons;
    private TextBox             filter;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    /**
    * Pass in a list of suggestions for the popup lists.
    * Set a click listener to get notified when the OK button is clicked.
    */
    public ChoiceList(final DSLSentence[] sen,
                      final DSLRuleEditor self) {
        super( true );

        this.sentences = sen;
        filter = new TextBox();
        filter.setWidth( "100%" );
        final String defaultMessage = constants.enterTextToFilterList();
        filter.setText( defaultMessage );
        filter.addFocusListener( new FocusListener() {
            public void onFocus(Widget w) {
                filter.setText( "" );
            }

            public void onLostFocus(Widget w) {
                filter.setText( defaultMessage );
            }
        } );
        filter.addKeyboardListener( new KeyboardListener() {

            public void onKeyDown(Widget arg0,
                                  char arg1,
                                  int arg2) {

            }

            public void onKeyPress(Widget arg0,
                                   char arg1,
                                   int arg2) {
            }

            public void onKeyUp(Widget arg0,
                                char arg1,
                                int arg2) {
                if ( arg1 == KEY_ENTER ) {
                    applyChoice( self );
                } else {
                    populateList( ListUtil.filter( sentences,
                                                   filter.getText() ) );
                }
            }

        } );
        filter.setFocus( true );

        VerticalPanel panel = new VerticalPanel();
        panel.add( filter );

        list = new ListBox();
        list.setVisibleItemCount( 5 );

        populateList( ListUtil.filter( this.sentences,
                                       "" ) );

        panel.add( list );

        Button ok = new Button( constants.OK() );
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget btn) {
                applyChoice( self );
            }
        } );

        Button cancel = new Button(constants.Cancel());
        cancel.addClickListener( new ClickListener() {
            public void onClick(Widget btn) {
                hide();
            }
        } );

        buttons = new HorizontalPanel();

        buttons.add( ok );
        buttons.add( cancel );

        panel.add( buttons );

        add( panel );
        setStyleName( "ks-popups-Popup" );  //NON-NLS

    }

    private void applyChoice(final DSLRuleEditor self) {
        self.insertText( getSelectedItem() );
        hide();
    }

    private void populateList(List filtered) {
        list.clear();
        for ( int i = 0; i < filtered.size(); i++ ) {
            list.addItem( ((DSLSentence) filtered.get( i )).sentence );
        }
    }

    public String getSelectedItem() {
        return list.getItemText( list.getSelectedIndex() );
    }

}