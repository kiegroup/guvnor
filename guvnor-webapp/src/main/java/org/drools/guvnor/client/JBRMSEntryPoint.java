/**
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

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.ConfigurationsLoader;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.examples.SampleRepositoryInstaller;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ClientFactoryImpl;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.OperatorsResource;
import org.drools.guvnor.client.resources.RoundedCornersResource;
import org.drools.guvnor.client.resources.WizardCellListResources;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.UserSecurityContext;
import org.drools.guvnor.client.ruleeditor.StandaloneEditorManager;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the main launching/entry point for the JBRMS web console. It
 * essentially sets the initial layout.
 * <p/>
 * If you hadn't noticed, this is using GWT from google. Refer to GWT docs if
 * GWT is new to you (it is quite a different way of building web apps).
 */
public class JBRMSEntryPoint
        implements
        EntryPoint {

    private Constants constants = GWT.create(Constants.class);
    private AppController appController;

    public void onModuleLoad() {
        loadStyles();
        hideLoadingPopup();
        checkLogIn();
    }

    private void loadStyles() {
        GuvnorResources.INSTANCE.headerCss().ensureInjected();
        GuvnorResources.INSTANCE.titledTextCellCss().ensureInjected();
        GuvnorResources.INSTANCE.guvnorCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
        OperatorsResource.INSTANCE.operatorsCss().ensureInjected();
        WizardCellListResources.INSTANCE.cellListStyle().ensureInjected();
        WizardResources.INSTANCE.style().ensureInjected();
    }

    /**
     * Check if user is logged in, if not, then show prompt. If it is, then we
     * show the app, in all its glory !
     */
    private void checkLogIn() {
        RepositoryServiceFactory.getSecurityService().getCurrentUser(new GenericCallback<UserSecurityContext>() {
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

        Window.setStatus(constants.LoadingUserPermissions());

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

        appController.setUserName(userName);
    }

    /**
     * Creates the main view of Guvnor. The path used to invoke guvnor is used
     * to identify the view to show: If the path contains
     * "StandaloneEditor.html" then the StandaloneGuidedEditorManager is used to
     * render the view. If not, the default view is shown.
     */
    private void createMain() {
        EventBus eventBus = new SimpleEventBus();
        ClientFactory clientFactory = new ClientFactoryImpl(eventBus);
        appController = new AppController(clientFactory,eventBus);

        if (Window.Location.getPath().contains("StandaloneEditor.html")) {
            RootLayoutPanel.get().add(new StandaloneEditorManager(clientFactory, eventBus).getBaseLayout());
        } else {

            RootLayoutPanel.get().add(appController.getMainPanel());
        }

        askToInstallSampleRepository();
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
