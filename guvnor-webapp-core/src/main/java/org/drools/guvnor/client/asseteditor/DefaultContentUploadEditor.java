/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.asseteditor;

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class DefaultContentUploadEditor extends AssetAttachmentFileWidget {

    public DefaultContentUploadEditor( Asset asset,
                                       RuleViewer viewer,
                                       ClientFactory clientFactory,
                                       EventBus eventBus) {
        super( asset,
                viewer,
                clientFactory,
                eventBus);
        super.addSupplementaryWidget( makeDescriptionWidget() );
    }

    private Widget makeDescriptionWidget() {
        return new HTML( constants.UploadNewVersionDescription() );
    }

    public ImageResource getIcon() {
        return images.decisionTable();
    }

    public String getOverallStyleName() {
        return "decision-Table-upload"; //NON-NLS
    }

}
