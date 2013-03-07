package org.kie.guvnor.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.guided.template.shared.TemplateModel;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.uberfire.backend.vfs.Path;

/**
 * Guided Rule Template Editor View definition
 */
public interface GuidedRuleTemplateEditorView extends IsWidget {

    void setContent( final Path path,
                     final TemplateModel model,
                     final DataModelOracle dataModel,
                     final EventBus eventBus,
                     final boolean isReadOnly );

    TemplateModel getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
