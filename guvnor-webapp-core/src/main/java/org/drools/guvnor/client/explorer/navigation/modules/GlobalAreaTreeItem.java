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

package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Module;

public class GlobalAreaTreeItem extends ModulesTreeItemBase {

    public GlobalAreaTreeItem( ClientFactory clientFactory ) {
        super(
                clientFactory,
                clientFactory.getNavigationViewFactory().getGlobalAreaTreeItemView(),
                null);
    }

    @Override
    protected void fillModulesTree( final IsTreeItem treeItem ) {
        clientFactory.getModuleService().loadGlobalModule( new GenericCallback<Module>() {
            public void onSuccess( Module packageConfigData ) {
                new ModuleTreeSelectableItem(
                        clientFactory,
                        treeItem,
                        packageConfigData
                );
            }
        } );
    }
}
