package org.drools.guvnor.client.explorer;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ModuleEditorActivityTest {

    private ClientFactory clientFactory;
    private ModuleEditorActivity moduleEditorActivity;
    private ModuleEditorActivityView view;

    @Before
    public void setUp() throws Exception {
        clientFactory = mock( ClientFactory.class );
        view = mock( ModuleEditorActivityView.class );
        when(
                clientFactory.getModuleEditorActivityView()
        ).thenReturn(
                view
        );
        moduleEditorActivity = new ModuleEditorActivity( "mockUuid", clientFactory );
    }

    @Test
    public void testStart() throws Exception {
        verify( view ).showLoadingPackageInformationMessage();
    }

    // TODO: Test setWidget -Rikkola-
}
