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

package org.jboss.bpm.console.client.navigation.modules;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.jboss.bpm.console.client.navigation.NavigationViewFactory;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.rpc.Module;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.gwt.user.client.ui.IsTreeItem;

public class ModuleTreeItemTest {

    @Test
    public void testSetRootItem() throws Exception {
        IsTreeItem treeItem = Mockito.mock(IsTreeItem.class);
        ModuleTreeItemView view = Mockito.mock(ModuleTreeItemView.class);

        PerspectiveFactory perspectiveFactory = Mockito.mock(PerspectiveFactory.class);
        Mockito.when(
                perspectiveFactory.getRegisteredAssetEditorFormats("package")
        ).thenReturn(
                new String[0]
        );

        NavigationViewFactory navigationViewFactory = Mockito.mock(NavigationViewFactory.class);
        Mockito.when(
                navigationViewFactory.getModuleTreeItemView()
        ).thenReturn(
                view
        );

        Module packageConfigData = Mockito.mock(Module.class);
        Mockito.when(
                packageConfigData.getUuid()
        ).thenReturn(
                "mockUuid"
        );
        new ModuleTreeSelectableItem( navigationViewFactory, treeItem, packageConfigData );

        Mockito.verify(view).setRootItem(treeItem);

        ArgumentCaptor<ModuleEditorPlace> moduleEditorPlaceArgumentCaptor = ArgumentCaptor.forClass( ModuleEditorPlace.class );
        Mockito.verify(view).setRootUserObject( moduleEditorPlaceArgumentCaptor.capture() );
        ModuleEditorPlace assetViewerPlace = moduleEditorPlaceArgumentCaptor.getValue();

        Assert.assertEquals("mockUuid", assetViewerPlace.getUuid());
    }


}
