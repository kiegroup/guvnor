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

package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.PackageBuilderWidget;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.util.PercentageBar;
import org.drools.guvnor.client.util.ToggleLabel;
import org.drools.guvnor.client.util.ValueList;

/**
 * This presents the results of a bulk run.
 */
public class BulkRunResultViewImpl extends Composite
        implements
        BulkRunResultView {

    private final ClientFactory clientFactory;

    interface BulkRunResultViewImplBinder
            extends
            UiBinder<Widget, BulkRunResultViewImpl> {

    }

    private static BulkRunResultViewImplBinder uiBinder = GWT.create(BulkRunResultViewImplBinder.class);

    private Constants constants = GWT.create(Constants.class);

    private Presenter presenter;

    @UiField
    ToggleLabel overAll;

    @UiField
    PercentageBar resultsBar;

    @UiField
    SmallLabel failuresOutOfExpectations;

    @UiField
    PercentageBar coveredPercentBar;

    @UiField
    SmallLabel ruleCoveragePercent;

    @UiField
    ValueList uncoveredRules;

    @UiField(provided = true)
    SummaryTableView summaryTableView;

    private SummaryTable summaryTable;

    public BulkRunResultViewImpl(ClientFactory clientFactory) {
        summaryTableView = new SummaryTableViewImpl();
        this.clientFactory = clientFactory;
        summaryTable = new SummaryTable(
                summaryTableView,
                clientFactory);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("closeButton") void close(ClickEvent clickEvent) {
        presenter.onClose();
    }

    public void showErrors(BuilderResult errors) {

        Panel err = new SimplePanel();

        PackageBuilderWidget.showBuilderErrors(
                errors,
                err,
                clientFactory);
    }

    public void addNormalSummaryTableRow(int totalFailures, int grandTotal, String scenarioName, int percentage, String uuid) {

        SummaryTable.Row row = new SummaryTable.Row();

        row.setMessage(constants.TestFailureBulkFailures(totalFailures, grandTotal));
        row.setScenarioName(scenarioName);
        row.setUuid(uuid);
        row.setPercentage(percentage);
        row.setBackgroundColor("WHITE");

        summaryTable.addRow(row);
    }

    public void addMissingExpectationSummaryTableRow(String scenarioName, String uuid) {

        SummaryTable.Row row = new SummaryTable.Row();

        row.setMessage(constants.MissingExpectations());
        row.setScenarioName(scenarioName);
        row.setUuid(uuid);
        row.setPercentage(0);
        row.setBackgroundColor("YELLOW");

        summaryTable.addRow(row);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setFailed() {
        overAll.setValue(false);
    }

    public void setSuccess() {
        overAll.setValue(true);
    }

    public void setFailuresOutOfExpectation(int totalFailures,
            int grandTotal) {
        failuresOutOfExpectations.setText(constants.failuresOutOFExpectations(totalFailures, grandTotal));
    }

    public void setResultsPercent(int i) {
        resultsBar.setValue(i);
    }

    public void setRulesCoveredPercent(int percentCovered) {
        coveredPercentBar.setValue(percentCovered);
        ruleCoveragePercent.setText(constants.RuleCoveragePercent(percentCovered));
    }

    public void addUncoveredRules(String uncoveredRule) {
        uncoveredRules.addItem(uncoveredRule);
    }
}
