package org.drools.brms.client.common;
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



import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This builds on the FormStyleLayout for providing common popup features in a 
 * columnar form layout, with a title and a large (ish) icon.
 * 
 * @author Michael Neale
 */
public class FormStylePopup extends PopupPanel {

    
    private FormStyleLayout form;
 
    public FormStylePopup(String image,
                          String title) {
        super( true );
        form = new FormStyleLayout( image, title );
        this.setStyleName( "ks-popups-Popup" );

        Image close = new ImageButton("images/close.gif");
        close.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                hide();                
            }            
        });

        form.setFlexTableWidget( 0, 2, close );
        add( form );
        
    }

    public void addAttribute(String label, Widget wid) {
        form.addAttribute( label, wid );
    }
    
    public void addRow(Widget wid) {
        form.addRow( wid );
    }
}