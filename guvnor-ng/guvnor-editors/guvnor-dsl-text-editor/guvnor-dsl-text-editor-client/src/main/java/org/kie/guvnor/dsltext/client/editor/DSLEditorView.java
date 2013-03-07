package org.kie.guvnor.dsltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;

public interface DSLEditorView
        extends IsWidget {

    void setContent( final String content );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void makeReadOnly();

    void alertReadOnly();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
