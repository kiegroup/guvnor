/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation.processes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.explorer.navigation.RuntimeBaseTree;
import org.drools.guvnor.client.messages.Constants;

public class ProcessesTreeViewImpl extends RuntimeBaseTree implements ProcessesTreeView {

    private static Constants constants = GWT.create(Constants.class);

    private Presenter presenter;
    private TreeItem executionHistoryTreeItem;
    private TreeItem processOverviewTreeItem;

    public ProcessesTreeViewImpl() {
        super();
        executionHistoryTreeItem = addItem(constants.ExecutionHistory());
        processOverviewTreeItem = addItem(constants.ProcessOverview());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onSelection(TreeItem selectedItem) {
        if (selectedItem.equals(executionHistoryTreeItem)) {
            presenter.onExecutionHistorySelected();
        } else if (selectedItem.equals(processOverviewTreeItem)) {
            presenter.onProcessOverViewSelected();
        }
    }
}
