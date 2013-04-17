package org.kie.guvnor.workitems.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.commons.ui.client.widget.HasBusyIndicator;

public interface WorkItemsEditorView extends HasBusyIndicator,
                                             IsWidget {

    void setContent( final String content );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
