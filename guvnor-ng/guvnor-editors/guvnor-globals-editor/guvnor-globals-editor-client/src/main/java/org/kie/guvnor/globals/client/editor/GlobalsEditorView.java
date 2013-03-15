package org.kie.guvnor.globals.client.editor;

import java.util.List;

import org.kie.guvnor.commons.ui.client.widget.HasBusyIndicator;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.globals.model.Global;
import org.uberfire.client.mvp.UberView;

/**
 * Globals Editor View definition
 */
public interface GlobalsEditorView extends HasBusyIndicator,
                                           UberView<GlobalsEditorPresenter> {

    void setContent( final DataModelOracle oracle,
                     final List<Global> globals,
                     final boolean isReadOnly );

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
