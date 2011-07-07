package org.drools.guvnor.client.explorer.navigation;

import org.junit.Before;

import static org.mockito.Mockito.mock;

public class GlobalAreaTreeItemTest {

    private GlobalAreaTreeItemView view;

    @Before
    public void setUp() throws Exception {
        view = mock( GlobalAreaTreeItemView.class );
        new GlobalAreaTreeItem( view );
    }
}
