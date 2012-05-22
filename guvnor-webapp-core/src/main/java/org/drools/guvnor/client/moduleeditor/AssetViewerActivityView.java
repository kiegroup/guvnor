/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.moduleeditor;

import java.util.List;

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Module;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * What the Asset Viewer is capable of doing
 */
public interface AssetViewerActivityView
        extends
        IsWidget {

    interface Presenter {
    }

    void addAssetFormat(List<String> formatsInList,
                        Boolean formatIsRegistered,
                        String title,
                        ImageResource icon,
                        Module packageConfigData,
                        ClientFactory clientFactory);

    void showLoadingPackageInformationMessage();

    void closeLoadingPackageInformationMessage();

    void showHasNoAssetsWarning(boolean isVisible);

}
