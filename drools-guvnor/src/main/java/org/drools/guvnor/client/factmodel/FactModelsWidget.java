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

package org.drools.guvnor.client.factmodel;

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.ruleeditor.DefaultRuleContentWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The editor for fact models (DRL declared types).
 *
 * @author Michael Neale
 */
public class FactModelsWidget extends Composite
    implements
    SaveEventListener {

    private RuleAsset        asset;
    private static Constants constants = ((Constants) GWT.create( Constants.class ));

    public FactModelsWidget(RuleAsset asset,
                           RuleViewer viewer) {
        this( asset );
    }

    public FactModelsWidget(final RuleAsset asset) {
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
        return asset.content instanceof RuleContentText;
    }

    private Widget getPlainTextEditor() {
        return new DefaultRuleContentWidget( asset );
    }

    private Widget getFactModelsEditor() {
        if ( asset.content == null ) {
            asset.content = new FactModels();
        }

        return new FactModelsEditor( ((FactModels) asset.content).models );

    }

    public void onAfterSave() {
        LoadingPopup.showMessage( constants.RefreshingModel() );
        SuggestionCompletionCache.getInstance().loadPackage( this.asset.metaData.packageName,
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
