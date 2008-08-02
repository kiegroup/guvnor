package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;

/**
 *
 */
public class PropertiesWidget extends AssetAttachmentFileWidget {

    public PropertiesWidget(final RuleAsset asset, final RuleViewer viewer) {
        super(asset, viewer);
    }

    public String getIcon() {
        return null;
    }

    public String getOverallStyleName() {
        return null;
    }
}
