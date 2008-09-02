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

package org.drools.guvnor.client.rulefloweditor;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.user.client.DOM;

/**
 * @author trikkola
 *
 */
public class RuleFlowRoundedPanel extends RoundedPanel {

    /**
     * @param all
     * @param cornerHeight
     */
    public RuleFlowRoundedPanel(int all,
                                int cornerHeight) {
        super( all,
               cornerHeight );
    }

    /**
     * Sets this nodes top and bottom row to black.
     * 
     * @param node
     * @param rp
     */
    public void setTopAndBottomRowColor(String color) {
        DOM.setStyleAttribute( divt[0],
                               "backgroundColor",
                               color );
        DOM.setStyleAttribute( divb[0],
                               "backgroundColor",
                               color );
    }
}
