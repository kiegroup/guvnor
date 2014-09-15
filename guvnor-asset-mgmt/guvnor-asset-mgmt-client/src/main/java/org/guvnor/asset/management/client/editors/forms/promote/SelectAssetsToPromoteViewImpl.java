/*
 * Copyright 2012 JBoss Inc
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
package org.guvnor.asset.management.client.editors.forms.promote;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.asset.management.client.i18n.Constants;
import org.kie.uberfire.client.forms.FormDisplayerView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class SelectAssetsToPromoteViewImpl extends Composite implements SelectAssetsToPromotePresenter.SelectAssetsToPromoteView, FormDisplayerView {

    interface Binder
            extends UiBinder<Widget, SelectAssetsToPromoteViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);
    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private SelectAssetsToPromotePresenter presenter;

    private boolean isReadOnly = false;

    @UiField
    public TextBox sourceBranchBox;

    @UiField
    public CheckBox requiresReviewCheckBox;

    @UiField(provided = true)
    public ListBox filesInTheBranchList;

    @UiField(provided = true)
    public ListBox filesToPromoteList;

    @UiField
    public Button promoteFilesButton;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    private Map<String, List<String>> commitsPerFile;

    public SelectAssetsToPromoteViewImpl() {

        filesInTheBranchList = new ListBox(true);

        filesToPromoteList = new ListBox(true);

        initWidget(uiBinder.createAndBindUi(this));

    }

    public Map<String, Object> getOutputMap() {
        Map<String, Object> outputMap = new HashMap<String, Object>();

        int filesToPromoteCount = getFilesToPromoteList().getItemCount();
        String out_commits = "";
        for (int i = 0; i < filesToPromoteCount; i++) {
            List<String> commits = commitsPerFile.get(getFilesToPromoteList().getItemText(i));
            for (String commit : commits) {
                if (!out_commits.contains(commit)) {
                    out_commits += commit + ",";
                }
            }
        }

        outputMap.put("out_commits", out_commits);
        outputMap.put("out_requires_review", getRequiresReviewCheckBox().getValue());

        return outputMap;
    }

    @Override
    public void init(SelectAssetsToPromotePresenter presenter) {
        this.presenter = presenter;

        filesToPromoteList.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int selectedIndex = filesToPromoteList.getSelectedIndex();
                String value = filesToPromoteList.getValue(selectedIndex);
                filesInTheBranchList.addItem(value);
                filesToPromoteList.removeItem(selectedIndex);
            }
        });

    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public TextBox getSourceBranchBox() {
        return sourceBranchBox;
    }

    @UiHandler("promoteFilesButton")
    public void promoteFilesButton(ClickEvent e) {
        int selectedIndex = filesInTheBranchList.getSelectedIndex();
        String value = filesInTheBranchList.getValue(selectedIndex);
        filesToPromoteList.addItem(value);
        filesInTheBranchList.removeItem(selectedIndex);

    }

    @Override
    public ListBox getFilesInTheBranchList() {
        return filesInTheBranchList;
    }

    @Override
    public ListBox getFilesToPromoteList() {
        return filesToPromoteList;
    }

    @Override
    public CheckBox getRequiresReviewCheckBox() {
        return requiresReviewCheckBox;
    }

    @Override
    public void setInputMap(Map<String, Object> params) {

        String files = (String) params.get("in_list_of_files");
        commitsPerFile = (Map<String, List<String>>) params.get("in_commits_per_file");

        String[] filesArray = files.split(",");

        getSourceBranchBox().setText((String) params.get("in_source_branch_name"));

        getFilesInTheBranchList().clear();
        for (String file : filesArray) {
            getFilesInTheBranchList().addItem(file);
        }

//        this.taskId = (String)params.get("taskId");
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        if (isReadOnly) {
            getFilesInTheBranchList().setEnabled(false);
            getFilesToPromoteList().setEnabled(false);
            promoteFilesButton.setEnabled(false);
            requiresReviewCheckBox.setEnabled(false);
            sourceBranchBox.setEnabled(false);
        } else {
            getFilesInTheBranchList().setEnabled(true);
            getFilesToPromoteList().setEnabled(true);
            promoteFilesButton.setEnabled(true);
            requiresReviewCheckBox.setEnabled(true);
            sourceBranchBox.setEnabled(true);
        }

    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

}
