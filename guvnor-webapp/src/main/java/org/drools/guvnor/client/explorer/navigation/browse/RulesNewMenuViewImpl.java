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

package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;
import org.drools.guvnor.client.util.Util;

public class RulesNewMenuViewImpl extends MenuBar implements RulesNewMenuView {

    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );
    private Presenter presenter;

    public RulesNewMenuViewImpl() {
        super( true );
        setAutoOpen( true );
        setAnimationEnabled( true );

        MenuBar createNewMenu = new MenuBar( true );

        createNewMenu.addItem( Util.getHeader( images.businessRule(), constants.BusinessRuleGuidedEditor() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onOpenWizard( AssetFormats.BUSINESS_RULE, true );
                    }
                } );

        createNewMenu.addItem( Util.getHeader( images.ruleAsset(), constants.DSLBusinessRuleTextEditor() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onOpenWizard( AssetFormats.DSL_TEMPLATE_RULE, true );
                    }
                } );

        createNewMenu.addItem( Util.getHeader( images.ruleAsset(), constants.DRLRuleTechnicalRuleTextEditor() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onOpenWizard( AssetFormats.DRL, true );
                    }
                } );

        createNewMenu.addItem( Util.getHeader( images.spreadsheetSmall(), constants.DecisionTableSpreadsheet() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onOpenWizard( AssetFormats.DECISION_SPREADSHEET_XLS, true );
                    }
                } );

        createNewMenu.addItem( Util.getHeader( images.gdst(), constants.DecisionTableWebGuidedEditor() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onOpenWizard( AssetFormats.DECISION_TABLE_GUIDED, true );
                    }
                } );

        createNewMenu.addItem( Util.getHeader( images.testManager(), constants.TestScenario() ).asString(),
                true,
                new Command() {
                    public void execute() {
                        presenter.onOpenWizard( AssetFormats.TEST_SCENARIO, false );
                    }
                } );


        addItem( new MenuItem( constants.CreateNew(), createNewMenu ) );
    }

    public void launchWizard( String format, boolean showCategories, ClientFactory clientFactory ) {
        new NewAssetWizard(
                showCategories,
                format,
                clientFactory ).show();
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }
}
