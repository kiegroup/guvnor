package org.drools.brms.server;
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



import java.io.InputStream;

import junit.framework.TestCase;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.server.files.FileManagerUtils;
import org.drools.brms.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This class will setup the data in a test state, which is
 * good for screenshots/playing around.
 *
 * If you run this by itself, the database will be wiped, and left with only this data in it.
 * If it is run as part of the suite, it will just augment the data.
 *
 * This sets up the data for a fictional company Billasurf, dealing with surfwear and equipment
 * (for surfing, boarding etc).
 *
 * @author Michael Neale
 */
public class PopulateDataTest extends TestCase {

    public void testPopulate() throws Exception {
        ServiceImplementation serv = new ServiceImplementation();
        serv.repository = new RulesRepository(TestEnvironmentSessionHelper.getSession());

        createCategories( serv );
        createStates( serv );
        createPackages( serv );
        createModel( serv );

        createSomeRules( serv );
        createPackageSnapshots( serv );

    }

    private void createModel(ServiceImplementation serv) throws Exception {
        RulesRepository repo = serv.repository;
        String uuid = serv.createNewRule( "DomainModel", "This is the business object model", null, "com.billasurf.manufacturing.plant", AssetFormats.MODEL );
        InputStream file = this.getClass().getResourceAsStream( "/billasurf.jar" );
        assertNotNull(file);

        FileManagerUtils fm = new FileManagerUtils();
        fm.repository = repo;

        fm.attachFileToAsset( uuid, file, "billasurf.jar" );

        AssetItem item = repo.loadAssetByUUID( uuid );
        assertNotNull(item.getBinaryContentAsBytes());
        assertEquals( item.getBinaryContentAttachmentFileName(), "billasurf.jar" );


        PackageItem pkg = repo.loadPackage( "com.billasurf.manufacturing.plant" );
        pkg.updateHeader( "import com.billasurf.Board\nimport com.billasurf.Person" +
                "\n\nglobal com.billasurf.Person prs" );
        pkg.checkin( "added imports" );

        SuggestionCompletionEngine eng = serv.loadSuggestionCompletionEngine( "com.billasurf.manufacturing.plant" );
        assertNotNull(eng);

        assertEquals(2, eng.factTypes.length);
        String[] fields = (String[]) eng.fieldsForType.get( "Board" );
        assertTrue(fields.length == 3);

        String[] globalVars = eng.getGlobalVariables();
        assertEquals(1, globalVars.length);
        assertEquals("prs", globalVars[0]);
        assertEquals(2, eng.getFieldCompletionsForGlobalVariable( "prs" ).length);

        fields = (String[]) eng.fieldsForType.get( "Person" );

        assertTrue(fields.length == 2);




    }

    private void createPackageSnapshots(ServiceImplementation serv) {
        serv.createPackageSnapshot( "com.billasurf.manufacturing", "TEST", false, "The testing region." );
        serv.createPackageSnapshot( "com.billasurf.manufacturing", "PRODUCTION", false, "The testing region." );
        serv.createPackageSnapshot( "com.billasurf.manufacturing", "PRODUCTION ROLLBACK", false, "The testing region." );

    }

    private void createSomeRules(ServiceImplementation serv) throws SerializableException {
        String uuid = serv.createNewRule( "Surfboard_Colour_Combination", "allowable combinations for basic boards.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        serv.changeState( uuid, "Pending", false );
        uuid = serv.createNewRule( "Premium_Colour_Combinations", "This defines XXX.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        serv.changeState( uuid, "Approved", false );
        uuid = serv.createNewRule( "Fibreglass supplier selection", "This defines XXX.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        uuid = serv.createNewRule( "Recommended wax", "This defines XXX.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        uuid = serv.createNewRule( "SomeDSL", "Ignore me.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.DSL );




    }

    private void createPackages(ServiceImplementation serv) throws SerializableException {
        String uuid = serv.createPackage( "com.billasurf.manufacturing", "Rules for manufacturing." );

        PackageConfigData conf = serv.loadPackageConfig( uuid );
        conf.header = "import com.billasurf.manuf.materials.*";
        serv.savePackage( conf );

        serv.createPackage( "com.billasurf.manufacturing.plant", "Rules for manufacturing plants." );
        serv.createPackage( "com.billasurf.finance", "All financial rules." );
        serv.createPackage( "com.billasurf.hrman", "Rules for in house HR application." );
        serv.createPackage( "com.billasurf.sales", "Rules exposed as a service for pricing, and discounting." );

    }

    private void createStates(ServiceImplementation serv) throws SerializableException {
        serv.createState( "Approved" );
        serv.createState( "Pending" );
    }

    private void createCategories(ServiceImplementation serv) {
        serv.createCategory( "/", "HR", "" );
        serv.createCategory( "/", "Sales", "" );
        serv.createCategory( "/", "Manufacturing", "" );
        serv.createCategory( "/", "Finance", "" );

        serv.createCategory( "HR", "Leave", "" );
        serv.createCategory( "HR", "Training", "" );
        serv.createCategory( "Sales", "Promotions", "" );
        serv.createCategory( "Sales", "Old promotions", "" );
        serv.createCategory( "Sales", "Boogie boards", "" );
        serv.createCategory( "Sales", "Surf boards", "" );
        serv.createCategory( "Sales", "Surf wear", "" );
        serv.createCategory( "Manufacturing", "Surf wear", "" );
        serv.createCategory( "Manufacturing", "Boards", "" );
        serv.createCategory( "Finance", "Employees", "" );
        serv.createCategory( "Finance", "Payables", "" );
        serv.createCategory( "Finance", "Receivables", "" );
    }

}