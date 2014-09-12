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
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.guvnor.asset.management.client.i18n.Constants;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.kie.uberfire.client.forms.FormDisplayerView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "SelectAssetsToPromoteViewImpl.html")
public class SelectAssetsToPromoteViewImpl extends Composite implements SelectAssetsToPromotePresenter.SelectAssetsToPromoteView, FormDisplayerView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private SelectAssetsToPromotePresenter presenter;

    @Inject
    @DataField
    public TextBox taskIdBox;

    private boolean isReadOnly = false;

    @Inject
    @DataField
    public Label sourceBranchLabel;

    @Inject
    @DataField
    public TextBox sourceBranchBox;

    @Inject
    @DataField
    public Label requiresReviewLabel;

    @Inject
    @DataField
    public CheckBox requiresReviewCheckBox;


    
    @DataField
    public ListBox filesInTheBranchList;
    
    @DataField
    public ListBox filesToPromoteList;
   
    
    @Inject
    @DataField
    public Button promoteFilesButton;
    
    @Inject
    @DataField
    public Label filesToPromoteLabel;
    
  
    @Inject
    @DataField
    public Label filesInTheBranchLabel;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);
    
    private Map<String, List<String>> commitsPerFile;

    public SelectAssetsToPromoteViewImpl() {

        filesInTheBranchList = new ListBox(true);
        
        filesToPromoteList = new ListBox(true);
        
    }
    
     public Map<String, Object> getOutputMap(){
        Map<String, Object> outputMap = new HashMap<String, Object>();
        
        int filesToPromoteCount = getFilesToPromoteList().getItemCount();
        String out_commits = "";
        for(int i = 0; i < filesToPromoteCount; i ++){
            List<String> commits = commitsPerFile.get(getFilesToPromoteList().getItemText(i));
            for(String commit : commits){
                if(!out_commits.contains(commit)){
                    out_commits += commit+",";
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
       
        this.requiresReviewLabel.setText(constants.Requires_Review());
       
        this.sourceBranchLabel.setText(constants.Source_Branch());
       
        this.promoteFilesButton.setText(constants.Promote_Assets());
        this.filesInTheBranchLabel.setText(constants.Files_In_The_Branch());
        this.filesToPromoteLabel.setText(constants.Files_To_Promote());
       
        
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

    public TextBox getTaskIdBox() {
        return taskIdBox;
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    public TextBox getSourceBranchBox() {
        return sourceBranchBox;
    }

    

    
    
    @EventHandler("promoteFilesButton")
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

        String files = (String)params.get("in_list_of_files");
        commitsPerFile = (Map<String, List<String>>)params.get("in_commits_per_file");
        
        
        String[] filesArray = files.split(",");
        
        getSourceBranchBox().setText((String)params.get("in_source_branch_name"));
        
        getFilesInTheBranchList().clear();
        for(String file : filesArray){
            getFilesInTheBranchList().addItem(file);
        }

        getTaskIdBox().setText((String)params.get("taskId"));
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        if(isReadOnly){
            getFilesInTheBranchList().setEnabled(false);
            getFilesToPromoteList().setEnabled(false);
            promoteFilesButton.setEnabled(false);
            requiresReviewCheckBox.setEnabled(false);
            sourceBranchBox.setEnabled(false);
        }else{
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
