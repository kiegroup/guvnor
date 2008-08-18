package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;

/**
 *
 */
public class XmlFileWidget extends AssetAttachmentFileWidget implements SaveEventListener {

    private TextArea text;
    final private RuleContentText data;


    public XmlFileWidget(final RuleAsset asset, final RuleViewer viewer) {
        super(asset, viewer);
        data = (RuleContentText) asset.content;

        if (data.content == null) {
            data.content = "";
        }

        text = new TextArea();
        text.setWidth("100%");
        text.setVisibleLines(16);
        text.setText(data.content);

        text.setStyleName("default-text-Area");

        text.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                data.content = text.getText();
            }
        });

        layout.addRow(text);
    }

    public String getIcon() {
        return null;
    }

    public String getOverallStyleName() {
        return null;
    }

    public void onSave() {
        data.content = text.getText();
        asset.content = data;
    }

    public void onAfterSave() {

    }
}
