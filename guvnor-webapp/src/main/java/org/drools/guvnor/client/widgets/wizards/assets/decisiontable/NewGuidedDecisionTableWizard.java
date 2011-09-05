/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.widgets.wizards.assets.decisiontable;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.widgets.wizards.WizardPageStatusChangeEvent;
import org.drools.guvnor.client.widgets.wizards.WizardPage;
import org.drools.guvnor.client.widgets.wizards.assets.AbstractNewAssetWizard;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Work in progress.
 */
public class NewGuidedDecisionTableWizard
        extends AbstractNewAssetWizard {

    private List<WizardPage> pages = new ArrayList<WizardPage>();

    public NewGuidedDecisionTableWizard(ClientFactory clientFactory,
                                        EventBus eventBus) {
        super( clientFactory,
               eventBus );
        pages.add( new NewGuidedDecisionTableWizardPage( "Page 1 - Some things",
                                                         1 ) );
        pages.add( new NewGuidedDecisionTableWizardPage( "Page 2 - Other things",
                                                         2 ) );
        pages.add( new NewGuidedDecisionTableWizardPage( "Page 3 - More",
                                                         3 ) );
        pages.add( new NewGuidedDecisionTableWizardPage( "Page 4 - Even more",
                                                         4 ) );
        pages.add( new NewGuidedDecisionTableWizardPage( "Page 5 - Last",
                                                         5 ) );
    }

    public String getTitle() {
        return "An example wizard serving no purpose";
    }

    public List<WizardPage> getPages() {
        return this.pages;
    }

    public Widget getPageWidget(int pageNumber) {
        return this.pages.get( pageNumber ).getContent();
    }

    public int getPreferredHeight() {
        return 500;
    }

    public int getPreferredWidth() {
        return 800;
    }

    public boolean isComplete() {
        for ( WizardPage page : this.pages ) {
            if ( !page.isComplete() ) {
                return false;
            }
        }
        return true;
    }

    private class NewGuidedDecisionTableWizardPage
        implements
        WizardPage {

        private String  title;
        private int     pageNumber;
        private boolean isComplete;
        private Widget  content;

        NewGuidedDecisionTableWizardPage(String title,
                                         int pageNumber) {
            this.title = title;
            this.pageNumber = pageNumber;
        }

        public String getTitle() {
            return this.title;
        }

        public Widget getContent() {
            if ( content == null ) {
                SimplePanel p = new SimplePanel();
                VerticalPanel vp = new VerticalPanel();
                vp.add( new Label( title ) );
                vp.add( new Label( "Page = " + pageNumber ) );
                CheckBox chkIsComplete = new CheckBox( "Is page complete?" );
                chkIsComplete.addValueChangeHandler( new ValueChangeHandler<Boolean>() {

                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        isComplete = event.getValue();
                        WizardPageStatusChangeEvent statusChangeEvent = new WizardPageStatusChangeEvent( NewGuidedDecisionTableWizardPage.this );
                        eventBus.fireEvent( statusChangeEvent );
                    }

                } );
                vp.add( chkIsComplete );
                p.getElement().getStyle().setBackgroundColor( "#f0f0f0" );
                p.setWidget( vp );
                content = p;
            }
            return content;
        }

        public boolean isComplete() {
            return this.isComplete;
        }

        @Override
        public int hashCode() {
            int hash = 0;
            hash = hash + 31 * title.hashCode();
            hash = hash + 31 * pageNumber;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) {
                return true;
            }
            if ( !(obj instanceof NewGuidedDecisionTableWizardPage) ) {
                return false;
            }
            NewGuidedDecisionTableWizardPage that = (NewGuidedDecisionTableWizardPage) obj;
            if ( title != null ? !title.equals( that.title ) : that.title != null ) return false;
            if ( pageNumber != that.pageNumber ) return false;
            if ( isComplete != that.isComplete ) return false;
            return true;
        }

    }

}
