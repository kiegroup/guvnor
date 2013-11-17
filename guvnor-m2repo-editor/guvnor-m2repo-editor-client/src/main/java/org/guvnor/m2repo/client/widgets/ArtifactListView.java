package org.guvnor.m2repo.client.widgets;

import java.util.Comparator;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.m2repo.model.JarListPageRow;

public interface ArtifactListView extends IsWidget {

    void showBusyIndicator( String message );

    void hideBusyIndicator();

    String getCurrentFilter();

    void setCurrentFilter( String filter );

    int getPageStart();

    int getPageSize();

    void setContentHeight( String s );

    void addColumn( final Column<JarListPageRow, ?> column,
                    final Comparator<JarListPageRow> comparable,
                    final String name );
}
