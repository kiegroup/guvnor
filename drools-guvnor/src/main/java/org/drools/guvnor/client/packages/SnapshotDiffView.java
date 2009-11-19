package org.drools.guvnor.client.packages;

import org.drools.guvnor.client.explorer.ExplorerViewCenterPanel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.SnapshotDiff;
import org.drools.guvnor.client.rpc.SnapshotDiffs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;

public class SnapshotDiffView extends Composite {

    private static Constants        constants = GWT.create( Constants.class );
    private ExplorerViewCenterPanel centerPanel;

    private SimplePanel             layout    = new SimplePanel();
    private String                  leftHeader;
    private String                  rightHeader;

    public SnapshotDiffView(ExplorerViewCenterPanel center) {
        this.centerPanel = center;
        initWidget( layout );
    }

    private GridPanel doGrid(MemoryProxy proxy) {
        RecordDef recordDef = new RecordDef( new FieldDef[]{new StringFieldDef( "uuid" ), new StringFieldDef( "left" ), new StringFieldDef( "type" ), new StringFieldDef( "right" )} );

        ArrayReader reader = new ArrayReader( recordDef );
        Store store = new Store( proxy,
                                 reader );
        store.setDefaultSort( "left",
                              SortDir.DESC );
        store.load();

        ColumnModel cm = new ColumnModel( new ColumnConfig[]{new ColumnConfig() {
            {
                setHidden( true );
                setHeader( "uuid" );
                setSortable( true );
            }
        }, new ColumnConfig() {
            {
                setHeader( Format.format( constants.Older0(),
                                          leftHeader ) );
                setDataIndex( "left" ); //NON-NLS
                setSortable( true );
                setWidth( 200 );
            }
        }, new ColumnConfig() {
            {
                setHeader( constants.Type() );
                setSortable( true );
                setDataIndex( "type" );
                setWidth( 100 );
                setAlign( TextAlign.CENTER );
                setRenderer( new Renderer() {
                    public String render(Object value,
                                         CellMetadata cellMetadata,
                                         Record record,
                                         int rowIndex,
                                         int colNum,
                                         Store store) {
                        String type = (String) value;

                        if ( type.equals( SnapshotDiff.TYPE_ADDED ) ) {
                            cellMetadata.setHtmlAttribute( "style=\"background:blue;color:white;font-weight: bold;\"" );
                            type = constants.TypeAdded();
                        } else if ( type.equals( SnapshotDiff.TYPE_ARCHIVED ) ) {
                            cellMetadata.setHtmlAttribute( "style=\"background:purple;color:white;font-weight: bold;\"" );
                            type = constants.TypeArchived();
                        } else if ( type.equals( SnapshotDiff.TYPE_DELETED ) ) {
                            cellMetadata.setHtmlAttribute( "style=\"background:red;color:white;font-weight: bold;\"" );
                            type = constants.TypeDeleted();
                        } else if ( type.equals( SnapshotDiff.TYPE_RESTORED ) ) {
                            cellMetadata.setHtmlAttribute( "style=\"background:orange;color:white;font-weight: bold;\"" );
                            type = constants.TypeRestored();
                        } else if ( type.equals( SnapshotDiff.TYPE_UPDATED ) ) {
                            cellMetadata.setHtmlAttribute( "style=\"background:green;color:white;font-weight: bold;\"" );
                            type = constants.TypeUpdated();
                        }

                        return type;
                    }
                } );
            }
        }, new ColumnConfig() {
            {
                setHeader( Format.format( constants.Newer0(),
                                          rightHeader ) );
                setSortable( true );
                setDataIndex( "right" ); //NON-NLS
                setWidth( 200 );
            }
        }} );

        final GridPanel g = new GridPanel( store,
                                           cm );

        g.addGridRowListener( new GridRowListenerAdapter() {
            public void onRowDblClick(GridPanel grid,
                                      int rowIndex,
                                      EventObject e) {
                String uuid = grid.getSelectionModel().getSelected().getAsString( "uuid" ); //NON-NLS
                centerPanel.openAsset( uuid );
            }
        } );

        g.setWidth( 800 );
        g.setHeight( 600 );

        return g;
    }

    public void showDiffs(SnapshotDiffs snapshotDiffs) {

        SnapshotDiff[] diffs = snapshotDiffs.diffs;

        leftHeader = snapshotDiffs.leftName;
        rightHeader = snapshotDiffs.rightName;

        Object[][] data = new Object[diffs.length][4];
        for ( int i = 0; i < diffs.length; i++ ) {
            SnapshotDiff diff = diffs[i];
            data[i][0] = diff.rightUuid;
            data[i][1] = diff.name;
            data[i][2] = diff.diffType;
            data[i][3] = diff.name;
        }

        this.layout.clear();
        this.layout.add( doGrid( new MemoryProxy( data ) ) );
    }

}
