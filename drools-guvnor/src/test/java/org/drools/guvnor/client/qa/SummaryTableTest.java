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

import java.util.List;

import org.drools.guvnor.client.qa.BulkRunResultViewImpl.SummaryTableRow;
import org.drools.guvnor.client.rpc.ScenarioResultSummary;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author rikkola
 *
 */
public class SummaryTableTest {

    private SummaryTable     summaryTable;
    private SummaryTableView summaryTableView;

    @Before
    public void setUp() {
        summaryTableView = mock( SummaryTableView.class );
        summaryTable = new SummaryTable( summaryTableView );
    }

    @Test
    public void emptyTable() throws Exception {
        verify( summaryTableView,
                never() ).addRow( any() );
    }

    @Test
    public void singleRow() throws Exception {
        ScenarioResultSummary scenarioResultSummary = new ScenarioResultSummary( 0,
                                                                                 1,
                                                                                 "Test",
                                                                                 "No Description",
                                                                                 "uuid" );
        summaryTable.addRow( scenarioResultSummary );

        verify( summaryTableView ).addRow( sc )
    }
}
