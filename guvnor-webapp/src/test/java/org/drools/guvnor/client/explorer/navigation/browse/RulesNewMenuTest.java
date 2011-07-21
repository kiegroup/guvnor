package org.drools.guvnor.client.explorer.navigation.browse;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RulesNewMenuTest {


    private RulesNewMenuView view;
    private RulesNewMenuView.Presenter presenter;
    private ClientFactory clientFactory;

    @Before
    public void setUp() throws Exception {
        view = mock( RulesNewMenuView.class );
        clientFactory = mock( ClientFactory.class );
        NavigationViewFactory navigationViewFactory = mock( NavigationViewFactory.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        when(
                navigationViewFactory.getRulesNewMenuView()
        ).thenReturn(
                view
        );
        presenter = new RulesNewMenu( clientFactory );
    }

    @Test
    public void testPresenterIsSet() throws Exception {
        verify( view ).setPresenter( presenter );
    }

    @Test
    public void testOpenNewEditorBusinessRuleGuidedEditor() throws Exception {
        presenter.onOpenWizard( AssetFormats.BUSINESS_RULE, true );
        verify( view ).launchWizard( AssetFormats.BUSINESS_RULE, true, clientFactory );
    }
}
