package org.drools.guvnor.client.packages;
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



import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.core.client.GWT;

/**
 * This wraps a file uploader utility for model packages.
 * Model packages are jar files.
 *
 * @author Michael Neale
 * @author Fernando Meyer
 */

public class ModelAttachmentFileWidget extends AssetAttachmentFileWidget implements SaveEventListener {


    private String packageName;

	public ModelAttachmentFileWidget(RuleAsset asset, RuleViewer viewer) {
        super( asset, viewer );
        this.packageName = asset.metaData.packageName;
    }


    public String getIcon() {
        return "images/model_large.png";
    }

    public String getOverallStyleName() {
        return "editable-Surface";
    }



	public void onSave() {
	}

    /**
     * As we want to refresh the suggestion completion engine.
     */
	public void onAfterSave() {
		LoadingPopup.showMessage(((Constants) GWT.create(Constants.class)).RefreshingModel());
		SuggestionCompletionCache.getInstance().loadPackage(packageName, new Command() {
			public void execute() {
				LoadingPopup.close();
			}
		});

	}

}

