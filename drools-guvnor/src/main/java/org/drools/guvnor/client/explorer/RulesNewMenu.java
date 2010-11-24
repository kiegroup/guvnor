/**
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class RulesNewMenu {
    private static Constants constants = ((Constants) GWT.create(Constants.class));
    private static Images images = (Images) GWT.create(Images.class);

    public static MenuBar getMenu(final AbstractTree manager) {
    	MenuBar createNewMenu = new MenuBar(true);

    	createNewMenu.addItem(Util.getHeader(images.businessRule(), constants.BusinessRuleGuidedEditor()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.BUSINESS_RULE, constants.NewBusinessRuleGuidedEditor(), true);
            }
        });

        createNewMenu.addItem(Util.getHeader(images.ruleAsset(), constants.DSLBusinessRuleTextEditor()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.DSL_TEMPLATE_RULE, constants.NewRuleUsingDSL(), true);
            }
        });                 
        
        createNewMenu.addItem(Util.getHeader(images.ruleAsset(), constants.DRLRuleTechnicalRuleTextEditor()),
        		true,
        		new Command() {
            public void execute() {
            	manager.launchWizard(AssetFormats.DRL, constants.NewDRL(), true);
            }
        }); 
        
        createNewMenu.addItem(Util.getHeader(images.spreadsheetSmall(), constants.DecisionTableSpreadsheet()),
        		true,
        		new Command() {
            public void execute() {
            	manager.launchWizard(AssetFormats.DECISION_SPREADSHEET_XLS, constants.NewDecisionTableSpreadsheet(), true);       
            }
        }); 

        createNewMenu.addItem(Util.getHeader(images.gdst(), constants.DecisionTableWebGuidedEditor()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.DECISION_TABLE_GUIDED, constants.NewDecisionTableGuidedEditor(), true);
            }
        });       
        
        createNewMenu.addItem(Util.getHeader(images.testManager(), constants.TestScenario()),
        		true,
        		new Command() {
            public void execute() {
                manager.launchWizard(AssetFormats.TEST_SCENARIO,
                        constants.CreateATestScenario(), false);
            }
        }); 

        MenuBar rootMenuBar = new MenuBar(true);
        rootMenuBar.setAutoOpen(true);
        rootMenuBar.setAnimationEnabled(true);
        
        rootMenuBar.addItem(new MenuItem(constants.CreateNew(), createNewMenu));

        return rootMenuBar;
    }
}
