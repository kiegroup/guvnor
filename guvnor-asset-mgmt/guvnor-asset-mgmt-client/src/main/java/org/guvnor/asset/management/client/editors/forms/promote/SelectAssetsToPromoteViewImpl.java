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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

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
import javax.enterprise.event.Observes;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class SelectAssetsToPromoteViewImpl extends Composite implements SelectAssetsToPromotePresenter.SelectAssetsToPromoteView {

    interface Binder
            extends UiBinder<Widget, SelectAssetsToPromoteViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);
    @Inject
    private User identity;

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
    public Button promoteAllFilesButton;
    
    @UiField
    public Button promoteSelectedFilesButton;

    @Inject
    private Event<NotificationEvent> notification;
    
    @Inject
    private Event<GetFormParamsEvent> getFormParamsEvent;


    private Map<String, String> commitsPerFile;

    public SelectAssetsToPromoteViewImpl() {

        filesInTheBranchList = new ListBox(true);

        filesToPromoteList = new ListBox(true);

        initWidget(uiBinder.createAndBindUi(this));

    }

    public void getOutputMap(@Observes RequestFormParamsEvent event) {
        
        Map<String, Object> outputMap = new HashMap<String, Object>();

        int filesToPromoteCount = getFilesToPromoteList().getItemCount();
        String out_commits = "";
        for (int i = 0; i < filesToPromoteCount; i++) {
            String commits = commitsPerFile.get(getFilesToPromoteList().getItemText(i));
            if (commits == null || commits.length() == 0 || out_commits.contains(commits)) continue;
            if (out_commits.length() > 0) out_commits += ",";
            out_commits += commits;
        }

        outputMap.put("out_commits", out_commits);
        outputMap.put("out_requires_review", getRequiresReviewCheckBox().getValue());
        getFormParamsEvent.fire(new GetFormParamsEvent(event.getAction(), outputMap));
        
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

    @UiHandler("promoteSelectedFilesButton")
    public void promoteSelectedFilesButton(ClickEvent e) {
        int selectedIndex = filesInTheBranchList.getSelectedIndex();
        String value = filesInTheBranchList.getValue(selectedIndex);
        filesToPromoteList.addItem(value);
        filesInTheBranchList.removeItem(selectedIndex);

    }
    
    @UiHandler("promoteAllFilesButton")
    public void promoteAllFilesButton(ClickEvent e) {
        int itemCount = filesInTheBranchList.getItemCount();
        for(int i = 0; i < itemCount; i ++){
            String value = filesInTheBranchList.getValue(i);
            filesToPromoteList.addItem(value);
        }
        filesInTheBranchList.clear();

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

    public void setInputMap(@Observes SetFormParamsEvent event) {
        Map<String, String> params = event.getParams();
        String files = params.get("in_list_of_files");
        commitsPerFile = new HashMap<String, String>();

        String in_commits_per_file = params.get("in_commits_per_file");

        if (in_commits_per_file != null && in_commits_per_file.length() > 0) {
            String[] commits_info = in_commits_per_file.split(";");

            for (String commit : commits_info) {
                String commits = commit.substring(commit.indexOf("=") + 1);
                commitsPerFile.put(commit.substring(0, commit.indexOf("=")), commits);
            }
        }

        String[] filesArray = files.split(",");

        getSourceBranchBox().setText(params.get("in_source_branch_name"));

        getFilesInTheBranchList().clear();
        for (String file : filesArray) {
            getFilesInTheBranchList().addItem(file);
        }
        setReadOnly(event.isReadOnly());

    }

    private void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        if (isReadOnly) {
            getFilesInTheBranchList().setEnabled(false);
            getFilesToPromoteList().setEnabled(false);
            promoteSelectedFilesButton.setEnabled(false);
            promoteAllFilesButton.setEnabled(false);
            requiresReviewCheckBox.setEnabled(false);
            sourceBranchBox.setEnabled(false);
        } else {
            getFilesInTheBranchList().setEnabled(true);
            getFilesToPromoteList().setEnabled(true);
            promoteSelectedFilesButton.setEnabled(true);
            promoteAllFilesButton.setEnabled(true);
            requiresReviewCheckBox.setEnabled(true);
            sourceBranchBox.setEnabled(true);
        }

    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

}
