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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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

    private static BulkRunResultViewImplBinder uiBinder  = GWT.create( BulkRunResultViewImplBinder.class );

    private Constants                          constants = GWT.create( Constants.class );

    private Presenter                          presenter;

    @UiField
    Label                                      overAll;

    @UiField
    PercentageBar                              resultsBar;

    @UiField
    SmallLabel                                 failuresOutOfExpectations;

    @UiField
    PercentageBar                              coveredPercentBar;

    @UiField
    SmallLabel                                 ruleCoveragePercent;

    @UiField
    ListBox                                    uncoveredRules;

    @UiField
    FlexTable                                  summaryTable;

    public BulkRunResultViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler("closeButton")
    void close(ClickEvent clickEvent) {
        presenter.onClose();
    }

    public void setOverAllStatus(boolean success) {
        overAll.setText( success ? constants.SuccessOverall() : constants.FailureOverall() );
    }

    public void setTotalFailures(int totalFailures,
                                 int grandTotal) {

        if ( totalFailures > 0 ) {
            resultsBar.setColour( "#CC0000" );
            resultsBar.setPercent( totalFailures,
                                   grandTotal );
        } else {
            resultsBar.setColour( "GREEN" );
            resultsBar.setPercent( 100 );
        }

        failuresOutOfExpectations.setText( Format.format( constants.failuresOutOFExpectations(),
                                                          totalFailures,
                                                          grandTotal ) );
    }

    public void setUncoveredRules(String[] rulesNotCovered) {
        for ( int i = 0; i < rulesNotCovered.length; i++ ) {
            uncoveredRules.addItem( rulesNotCovered[i] );
        }
        if ( rulesNotCovered.length > 20 ) {
            uncoveredRules.setVisibleItemCount( 20 );
        } else {
            uncoveredRules.setVisibleItemCount( rulesNotCovered.length );
        }
    }

    public void setCoveredPercent(int percentCovered) {
        if ( percentCovered < 100 ) {
            coveredPercentBar.setColour( "YELLOW" );
            coveredPercentBar.setPercent( percentCovered );
        } else {
            coveredPercentBar.setColour( "GREEN" );
            coveredPercentBar.setPercent( 100 );
        }

        coveredPercentBar.render();

        ruleCoveragePercent.setText( Format.format( constants.RuleCoveragePercent(),
                                                    percentCovered ) );
    }

    public void addSummary(int i,
                           final ScenarioResultSummary summary) {

        //now render this summary
        summaryTable.setWidget( i,
                                0,
                                new SmallLabel( summary.getScenarioName() + ":" ) );
        summaryTable.getFlexCellFormatter().setHorizontalAlignment( i,
                                                                    0,
                                                                    HasHorizontalAlignment.ALIGN_RIGHT );

        if ( summary.getFailures() > 0 ) {
            summaryTable.setWidget( i,
                                    1,
                                    new PercentageBar( "#CC0000",
                                                       150,
                                                       summary.getFailures(),
                                                       summary.getTotal() ) );
        } else {
            summaryTable.setWidget( i,
                                    1,
                                    new PercentageBar( "GREEN",
                                                       150,
                                                       100 ) );
        }

        summaryTable.setWidget( i,
                                2,
                                new SmallLabel( Format.format( constants.TestFailureBulkFailures(),
                                                               summary.getFailures(),
                                                               summary.getTotal() ) ) );
        Button open = new Button( constants.Open() );
        open.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                presenter.onOpenTestScenario( summary.getUuid() );
            }
        } );
        summaryTable.setWidget( i,
                                3,
                                open );
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
}
