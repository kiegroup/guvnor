package org.drools.guvnor.client.rulefloweditor;

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

import java.util.Map;

import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class WorkItemNode extends RuleFlowBaseNode
    implements
    AttributableNode {

    public static final String LOG   = "Log";
    public static final String EMAIL = "Email";
    public static final String DEFAULT = "Default";

    Map<String, String>        attributes;

    @Override
    public Corners getCorners() {
        return Corners.ROUNDED;
    }

    @Override
    public String getStyle() {
        return YELLOW_RULE_FLOW_NODE_STYLE;
    }

    public void addAttribute(String key,
                             String value) {
        attributes.put( key,
                        value );
    }

    public Map<String, String> getParameters() {
        return attributes;
    }

    public void setAttributes(Map<String, String> map) {
        attributes = map;
    }

    /**
     * Show parameters when clicked.
     */
    public void onClick(Widget arg0) {

        if ( parametersForm != null ) {

            parametersForm.clear();

            for ( final String key : attributes.keySet() ) {

                final String value = attributes.get( key );

                final TextBox tb = new TextBox();
                tb.setWidth( "300px" );
                tb.setText( value );

                tb.addFocusListener( new FocusListener() {
                    public void onFocus(Widget arg1) {
                        tb.selectAll();
                    }

                    public void onLostFocus(Widget arg1) {
                        attributes.put( key,
                                        tb.getText() );
                    }
                } );

                parametersForm.addAttribute( key,
                                             tb );
            }

        }
    }
}
