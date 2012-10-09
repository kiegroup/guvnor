/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer.drools;

import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.GuvnorActivityMapper;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotActivity;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotAssetListActivity;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotAssetListPlace;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotPlace;
import org.drools.guvnor.client.explorer.navigation.qa.TestScenarioListActivity;
import org.drools.guvnor.client.explorer.navigation.qa.TestScenarioListPlace;
import org.drools.guvnor.client.explorer.navigation.qa.VerifierActivity;
import org.drools.guvnor.client.explorer.navigation.qa.VerifierPlace;
import org.drools.guvnor.client.util.Activity;

public class GuvnorDroolsActivityMapper extends GuvnorActivityMapper {

    public GuvnorDroolsActivityMapper(ClientFactory clientFactory) {
        super(clientFactory);
    }

    public Activity getActivity(Place place) {
        Activity activity = tryParent(place);

        if (activity == null) {
            activity = tryDroolsGuvnor(place);
        }

        return activity;
    }

    private Activity tryDroolsGuvnor(Place place) {
        if (place instanceof VerifierPlace) {
            return new VerifierActivity(
                    ((VerifierPlace) place).getModuleUuid(),
                    clientFactory);
        } else if (place instanceof SnapshotPlace) {
            return new SnapshotActivity(
                    ((SnapshotPlace) place).getModuleName(),
                    ((SnapshotPlace) place).getSnapshotName(),
                    clientFactory);
        } else if (place instanceof SnapshotAssetListPlace) {
            return new SnapshotAssetListActivity(
                    (SnapshotAssetListPlace) place,
                    clientFactory);
        } else {
            return null;
        }
    }

    private Activity tryParent(Place place) {
        return super.getActivity(place);
    }
}
