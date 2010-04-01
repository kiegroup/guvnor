package org.drools.guvnor.client.ruleeditor;
/*
 * Copyright 2005 JBoss Inc
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


import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.TableDataResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import org.drools.guvnor.client.modeldriven.ui.RuleModeller;
import org.drools.guvnor.client.packages.WorkingSetManager;

public class WorkingSetSelectorPopup {

    private final RuleModeller modeller;
    private final RuleAsset asset;
    private TreePanel treePanel;
    private Button save;
    private FormStylePopup pop;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public WorkingSetSelectorPopup(RuleModeller m, RuleAsset a) {

        this.modeller = m;
        this.asset = a;

        pop = new FormStylePopup();
        pop.setTitle(constants.SelectWorkingSets());

        treePanel = new TreePanel();
        treePanel.setWidth("100%");
        treePanel.setHeight("100%");
        final TreeNode root = new TreeNode("ROOT");
        root.setChecked(false);
        treePanel.setRootNode(root);
        treePanel.setRootVisible(false);


		RepositoryServiceFactory.getService().listAssets(asset.metaData.packageUUID,
				new String[] { AssetFormats.WORKING_SET }, 0, -1, "workingsetList",
				new GenericCallback<TableDataResult>() {

					public void onSuccess(TableDataResult result) {

						for (int i = 0; i < result.data.length; i++) {
							TreeNode node = new TreeNode(result.data[i].getDisplayName());
							node.setUserObject(result.data[i].id);
							node.setChecked(WorkingSetManager.getInstance().isWorkingSetActive(
									asset.metaData.packageName, result.data[i].id));
							root.appendChild(node);
						}
					}
				});

        save = new Button(constants.SaveAndClose());
        save.addClickListener(new ClickListener() {

            public void onClick(Widget widget) {
                TreeNode[] checked = treePanel.getChecked();

                String[] wsUUIDs = new String[checked.length];
                for (int i = 0; i < checked.length; i++) {
                    TreeNode treeNode = checked[i];
                    wsUUIDs[i] = (String) treeNode.getUserObject();
                }


                WorkingSetManager.getInstance().applyWorkingSets(asset.metaData.packageName, wsUUIDs, new Command() {

                        public void execute() {
                            LoadingPopup.close();
                            pop.hide();
                            modeller.refreshWidget();
                        }
                    });
            }
        });

        treePanel.setHeight(100);
        treePanel.setAutoScroll(true);
        treePanel.expandAll();
        pop.addRow(treePanel);
        pop.addRow(save);
    }

    public void show() {
        treePanel.expandAll();
        pop.show();

    }
}
