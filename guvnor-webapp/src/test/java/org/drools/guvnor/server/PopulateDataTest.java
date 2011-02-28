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
import org.drools.guvnor.server.security.RoleTypes;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.After;
import org.junit.Before;
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

    @Before
    public void setUp() {
        setUpSeamAndRepository();
        setUpMockIdentity();
    }

    @After
    public void tearDown() {
        tearAllDown();
    }

    @Test
    public void testPopulate() throws Exception {
        ServiceImplementation serv = getServiceImplementation();

        createCategories( serv );
        createStates( serv );
        createPackages( serv );
        createModel( serv );

        createSomeRules( serv );
        createPackageSnapshots( serv );

        createPermissions( serv );

        PackageItem pkg = serv.getRulesRepository().loadPackage( "com.billasurf.manufacturing.plant" );

        serv.buildPackage( pkg.getUUID(),
                           true );

    }

    private void createPermissions(ServiceImplementation serv) {
        Map<String, List<String>> perms = new HashMap<String, List<String>>();
        perms.put( RoleTypes.ADMIN,
                   new ArrayList<String>() );
        serv.updateUserPermissions( "woozle1",
                                    perms );

        perms = new HashMap<String, List<String>>();
        List<String> targets = new ArrayList<String>();
        targets.add( "category=/foo/bar" );
        targets.add( "category=/whee" );
        perms.put( RoleTypes.ANALYST,
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

    private void createPackageSnapshots(ServiceImplementation serv) {
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

    private void createSomeRules(ServiceImplementation serv) throws SerializationException {
        String uuid = serv.createNewRule( "Surfboard_Colour_Combination",
                                          "allowable combinations for basic boards.",
                                          "Manufacturing/Boards",
                                          "com.billasurf.manufacturing",
                                          AssetFormats.BUSINESS_RULE );
        serv.changeState( uuid,
                          "Pending",
                          false );
        uuid = serv.createNewRule( "Premium_Colour_Combinations",
                                   "This defines XXX.",
                                   "Manufacturing/Boards",
                                   "com.billasurf.manufacturing",
                                   AssetFormats.BUSINESS_RULE );
        serv.changeState( uuid,
                          "Approved",
                          false );
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

    private void createPackages(ServiceImplementation serv) throws SerializationException {
        String uuid = serv.createPackage( "com.billasurf.manufacturing",
                                          "Rules for manufacturing." );

        PackageConfigData conf = serv.loadPackageConfig( uuid );
        conf.header = "import com.billasurf.manuf.materials.*";
        serv.savePackage( conf );

        serv.createPackage( "com.billasurf.manufacturing.plant",
                            "Rules for manufacturing plants." );
        serv.createPackage( "com.billasurf.finance",
                            "All financial rules." );
        serv.createPackage( "com.billasurf.hrman",
                            "Rules for in house HR application." );
        serv.createPackage( "com.billasurf.sales",
                            "Rules exposed as a service for pricing, and discounting." );

    }

    private void createStates(ServiceImplementation serv) throws SerializationException {
        serv.createState( "Approved" );
        serv.createState( "Pending" );
    }

    private void createCategories(ServiceImplementation serv) {
        serv.createCategory( "/",
                             "HR",
                             "" );
        serv.createCategory( "/",
                             "Sales",
                             "" );
        serv.createCategory( "/",
                             "Manufacturing",
                             "" );
        serv.createCategory( "/",
                             "Finance",
                             "" );

        serv.createCategory( "HR",
                             "Leave",
                             "" );
        serv.createCategory( "HR",
                             "Training",
                             "" );
        serv.createCategory( "Sales",
                             "Promotions",
                             "" );
        serv.createCategory( "Sales",
                             "Old promotions",
                             "" );
        serv.createCategory( "Sales",
                             "Boogie boards",
                             "" );
        serv.createCategory( "Sales",
                             "Surf boards",
                             "" );
        serv.createCategory( "Sales",
                             "Surf wear",
                             "" );
        serv.createCategory( "Manufacturing",
                             "Surf wear",
                             "" );
        serv.createCategory( "Manufacturing",
                             "Boards",
                             "" );
        serv.createCategory( "Finance",
                             "Employees",
                             "" );
        serv.createCategory( "Finance",
                             "Payables",
                             "" );
        serv.createCategory( "Finance",
                             "Receivables",
                             "" );
    }

}
