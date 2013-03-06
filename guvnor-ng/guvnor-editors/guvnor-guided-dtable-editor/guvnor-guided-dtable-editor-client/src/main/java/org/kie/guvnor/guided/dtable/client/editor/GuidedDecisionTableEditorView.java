package org.kie.guvnor.guided.dtable.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.uberfire.backend.vfs.Path;

/**
 * Guided Decision Table Editor View definition
 */
public interface GuidedDecisionTableEditorView extends IsWidget {

    void setContent( final Path path,
                     final DataModelOracle dataModel,
                     final GuidedDecisionTable52 content,
                     final boolean isReadOnly );

    GuidedDecisionTable52 getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
