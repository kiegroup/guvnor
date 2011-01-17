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

package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Command;

/**
 * This presents the results of a bulk run.
 * @author Michael Neale
 */
public class BulkRunResult
    implements
    BulkRunResultView.Presenter {

    private BulkTestRunResult result;
    private EditItemEvent     editEvent;
    private Command           closeCommand;

    private BulkRunResultView display;

    public BulkRunResult(BulkTestRunResult result,
                         BulkRunResultView display) {
        this.result = result;
        this.display = display;

        display.setPresenter( this );

        bind();
    }

    private void bind() {
        if ( hasErrors( result ) ) {
            display.showErrors( result.getResult(),
                                editEvent );
        } else {
            showResult();
        }
    }

    private void showResult() {
        ScenarioResultSummary[] summaries = result.getResults();
        int percentCovered = result.getPercentCovered();
        String[] rulesNotCovered = result.getRulesNotCovered();

        int grandTotal = 0;
        int totalFailures = 0;
        for ( ScenarioResultSummary scenarioResultSummary : summaries ) {

            grandTotal = grandTotal + scenarioResultSummary.getTotal();
            totalFailures = totalFailures + scenarioResultSummary.getFailures();

            display.addSummary( scenarioResultSummary );
        }

        display.getTotalFailuresPercent().setValue( calculatePercent( totalFailures,
                                                                      grandTotal ) );

        display.setTotalFailures( totalFailures,
                                  grandTotal );

        display.setCoveredPercentText( Integer.toString( percentCovered ) );

        display.getCoveredPercent().setValue( percentCovered );

        display.getOverAllStatus().setValue( totalFailures == 0 );

        if ( percentCovered < 100 ) {
            display.getUncoveredRules().setValue( rulesNotCovered );
        }
    }

    private static int calculatePercent(int numerator,
                                        int denominator) {
        int percent = 0;

        if ( denominator != 0 ) {
            percent = (int) ((((float) denominator - (float) numerator) / (float) denominator) * 100);
        }

        return percent;
    }

    private boolean hasErrors(BulkTestRunResult result) {
        return result.getResult() != null && result.getResult().getLines() != null && result.getResult().getLines().length > 0;
    }

    public void onClose() {
        closeCommand.execute();
    }

    public void onOpenTestScenario(String uuid) {
        editEvent.open( uuid );
    }

    public void setEditItemEvent(EditItemEvent editEvent) {
        this.editEvent = editEvent;
    }

    public void setCloseCommand(Command closeCommand) {
        this.closeCommand = closeCommand;
    }

}
