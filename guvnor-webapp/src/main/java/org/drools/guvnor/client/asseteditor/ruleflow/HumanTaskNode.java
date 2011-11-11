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

package org.drools.guvnor.client.asseteditor.ruleflow;

import java.util.Map;

import org.drools.guvnor.client.resources.FlowImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HumanTaskNode extends RuleFlowBaseNode
    implements
    AttributableNode {

    private static FlowImages   images = GWT.create( FlowImages.class );

    private Map<String, String> attributes;

    @Override
    public Corners getCorners() {
        return Corners.ROUNDED;
    }

    @Override
    public ImageResource getImagePath() {
        return images.humanTask();
    }

    @Override
    public String getStyle() {
        return YELLOW_RULE_FLOW_NODE_STYLE;
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

                tb.addFocusHandler( new FocusHandler() {
                    public void onFocus(FocusEvent event) {
                        tb.selectAll();
                    }
                } );
                tb.addBlurHandler( new BlurHandler() {
                    public void onBlur(BlurEvent event) {
                        attributes.put( key,
                                        tb.getText() );
                    }
                } );

                parametersForm.addAttribute( key,
                                             tb );
            }

        }
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
}
