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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.AssetPageRequest;
import org.drools.guvnor.client.rpc.AssetPageRow;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.ruleeditor.DefaultRuleContentWidget;
import org.drools.guvnor.client.ruleeditor.EditorWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The editor for fact models (DRL declared types).
 */
public class FactModelsWidget extends Composite
    implements
    SaveEventListener,
    EditorWidget {

    private static final Constants    constants           = ((Constants) GWT.create( Constants.class ));

    private final RuleAsset           asset;
    private final ClientFactory       clientFactory;

    //These are passed up to FactEditorPopup to provide the list of types from which to extend and provide 
    //validation services. They are populated asynchronously from 'populateSuperTypesFactModels' that calls 
    //back to the server to retrieve a list of all Declared Facts in the package.
    private final List<FactMetaModel> superTypeFactModels = new ArrayList<FactMetaModel>();
    private final ModelNameHelper     modelNameHelper     = new ModelNameHelper();

    //While the necessary model information is loading the screen is initialised to an 
    //empty container. The container is populated with the actual screen widget once the 
    //necessary model information has been loaded.
    private final SimplePanel         editorContainer     = new SimplePanel();

    public FactModelsWidget(final RuleAsset asset,
                            final RuleViewer viewer,
                            final ClientFactory clientFactory,
                            final EventBus eventBus) {
        this.asset = asset;
        this.clientFactory = clientFactory;

        if ( isContentPlainText() ) {
            initWidget( getPlainTextEditor() );
        } else {
            initWidget( editorContainer );
            initFactModelsEditor();
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

    private void initFactModelsEditor() {
        if ( asset.getContent() == null ) {
            asset.setContent( new FactModels() );
        }
        populateSuperTypesFactModels();
    }

    //Load all Declarative Model assets in the package
    private void populateSuperTypesFactModels() {
        String containingModuleUUID = asset.getMetaData().getPackageUUID();
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

                //Load the fact being edited
                loadFacts( asset );

                //Don't load Facts for the Asset being edited as we want the same
                //objects instances to detect for circular dependencies.
                final FactModelsSemaphore s = new FactModelsSemaphore( assets.size() - 1 );

                //If there are no models show the editor
                if ( s.areAllFactModelsProcessed() ) {
                    addEditorToContainer();
                } else {
                    for ( AssetPageRow otherAsset : assets ) {
                        if ( !otherAsset.getUuid().equals( asset.getUuid() ) ) {
                            clientFactory.getAssetService().loadRuleAsset( otherAsset.getUuid(),
                                                                           makeLoadFactModelsCallback( s ) );
                        }
                    }
                }
            }
        };
    }

    //Add all Facts in a Declarative Model to the complete (cross-package) collection
    private GenericCallback<RuleAsset> makeLoadFactModelsCallback(final FactModelsSemaphore s) {
        return new GenericCallback<RuleAsset>() {

            public void onSuccess(RuleAsset otherAsset) {
                loadFacts( otherAsset );

                //When all Fact Models have been loaded show the editor
                s.recordFactModelProcessed();
                if ( s.areAllFactModelsProcessed() ) {
                    addEditorToContainer();
                }
            }

        };
    }

    private void addEditorToContainer() {
        FactModelsEditor editor = new FactModelsEditor( ((FactModels) asset.getContent()).models,
                                                        superTypeFactModels,
                                                        modelNameHelper );
        editorContainer.setWidget( editor );
    }

    //Load Facts for a given asset
    private void loadFacts(RuleAsset asset) {
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

    //A container for the number of Fact Models to be added to the editor. Each Fact 
    //Model is loaded with asynchronous GWT-RPC calls. Since we cannot control when the 
    //responses are received we keep a running total of the number of Fact Models so
    //the UI can be created after all have been loaded
    private static class FactModelsSemaphore {

        int numberOfFactModels = 0;

        FactModelsSemaphore(int numberOfFactModels) {
            this.numberOfFactModels = numberOfFactModels;
        }

        synchronized void recordFactModelProcessed() {
            numberOfFactModels--;
        }

        synchronized boolean areAllFactModelsProcessed() {
            return this.numberOfFactModels == 0;
        }

    }

    public void onAfterSave() {
    	//Refresh  SuggestionCompletionCache is done by RuleViewer.flushSuggestionCompletionCache(). No need to refresh it twice here.
/*        LoadingPopup.showMessage( constants.RefreshingModel() );
        SuggestionCompletionCache.getInstance().loadPackage( this.asset.getMetaData().getPackageName(),
                                                             new Command() {
                                                                 public void execute() {
                                                                     LoadingPopup.close();
                                                                 }
                                                             } );*/
    }

    public void onSave() {
        //not needed.

    }

}