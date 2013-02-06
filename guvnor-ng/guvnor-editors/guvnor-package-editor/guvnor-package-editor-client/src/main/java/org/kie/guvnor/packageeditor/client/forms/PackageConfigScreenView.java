package org.kie.guvnor.packageeditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.services.config.model.imports.Imports;
import org.kie.guvnor.services.metadata.model.Metadata;

public interface PackageConfigScreenView
        extends IsWidget {

    interface Presenter {

        void onShowMetadata();

    }

    void setPresenter(Presenter presenter);

    void setMetadata(Metadata metadata);

    void setImports(Imports imports);
}
