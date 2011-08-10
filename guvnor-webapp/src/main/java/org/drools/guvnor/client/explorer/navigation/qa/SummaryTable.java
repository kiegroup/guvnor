/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.explorer.navigation.qa;

import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.qa.SummaryTableView.Presenter;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;

public class SummaryTable
        implements
        Presenter {

    private SummaryTableView summaryTableView;
    private final ClientFactory clientFactory;

    public SummaryTable( SummaryTableView summaryTableView,
                         ClientFactory clientFactory ) {
        this.summaryTableView = summaryTableView;
        this.clientFactory = clientFactory;

        summaryTableView.setPresenter( this );
    }

    public void addRow( ScenarioResultSummary scenarioResultSummary ) {
        summaryTableView.addRow( scenarioResultSummary.getFailures(),
                scenarioResultSummary.getTotal(),
                scenarioResultSummary.getScenarioName(),
                scenarioResultSummary.getUuid() );
    }

    public void openTestScenario( String uuid ) {
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( uuid ) );
    }

}
