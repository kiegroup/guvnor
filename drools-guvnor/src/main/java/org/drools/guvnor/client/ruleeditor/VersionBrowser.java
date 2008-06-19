package org.drools.guvnor.client.ruleeditor;

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

import java.util.Arrays;
import java.util.Comparator;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.MetaData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.client.table.DataModel;

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
import com.google.gwt.user.client.ui.ListBox;
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
        DeferredCommand.addCommand( new Command() {
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
                                                                            layout.setWidget( 1, 0, new Label( "No history." ) );
                                                                            showStaticIcon();
                                                                            return;
                                                                        }
                                                                        TableDataResult table = (TableDataResult) data;
                                                                        TableDataRow[] rows = table.data;
                                                                        Arrays.sort(rows, new Comparator() {
																			public int compare(
																					Object o1,
																					Object o2) {
																				TableDataRow r1 = (TableDataRow) o1;
																				TableDataRow r2 = (TableDataRow) o2;
																				return r2.values[0].compareTo(r1.values[0]);
																			}

                                                                        });

                                                                        //String[] header = new String[]{"Version number", "Comment", "Date Modified", "Status"};

                                                                        final ListBox history = new ListBox(true);

                                                                        for (int i = 0; i < rows.length; i++) {
																			TableDataRow row = rows[i];
																			String s = row.values[0]   + " modified on: " + row.values[2] + " (" + row.values[1] + ")" ;
																			history.addItem(s, row.id);
																		}


                                                                        layout.setWidget( 1, 0, history );
                                                                        FlexCellFormatter formatter = layout.getFlexCellFormatter();

                                                                        formatter.setColSpan( 1,0,2 );

                                                                        Button open = new Button( "View" );

                                                                        open.addClickListener( new ClickListener() {
                                                                            public void onClick(Widget w) {
                                                                            	showVersion(history.getValue(history.getSelectedIndex()));
                                                                            }

                                                                        } );

                                                                        layout.setWidget( 2, 1, open );
                                                                        formatter.setColSpan( 2, 1, 3 );
                                                                        formatter.setHorizontalAlignment( 2,1,HasHorizontalAlignment.ALIGN_CENTER );

                                                                        showStaticIcon();

                                                                    }

                                                                } );

    }

    /**
     * This should popup a view of the chosen historical version.
     */
    private void showVersion(final String versionUUID) {
//        VersionViewer viewer = new VersionViewer( this.metaData, versionUUID, uuid, refreshCommand );

        LoadingPopup.showMessage( "Loading version" );

        RepositoryServiceFactory.getService().loadRuleAsset( versionUUID, new GenericCallback() {

            public void onSuccess(Object data) {

                RuleAsset asset = (RuleAsset) data;
                asset.isreadonly = true;
                asset.metaData.name = metaData.name;

                final FormStylePopup pop = new FormStylePopup("images/snapshot.png", "Version number [" + asset.metaData.versionNumber + "] of [" + asset.metaData.name + "]",
                		new Integer(800), new Integer(500), new Boolean(false));

                Button restore = new Button("Restore this version");
                restore.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        restore(w, versionUUID, new Command() {
							public void execute() {
								refreshCommand.execute();
								pop.hide();
							}
                        });
                    }
                });

                RuleViewer viewer = new RuleViewer(asset, true);
                viewer.setWidth( "100%" );


                pop.addRow(restore);
                pop.addRow(viewer);
                pop.show();
            }
        });
    }

    private void restore(Widget w, final String versionUUID, final Command refresh) {

        final CheckinPopup pop = new CheckinPopup(w.getAbsoluteLeft() + 10,
                                                  w.getAbsoluteTop() + 10,
                                                  "Restore this version?");
        pop.setCommand( new Command() {
            public void execute() {
                RepositoryServiceFactory.getService().restoreVersion( versionUUID, uuid, pop.getCheckinComment(), new GenericCallback() {
                    public void onSuccess(Object data) {
                        refresh.execute();
                    }
                });
            }
        });
        pop.show();
    }


    private void restore(Widget w, final Command refresh, final String versionUUID, final String headUUID) {

        final CheckinPopup pop = new CheckinPopup(w.getAbsoluteLeft() + 10,
                                                  w.getAbsoluteTop() + 10,
                                                  "Restore this version?");
        pop.setCommand( new Command() {
            public void execute() {
                RepositoryServiceFactory.getService().restoreVersion( versionUUID, headUUID, pop.getCheckinComment(), new GenericCallback() {
                    public void onSuccess(Object data) {
                        refresh.execute();
                    }
                });
            }
        });
        pop.show();
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