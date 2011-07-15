package org.drools.guvnor.client.explorer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void testMock() throws Exception {
        assertTrue( true );
    }

    //    @Test
//    public void testStart() throws Exception {
//        AcceptsOneWidget acceptsOneWidget = mock( AcceptsOneWidget.class );
//        EventBus eventBus = mock( EventBus.class );
//
//        PackageServiceAsync packageService = mock( PackageServiceAsync.class );
//        when(
//                clientFactory.getPackageService()
//        ).thenReturn(
//                packageService
//        );
//
//        moduleEditorActivity.start( acceptsOneWidget, eventBus );
//
//
//        verify( view ).showLoadingPackageInformationMessage();
//
//        ArgumentCaptor<GenericCallback> packageConfigDataArgumentCaptor = ArgumentCaptor.forClass( GenericCallback.class );
//        verify( packageService ).loadPackageConfig( eq( "mockUuid" ), packageConfigDataArgumentCaptor.capture() );
//
//        GenericCallback<PackageConfigData> value = (GenericCallback<PackageConfigData>) packageConfigDataArgumentCaptor.getValue();
//
//        PackageConfigData packageConfigData = new PackageConfigData();
//
//        value.onSuccess( packageConfigData );
//
//        verify( acceptsOneWidget ).setWidget( Matchers.<PackageEditorWrapper>any() );
//
    // TODO: Make currentlySelectedPackage better for testing -Rikkola-
//        assertEquals( "mockUuid", RulePackageSelector.currentlySelectedPackage );
//
//    }

}
