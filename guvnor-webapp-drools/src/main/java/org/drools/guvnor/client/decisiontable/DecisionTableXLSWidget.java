/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;

import org.drools.guvnor.client.asseteditor.AssetAttachmentFileWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RuleAsset;

/**
 * This widget deals with XLS files in "classic" decision tables.
 */
public class DecisionTableXLSWidget extends AssetAttachmentFileWidget {

    private static Images images = (Images) GWT.create( Images.class );

    public DecisionTableXLSWidget( RuleAsset asset,
                                   RuleViewer viewer,
                                   ClientFactory clientFactory,
                                   EventBus eventBus) {
        super( asset,
                viewer,
                clientFactory,
                eventBus);
        super.addDescription( new HTML( ((Constants) GWT.create( Constants.class )).DecisionTableWidgetDescription() ) );
    }

    public ImageResource getIcon() {
        return images.decisionTable();
    }

    public String getOverallStyleName() {
        return "decision-Table-upload"; //NON-NLS
    }

}
