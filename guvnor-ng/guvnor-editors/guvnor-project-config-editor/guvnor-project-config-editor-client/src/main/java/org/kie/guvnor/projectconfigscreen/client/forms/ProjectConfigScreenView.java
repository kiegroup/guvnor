package org.kie.guvnor.projectconfigscreen.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;

public interface ProjectConfigScreenView
        extends IsWidget {

    interface Presenter {

        void onShowMetadata();

    }

    void setPresenter( final Presenter presenter );

    void setMetadata( final Metadata metadata );

    Metadata getMetadata();

    void setImports( final Path path,
                     final Imports imports );

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
