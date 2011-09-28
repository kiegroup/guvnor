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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.util.PercentageBar;

public class SummaryTableViewImpl extends Composite
        implements
        SummaryTableView {

    private Constants constants = GWT.create(Constants.class);

    private FlexTable flexTable = new FlexTable();

    private Presenter presenter;

    private int summaryTableIndex = 0;

    public SummaryTableViewImpl() {
        initWidget(flexTable);
    }

    public void addRow(SummaryTable.Row row) {
        SummaryTableRow summaryTableRow = new SummaryTableRow(row.getUuid());
        summaryTableRow.setName(row.getScenarioName());
        summaryTableRow.setPercentage(row.getPercentage(), row.getBackgroundColor());
        summaryTableRow.setMessage(row.getMessage());
        summaryTableIndex = summaryTableIndex + 1;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    class SummaryTableRow {

        final static int BAR_WIDTH = 150;

        public SummaryTableRow(String uuid) {
            addOpenButton(uuid);
        }

        void setName(String scenarioName) {
            flexTable.setWidget(summaryTableIndex,
                    0,
                    new SmallLabel(scenarioName + ":"));
            flexTable.getFlexCellFormatter().setHorizontalAlignment(summaryTableIndex,
                    0,
                    HasHorizontalAlignment.ALIGN_RIGHT);

        }

        void setMessage(String message) {
            flexTable.setWidget(summaryTableIndex,
                    2,
                    new SmallLabel(message));
        }

        private void addOpenButton(String uuid) {
            flexTable.setWidget(summaryTableIndex,
                    3,
                    createOpenButton(uuid));
        }

        private Button createOpenButton(final String uuid) {
            Button open = new Button(constants.Open());

            open.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    presenter.openTestScenario(uuid);
                }
            });

            return open;
        }

        public void setPercentage(int percentage, String color) {
            setPercentageBar(createPercentageBar(percentage, color));
        }

        private PercentageBar createPercentageBar(int percentage, String color) {
            PercentageBar percentageBar = new PercentageBar();
            percentageBar.setPercent(percentage);
            percentageBar.setBackgroundColor(color);
            percentageBar.setWidth(SummaryTableRow.BAR_WIDTH);
            return percentageBar;
        }

        void setPercentageBar(PercentageBar percentageBar) {
            flexTable.setWidget(summaryTableIndex,
                    1,
                    percentageBar);
        }

    }
}
