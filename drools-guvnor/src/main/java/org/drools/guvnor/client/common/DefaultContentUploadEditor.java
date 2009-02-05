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
