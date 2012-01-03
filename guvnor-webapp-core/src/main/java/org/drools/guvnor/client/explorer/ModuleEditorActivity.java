/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.explorer;

import com.google.gwt.event.shared.EventBus;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.moduleeditor.ModuleEditorWrapper;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.util.Activity;

public class ModuleEditorActivity extends Activity {

    private final ClientFactory clientFactory;
    private ModuleEditorActivityView view;
    private String uuid;

    // TODO: add handler for module refresh event -Rikkola-

    public ModuleEditorActivity( String uuid, ClientFactory clientFactory ) {
        this.view = clientFactory.getNavigationViewFactory().getModuleEditorActivityView();

        this.uuid = uuid;

        this.clientFactory = clientFactory;
    }

    @Override
    public void start( final AcceptItem acceptTabItem, final EventBus eventBus ) {

        view.showLoadingPackageInformationMessage();

        clientFactory.getModuleService().loadModule( uuid,
                new GenericCallback<Module>() {
                    public void onSuccess( Module packageConfigData ) {
                        RulePackageSelector.currentlySelectedPackage = packageConfigData.getUuid();
                        acceptTabItem.add(
                                packageConfigData.name,
                                new ModuleEditorWrapper( packageConfigData, clientFactory, eventBus ) );

                        view.closeLoadingPackageInformationMessage();
                    }
                } );
    }
}
