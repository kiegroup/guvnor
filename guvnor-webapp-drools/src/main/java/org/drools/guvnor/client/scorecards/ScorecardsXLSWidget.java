/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.scorecards;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.asseteditor.AssetAttachmentFileWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.guvnor.client.rpc.Asset;


/**
 * This widget deals with XLS files in scorecards.
 */
public class ScorecardsXLSWidget extends AssetAttachmentFileWidget {

    public ScorecardsXLSWidget(Asset asset,
                               RuleViewer viewer,
                               ClientFactory clientFactory,
                               EventBus eventBus) {
        super(asset,
                viewer,
                clientFactory,
                eventBus);

        //Set-up supplementary widgets
        super.addSupplementaryWidget(makeDescriptionWidget());
    }

    private Widget makeDescriptionWidget() {
        return new HTML(Constants.INSTANCE.ScorecardWidgetDescription());
    }

    public Image getIcon() {
        return DroolsGuvnorImages.INSTANCE.ScorecardSmall();
    }

    public String getOverallStyleName() {
        return "scorecard-upload"; //NON-NLS
    }

}
