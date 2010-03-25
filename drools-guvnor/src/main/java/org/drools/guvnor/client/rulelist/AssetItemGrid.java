package org.drools.guvnor.client.rulelist;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Stack;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.client.rpc.TableDataRow;
import org.drools.guvnor.client.ruleeditor.EditorLauncher;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarItem;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;

/**
 * Uses an awesome grid and does paging for asset lists.
 * It works well, but here be dragons.
 * @author Michael Neale
 */
public class AssetItemGrid extends Composite {

    public static final String  RULE_LIST_TABLE_ID          = "rulelist";
    public static final String  PACKAGEVIEW_LIST_TABLE_ID   = "packageviewlist";
    public static final String  ARCHIVED_RULE_LIST_TABLE_ID = "archivedrulelist";
    private static final Map<String, ColumnModel>    columnConfigs               = new HashMap<String, ColumnModel>();
    private static final Map<String, RecordDef>    recordDefs                  = new HashMap<String, RecordDef>();
    private static final Map<String, Integer>    rowsPerPage                 = new HashMap<String, Integer>();

    private final EditItemEvent editEvent;
    private SimplePanel         layout;
    private Command             refresh;

    /**
     * Used for tracking paging.
     */
    private Stack<Integer>      cursorPositions             = getPositionStack();

    private int                 currentCursorPosition       = 0;

    protected Store             store;
    private GridPanel           currentGrid;
    private static Constants constants = GWT.create(Constants.class);
    private String feedURL;
    private Command unloadHook;


    /**
     * Call this to set up a table config instead of loading it from the server. You then pass in the config name for later use.
     * Can save a round trip.
     */
    public static void registerTableConf(TableConfig conf, String tableConfig) {
       if (columnConfigs.containsKey(tableConfig)) return;
       ColumnModel cm = createColumnModel( conf );
       columnConfigs.put( tableConfig, cm );
       RecordDef rd = createRecordDef( conf );
       recordDefs.put( tableConfig, rd );
       rowsPerPage.put( tableConfig, new Integer( conf.rowsPerPage ) );
    }


    /**
     * Create a grid using the given config - config will be loaded from the server if it is not already cached.
     * You can use registerTableConf to register it to avoid a server hit.
     */
    public AssetItemGrid(final EditItemEvent event,
                         final String tableConfig,
                         final AssetItemGridDataLoader source) {

        this.editEvent = event;
        this.layout = new SimplePanel();
        if ( !columnConfigs.containsKey( tableConfig ) ) {
            RepositoryServiceFactory.getService().loadTableConfig( tableConfig,
                                                                   new GenericCallback<TableConfig>() {
                                                                       public void onSuccess(TableConfig conf) {
                                                                           registerTableConf(conf, tableConfig);
                                                                           doGrid( source,
                                                                                   columnConfigs.get(tableConfig),
                                                                                   recordDefs.get(tableConfig),
                                                                                   conf.rowsPerPage );
                                                                       }
                                                                   } );
        } else {
            doGrid( source,
                    (ColumnModel) columnConfigs.get( tableConfig ),
                    (RecordDef) recordDefs.get( tableConfig ),
                    ((Integer) rowsPerPage.get( tableConfig )).intValue() );
        }

        initWidget( layout );
    }


    /**
     * Similar to the other constructor, but takes an optional feelURL to show with an atom icon in the top right.
     */
    public AssetItemGrid(final EditItemEvent event,
                     final String tableConfig,
                     final AssetItemGridDataLoader source,
                     String feedURL) {
        this(event, tableConfig, source);
        this.feedURL = feedURL;
    }
    

    private Stack<Integer> getPositionStack() {
        Stack<Integer> cursorPositions = new Stack<Integer>();
        cursorPositions.push( 0 );
        return cursorPositions;
    }

