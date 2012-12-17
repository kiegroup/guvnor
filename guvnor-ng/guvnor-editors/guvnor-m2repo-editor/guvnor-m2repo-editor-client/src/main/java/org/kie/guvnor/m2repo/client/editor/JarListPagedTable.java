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

import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.data.tables.PageRequest;
import org.kie.guvnor.commons.data.tables.PageResponse;
import org.kie.guvnor.commons.ui.client.tables.AbstractPagedTable;
import org.kie.guvnor.commons.ui.client.tables.ColumnPicker;
import org.kie.guvnor.commons.ui.client.tables.SortableHeader;
import org.kie.guvnor.commons.ui.client.tables.SortableHeaderGroup;
import org.kie.guvnor.m2repo.model.JarListPageRow;
import org.kie.guvnor.m2repo.service.M2RepoService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Widget with a table of jar list in Guvnor M2_REPO
 */
public class JarListPagedTable extends AbstractPagedTable<JarListPageRow> {

    // UI
    interface JarListPagedTableBinder
        extends
        UiBinder<Widget, JarListPagedTable> {
    }

    @UiField()
    protected Button                             uploadJarButton;

    @UiField()
    protected Button                             deleteSelectedJarButton;

/*    @UiField()
    protected Button                             refreshButton;*/
    
    private static JarListPagedTableBinder uiBinder  = GWT.create( JarListPagedTableBinder.class );


    // Other stuff
    private static final int                     PAGE_SIZE = 10;    

/*    @Inject
    private Caller<M2RepoService> m2RepoService;*/
    
    public JarListPagedTable() {
        //initWidget( uiBinder.createAndBindUi( this ) );
        super( PAGE_SIZE );
/*
        setDataProvider( new AsyncDataProvider<JarListPageRow>() {
            protected void onRangeChanged(HasData<JarListPageRow> display) {
                PageRequest request = new PageRequest( 0pager.getPageStart(), pageSize );
                String filters = null;
                
                m2RepoService.call( new RemoteCallback<PageResponse<JarListPageRow>>() {
                    @Override
                    public void callback( final PageResponse<JarListPageRow> response) {
                        updateRowCount( response.getTotalRowSize(),
                                response.isTotalRowSizeExact() );
                        updateRowData( response.getStartRowIndex(),
                               response.getPageRowList() );
                    }
                } ).listJars(request, filters);
            }
        } );*/

    }

    //@Override
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
                                                                                  "name",
                                                                                  nameColumn ),
                                true );
        
        TextColumn<JarListPageRow> lastModifiedColumn = new TextColumn<JarListPageRow>() {
            public String getValue(JarListPageRow row) {
                return row.getLastModified().toString();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<JarListPageRow, String>(
                                                                                  sortableHeaderGroup,
                                                                                  "lastModified",
                                                                                  lastModifiedColumn ),
                                true );

     }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("uploadJarButton")
    void uploadJar(ClickEvent e) {

    }

    @UiHandler("deleteSelectedJarButton")
    void deleteSelectedJar(ClickEvent e) {

    }
    
/*    @UiHandler("refreshButton")
    void refresh(ClickEvent e) {

    }*/

}
