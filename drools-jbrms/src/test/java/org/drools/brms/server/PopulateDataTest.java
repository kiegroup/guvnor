package org.drools.brms.server;

import java.io.InputStream;

import junit.framework.TestCase;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.server.rules.SuggestionCompletionLoader;
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
        JBRMSServiceServlet serv = new TestHarnessJBRMSServiceServlet();
        
        createCategories( serv );
        createStates( serv );
        createPackages( serv );
        createModel( serv );
        
        createSomeRules( serv );
        createPackageSnapshots( serv );
        
    }

    private void createModel(JBRMSServiceServlet serv) throws Exception {
        RulesRepository repo = serv.getRulesRepository();
        String uuid = serv.createNewRule( "DomainModel", "This is the business object model", null, "com.billasurf.manufacturing.plant", AssetFormats.MODEL );
        InputStream file = this.getClass().getResourceAsStream( "/billasurf.jar" );
        assertNotNull(file);
        
        FileUploadServlet.attachFileToAsset( repo, uuid, file, "billasurf.jar" );
        
        AssetItem item = repo.loadAssetByUUID( uuid );
        assertNotNull(item.getBinaryContentAsBytes());
        
        
        
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        PackageItem pkg = repo.loadPackage( "com.billasurf.manufacturing.plant" );
        pkg.updateHeader( "import com.billasurf.Board\nimport com.billasurf.Person" +
                "\n\nglobal com.billasurf.Person prs" );
        pkg.checkin( "added imports" );
        
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( pkg );
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

    private void createPackageSnapshots(JBRMSServiceServlet serv) {
        serv.createPackageSnapshot( "com.billasurf.finance", "TEST", false, "The testing region." );
        serv.createPackageSnapshot( "com.billasurf.finance", "PRODUCTION", false, "The testing region." );
        serv.createPackageSnapshot( "com.billasurf.finance", "PRODUCTION ROLLBACK", false, "The testing region." );
    }

    private void createSomeRules(JBRMSServiceServlet serv) throws SerializableException {
        String uuid = serv.createNewRule( "Surfboard_Colour_Combination", "allowable combinations for basic boards.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        serv.changeState( uuid, "Pending", false );
        uuid = serv.createNewRule( "Premium_Colour_Combinations", "This defines XXX.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        serv.changeState( uuid, "Approved", false );
        uuid = serv.createNewRule( "Fibreglass supplier selection", "This defines XXX.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        uuid = serv.createNewRule( "Recommended wax", "This defines XXX.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );

        
        
    }

    private void createPackages(JBRMSServiceServlet serv) throws SerializableException {
        String uuid = serv.createPackage( "com.billasurf.manufacturing", "Rules for manufacturing." );
        
        PackageConfigData conf = serv.loadPackageConfig( uuid );
        conf.header = "import com.billasurf.manuf.materials.*";
        serv.savePackage( conf );
        
        serv.createPackage( "com.billasurf.manufacturing.plant", "Rules for manufacturing plants." );
        serv.createPackage( "com.billasurf.finance", "All financial rules." );
        serv.createPackage( "com.billasurf.hrman", "Rules for in house HR application." );
        serv.createPackage( "com.billasurf.sales", "Rules exposed as a service for pricing, and discounting." );
        
    }

    private void createStates(JBRMSServiceServlet serv) throws SerializableException {
        serv.createState( "Approved" );
        serv.createState( "Pending" );
    }

    private void createCategories(JBRMSServiceServlet serv) {
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
