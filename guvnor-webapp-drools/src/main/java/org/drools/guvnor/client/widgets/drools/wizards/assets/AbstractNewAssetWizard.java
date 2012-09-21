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
package org.drools.guvnor.client.widgets.drools.wizards.assets;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.NewAssetWithContentConfiguration;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.client.rpc.RepositoryServiceAsync;
import org.drools.guvnor.client.widgets.wizards.AbstractWizard;
import org.drools.guvnor.client.widgets.wizards.WizardActivityView;
import org.drools.guvnor.shared.api.PortableObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

/**
 * A Wizard representing new assets
 */
public abstract class AbstractNewAssetWizard<T extends PortableObject>
        extends AbstractWizard<NewAssetWizardContext> {

    public AbstractNewAssetWizard(ClientFactory clientFactory,
                                  EventBus eventBus,
                                  NewAssetWizardContext context,
                                  WizardActivityView.Presenter presenter) {
        super( clientFactory,
               eventBus,
               context,
               presenter );
    }

    /**
     * Save the asset.
     * 
     * @param config
     */
    protected void save(final NewAssetWithContentConfiguration< ? extends PortableObject> config,
                        final T content) {
        RepositoryServiceAsync repositoryService = GWT.create( RepositoryService.class );
        repositoryService.createNewRule( config,
                                         createCreateAssetCallback( content ) );
    }

    /**
     * Call-back following creation of the new Asset. Upon successful creation
     * the new Asset is loaded in order for its content to be updated
     * 
     * @param content
     * @return
     */
    protected GenericCallback<String> createCreateAssetCallback(final T content) {
        GenericCallback<String> cb = new GenericCallback<String>() {
            public void onSuccess(String uuid) {
                presenter.hideSavingIndicator();
                if ( uuid == null ) {
                    presenter.showUnspecifiedCheckinError();
                    return;
                }
                if ( uuid.startsWith( "DUPLICATE" ) ) {
                    presenter.showDuplicateAssetNameError();
                    return;
                }
                if ( uuid.startsWith( "ERR" ) ) {
                    presenter.showCheckinError( uuid.substring( 5 ) );
                    return;
                }
                presenter.hide();
                openEditor( uuid );
            }
        };
        return cb;
    }

    /**
     * Open an Asset in its corresponding editor
     * 
     * @param uuid
     */
    protected void openEditor(String uuid) {
        clientFactory.getDeprecatedPlaceController().goTo( new AssetEditorPlace( uuid ) );
    }

}
