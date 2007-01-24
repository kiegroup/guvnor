package org.drools.brms.client.ruleeditor;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
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

    private Image refresh;
    private FlexTable layout;
    private String uuid;
    private MetaData metaData;
    private Command refreshCommand;

    public VersionBrowser(String uuid, MetaData data, Command ref) {
        
        this.uuid = uuid;
        this.metaData = data;
        this.refreshCommand = ref;
        
        this.uuid = uuid;
        HorizontalPanel wrapper = new HorizontalPanel();
        
        layout = new FlexTable();
        layout.setWidget( 0, 0, new Label("Version history") );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setHorizontalAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        
        refresh = new Image("images/refresh.gif");
        
        refresh.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                clickLoadHistory();                
            }            
        });
        
        layout.setWidget( 0, 1, refresh );
        formatter.setHorizontalAlignment( 0, 1, HasHorizontalAlignment.ALIGN_RIGHT);

        
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
        });
        
    }

    private void showBusyIcon() {
        refresh.setUrl( "images/searching.gif" );
    }

    /**
     * Actually load the history data, as demanded.
     */
    protected void loadHistoryData() {
        
        RepositoryServiceFactory.getService().loadAssetHistory( this.uuid, new GenericCallback() {

            public void onSuccess(Object data) {
                if (data == null) {
                    layout.setWidget( 1, 0, new Label("No history.") );
                    showStaticIcon();
                    return;
                }
                TableDataResult table = (TableDataResult) data;                
                TableDataRow[] rows = table.data;
                
                String[] header = new String[] {"Version number", "Comment", "Date Modified", "Status"};
                
                final SortableTable tableWidget = populateTableData( rows,
                                                               header );
                
                tableWidget.setWidth( "100%" );
                
                layout.setWidget( 1, 0, tableWidget );
                FlexCellFormatter formatter = layout.getFlexCellFormatter();
                
                formatter.setColSpan( 1, 0, 2 );
                
                Button open = new Button("View selected version");
                
                open.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        showVersion(tableWidget.getSelectedKey());                        
                    }

                });
                
                layout.setWidget( 2, 1, open );
                formatter.setColSpan( 2, 1, 3 );
                formatter.setHorizontalAlignment( 2, 1, HasHorizontalAlignment.ALIGN_CENTER );
                
                showStaticIcon();

            }


        });
        
        
    }
    
    /**
     * This should popup a view of the chosen historical version.
     * @param selectedUUID
     */
    private void showVersion(String selectedUUID) {
        
        VersionViewer viewer = new VersionViewer(this.metaData, selectedUUID, uuid, refreshCommand);
        viewer.setPopupPosition( 100, 100 );
        
        
        viewer.show();
        
    }                    




    /**
     * This will return a sortable table ready to go.
     * @param rows The data.
     * @param header Headers.
     * @return
     */
    private SortableTable populateTableData(TableDataRow[] rows,
                                            String[] header) {
        SortableTable tableWidget = new SortableTable(rows.length + 1, rows[0].values.length + 1);
        
        tableWidget.setColumnHeader( "", 0 );
        
        for ( int i = 0; i < header.length; i++ ) {
            tableWidget.setColumnHeader( header[i], i + 1 );
        }
        
        
        tableWidget.setHiddenColumn( 0 );
        for ( int i = 0; i < rows.length; i++ ) {
            String[] cols = rows[i].values;
            
            tableWidget.setValue( i + 1, 0, rows[i].id );
            for ( int j = 0; j < cols.length; j++ ) {
                tableWidget.setValue( i + 1, j + 1, cols[j] );
            }
        }
        return tableWidget;
    }

    
    private void showStaticIcon() {
        refresh.setUrl( "images/refresh.gif" );
    }

    
}
