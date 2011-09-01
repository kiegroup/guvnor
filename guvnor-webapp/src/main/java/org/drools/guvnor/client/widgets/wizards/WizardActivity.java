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

import org.drools.guvnor.client.explorer.AcceptTabItem;
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

    private WizardActivityView view;
    private Wizard             wizard;

    public WizardActivity(WizardPlace< ? > place,
                          ClientFactory clientFactory,
                          EventBus eventBus) {
        
        //The generic view
        view = clientFactory.getNavigationViewFactory().getWizardView();
        
        //The specific "page factory" for a particular Wizard
        wizard = clientFactory.getWizardFactory().getWizard( place.getContext() );
        view.setPresenter( this );
        
        //Wire-up the events
        eventBus.addHandler( WizardPageStatusChangeEvent.TYPE,
                             this );
        eventBus.addHandler( WizardPageSelectedEvent.TYPE,
                             this );
    }

    public void onStatusChange(WizardPageStatusChangeEvent event) {
        WizardPage page = event.getSource();
        int index = wizard.getPages().indexOf( page );
        view.setPageCompletionState( index,
                                     page.isComplete() );
        view.setCompletionStatus( wizard.isComplete() );
    }

    public void onPageSelected(WizardPageSelectedEvent event) {
        WizardPage page = event.getSource();
        int index = wizard.getPages().indexOf( page );
        view.selectPage( index );
    }

    @Override
    public void start(AcceptTabItem acceptTabItem,
                      EventBus eventBus) {
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

}
