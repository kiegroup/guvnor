package org.drools.guvnor.client.common;
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



import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.messages.Constants;

/**
 * This presents validation error messages to the user, from the server side.
 * Such as validation or compile errors.
 * 
 * @author Michael Neale
 */
public class ValidationMessageWidget extends FormStylePopup {
    private Constants constants = ((Constants) GWT.create(Constants.class));

    /**
     * The heading is the short description in bold at the top.
     */
    public ValidationMessageWidget(String heading, String body) {

        super("images/attention_needed.png", heading); //NON-NLS
        addAttribute(constants.Detail(), details(body) );
        //addRow( close() );
        
    }


    private Widget details(String body) {
        TextArea area = new TextArea();
        area.setStyleName( "editable-Surface" );
        area.setVisibleLines( 12 );
        area.setText( body );
        area.setWidth( "100%" );       
        return area;
    }
}