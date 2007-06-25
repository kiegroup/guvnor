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



import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A simple confirmation dialog. 
 * 
 * @author Michael Neale
 *
 */
public class YesNoDialog extends DialogBox {

    public YesNoDialog(String message, final Command yes) {
        setText( message );
        
        Button y = new Button("Yes");
        Button n = new Button("No");
        
        y.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                yes.execute();
                hide();
            }            
        });
        
        n.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                hide();                
            }
            
        });
        
        HorizontalPanel horiz = new HorizontalPanel();
        horiz.add( y );
        horiz.add( n );
        
        setWidget( horiz );
    }
    
}