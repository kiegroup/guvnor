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

public class SummaryTable
        implements
        Presenter {

    private SummaryTableView summaryTableView;
    private final ClientFactory clientFactory;

    public SummaryTable(SummaryTableView summaryTableView,
            ClientFactory clientFactory) {
        this.summaryTableView = summaryTableView;
        this.clientFactory = clientFactory;

        summaryTableView.setPresenter(this);
    }

    public void addRow(Row row) {
        summaryTableView.addRow(row);
    }

    public void openTestScenario(String uuid) {
        clientFactory.getDeprecatedPlaceController().goTo(new AssetEditorPlace(uuid));
    }

    static class Row {

        private String scenarioName;
        private String uuid;
        private String message;
        private int percentage;
        private String backgroundColor;

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public String getScenarioName() {
            return scenarioName;
        }

        public void setScenarioName(String scenarioName) {
            this.scenarioName = scenarioName;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getPercentage() {
            return percentage;
        }

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }
    }

}
