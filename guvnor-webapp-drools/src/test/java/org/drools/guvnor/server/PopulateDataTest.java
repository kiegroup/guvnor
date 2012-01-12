/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.files.FileManagerService;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.junit.Test;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * This class will setup the data in a test state, which is
 * good for screenshots/playing around.
 *
 * If you run this by itself, the database will be wiped, and left with only this data in it.
 * If it is run as part of the suite, it will just augment the data.
 *
 * This sets up the data for a fictional company Billasurf, dealing with surfwear and equipment
 * (for surfing, boarding etc).
 */
public class PopulateDataTest extends GuvnorTestBase {

    @Inject
    private FileManagerService fileManagerService;

    @Test
    public void testPopulate() throws Exception {
        createCategories();
        createStates();
        createPackages();
        createModel();

        createSomeRules();

        createPackageSnapshots();

        createPermissions();

        ModuleItem pkg = rulesRepository.loadModule("com.billasurf.manufacturing.plant");
        repositoryPackageService.buildPackage( pkg.getUUID(),
                                               true );
    }

    private void createPermissions() {
        Map<String, List<String>> perms = new HashMap<String, List<String>>();
        perms.put( RoleType.ADMIN.getName(),
                   new ArrayList<String>() );
        serviceImplementation.updateUserPermissions("woozle1",
                perms);

        perms = new HashMap<String, List<String>>();
        List<String> targets = new ArrayList<String>();
        targets.add( "category=/foo/bar" );
        targets.add( "category=/whee" );
        perms.put( RoleType.ANALYST.getName(),
                   targets );
        serviceImplementation.updateUserPermissions("woozle2",
                perms);

    }

    private void createModel() throws Exception {
        String uuid = serviceImplementation.createNewRule( "DomainModel",
                                          "This is the business object model",
                                          null,
                                          "com.billasurf.manufacturing.plant",
                                          AssetFormats.MODEL );
        InputStream file = this.getClass().getResourceAsStream( "/billasurf.jar" );
        assertNotNull(file);

        fileManagerService.attachFileToAsset(uuid,
                file,
                "billasurf.jar");

        AssetItem item = rulesRepository.loadAssetByUUID( uuid );
        assertNotNull(item.getBinaryContentAsBytes());
        assertEquals(item.getBinaryContentAttachmentFileName(),
                "billasurf.jar");

        ModuleItem pkg = rulesRepository.loadModule( "com.billasurf.manufacturing.plant" );
        DroolsHeader.updateDroolsHeader("import com.billasurf.Board\nimport com.billasurf.Person" + "\n\nglobal com.billasurf.Person prs",
                pkg);
        pkg.checkin( "added imports" );

        SuggestionCompletionEngine eng = serviceImplementation.loadSuggestionCompletionEngine( "com.billasurf.manufacturing.plant" );
        assertNotNull( eng );

        //The loader could define extra imports
        assertTrue( eng.getFactTypes().length >= 2 );
        String[] fields = (String[]) eng.getModelFields( "Board" );
        assertTrue( fields.length >= 3 );

        String[] globalVars = eng.getGlobalVariables();
        assertEquals( 1,
                      globalVars.length );
        assertEquals( "prs",
                      globalVars[0] );
        assertTrue( eng.getFieldCompletionsForGlobalVariable( "prs" ).length >= 2 );

        fields = (String[]) eng.getModelFields( "Person" );

        assertTrue( fields.length >= 2 );

    }

    private void createPackageSnapshots() {
        repositoryPackageService.createModuleSnapshot("com.billasurf.manufacturing",
                "TEST",
                false,
                "The testing region.");
        repositoryPackageService.createModuleSnapshot("com.billasurf.manufacturing",
                "PRODUCTION",
                false,
                "The testing region.");
        repositoryPackageService.createModuleSnapshot("com.billasurf.manufacturing",
                "PRODUCTION ROLLBACK",
                false,
                "The testing region.");

    }

    private void createSomeRules() throws SerializationException {
        String uuid = serviceImplementation.createNewRule( "Surfboard_Colour_Combination",
                                          "allowable combinations for basic boards.",
                                          "Manufacturing/Boards",
                                          "com.billasurf.manufacturing",
                                          AssetFormats.BUSINESS_RULE );
        repositoryAssetService.changeState(uuid,
                "Pending");
        uuid = serviceImplementation.createNewRule( "Premium_Colour_Combinations",
                                   "This defines XXX.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.BUSINESS_RULE );
        repositoryAssetService.changeState(uuid,
                "Approved");
        uuid = serviceImplementation.createNewRule( "Fibreglass supplier selection",
                                   "This defines XXX.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.BUSINESS_RULE );
        uuid = serviceImplementation.createNewRule( "Recommended wax",
                                   "This defines XXX.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.BUSINESS_RULE );
        uuid = serviceImplementation.createNewRule( "SomeDSL",
                                   "Ignore me.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.DSL );
    }

    private void createPackages() throws SerializationException {
        String uuid = repositoryPackageService.createModule( "com.billasurf.manufacturing",
                                          "Rules for manufacturing.",
                                          "package");

        Module conf = repositoryPackageService.loadModule( uuid );
        conf.setHeader("import com.billasurf.manuf.materials.*");
        repositoryPackageService.saveModule( conf );

        repositoryPackageService.createModule("com.billasurf.manufacturing.plant",
                "Rules for manufacturing plants.",
                "package");
        repositoryPackageService.createModule("com.billasurf.finance",
                "All financial rules.",
                "package");
        repositoryPackageService.createModule("com.billasurf.hrman",
                "Rules for in house HR application.",
                "package");
        repositoryPackageService.createModule("com.billasurf.sales",
                "Rules exposed as a service for pricing, and discounting.",
                "package");

    }

    private void createStates() throws SerializationException {
        serviceImplementation.createState("Approved");
        serviceImplementation.createState("Pending");
    }

    private void createCategories() {
        repositoryCategoryService.createCategory("/",
                "HR",
                "");
        repositoryCategoryService.createCategory("/",
                "Sales",
                "");
        repositoryCategoryService.createCategory("/",
                "Manufacturing",
                "");
        repositoryCategoryService.createCategory("/",
                "Finance",
                "");

        repositoryCategoryService.createCategory("HR",
                "Leave",
                "");
        repositoryCategoryService.createCategory("HR",
                "Training",
                "");
        repositoryCategoryService.createCategory("Sales",
                "Promotions",
                "");
        repositoryCategoryService.createCategory("Sales",
                "Old promotions",
                "");
        repositoryCategoryService.createCategory("Sales",
                "Boogie boards",
                "");
        repositoryCategoryService.createCategory("Sales",
                "Surf boards",
                "");
        repositoryCategoryService.createCategory("Sales",
                "Surf wear",
                "");
        repositoryCategoryService.createCategory("Manufacturing",
                "Surf wear",
                "");
        repositoryCategoryService.createCategory("Manufacturing",
                "Boards",
                "");
        repositoryCategoryService.createCategory("Finance",
                "Employees",
                "");
        repositoryCategoryService.createCategory("Finance",
                "Payables",
                "");
        repositoryCategoryService.createCategory("Finance",
                "Receivables",
                "");
    }

}
