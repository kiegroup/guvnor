package org.drools.brms.client.ruleeditor;
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



import org.drools.brms.client.packages.AssetAttachmentFileWidget;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.ui.HTML;

/**
 * For ruleflow upload.
 * 
 * @author Michael Neale
 */
public class RuleFlowUploadWidget extends AssetAttachmentFileWidget {

    public RuleFlowUploadWidget(
                                  RuleAsset asset, RuleViewer viewer) {
        super( asset,
               viewer );
        super.addDescription(new HTML("<small><i>Ruleflows allow flow control between rules. " +
                "The eclipse plugin provides a graphical editor. Upload ruleflow .rf files for inclusion in this package.</i></small>"));
    }

    public String getIcon() {
        return "images/ruleflow_large.png";
    }
    
    public String getOverallStyleName() {
        return "decision-Table-upload";
    }
    


}