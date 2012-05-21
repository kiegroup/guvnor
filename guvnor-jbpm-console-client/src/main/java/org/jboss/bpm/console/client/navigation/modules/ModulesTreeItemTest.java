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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.jboss.bpm.console.client.navigation.ModuleFormatsGridPlace;
import org.jboss.bpm.console.client.navigation.NavigationViewFactory;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEvent;
import org.drools.guvnor.client.moduleeditor.RefreshModuleListEventHandler;
import org.drools.guvnor.client.perspective.PerspectiveFactory;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleServiceAsyncMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ModulesTreeItemTest {

    private ModulesTreeItemView view;
    private ModulesTreeItemBaseView.Presenter presenter;
    private Module[]          packageConfigDatas = new Module[0];
    private PlaceController              placeController;
    private IsTreeItem                   modulesTreeItem;
    private ClientFactory                clientFactory;
    private EventBus                     eventBus;

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(ModulesTreeItemView.class);

        modulesTreeItem = Mockito.mock(IsTreeItem.class);
        Mockito.when(view.addModulesTreeItem()).thenReturn( modulesTreeItem );

        clientFactory = Mockito.mock(ClientFactory.class);
        placeController = Mockito.mock(PlaceController.class);
        Mockito.when(
                clientFactory.getPlaceController()).thenReturn(
                                                                 placeController
                );

        Mockito.when(
                clientFactory.getModuleService()).thenReturn(
                                                                new PackageServiceAsyncMockImpl()
                );

        NavigationViewFactory navigationViewFactory = Mockito.mock(NavigationViewFactory.class);
        Mockito.when(
                clientFactory.getNavigationViewFactory()).thenReturn(
                                                                       navigationViewFactory
                );

        Mockito.when(
                navigationViewFactory.getModulesTreeItemView()).thenReturn(
                                                                                      view
                );

        ModuleTreeItemView moduleTreeItemView = Mockito.mock(ModuleTreeItemView.class);
        Mockito.when(
                navigationViewFactory.getModuleTreeItemView()).thenReturn(
                                                                            moduleTreeItemView
                );

        PerspectiveFactory perspectiveFactory = Mockito.mock(PerspectiveFactory.class);
        Mockito.when(
                clientFactory.getPerspectiveFactory()).thenReturn(
                        perspectiveFactory
                );
        Mockito.when(
                perspectiveFactory.getRegisteredAssetEditorFormats("package")).thenReturn(
                                                                                   new String[0]
                );

        eventBus = Mockito.mock(EventBus.class);
    }

    private void setUpPresenter() {
        presenter = new ModulesTreeItem( clientFactory, eventBus, "AuthorPerspective" );
    }

    @Test
    public void testAddModulesNoModulesExist() throws Exception {

        packageConfigDatas = new Module[0];
        setUpPresenter();

        Mockito.verify(view).setPresenter( presenter );
        Mockito.verify(view).addModulesTreeItem();
        Mockito.verify(view,
                Mockito.never()).addModuleTreeItem( Matchers.eq(modulesTreeItem),
                                             Matchers.anyString() );
    }

    @Test
    public void testAddModules() throws Exception {

        ArrayList<Module> firstLevelDatas = new ArrayList<Module>();
        firstLevelDatas.add( new Module( "defaultPackage" ) );
        Module mortgageConfigData = new Module( "mortgage" );

        ArrayList<Module> secondLevelDatas = new ArrayList<Module>();
        secondLevelDatas.add( new Module( "sub1" ) );

        Module thirdLevelConfigDataParent = new Module( "sub2" );
        secondLevelDatas.add( thirdLevelConfigDataParent );

        ArrayList<Module> thirdLevelDatas = new ArrayList<Module>();
        thirdLevelDatas.add( new Module( "level3" ) );

        thirdLevelConfigDataParent.setSubModules( thirdLevelDatas.toArray( new Module[thirdLevelDatas.size()] ) );
        secondLevelDatas.add( new Module( "sub3" ) );

        mortgageConfigData.setSubModules( secondLevelDatas.toArray( new Module[secondLevelDatas.size()] ) );
        firstLevelDatas.add( mortgageConfigData );

        packageConfigDatas = firstLevelDatas.toArray( new Module[firstLevelDatas.size()] );

        IsTreeItem mortgagesRootTreeItem = Mockito.mock(IsTreeItem.class);
        Mockito.when(
                view.addModuleTreeSelectableItem(modulesTreeItem,
                        "mortgage")).thenReturn(
                                                                             mortgagesRootTreeItem
                );

        IsTreeItem thirdLevelParentRootTreeItem = Mockito.mock(IsTreeItem.class);
        Mockito.when(
                view.addModuleTreeSelectableItem(mortgagesRootTreeItem,
                        "sub2")).thenReturn(
                                                                         thirdLevelParentRootTreeItem
                );

        setUpPresenter();

        Mockito.verify(view).addModuleTreeSelectableItem( modulesTreeItem,
                                                    "defaultPackage" );
        Mockito.verify(view).addModuleTreeSelectableItem( modulesTreeItem,
                                                    "mortgage" );

        Mockito.verify(view).addModuleTreeSelectableItem( mortgagesRootTreeItem,
                                                    "sub1" );
        Mockito.verify(view).addModuleTreeSelectableItem( mortgagesRootTreeItem,
                                                    "sub2" );
        Mockito.verify(view).addModuleTreeSelectableItem( mortgagesRootTreeItem,
                                                    "sub3" );

        Mockito.verify(view).addModuleTreeSelectableItem( thirdLevelParentRootTreeItem,
                                                    "level3" );
    }

    @Test
    public void testModuleSelected() throws Exception {

        setUpPresenter();

        presenter.onModuleSelected( new ModuleEditorPlace( "mortgagesUuid" ) );

        ArgumentCaptor<ModuleEditorPlace> placeArgumentCaptor = ArgumentCaptor.forClass( ModuleEditorPlace.class );

        Mockito.verify(placeController).goTo( placeArgumentCaptor.capture() );

        ModuleEditorPlace moduleEditorPlace = placeArgumentCaptor.getValue();
        Assert.assertEquals("mortgagesUuid",
                moduleEditorPlace.getUuid());
    }

    @Test
    public void testSomeOtherModuleSelected() throws Exception {

        setUpPresenter();

        presenter.onModuleSelected( new ModuleEditorPlace( "defaultUuid" ) );

        ArgumentCaptor<ModuleEditorPlace> placeArgumentCaptor = ArgumentCaptor.forClass( ModuleEditorPlace.class );

        Mockito.verify(placeController).goTo( placeArgumentCaptor.capture() );

        ModuleEditorPlace moduleEditorPlace = placeArgumentCaptor.getValue();
        Assert.assertEquals("defaultUuid",
                moduleEditorPlace.getUuid());
    }

    @Test
    public void testSelectedModuleCanNotBeTheRootOne() throws Exception {

        packageConfigDatas = new Module[0];
        setUpPresenter();

        presenter.onModuleSelected( null );

        Mockito.verify(placeController,
                Mockito.never()).goTo( Matchers.any(Place.class) );
    }

    @Test
    public void testOpenFormatsPlace() throws Exception {

        setUpPresenter();

        Module packageConfigData = new Module( "default" );
        packageConfigData.setUuid( "defaultUuid" );

        presenter.onModuleSelected(
                new ModuleFormatsGridPlace(
                                       packageConfigData,
                                       "Rules",
                                       new String[]{AssetFormats.DRL, AssetFormats.BUSINESS_RULE} ) );

        ArgumentCaptor<ModuleFormatsGridPlace> moduleFormatsArgumentCaptor = ArgumentCaptor.forClass( ModuleFormatsGridPlace.class );
        Mockito.verify(placeController).goTo( moduleFormatsArgumentCaptor.capture() );
        ModuleFormatsGridPlace moduleFormatsGridPlace = moduleFormatsArgumentCaptor.getValue();

        assertEquals( "defaultUuid",
                      moduleFormatsGridPlace.getPackageConfigData().getUuid() );
        assertEquals( "default",
                      moduleFormatsGridPlace.getPackageConfigData().getName() );
        assertEquals( "Rules",
                      moduleFormatsGridPlace.getTitle() );
        assertContains( moduleFormatsGridPlace.getFormats(),
                        AssetFormats.DRL );
        assertContains( moduleFormatsGridPlace.getFormats(),
                        AssetFormats.BUSINESS_RULE );
    }

    @Test
    public void testRefreshTreeAfterModuleRename() throws Exception {
        setUpDefaultModule( "default" );
        setUpPresenter();

        Mockito.verify(view).addModuleTreeSelectableItem( modulesTreeItem,
                                                    "default" );

        ArgumentCaptor<RefreshModuleListEventHandler> refreshModuleListEventHandlerArgumentCaptor = ArgumentCaptor.forClass( RefreshModuleListEventHandler.class );
        Mockito.verify(eventBus).addHandler(
                                       Matchers.eq(RefreshModuleListEvent.TYPE),
                                       refreshModuleListEventHandlerArgumentCaptor.capture() );
        RefreshModuleListEventHandler refreshModuleListEventHandler = refreshModuleListEventHandlerArgumentCaptor.getValue();

        setUpDefaultModule( "newName" );

        refreshModuleListEventHandler.onRefreshList( new RefreshModuleListEvent() );

        Mockito.verify(view,
                Mockito.atLeastOnce()).clearModulesTreeItem();
        Mockito.verify(view,
                Mockito.times(2)).addModulesTreeItem();
        Mockito.verify(view).addModuleTreeSelectableItem( modulesTreeItem,
                                                    "newName" );
    }

    private IsTreeItem setUpDefaultModule(String moduleName) {
        ArrayList<Module> firstLevelDatas = new ArrayList<Module>();
        Module mortgageConfigData = new Module( moduleName );
        mortgageConfigData.setUuid( "defaultUuid" );
        firstLevelDatas.add( mortgageConfigData );
        packageConfigDatas = firstLevelDatas.toArray( new Module[firstLevelDatas.size()] );

        IsTreeItem defaultRootTreeItem = Mockito.mock(IsTreeItem.class);
        Mockito.when(
                view.addModuleTreeItem(modulesTreeItem,
                        "default")).thenReturn(
                                                                  defaultRootTreeItem
                );
        return defaultRootTreeItem;
    }

    private void assertContains(String[] formats,
                                String expectedFormat) {
        for ( String format : formats ) {
            if ( format.equals( expectedFormat ) ) {
                return;
            }
        }
        Assert.fail("Format " + expectedFormat + " was expected, but not found.");
    }

    class PackageServiceAsyncMockImpl extends ModuleServiceAsyncMock {

        public void listModules(AsyncCallback<Module[]> cb) {
            cb.onSuccess( packageConfigDatas );
        }

    }
}
