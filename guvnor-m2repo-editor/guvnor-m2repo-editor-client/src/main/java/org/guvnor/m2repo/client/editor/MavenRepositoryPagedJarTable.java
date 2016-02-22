/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.m2repo.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.guvnor.m2repo.client.widgets.ArtifactListPresenter;
import org.guvnor.m2repo.client.widgets.ColumnType;
import org.guvnor.m2repo.model.JarListPageRow;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;

@Dependent
public class MavenRepositoryPagedJarTable
        extends Composite
        implements RequiresResize {

    private ArtifactListPresenter presenter;

    @Inject
    protected User identity;

    private final Div content = new Div();

    public MavenRepositoryPagedJarTable() {
        initWidget( content );
    }

    @Override
    public void onResize() {
        if ( ( getParent().getOffsetHeight() - 148 ) > 0 && presenter != null ) {
            presenter().getView().setContentHeight( getParent().getOffsetHeight() - 148 + "px" );
        }
    }

    private String getFileDownloadURL( final String path ) {
        return getGuvnorM2RepoBaseURL() + path;
    }

    private String getGuvnorM2RepoBaseURL() {
        final String baseUrl = GWT.getModuleBaseURL().replace( GWT.getModuleName() + "/", "" );
        return baseUrl + "maven2wb/";
    }

    public void search( String filter ) {
        presenter().search( filter );
    }

    public void refresh() {
        presenter().refresh();
    }

    private ArtifactListPresenter presenter() {
        if ( presenter == null ) {
            presenter = IOC.getBeanManager().lookupBean( ArtifactListPresenter.class ).getInstance();
            presenter.setup( ColumnType.NAME, ColumnType.GAV, ColumnType.LAST_MODIFIED );
            // Add "View KJAR's pom" button column
            final Column<JarListPageRow, String> openColumn = new Column<JarListPageRow, String>( new ButtonCell( ButtonSize.EXTRA_SMALL ) ) {
                @Override
                public String getValue( JarListPageRow row ) {
                    return M2RepoEditorConstants.INSTANCE.Open();
                }
            };
            openColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
                @Override
                public void update( int index,
                                    JarListPageRow row,
                                    String value ) {
                    presenter.onOpenPom( row.getPath() );
                }
            } );
            presenter.getView().addColumn( openColumn,
                                           M2RepoEditorConstants.INSTANCE.Open(),
                                           100.0,
                                           Style.Unit.PX );

            //If the current user is an Administrator include the download button
            if ( identity.getRoles().contains( new RoleImpl( "admin" ) ) ) {
                final Column<JarListPageRow, String> downloadColumn = new Column<JarListPageRow, String>( new ButtonCell( ButtonSize.EXTRA_SMALL ) ) {
                    public String getValue( JarListPageRow row ) {
                        return M2RepoEditorConstants.INSTANCE.Download();
                    }
                };

                downloadColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
                    public void update( int index,
                                        JarListPageRow row,
                                        String value ) {
                        Window.open( getFileDownloadURL( row.getPath() ),
                                     M2RepoEditorConstants.INSTANCE.Downloading(),
                                     "resizable=no,scrollbars=yes,status=no" );
                    }
                } );

                presenter.getView().addColumn( downloadColumn,
                                               M2RepoEditorConstants.INSTANCE.Download(),
                                               100.0,
                                               Style.Unit.PX );
            }
            presenter.search( "" );
            content.add( presenter.getView() );
        }
        return presenter;
    }

    public void init() {
        presenter();
    }
}
