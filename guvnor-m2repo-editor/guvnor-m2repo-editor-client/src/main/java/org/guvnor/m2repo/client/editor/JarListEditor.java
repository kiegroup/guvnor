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

package org.guvnor.m2repo.client.editor;


import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.guvnor.m2repo.client.resources.i18n.Constants;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

/**
 * Widget with a table of jar list in Guvnor M2_REPO
 */
public class JarListEditor
        extends Composite {

    // UI
    interface JarListEditorBinder
            extends
            UiBinder<Widget, JarListEditor> {
    }

    @UiField()
    protected Button deleteSelectedJarButton;

    @UiField()
    protected Button refreshButton;

    @UiField()
    protected Button auditButton;

    @UiField(provided = true)
    public PagedJarTable pagedJarTable;

    private static JarListEditorBinder uiBinder = GWT.create( JarListEditorBinder.class );

    private Caller<M2RepoService> m2RepoService;

    public JarListEditor( Caller<M2RepoService> repoService ) {
        this( repoService, null );

    }

    public JarListEditor( Caller<M2RepoService> repoService,
                          final String searchFilter ) {
        this.m2RepoService = repoService;
        pagedJarTable = new PagedJarTable( repoService, searchFilter );

        Column<JarListPageRow, String> downloadColumn = new Column<JarListPageRow, String>( new ButtonCell() ) {
            public String getValue( JarListPageRow row ) {
                return "Download";
            }
        };

        downloadColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
            public void update( int index,
                                JarListPageRow row,
                                String value ) {
                Window.open( getFileDownloadURL( row.getPath() ),
                             "downloading",
                             "resizable=no,scrollbars=yes,status=no" );
            }
        } );

        pagedJarTable.addColumn( downloadColumn, new TextHeader( "Download" ) );

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler("deleteSelectedJarButton")
    void deleteSelectedJar( ClickEvent e ) {
        if ( pagedJarTable.getSelectedJars() == null ) {
            Window.alert( "Please Select A Jar To Delete" );
            return;
        }
        if ( !Window.confirm( Constants.INSTANCE.AreYouSureYouWantToDeleteTheseItems() ) ) {
            return;
        }
        m2RepoService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                Window.alert( "Deleted successfully" );
                pagedJarTable.refresh();
            }
        } ).deleteJar( pagedJarTable.getSelectedJars() );
    }

    @UiHandler("refreshButton")
    void refresh( ClickEvent e ) {
        pagedJarTable.refresh();
    }

    @UiHandler("auditButton")
    void viewAuditLog( ClickEvent e ) {
    }

    String getFileDownloadURL( String path ) {
        String url = getGuvnorM2RepoBaseURL() + path;
        return url;
    }

    public static String getGuvnorM2RepoBaseURL() {
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );
        return baseUrl + "maven2/";
    }

}
