/*
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

package org.drools.guvnor.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.guvnor.client.configurations.ConfigurationsLoader;
import org.drools.guvnor.client.examples.SampleRepositoryInstaller;
import org.drools.guvnor.client.resources.*;
import org.drools.guvnor.client.resources.decisiontable.DecisionTableResources;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.drools.guvnor.shared.security.AppRoles;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.security.Identity;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * This is the main launching/entry point for the Guvnor web console. It
 * essentially sets the initial layout.
 * <p/>
 * If you hadn't noticed, this is using GWT from google. Refer to GWT docs if
 * GWT is new to you (it is quite a different way of building web apps).
 */
@org.jboss.errai.ioc.client.api.EntryPoint
public class GuvnorDroolsEntryPoint {

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    Identity identity;

    @PostConstruct
    public void init() {
        loadStyles();

        ConfigurationsLoader.loadPreferences(new Command() {
            public void execute() {
//                loadUserCapabilities("userName");
            }
        });

        menubar.addMenuItem(new DefaultMenuItemCommand("Guvnor", new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {

            }
        }));

        if (identity.hasRole(AppRoles.ADMIN)) {
            SampleRepositoryInstaller.askToInstall();
        }

        hideLoadingPopup();
    }

    private void loadStyles() {
        GuvnorResources.INSTANCE.headerCss().ensureInjected();
        GuvnorResources.INSTANCE.guvnorCss().ensureInjected();
        DroolsGuvnorResources.INSTANCE.titledTextCellCss().ensureInjected();
        GuvnorResources.INSTANCE.guvnorCss().ensureInjected();
        DroolsGuvnorResources.INSTANCE.droolsGuvnorCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
        OperatorsResource.INSTANCE.operatorsCss().ensureInjected();
        WizardCellListResources.INSTANCE.cellListStyle().ensureInjected();
        WizardResources.INSTANCE.style().ensureInjected();
        DecisionTableResources.INSTANCE.style().ensureInjected();
        SimulationResources.INSTANCE.style().ensureInjected();
    }

    /*
    * Fade out the "Loading application" pop-up
    */
    private void hideLoadingPopup() {
        final Element loadingElement = RootPanel.get("loading").getElement();

        Animation animation = new Animation() {

            @Override
            protected void onUpdate(double progress) {
                loadingElement.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                loadingElement.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }

        };

        animation.run(500);

    }

}
