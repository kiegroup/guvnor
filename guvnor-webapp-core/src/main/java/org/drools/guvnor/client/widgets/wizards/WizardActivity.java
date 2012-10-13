/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.widgets.wizards;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.GuvnorEventBus;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.uberfire.client.annotations.*;
import org.uberfire.shared.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * The generic "Wizard" container, providing a left-hand side list of Page
 * titles, buttons to navigate the Wizard pages and a mechanism to display
 * different pages of the Wizard.
 */
@Dependent
@WorkbenchPopup(identifier = "wizardPopup")
public class WizardActivity
        implements
        WizardActivityView.Presenter,
        WizardPageStatusChangeEvent.Handler,
        WizardPageSelectedEvent.Handler {

    private WizardActivityView view;
    private Wizard wizard;
    private WizardContext context;
    private final GuvnorEventBus eventBus;
    private final ClientFactory clientFactory;

    @Inject
    public WizardActivity(ClientFactory clientFactory,
                          GuvnorEventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }


    @OnStart
    public void init(final PlaceRequest place) {
        //The context of this Wizard instance
        this.context = clientFactory.makeContext(place.getParameters());

        //The generic view
        view = clientFactory.getNavigationViewFactory().getWizardView(context);

        //The specific "page factory" for a particular Wizard
        wizard = clientFactory.getWizardFactory().getWizard(context,
                this);
        view.setPresenter(this);

    }

    public void onStatusChange(WizardPageStatusChangeEvent event) {

        //The event might not have been raised by a page belonging to this Wizard instance
        if (event.getSource() != context) {
            return;
        }

        //Update the status of each belonging to this Wizard
        for (WizardPage wp : wizard.getPages()) {
            int index = wizard.getPages().indexOf(wp);
            view.setPageCompletionState(index,
                    wp.isComplete());
        }

        //Update the status of this Wizard
        view.setCompletionStatus(wizard.isComplete());
    }

    public void onPageSelected(WizardPageSelectedEvent event) {
        if (event.getSource() != context) {
            return;
        }
        WizardPage page = event.getSelectedPage();
        int index = wizard.getPages().indexOf(page);
        view.selectPage(index);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return wizard.getTitle();
    }

    @WorkbenchPartView
    public PopupPanel asWidget() {

        //Wire-up the events
        eventBus.addHandler(WizardPageStatusChangeEvent.TYPE,
                this);
        eventBus.addHandler(WizardPageSelectedEvent.TYPE,
                this);

        //Go, Go gadget Wizard
        view.setTitle(wizard.getTitle());
        view.setPreferredHeight(wizard.getPreferredHeight());
        view.setPreferredWidth(wizard.getPreferredWidth());
        view.setPageTitles(wizard.getPages());

        return (PopupPanel) view;
    }

    @OnReveal
    public void onReveal() {
        view.selectPage(0);
    }

    public void pageSelected(int pageNumber) {
        Widget w = wizard.getPageWidget(pageNumber);
        view.setBodyWidget(w);
    }

    public void complete() {
        wizard.complete();
    }

    public void showSavingIndicator() {
        view.showSavingIndicator();
    }

    public void hideSavingIndicator() {
        view.hideSavingIndicator();
    }

    public void showDuplicateAssetNameError() {
        view.showDuplicateAssetNameError();
    }

    public void showUnspecifiedCheckinError() {
        view.showUnspecifiedCheckinError();
    }

    public void showCheckinError(String message) {
        view.showCheckinError(message);
    }

    public void hide() {
        view.hide();
    }

}
