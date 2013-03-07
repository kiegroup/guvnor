package org.kie.guvnor.drltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

public interface DRLEditorView
        extends IsWidget {

    void setContent( final String content,
                     final DataModelOracle dataModel );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
