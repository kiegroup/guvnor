package org.drools.brms.server;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.rpc.PackageConfigData;

import com.google.gwt.user.client.rpc.SerializableException;

import junit.framework.TestCase;

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
        JBRMSServiceServlet serv = new MockJBRMSServiceServlet();
        
        createCategories( serv );
        createStates( serv );
        createPackages( serv );
        
        serv.createNewRule( "SurfboardColourCombination", "allowable combinations for basic boards.", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        serv.createNewRule( "PremiumColourCombinations", "This defines .", "Manufacturing/Boards", "com.billasurf.manufacturing", AssetFormats.BUSINESS_RULE );
        
        
        
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
