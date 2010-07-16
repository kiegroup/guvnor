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

import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

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