    /**
     * Actually build the grid.
     */
    private void doGrid(final AssetItemGridDataLoader source,
                        final ColumnModel cm,
                        final RecordDef rd,
                        final int pageSize) {
        final int numFlds = rd.getFields().length;

        final boolean[] loaded = {false};
        Timer t = new Timer() {
            public void run() {
                if (!loaded[0]) LoadingPopup.showMessage( constants.PleaseWait() );
            }
        };

        t.schedule(90);


        source.loadData( cursorPositions.peek(),
                         pageSize,
                         new GenericCallback<TableDataResult>() {
                             public void onSuccess(TableDataResult result) {
                                 Object[][] gridData = new Object[result.data.length][];
                                 for ( int i = 0; i < result.data.length; i++ ) {
                                     TableDataRow row = result.data[i];
                                     Object[] rowData = new Object[numFlds];
                                     rowData[0] = row.id;
                                     rowData[1] = row.format;
                                     for ( int j = 2; j < numFlds; j++ ) {
                                         if (rd.getFields()[j] instanceof DateFieldDef) {
                                            Date dt = new Date(Long.parseLong(row.values[j - 2]));
                                            //NOTE, GWTEXT only understands certain date formats, for example "yyyy/MM/dd"
                                            //works but other formats such as "yyyy/MM/dd" or localized formats  is not recognizable 
                                            //by GWTEXT. See http://code.google.com/p/gwt-ext/issues/detail?id=459&start=100.
                                            DateTimeFormat format = DateTimeFormat.getFormat( "yyyy/MM/dd");
                                             //DateTimeFormat format = DateTimeFormat.getFullDateFormat();
                                            rowData[j] = format.format(dt);
                                         } else {
                                            rowData[j] = row.values[j - 2];
                                         }
                                     }
                                     gridData[i] = rowData;
                                 }
                                 MemoryProxy proxy = new MemoryProxy( gridData );
                                 ArrayReader reader = new ArrayReader( rd );
                                 store = new Store( proxy,
                                                    reader );
                                 //currentGrid = new Grid(Ext.generateId(), "600px", "600px", store, cm);
                                 currentGrid = new GridPanel( store,
                                                              cm );
                                 currentGrid.setWidth( 600 );
                                 currentGrid.setHeight( 600 );

                                 Toolbar tb = new Toolbar();
                                 currentGrid.setTopToolbar( tb );
                                 if ( result.total > -1 ) {
                                     tb.addItem( new ToolbarTextItem( Format.format(constants.ShowingNofXItems().replace("X", "{0}").replace("Y", "{1}"),  //NON-NLS
                                                                                     new String[]{"" + result.data.length, "" + result.total} ) ) );
                                 } else {
                                     tb.addItem( new ToolbarTextItem( Format.format(constants.NItems().replace("X", "{0}"),
                                                                                     new String[]{"" + result.data.length} ) ) );

                                 }

                                 if ( cursorPositions.peek() > 0 ) {
                                     navButton( source,
                                                cm,
                                                rd,
                                                pageSize,
                                                currentGrid,
                                                false,
                                                tb );
                                 }
                                 if ( result.hasNext ) {
                                     navButton( source,
                                                cm,
                                                rd,
                                                pageSize,
                                                currentGrid,
                                                true,
                                                tb );
                                 }

                                 refresh = new Command() {
                                     public void execute() {
                                         layout.clear();
                                         currentGrid.destroy();
                                         doGrid( source,
                                                 cm,
                                                 rd,
                                                 pageSize );
                                     }
                                 };

                                 ToolbarButton refreshB = new ToolbarButton();
                                 refreshB.setText(constants.refreshList());
                                 refreshB.addListener( new ButtonListenerAdapter() {
                                     public void onClick(Button button,
                                                         EventObject e) {
                                         refresh.execute();
                                     }
                                 } );

                                 tb.addButton( refreshB );


                                 ToolbarButton openSelected = new ToolbarButton();
                                 openSelected.setText( constants.openSelected() );
                                 openSelected.addListener( new ButtonListenerAdapter() {
                                     public void onClick(Button button,
                                                         EventObject e) {
                                         Record[] selections = currentGrid.getSelectionModel().getSelections();
                                         for ( Record record : selections ) {
                                             String uuid = record.getAsString( "uuid" );
                                             editEvent.open( uuid );
                                         }
                                     }
                                 } );
                                 tb.addButton( openSelected );
                                 ToolbarButton openSelectedToSingleTab = new ToolbarButton();
                                 openSelectedToSingleTab.setText( constants.openSelectedToSingleTab() );
                                 openSelectedToSingleTab.addListener( new ButtonListenerAdapter() {
                                     public void onClick(Button button,
                                                         EventObject e) {
                                         Record[] selections = currentGrid.getSelectionModel().getSelections();
                                         MultiViewRow[] rows = new MultiViewRow[selections.length];
                                         for ( int i = 0; i < selections.length; i++ ) {
                                             MultiViewRow row = new MultiViewRow();
                                             row.uuid = selections[i].getAsString( "uuid" );
                                             row.name = selections[i].getAsString( "Name" );
                                             row.format = selections[i].getAsString( "format" );
                                             rows[i] = row;
                                         }
                                         editEvent.open( rows );
                                     }
                                 } );
                                 tb.addButton( openSelectedToSingleTab );


                                 if (feedURL != null) {
                                     tb.addFill();
                                     //System.err.println("Base: " + GWT.getModuleBaseURL());
                                     //System.err.println("URL: " + com.google.gwt.user.client.Window.Location.getHref());
                                     
                                     ToolbarItem item = new ToolbarItem(new HTML("<a href='" + feedURL + "' target='_blank'><img src='images/feed.png'/></a>").getElement());
                                     tb.addItem(item);
                                 }


                                 currentGrid.addGridRowListener( new GridRowListenerAdapter() {
                                     public void onRowDblClick(GridPanel grid,
                                                               int rowIndex,
                                                               EventObject e) {
                                         String uuid = grid.getSelectionModel().getSelected().getAsString( "uuid" );
                                         editEvent.open( uuid );
                                     }
                                 } );
                                 store.load();
                                 layout.add( currentGrid );
                                 //store the end position
                                 currentCursorPosition = (int) result.currentPosition;
                                 loaded[0] = true;
                                 LoadingPopup.close();
                             }

                         } );
    }

