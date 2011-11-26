package org.drools.guvnor.client.explorer.drools;


import org.drools.guvnor.client.explorer.GuvnorPlaceHistoryMapper;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotAssetListPlace;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotPlace;
import org.drools.guvnor.client.explorer.navigation.qa.TestScenarioListPlace;
import org.drools.guvnor.client.explorer.navigation.qa.VerifierPlace;
import org.drools.guvnor.client.widgets.drools.wizards.assets.NewGuidedDecisionTableAssetWizardContext;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers(
        {
                TestScenarioListPlace.Tokenizer.class,
                VerifierPlace.Tokenizer.class,
                SnapshotPlace.Tokenizer.class,
                SnapshotAssetListPlace.Tokenizer.class,
                NewGuidedDecisionTableAssetWizardContext.Tokenizer.class
        }
)
public interface GuvnorDroolsPlaceHistoryMapper extends GuvnorPlaceHistoryMapper {
}
