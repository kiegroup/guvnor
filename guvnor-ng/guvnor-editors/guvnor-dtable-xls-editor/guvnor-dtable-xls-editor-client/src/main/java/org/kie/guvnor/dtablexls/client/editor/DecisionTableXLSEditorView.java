package org.kie.guvnor.dtablexls.client.editor;

import org.uberfire.backend.vfs.Path;

import com.google.gwt.user.client.ui.IsWidget;

public interface DecisionTableXLSEditorView
        extends IsWidget {
    void setPath(Path path);
    
    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void makeReadOnly();
}
