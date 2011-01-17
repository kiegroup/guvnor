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

import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.PackageBuilderWidget;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.util.Format;
import org.drools.guvnor.client.util.PercentageBar;
import org.drools.guvnor.client.util.ToggleLabel;
import org.drools.guvnor.client.util.ValueList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This presents the results of a bulk run.
 * @author Michael Neale
 */
public class BulkRunResultViewImpl extends Composite
    implements
    BulkRunResultView {

    interface BulkRunResultViewImplBinder
        extends
        UiBinder<Widget, BulkRunResultViewImpl> {
    }

    private static BulkRunResultViewImplBinder uiBinder          = GWT.create( BulkRunResultViewImplBinder.class );

    private Constants                          constants         = GWT.create( Constants.class );

    private Presenter                          presenter;

    @UiField
    ToggleLabel                                overAll;

    @UiField
    PercentageBar                              resultsBar;

    @UiField
    SmallLabel                                 failuresOutOfExpectations;

    @UiField
    PercentageBar                              coveredPercentBar;

    @UiField
    SmallLabel                                 ruleCoveragePercent;

    @UiField
    ValueList                                  uncoveredRules;

    @UiField
    FlexTable                                  summaryTable;

    private int                                summaryTableIndex = 0;

    public BulkRunResultViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler("closeButton")
    void close(ClickEvent clickEvent) {
        presenter.onClose();
    }

    public HasValue<Boolean> getOverAllStatus() {
        return overAll;
    }

    public HasValue<Integer> getTotalFailuresPercent() {
        return resultsBar;
    }

    public void setTotalFailures(int totalFailures,
                                 int grandTotal) {

        failuresOutOfExpectations.setText( Format.format( constants.failuresOutOFExpectations(),
                                                          totalFailures,
                                                          grandTotal ) );
    }

    public HasValue<String[]> getUncoveredRules() {
        return uncoveredRules;
    }

    public HasValue<Integer> getCoveredPercent() {
        return coveredPercentBar;
    }

    public void setCoveredPercentText(String percentCovered) {
        ruleCoveragePercent.setText( Format.format( constants.RuleCoveragePercent(),
                                                    percentCovered ) );
    }

    public void addSummary(ScenarioResultSummary summary) {

        SummaryTableRow row = new SummaryTableRow( summary.getUuid() );

        row.setName( summary.getScenarioName() );

        PercentageBar percentageBar = new PercentageBar();
        percentageBar.setWidth( SummaryTableRow.BAR_WIDTH );
        if ( summary.getFailures() > 0 ) {
            percentageBar.setPercent( summary.getFailures(),
                                      summary.getTotal() );
        } else {
            percentageBar.setPercent( 100 );
        }
        row.setPercentageBar( percentageBar );

        row.setFailuresOutOfTotalInfo( summary.getFailures(),
                                       summary.getTotal() );

        summaryTableIndex = summaryTableIndex + 1;
    }

    public void showErrors(BuilderResult errors,
                           EditItemEvent editEvent) {

        Panel err = new SimplePanel();

        PackageBuilderWidget.showBuilderErrors( errors,
                                                err,
                                                editEvent );
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    class SummaryTableRow {

        final static int BAR_WIDTH = 150;

        public SummaryTableRow(String uuid) {
            addOpenButton( uuid );
        }

        void setName(String scenarioName) {
            summaryTable.setWidget( summaryTableIndex,
                                    0,
                                    new SmallLabel( scenarioName + ":" ) );
            summaryTable.getFlexCellFormatter().setHorizontalAlignment( summaryTableIndex,
                                                                        0,
                                                                        HasHorizontalAlignment.ALIGN_RIGHT );

        }

        void setPercentageBar(PercentageBar percentageBar) {
            summaryTable.setWidget( summaryTableIndex,
                                    1,
                                    percentageBar );
        }

        void setFailuresOutOfTotalInfo(int failures,
                                       int total) {
            summaryTable.setWidget( summaryTableIndex,
                                    2,
                                    new SmallLabel( Format.format( constants.TestFailureBulkFailures(),
                                                                   failures,
                                                                   total ) ) );
        }

        private void addOpenButton(String uuid) {
            summaryTable.setWidget( summaryTableIndex,
                                    3,
                                    createOpenButton( uuid ) );
        }

        private Button createOpenButton(final String uuid) {
            Button open = new Button( constants.Open() );

            open.addClickHandler( new ClickHandler() {
                public void onClick(ClickEvent event) {
                    presenter.onOpenTestScenario( uuid );
                }
            } );

            return open;
        }
    }
}
