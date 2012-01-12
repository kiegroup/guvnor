package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.MenuBar;

import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

public class ModulesTreeTest {

    private ModulesTreeView view;
    private ModulesTree presenter;

    @Before
    public void setUp() throws Exception {
        view = mock( ModulesTreeView.class );
    }

    @Test
    public void testNewAssetMenuIsSet() throws Exception {
        setUpPresenter();

        verify( view ).setNewAssetMenu( (any( MenuBar.class )) );
    }

    @Test
    public void testNewAssetMenuIsNotSet() throws Exception {
        setUpUserCapabilities( false );

        createPresenter();

        verify( view, never() ).setNewAssetMenu( (any( MenuBar.class )) );
    }

    @Test
    public void testRootItemsAreSet() throws Exception {
        setUpPresenter();

        verify( view ).setGlobalAreaTreeItem( Matchers.<GlobalAreaTreeItem>any() );
        verify( view ).setModulesTreeItem( Matchers.<ModulesTreeItem>any() );
    }

    private void setUpPresenter() {
        setUpUserCapabilities( true );
        createPresenter();
    }

    private void createPresenter() {
        ClientFactory clientFactory = mock( ClientFactory.class );

        NavigationViewFactory navigationViewFactory = mock( NavigationViewFactory.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );

        when(
                navigationViewFactory.getModulesTreeView()
        ).thenReturn(
                view
        );

        ModulesTreeItemView knowledgeModulesTreeItemView = mock( ModulesTreeItemView.class );
        when(
                navigationViewFactory.getModulesTreeItemView()
        ).thenReturn(
                knowledgeModulesTreeItemView
        );

        ModuleServiceAsync packageService = mock( ModuleServiceAsync.class );
        when(
                clientFactory.getModuleService()
        ).thenReturn(
                packageService
        );

/*        
        MenuBar rootMenuBar = new MenuBar( true ); 
        when(
                modulesNewAssetMenuView.asWidget()
        ).thenReturn(
                rootMenuBar
        );*/
        
        GlobalAreaTreeItemView globalAreaTreeItemView = mock( GlobalAreaTreeItemView.class );
        when(
                navigationViewFactory.getGlobalAreaTreeItemView()
        ).thenReturn(
                globalAreaTreeItemView
        );
        EventBus eventBus = mock( EventBus.class );

        presenter = new ModulesTree( clientFactory ,eventBus, "AuthorPerspective");
    }

    private void setUpUserCapabilities( boolean canMakeNewAssets ) {
        UserCapabilities userCapabilities = mock( UserCapabilities.class );
        UserCapabilities.INSTANCE = userCapabilities;
        when( userCapabilities.hasCapability( Capability.SHOW_CREATE_NEW_ASSET ) ).thenReturn( canMakeNewAssets );
    }
}
