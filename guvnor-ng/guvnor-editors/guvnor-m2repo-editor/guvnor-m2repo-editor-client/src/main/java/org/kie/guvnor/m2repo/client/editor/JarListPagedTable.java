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

package org.kie.guvnor.m2repo.client.editor;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.data.tables.PageRequest;
import org.kie.guvnor.commons.data.tables.PageResponse;
import org.kie.guvnor.commons.ui.client.tables.AbstractPagedTable;
import org.kie.guvnor.commons.ui.client.tables.ColumnPicker;
import org.kie.guvnor.commons.ui.client.tables.SelectionColumn;
import org.kie.guvnor.commons.ui.client.tables.SortableHeader;
import org.kie.guvnor.commons.ui.client.tables.SortableHeaderGroup;
import org.kie.guvnor.m2repo.model.JarListPageRow;
import org.kie.guvnor.m2repo.service.M2RepoService;
import org.uberfire.client.common.LoadingPopup;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Widget with a table of jar list in Guvnor M2_REPO
 */
public class JarListPagedTable extends AbstractPagedTable<JarListPageRow> {

    // UI
    interface JarListPagedTableBinder
        extends
        UiBinder<Widget, JarListPagedTable> {
    }

/*    @UiField()
    protected Button                             uploadJarButton;*/

    @UiField()
    protected Button                             deleteSelectedJarButton;

    @UiField()
    protected Button                             refreshButton;
    
    @UiField()
    protected Button                             auditButton;
    
    private static JarListPagedTableBinder uiBinder  = GWT.create( JarListPagedTableBinder.class );
    
    protected MultiSelectionModel<JarListPageRow> selectionModel;
    
    // Other stuff
    private static final int                     PAGE_SIZE = 10;    

    //@Inject
    private Caller<M2RepoService> m2RepoService;
    
    public JarListPagedTable(Caller<M2RepoService> repoService, final String searchFilter) {
        super( PAGE_SIZE );
        this.m2RepoService = repoService;

        setDataProvider( new AsyncDataProvider<JarListPageRow>() {
            protected void onRangeChanged(HasData<JarListPageRow> display) {
                PageRequest request = new PageRequest( pager.getPageStart(), pageSize );
                
                m2RepoService.call( new RemoteCallback<PageResponse<JarListPageRow>>() {
                    @Override
                    public void callback( final PageResponse<JarListPageRow> response) {
                        updateRowCount( response.getTotalRowSize(),
                                response.isTotalRowSizeExact() );
                        updateRowData( response.getStartRowIndex(),
                               response.getPageRowList() );
                    }
                } ).listJars(request, searchFilter);
            }
        } );

    }
    @Override
    protected void doCellTable() {

        ProvidesKey<JarListPageRow> providesKey = new ProvidesKey<JarListPageRow>() {
            public Object getKey(JarListPageRow row) {
                return row.getPath();
            }
        };

        cellTable = new CellTable<JarListPageRow>( providesKey );
        selectionModel = new MultiSelectionModel<JarListPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<JarListPageRow> columnPicker = new ColumnPicker<JarListPageRow>( cellTable );
        SortableHeaderGroup<JarListPageRow> sortableHeaderGroup = new SortableHeaderGroup<JarListPageRow>( cellTable );


        // Add any additional columns
        addAncillaryColumns( columnPicker,
                sortableHeaderGroup );


        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    @Override
    protected void addAncillaryColumns(ColumnPicker<JarListPageRow> columnPicker,
                                       SortableHeaderGroup<JarListPageRow> sortableHeaderGroup) {

        TextColumn<JarListPageRow> nameColumn = new TextColumn<JarListPageRow>() {
            public String getValue(JarListPageRow row) {
                return row.getName();
            }
        };
        columnPicker.addColumn( nameColumn,
                                new SortableHeader<JarListPageRow, String>(
                                                                                  sortableHeaderGroup,
                                                                                  "Name",
                                                                                  nameColumn ),
                                true );
        
        TextColumn<JarListPageRow> pathColumn = new TextColumn<JarListPageRow>() {
            public String getValue(JarListPageRow row) {
                return row.getPath();
            }
        };
        columnPicker.addColumn( pathColumn,
                                new SortableHeader<JarListPageRow, String>(
                                                                                  sortableHeaderGroup,
                                                                                  "Path",
                                                                                  pathColumn ),
                                true );
        
        Column<JarListPageRow, Date> lastModifiedColumn = new Column<JarListPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue(JarListPageRow row) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<JarListPageRow, Date>( sortableHeaderGroup,
                                                                        "LastModified",
                                                                        lastModifiedColumn ),
                                false );

        // Add "View kjar detail" button column
        Column<JarListPageRow, String> openColumn = new Column<JarListPageRow, String>( new ButtonCell() ) {
            public String getValue(JarListPageRow row) {
                return "Open";
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
            public void update(int index,
                               JarListPageRow row,
                               String value) {
                m2RepoService.call( new RemoteCallback<String>() {
                    @Override
                    public void callback( final String response) {
                        JarDetailEditor editor = new JarDetailEditor(response);
                        editor.show();
                    }
                } ).getJarDetails(row.getPath());
            }
        } );
        columnPicker.addColumn( openColumn,
                new TextHeader( "Open" ),
                true ); 
        
        
        // Add "Download" button column
        Column<JarListPageRow, String> downloadColumn = new Column<JarListPageRow, String>( new ButtonCell() ) {
            public String getValue(JarListPageRow row) {
                return "Download";
            }
        };
        downloadColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
            public void update(int index,
                    JarListPageRow row,
                               String value) {
                Window.open( GWT.getModuleBaseURL() + "file?" + HTMLFileManagerFields.FORM_FIELD_UUID + "=" + "uuid",
                        "downloading",
                        "resizable=no,scrollbars=yes,status=no" );
            }
        } );
        columnPicker.addColumn( downloadColumn,
                new TextHeader( "Download" ),
                true );       

     }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

/*    @UiHandler("uploadJarButton")
    void uploadJar(ClickEvent e) {

    }*/

    @UiHandler("deleteSelectedJarButton")
    void deleteSelectedJar(ClickEvent e) {
        if (getSelectedJars() == null) {
            Window.alert("Please Select A Jar To Delete");
            return;
        }
        if (!Window.confirm("AreYouSureYouWantToDeleteTheseItems")) {
            return;
        }
        m2RepoService.call( new RemoteCallback<Void>() {
            @Override
            public void callback(Void v) {
            }
        } ).deleteJar(getSelectedJars());
    }   
    
    public String[] getSelectedJars() {
        Set<JarListPageRow> selectedRows = selectionModel.getSelectedSet();

        // Compatibility with existing API
        if ( selectedRows.size() == 0 ) {
            return null;
        }

        // Create the array of paths
        String[] paths = new String[selectedRows.size()];
        int rowCount = 0;
        for (JarListPageRow row : selectedRows) {
            paths[rowCount++] = row.getPath();
        }
        return paths;
    }
    
    @UiHandler("refreshButton")
    void refresh(ClickEvent e) {
        selectionModel.clear();
        cellTable.setVisibleRangeAndClearData( cellTable.getVisibleRange(),
                true );
    }
    
    @UiHandler("auditButton")
    void viewAuditLog(ClickEvent e) {

    }   
    


}
