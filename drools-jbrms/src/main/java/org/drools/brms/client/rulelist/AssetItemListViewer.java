package org.drools.brms.client.rulelist;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.ruleeditor.EditorLauncher;
import org.drools.brms.client.table.DataModel;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.grid.Grid;
import com.gwtext.client.widgets.grid.event.GridRowListener;

/**
 * This is a viewer for viewing a list of rules for editing/selection.
 * This uses the sortable table widget, and can be re-used with different data sets.
 * (ie no need to throw is away).
 */
public class AssetItemListViewer extends Composite {

    /** The number of rows to "fill out" */
    private static final int              FILLER_ROWS                 = 25;

    public static final String            RULE_LIST_TABLE_ID          = "rulelist";
    public static final String            ARCHIVED_RULE_LIST_TABLE_ID = "archivedrulelist";

    private FlexTable                     outer                       = new FlexTable();
    private SortableTable                 table;
    private TableConfig                   tableConfig;
    private EditItemEvent                 openItemEvent;

    private Image                         refreshIcon                 = new ImageButton( "images/refresh.gif" );
    private Command                       refresh;
    private static RepositoryServiceAsync service                     = RepositoryServiceFactory.getService();
    private Label                         itemCounter                 = new Label();

    public AssetItemListViewer(EditItemEvent event,
                               String tableconfig) {

        init();

        loadTableConfig( tableconfig );
        this.refreshIcon.setVisible( false );
        this.openItemEvent = event;
        this.refreshIcon.setTitle( "Refresh current list. Will show any changes." );
        this.refreshIcon.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                LoadingPopup.showMessage( "Refreshing list, please wait..." );
                refresh.execute();
            }
        } );
    }

    /**
     * Optionally set the refresh command to re-populate the list on demand.
     * @param refreshCom
     */
    public void setRefreshCommand(Command refreshCom) {
        this.refresh = refreshCom;
        this.refreshIcon.setVisible( true );

    }

    private void loadTableConfig(String tableconfig) {
        service.loadTableConfig( tableconfig,
                                 new GenericCallback() {
                                     public void onSuccess(Object o) {
                                         tableConfig = (TableConfig) o;
                                         loadTableData( null );
                                     }
                                 } );
    }

    /**
     * Initialize the widget goodness.
     */
    private void init() {
        FlexCellFormatter formatter = outer.getFlexCellFormatter();

        //outer.setStyleName( SortableTable.styleList );
        outer.setWidth( "100%" );

        formatter.setAlignment( 0,
                                0,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_TOP );

        Image openIcon = new ImageButton( "images/open_item.gif" );
        openIcon.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                LoadingPopup.showMessage( "Loading item, please wait ..." );
                openItemEvent.open( TableDataRow.getId( table.getSelectedKey() ) );

            }
        } );
        openIcon.setTitle( "Open item" );

        outer.setWidget( 0,
                         1,
                         openIcon );
        formatter.setAlignment( 0,
                                1,
                                HasHorizontalAlignment.ALIGN_RIGHT,
                                HasVerticalAlignment.ALIGN_TOP );

        initWidget( outer );
    }

    /**
     * This will create a table, and load the data, wrapping it in a scrolling area.
     * If the data is null, it will just fill it in with something empty
     * so it looks busy.
     *
     * This could probably clear the table, but I just throw it away
     * let the garbage collector do the hard work.
     */
    public void loadTableData(TableDataResult data) {
        FlexCellFormatter formatter = outer.getFlexCellFormatter();
        outer.setWidget( 1,
                         0,
                         null );

        //if no data, just fill it out
        if ( data == null || data.data.length == 0 ) {

            DataModel nil = new DataModel() {

                public int getNumberOfRows() {
                    return 0;
                }

                public String getRowId(int row) {
                    return "";
                }

                public Comparable getValue(int row,
                                           int col) {
                    return "";
                }

                public Widget getWidget(int row,
                                        int col) {
                    return null;
                }

            };

            table = SortableTable.createTableWidget( nil,
                                                     tableConfig.headers,
                                                     FILLER_ROWS,
                                                     true );
            itemCounter.setVisible( false );
        } else {
            final TableDataRow[] rows = data.data;
            DataModel mdl = new DataModel() {

                public int getNumberOfRows() {
                    return rows.length;
                }

                public String getRowId(int row) {
                    return rows[row].id;
                }

                public Comparable getValue(int row,
                                           int col) {
                    return rows[row].values[col];
                }

                public Widget getWidget(int row,
                                        int col) {

                    if ( tableConfig.headers[col].equals( "*" ) ) {
                        return new Image( "images/" + EditorLauncher.getAssetFormatIcon( rows[row].format ) );
                    } else {
                        return null;
                    }
                }

            };

            table = SortableTable.createTableWidget( mdl,
                                                     this.tableConfig.headers,
                                                     FILLER_ROWS,
                                                     true );

            HorizontalPanel panel = new HorizontalPanel();
            panel.add( refreshIcon );
            itemCounter.setVisible( true );
            itemCounter.setText( "  " + data.data.length + " items." );
            panel.add( itemCounter );

            outer.setWidget( 0,
                             0,
                             panel );
        }

        table.setWidth( "100%" );
        outer.setWidget( 1,
                         0,
                         table );
        formatter.setColSpan( 1,
                              0,
                              2 );

    }

    public String getSelectedElementUUID() {
        return TableDataRow.getId( table.getSelectedKey() );
    }
}