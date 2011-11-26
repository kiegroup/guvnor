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

package org.drools.guvnor.client.widgets.tables;

import org.drools.guvnor.client.rpc.AbstractPageRow;
import org.drools.guvnor.client.rpc.AssetServiceAsync;
import org.drools.guvnor.client.rpc.CategoryServiceAsync;
import org.drools.guvnor.client.rpc.PackageServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.AsyncDataProvider;

/**
 * Widget that shows rows of paged data.
 */
public abstract class AbstractPagedTable<T extends AbstractPageRow> extends AbstractSimpleTable<T> {

    // TODO use (C)DI (first GWT/Seam needs to behave properly in hosted mode)
    protected RepositoryServiceAsync repositoryService = RepositoryServiceFactory.getService();
    protected AssetServiceAsync      assetService      = RepositoryServiceFactory.getAssetService();
    protected PackageServiceAsync    packageService    = RepositoryServiceFactory.getPackageService();
    protected CategoryServiceAsync   categoryService = RepositoryServiceFactory.getCategoryService();

    protected int                    pageSize;
    protected AsyncDataProvider<T>   dataProvider;

    @UiField
    public GuvnorSimplePager      pager;

    /**
     * Constructor
     * 
     * @param pageSize
     */
    public AbstractPagedTable(int pageSize) {
        this.pageSize = pageSize;
        pager.setDisplay( cellTable );
        pager.setPageSize( pageSize );
    }

    /**
     * Set up table with zero columns. Additional columns can be appended by
     * overriding <code>addAncillaryColumns()</code>
     */
    protected void doCellTable() {

        cellTable = new CellTable<T>();

        ColumnPicker<T> columnPicker = new ColumnPicker<T>( cellTable );
        SortableHeaderGroup<T> sortableHeaderGroup = new SortableHeaderGroup<T>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();

    }

    /**
     * Link a data provider to the table
     * 
     * @param dataProvider
     */
    public void setDataProvider(AsyncDataProvider<T> dataProvider) {
        this.dataProvider = dataProvider;
        this.dataProvider.addDataDisplay( cellTable );
    }

}
