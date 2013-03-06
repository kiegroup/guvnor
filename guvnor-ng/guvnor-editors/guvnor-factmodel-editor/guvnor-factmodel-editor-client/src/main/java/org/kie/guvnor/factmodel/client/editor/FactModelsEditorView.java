package org.kie.guvnor.factmodel.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.factmodel.model.FactMetaModel;
import org.kie.guvnor.factmodel.model.FactModels;

import java.util.List;

public interface FactModelsEditorView
        extends IsWidget {

    void setContent(final FactModels content,
                    final List<FactMetaModel> superTypeFactModels,
                    final ModelNameHelper modelNameHelper);

    FactModels getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
