package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.common.AssetEditorFactory;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.navigation.ModuleFormatsGrid;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.explorer.navigation.modules.ModulesTreeItemBaseView.Presenter;
import org.drools.guvnor.client.packages.RefreshModuleListEvent;
import org.drools.guvnor.client.packages.RefreshModuleListEventHandler;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PackageServiceAsyncMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class KnowledgeModulesTreeItemTest {

    private KnowledgeModulesTreeItemView view;
    private Presenter presenter;
    private PackageConfigData[] packageConfigDatas = new PackageConfigData[0];
    private PlaceController placeController;
    private IsTreeItem modulesTreeItem;
    private ClientFactory clientFactory;
    private EventBus eventBus;

    @Before
    public void setUp() throws Exception {
        view = mock( KnowledgeModulesTreeItemView.class );

        modulesTreeItem = mock( IsTreeItem.class );
        when( view.addModulesTreeItem() ).thenReturn( modulesTreeItem );

        clientFactory = mock( ClientFactory.class );
        placeController = mock( PlaceController.class );
        when(
                clientFactory.getPlaceController()
        ).thenReturn(
                placeController
        );

        when(
                clientFactory.getPackageService()
        ).thenReturn(
                new PackageServiceAsyncMockImpl()
        );


        NavigationViewFactory navigationViewFactory = mock( NavigationViewFactory.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );

        when(
                navigationViewFactory.getKnowledgeModulesTreeItemView()
        ).thenReturn(
                view
        );

        ModuleTreeItemView moduleTreeItemView = mock( ModuleTreeItemView.class );
        when(
                navigationViewFactory.getModuleTreeItemView()
        ).thenReturn(
                moduleTreeItemView
        );

        AssetEditorFactory assetEditorFactory = mock( AssetEditorFactory.class );
        when(
                clientFactory.getAssetEditorFactory()
        ).thenReturn(
                assetEditorFactory
        );
        when(
                assetEditorFactory.getRegisteredAssetEditorFormats()
        ).thenReturn(
                new String[0]
        );

        eventBus = mock( EventBus.class );
        when(
                clientFactory.getEventBus()
        ).thenReturn(
                eventBus
        );
    }

    private void setUpPresenter() {
        presenter = new KnowledgeModulesTreeItem( clientFactory );
    }

    @Test
    public void testAddModulesNoModulesExist() throws Exception {

        packageConfigDatas = new PackageConfigData[0];
        setUpPresenter();

        verify( view ).setPresenter( presenter );
        verify( view ).addModulesTreeItem();
        verify( view, never() ).addModuleTreeItem( eq( modulesTreeItem ), anyString() );
    }

    @Test
    public void testAddModules() throws Exception {

        ArrayList<PackageConfigData> firstLevelDatas = new ArrayList<PackageConfigData>();
        firstLevelDatas.add( new PackageConfigData( "defaultPackage" ) );
        PackageConfigData mortgageConfigData = new PackageConfigData( "mortgage" );

        ArrayList<PackageConfigData> secondLevelDatas = new ArrayList<PackageConfigData>();
        secondLevelDatas.add( new PackageConfigData( "sub1" ) );
        PackageConfigData thirdLevelConfigDataParent = new PackageConfigData( "sub2" );
        secondLevelDatas.add( thirdLevelConfigDataParent );
        ArrayList<PackageConfigData> thirdLevelDatas = new ArrayList<PackageConfigData>();
        thirdLevelDatas.add( new PackageConfigData( "3rdLevel" ) );
        thirdLevelConfigDataParent.setSubPackages( thirdLevelDatas.toArray( new PackageConfigData[thirdLevelDatas.size()] ) );
        secondLevelDatas.add( new PackageConfigData( "sub3" ) );
        mortgageConfigData.setSubPackages( secondLevelDatas.toArray( new PackageConfigData[secondLevelDatas.size()] ) );
        firstLevelDatas.add( mortgageConfigData );

        packageConfigDatas = firstLevelDatas.toArray( new PackageConfigData[firstLevelDatas.size()] );

        IsTreeItem mortgagesRootTreeItem = mock( IsTreeItem.class );
        when(
                view.addModuleTreeItem( modulesTreeItem, "mortgage" )
        ).thenReturn(
                mortgagesRootTreeItem
        );

        IsTreeItem thirdLevelParentRootTreeItem = mock( IsTreeItem.class );
        when(
                view.addModuleTreeItem( mortgagesRootTreeItem, "sub2" )
        ).thenReturn(
                thirdLevelParentRootTreeItem
        );

        setUpPresenter();

        verify( view ).addModuleTreeItem( modulesTreeItem, "defaultPackage" );
        verify( view ).addModuleTreeItem( modulesTreeItem, "mortgage" );

        verify( view ).addModuleTreeItem( mortgagesRootTreeItem, "sub1" );
        verify( view ).addModuleTreeItem( mortgagesRootTreeItem, "sub2" );
        verify( view ).addModuleTreeItem( mortgagesRootTreeItem, "sub3" );

        verify( view ).addModuleTreeItem( thirdLevelParentRootTreeItem, "3rdLevel" );
    }

    @Test
    public void testModuleSelected() throws Exception {

        setUpPresenter();

        presenter.onModuleSelected( new ModuleEditorPlace( "mortgagesUuid" ) );

        ArgumentCaptor<ModuleEditorPlace> placeArgumentCaptor = ArgumentCaptor.forClass( ModuleEditorPlace.class );

        verify( placeController ).goTo( placeArgumentCaptor.capture() );

        ModuleEditorPlace moduleEditorPlace = placeArgumentCaptor.getValue();
        assertEquals( "mortgagesUuid", moduleEditorPlace.getUuid() );
    }

    @Test
    public void testSomeOtherModuleSelected() throws Exception {

        setUpPresenter();

        presenter.onModuleSelected( new ModuleEditorPlace( "defaultUuid" ) );

        ArgumentCaptor<ModuleEditorPlace> placeArgumentCaptor = ArgumentCaptor.forClass( ModuleEditorPlace.class );

        verify( placeController ).goTo( placeArgumentCaptor.capture() );

        ModuleEditorPlace moduleEditorPlace = placeArgumentCaptor.getValue();
        assertEquals( "defaultUuid", moduleEditorPlace.getUuid() );
    }

    @Test
    public void testSelectedModuleCanNotBeTheRootOne() throws Exception {

        packageConfigDatas = new PackageConfigData[0];
        setUpPresenter();

        presenter.onModuleSelected( null );

        verify( placeController, never() ).goTo( any( Place.class ) );
    }

    @Test
    public void testOpenFormatsPlace() throws Exception {

        setUpPresenter();

        PackageConfigData packageConfigData = new PackageConfigData( "default" );
        packageConfigData.setUuid( "defaultUuid" );

        presenter.onModuleSelected(
                new ModuleFormatsGrid(
                        packageConfigData,
                        "Rules",
                        new String[]{AssetFormats.DRL, AssetFormats.BUSINESS_RULE} ) );

        ArgumentCaptor<ModuleFormatsGrid> moduleFormatsArgumentCaptor = ArgumentCaptor.forClass( ModuleFormatsGrid.class );
        verify( placeController ).goTo( moduleFormatsArgumentCaptor.capture() );
        ModuleFormatsGrid moduleFormatsGrid = moduleFormatsArgumentCaptor.getValue();

        assertEquals( "defaultUuid", moduleFormatsGrid.getPackageConfigData().getUuid() );
        assertEquals( "default", moduleFormatsGrid.getPackageConfigData().getName() );
        assertEquals( "Rules", moduleFormatsGrid.getTitle() );
        assertContains( moduleFormatsGrid.getFormats(), AssetFormats.DRL );
        assertContains( moduleFormatsGrid.getFormats(), AssetFormats.BUSINESS_RULE );
    }

    @Test
    public void testRefreshTreeAfterModuleRename() throws Exception {
        setUpDefaultModule( "default" );
        setUpPresenter();

        verify( view ).addModuleTreeItem( modulesTreeItem, "default" );

        ArgumentCaptor<RefreshModuleListEventHandler> refreshModuleListEventHandlerArgumentCaptor = ArgumentCaptor.forClass( RefreshModuleListEventHandler.class );
        verify( eventBus ).addHandler(
                eq( RefreshModuleListEvent.TYPE ),
                refreshModuleListEventHandlerArgumentCaptor.capture() );
        RefreshModuleListEventHandler refreshModuleListEventHandler = refreshModuleListEventHandlerArgumentCaptor.getValue();

        setUpDefaultModule( "newName" );

        refreshModuleListEventHandler.onRefreshList( new RefreshModuleListEvent() );

        verify( view, atLeastOnce() ).clearModulesTreeItem();
        verify( view, times( 2 ) ).addModulesTreeItem();
        verify( view ).addModuleTreeItem( modulesTreeItem, "newName" );
    }

    private IsTreeItem setUpDefaultModule( String moduleName ) {
        ArrayList<PackageConfigData> firstLevelDatas = new ArrayList<PackageConfigData>();
        PackageConfigData mortgageConfigData = new PackageConfigData( moduleName );
        mortgageConfigData.setUuid( "defaultUuid" );
        firstLevelDatas.add( mortgageConfigData );
        packageConfigDatas = firstLevelDatas.toArray( new PackageConfigData[firstLevelDatas.size()] );

        IsTreeItem defaultRootTreeItem = mock( IsTreeItem.class );
        when(
                view.addModuleTreeItem( modulesTreeItem, "default" )
        ).thenReturn(
                defaultRootTreeItem
        );
        return defaultRootTreeItem;
    }

    private void assertContains( String[] formats, String expectedFormat ) {
        for (String format : formats) {
            if ( format.equals( expectedFormat ) ) {
                return;
            }
        }
        fail( "Format " + expectedFormat + " was expected, but not found." );
    }


    class PackageServiceAsyncMockImpl extends PackageServiceAsyncMock {

        public void listPackages( AsyncCallback<PackageConfigData[]> cb ) {
            cb.onSuccess( packageConfigDatas );
        }
    }
}
