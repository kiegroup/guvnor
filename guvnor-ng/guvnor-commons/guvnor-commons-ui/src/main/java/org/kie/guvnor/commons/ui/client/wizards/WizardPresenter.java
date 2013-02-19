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

package org.kie.guvnor.commons.ui.client.wizards;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;

/**
 * The generic "Wizard" container, providing a left-hand side list of Page
 * titles, buttons to navigate the Wizard pages and a mechanism to display
 * different pages of the Wizard.
 */
public class WizardPresenter implements
                             WizardView.Presenter,
                             WizardPageStatusChangeEvent.Handler,
                             WizardPageSelectedEvent.Handler {

    private final Wizard wizard;
    private final WizardView view;
    private final WizardContext context;

    //TODO clientFactory.getWizardFactory().getWizard( place.getContext(), this );
    //TODO clientFactory.getNavigationViewFactory().getWizardView( context );
    public WizardPresenter( final Wizard wizard,
                            final WizardView view,
                            final WizardContext context ) {

        //The specific "page factory" for a particular Wizard
        this.wizard = wizard;

        //The generic view
        this.view = view;

        //The context of this Wizard instance
        this.context = context;

        view.setPresenter( this );
    }

    public void onStatusChange( final WizardPageStatusChangeEvent event ) {

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

    public void onPageSelected( final WizardPageSelectedEvent event ) {
        if ( event.getSource() != context ) {
            return;
        }
        WizardPage page = event.getSelectedPage();
        int index = wizard.getPages().indexOf( page );
        view.selectPage( index );
    }

    public void start( final EventBus eventBus ) {

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

    public void pageSelected( final int pageNumber ) {
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

    public void showCheckinError( String message ) {
        view.showCheckinError( message );
    }

    public void hide() {
        view.hide();
    }

}
