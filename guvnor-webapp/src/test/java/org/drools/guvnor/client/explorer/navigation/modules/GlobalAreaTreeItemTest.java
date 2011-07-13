package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.explorer.navigation.modules.GlobalAreaTreeItem;
import org.drools.guvnor.client.explorer.navigation.modules.GlobalAreaTreeItemView;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.PackageServiceAsync;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

public class GlobalAreaTreeItemTest {

    private GlobalAreaTreeItemView view;
    private GlobalAreaTreeItem presenter;
    private NavigationViewFactory navigationViewFactory;
    private PackageServiceAsync packageService;

    @Before
    public void setUp() throws Exception {
        view = mock( GlobalAreaTreeItemView.class );
        ClientFactory clientFactory = mock( ClientFactory.class );
        navigationViewFactory = mock( NavigationViewFactory.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        when(
                navigationViewFactory.getGlobalAreaTreeItemView()
        ).thenReturn(
                view
        );

        packageService = mock( PackageServiceAsync.class );
        when(
                clientFactory.getPackageService()
        ).thenReturn(
                packageService
        );

        presenter = new GlobalAreaTreeItem( clientFactory );
    }

    @Test
    public void testSetUp() throws Exception {
        verify( navigationViewFactory ).getGlobalAreaTreeItemView();
        verify( view ).setPresenter( presenter );
        verify( packageService ).loadGlobalPackage( Matchers.<AsyncCallback<PackageConfigData>>any() );
    }
}
