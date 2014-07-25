/*
 * Copyright 2014 JBoss Inc
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

import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.client.screens.Empty;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchScreen(identifier = "settingsScreen")
public class SettingsScreenPresenter {

    private TabPanel multiPage = new TabPanel();

    @Inject
    private GeneralTab generalTab;

    @OnStartup
    public void onStartUp() {
        multiPage.add(generalTab);
        addPage("Social");
        addPage("Registration");
        addPage("Roles");
        addPage("Credentials");
        addPage("Token");
        addPage("Keys");
        addPage("SMTP");

        multiPage.selectTab(0);
        generalTab.load();
    }

    private void addPage(final String text) {
        Tab tab = new Tab();
        tab.setHeading(text);
        tab.add(new Empty(text));
        multiPage.add(tab);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings";
    }
}
