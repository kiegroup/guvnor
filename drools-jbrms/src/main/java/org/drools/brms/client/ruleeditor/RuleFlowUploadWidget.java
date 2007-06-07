package org.drools.brms.client.ruleeditor;

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