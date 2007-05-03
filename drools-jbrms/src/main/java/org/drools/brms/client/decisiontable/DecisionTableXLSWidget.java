package org.drools.brms.client.decisiontable;

import org.drools.brms.client.packages.AssetAttachmentFileWidget;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.ruleeditor.RuleViewer;

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
