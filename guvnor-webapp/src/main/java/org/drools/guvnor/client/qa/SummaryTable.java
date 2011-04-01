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
package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.explorer.TabContainer;
import org.drools.guvnor.client.qa.SummaryTableView.Presenter;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;

public class SummaryTable
    implements
    Presenter {

    private SummaryTableView summaryTableView;

    public SummaryTable(SummaryTableView summaryTableView) {
        this.summaryTableView = summaryTableView;

        summaryTableView.setPresenter( this );
    }

    public void addRow(ScenarioResultSummary scenarioResultSummary) {
        summaryTableView.addRow( scenarioResultSummary.getFailures(),
                                 scenarioResultSummary.getTotal(),
                                 scenarioResultSummary.getScenarioName(),
                                 scenarioResultSummary.getUuid() );
    }

    public void openTestScenario(String uuid) {
        TabContainer.getInstance().openAsset( uuid );
    }

}
