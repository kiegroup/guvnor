package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;

/**
 *
 */
public class VideoWidget extends AssetAttachmentFileWidget {

    public VideoWidget(final RuleAsset asset, final RuleViewer viewer) {
        super(asset, viewer);
    }

    public String getIcon() {
        return null;
    }

    public String getOverallStyleName() {
        return null;
    }
}
