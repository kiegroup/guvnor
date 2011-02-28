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

package org.drools.guvnor.client.processeditor;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.client.ruleeditor.EditorLauncher;
import org.drools.guvnor.client.ruleeditor.EditorWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewerSettings;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
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

    public BusinessProcessEditor(RuleAsset asset) {
        this.asset = asset;
        modelUUID = asset.uuid;
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
        
        name = "/designer/editor/?uuid=" + modelUUID + "&profile=drools";
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
        //window.alert("JSON: " + frameDoc.defaultView.ORYX.EDITOR.getSerializedJSON());
        return frameDoc.defaultView.ORYX.EDITOR.getSerializedJSON();
    }-*/;
    
    public void onSave() {
        try {
            String s = callSave( ((IFrameElement) ((com.google.gwt.dom.client.Element) frame.getElement())).getContentDocument() );
            if ( asset.content == null ) {
                asset.content = new RuleFlowContentModel();
            }
            ((RuleFlowContentModel) asset.content).setXml( null );
            ((RuleFlowContentModel) asset.content).setJson( s );
        } catch(Exception e) {
            GWT.log("JSNI method callSave() threw an exception:", e);
            Window.alert("JSNI method callSave() threw an exception: " + e);
        }
    }

    public void onAfterSave() {
    }

}
