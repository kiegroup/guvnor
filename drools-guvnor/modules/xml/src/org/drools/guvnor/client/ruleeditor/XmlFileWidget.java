package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.user.client.ui.TextArea;
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;

/**
 * xml content editor
 */
public class XmlFileWidget extends AssetAttachmentFileWidget implements SaveEventListener {

    RuleAsset asset;
    private TextArea text;

    public XmlFileWidget(final RuleAsset asset, final RuleViewer viewer) {
        super(asset, viewer);
        this.asset = asset;
        text = new TextArea();
        text.setHeight("300px");
        text.setWidth("600px");

        if (asset.content != null) {
            RuleContentText xmlContent = (RuleContentText) asset.content;
            text.setText(xmlContent.content);
        }

        layout.addRow(text);

        //TODO: add tree representation of the document 
    }

    public String getIcon() {
        return "images/decision_table.png";  //TODO: add icon
    }

    public String getOverallStyleName() {
        return "decision-Table-upload";      //TODO: define style?
    }

    public void onSave() {
        //TODO: validate if the XML is valid 

        RuleContentText rct = new RuleContentText();
        rct.content = text.getText();
        asset.content = rct;
    }

    public void onAfterSave() {

    }
}