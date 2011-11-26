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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Base class for Pop-ups used by RuleModeller
 */
public abstract class AbstractRuleModellerSelectorPopup extends Popup {

    protected static final Constants     constants              = GWT.create( Constants.class );

    protected String                     SECTION_SEPARATOR      = "..................";

    protected int                        MIN_WIDTH              = 500;
    protected int                        MIN_HEIGHT             = 200;

    protected boolean                    bOnlyShowDSLConditions = ApplicationPreferences.getBooleanPref( "rule-modeller-onlyShowDSLStatements" );

    protected RuleModel                  model;
    protected RuleModeller               ruleModeller;
    protected SuggestionCompletionEngine completions;
    protected Map<String, Command>       cmds                   = new HashMap<String, Command>();
    protected Integer                    position;

    protected SimplePanel                choicesPanel           = new SimplePanel();
    protected FormStyleLayout            layoutPanel            = new FormStyleLayout();
    protected ListBox                    positionCbo            = new ListBox();
    protected ListBox                    choices;

    public AbstractRuleModellerSelectorPopup(RuleModel model,
                                             RuleModeller ruleModeller,
                                             String packageName,
                                             Integer position) {
        this.model = model;
        this.position = position;
        this.ruleModeller = ruleModeller;
        this.completions = SuggestionCompletionCache.getInstance().getEngineFromCache( packageName );
        this.setTitle( getPopupTitle() );
    }

    /**
     * Get a title for the pop-up
     * 
     * @return
     */
    protected abstract String getPopupTitle();

    /**
     * Executed when a selection has been made. Refreshes the underlying
     * RuleModeller widget
     */
    protected void selectSomething() {
        int sel = choices.getSelectedIndex();
        if ( sel != -1 ) {
            Command cmd = cmds.get( choices.getValue( sel ) );
            if ( cmd != null ) {
                cmd.execute();
                ruleModeller.refreshWidget();
            }
        }
    }

    /**
     * Width of pop-up, 1/4 of the client width or MIN_WIDTH
     * 
     * @return
     */
    protected int getChoicesWidth() {
        int w = Window.getClientWidth() / 4;
        if ( w < MIN_WIDTH ) {
            w = MIN_WIDTH;
        }
        return w;
    }

    /**
     * Height of pop-up, 1/2 of the client height or MIN_HEIGHT
     * 
     * @return
     */
    protected int getChoicesHeight() {
        int h = Window.getClientHeight() / 2;
        if ( h < MIN_HEIGHT ) {
            h = MIN_HEIGHT;
        }
        return h;
    }

}
