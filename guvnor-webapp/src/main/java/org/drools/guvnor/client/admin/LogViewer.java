/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.admin;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.widgets.tables.LogPagedTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LogViewer extends Composite {

    private static Images images    = (Images) GWT.create( Images.class );
    private Constants     constants = ((Constants) GWT.create( Constants.class )); ;

    private VerticalPanel layout;
    private LogPagedTable table;

    public LogViewer() {

        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel header = new VerticalPanel();
        Label caption = new Label( constants.ShowRecentLogTip() );
        caption.getElement().getStyle().setFontWeight( FontWeight.BOLD );
        header.add( caption );

        pf.addHeader( images.eventLogLarge(),
                      header );

        layout = new VerticalPanel();
        layout.setHeight( "100%" );
        layout.setWidth( "100%" );

        pf.startSection();
        pf.addRow( layout );
        pf.endSection();

        setupWidget();
        initWidget( pf );
    }

    private void setupWidget() {

        final Command cleanCommand = new Command() {

            @Override
            public void execute() {
                cleanLog();
            }

        };

        this.table = new LogPagedTable( cleanCommand );
        layout.add( table );

    }

    private void cleanLog() {
        LoadingPopup.showMessage( constants.CleaningLogMessages() );
        RepositoryServiceFactory.getService().cleanLog( new GenericCallback<java.lang.Void>() {
            public void onSuccess(Void v) {
                table.refresh();
                LoadingPopup.close();
            }
        } );
    }

}
