package org.kie.guvnor.enums.client.editor;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Enum Editor View definition
 */
public interface EnumEditorView extends IsWidget {

    void setContent( String content );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
