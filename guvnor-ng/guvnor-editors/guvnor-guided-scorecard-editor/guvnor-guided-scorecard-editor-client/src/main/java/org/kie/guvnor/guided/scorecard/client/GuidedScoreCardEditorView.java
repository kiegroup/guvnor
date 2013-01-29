package org.kie.guvnor.guided.scorecard.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.guided.scorecard.model.ScoreCardModel;

public interface GuidedScoreCardEditorView
        extends IsWidget {


    void setContent(final ScoreCardModel model,
                    final DataModelOracle oracle);

    ScoreCardModel getModel();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();
}
