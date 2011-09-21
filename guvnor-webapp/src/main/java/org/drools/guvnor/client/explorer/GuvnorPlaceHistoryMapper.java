package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.explorer.navigation.admin.ManagerPlace;
import org.drools.guvnor.client.explorer.navigation.browse.CategoryPlace;
import org.drools.guvnor.client.explorer.navigation.browse.InboxPlace;
import org.drools.guvnor.client.explorer.navigation.browse.StatePlace;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotAssetListPlace;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotPlace;
import org.drools.guvnor.client.explorer.navigation.processes.ProcessOverviewPlace;
import org.drools.guvnor.client.explorer.navigation.qa.TestScenarioListPlace;
import org.drools.guvnor.client.explorer.navigation.qa.VerifierPlace;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportTemplatesPlace;
import org.drools.guvnor.client.explorer.navigation.settings.PreferencesPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.GroupTasksPlace;
import org.drools.guvnor.client.explorer.navigation.tasks.PersonalTasksPlace;
import org.drools.guvnor.client.widgets.assetviewer.AssetViewerPlace;
import org.drools.guvnor.client.widgets.wizards.assets.NewAssetWizardContext;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers(
        {
                FindPlace.Tokenizer.class,
                AssetEditorPlace.Tokenizer.class,
                ModuleEditorPlace.Tokenizer.class,
                AssetViewerPlace.Tokenizer.class,
                ManagerPlace.Tokenizer.class,
                TestScenarioListPlace.Tokenizer.class,
                VerifierPlace.Tokenizer.class,
                SnapshotPlace.Tokenizer.class,
                SnapshotAssetListPlace.Tokenizer.class,
                CategoryPlace.Tokenizer.class,
                StatePlace.Tokenizer.class,
                InboxPlace.Tokenizer.class,
                MultiAssetPlace.Tokenizer.class,
                NewAssetWizardContext.Tokenizer.class,
                PersonalTasksPlace.Tokenizer.class,
                GroupTasksPlace.Tokenizer.class,
                ReportTemplatesPlace.Tokenizer.class,
                PreferencesPlace.Tokenizer.class,
                ProcessOverviewPlace.Tokenizer.class
        }
)
public interface GuvnorPlaceHistoryMapper extends PlaceHistoryMapper {
}
