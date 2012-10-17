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
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.ConfigurationsLoader;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.examples.SampleRepositoryInstaller;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.*;
import org.drools.guvnor.client.resources.decisiontable.DecisionTableResources;
import org.drools.guvnor.client.rpc.SecurityServiceAsync;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.simulation.resources.SimulationResources;

/**
 * This is the main launching/entry point for the Guvnor web console. It
 * essentially sets the initial layout.
 * <p/>
 * If you hadn't noticed, this is using GWT from google. Refer to GWT docs if
 * GWT is new to you (it is quite a different way of building web apps).
 */
public class GuvnorDroolsEntryPoint
        implements
        EntryPoint {

    public void onModuleLoad() {
        loadStyles();
        hideLoadingPopup();
        checkLogIn();
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

    /**
     * Check if user is logged in, if not, then show prompt. If it is, then we
     * show the app, in all its glory !
     */
    private void checkLogIn() {
        SecurityServiceAsync.INSTANCE.getCurrentUser(new GenericCallback<UserSecurityContext>() {
            public void onSuccess(UserSecurityContext userSecurityContext) {
                String userName = userSecurityContext.getUserName();
                if (userName != null) {
                    showMain(userName);
                } else {
                    logIn();
                }
            }
        });
    }

    private void logIn() {
        final LoginWidget loginWidget = new LoginWidget();
        loginWidget.setLoggedInEvent(new Command() {
            public void execute() {
                showMain(loginWidget.getUserName());
            }
        });
        loginWidget.show();
    }

    private void showMain(final String userName) {

        Window.setStatus(Constants.INSTANCE.LoadingUserPermissions());

        loadConfigurations(userName);
    }

    private void loadConfigurations(final String userName) {
        ConfigurationsLoader.loadPreferences(new Command() {
            public void execute() {
                loadUserCapabilities(userName);
            }
        });
    }

    private void loadUserCapabilities(final String userName) {
        ConfigurationsLoader.loadUserCapabilities(new Command() {
            public void execute() {
                setUpMain(userName);
            }
        });
    }

    private void setUpMain(String userName) {
        Window.setStatus(" ");

        createMain();

    }

    /**
     * Creates the main view of Guvnor. The path used to invoke guvnor is used
     * to identify the view to show: If the path contains
     * "StandaloneEditor.html" then the StandaloneGuidedEditorManager is used to
     * render the view. If not, the default view is shown.
     */
    private void createMain() {
        EventBus eventBus = new SimpleEventBus();
//        ClientFactory clientFactory = new ClientFactoryImpl(eventBus);
//        appController = new AppControllerImpl(clientFactory,eventBus);

        if (Window.Location.getPath().contains("StandaloneEditor.html")) {
//            RootLayoutPanel.get().add(new StandaloneEditorManager(clientFactory, eventBus).getBaseLayout());
        } else {

//            RootLayoutPanel.get().add(appController.getMainPanel());
        }

        askToInstallSampleRepository();

        //Have to start the FindPlace as the last thing during the initialization, otherwise we got https://bugzilla.redhat.com/show_bug.cgi?id=790025
//        clientFactory.getPlaceController().goTo(new FindPlace());
    }

    private void askToInstallSampleRepository() {
        if (UserCapabilities.INSTANCE.hasCapability(Capability.SHOW_ADMIN)) {
            SampleRepositoryInstaller.askToInstall();
        }
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        Animation r = new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Visibility.HIDDEN);
            }

        };

        r.run(500);

    }

}
