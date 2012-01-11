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

package org.drools.guvnor.client.asseteditor.drools.factmodel;

import org.drools.guvnor.client.asseteditor.DefaultRuleContentWidget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The editor for fact models (DRL declared types).
 */
public class FactModelsWidget extends Composite
    implements
    SaveEventListener,
    EditorWidget {

    private Asset        asset;
    private static Constants constants = ((Constants) GWT.create( Constants.class ));

    public FactModelsWidget(Asset asset,
                            RuleViewer viewer,
                        ClientFactory clientFactory,
                        EventBus eventBus) {
        this( asset );
    }

    public FactModelsWidget(final Asset asset) {
        this.asset = asset;

        if ( isContentPlainText() ) {
            initWidget( getPlainTextEditor() );
        } else {
            initWidget( getFactModelsEditor() );
        }

        setWidth( "100%" );

        setStyleName( "model-builder-Background" ); //NON-NLS
    }

    private boolean isContentPlainText() {
        return asset.getContent() instanceof RuleContentText;
    }

    private Widget getPlainTextEditor() {
        return new DefaultRuleContentWidget( asset );
    }

    private Widget getFactModelsEditor() {
        if ( asset.getContent() == null ) {
            asset.setContent( new FactModels() );
        }

        return new FactModelsEditor( ((FactModels) asset.getContent()).models );

    }

    public void onAfterSave() {
        LoadingPopup.showMessage( constants.RefreshingModel() );
        SuggestionCompletionCache.getInstance().loadPackage( this.asset.getMetaData().getModuleName(),
                                                             new Command() {
                                                                 public void execute() {
                                                                     LoadingPopup.close();
                                                                 }
                                                             } );
    }

    public void onSave() {
        //not needed.

    }

}
