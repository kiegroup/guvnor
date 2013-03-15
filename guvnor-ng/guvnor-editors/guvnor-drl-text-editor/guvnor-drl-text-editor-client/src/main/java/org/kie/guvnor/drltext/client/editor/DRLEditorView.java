package org.kie.guvnor.drltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.commons.ui.client.widget.HasBusyIndicator;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

public interface DRLEditorView extends HasBusyIndicator,
                                       IsWidget {

    void setContent( final String content,
                     final DataModelOracle dataModel );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
