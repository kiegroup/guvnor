package org.kie.guvnor.guided.scorecard.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;

public interface GuidedScoreCardEditorView
        extends IsWidget {

    void setContent( final ScoreCardModel model,
                     final DataModelOracle oracle );

    ScoreCardModel getModel();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();
}
