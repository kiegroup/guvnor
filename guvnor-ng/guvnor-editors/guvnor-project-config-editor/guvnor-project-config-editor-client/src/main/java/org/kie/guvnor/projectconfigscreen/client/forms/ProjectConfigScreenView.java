package org.kie.guvnor.projectconfigscreen.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.kie.guvnor.commons.service.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;

public interface ProjectConfigScreenView
        extends IsWidget {

    interface Presenter {

        void onShowMetadata();

    }

    void setPresenter(Presenter presenter);

    void setMetadata(Metadata metadata);

    void setImports(Path path, Imports imports);
}
