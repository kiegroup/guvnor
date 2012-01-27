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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.asseteditor.DefaultRuleContentWidget;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
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

    private static final Constants    constants           = ((Constants) GWT.create( Constants.class ));

    private final Asset               asset;
    private final ClientFactory       clientFactory;

    //These are passed up to FactEditorPopup to provide the list of types from which to extend and provide 
    //validation services. They are populated asynchronously from 'populateSuperTypesFactModels' that calls 
    //back to the server to retrieve a list of all Declared Facts in the package. There is a chance they
    //will not be populated before the user navigates to the FactEditorPopup. If this proves to be the 
    //case we should defer population of the screen widget until the list has been populated.
    private final List<FactMetaModel> superTypeFactModels = new ArrayList<FactMetaModel>();
    private final ModelNameHelper     modelNameHelper     = new ModelNameHelper();

    public FactModelsWidget(final Asset asset,
                            final RuleViewer viewer,
                            final ClientFactory clientFactory,
                            final EventBus eventBus) {
        this.asset = asset;
        this.clientFactory = clientFactory;

        if ( isContentPlainText() ) {
            initWidget( getPlainTextEditor() );
        } else {
            initWidget( getFactModelsEditor() );
        }

        setWidth( "100%" );

        setStyleName( "model-builder-Background" );
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
        populateSuperTypesFactModels();
        return new FactModelsEditor( ((FactModels) asset.getContent()).models,
                                     superTypeFactModels,
                                     modelNameHelper );

    }

    //Load all Declarative Model assets in the package
    private void populateSuperTypesFactModels() {
        String containingModuleUUID = asset.getMetaData().getModuleUUID();
        List<String> formats = Arrays.asList( new String[]{AssetFormats.DRL_MODEL} );

        AssetPageRequest request = new AssetPageRequest( containingModuleUUID,
                                                         formats,
                                                         null );
        clientFactory.getAssetService().findAssetPage( request,
                                                       makeLoadAssetsCallback() );
    }

    //Load all Facts in a Declarative Model asset
    private GenericCallback<PageResponse<AssetPageRow>> makeLoadAssetsCallback() {
        return new GenericCallback<PageResponse<AssetPageRow>>() {

            //We have a list of Declarative Models
            public void onSuccess(PageResponse<AssetPageRow> result) {
                loadFactModelAssets( result.getPageRowList() );
            }

            //Iterate list of Declarative Models, loading each model and adding types to complete list
            private void loadFactModelAssets(List<AssetPageRow> assets) {
                for ( AssetPageRow otherAsset : assets ) {
                    //Don't load Facts for the Asset being edited as we want the same
                    //objects instances to detect for circular dependencies.
                    if ( !otherAsset.getUuid().equals( asset.getUuid() ) ) {
                        clientFactory.getAssetService().loadRuleAsset( otherAsset.getUuid(),
                                                                       makeLoadFactModelsCallback() );
                    }
                }
                
                //Load the fact being edited
                loadFacts( asset );
            }
        };
    }

    //Add all Facts in a Declarative Model to the complete (cross-package) collection
    private GenericCallback<Asset> makeLoadFactModelsCallback() {
        return new GenericCallback<Asset>() {

            public void onSuccess(Asset asset) {
                loadFacts( asset );
            }

        };
    }

    //Load Facts for a given asset
    private void loadFacts(Asset asset) {
        if ( asset.getContent() == null ) {
            asset.setContent( new FactModels() );
        }
        FactModels factModels = ((FactModels) asset.getContent());
        for ( FactMetaModel factMetaModel : factModels.models ) {
            superTypeFactModels.add( factMetaModel );
            modelNameHelper.getTypeDescriptions().put( factMetaModel.getName(),
                                                       factMetaModel.getName() );
        }
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