    public String getSelectedRowUUID() {
        Record r = currentGrid.getSelectionModel().getSelected();
        if ( r != null) {
            return r.getAsString("uuid");
        } else {
            return null;
        }
    }

    public String[] getSelectedRowUUIDs() {
        Record[] records = currentGrid.getSelectionModel().getSelections();
        if ( records != null && records.length !=0) {
            String[] rtn = new String[records.length];
            for(int i=0; i<records.length; i++) {
                rtn[i] = records[i].getAsString("uuid");
            }
            return rtn;
        } else {
            return null;
        }
    }
    
    private void navButton(final AssetItemGridDataLoader source,
                           final ColumnModel cm,
                           final RecordDef rd,
                           final int pageSize,
                           final GridPanel g,
                           final boolean forward,
                           Toolbar tb) {

        ToolbarButton b = new ToolbarButton();
        b.setText( (forward) ? constants.Next() : constants.Previous());

        tb.addButton( b );

        b.addListener( new ButtonListenerAdapter() {
            public void onClick(Button button,
                                EventObject e) {
                if ( forward ) {
                    int newPos = currentCursorPosition - 2;
                    if ( newPos > 0 ) {
                        cursorPositions.push( newPos );
                    }
                } else {
                    cursorPositions.pop();
                }
                layout.clear();
                g.destroy();
                doGrid( source,
                        cm,
                        rd,
                        pageSize );
            }
        } );

        if ( !forward ) {
            ToolbarButton first = new ToolbarButton(constants.goToFirst());
            tb.addButton( first );
            first.addListener( new ButtonListenerAdapter() {
                @Override
                public void onClick(Button button,
                                    EventObject e) {
                    cursorPositions.clear();
                    cursorPositions.push( 0 );
                    layout.clear();
                    g.destroy();
                    doGrid( source,
                            cm,
                            rd,
                            pageSize );
                }
            } );

        }
    }

