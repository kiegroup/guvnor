package org.jboss.bpm.console.client.navigation.modules;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.MenuBar;

import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.jboss.bpm.console.client.navigation.NavigationViewFactory;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

public class ModulesTreeTest {

    private ModulesTreeView view;
   ;

    @Before
    public void setUp() throws Exception {
        view = Mockito.mock(ModulesTreeView.class);
    }

    @Test
    public void testNewAssetMenuIsSet() throws Exception {
        setUpPresenter();

        Mockito.verify(view).setNewAssetMenu( (Matchers.any(MenuBar.class)) );
    }

    @Test
    public void testNewAssetMenuIsNotSet() throws Exception {
        setUpUserCapabilities( false );

        createPresenter();

        Mockito.verify(view, Mockito.never()).setNewAssetMenu( (Matchers.any(MenuBar.class)) );
    }

    @Test
    public void testRootItemsAreSet() throws Exception {
        setUpPresenter();

        Mockito.verify(view).setGlobalAreaTreeItem( Matchers.<GlobalAreaTreeItem>any() );
        Mockito.verify(view).setModulesTreeItem( Matchers.<ModulesTreeItem>any() );
    }

    private void setUpPresenter() {
        setUpUserCapabilities( true );
        createPresenter();
    }

    private void createPresenter() {
        ClientFactory clientFactory = Mockito.mock(ClientFactory.class);

        NavigationViewFactory navigationViewFactory = Mockito.mock(NavigationViewFactory.class);
        Mockito.when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );

        Mockito.when(
                navigationViewFactory.getModulesTreeView()
        ).thenReturn(
                view
        );

        ModulesTreeItemView knowledgeModulesTreeItemView = Mockito.mock(ModulesTreeItemView.class);
        Mockito.when(
                navigationViewFactory.getModulesTreeItemView()
        ).thenReturn(
                knowledgeModulesTreeItemView
        );

        ModuleServiceAsync packageService = Mockito.mock(ModuleServiceAsync.class);
        Mockito.when(
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
        
        GlobalAreaTreeItemView globalAreaTreeItemView = Mockito.mock(GlobalAreaTreeItemView.class);
        Mockito.when(
                navigationViewFactory.getGlobalAreaTreeItemView()
        ).thenReturn(
                globalAreaTreeItemView
        );
        EventBus eventBus = Mockito.mock(EventBus.class);

        new ModulesTree( clientFactory ,eventBus, "AuthorPerspective");
    }

    private void setUpUserCapabilities( boolean canMakeNewAssets ) {
        UserCapabilities userCapabilities = Mockito.mock(UserCapabilities.class);
        UserCapabilities.INSTANCE = userCapabilities;
        Mockito.when(userCapabilities.hasCapability(Capability.SHOW_CREATE_NEW_ASSET)).thenReturn( canMakeNewAssets );
    }
}
