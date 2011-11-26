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

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
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
        NavigationViewFactory navigationViewFactory = setUpNavigationFactory();

        view = mock( ModuleEditorActivityView.class );
        when(
                clientFactory.getNavigationViewFactory().getModuleEditorActivityView()
        ).thenReturn(
                view
        );
        moduleEditorActivity = new ModuleEditorActivity( "mockUuid", clientFactory );
    }

    private NavigationViewFactory setUpNavigationFactory() {
        NavigationViewFactory navigationViewFactory = mock( NavigationViewFactory.class );
        when(
                clientFactory.getNavigationViewFactory()
        ).thenReturn(
                navigationViewFactory
        );
        return navigationViewFactory;
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