    private static RecordDef createRecordDef(TableConfig conf) {
        FieldDef[] fd = new FieldDef[conf.headers.length + 2]; //2 as we have format and UUID to tack on.
        fd[0] = new StringFieldDef( "uuid" );                 //NON-NLS
        fd[1] = new StringFieldDef( "format" );              //NON-NLS
        for ( int i = 0; i < conf.headers.length; i++ ) {

            if ( conf.headerTypes[i].equals( "class java.util.Calendar" ) ) { //NON-NLS
                fd[i + 2] = new DateFieldDef( conf.headers[i] );
            } else {
                fd[i + 2] = new StringFieldDef( conf.headers[i] );
            }

        }
        return new RecordDef( fd );
    }

    private static ColumnModel createColumnModel(TableConfig conf) {
        ColumnConfig[] cfgs = new ColumnConfig[conf.headers.length + 1];

        //first the UUID
        cfgs[0] = new ColumnConfig() {
            {
                setHidden( true );
                setHeader( "uuid" );
                setDataIndex( "uuid" );   //NON-NLS
            }
        };

        //now the visible headers
        for ( int i = 0; i < conf.headers.length; i++ ) {
            final String header = conf.headers[i];
            final String headerType = conf.headerTypes[i];

            cfgs[i + 1] = new ColumnConfig() {
                {
                        setupHeader();
                        setSortable( true );
                        setDataIndex( header );
                        if ( header.equals( "Name" ) ) { //name is special !
                            setWidth( 220 );
                            setRenderer( new Renderer() {
                                public String render(Object value,
                                                     CellMetadata cellMetadata,
                                                     Record record,
                                                     int rowIndex,
                                                     int colNum,
                                                     Store store) {
                                    String fmtIcon = "images/" + EditorLauncher.getAssetFormatIcon( record.getAsString( "format" ) );
                                    String desc = record.getAsString( "Description" );
                                    if ( desc == null ) {
                                        desc = "";
                                    }
                                    return Format.format( "<img src='{0}'/>&nbsp;<b>{1}</b><br/><small>{2}</small>",
                                                          new String[]{fmtIcon, (String) value, desc} );
                                }
                            } );
                        } else if ( headerType.equals( "class java.util.Calendar" ) ) {
                            setRenderer( new Renderer() {
                                public String render(Object value,
                                                     CellMetadata cellMetadata,
                                                     Record record,
                                                     int rowIndex,
                                                     int colNum,
                                                     Store store) {
                                    DateTimeFormat format = DateTimeFormat.getMediumDateFormat();// DateTimeFormat.getFormat( "MMM d, yyyy");
                                    //System.out.println("----format.format( (Date) value  )" + format.format( (Date) value  ));
                                    return format.format( (Date) value  );
                                }
                            } );
                        } else if ( header.equals( "Description" ) ) {
                            setHidden( true ); //don't want to show a separate description
                        }
                    
                }

                /**
                 * This dirty hack is needed to cope with keys that may not be localised.
                 * ie not built in ones (eg a user adds a custom column).
                 */
                private void setupHeader() {
                    try {
                        String headerDisplay = constants.getString(header);
                        setHeader( headerDisplay );
                    } catch (MissingResourceException me) {
                        setHeader( header );
                    }
                }
            };
        }

        return new ColumnModel( cfgs );
    }

    public void refreshGrid() {
        this.refresh.execute();
    }

    /**
     * To be used when unloading.
     * @param command
     */
    public void addUnloadListener(Command command) {
        this.unloadHook = command;
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        if (unloadHook != null) unloadHook.execute();
    }
}