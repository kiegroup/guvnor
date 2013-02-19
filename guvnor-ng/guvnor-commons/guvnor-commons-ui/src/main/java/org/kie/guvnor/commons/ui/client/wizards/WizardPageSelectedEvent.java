/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.kie.guvnor.commons.ui.client.wizards;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event representing the selection of a page within a Wizard
 */
public class WizardPageSelectedEvent extends GwtEvent<WizardPageSelectedEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onPageSelected( final WizardPageSelectedEvent event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private final WizardPage selectedPage;

    public WizardPageSelectedEvent( final WizardPage selectedPage ) {
        this.selectedPage = selectedPage;
    }

    public WizardPage getSelectedPage() {
        return selectedPage;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final WizardPageSelectedEvent.Handler handler ) {
        handler.onPageSelected( this );
    }

}
