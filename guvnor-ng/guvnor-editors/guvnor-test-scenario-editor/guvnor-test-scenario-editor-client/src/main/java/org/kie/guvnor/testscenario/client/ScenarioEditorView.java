package org.kie.guvnor.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.drools.guvnor.models.testscenarios.shared.Scenario;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.kie.guvnor.testscenario.service.ScenarioTestEditorService;
import org.uberfire.backend.vfs.Path;

public interface ScenarioEditorView
        extends IsWidget {

    void showCanNotSaveReadOnly();

    void showBusyIndicator();

    void renderEditor();

    void addTestRunnerWidget(Scenario scenario, Caller<ScenarioTestEditorService> testScenarioEditorService, Path path);

    void addMetaDataPage(Path path, final boolean isReadOnly);

    void setScenario(String packageName, Scenario scenario, DataModelOracle dmo);

    void showSaveSuccessful();

    String getTitle();

    void initImportsTab(DataModelOracle dmo, Imports imports, boolean readOnly);

    Metadata getMetadata();

    void resetMetadataDirty();
}
