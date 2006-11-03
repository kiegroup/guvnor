package org.drools.brms.server;

import java.util.Calendar;

import junit.framework.TestCase;

import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.rulelist.RuleItemListViewer;

import org.drools.repository.CategoryItem;
import org.drools.repository.RuleItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class ServiceImplementationTest extends TestCase {

  public void testCategory() throws Exception {
        //ServiceImpl impl = new ServiceImpl(new RulesRepository(SessionHelper.getSession()));

        RepositoryService impl = new MockJBRMSServiceServlet();
      
        String[] originalCats = impl.loadChildCategories( "/" );
        
        Boolean result = impl.createCategory( "/",
                                              "TopLevel1",
                                              "a description" );
        assertTrue( result.booleanValue() );

        result = impl.createCategory( "/",
                                      "TopLevel2",
                                      "a description" );
        assertTrue( result.booleanValue() );
        
        String[] cats = impl.loadChildCategories( "/" );
        assertTrue( cats.length == originalCats.length + 2 );
        
        result = impl.createCategory( "", "Top3", "description" );
        assertTrue(result.booleanValue());
        
        result = impl.createCategory( null, "Top4", "description" );
        assertTrue(result.booleanValue());
        

    }
  
  public void testAddRuleAndListPackages() throws Exception {
      //ServiceImpl impl = new ServiceImpl(new RulesRepository(SessionHelper.getSession()));
      
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      
      impl.repo.loadDefaultRulePackage();
      impl.repo.createRulePackage( "another", "woot" );
      
      
      CategoryItem cat = impl.repo.loadCategory( "/" );
      cat.addCategory( "testAddRule", "yeah" );
      
      
      Boolean result = impl.createNewRule( "testAddRule", "a description", "testAddRule", "another" );
      assertTrue(result.booleanValue());
      
      String[] packages = impl.listRulePackages();
      assertTrue(packages.length > 0);
      
      boolean found = false;
      for ( int i = 0; i < packages.length; i++ ) {
          if (packages[i].equals( "another" )) {
              found = true;
          }
      }
      assertTrue(found);
      
  }

  public void testAttemptDupeRule() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      CategoryItem cat = impl.repo.loadCategory( "/" );
      cat.addCategory( "testAttemptDupeRule", "yeah" );
      
      impl.repo.createRulePackage("dupes", "yeah");
      
      impl.createNewRule( "testAttemptDupeRule", "ya", "testAttemptDupeRule", "dupes" );
      
      try {
          impl.createNewRule( "testAttemptDupeRule", "ya", "testAttemptDupeRule", "dupes" );
          fail("should not allow duplicates.");
      } catch (SerializableException e) {
          assertNotNull(e.getMessage());
      }
      
  }
  
  public void testRuleTableLoad() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      TableConfig conf = impl.loadTableConfig( RuleItemListViewer.RULE_LIST_TABLE_ID );
      assertNotNull(conf.headers);
      
      CategoryItem cat = impl.repo.loadCategory( "/" );
      cat.addCategory( "testRuleTableLoad", "yeah" );
            
      impl.repo.createRulePackage("testRuleTableLoad", "yeah");      
      impl.createNewRule( "testRuleTableLoad", "ya", "testRuleTableLoad", "testRuleTableLoad" );
      impl.createNewRule( "testRuleTableLoad2", "ya", "testRuleTableLoad", "testRuleTableLoad" );

      TableDataResult result = impl.loadRuleListForCategories( "testRuleTableLoad" );
      assertEquals(2, result.numberOfRows);
      assertEquals(2, result.data.length);
      
      String key = result.data[0].id;
      assertFalse(key.startsWith( "testRule" ));
      
      assertEquals(result.data[0].format, "DRL");
      assertTrue(result.data[0].values[0].startsWith( "testRule" ));
      
      
      
  }
  
  public void testDateFormatting() throws Exception {
      Calendar cal = Calendar.getInstance();
      TableDisplayHandler handler = new TableDisplayHandler();
      String fmt = handler.formatDate( cal );
      assertNotNull(fmt);
      
      assertTrue(fmt.length() > 8);
      System.out.println(fmt);
  }
  
  public void testLoadRuleAsset() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      impl.repo.createRulePackage( "testLoadRuleAsset", "desc" );
      impl.createCategory( "", "testLoadRuleAsset", "this is a cat" );
      
      
      impl.createNewRule( "testLoadRuleAsset", "description", "testLoadRuleAsset", "testLoadRuleAsset" );
      
      TableDataResult res = impl.loadRuleListForCategories( "testLoadRuleAsset" );
      assertEquals(1, res.data.length);
      
      TableDataRow row = res.data[0];
      String uuid = row.id;
      
      System.out.println("UUID: " + uuid);
      
      RuleAsset asset = impl.loadRuleAsset( uuid );
      assertNotNull(asset);
      
      assertEquals("description", asset.metaData.description);
      
      
      assertNotNull(asset.content);
      assertTrue(asset.content instanceof RuleContentText);
      assertEquals("testLoadRuleAsset", asset.metaData.name);
      assertEquals("testLoadRuleAsset", asset.metaData.title);
      assertEquals("testLoadRuleAsset", asset.metaData.packageName);
      
  }
  
    
}
