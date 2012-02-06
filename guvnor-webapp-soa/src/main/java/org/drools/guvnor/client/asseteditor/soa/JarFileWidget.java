/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.asseteditor.soa;

import org.drools.guvnor.client.asseteditor.AssetAttachmentFileWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;

/**
 * This widget deals with JAR file containing Java classes as a service
 * artifact.
 */
public class JarFileWidget extends AssetAttachmentFileWidget {

    private final TextArea        text;
    private String data = "";
    
    public JarFileWidget(Asset asset,
                      RuleViewer viewer,
                      ClientFactory clientFactory,
                      EventBus eventBus) {
        super( asset,
               viewer,
               clientFactory,
               eventBus );
        
        //super.addSupplementaryWidget( new HTML( ((Constants) GWT.create( Constants.class )).JarWidgetDescription() ) );
        
        RuleContentText ruleContentText = (RuleContentText) asset.getContent();

        if ( ruleContentText != null && ruleContentText.content != null ) {
            data = ruleContentText.content;
        }

        text = new TextArea();
        text.setWidth( "100%" );
        text.setVisibleLines( 16 );
        text.setText( data );
        text.setReadOnly(true);

        text.setStyleName( "default-text-Area" );

/*        text.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                data.content = text.getText();
            }
        } );*/

        addSupplementaryWidget( text );        
    }

    public ImageResource getIcon() {
        return images.modelLarge();
    }

    public String getOverallStyleName() {
        return "decision-Table-upload"; //NON-NLS
    }
}
