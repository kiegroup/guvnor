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

package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.common.DefaultContentUploadEditor;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BPELWrapper extends Composite
    implements
    EditorWidget {

    private Constants constants = GWT.create( Constants.class );

    public BPELWrapper(RuleAsset asset,
                       RuleViewer viewer) {

        final String uuid = asset.uuid;
        final String fileName = asset.metaData.name;
        final String dirName = asset.metaData.packageName;
        final String servletName = "workflowmanager";
        final String isNew = (asset.content == null ? "true" : "false");

        AssetAttachmentFileWidget uploadWidget = new DefaultContentUploadEditor( asset,
                                                                                 viewer );

        VerticalPanel panel = new VerticalPanel();
        panel.add( uploadWidget );

        Button viewSource = new Button();
        viewSource.setText( constants.OpenEditorInNewWindow() );

        final String url = Format.format( "bpeleditor/BPELEditor.html?uuid={0}&fileName={1}&dirName={2}&servletName={3}&isNew={4}",
                                          new String[]{uuid, fileName, dirName, servletName, isNew} );
        viewSource.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                Window.open( url,
                             "_" + fileName,
                             null );
            }
        } );

        panel.add( viewSource );

        initWidget( panel );

        this.setStyleName( getOverallStyleName() );
    }

    public String getOverallStyleName() {
        return "decision-Table-upload";
    }
}
