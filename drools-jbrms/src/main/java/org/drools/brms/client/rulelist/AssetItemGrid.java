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

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.ruleeditor.EditorLauncher;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.ButtonConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.Grid;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;

/**
 * Uses an awesome grid and does paging for asset lists.
 * @author Michael Neale
 */
public class AssetItemGrid extends Composite {


    public static final String            RULE_LIST_TABLE_ID          = "rulelist";
    public static final String            ARCHIVED_RULE_LIST_TABLE_ID = "archivedrulelist";
    private static final Map 			  columnConfigs = new HashMap();
    private static final Map		      recordDefs = new HashMap();
    private static final Map			  rowsPerPage = new HashMap();

    private final EditItemEvent editEvent;
    private SimplePanel layout;

    private int currentPosition = 0;
	protected Store store;

    public AssetItemGrid(final EditItemEvent event, final String tableConfig, final AssetItemGridDataLoader source) {

        this.editEvent = event;
        this.layout = new SimplePanel();

        if (!columnConfigs.containsKey(tableConfig)) {
            RepositoryServiceFactory.getService().loadTableConfig(tableConfig, new GenericCallback() {
                public void onSuccess(Object data) {
                    TableConfig conf = (TableConfig) data;
                    ColumnModel cm = createColumnModel(conf);
                    columnConfigs.put(tableConfig, cm);
                    RecordDef rd = createRecordDef(conf);
                    recordDefs.put(tableConfig, rd);
                    rowsPerPage.put(tableConfig, new Integer(conf.rowsPerPage));
                    doGrid(source, cm, rd, conf.rowsPerPage);
                }
            });
        } else {
            doGrid(source,
                    (ColumnModel) columnConfigs.get(tableConfig),
                    (RecordDef) recordDefs.get(tableConfig), ((Integer) rowsPerPage.get(tableConfig)).intValue());
        }

        initWidget(layout);
    }


    /**
     * Actually build the grid.
     */
    private void doGrid(final AssetItemGridDataLoader source, final ColumnModel cm, final RecordDef rd, final int pageSize) {
        final int numFlds = rd.getFields().length;
        LoadingPopup.showMessage("Loading data...");
        source.loadData(currentPosition, pageSize, new GenericCallback() {
            public void onSuccess(Object data) {
                TableDataResult result = (TableDataResult) data;
                Object[][] gridData = new Object[result.data.length][];
                for (int i = 0; i < result.data.length; i++) {
                    TableDataRow row = result.data[i];
                    Object[] rowData = new Object[numFlds];
                    rowData[0] = row.id;
                    rowData[1] = row.format;
                    for(int j = 2; j < numFlds; j++) {
                        rowData[j] = row.values[j - 2];
                    }
                    gridData[i] = rowData;
                }
                MemoryProxy proxy = new MemoryProxy(gridData);
                ArrayReader reader = new ArrayReader(rd);
                store = new Store(proxy, reader);
                final Grid g = new Grid(Ext.generateId(), "600px", "600px", store, cm);
                g.render();
                g.setLoadMask("Loading data...");

                Toolbar tb = new Toolbar(g.getView().getHeaderPanel(true));
                tb.addItem(new ToolbarTextItem(Format.format(
                                        "Showing item #{0} to {1} of {2} items.",
                                        new String[] {""+(currentPosition + 1), "" + (currentPosition + result.data.length), "" + result.total})));
                if (currentPosition > 0) {
                    navButton(source, cm, rd, pageSize, g, false, tb);
                }
                if (result.hasNext) {
                    navButton(source, cm, rd, pageSize, g, true, tb);
                }

                tb.addButton(new ToolbarButton(new ButtonConfig() {
                    {
                        setText("Refresh");
                        setButtonListener(new ButtonListenerAdapter() {
                            public void onClick(Button button, EventObject e) {
                                layout.clear();
                                g.destroy();
                                doGrid(source, cm, rd, pageSize);
                            }
                        });
                    }
                }));



                g.addGridRowListener(new GridRowListenerAdapter() {
                    public void onRowDblClick(Grid grid, int rowIndex, EventObject e) {
                        String uuid = grid.getSelectionModel().getSelected().getAsString("uuid");
                        System.err.println("Opening: " + uuid);
                        editEvent.open(uuid);
                    }
                });
                store.load();
                layout.add(g);
                LoadingPopup.close();
            }


        });
    }

    private void navButton(final AssetItemGridDataLoader source,
            final ColumnModel cm, final RecordDef rd,
            final int pageSize, final Grid g, final boolean forward, Toolbar tb) {

        ToolbarButton b = new ToolbarButton(new ButtonConfig() {
            {
                setText((forward) ? "Next ->" : "<- Previous");
            }
        });
        tb.addButton(b);

        b.addButtonListener(new ButtonListenerAdapter() {
                    public void onClick(Button button, EventObject e) {
                        currentPosition = (forward) ? currentPosition + pageSize : currentPosition - pageSize;
                        layout.clear();
                        g.destroy();
                        doGrid(source, cm, rd, pageSize);
                    }
                });
    }

    private RecordDef createRecordDef(TableConfig conf) {
        FieldDef[] fd = new FieldDef[conf.headers.length + 2]; //2 as we have format and UUID to tack on.
        fd[0] = new StringFieldDef("uuid");
        fd[1] = new StringFieldDef("format");
        for (int i = 0; i < conf.headers.length; i++) {
            fd[i + 2] = new StringFieldDef(conf.headers[i]);
        }
        return new RecordDef(fd);
    }

    private ColumnModel createColumnModel(TableConfig conf) {
        ColumnConfig[] cfgs = new ColumnConfig[conf.headers.length + 1];

        //first the UUID
        cfgs[0] = new ColumnConfig() {
            {
                setHidden(true);
                setDataIndex("uuid");
            }
        };


        //now the visible headers
        for (int i = 0; i < conf.headers.length; i++) {
            final String header = conf.headers[i];

            cfgs[i + 1] = new ColumnConfig() {
                    {
                        if (!header.equals("Description")) {
                            setHeader(header);
                            setSortable(true);
                            setDataIndex(header);
                            if (header.equals("Name")) { //name is special !
                                setWidth(220);
                                setRenderer(new Renderer() {
                                    public String render(Object value,
                                            CellMetadata cellMetadata, Record record,
                                            int rowIndex, int colNum, Store store) {
                                        String fmtIcon = "images/" + EditorLauncher.getAssetFormatIcon(record.getAsString("format"));
                                        return Format.format("<img src='{0}'/><b>{1}</b><br/><small>{2}</small>", new String[]{fmtIcon,
                                                (String) value,
                                                record.getAsString("Description")});
                                    }
                                });
                            }
                        } else {
                            setHidden(true); //don't want to show a separate description
                        }


                    }
                };
        }


        return new ColumnModel(cfgs);
    }



}