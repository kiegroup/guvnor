package org.drools.brms.client.rulelist;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
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

/**
 * This is a viewer for viewing a list of rules for editing/selection.
 * This uses the sortable table widget, and can be re-used with different data sets.
 * (ie no need to throw is away). 
 */
public class AssetItemListViewer extends Composite {

    /** The number of rows to "fill out" */
    private static final int FILLER_ROWS = 100;
    public static final String RULE_LIST_TABLE_ID = "ruleList";
    
    private FlexTable     outer = new FlexTable();
    private SortableTable table;
    private static TableConfig   tableConfig;
    private EditItemEvent openItemEvent;

    private Image refreshIcon = new Image("images/refresh.gif");
    private Command refresh;
    private static RepositoryServiceAsync service = RepositoryServiceFactory.getService();
    private Label itemCounter = new Label();
    
    
    public AssetItemListViewer(EditItemEvent event) {

        init();
        
        if (tableConfig == null) {
            loadTableConfig();
        }
        this.refreshIcon.setVisible( false );
        this.openItemEvent = event;
        this.refreshIcon.setTitle( "Refresh current list. Will show any changes." );
        this.refreshIcon.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                refresh.execute();
            }            
        });
        
    }

    /**
     * Optionally set the refresh command to repopulate the list on demand.
     * @param refreshCom
     */
    public void setRefreshCommand(Command refreshCom) {
        this.refresh = refreshCom;
        this.refreshIcon.setVisible( true );
        
    }
    
    private void loadTableConfig() {
        service.loadTableConfig( "ruleList",
                                                               new GenericCallback() {
                                                                   public void onSuccess(Object o) {
                                                                       tableConfig = (TableConfig) o;
                                                                       loadTableData( null );
                                                                   }
                                                               } );
    }

    /**
     * Initialise the widget goodness.
     */
    private void init() {
        FlexCellFormatter formatter = outer.getFlexCellFormatter();

        //outer.setStyleName( SortableTable.styleList );
        outer.setWidth( "100%" );

        formatter.setAlignment( 0,
                                0,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_MIDDLE );

        Image openIcon = new Image( "images/open_item.gif" );
        openIcon.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                LoadingPopup.showMessage( "Loading item, please wait ..." );
                openItemEvent.open( TableDataRow.getId( table.getSelectedKey()));
                
            }
        } );
        openIcon.setTitle( "Open item" );

        outer.setWidget( 0,
                         1,
                         openIcon );
        formatter.setAlignment( 0,
                                1,
                                HasHorizontalAlignment.ALIGN_RIGHT,
                                HasVerticalAlignment.ALIGN_MIDDLE );

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
        if ( data == null || data.data.length == 0) {
            
            table = SortableTable.createTableWidget( new TableDataRow[0], tableConfig.headers, FILLER_ROWS );
            itemCounter.setVisible( false );
        } else {
            table = SortableTable.createTableWidget( data.data, this.tableConfig.headers, FILLER_ROWS );

            
            HorizontalPanel panel = new HorizontalPanel();
            panel.add( refreshIcon );
            itemCounter.setVisible( true );
            itemCounter.setText( "  " + data.data.length + " items." );
            panel.add( itemCounter );
            
            outer.setWidget( 0, 0, panel);
        }

        table.setWidth( "100%" );
        outer.setWidget( 1, 0, table );
        formatter.setColSpan( 1, 0, 2 );

    }
    

//    /**
//     * This is called to tell the widget to reload itself for the given cat path.
//     */
//    public void loadRulesForCategoryPath(final String selectedPath) {
//
//        //this.currentSelectedPath = selectedPath;
//        
//
//        
//        service .loadRuleListForCategories( selectedPath,
//                                                                         new AsyncCallback() {
//
//                                                                             public void onFailure(Throwable t) {
//                                                                                 ErrorPopup.showMessage( t.getMessage() );
//                                                                             }
//
//                                                                             public void onSuccess(Object o) {
//                                                                                 TableDataResult result = (TableDataResult) o;
//                                                                                 loadTableData( result );                                                                                 
//                                                                             }
//
//                                                                         } );
//
//        
//    }


    
}
