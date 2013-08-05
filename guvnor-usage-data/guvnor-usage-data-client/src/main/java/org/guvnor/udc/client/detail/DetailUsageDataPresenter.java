/*
 * Copyright 2013 JBoss Inc
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

package org.guvnor.udc.client.detail;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.udc.client.i8n.Constants;
import org.guvnor.udc.client.util.UtilUsageData;
import org.guvnor.udc.model.UsageEventSummary;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;

@Dependent
@WorkbenchPopup(identifier = "Detail Usage Data")
public class DetailUsageDataPresenter {

    private Constants constants = GWT.create(Constants.class);

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Title_Detail();
    }

    @WorkbenchPartView
    public UberView<DetailUsageDataPresenter> getView() {
        return view;
    }

    @Inject
    DetailUsageDataEventView view;

    @Inject
    Identity identity;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;
    
    @OnStartup
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }
    
    @OnOpen
    public void onReveal() {
        refreshDetailUsageData(UtilUsageData.getUsageDataParam(place));
    }

    public interface DetailUsageDataEventView extends UberView<DetailUsageDataPresenter> {
        void displayNotification(String text);

        TextBox getUsageModuleText();

        TextBox getUsageUserText();

        TextBox getUsageComponentText();

        TextBox getUsageActionText();

        TextBox getUsageKeyText();

        TextBox getUsageLevelText();

        TextBox getUsageStatusText();

        TextArea getTextAreaDescription();
        
        TextBox getFileSystemText();
        
        TextBox getFileNameText();
        
        TextBox getItemPathText();
        
    }

    private void refreshDetailUsageData(UsageEventSummary usageData) {
        view.getUsageModuleText().setText(usageData.getModule());
        view.getUsageUserText().setText(usageData.getFrom());
        view.getUsageComponentText().setText(usageData.getComponent());
        view.getUsageActionText().setText(usageData.getAction());
        view.getUsageKeyText().setText(usageData.getKey());
        view.getUsageLevelText().setText(usageData.getLevel());
        view.getUsageStatusText().setText(usageData.getStatus());
        view.getTextAreaDescription().setText(usageData.getDescription());
        view.getFileSystemText().setText(usageData.getFileSystem());
        view.getFileNameText().setText(usageData.getFileName());
        view.getItemPathText().setText(usageData.getItemPath());
    }

    public void close() {
        closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
    }

}
