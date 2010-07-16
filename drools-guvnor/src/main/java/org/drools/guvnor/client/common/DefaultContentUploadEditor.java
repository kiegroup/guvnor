/**
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.common;

import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.core.client.GWT;

public class DefaultContentUploadEditor extends AssetAttachmentFileWidget {

    public DefaultContentUploadEditor(
            RuleAsset asset, RuleViewer viewer) {
		super( asset,
		viewer );
		super.addDescription(new HTML(((Constants) GWT.create(Constants.class)).UploadNewVersionDescription()));
    }

    public String getIcon() {
    	return "images/decision_table.png";       //NON-NLS
    }

    public String getOverallStyleName() {
    	return "decision-Table-upload";           //NON-NLS
    }

}
