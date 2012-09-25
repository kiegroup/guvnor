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

import java.util.List;

import com.google.gwt.place.shared.PlaceController;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.qa.SummaryTableView.Presenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SummaryTableTest {

    private SummaryTable summaryTable;
    private SummaryTableView summaryTableView;
    private PlaceController placeController;

    @Before
    public void setUp() {
        summaryTableView = mock(SummaryTableView.class);
        ClientFactory clientFactory = mock(ClientFactory.class);
        placeController = mock(PlaceController.class);
        when(
                clientFactory.getDeprecatedPlaceController()
        ).thenReturn(
                placeController
        );

        summaryTable = new SummaryTable(
                summaryTableView,
                clientFactory);
    }

    @Test
    public void emptyTable() throws Exception {
        verify(summaryTableView, never()).addRow(Matchers.<SummaryTable.Row>any());
    }

//    @Test
//    public void testGoTo() throws Exception {
//        getPresenter().openTestScenario("uuid");
//        ArgumentCaptor<AssetEditorPlace> assetEditorPlaceArgumentCaptor = ArgumentCaptor.forClass(AssetEditorPlace.class);
//        verify(placeController).goTo(assetEditorPlaceArgumentCaptor.capture());
//        assertEquals(assetEditorPlaceArgumentCaptor.getValue().getUuid(), "uuid");
//    }

    @Test
    public void testSeveralRows() throws Exception {

        addRow("scenarioName1", "uuid1", "message1", 11, "WHITE");
        addRow("scenarioName2", "uuid2", "message2", 20, "YELLOW");
        addRow("scenarioName3", "uuid3", "message3", 33, "WHITE");

        ArgumentCaptor<SummaryTable.Row> rowArgumentCaptor = ArgumentCaptor.forClass(SummaryTable.Row.class);
        verify(summaryTableView, times(3)).addRow(rowArgumentCaptor.capture());
        List<SummaryTable.Row> allValues = rowArgumentCaptor.getAllValues();

        assertContainsRow(allValues, "scenarioName1", "uuid1", "message1", 11, "WHITE");
        assertContainsRow(allValues, "scenarioName2", "uuid2", "message2", 20, "YELLOW");
        assertContainsRow(allValues, "scenarioName3", "uuid3", "message3", 33, "WHITE");
    }

    private void assertContainsRow(List<SummaryTable.Row> allValues, String scenarioName, String uuid, String message, int percentage, String backgroudColor) {
        boolean found = false;
        for (SummaryTable.Row row : allValues) {
            if (row.getScenarioName().equals(scenarioName)
                    && row.getUuid().equals(uuid)
                    && row.getMessage().equals(message)
                    && row.getPercentage() == percentage
                    && row.getBackgroundColor().equals(backgroudColor)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    private void addRow(String scenarioName, String uuid, String message, int percentage, String backgroudColor) {
        SummaryTable.Row row = new SummaryTable.Row();
        row.setScenarioName(scenarioName);
        row.setUuid(uuid);
        row.setMessage(message);
        row.setPercentage(percentage);
        row.setBackgroundColor(backgroudColor);

        summaryTable.addRow(row);
    }

    @Test
    public void presenterIsSet() throws Exception {
        verify(summaryTableView).setPresenter(getPresenter());
    }

    private Presenter getPresenter() {
        return summaryTable;
    }

}
