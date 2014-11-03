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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import org.guvnor.asset.management.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "SelectAssetsToPromote Form")
public class SelectAssetsToPromotePresenter  {

    private Constants constants = GWT.create(Constants.class);

    public interface SelectAssetsToPromoteView extends UberView<SelectAssetsToPromotePresenter> {

        void displayNotification(String text);

        TextBox getSourceBranchBox();
        
        ListBox getFilesInTheBranchList();

        ListBox getFilesToPromoteList();
        
        CheckBox getRequiresReviewCheckBox();
    }

    @Inject
    SelectAssetsToPromoteView view;

    private PlaceRequest place;

    @Inject
    private PlaceManager placeManager;

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }
    
   

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Promote_Assets();
    }

    @WorkbenchPartView
    public UberView<SelectAssetsToPromotePresenter> getView() {
        return view;
    }

    public SelectAssetsToPromotePresenter() {
    }

    @PostConstruct
    public void init() {
    }

    @OnOpen
    public void onOpen() {
        

    }

}
