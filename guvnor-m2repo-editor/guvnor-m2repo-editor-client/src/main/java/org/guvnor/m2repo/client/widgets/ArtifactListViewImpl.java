/*
 * Copyright 2014 JBoss Inc
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
package org.guvnor.m2repo.client.widgets;

import java.util.Comparator;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import org.guvnor.m2repo.client.editor.JarDetailPopup;
import org.guvnor.m2repo.client.resources.i18n.M2RepoEditorConstants;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.tables.ResizableHeader;

public class ArtifactListViewImpl
        extends Composite
        implements ArtifactListView {

    private static final int PAGE_SIZE = 10;

    interface Binder
            extends
            UiBinder<Widget, ArtifactListViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    protected ArtifactListPresenter presenter;

    @Inject
    protected Caller<M2RepoService> m2RepoService;

    @UiField
    public DataGrid<JarListPageRow> cellTable;

    @UiField
    public SimplePager pager;

    @UiField
    public SimplePanel listContainer;

    protected String currentFilter = "";

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setContentHeight( String s ) {
        listContainer.setHeight( s );
    }

    public void init( final ArtifactListPresenter presenter ) {
        this.presenter = presenter;

        // Set the message to display when the table is empty.
        final Label emptyTable = new Label( M2RepoEditorConstants.INSTANCE.NoArtifactAvailable() );
        emptyTable.setStyleName( "" );
        cellTable.setEmptyTableWidget( emptyTable );

        // Create a Pager to control the table.
        pager.setDisplay( cellTable );
        pager.setPageSize( PAGE_SIZE );

        initTableColumns();
    }

    public void addColumn( final Column<JarListPageRow, ?> column,
                           final Comparator<JarListPageRow> comparator,
                           final String columnName ) {
        cellTable.addColumn( column, new ResizableHeader( columnName,
                                                          cellTable,
                                                          column ) );
    }

    private void initTableColumns() {
        final Column<JarListPageRow, String> nameColumn = new Column<JarListPageRow, String>( new TextCell() ) {
            @Override
            public String getValue( JarListPageRow row ) {
                return row.getName();
            }
        };
        cellTable.addColumn( nameColumn, new ResizableHeader(M2RepoEditorConstants.INSTANCE.Name(),
                                                              cellTable,
                                                              nameColumn ) );

        final Column<JarListPageRow, String> pathColumn = new Column<JarListPageRow, String>( new TextCell() ) {
            @Override
            public String getValue( JarListPageRow row ) {
                return row.getPath();
            }
        };
        cellTable.addColumn( pathColumn, new ResizableHeader( M2RepoEditorConstants.INSTANCE.Path(),
                                                              cellTable,
                                                              pathColumn ) );

        final Column<JarListPageRow, Date> lastModifiedColumn = new Column<JarListPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            @Override
            public Date getValue( JarListPageRow row ) {
                return row.getLastModified();
            }
        };
        cellTable.addColumn( lastModifiedColumn, new ResizableHeader( M2RepoEditorConstants.INSTANCE.LastModified(),
                                                                      cellTable,
                                                                      lastModifiedColumn ) );

        // Add "View kjar detail" button column
        final Column<JarListPageRow, String> openColumn = new Column<JarListPageRow, String>( new ButtonCell() {{
            setSize( ButtonSize.MINI );
        }} ) {
            public String getValue( JarListPageRow row ) {
                return M2RepoEditorConstants.INSTANCE.Open();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
            public void update( int index,
                                JarListPageRow row,
                                String value ) {
                m2RepoService.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response ) {
                        JarDetailPopup popup = new JarDetailPopup( response );
                        popup.show();
                    }
                } ).loadPOMStringFromJar( row.getPath() );
            }
        } );
        cellTable.addColumn( openColumn, new ResizableHeader( M2RepoEditorConstants.INSTANCE.Open(),
                                                              cellTable,
                                                              openColumn ) );
    }

    @Override
    public String getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public void setCurrentFilter( final String currentFilter ) {
        this.currentFilter = currentFilter;
    }

    @Override
    public HasData<JarListPageRow> getDisplay() {
        return this.cellTable;
    }

}
