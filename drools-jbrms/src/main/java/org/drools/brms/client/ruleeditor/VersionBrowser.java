package org.drools.brms.client.ruleeditor;

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
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.table.DataModel;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This widget shows a list of versions.
 *
 * @author Michael Neale
 */
public class VersionBrowser extends Composite {

    private Image     refresh;
    private FlexTable layout;
    private String    uuid;
    private MetaData  metaData;
    private Command   refreshCommand;

    public VersionBrowser(String uuid,
                          MetaData data,
                          Command ref) {

        this.uuid = uuid;
        this.metaData = data;
        this.refreshCommand = ref;

        this.uuid = uuid;
        HorizontalPanel wrapper = new HorizontalPanel();

        layout = new FlexTable();
        layout.setWidget( 0,
                          0,
                          new Label( "Version history" ) );
        layout.getCellFormatter().setStyleName( 0, 0, "metadata-Widget" );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setHorizontalAlignment( 0,
                                          0,
                                          HasHorizontalAlignment.ALIGN_LEFT );

        refresh = new ImageButton( "images/refresh.gif" );

        refresh.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                clickLoadHistory();
            }
        } );

        layout.setWidget( 0,
                          1,
                          refresh );
        formatter.setHorizontalAlignment( 0,
                                          1,
                                          HasHorizontalAlignment.ALIGN_RIGHT );

        wrapper.setStyleName( "version-browser-Border" );

        wrapper.add( layout );

        layout.setWidth( "100%" );
        wrapper.setWidth( "100%" );

        initWidget( wrapper );
    }

    protected void clickLoadHistory() {
        showBusyIcon();
        DeferredCommand.add( new Command() {
            public void execute() {
                loadHistoryData();
            }
        } );

    }

    private void showBusyIcon() {
        refresh.setUrl( "images/searching.gif" );
    }

    /**
     * Actually load the history data, as demanded.
     */
    protected void loadHistoryData() {

        RepositoryServiceFactory.getService().loadAssetHistory( this.uuid,
                                                                new GenericCallback() {

                                                                    public void onSuccess(Object data) {
                                                                        if ( data == null ) {
                                                                            layout.setWidget( 1,
                                                                                              0,
                                                                                              new Label( "No history." ) );
                                                                            showStaticIcon();
                                                                            return;
                                                                        }
                                                                        TableDataResult table = (TableDataResult) data;
                                                                        final TableDataRow[] rows = table.data;

                                                                        String[] header = new String[]{"Version number", "Comment", "Date Modified", "Status"};

                                                                        DataModel mdl = getTableDataModel( rows );

                                                                        final SortableTable tableWidget = SortableTable.createTableWidget( mdl,
                                                                                                                                           header,
                                                                                                                                           0,
                                                                                                                                           false);

                                                                        tableWidget.setWidth( "100%" );

                                                                        layout.setWidget( 1,
                                                                                          0,
                                                                                          tableWidget );
                                                                        FlexCellFormatter formatter = layout.getFlexCellFormatter();

                                                                        formatter.setColSpan( 1,
                                                                                              0,
                                                                                              2 );

                                                                        Button open = new Button( "View selected version" );

                                                                        open.addClickListener( new ClickListener() {
                                                                            public void onClick(Widget w) {
                                                                                if ( tableWidget.getSelectedRow() == 0 ) return;
                                                                                showVersion( tableWidget.getSelectedKey() );
                                                                            }

                                                                        } );

                                                                        layout.setWidget( 2,
                                                                                          1,
                                                                                          open );
                                                                        formatter.setColSpan( 2,
                                                                                              1,
                                                                                              3 );
                                                                        formatter.setHorizontalAlignment( 2,
                                                                                                          1,
                                                                                                          HasHorizontalAlignment.ALIGN_CENTER );

                                                                        showStaticIcon();

                                                                    }

                                                                } );

    }

    /**
     * This should popup a view of the chosen historical version.
     * @param selectedUUID
     */
    private void showVersion(String selectedUUID) {

        VersionViewer viewer = new VersionViewer( this.metaData,
                                                  selectedUUID,
                                                  uuid,
                                                  refreshCommand );
        viewer.setPopupPosition( 100,
                                 100 );

        viewer.show();

    }

    private void showStaticIcon() {
        refresh.setUrl( "images/refresh.gif" );
    }

    private DataModel getTableDataModel(final TableDataRow[] rows) {
        return new DataModel() {

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
                return null;
            }

        };
    }

}