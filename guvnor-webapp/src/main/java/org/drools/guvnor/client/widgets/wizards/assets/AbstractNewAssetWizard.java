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
package org.drools.guvnor.client.widgets.wizards.assets;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.AssetEditorPlace;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.RefreshModuleEditorEvent;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.widgets.wizards.AbstractWizard;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;

/**
 * A Wizard representing new assets
 */
public abstract class AbstractNewAssetWizard
        extends AbstractWizard {

    public AbstractNewAssetWizard(ClientFactory clientFactory,
                                  EventBus eventBus) {
        super( clientFactory,
               eventBus );
    }

    protected Command makeSaveCommand(final NewAssetWizardContext context) {
        final Command cmdSave = new Command() {

            public void execute() {
                RepositoryServiceFactory.getService().createNewRule( context.getAssetName(),
                                                                     context.getDescription(),
                                                                     context.getInitialCategory(),
                                                                     context.getPackageName(),
                                                                     context.getFormat(),
                                                                     createGenericCallbackForOk( context ) );
            }
        };
        return cmdSave;
    }

    protected GenericCallback<String> createGenericCallbackForOk(final NewAssetWizardContext context) {
        GenericCallback<String> cb = new GenericCallback<String>() {
            public void onSuccess(String uuid) {
                if ( uuid.startsWith( "DUPLICATE" ) ) {
                    LoadingPopup.close();
                    //TODO UI-->        Window.alert( constants.AssetNameAlreadyExistsPickAnother() );
                } else {
                    eventBus.fireEvent( new RefreshModuleEditorEvent( context.getPackageUUID() ) );
                    openEditor( uuid );
                    //TODO UI-->        hide();
                }
            }
        };
        return cb;
    }

    protected void openEditor(String uuid) {
        clientFactory.getPlaceController().goTo( new AssetEditorPlace( uuid ) );
    }

}
