/*
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

package org.drools.guvnor.client.asseteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RuleAsset;

public class BPELWrapper extends Composite
        implements
        EditorWidget {

    public BPELWrapper(RuleAsset asset,
                       RuleViewer viewer,
                       ClientFactory clientFactory,
                       EventBus eventBus) {

        final String uuid = asset.getUuid();
        final String fileName = asset.getName();
        final String dirName = asset.getMetaData().getPackageName();
        final String servletName = "workflowmanager";
        final String isNew = (asset.getContent() == null ? "true" : "false");

        AssetAttachmentFileWidget uploadWidget = new DefaultContentUploadEditor(
                asset,
                viewer,
                clientFactory,
                eventBus);

        VerticalPanel panel = new VerticalPanel();
        panel.add(uploadWidget);

        Button viewSource = new Button();
        Constants constants = GWT.create(Constants.class);
        viewSource.setText(constants.OpenEditorInNewWindow());

        final String url = "bpeleditor/BPELEditor.html?uuid=" + uuid + "&fileName=" + fileName + "&dirName=" + dirName
                + "&servletName=" + servletName + "&isNew=" + isNew;
        viewSource.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                Window.open(url,
                        "_" + fileName,
                        null);
            }
        });

        panel.add(viewSource);

        initWidget(panel);

        this.setStyleName(getOverallStyleName());
    }

    public String getOverallStyleName() {
        return "decision-Table-upload";
    }
}
