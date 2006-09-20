/*
 * Copyright 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.brms.client;

import org.drools.brms.client.breditor.BREditor;
import org.drools.brms.client.breditor.EditableLine;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Demonstrates {@link com.google.gwt.user.client.ui.PopupPanel} and
 * {@link com.google.gwt.user.client.ui.DialogBox}.
 */
public class Packages extends JBRMSFeature
    implements
    ClickListener {

    /**
     * A very simple popup that closes automatically when you click off of it.
     */
    private static class MyPopup extends PopupPanel {
        public MyPopup() {
            super( true );

            ListBox list = new ListBox();
            list.addItem( "There is a person {bob} who is blah" );
            list.addItem( "There is a cheese {bob} who is {type}" );
            list.addItem( "- age is less then {number}" );
            list.addItem( "- likes doing '{number}'" );

            HTML contents = new HTML( "Click anywhere outside this popup to make it disappear." );
            contents.setWidth( "128px" );

            //add(contents);
            add( list );
            //add(new Label("this is a label"));      
            setStyleName( "ks-popups-Popup" );
        }
    }

    public static ComponentInfo init() {
        return new ComponentInfo( "Packages",
                                  "This is where you configure packages of rules." + "You select rules to belong to packages, and what version they are. A rule can "
                                          + "appear in more then one package, and possibly even different versions of the rule." ) {
            public JBRMSFeature createInstance() {
                return new Packages();
            }

            public Image getImage() {
                return new Image( "images/package.gif" );
            }
        };
    }

    private Button fPopupButton = new Button( "Show Popup",
                                              this );

    public Packages() {
        VerticalPanel panel = new VerticalPanel();
        panel.add( fPopupButton );
        panel.add( new BREditor() );
        
        panel.setSpacing( 8 );
        initWidget( panel );
    }

    public void onClick(Widget sender) {
        if ( sender == fPopupButton ) {
            MyPopup p = new MyPopup();
            int left = sender.getAbsoluteLeft() + 10;
            int top = sender.getAbsoluteTop() + 10;
            p.setPopupPosition( left,
                                top );
            p.show();
        }
    }

    public void onShow() {
    }
}
