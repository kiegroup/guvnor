package org.drools.brms.client.rulelist;

import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This is a viewer for viewing a list of rules for editing/selection.
 */
public class NewView extends Composite {

    private FlexTable     outer = new FlexTable();
    private SortableTable table;

    public NewView(TableConfig conf) {

        RepositoryServiceFactory.getService().loadTableConfig( "ruleList",
                                                               new AsyncCallback() {

                                                                   public void onFailure(Throwable w) {
                                                                       ErrorPopup.showMessage( w.getMessage() );
                                                                   }

                                                                   public void onSuccess(Object o) {
                                                                       configTable( (TableConfig) o );
                                                                   }

                                                               } );

        init();

    }

    private void init() {
        FlexCellFormatter formatter = outer.getFlexCellFormatter();

        //outer.setStyleName( SortableTable.styleList );
        outer.setWidth( "100%" );

        outer.setWidget( 0,
                         0,
                         new Label( "left" ) );
        formatter.setAlignment( 0,
                                0,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_MIDDLE );

        outer.setWidget( 0,
                         1,
                         new Label( "right" ) );
        formatter.setAlignment( 0,
                                1,
                                HasHorizontalAlignment.ALIGN_RIGHT,
                                HasVerticalAlignment.ALIGN_MIDDLE );

        initWidget( outer );
    }

    private void configTable(TableConfig conf) {
        FlexCellFormatter formatter = outer.getFlexCellFormatter();
        table = new SortableTable( 50,
                                   conf.headers.length + 1 );
        table.setHiddenColumn( 0 );
        table.addColumnHeader( "",
                               0 );
        table.setWidth( "100%" );

        for ( int i = 0; i < conf.headers.length; i++ ) {
            table.addColumnHeader( conf.headers[i],
                                   i + 1  );
        }

        ScrollPanel scroll = new ScrollPanel();

        scroll.setScrollPosition( 1000 );
        scroll.setAlwaysShowScrollBars( true );
        scroll.add( table );

        outer.setWidget( 1,
                         0,
                         scroll );
        formatter.setColSpan( 1,
                              0,
                              2 );
        for (int i = 1; i < 40; i++) {
            table.setValue( i, 0, "KEY" );
            table.setValue( i, 1, "yeah1" );
            table.setValue( i, 2, "yeah2" );
            table.setValue( i, 3, "yeah3" );
            table.setValue( i, 4, "yeah4" );
            
            
        }
    }

}
