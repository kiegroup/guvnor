package org.kie.guvnor.dtablexls.client.editor;

import com.google.gwt.user.client.ui.IsWidget;

public interface DecisionTableXLSEditorView
        extends IsWidget {
    
    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void makeReadOnly();
}
