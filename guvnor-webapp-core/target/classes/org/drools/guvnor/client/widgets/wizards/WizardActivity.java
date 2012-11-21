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

import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.util.Activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;

/**
 * The generic "Wizard" container, providing a left-hand side list of Page
 * titles, buttons to navigate the Wizard pages and a mechanism to display
 * different pages of the Wizard.
 */
public class WizardActivity extends Activity
        implements
        WizardActivityView.Presenter,
        WizardPageStatusChangeEvent.Handler,
        WizardPageSelectedEvent.Handler {

    private final WizardActivityView view;
    private final Wizard             wizard;
    private final WizardContext      context;

    public WizardActivity(WizardPlace< ? > place,
                          ClientFactory clientFactory) {

        //The context of this Wizard instance
        this.context = place.getContext();

        //The generic view
        view = clientFactory.getNavigationViewFactory().getWizardView( context );

        //The specific "page factory" for a particular Wizard
        wizard = clientFactory.getWizardFactory().getWizard( place.getContext(),
                                                             this );
        view.setPresenter( this );
    }

    public void onStatusChange(WizardPageStatusChangeEvent event) {

        //The event might not have been raised by a page belonging to this Wizard instance
        if ( event.getSource() != context ) {
            return;
        }

        //Update the status of each belonging to this Wizard
        for ( WizardPage wp : wizard.getPages() ) {
            int index = wizard.getPages().indexOf( wp );
            view.setPageCompletionState( index,
                                         wp.isComplete() );
        }

        //Update the status of this Wizard
        view.setCompletionStatus( wizard.isComplete() );
    }

    public void onPageSelected(WizardPageSelectedEvent event) {
        if ( event.getSource() != context ) {
            return;
        }
        WizardPage page = event.getSelectedPage();
        int index = wizard.getPages().indexOf( page );
        view.selectPage( index );
    }

    @Override
    public void start(AcceptItem acceptTabItem,
                      EventBus eventBus) {

        //Wire-up the events
        eventBus.addHandler( WizardPageStatusChangeEvent.TYPE,
                             this );
        eventBus.addHandler( WizardPageSelectedEvent.TYPE,
                             this );

        //Go, Go gadget Wizard
        view.setTitle( wizard.getTitle() );
        view.setPreferredHeight( wizard.getPreferredHeight() );
        view.setPreferredWidth( wizard.getPreferredWidth() );
        view.setPageTitles( wizard.getPages() );
        view.show();
        view.selectPage( 0 );
    }

    public void pageSelected(int pageNumber) {
        Widget w = wizard.getPageWidget( pageNumber );
        view.setBodyWidget( w );
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
        view.showCheckinError( message );
    }

    public void hide() {
        view.hide();
    }

}
