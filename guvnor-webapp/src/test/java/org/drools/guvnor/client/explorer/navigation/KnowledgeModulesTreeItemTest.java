package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsTreeItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.ModuleEditorPlace;
import org.drools.guvnor.client.explorer.navigation.KnowledgeModulesTreeItemView.Presenter;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PackageServiceAsyncMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class KnowledgeModulesTreeItemTest {

    private KnowledgeModulesTreeItemView view;
    private Presenter presenter;
    private PackageConfigData[] packageConfigDatas;
    private PlaceController placeController;
    private IsTreeItem modulesTreeItem;
    private ClientFactory clientFactory;

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
        verify( view, never() ).addModulesTreeItem( eq( modulesTreeItem ), anyString() );
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

        IsTreeItem mortgagesTreeItem = mock( IsTreeItem.class );
        when(
                view.addModulesTreeItem( modulesTreeItem, "mortgage" )
        ).thenReturn(
                mortgagesTreeItem
        );

        IsTreeItem thirdLevelParentTreeItem = mock( IsTreeItem.class );
        when(
                view.addModulesTreeItem( mortgagesTreeItem, "sub2" )
        ).thenReturn(
                thirdLevelParentTreeItem
        );

        setUpPresenter();

        verify( view ).addModulesTreeItem( modulesTreeItem, "defaultPackage" );
        verify( view ).addModulesTreeItem( modulesTreeItem, "mortgage" );

        verify( view ).addModulesTreeItem( mortgagesTreeItem, "sub1" );
        verify( view ).addModulesTreeItem( mortgagesTreeItem, "sub2" );
        verify( view ).addModulesTreeItem( mortgagesTreeItem, "sub3" );

        verify( view ).addModulesTreeItem( thirdLevelParentTreeItem, "3rdLevel" );
    }

    @Test
    public void testModuleSelected() throws Exception {

        ArrayList<PackageConfigData> firstLevelDatas = new ArrayList<PackageConfigData>();
        PackageConfigData mortgageConfigData = new PackageConfigData( "mortgage" );
        mortgageConfigData.setUuid( "mortgagesUuid" );
        firstLevelDatas.add( mortgageConfigData );
        packageConfigDatas = firstLevelDatas.toArray( new PackageConfigData[firstLevelDatas.size()] );

        IsTreeItem mortgagesTreeItem = mock( IsTreeItem.class );
        when(
                view.addModulesTreeItem( modulesTreeItem, "mortgage" )
        ).thenReturn(
                mortgagesTreeItem
        );

        setUpPresenter();

        presenter.onModuleSelected( mortgagesTreeItem );

        ArgumentCaptor<ModuleEditorPlace> placeArgumentCaptor = ArgumentCaptor.forClass( ModuleEditorPlace.class );

        verify( placeController ).goTo( placeArgumentCaptor.capture() );

        ModuleEditorPlace moduleEditorPlace = placeArgumentCaptor.getValue();
        assertEquals( "mortgagesUuid", moduleEditorPlace.getUuid() );
    }

    @Test
    public void testSomeOtherModuleSelected() throws Exception {

        ArrayList<PackageConfigData> firstLevelDatas = new ArrayList<PackageConfigData>();
        PackageConfigData mortgageConfigData = new PackageConfigData( "default" );
        mortgageConfigData.setUuid( "defaultUuid" );
        firstLevelDatas.add( mortgageConfigData );
        packageConfigDatas = firstLevelDatas.toArray( new PackageConfigData[firstLevelDatas.size()] );

        IsTreeItem defaultTreeItem = mock( IsTreeItem.class );
        when(
                view.addModulesTreeItem( modulesTreeItem, "default" )
        ).thenReturn(
                defaultTreeItem
        );

        setUpPresenter();

        presenter.onModuleSelected( defaultTreeItem );

        ArgumentCaptor<ModuleEditorPlace> placeArgumentCaptor = ArgumentCaptor.forClass( ModuleEditorPlace.class );

        verify( placeController ).goTo( placeArgumentCaptor.capture() );

        ModuleEditorPlace moduleEditorPlace = placeArgumentCaptor.getValue();
        assertEquals( "defaultUuid", moduleEditorPlace.getUuid() );
    }

    @Test
    public void testSelectedModuleCanNotBeTheRootOne() throws Exception {

        packageConfigDatas = new PackageConfigData[0];
        setUpPresenter();

        presenter.onModuleSelected( modulesTreeItem );

        verify( placeController, never() ).goTo( any( Place.class ) );
    }

    class PackageServiceAsyncMockImpl extends PackageServiceAsyncMock {

        public void listPackages( AsyncCallback<PackageConfigData[]> cb ) {
            cb.onSuccess( packageConfigDatas );
        }
    }
}
