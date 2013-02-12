package org.kie.guvnor.configresource.client.widget;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.uberfire.backend.vfs.Path;

public interface ImportsWidgetView
        extends IsWidget {

    interface Presenter {

        void onAddImport();

        void onRemoveImport();

        void setImports( final Path path,
                         final Imports resourceImports );

    }

    void addImport( String type );

    String getSelected();

    void removeImport( String selected );

    void setupReadOnly();

    void setPresenter( Presenter presenter );

    void showPleaseSelectAnImport();
}
