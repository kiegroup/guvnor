package org.drools.guvnor.client.decisiontable;
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



import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;

import com.google.gwt.user.client.ui.HTML;

/**
 * This widget deals with XLS files in "classic" decision tables.
 *
 * @author Michael Neale
 */
public class DecisionTableXLSWidget extends AssetAttachmentFileWidget {

    public DecisionTableXLSWidget(
                                  RuleAsset asset, RuleViewer viewer) {
        super( asset,
               viewer );
        super.addDescription(new HTML("<small><i>This is a decision table in a spreadsheet (XLS). Typically they contain many rules in one sheet.</i></small>"));
    }

    public String getIcon() {
        return "images/decision_table.png";
    }

    public String getOverallStyleName() {
        return "decision-Table-upload";
    }



}