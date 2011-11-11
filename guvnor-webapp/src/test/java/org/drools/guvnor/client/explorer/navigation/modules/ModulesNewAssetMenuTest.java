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

package org.drools.guvnor.client.explorer.navigation.modules;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.NavigationViewFactory;
import org.drools.guvnor.client.rpc.PackageServiceAsync;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ModulesNewAssetMenuTest {

//    private ModulesNewAssetMenuView view;
//    private Presenter presenter;
//    private PackageServiceAsync packageService;
//    private ClientFactory clientFactory;
//
//    @Before
//    public void setUp() throws Exception {
//        view = mock( ModulesNewAssetMenuView.class );
//        packageService = mock( PackageServiceAsync.class );
//
//        clientFactory = mock( ClientFactory.class );
//        NavigationViewFactory navigationViewFactory = mock( NavigationViewFactory.class );
//        when(
//                clientFactory.getNavigationViewFactory()
//        ).thenReturn(
//                navigationViewFactory
//        );
//        when(
//                navigationViewFactory.getModulesNewAssetMenuView()
//        ).thenReturn(
//                view
//        );
//        when(
//                clientFactory.getPackageService()
//        ).thenReturn(
//                packageService
//        );
//
//        presenter = new ModulesNewAssetMenu( clientFactory );
//    }

    @Test
    public void testMock() throws Exception {
        assertTrue(true);
    }

//    @Test
//    public void testIsPresenterSet() throws Exception {
//        verify( view ).setPresenter( presenter );
//    }

//    @Test
//    public void testNewModule() throws Exception {
//        presenter.onNewModule();
//        verify( view ).openNewPackageWizard( clientFactory );
//    }
//
//    @Test
//    public void testNewSpringContext() throws Exception {
//        presenter.onNewSpringContext();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.SPRING_CONTEXT );
//    }
//
//    @Test
//    public void testNewWorkingSet() throws Exception {
//        presenter.onNewWorkingSet();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.WORKING_SET );
//    }
//
//    @Test
//    public void testNewRule() throws Exception {
//        presenter.onNewRule();
//        verify( view ).openNewAssetWizardWithCategories( null );
//    }
//
//    @Test
//    public void testNewRuleTemplate() throws Exception {
//        presenter.onNewRuleTemplate();
//        verify( view ).openNewAssetWizardWithCategories( AssetFormats.RULE_TEMPLATE );
//    }
//
//    @Test
//    public void testNewPojoModel() throws Exception {
//        presenter.onNewPojoModel();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.MODEL );
//    }
//
//    @Test
//    public void testNewDeclarativeModel() throws Exception {
//        presenter.onNewDeclarativeModel();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.DRL_MODEL );
//    }
//
//    @Test
//    public void testNewBPELPackage() throws Exception {
//        presenter.onNewBPELPackage();
//        verify( view ).openNewAssetWizardWithCategories( AssetFormats.BPEL_PACKAGE );
//    }
//
//    @Test
//    public void testNewFunction() throws Exception {
//        presenter.onNewFunction();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.FUNCTION );
//    }
//
//    @Test
//    public void testNewDSL() throws Exception {
//        presenter.onNewDSL();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.DSL );
//    }
//
//    @Test
//    public void testNewRuleFlow() throws Exception {
//        presenter.onNewRuleFlow();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.RULE_FLOW_RF );
//    }
//
//    @Test
//    public void testNewBPMN2Process() throws Exception {
//        presenter.onNewBPMN2Process();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.BPMN2_PROCESS );
//    }
//
//    @Test
//    public void testNewWorkitemDefinition() throws Exception {
//        presenter.onNewWorkitemDefinition();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.WORKITEM_DEFINITION );
//    }
//
//    @Test
//    public void testNewEnumeration() throws Exception {
//        presenter.onNewEnumeration();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.ENUMERATION );
//    }
//
//    @Test
//    public void testNewTestScenario() throws Exception {
//        presenter.onNewTestScenario();
//        verify( view ).openNewAssetWizardWithoutCategories( AssetFormats.TEST_SCENARIO );
//    }
//
//    @Test
//    public void testNewFile() throws Exception {
//        presenter.onNewFile();
//        verify( view ).openNewAssetWizardWithoutCategories( "*" );
//    }
//
//    @Test
//    public void testRebuildAllPackages() throws Exception {
//        presenter.onRebuildAllPackages();
//        verify( view ).confirmRebuild();
//        presenter.onRebuildConfirmed();
//        verify( view ).showLoadingPopUpRebuildingPackageBinaries();
//
//        ArgumentCaptor<AsyncCallback> argumentCaptor = ArgumentCaptor.forClass( AsyncCallback.class );
//
//        verify( packageService ).rebuildPackages( argumentCaptor.capture() );
//
//        argumentCaptor.getValue().onSuccess( null );
//
//        verify( view ).closeLoadingPopUp();
//    }
}
