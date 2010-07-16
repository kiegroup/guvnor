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

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.google.gwt.core.client.GWT;

/**
 * TODO: this class should be generated via ant task:  'ant plug-editors'
 *
 * @author ant plug-editors
 */
public class RulesNewMenu {
    private static Constants constants = ((Constants) GWT.create(Constants.class));

    public static Menu getMenu(final GenericPanel manager) {
        Menu m = new Menu();

        m.addItem(new Item(constants.BusinessRuleGuidedEditor(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                manager.launchWizard(AssetFormats.BUSINESS_RULE, constants.NewBusinessRuleGuidedEditor(), true);
            }
        }, "images/business_rule.gif"));                                     //NON-NLS


        m.addItem(new Item(constants.DSLBusinessRuleTextEditor(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                manager.launchWizard(AssetFormats.DSL_TEMPLATE_RULE, constants.NewRuleUsingDSL(), true);
            }
        }, "images/business_rule.gif"));                               //NON-NLS


        m.addItem(new Item(constants.DRLRuleTechnicalRuleTextEditor(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                manager.launchWizard(AssetFormats.DRL, constants.NewDRL(), true);
            }
        }, "images/rule_asset.gif"));                    //NON-NLS

        m.addItem(new Item(constants.DecisionTableSpreadsheet(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                manager.launchWizard(AssetFormats.DECISION_SPREADSHEET_XLS, constants.NewDecisionTableSpreadsheet(), true);
            }
        }, "images/spreadsheet_small.gif"));                                   //NON-NLS

        m.addItem(new Item(constants.DecisionTableWebGuidedEditor(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                manager.launchWizard(AssetFormats.DECISION_TABLE_GUIDED, constants.NewDecisionTableGuidedEditor(), true);
            }
        }, "images/gdst.gif")); //NON-NLS

        m.addItem(new Item(constants.TestScenario(), new BaseItemListenerAdapter() {
            public void onClick(BaseItem item, EventObject e) {
                manager.launchWizard(AssetFormats.TEST_SCENARIO,
                        constants.CreateATestScenario(), false);
            }
        }, "images/test_manager.gif")); //NON-NLS

        return m;
    }


}
