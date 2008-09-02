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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class RuleFlowRoundPanel extends RuleFlowRoundedPanel {

    /**
     * Lookup table for corner border width
     */
    private final static String[] CORNERBORDER = {"1px", "1px", "1px", "1px", "1px", "1px", "1px", "2px", "2px", "2px", "3px", "3px", "2px", "2px", "4px", "1px"};

    /**
     * Lookup table for corner height
     */
    private final static String[] CORNERHEIGHT = {"1px", "2px", "2px", "1px", "1px", "1px", "1px", "1px", "1px", "1px", "1px", "1px", "1px", "1px", "1px", "1px"};

    private final static String[] CORNERMARGIN = {"1px", "1px", "1px", "2px", "2px", "3px", "4px", "5px", "6px", "8px", "9px", "11px", "14px", "16px", "18px", "22px"};

    public RuleFlowRoundPanel(int cornerHeight) {
        //        super( ALL,
        //               cornerHeight );
        super( ALL,
               9 );
    }

    protected Element addLine(int corner,
                              int heightIndex) {
        String margin = "0 " + RuleFlowRoundPanel.CORNERMARGIN[heightIndex];

        Element div = DOM.createDiv();

        DOM.setStyleAttribute( div,
                               "fontSize",
                               "0px" );
        DOM.setStyleAttribute( div,
                               "height",
                               RuleFlowRoundPanel.CORNERHEIGHT[heightIndex] );
        DOM.setStyleAttribute( div,
                               "borderWidth",
                               "0 " + RuleFlowRoundPanel.CORNERBORDER[heightIndex] );
        DOM.setStyleAttribute( div,
                               "lineHeight",
                               RuleFlowRoundPanel.CORNERHEIGHT[heightIndex] );
        DOM.setStyleAttribute( div,
                               "margin",
                               margin );
        DOM.setInnerHTML( div,
                          "&nbsp;" );
        DOM.appendChild( getElement(), // body
                         div );

        return div;
    }
}
