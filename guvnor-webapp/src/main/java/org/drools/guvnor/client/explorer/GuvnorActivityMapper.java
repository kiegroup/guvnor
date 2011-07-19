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

package org.drools.guvnor.client.explorer;

import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.explorer.navigation.ModuleFormatsGrid;
import org.drools.guvnor.client.util.Activity;
import org.drools.guvnor.client.util.ActivityMapper;
import org.drools.guvnor.client.widgets.assetviewer.AssetViewerActivity;
import org.drools.guvnor.client.widgets.assetviewer.AssetViewerPlace;

public class GuvnorActivityMapper
    implements
    ActivityMapper {
    private ClientFactory clientFactory;

    public GuvnorActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    public Activity getActivity(Place place) {
        if ( place instanceof FindPlace ) {
            return new FindActivity();
        } else if ( place instanceof AssetEditorPlace ) {
            return new AssetEditorActivity();
        } else if ( place instanceof ModuleEditorPlace ) {
            return new ModuleEditorActivity( ((ModuleEditorPlace) place).getUuid(),
                                             clientFactory );
        } else if ( place instanceof AssetViewerPlace ) {
            return new AssetViewerActivity( ((AssetViewerPlace) place).getUuid(),
                                            clientFactory );
        } else if ( place instanceof ModuleFormatsGrid ) {
            return new ModuleFormatsGridPlace( (ModuleFormatsGrid) place );
        } else {
            return null;
        }
    }
}
