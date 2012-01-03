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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.TextArea;

import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;

public class XmlFileWidget extends AssetAttachmentFileWidget
        implements
        SaveEventListener {

    private TextArea text;
    final private RuleContentText data;

    public XmlFileWidget( final Asset asset,
                          final RuleViewer viewer,
                          ClientFactory clientFactory,
                          EventBus eventBus) {
        super( asset,
                viewer,
                clientFactory,
                eventBus );
        data = (RuleContentText) asset.getContent();

        if ( data.content == null ) {
            data.content = "";
        }

        text = new TextArea();
        text.setWidth( "100%" );
        text.setVisibleLines( 16 );
        text.setText( data.content );

        text.setStyleName( "default-text-Area" );

        text.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                data.content = text.getText();
            }
        } );

        layout.addRow( text );
    }

    public ImageResource getIcon() {
        return null;
    }

    public String getOverallStyleName() {
        return null;
    }

    public void onSave() {
        data.content = text.getText();
        asset.setContent( data );
    }

    public void onAfterSave() {

    }
}
