package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;

/**
 *
 */
public class XmlFileWidget extends AssetAttachmentFileWidget {

    RuleAsset asset;

    public XmlFileWidget(final RuleAsset asset, final RuleViewer viewer) {
        super(asset, viewer);
        this.asset = asset;

        //TODO: reflect xml tree and a text area containing the textual XML representation
        //TODO: 

    }

    public String getIcon() {
        return "images/decision_table.png";  //TODO: add icon
    }

    public String getOverallStyleName() {
        return "decision-Table-upload";      //TODO: define style?
    }
}
