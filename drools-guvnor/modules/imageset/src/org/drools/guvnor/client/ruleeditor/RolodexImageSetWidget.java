package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.user.client.ui.impl.ClippedImagePrototype;
import com.google.gwt.core.client.GWT;
import com.yesmail.gwt.rolodex.client.RolodexCardBundle;
import com.yesmail.gwt.rolodex.client.RolodexCard;
import com.yesmail.gwt.rolodex.client.RolodexPanel;
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.common.HTMLFileManagerFields;
import org.drools.guvnor.client.ruleeditor.RuleViewer;

/**
 * RolodexImageSetWidget makes use of <a href="http://code.google.com/p/gwt-rolodex/">rolodex</a><br/>
 * widget to display the attached images.
 *
 * @author Anton Arhipov
 */
public class RolodexImageSetWidget extends AssetAttachmentFileWidget {

    RuleAsset asset;

    public RolodexImageSetWidget(final RuleAsset asset, final RuleViewer viewer) {
        super(asset, viewer);
        this.asset = asset;

        RolodexCardBundle images = getImagesFromAsset();
        RolodexCard[] rolodexCards = images.getRolodexCards();
        if (rolodexCards.length > 0) {
            final RolodexPanel rolodex = new RolodexPanel(images, 3, rolodexCards[0], true);
            rolodex.setHeight("200px");  //TODO: panel size should be computed based on the image
            layout.addRow(rolodex);
        }
    }

    public String getIcon() {
        return "images/decision_table.png";  //TODO: add icon
    }

    public String getOverallStyleName() {
        return "decision-Table-upload";      //TODO: define style?
    }

    public RolodexCardBundle getImagesFromAsset() {
        return new RolodexCardBundle() {
            public int getMaxHeight() {
                return 80;                   //TODO: get a real maximum height
            }

            ClippedImagePrototype clip = getClip();

            RolodexCard card = new RolodexCard(clip, clip, clip, 300, 100, 10);

            public RolodexCard[] getRolodexCards() {
                return new RolodexCard[]{card};
            }
        };
    }

    private ClippedImagePrototype getClip() {
        //TODO: if the attachment doesn't exist AssetFileServlet will throw an NPE
        //TODO: need to find out how to check the condition if an attachment exists for a given UUID
        return new ClippedImagePrototype(
                GWT.getModuleBaseURL() + "asset?" +
                        HTMLFileManagerFields.FORM_FIELD_UUID +
                        "=" + asset.uuid, 0, 0, 300, 200
        );
    }

}
