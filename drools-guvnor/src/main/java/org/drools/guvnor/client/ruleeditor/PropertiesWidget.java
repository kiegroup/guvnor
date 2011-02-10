/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.widgets.tables.PropertiesEditorSimpleTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Properties (key/value pairs) editor with a file attachment.
 * 
 * @author Anton Arhipov
 */
public class PropertiesWidget extends AssetAttachmentFileWidget
    implements
    SaveEventListener {

    private PropertiesHolder            properties;
    private PropertiesEditorSimpleTable propertiesEditor;

    private static Images               images = (Images) GWT.create( Images.class );

    public PropertiesWidget(final RuleAsset asset,
                            final RuleViewer viewer) {
        super( asset,
               viewer );

        if ( asset.content == null ) {
            properties = new PropertiesHolder();
        } else {
            properties = (PropertiesHolder) asset.content;
        }

        VerticalPanel panel = new VerticalPanel();
        propertiesEditor = new PropertiesEditorSimpleTable( properties.list );
        panel.add( propertiesEditor );

        layout.addRow( panel );
    }

    public ImageResource getIcon() {
        return images.newFileLarge();
    }

    public String getOverallStyleName() {
        return ""; // TODO: set correct style
    }

    @Override
    public void onSave() {
        // Scrape changes back into the persistent model
        properties.list = propertiesEditor.getPropertyHolders();
        asset.content = properties;
    }

    @Override
    public void onAfterSave() {
        // Do nothing
    }

}
