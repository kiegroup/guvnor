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

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;

/**
 * The Business Process Editor, wrapping the Process Editor
 */
public class BusinessProcessEditor extends DirtyableComposite
    implements
    SaveEventListener,
    EditorWidget {

    private String    modelUUID;
    private RuleAsset asset;
    private Frame     frame;

    public BusinessProcessEditor(RuleAsset asset,
                                 RuleViewer viewer,
                                 ClientFactory clientFactory,
                                 EventBus eventBus) {
        this.asset = asset;
        modelUUID = asset.getUuid();
        initWidgets();
    }

    private void initWidgets() {
        String name;

        /**
         EditorLauncher.HOSTED_MODE = Boolean.TRUE; // HACK to set it to HOSTED MODE
         if ( EditorLauncher.HOSTED_MODE.booleanValue() ) {
             name = "http://localhost:8080/designer/editor";
         } else {
             name = "/designer/editor";
         } **/

        name = "/"+ApplicationPreferences.getDesignerContext()+"/editor/?uuid=" + modelUUID + "&profile="+ApplicationPreferences.getDesignerProfile();
        frame = new Frame( name );
        frame.getElement().setAttribute( "domain",
                                         Document.get().getDomain() );
        frame.setWidth( "100%" );
        frame.setHeight( "100%" );
        initWidget( frame );
        setWidth( "100%" );
        setHeight( "100%" );
    }

    private final native String callSave(Document frameDoc) /*-{
                                                            return frameDoc.defaultView.ORYX.EDITOR.getSerializedJSON();
                                                            }-*/;
    
    private final native String callPreprocessingData(Document frameDoc) /*-{
        return frameDoc.defaultView.ORYX.PREPROCESSING;
    }-*/;

    public void onSave() {
        try {
            String s = callSave( ((IFrameElement) ((com.google.gwt.dom.client.Element) frame.getElement())).getContentDocument() );
            String p = callPreprocessingData( ((IFrameElement) ((com.google.gwt.dom.client.Element) frame.getElement())).getContentDocument() );
            if ( asset.getContent() == null ) {
                asset.setContent( new RuleFlowContentModel() );
            }
            ((RuleFlowContentModel) asset.getContent()).setXml( null );
            ((RuleFlowContentModel) asset.getContent()).setJson( s );
            ((RuleFlowContentModel) asset.getContent()).setPreprocessingdata(p);
        } catch ( Exception e ) {
            GWT.log( "JSNI method callSave() threw an exception:",
                     e );
            Window.alert( "JSNI method callSave() threw an exception: " + e );
        }
    }

    public void onAfterSave() {
    }
}
