/*
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

package org.drools.guvnor.client.explorer.navigation;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.explorer.TabManager;
import org.drools.guvnor.client.explorer.navigation.NavigationItemBuilderOld;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.util.Util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class RulesNewMenu {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    public static MenuBar getMenu() {
        MenuBar createNewMenu = new MenuBar(true);

        createNewMenu.addItem(Util.getHeader(images.businessRule(), constants.BusinessRuleGuidedEditor()).asString(),
                true,
                new Command() {
                    public void execute() {
                        launchWizard(AssetFormats.BUSINESS_RULE, constants.NewBusinessRuleGuidedEditor(), true);
                    }
                });

        createNewMenu.addItem(Util.getHeader(images.ruleAsset(), constants.DSLBusinessRuleTextEditor()).asString(),
                true,
                new Command() {
                    public void execute() {
                        launchWizard(AssetFormats.DSL_TEMPLATE_RULE, constants.NewRuleUsingDSL(), true);
                    }
                });

        createNewMenu.addItem(Util.getHeader(images.ruleAsset(), constants.DRLRuleTechnicalRuleTextEditor()).asString(),
                true,
                new Command() {
                    public void execute() {
                        launchWizard(AssetFormats.DRL, constants.NewDRL(), true);
                    }
                });

        createNewMenu.addItem(Util.getHeader(images.spreadsheetSmall(), constants.DecisionTableSpreadsheet()).asString(),
                true,
                new Command() {
                    public void execute() {
                        launchWizard(AssetFormats.DECISION_SPREADSHEET_XLS, constants.NewDecisionTableSpreadsheet(), true);
                    }
                });

        createNewMenu.addItem(Util.getHeader(images.gdst(), constants.DecisionTableWebGuidedEditor()).asString(),
                true,
                new Command() {
                    public void execute() {
                        launchWizard(AssetFormats.DECISION_TABLE_GUIDED, constants.NewDecisionTableGuidedEditor(), true);
                    }
                });

        createNewMenu.addItem(Util.getHeader(images.testManager(), constants.TestScenario()).asString(),
                true,
                new Command() {
                    public void execute() {
                        launchWizard(AssetFormats.TEST_SCENARIO,
                                constants.CreateATestScenario(), false);
                    }
                });

        MenuBar rootMenuBar = new MenuBar(true);
        rootMenuBar.setAutoOpen(true);
        rootMenuBar.setAnimationEnabled(true);

        rootMenuBar.addItem(new MenuItem(constants.CreateNew(), createNewMenu));

        return rootMenuBar;
    }

    protected static void launchWizard(String format,
                                       String title,
                                       boolean showCats) {
        final TabManager tabManager = TabContainer.getInstance();

        NewAssetWizard pop = new NewAssetWizard(new OpenItemCommand() {
            public void open(String key) {
                tabManager.openAsset(key);
            }

            public void open(MultiViewRow[] rows) {
                for (MultiViewRow row : rows) {
                    tabManager.openAsset(row.uuid);
                }
            }
        },
                showCats,
                format,
                title);

        pop.show();
    }
}
