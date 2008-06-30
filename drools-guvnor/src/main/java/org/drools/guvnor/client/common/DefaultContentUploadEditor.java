package org.drools.guvnor.client.common;

import com.google.gwt.user.client.ui.HTML;
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.Editor;
import org.drools.guvnor.client.ruleeditor.RuleViewer;

public class DefaultContentUploadEditor extends AssetAttachmentFileWidget implements Editor {

    public DefaultContentUploadEditor(RuleAsset asset, RuleViewer viewer) {
		super( asset, viewer );
		super.addDescription(new HTML("<small><i>Upload new version...</i></small>"));
    }

    public String getIcon() {
    	return "images/decision_table.png";
    }

    public String getOverallStyleName() {
    	return "decision-Table-upload";
    }


    public String getWrapperClass() {
        return null;
    }

    public String getAssetFormat() {
        return null;
    }
}
