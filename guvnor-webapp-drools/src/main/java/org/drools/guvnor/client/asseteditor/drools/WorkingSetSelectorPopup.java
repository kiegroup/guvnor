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

package org.drools.guvnor.client.asseteditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.WorkingSetManager;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;


public class WorkingSetSelectorPopup {
    private final RuleModeller modeller;
    private final Asset asset;
    private FormStylePopup pop;
    private ListBox availableFacts = new ListBox(true);
    private ListBox selectedFacts = new ListBox(true);

    public WorkingSetSelectorPopup(RuleModeller m, Asset a) {
        this.modeller = m;
        this.asset = a;

        pop = new FormStylePopup();
        Constants constants = ((Constants) GWT.create(Constants.class));
        pop.setTitle(constants.SelectWorkingSets());
        Grid g = buildDoubleList(null);

        RepositoryServiceFactory.getAssetService().listAssets(asset.getMetaData().getModuleUUID(),
                new String[]{AssetFormats.WORKING_SET}, 0, -1, "workingsetList",
                new GenericCallback<TableDataResult>() {

                    public void onSuccess(TableDataResult result) {

                        for (int i = 0; i < result.data.length; i++) {
                            if (WorkingSetManager.getInstance().isWorkingSetActive(
                                    asset.getMetaData().getModuleName(),
                                    result.data[i].id)) {
                                selectedFacts.addItem(result.data[i].getDisplayName(), result.data[i].id);
                            } else {
                                availableFacts.addItem(result.data[i].getDisplayName(), result.data[i].id);
                            }
                        }
                    }
                });

        Button save = new Button(constants.SaveAndClose());
        save.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String[] wsUUIDs = new String[selectedFacts.getItemCount()];
                for (int i = 0; i < selectedFacts.getItemCount(); i++) {
                    wsUUIDs[i] = selectedFacts.getValue(i);
                }

                WorkingSetManager.getInstance().applyWorkingSets(asset.getMetaData().getModuleName(), wsUUIDs, new Command() {
                    public void execute() {
                        LoadingPopup.close();
                        pop.hide();
                        modeller.refreshWidget();
                        modeller.verifyRule(null, true);
                    }
                });
            }

        });

        pop.addRow(g);
        pop.addRow(save);
    }

    public void show() {
        pop.show();
    }

    private Grid buildDoubleList(WorkingSetConfigData wsData) {
        Grid grid = new Grid(2, 3);

        availableFacts.setVisibleItemCount(10);
        selectedFacts.setVisibleItemCount(10);

        Grid btnsPanel = new Grid(2, 1);

        btnsPanel.setWidget(0, 0, new Button(">", new ClickHandler() {
            public void onClick(ClickEvent sender) {
                moveSelected(availableFacts, selectedFacts);
            }
        }));

        btnsPanel.setWidget(1, 0, new Button("&lt;", new ClickHandler() {
            public void onClick(ClickEvent sender) {
                moveSelected(selectedFacts, availableFacts);
            }
        }));

        grid.setWidget(0, 0, new SmallLabel("Available")); // TODO i18n
        grid.setWidget(0, 1, new SmallLabel(""));
        grid.setWidget(0, 2, new SmallLabel("Selected")); // TODO i18n
        grid.setWidget(1, 0, availableFacts);
        grid.setWidget(1, 1, btnsPanel);
        grid.setWidget(1, 2, selectedFacts);

        grid.getColumnFormatter().setWidth(0, "45%");
        grid.getColumnFormatter().setWidth(0, "10%");
        grid.getColumnFormatter().setWidth(0, "45%");
        return grid;
    }

    private void moveSelected(final ListBox from, final ListBox to) {
        int selected;
        while ((selected = from.getSelectedIndex()) != -1) {
            to.addItem(from.getItemText(selected), from.getValue(selected));
            from.removeItem(selected);
        }
    }
}
