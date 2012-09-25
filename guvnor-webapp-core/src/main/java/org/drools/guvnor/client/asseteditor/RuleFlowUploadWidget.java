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

import com.google.gwt.user.client.ui.Image;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.resources.ImagesCore;
import org.drools.guvnor.client.rpc.Asset;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * For ruleflow upload.
 */
public class RuleFlowUploadWidget extends AssetAttachmentFileWidget {

    public RuleFlowUploadWidget(Asset asset,
                                RuleViewer viewer,
                                ClientFactory clientFactory,
                                EventBus eventBus) {
        super( asset,
                viewer,
                clientFactory,
                eventBus );
        super.addSupplementaryWidget( makeDescriptionWidget() );
    }

    private Widget makeDescriptionWidget() {
        return new HTML( "<small><i>" + ConstantsCore.INSTANCE.RuleFlowUploadTip() + "</i></small>" );
    }

    public Image getIcon() {
        Image image = new Image(ImagesCore.INSTANCE.ruleflowLarge());
        image.setAltText(ConstantsCore.INSTANCE.Ruleflow());
        return image;
    }

    public String getOverallStyleName() {
        return "decision-Table-upload"; //NON-NLS
    }

}
