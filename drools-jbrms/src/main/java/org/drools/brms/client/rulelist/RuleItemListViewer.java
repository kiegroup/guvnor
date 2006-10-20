package org.drools.brms.client.rulelist;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
 * This uses the sortable table widget, and 
 */
public class RuleItemListViewer extends Composite {

    private static final int DEFAULT_ROWS = 100;
    private FlexTable     outer = new FlexTable();
    private SortableTable table;
    private TableConfig   tableConfig;
    private EditItemEvent openItemEvent;
    private String currentSelectedPath;
    private Image refreshIcon;
    private static RepositoryServiceAsync service = RepositoryServiceFactory.getService();

    public RuleItemListViewer(EditItemEvent event) {

        init();

        service.loadTableConfig( "ruleList",
                                                               new AsyncCallback() {

                                                                   public void onFailure(Throwable w) {
                                                                       ErrorPopup.showMessage( w.getMessage() );
                                                                   }

                                                                   public void onSuccess(Object o) {
                                                                       tableConfig = (TableConfig) o;
                                                                       doTable( null );
                                                                   }

                                                               } );

        this.openItemEvent = event;

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
                openItemEvent.open( table.getSelectedKey() );
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
     * 
     */
    private void doTable(TableDataResult data) {
        FlexCellFormatter formatter = outer.getFlexCellFormatter();
        outer.setWidget( 1,
                         0,
                         null );

        //if no data, just fill it out
        if ( data == null || data.data.length == 0) {
            table = new SortableTable( 100,
                                       this.tableConfig.headers.length + 1 );
            table.setValue( 1,
                            1,
                            "" );
        } else {
            int maxRows = data.numberOfRows;
            if (data.numberOfRows < DEFAULT_ROWS) {
                maxRows = 100;
            }
            table = new SortableTable( maxRows,
                                       this.tableConfig.headers.length + 1 );
            for ( int i = 0; i < data.data.length; i++ ) {
                TableDataRow row = data.data[i];
                table.setValue( i + 1,
                                0,
                                row.key ); //this is the key
                for ( int j = 0; j < row.values.length; j++ ) {
                    String val = row.values[j];
                    table.setValue( i + 1,
                                    j + 1,
                                    val );
                }
            }
            
            //now the refresh icon and the number of rows.
            refreshIcon = new Image("images/refresh.gif");
            refreshIcon.addClickListener( new ClickListener() {
                public void onClick(Widget w) {                    
                    loadRulesForCategoryPath( currentSelectedPath );
                }                
            });
            refreshIcon.setTitle( "Refresh current list. Will show any changes." );
            
            
            HorizontalPanel panel = new HorizontalPanel();
            panel.add( refreshIcon );
            panel.add( new Label( "  " + data.numberOfRows + " items." ));
            
            outer.setWidget( 0,
                             0,
                              panel);
        }

        //setup the "key" column
        table.setHiddenColumn( 0 );
        table.addColumnHeader( "",
                               0 );
        table.setWidth( "100%" );

        //add the headers
        for ( int i = 0; i < this.tableConfig.headers.length; i++ ) {
            table.addColumnHeader( this.tableConfig.headers[i],
                                   i + 1 );
        }

        outer.setWidget( 1,
                         0,
                         table );
        formatter.setColSpan( 1,
                              0,
                              2 );

    }

    /**
     * This is called to tell the widget to reload itself for the given cat path.
     */
    public void loadRulesForCategoryPath(final String selectedPath) {

        this.currentSelectedPath = selectedPath;
        

        
        service .loadRuleListForCategories( selectedPath,
                                                                         "",
                                                                         new AsyncCallback() {

                                                                             public void onFailure(Throwable t) {
                                                                                 ErrorPopup.showMessage( t.getMessage() );
                                                                             }

                                                                             public void onSuccess(Object o) {
                                                                                 TableDataResult result = (TableDataResult) o;
                                                                                 doTable( result );                                                                                 
                                                                             }

                                                                         } );

        
    }


    
}
