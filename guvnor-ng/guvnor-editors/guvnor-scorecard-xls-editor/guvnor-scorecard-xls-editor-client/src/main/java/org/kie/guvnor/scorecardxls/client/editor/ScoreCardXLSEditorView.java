package org.kie.guvnor.scorecardxls.client.editor;

import org.uberfire.backend.vfs.Path;

import com.google.gwt.user.client.ui.IsWidget;

public interface ScoreCardXLSEditorView
        extends IsWidget {
    void setPath(Path path);
    
    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void makeReadOnly();
}
