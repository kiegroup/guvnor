/**
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

package org.drools.guvnor.client.explorer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;

import com.gwtext.client.util.Format;

import org.drools.guvnor.client.images.Images;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.qa.AnalysisView;
import org.drools.guvnor.client.qa.ScenarioPackageView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.rulelist.EditItemEvent;

public class QATree extends AbstractTree {
    private static Constants constants = GWT.create(Constants.class);
    private static Images images = (Images) GWT.create(Images.class);       

    private Map<TreeItem, String> itemWidgets = new HashMap<TreeItem, String>();

    public QATree(ExplorerViewCenterPanel tabbedPanel) {
        super(tabbedPanel);
        this.name = constants.QA1();
        this.image = images.analyze();
        
        mainTree = ExplorerNodeConfig.getQAStructure(itemWidgets);

        //Add Selection listener
        mainTree.addSelectionHandler(this);
    }
    
    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();
        
        if (item.getUserObject() instanceof PackageConfigData) {            
			PackageConfigData pc = (PackageConfigData) item.getUserObject();
			String id = itemWidgets.get(item);
			
			if (ExplorerNodeConfig.TEST_SCENARIOS.equals(id)) {
				if (!centertabbedPanel.showIfOpen("scenarios" + pc.uuid)) { // NON-NLS
					final EditItemEvent edit = new EditItemEvent() {
						public void open(String key) {
							centertabbedPanel.openAsset(key);
						}

						public void open(MultiViewRow[] rows) {
							for (MultiViewRow row : rows) {
								centertabbedPanel.openAsset(row.uuid);
							}
						}
					};

					String m = Format.format(constants.ScenariosForPackage(),
							pc.name);
					centertabbedPanel.addTab(m, true, new ScenarioPackageView(
							pc.uuid, pc.name, edit, centertabbedPanel),
							"scenarios" + pc.uuid);
				}
			} else if (ExplorerNodeConfig.ANALYSIS.equals(id)) {
                if (!centertabbedPanel.showIfOpen("analysis" + pc.uuid)) { //NON-NLS
                    final EditItemEvent edit = new EditItemEvent() {
                        public void open(String key) {
                        	centertabbedPanel.openAsset(key);
                        }

                        public void open(MultiViewRow[] rows) {
                            for (MultiViewRow row : rows) {
                            	centertabbedPanel.openAsset(row.uuid);
                            }
                        }
                    };
                    
                    String m = Format.format(constants.AnalysisForPackage(),
                                              pc.name);
                    centertabbedPanel.addTab(m,
                                        true,
                                        new AnalysisView(pc.uuid,
                                        		         pc.name,
                                        		         edit),
                                            "analysis" + pc.uuid); 
                }
			}
        }
    }  
}
