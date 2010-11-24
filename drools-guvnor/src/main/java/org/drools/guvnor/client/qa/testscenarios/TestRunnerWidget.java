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

package org.drools.guvnor.client.qa.testscenarios;

import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;
import org.drools.guvnor.client.util.Format;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fixture;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:27:09
 * To change this template use File | Settings | File Templates.
 * 
 * Runs the test, plus shows a summary view of the results.
 */
public class TestRunnerWidget extends Composite {

    private Constants     constants = GWT.create( Constants.class );
    private static Images images    = GWT.create( Images.class );

    FlexTable             results   = new FlexTable();
    VerticalPanel         layout    = new VerticalPanel();

    private SimplePanel   actions   = new SimplePanel();

    public TestRunnerWidget(final ScenarioWidget parent,
                            final String packageName) {

        final Button run = new Button( constants.RunScenario() );
        run.setTitle( constants.RunScenarioTip() );
        run.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                LoadingPopup.showMessage( constants.BuildingAndRunningScenario() );
                RepositoryServiceFactory.getService().runScenario( parent.getMetaData().packageName,
                                                                   parent.getScenario(),
                                                                   new GenericCallback<SingleScenarioResult>() {
                                                                       public void onSuccess(SingleScenarioResult data) {
                                                                           LoadingPopup.close();
                                                                           layout.clear();
                                                                           layout.add( actions );
                                                                           layout.add( results );
                                                                           actions.setVisible( true );
                                                                           ScenarioRunResult result = data.result;
                                                                           if ( result.getErrors() != null ) {
                                                                               showErrors( result.getErrors() );
                                                                           } else {
                                                                               showResults( parent,
                                                                                            data );
                                                                           }
                                                                       }
                                                                   } );
            }
        } );

        actions.add( run );
        layout.add( actions );
        initWidget( layout );
    }

    private void showErrors(BuilderResultLine[] rs) {
        results.clear();
        results.setVisible( true );

        FlexTable errTable = new FlexTable();
        errTable.setStyleName( "build-Results" );
        for ( int i = 0; i < rs.length; i++ ) {
            int row = i;
            final BuilderResultLine res = rs[i];
            errTable.setWidget( row,
                                0,
                                new Image(images.error() ) );
            if ( res.assetFormat.equals( "package" ) ) {
                errTable.setText( row,
                                  1,
                                  constants.packageConfigurationProblem1() + res.message );
            } else {
                errTable.setText( row,
                                  1,
                                  "[" + res.assetName + "] " + res.message );
            }

        }
        ScrollPanel scroll = new ScrollPanel( errTable );

        scroll.setWidth( "100%" );
        results.setWidget( 0,
                           0,
                           scroll );

    }

    private void showResults(final ScenarioWidget parent,
                             final SingleScenarioResult data) {
        results.clear();
        results.setVisible( true );

        parent.setScenario( data.result.getScenario() );

        parent.setShowResults( true );
        parent.renderEditor();

        int failures = 0;
        int total = 0;
        VerticalPanel resultsDetail = new VerticalPanel();

        for ( Iterator<Fixture> fixturesIterator = data.result.getScenario().fixtures.iterator(); fixturesIterator.hasNext(); ) {
            Fixture fixture = fixturesIterator.next();
            if ( fixture instanceof VerifyRuleFired ) {

                VerifyRuleFired verifyRuleFired = (VerifyRuleFired) fixture;
                HorizontalPanel panel = new HorizontalPanel();
                if ( !verifyRuleFired.successResult.booleanValue() ) {
                    panel.add( new Image( images.warning() ) );
                    failures++;
                } else {
                    panel.add( new Image( images.testPassed() ) );
                }
                panel.add( new SmallLabel( verifyRuleFired.explanation ) );
                resultsDetail.add( panel );
                total++;
            } else if ( fixture instanceof VerifyFact ) {
                VerifyFact verifyFact = (VerifyFact) fixture;
                for ( Iterator<VerifyField> fieldIterator = verifyFact.fieldValues.iterator(); fieldIterator.hasNext(); ) {
                    total++;
                    VerifyField verifyField = fieldIterator.next();
                    HorizontalPanel panel = new HorizontalPanel();
                    if ( !verifyField.successResult.booleanValue() ) {
                        panel.add( new Image( images.warning() ) );
                        failures++;
                    } else {
                        panel.add( new Image( images.testPassed() ) );
                    }
                    panel.add( new SmallLabel( verifyField.explanation ) );
                    resultsDetail.add( panel );
                }

            } else if ( fixture instanceof ExecutionTrace ) {
                ExecutionTrace ex = (ExecutionTrace) fixture;
                if ( ex.getNumberOfRulesFired() == data.result.getScenario().maxRuleFirings ) {
                    Window.alert( Format.format( constants.MaxRuleFiringsReachedWarning(),
                                                 data.result.getScenario().maxRuleFirings ) );
                }
            }

        }

        results.setWidget( 0,
                           0,
                           new SmallLabel( constants.Results() ) );
        results.getFlexCellFormatter().setHorizontalAlignment( 0,
                                                               0,
                                                               HasHorizontalAlignment.ALIGN_RIGHT );
        if ( failures > 0 ) {
            results.setWidget( 0,
                               1,
                               ScenarioWidget.getBar( "#CC0000",
                                                      150,
                                                      failures,
                                                      total ) );
        } else {
            results.setWidget( 0,
                               1,
                               ScenarioWidget.getBar( "GREEN",
                                                      150,
                                                      failures,
                                                      total ) );
        }

        results.setWidget( 1,
                           0,
                           new SmallLabel( constants.SummaryColon() ) );
        results.getFlexCellFormatter().setHorizontalAlignment( 1,
                                                               0,
                                                               HasHorizontalAlignment.ALIGN_RIGHT );
        results.setWidget( 1,
                           1,
                           resultsDetail );
        results.setWidget( 2,
                           0,
                           new SmallLabel( constants.AuditLogColon() ) );

        final Button showExp = new Button( constants.ShowEventsButton() );
        results.setWidget( 2,
                           1,
                           showExp );
        showExp.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                showExp.setVisible( false );
                results.setWidget( 2,
                                   1,
                                   doAuditView( data.auditLog ) );
            }
        } );

    }

    private Widget doAuditView(List<String[]> auditLog) {
        VerticalPanel vp = new VerticalPanel();
        vp.add( new HTML( "<hr/>" ) );
        FlexTable g = new FlexTable();
        int row = 0;
        boolean firing = false;
        for ( int i = 0; i < auditLog.size(); i++ ) {
            String[] lg = auditLog.get( i );

            int id = Integer.parseInt( lg[0] );
            if ( id <= 7 ) {
                if ( id <= 3 ) {
                    if ( !firing ) {
                        g.setWidget( row,
                                     0,
                                     new Image( "images/audit_events/" + lg[0] + ".gif" ) );
                        g.setWidget( row,
                                     1,
                                     new SmallLabel( lg[1] ) );
                    } else {
                        g.setWidget( row,
                                     1,
                                     hz( new Image( "images/audit_events/" + lg[0] + ".gif" ),
                                         new SmallLabel( lg[1] ) ) );
                    }
                    row++;
                } else if ( id == 6 ) {
                    firing = true;
                    g.setWidget( row,
                                 0,
                                 new Image( "images/audit_events/" + lg[0] + ".gif" ) );
                    g.setWidget( row,
                                 1,
                                 new SmallLabel( "<b>" + lg[1] + "</b>" ) );
                    row++;
                } else if ( id == 7 ) {
                    firing = false;
                } else {
                    g.setWidget( row,
                                 0,
                                 new Image( "images/audit_events/" + lg[0] + ".gif" ) );
                    g.setWidget( row,
                                 1,
                                 new SmallLabel( "<font color='grey'>" + lg[1] + "</font>" ) );
                    row++;
                }
            } else {
                g.setWidget( row,
                             0,
                             new Image( "images/audit_events/misc_event.gif" ) );
                g.setWidget( row,
                             1,
                             new SmallLabel( "<font color='grey'>" + lg[1] + "</font>" ) );
                row++;
            }
        }
        vp.add( g );
        vp.add( new HTML( "<hr/>" ) );
        return vp;
    }

    private Widget hz(Image image,
                      SmallLabel smallLabel) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( image );
        h.add( smallLabel );
        return h;
    }
}
