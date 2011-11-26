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

package org.drools.guvnor.client.asseteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.client.widgets.MessageWidget;


/**
 * The main layout parent/controller the rule viewer.
 */
public class RuleViewer extends GuvnorEditor {

    private Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    interface RuleViewerBinder
            extends
            UiBinder<Widget, RuleViewer> {

    }

    private static RuleViewerBinder uiBinder = GWT.create( RuleViewerBinder.class );

    @UiField(provided = true)
    final Widget editor;

    @UiField
    MessageWidget messageWidget;

    protected RuleAsset asset;
    private final RuleViewerSettings ruleViewerSettings;
    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    private long lastSaved = System.currentTimeMillis();

    public RuleViewer(
            RuleAsset asset,
            ClientFactory clientFactory,
            EventBus eventBus) {
        this( asset,
                clientFactory,
                eventBus,               
                null );
    }

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset,
                      ClientFactory clientFactory,
                      EventBus eventBus,
                      boolean historicalReadOnly) {
        this( asset,
                clientFactory,
                eventBus,
                null );
    }

    /**
     * @param historicalReadOnly true if this is a read only view for historical purposes.
     */
    public RuleViewer(RuleAsset asset,
                      ClientFactory clientFactory,
                      EventBus eventBus,
                      RuleViewerSettings ruleViewerSettings) {
        this.asset = asset;
        this.eventBus = eventBus;
        this.clientFactory = clientFactory;

        if ( ruleViewerSettings == null ) {
            this.ruleViewerSettings = new RuleViewerSettings();
        } else {
            this.ruleViewerSettings = ruleViewerSettings;
        }

        //editor = EditorLauncher.getEditorViewer( asset, this );
        editor = clientFactory.getAssetEditorFactory().getAssetEditor( asset,
                this,
                clientFactory,
                eventBus);

        // for designer we need to give it more playing room
        if ( editor.getClass().getName().equals( "org.drools.guvnor.client.processeditor.BusinessProcessEditor" ) ) {
            if ( this.ruleViewerSettings.isStandalone() ) {
                // standalone bigger dimensions"
                editor.setWidth( "1600px" );
                editor.setHeight( "1000px" );
            } else {
                // normal dimensions inside guvnor
                editor.setWidth( "100%" );
                editor.setHeight( "580px" );
            }
        }

        initWidget( uiBinder.createAndBindUi( this ) );
        setWidth( "100%" );

        LoadingPopup.close();
    }

    public Widget getAssetEditor() {
    	return editor;
    }
    
    @Override
    public boolean isDirty() {
        return (System.currentTimeMillis() - lastSaved) > 3600000;
    }

	public void showInfoMessage(String message) {
		messageWidget.showMessage(message);
	}
}
