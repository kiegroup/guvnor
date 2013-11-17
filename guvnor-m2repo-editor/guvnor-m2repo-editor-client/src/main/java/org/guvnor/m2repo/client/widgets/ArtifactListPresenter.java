package org.guvnor.m2repo.client.widgets;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.m2repo.model.JarListPageRow;

public interface ArtifactListPresenter {

    ListDataProvider<JarListPageRow> getDataProvider();

    void addDataDisplay( HasData<JarListPageRow> display );

    ArtifactListView getView();

    void search( String filter );

    void refresh();
}
