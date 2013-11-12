package org.guvnor.m2repo.client.widgets;

import java.util.Date;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import org.guvnor.m2repo.client.editor.JarDetailEditor;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.tables.AbstractPagedTable;
import org.uberfire.client.tables.ColumnPicker;
import org.uberfire.client.tables.SortableHeader;
import org.uberfire.client.tables.SortableHeaderGroup;
import org.uberfire.paging.PageRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.security.Identity;

public abstract class AbstractPagedJarTable
        extends AbstractPagedTable<JarListPageRow> {

    private static final int PAGE_SIZE = 10;

    interface Binder
            extends
            UiBinder<Widget, AbstractPagedJarTable> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    protected Caller<M2RepoService> m2RepoService;

    protected ColumnPicker<JarListPageRow> columnPicker;

    @UiField()
    protected Button refreshButton;

    protected Identity identity;

    public AbstractPagedJarTable( final Caller<M2RepoService> m2RepoService ) {
        this( m2RepoService,
              null );
    }

    public AbstractPagedJarTable( final Caller<M2RepoService> m2RepoService,
                                  final String searchFilter ) {
        super( PAGE_SIZE );
        this.m2RepoService = m2RepoService;
        this.identity = IOC.getBeanManager().lookupBean( Identity.class ).getInstance();

        setDataProvider( new AsyncDataProvider<JarListPageRow>() {
            protected void onRangeChanged( HasData<JarListPageRow> display ) {
                PageRequest request = new PageRequest( pager.getPageStart(),
                                                       pageSize );

                m2RepoService.call( new RemoteCallback<PageResponse<JarListPageRow>>() {
                    @Override
                    public void callback( final PageResponse<JarListPageRow> response ) {
                        updateRowCount( response.getTotalRowSize(),
                                        response.isTotalRowSizeExact() );
                        updateRowData( response.getStartRowIndex(),
                                       response.getPageRowList() );
                    }
                } ).listJars( request, searchFilter );
            }
        } );

        // Add any additional columns
        SortableHeaderGroup<JarListPageRow> sortableHeaderGroup = new SortableHeaderGroup<JarListPageRow>( cellTable );
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );
    }

    @Override
    protected void doCellTable() {
        ProvidesKey<JarListPageRow> providesKey = new ProvidesKey<JarListPageRow>() {
            public Object getKey( JarListPageRow row ) {
                return row.getPath();
            }
        };

        cellTable = new CellTable<JarListPageRow>( providesKey );
        columnPicker = new ColumnPicker<JarListPageRow>( cellTable );
        columnPickerButton = columnPicker.createToggleButton();
        columnPickerButton.setVisible( false );
        columnPickerButton.setEnabled( false );
        cellTable.setWidth( "100%" );
    }

    @Override
    protected void addAncillaryColumns( final ColumnPicker<JarListPageRow> columnPicker,
                                        final SortableHeaderGroup<JarListPageRow> sortableHeaderGroup ) {

        final TextColumn<JarListPageRow> nameColumn = new TextColumn<JarListPageRow>() {
            public String getValue( JarListPageRow row ) {
                return row.getName();
            }
        };
        columnPicker.addColumn( nameColumn,
                                new SortableHeader<JarListPageRow, String>(
                                        sortableHeaderGroup,
                                        "Name",
                                        nameColumn ),
                                true );

        final TextColumn<JarListPageRow> pathColumn = new TextColumn<JarListPageRow>() {
            public String getValue( JarListPageRow row ) {
                return row.getPath();
            }
        };
        columnPicker.addColumn( pathColumn,
                                new SortableHeader<JarListPageRow, String>(
                                        sortableHeaderGroup,
                                        "Path",
                                        pathColumn ),
                                true );

        final Column<JarListPageRow, Date> lastModifiedColumn = new Column<JarListPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
            public Date getValue( JarListPageRow row ) {
                return row.getLastModified();
            }
        };
        columnPicker.addColumn( lastModifiedColumn,
                                new SortableHeader<JarListPageRow, Date>( sortableHeaderGroup,
                                                                          "LastModified",
                                                                          lastModifiedColumn ),
                                true );

        // Add "View kjar detail" button column
        final Column<JarListPageRow, String> openColumn = new Column<JarListPageRow, String>( new ButtonCell() ) {
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
        columnPicker.addColumn( openColumn,
                                new TextHeader( "View Artifact Detail" ),
                                true );
    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    public void addColumn( final Column<JarListPageRow, String> column,
                           final TextHeader textHeader ) {
        columnPicker.addColumn( column,
                                textHeader,
                                true );
    }

    @UiHandler("refreshButton")
    void refresh( ClickEvent e ) {
        refresh();
    }

}
