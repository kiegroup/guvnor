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

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.server.files.FileManagerUtils;
import org.drools.guvnor.server.security.RoleType;
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
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

    @Test
    public void testPopulate() throws Exception {
        ServiceImplementation serv = getServiceImplementation();
        RepositoryPackageService repositoryPackageService = getRepositoryPackageService();
        RepositoryAssetService repositoryAssetService = getRepositoryAssetService();
        createCategories( getRepositoryCategoryService() );
        createStates( serv );
        createPackages( repositoryPackageService );
        createModel( serv );

        createSomeRules( serv,
                         repositoryAssetService );
        
        PackageItem pkg = serv.getRulesRepository().loadPackage( "com.billasurf.manufacturing" );
        repositoryPackageService.buildPackage( pkg.getUUID(),
                true );
        
        createPackageSnapshots( repositoryPackageService );

        createPermissions( serv );


        repositoryPackageService.buildPackage( pkg.getUUID(),
                                               true );

    }

    private void createPermissions(ServiceImplementation serv) {
        Map<String, List<String>> perms = new HashMap<String, List<String>>();
        perms.put( RoleType.ADMIN.getName(),
                   new ArrayList<String>() );
        serv.updateUserPermissions( "woozle1",
                                    perms );

        perms = new HashMap<String, List<String>>();
        List<String> targets = new ArrayList<String>();
        targets.add( "category=/foo/bar" );
        targets.add( "category=/whee" );
        perms.put( RoleType.ANALYST.getName(),
                   targets );
        serv.updateUserPermissions( "woozle2",
                                    perms );

    }

    private void createModel(ServiceImplementation serv) throws Exception {
        RulesRepository repo = serv.getRulesRepository();
        String uuid = serv.createNewRule( "DomainModel",
                                          "This is the business object model",
                                          null,
                                          "com.billasurf.manufacturing.plant",
                                          AssetFormats.MODEL );
        InputStream file = this.getClass().getResourceAsStream( "/billasurf.jar" );
        assertNotNull( file );

        FileManagerUtils fm = new FileManagerUtils();
        fm.setRepository( repo );

        fm.attachFileToAsset( uuid,
                              file,
                              "billasurf.jar" );

        AssetItem item = repo.loadAssetByUUID( uuid );
        assertNotNull( item.getBinaryContentAsBytes() );
        assertEquals( item.getBinaryContentAttachmentFileName(),
                      "billasurf.jar" );

        PackageItem pkg = repo.loadPackage( "com.billasurf.manufacturing.plant" );
        DroolsHeader.updateDroolsHeader( "import com.billasurf.Board\nimport com.billasurf.Person" + "\n\nglobal com.billasurf.Person prs",
                                                  pkg );
        pkg.checkin( "added imports" );

        SuggestionCompletionEngine eng = serv.loadSuggestionCompletionEngine( "com.billasurf.manufacturing.plant" );
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

    private void createPackageSnapshots(RepositoryPackageService serv) throws SerializationException {
        serv.createPackageSnapshot( "com.billasurf.manufacturing",
                                    "TEST",
                                    false,
                                    "The testing region." );
        serv.createPackageSnapshot( "com.billasurf.manufacturing",
                                    "PRODUCTION",
                                    false,
                                    "The testing region." );
        serv.createPackageSnapshot( "com.billasurf.manufacturing",
                                    "PRODUCTION ROLLBACK",
                                    false,
                                    "The testing region." );

    }

    private void createSomeRules(ServiceImplementation serv,
                                 RepositoryAssetService repositoryAssetService) throws SerializationException {
        String uuid = serv.createNewRule( "Surfboard_Colour_Combination",
                                          "allowable combinations for basic boards.",
                                          "Manufacturing/Boards",
                                          "com.billasurf.manufacturing",
                                          AssetFormats.BUSINESS_RULE );
        repositoryAssetService.changeState( uuid,
                                            "Pending");
        uuid = serv.createNewRule( "Premium_Colour_Combinations",
                                   "This defines XXX.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.BUSINESS_RULE );
        repositoryAssetService.changeState( uuid,
                                            "Approved");
        uuid = serv.createNewRule( "Fibreglass supplier selection",
                                   "This defines XXX.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.BUSINESS_RULE );
        uuid = serv.createNewRule( "Recommended wax",
                                   "This defines XXX.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.BUSINESS_RULE );
        uuid = serv.createNewRule( "SomeDSL",
                                   "Ignore me.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.DSL );
    }

    private void createPackages(RepositoryPackageService serv) throws SerializationException {
        String uuid = serv.createPackage( "com.billasurf.manufacturing",
                                          "Rules for manufacturing.",
                                          "package");

        PackageConfigData conf = serv.loadPackageConfig( uuid );
        conf.setHeader( "import com.billasurf.manuf.materials.*" );
        serv.savePackage( conf );

        serv.createPackage( "com.billasurf.manufacturing.plant",
                            "Rules for manufacturing plants." ,
                            "package");
        serv.createPackage( "com.billasurf.finance",
                            "All financial rules." ,
                            "package");
        serv.createPackage( "com.billasurf.hrman",
                            "Rules for in house HR application." ,
                            "package");
        serv.createPackage( "com.billasurf.sales",
                            "Rules exposed as a service for pricing, and discounting." ,
                            "package");

    }

    private void createStates(ServiceImplementation serv) throws SerializationException {
        serv.createState( "Approved" );
        serv.createState( "Pending" );
    }

    private void createCategories(RepositoryCategoryService repositoryCategoryService) {
        repositoryCategoryService.createCategory( "/",
                                                  "HR",
                                                  "" );
        repositoryCategoryService.createCategory( "/",
                                                  "Sales",
                                                  "" );
        repositoryCategoryService.createCategory( "/",
                                                  "Manufacturing",
                                                  "" );
        repositoryCategoryService.createCategory( "/",
                                                  "Finance",
                                                  "" );

        repositoryCategoryService.createCategory( "HR",
                                                  "Leave",
                                                  "" );
        repositoryCategoryService.createCategory( "HR",
                                                  "Training",
                                                  "" );
        repositoryCategoryService.createCategory( "Sales",
                                                  "Promotions",
                                                  "" );
        repositoryCategoryService.createCategory( "Sales",
                                                  "Old promotions",
                                                  "" );
        repositoryCategoryService.createCategory( "Sales",
                                                  "Boogie boards",
                                                  "" );
        repositoryCategoryService.createCategory( "Sales",
                                                  "Surf boards",
                                                  "" );
        repositoryCategoryService.createCategory( "Sales",
                                                  "Surf wear",
                                                  "" );
        repositoryCategoryService.createCategory( "Manufacturing",
                                                  "Surf wear",
                                                  "" );
        repositoryCategoryService.createCategory( "Manufacturing",
                                                  "Boards",
                                                  "" );
        repositoryCategoryService.createCategory( "Finance",
                                                  "Employees",
                                                  "" );
        repositoryCategoryService.createCategory( "Finance",
                                                  "Payables",
                                                  "" );
        repositoryCategoryService.createCategory( "Finance",
                                                  "Receivables",
                                                  "" );
    }

}
