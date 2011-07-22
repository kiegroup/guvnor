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

package org.drools.guvnor.client.explorer.navigation.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.client.rpc.VerificationServiceAsync;

/**
 * Viewer for, well, analysis !
 */
public class VerifierScreen extends Composite {

    private Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private final VerticalPanel layout = new VerticalPanel();
    private final String packageUUID;

    public VerifierScreen(String packageUUID,
                          String packageName) {
        this.packageUUID = packageUUID;

        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel vert = new VerticalPanel();
        String m = constants.AnalysingPackage( packageName );
        vert.add( new HTML( m ) );
        Button run = new Button( constants.RunAnalysis() );
        run.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent event) {
                runAnalysis();
            }
        } );
        vert.add( run );

        pf.addHeader( images.analyzeLarge(),
                vert );
        layout.add( pf );

        layout.add( new Label() );

        layout.setWidth( "100%" );

        initWidget( layout );
    }

    private void runAnalysis() {
        LoadingPopup.showMessage( constants.AnalysingPackageRunning() );
        VerificationServiceAsync verificationService = GWT.create( VerificationService.class );

        verificationService.analysePackage( packageUUID,
                new GenericCallback<AnalysisReport>() {
                    public void onSuccess(AnalysisReport rep) {
                        VerifierResultWidget w = new VerifierResultWidget( rep,
                                true );
                        w.setWidth( "100%" );
                        layout.remove( 1 );
                        layout.add( w );
                        LoadingPopup.close();
                    }
                } );

    }

}
