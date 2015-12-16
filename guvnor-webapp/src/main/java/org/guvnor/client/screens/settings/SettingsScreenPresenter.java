/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.client.screens.settings;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.client.screens.Empty;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchScreen(identifier = "settingsScreen")
public class SettingsScreenPresenter {

    private TabPanel multiPage = new TabPanel();
    private NavTabs navTabs = new NavTabs();
    private TabContent multiPageContent = new TabContent();

    @Inject
    private GeneralTab generalTab;

    public SettingsScreenPresenter() {
        multiPage.add( navTabs );
        multiPage.add( multiPageContent );
    }

    @OnStartup
    public void onStartUp() {
        multiPageContent.add( generalTab );
        navTabs.add( new TabListItem( generalTab.getHeading() ) {{
            setDataTargetWidget( generalTab );
            setActive( true );
        }} );
        addPage( "Social" );
        addPage( "Registration" );
        addPage( "Roles" );
        addPage( "Credentials" );
        addPage( "Token" );
        addPage( "Keys" );
        addPage( "SMTP" );

        generalTab.load();
    }

    private void addPage( final String text ) {
        final TabPane tab = new TabPane();
        tab.add( new Empty( text ) );
        multiPageContent.add( tab );

        navTabs.add( new TabListItem( text ) {{
            setDataTargetWidget( tab );
        }} );
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPageContent;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings";
    }
}
