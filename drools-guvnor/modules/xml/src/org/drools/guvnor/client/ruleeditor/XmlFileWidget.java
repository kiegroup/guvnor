/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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