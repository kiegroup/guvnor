package org.guvnor.m2repo.client.editor;

import java.util.Date;
import java.util.Set;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.guvnor.m2repo.client.resources.i18n.Constants;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.tables.AbstractPagedTable;
import org.uberfire.client.tables.ColumnPicker;
import org.uberfire.client.tables.SelectionColumn;
import org.uberfire.client.tables.SortableHeader;
import org.uberfire.client.tables.SortableHeaderGroup;
import org.uberfire.paging.PageRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.security.Identity;

import static org.guvnor.m2repo.security.AppRole.*;

public class PagedJarTable
        extends AbstractPagedTable<JarListPageRow> {

    interface Binder
            extends
            UiBinder<Widget, PagedJarTable> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private final Caller<M2RepoService> m2RepoService;
    private ColumnPicker<JarListPageRow> columnPicker = new ColumnPicker<JarListPageRow>( cellTable );

    private SelectionColumn<JarListPageRow> selectionColumn;
    private MultiSelectionModel<JarListPageRow> selectionModel;
    private static final int PAGE_SIZE = 10;

    @UiField()
    protected Button deleteSelectedJarButton;

    @UiField()
    protected Button refreshButton;

    private final Identity identity;

    public PagedJarTable( final Caller<M2RepoService> m2RepoService ) {
        this( m2RepoService, null );
    }

    public PagedJarTable( final Caller<M2RepoService> m2RepoService,
                          final String searchFilter ) {
        super( PAGE_SIZE );
        this.m2RepoService = m2RepoService;

        identity = IOC.getBeanManager().lookupBean( Identity.class ).getInstance();

        //If the current user is not an Administrator do not include the download button
        if ( identity.hasRole( ADMIN ) ) {
            Column<JarListPageRow, String> downloadColumn = new Column<JarListPageRow, String>(
                    new ButtonCell() ) {
                public String getValue( JarListPageRow row ) {
                    return "Download";
                }
            };

            downloadColumn
                    .setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
                        public void update( int index,
                                            JarListPageRow row,
                                            String value ) {
                            Window.open( getFileDownloadURL( row.getPath() ),
                                         "downloading",
                                         "resizable=no,scrollbars=yes,status=no" );
                        }
                    } );

            addColumn( downloadColumn, new TextHeader( "Download" ) );
        }

        setDataProvider( new AsyncDataProvider<JarListPageRow>() {
            protected void onRangeChanged( HasData<JarListPageRow> display ) {
                PageRequest request = new PageRequest( pager.getPageStart(), pageSize );

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
    }

    @Override
    protected void doCellTable() {

        ProvidesKey<JarListPageRow> providesKey = new ProvidesKey<JarListPageRow>() {
            public Object getKey( JarListPageRow row ) {
                return row.getPath();
            }
        };

        cellTable = new CellTable<JarListPageRow>( providesKey );
        selectionModel = new MultiSelectionModel<JarListPageRow>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        selectionColumn = SelectionColumn.createAndAddSelectionColumn( cellTable );

        columnPicker = new ColumnPicker<JarListPageRow>( cellTable );
        SortableHeaderGroup<JarListPageRow> sortableHeaderGroup = new SortableHeaderGroup<JarListPageRow>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    public void hideSelectionColumn() {
        cellTable.removeColumn( selectionColumn );
    }

    public void hideColumnPicker() {
        columnPickerButton.setVisible( false );
    }

    public void refresh() {
        selectionModel.clear();
        cellTable.setVisibleRangeAndClearData( cellTable.getVisibleRange(),
                                               true );
    }

    public String[] getSelectedJars() {
        Set<JarListPageRow> selectedRows = selectionModel.getSelectedSet();
        // Compatibility with existing API
        if ( selectedRows.size() == 0 ) {
            return null;
        }

        // Create the array of paths
        String[] paths = new String[ selectedRows.size() ];
        int rowCount = 0;
        for ( JarListPageRow row : selectedRows ) {
            paths[ rowCount++ ] = row.getPath();
        }
        return paths;
    }

    @Override
    protected void addAncillaryColumns( ColumnPicker<JarListPageRow> columnPicker,
                                        SortableHeaderGroup<JarListPageRow> sortableHeaderGroup ) {

        TextColumn<JarListPageRow> nameColumn = new TextColumn<JarListPageRow>() {
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

        TextColumn<JarListPageRow> pathColumn = new TextColumn<JarListPageRow>() {
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

        Column<JarListPageRow, Date> lastModifiedColumn = new Column<JarListPageRow, Date>( new DateCell( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM ) ) ) {
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
        Column<JarListPageRow, String> openColumn = new Column<JarListPageRow, String>( new ButtonCell() ) {
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

    public void addColumn( Column<JarListPageRow, String> column,
                           TextHeader textHeader ) {
        columnPicker.addColumn( column,
                                textHeader,
                                true );
    }

    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("deleteSelectedJarButton")
    void deleteSelectedJar( ClickEvent e ) {
        if ( getSelectedJars() == null ) {
            Window.alert( "Please Select A Jar To Delete" );
            return;
        }
        if ( !Window.confirm( Constants.INSTANCE.AreYouSureYouWantToDeleteTheseItems() ) ) {
            return;
        }
        m2RepoService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                Window.alert( "Deleted successfully" );
                refresh();
            }
        } ).deleteJar( getSelectedJars() );
    }

    @UiHandler("refreshButton")
    void refresh( ClickEvent e ) {
        refresh();
    }

/*    @UiHandler("auditButton")
    void viewAuditLog( ClickEvent e ) {
    }*/

    String getFileDownloadURL( String path ) {
        String url = getGuvnorM2RepoBaseURL() + path;
        return url;
    }

    public static String getGuvnorM2RepoBaseURL() {
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );
        return baseUrl + "maven2/";
    }

}
