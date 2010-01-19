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

import org.drools.guvnor.client.common.ClickableLabel;
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
import org.drools.guvnor.client.messages.Constants;

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
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

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
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public VersionBrowser(String uuid,
                          MetaData data,
                          Command ref) {

        this.uuid = uuid;
        this.metaData = data;
        this.refreshCommand = ref;

        this.uuid = uuid;
        HorizontalPanel wrapper = new HorizontalPanel();

        ClickListener cl = new ClickListener() {
            public void onClick(Widget w) {
                clickLoadHistory();
            }
        } ;
        layout = new FlexTable();
        ClickableLabel vh = new ClickableLabel(constants.VersionHistory1(), cl );
        layout.setWidget( 0, 0, vh );
        layout.getCellFormatter().setStyleName( 0, 0, "metadata-Widget" ); //NON-NLS
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setHorizontalAlignment( 0,
                                          0,
                                          HasHorizontalAlignment.ALIGN_LEFT );

        refresh = new ImageButton( "images/refresh.gif" );  //NON-NLS


        refresh.addClickListener( cl );

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
                                                                new GenericCallback<TableDataResult>() {

                                                                    public void onSuccess(TableDataResult table) {
                                                                        if ( table == null ) {
                                                                            layout.setWidget( 1, 0, new Label(constants.NoHistory()) );
                                                                            showStaticIcon();
                                                                            return;
                                                                        }
                                                                        TableDataRow[] rows = table.data;
                                                                        Arrays.sort(rows, new Comparator<TableDataRow>() {
																			public int compare(
																					TableDataRow r1,
																					TableDataRow r2) {
																			    
																			    Integer v2 = Integer.valueOf( r2.values[0] );
                                                                                Integer v1 = Integer.valueOf( r1.values[0] );
																			    
																				return v2.compareTo( v1 );
																			}
                                                                        });


                                                                        final ListBox history = new ListBox(true);
                                                                        
                                                                        for (int i = 0; i < rows.length; i++) {
																			TableDataRow row = rows[i];
                                                                            String s = Format.format(constants.property0ModifiedOn12(), row.values[0], row.values[2], row.values[1] ) ;
																			history.addItem(s, row.id);
																		}


                                                                        layout.setWidget( 1, 0, history );
                                                                        FlexCellFormatter formatter = layout.getFlexCellFormatter();

                                                                        formatter.setColSpan( 1,0,2 );

                                                                        Button open = new Button(constants.View());

                                                                        open.addClickListener( new ClickListener() {
                                                                            public void onClick(Widget w) {
                                                                            	showVersion(history.getValue(history.getSelectedIndex()));
                                                                            }

                                                                        } );

                                                                        layout.setWidget( 2, 0, open );
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

        LoadingPopup.showMessage(constants.LoadingVersionFromHistory());

        RepositoryServiceFactory.getService().loadRuleAsset( versionUUID, new GenericCallback<RuleAsset>() {

            public void onSuccess(RuleAsset asset) {
                asset.isreadonly = true;
                asset.metaData.name = metaData.name;
                final FormStylePopup pop = new FormStylePopup("images/snapshot.png", Format.format(constants.VersionNumber0Of1(), "" + asset.metaData.versionNumber, asset.metaData.name),
                		new Integer(800), new Boolean(false));

                Button restore = new Button(constants.RestoreThisVersion());
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

                RuleViewer viewer = new RuleViewer(asset, null, true);
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
                constants.RestoreThisVersionQ());
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
                constants.RestoreThisVersionQ());
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