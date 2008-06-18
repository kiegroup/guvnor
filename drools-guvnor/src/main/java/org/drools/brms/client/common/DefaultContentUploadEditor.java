package org.drools.brms.client.common;

import org.drools.brms.client.packages.AssetAttachmentFileWidget;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.ruleeditor.RuleViewer;

import com.google.gwt.user.client.ui.HTML;

public class DefaultContentUploadEditor extends AssetAttachmentFileWidget {

    public DefaultContentUploadEditor(
            RuleAsset asset, RuleViewer viewer) {
		super( asset,
		viewer );
		super.addDescription(new HTML("<small><i>Upload new version...</i></small>"));
    }

    public String getIcon() {
    	return "images/decision_table.png";
    }

    public String getOverallStyleName() {
    	return "decision-Table-upload";
    }

}
