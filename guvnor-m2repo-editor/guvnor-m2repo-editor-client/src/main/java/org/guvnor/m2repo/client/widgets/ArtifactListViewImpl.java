package org.guvnor.m2repo.client.widgets;

import java.util.Comparator;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.m2repo.client.editor.JarDetailEditor;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.common.BusyPopup;

public class ArtifactListViewImpl
        extends Composite
        implements ArtifactListView {

    private static final int PAGE_SIZE = 10;

    interface Binder
            extends
            UiBinder<Widget, ArtifactListViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    protected ArtifactListPresenter presenter;

    @Inject
    protected Caller<M2RepoService> m2RepoService;

    @UiField
    public DataGrid<JarListPageRow> cellTable;

    @UiField
    public SimplePager pager;

    @UiField
    public SimplePanel listContainer;

    protected ColumnSortEvent.ListHandler<JarListPageRow> sortHandler;

    protected String currentFilter = "";

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setContentHeight( String s ) {
        listContainer.setHeight( s );
    }

    public void init( final ArtifactListPresenter presenter ) {
        this.presenter = presenter;

        // Set the message to display when the table is empty.
        final Label emptyTable = new Label( "No artifacts available" );
        emptyTable.setStyleName( "" );
        cellTable.setEmptyTableWidget( emptyTable );

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ColumnSortEvent.ListHandler<JarListPageRow>( presenter.getDataProvider().getList() );
        cellTable.addColumnSortHandler( sortHandler );

        // Create a Pager to control the table.

        pager.setDisplay( cellTable );
        pager.setPageSize( 10 );

        initTableColumns();

        presenter.addDataDisplay( cellTable );
    }

    public void addColumn( final Column<JarListPageRow, ?> column,
                           final Comparator<JarListPageRow> comparator,
                           final String columnName ) {

        if ( comparator != null ) {
            sortHandler.setComparator( column, comparator );
            column.setSortable( true );
        }
        cellTable.addColumn( column, new ResizableHeader( columnName, cellTable, column ) );
    }

    private void initTableColumns() {
        {
            final Column<JarListPageRow, String> nameColumn = new Column<JarListPageRow, String>( new TextCell() ) {
                @Override
                public String getValue( JarListPageRow row ) {
                    return row.getName();
                }
            };
            nameColumn.setSortable( true );
            sortHandler.setComparator( nameColumn, new Comparator<JarListPageRow>() {
                @Override
                public int compare( final JarListPageRow o1,
                                    final JarListPageRow o2 ) {
                    return o1.getName().compareTo( o2.getName() );
                }
            } );
            cellTable.addColumn( nameColumn, new ResizableHeader( "Name", cellTable, nameColumn ) );
        }
        {
            final Column<JarListPageRow, String> pathColumn = new Column<JarListPageRow, String>( new TextCell() ) {
                @Override
                public String getValue( JarListPageRow row ) {
                    return row.getPath();
                }
            };
            pathColumn.setSortable( true );
            sortHandler.setComparator( pathColumn, new Comparator<JarListPageRow>() {
                @Override
                public int compare( final JarListPageRow o1,
                                    final JarListPageRow o2 ) {
                    return o1.getPath().compareTo( o2.getPath() );
                }
            } );
            cellTable.addColumn( pathColumn, new ResizableHeader( "Path", cellTable, pathColumn ) );
        }
        {
            final Column<JarListPageRow, Date> lastModifiedColumn = new Column<JarListPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
                @Override
                public Date getValue( JarListPageRow row ) {
                    return row.getLastModified();
                }
            };
            lastModifiedColumn.setSortable( true );
            sortHandler.setComparator( lastModifiedColumn, new Comparator<JarListPageRow>() {
                @Override
                public int compare( final JarListPageRow o1,
                                    final JarListPageRow o2 ) {
                    return o1.getLastModified().compareTo( o2.getLastModified() );
                }
            } );
            cellTable.addColumn( lastModifiedColumn, new ResizableHeader( "LastModified", cellTable, lastModifiedColumn ) );
        }
        {
            // Add "View kjar detail" button column
            final Column<JarListPageRow, String> openColumn = new Column<JarListPageRow, String>( new ButtonCell() {{
                setSize( ButtonSize.MINI );
            }} ) {
                public String getValue( JarListPageRow row ) {
                    return "Open";
                }
            };
            openColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
                public void update( int index,
                                    JarListPageRow row,
                                    String value ) {
                    m2RepoService.call( new RemoteCallback<String>() {
                        @Override
                        public void callback( final String response ) {
                            JarDetailEditor editor = new JarDetailEditor( response );
                            editor.setSize( "800px", "600px" );
                            editor.show();
                        }
                    } ).loadPOMStringFromJar( row.getPath() );
                }
            } );
            cellTable.addColumn( openColumn, new ResizableHeader( "Open", cellTable, openColumn ) );
        }
    }

    @Override
    public void showBusyIndicator( String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public String getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public void setCurrentFilter( final String currentFilter ) {
        this.currentFilter = currentFilter;
    }

    @Override
    public int getPageStart() {
        return pager.getPageStart();
    }

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }
}
