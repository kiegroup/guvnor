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
import org.drools.guvnor.client.rpc.NewAssetConfiguration;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.widgets.wizards.AbstractWizard;
import org.drools.guvnor.client.widgets.wizards.WizardActivityView;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;

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
     * Save the asset. This is a three-phase solution; firstly a new Asset is
     * created in the Repository, then it is retrieved and its content updated
     * before being checked back into the Repository. Hence a new Asset created
     * by a Wizard will have two initial versions: one corresponding to the
     * creation and another corresponding to the checkin of content.
     * 
     * @param config
     */
    protected void save(final NewAssetConfiguration config,
                        final T content) {
        RepositoryServiceFactory.getService().createNewRule( config,
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
                if ( uuid.startsWith( "DUPLICATE" ) ) {
                    presenter.hideSavingIndicator();
                    presenter.showDuplicateAssetNameError();
                } else {
                    RepositoryServiceFactory.getAssetService().loadRuleAsset( uuid,
                                                                              createSetContentCallback( content ) );
                }
            }
        };
        return cb;
    }

    /**
     * Call-back following retrieval of the new Asset from the Repository. Upon
     * successful retrieval the new Asset has its content updated and it is
     * checked back into the Repository
     * 
     * @param content
     * @return
     */
    protected GenericCallback<RuleAsset> createSetContentCallback(final T content) {
        GenericCallback<RuleAsset> cb = new GenericCallback<RuleAsset>() {
            public void onSuccess(RuleAsset asset) {
                asset.setContent( content );
                asset.setCheckinComment( "Created from Wizard" );
                RepositoryServiceFactory.getAssetService().checkinVersion( asset,
                                                                           createCheckedInCallback() );
            }
        };
        return cb;
    }

    /**
     * Call-back following check-in of the updated Asset. Upon successful
     * check-in the new Asset is loaded into its corresponding editor and the
     * Wizard closed
     * 
     * @return
     */
    protected GenericCallback<String> createCheckedInCallback() {
        GenericCallback<String> cb = new GenericCallback<String>() {
            public void onSuccess(String uuid) {
                presenter.hideSavingIndicator();
                if ( uuid == null ) {
                    presenter.showUnspecifiedCheckinError();
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
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( uuid ) );
    }

}
