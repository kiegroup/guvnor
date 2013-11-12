package org.guvnor.m2repo.client.editor;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import org.guvnor.m2repo.client.widgets.AbstractPagedJarTable;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.tables.ColumnPicker;
import org.uberfire.client.tables.SortableHeaderGroup;

import static org.guvnor.m2repo.security.AppRole.*;

public class MavenRepositoryPagedJarTable
        extends AbstractPagedJarTable {

    public MavenRepositoryPagedJarTable( final Caller<M2RepoService> m2RepoService ) {
        super( m2RepoService );
    }

    public MavenRepositoryPagedJarTable( final Caller<M2RepoService> m2RepoService,
                                         final String searchFilter ) {
        super( m2RepoService,
               searchFilter );
    }

    @Override
    protected void addAncillaryColumns( final ColumnPicker<JarListPageRow> columnPicker,
                                        final SortableHeaderGroup<JarListPageRow> sortableHeaderGroup ) {
        super.addAncillaryColumns( columnPicker,
                                   sortableHeaderGroup );

        //If the current user is not an Administrator do not include the download button
        if ( identity.hasRole( ADMIN ) ) {
            final Column<JarListPageRow, String> downloadColumn = new Column<JarListPageRow, String>( new ButtonCell() ) {
                public String getValue( JarListPageRow row ) {
                    return "Download";
                }
            };

            downloadColumn.setFieldUpdater( new FieldUpdater<JarListPageRow, String>() {
                public void update( int index,
                                    JarListPageRow row,
                                    String value ) {
                    Window.open( getFileDownloadURL( row.getPath() ),
                                 "downloading",
                                 "resizable=no,scrollbars=yes,status=no" );
                }
            } );

            addColumn( downloadColumn,
                       new TextHeader( "Download" ) );
        }

    }

    private String getFileDownloadURL( final String path ) {
        String url = getGuvnorM2RepoBaseURL() + path;
        return url;
    }

    private String getGuvnorM2RepoBaseURL() {
        final String url = GWT.getModuleBaseURL();
        final String baseUrl = url.replace( GWT.getModuleName() + "/", "" );
        return baseUrl + "maven2/";
    }

}
