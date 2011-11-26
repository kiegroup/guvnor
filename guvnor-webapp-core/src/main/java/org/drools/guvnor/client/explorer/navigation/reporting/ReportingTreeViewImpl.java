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

package org.drools.guvnor.client.explorer.navigation.reporting;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.messages.Constants;

public class ReportingTreeViewImpl extends Tree implements ReportingTreeView {

    private static Constants constants = GWT.create(Constants.class);

    private Presenter presenter;

    public ReportingTreeViewImpl() {
        addItem(constants.ReportTemplates());
        setUpSelectionHandler();
    }

    private void setUpSelectionHandler() {
        addSelectionHandler(new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> treeItemSelectionEvent) {
                presenter.onReportTemplatesSelected();
            }
        });
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
