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

package org.drools.guvnor.client.examples;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * To be shown when the user opens repo for the first time.
 */
public class NewRepositoryDialog extends FormStylePopup {
    private Constants constants;

    public NewRepositoryDialog() {
        setTitle( ((Constants) GWT.create( Constants.class )).WelcomeToGuvnor() );
        setWidth( 300 + "px" );

        constants = ((Constants) GWT.create( Constants.class ));
        addAttribute( "",
                      new HTML( "<div class='highlight'>" + constants.BrandNewRepositoryNote() + "</div>" ) ); //NON-NLS

        HorizontalPanel hp = new HorizontalPanel();

        Button ins = new Button( constants.YesPleaseInstallSamples() );
        hp.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
        hp.add( ins );
        Button no = new Button( constants.NoThanks() );
        hp.add( no );

        addAttribute( "",
                      hp );
        ins.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                if ( !Window.confirm( constants.AboutToInstallSampleRepositoryAreYouSure() ) ) return;
                LoadingPopup.showMessage( constants.ImportingAndProcessing() );
                RepositoryServiceFactory.getPackageService().installSampleRepository( new GenericCallback<java.lang.Void>() {
                    public void onSuccess(Void v) {
                        Window.alert( constants.RepositoryInstalledSuccessfully() );
                        hide();
                        Window.Location.reload();
                    }
                } );
            }
        } );
        no.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        } );

    }

}
