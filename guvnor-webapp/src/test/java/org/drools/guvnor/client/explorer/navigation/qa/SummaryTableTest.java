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

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.qa.SummaryTable;
import org.drools.guvnor.client.explorer.navigation.qa.SummaryTableView;
import org.drools.guvnor.client.explorer.navigation.qa.SummaryTableView.Presenter;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SummaryTableTest {

    private SummaryTable summaryTable;
    private SummaryTableView summaryTableView;

    @Before
    public void setUp() {
        summaryTableView = mock( SummaryTableView.class );
        ClientFactory clientFactory = mock( ClientFactory.class );
        summaryTable = new SummaryTable(
                summaryTableView,
                clientFactory );
    }

    @Test
    public void emptyTable() throws Exception {
        verify( summaryTableView,
                never() ).addRow( anyInt(),
                anyInt(),
                anyString(),
                anyString() );
    }

    @Test
    public void singleRow() throws Exception {

        addTableRow( 0,
                1,
                "Test",
                "No Description",
                "uuid" );

        verifyRowWasAdded( 0,
                1,
                "Test",
                "uuid" );
    }

    @Test
    public void severalRows() throws Exception {

        addTableRow( 0,
                1,
                "Test1",
                "No Description",
                "uuid1" );
        addTableRow( 2,
                5,
                "Test2",
                "No Description",
                "uuid2" );
        addTableRow( 6,
                10,
                "Test3",
                "No Description",
                "uuid3" );

        verifyRowWasAdded( 0,
                1,
                "Test1",
                "uuid1" );
        verifyRowWasAdded( 2,
                5,
                "Test2",
                "uuid2" );
        verifyRowWasAdded( 6,
                10,
                "Test3",
                "uuid3" );
    }

    @Test
    public void presenterIsSet() throws Exception {
        verify( summaryTableView ).setPresenter( getPresenter() );
    }

    private Presenter getPresenter() {
        return summaryTable;
    }

    private void addTableRow( int failures,
                              int total,
                              String scenarioName,
                              String description,
                              String uuid ) {
        summaryTable.addRow( new ScenarioResultSummary( failures,
                total,
                scenarioName,
                description,
                uuid ) );
    }

    private void verifyRowWasAdded( int failures,
                                    int total,
                                    String scenarioName,
                                    String uuid ) {
        verify( summaryTableView ).addRow( failures,
                total,
                scenarioName,
                uuid );
    }

}
