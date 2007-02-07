package org.drools.brms.server;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.rulelist.AssetItemListViewer;
import org.drools.brms.server.util.TableDisplayHandler;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;

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
      
      impl.repo.loadDefaultPackage();
      impl.repo.createPackage( "another", "woot" );
      
      
      CategoryItem cat = impl.repo.loadCategory( "/" );
      cat.addCategory( "testAddRule", "yeah" );
      
      
      String result = impl.createNewRule( "test AddRule", "a description", "testAddRule", "another", "txt" );
      assertNotNull(result);
      assertFalse("".equals( result ));
      
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
      
      impl.repo.createPackage("dupes", "yeah");
      
      impl.createNewRule( "testAttemptDupeRule", "ya", "testAttemptDupeRule", "dupes", "rule" );
      
      try {
          impl.createNewRule( "testAttemptDupeRule", "ya", "testAttemptDupeRule", "dupes", "rule" );
          fail("should not allow duplicates.");
      } catch (SerializableException e) {
          assertNotNull(e.getMessage());
      }
      
  }
  
  public void testRuleTableLoad() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      TableConfig conf = impl.loadTableConfig( AssetItemListViewer.RULE_LIST_TABLE_ID );
      assertNotNull(conf.headers);
      
      CategoryItem cat = impl.repo.loadCategory( "/" );
      cat.addCategory( "testRuleTableLoad", "yeah" );
            
      impl.repo.createPackage("testRuleTableLoad", "yeah");      
      impl.createNewRule( "testRuleTableLoad", "ya", "testRuleTableLoad", "testRuleTableLoad", "rule" );
      impl.createNewRule( "testRuleTableLoad2", "ya", "testRuleTableLoad", "testRuleTableLoad", "rule" );

      TableDataResult result = impl.loadRuleListForCategories( "testRuleTableLoad" );
      assertEquals(2, result.data.length);
      
      String key = result.data[0].id;
      assertFalse(key.startsWith( "testRule" ));
      
      assertEquals(result.data[0].format, "rule");
      assertTrue(result.data[0].values[0].startsWith( "testRule" ));
      
      
      
  }
  
  public void testDateFormatting() throws Exception {
      Calendar cal = Calendar.getInstance();
      TableDisplayHandler handler = new TableDisplayHandler();
      String fmt = handler.formatDate( cal );
      assertNotNull(fmt);
      
      assertTrue(fmt.length() > 8);
  }
  
  public void testLoadRuleAsset() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      impl.repo.createPackage( "testLoadRuleAsset", "desc" );
      impl.createCategory( "", "testLoadRuleAsset", "this is a cat" );
      
      
      impl.createNewRule( "testLoadRuleAsset", "description", "testLoadRuleAsset", "testLoadRuleAsset", "txt" );
      
      TableDataResult res = impl.loadRuleListForCategories( "testLoadRuleAsset" );
      assertEquals(1, res.data.length);
      
      TableDataRow row = res.data[0];
      String uuid = row.id;
      

      
      RuleAsset asset = impl.loadRuleAsset( uuid );
      assertNotNull(asset);
      
      assertEquals(uuid, asset.uuid);
      
      assertEquals("description", asset.metaData.description);
      
      
      assertNotNull(asset.content);
      assertTrue(asset.content instanceof RuleContentText);
      assertEquals("testLoadRuleAsset", asset.metaData.name);
      assertEquals("testLoadRuleAsset", asset.metaData.title);
      assertEquals("testLoadRuleAsset", asset.metaData.packageName);
      assertEquals("txt", asset.metaData.format);
      assertNotNull(asset.metaData.createdDate);
      
      assertEquals(1, asset.metaData.categories.length);
      assertEquals("testLoadRuleAsset", asset.metaData.categories[0]);
      
      AssetItem rule = impl.repo.loadPackage( "testLoadRuleAsset" ).loadAsset( "testLoadRuleAsset" );
      rule.updateState( "whee" );
      rule.checkin( "changed state" );
      asset = impl.loadRuleAsset( uuid );
      
      assertEquals("whee", asset.metaData.state);
      assertEquals("changed state", asset.metaData.checkinComment);
      
      
  }
  
  public void testLoadAssetHistoryAndRestore() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      impl.repo.createPackage( "testLoadAssetHistory", "desc" );
      impl.createCategory( "", "testLoadAssetHistory", "this is a cat" );
      
      
      String uuid = impl.createNewRule( "testLoadAssetHistory", "description", "testLoadAssetHistory", "testLoadAssetHistory", "txt" );
      RuleAsset asset = impl.loadRuleAsset( uuid );
      impl.checkinVersion( asset ); //1
      impl.checkinVersion( asset ); //2
      impl.checkinVersion( asset ); //HEAD   
      
      TableDataResult result = impl.loadAssetHistory( uuid );
      assertNotNull(result);
      TableDataRow[] rows = result.data;
      assertEquals(2, rows.length);
      assertFalse(rows[0].id.equals( uuid ));
      assertFalse(rows[1].id.equals( uuid ));
      
      RuleAsset old = impl.loadRuleAsset( rows[0].id );
      RuleAsset newer = impl.loadRuleAsset( rows[1].id );
      assertFalse(old.metaData.versionNumber.equals( newer.metaData.versionNumber ));
            
      RuleAsset head = impl.loadRuleAsset( uuid );
      
      String oldVersion = old.metaData.versionNumber;
      assertFalse(oldVersion.equals( head.metaData.versionNumber ));
      
      impl.restoreVersion( old.uuid, head.uuid, "this was cause of a mistake" );
      
      RuleAsset newHead = impl.loadRuleAsset( uuid );
      
      
      assertEquals("this was cause of a mistake", newHead.metaData.checkinComment);
      
      
  }
  
  
  
  public void testCheckin() throws Exception {
          MockJBRMSServiceServlet serv = new MockJBRMSServiceServlet();
          
          serv.listRulePackages();
          
          serv.createCategory( "/", "testCheckinCategory", "this is a description" );
          serv.createCategory( "/", "testCheckinCategory2", "this is a description" );
          serv.createCategory( "testCheckinCategory", "deeper", "description" );
          
          String uuid = serv.createNewRule( "testChecking", "this is a description", "testCheckinCategory", "default", "drl" );
          
          RuleAsset asset = serv.loadRuleAsset( uuid );
          
          assertNotNull(asset.metaData.lastModifiedDate);
          
          asset.metaData.coverage = "boo";
          asset.content = new RuleContentText();
          ((RuleContentText) asset.content).content = "yeah !";
          
          Date start = new Date();
          Thread.sleep( 100 );
          
          String uuid2 = serv.checkinVersion( asset );
          assertEquals(uuid, uuid2);
          
          RuleAsset asset2 = serv.loadRuleAsset( uuid );
          assertNotNull(asset2.metaData.lastModifiedDate);
          assertTrue(asset2.metaData.lastModifiedDate.after( start ));
          
          
          assertEquals("boo", asset2.metaData.coverage);
          assertEquals("1", asset2.metaData.versionNumber);
          
          assertEquals("yeah !", ((RuleContentText) asset2.content).content);
          
          asset2.metaData.coverage = "ya";
          asset2.metaData.checkinComment = "checked in";
          
          String cat = asset2.metaData.categories[0];
          asset2.metaData.categories = new String[3];
          asset2.metaData.categories[0] = cat;          
          asset2.metaData.categories[1] = "testCheckinCategory2";
          asset2.metaData.categories[2] = "testCheckinCategory/deeper";
          
          serv.checkinVersion( asset2 );
          
          asset2 = serv.loadRuleAsset( uuid );
          assertEquals("ya", asset2.metaData.coverage);
          assertEquals("2", asset2.metaData.versionNumber);
          assertEquals("checked in", asset2.metaData.checkinComment);
          assertEquals(3, asset2.metaData.categories.length);
          assertEquals("testCheckinCategory", asset2.metaData.categories[0]);
          assertEquals("testCheckinCategory2", asset2.metaData.categories[1]);
          assertEquals("testCheckinCategory/deeper", asset2.metaData.categories[2]);
          
  }
  
  
  public void testCreatePackage() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      String[] pkgs = impl.listRulePackages();
      String uuid = impl.createPackage( "testCreatePackage", "this is a new package" );
      assertNotNull( uuid );
      
      PackageItem item = impl.repo.loadPackage( "testCreatePackage" );
      assertNotNull(item);
      assertEquals("this is a new package", item.getDescription());
      
      
      
      assertEquals(pkgs.length + 1, impl.listRulePackages().length);
      
      PackageConfigData conf = impl.loadPackage( "testCreatePackage" );
      assertEquals("this is a new package", conf.description);
      assertNotNull(conf.lastModified);
  }
  
  public void testLoadPackageConfig() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      PackageItem it = impl.repo.loadDefaultPackage();
      it.updateCoverage( "xyz" );
      it.updateExternalURI( "ext" );
      it.updateHeader( "header" );
      impl.repo.save();
      
      PackageConfigData data = impl.loadPackage( "default" );
      assertNotNull(data);
      
      assertEquals("default", data.name);
      assertEquals("header", data.header);
      assertEquals("ext", data.externalURI);
      
      assertNotNull(data.uuid);
      
  }
  
  public void testPackageConfSave() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      String uuid = impl.createPackage( "testPackageConfSave", "a desc" );
      PackageConfigData data = impl.loadPackage( "testPackageConfSave" );
      
      data.description = "new desc";
      data.header = "wa";
      data.externalURI = "new URI";
      
      
      String uuid2 = impl.savePackage( data );
      assertNotNull(uuid);
      assertEquals(uuid, uuid2);
      
      data = impl.loadPackage( "testPackageConfSave" );
      assertEquals("new desc", data.description);
      assertEquals("wa", data.header);
      assertEquals("new URI", data.externalURI);      
  }
  
  public void testListByFormat() throws Exception {
      MockJBRMSServiceServlet impl = new MockJBRMSServiceServlet();
      String cat = "testListByFormat";
      impl.createCategory( "/", cat, "ya" );
      impl.createPackage( "testListByFormat", "used for listing by format." );
      
      String uuid = impl.createNewRule( "testListByFormat", "x", cat, "testListByFormat", "testListByFormat" );
      String uuid2 = impl.createNewRule( "testListByFormat2", "x", cat, "testListByFormat", "testListByFormat" );
      String uuid3 = impl.createNewRule( "testListByFormat3", "x", cat, "testListByFormat", "testListByFormat" );
      String uuid4 = impl.createNewRule( "testListByFormat4", "x", cat, "testListByFormat", "testListByFormat" );

      TableDataResult res = impl.listAssetsByFormat( "testListByFormat", "testListByFormat", -1, 0 );
      assertEquals(4, res.data.length);
      assertEquals(uuid, res.data[0].id);
      assertEquals("testListByFormat", res.data[0].values[0]);
      
      res = impl.listAssetsByFormat( "testListByFormat", "testListByFormat", 4, 0 );      
      assertEquals(4, res.data.length);

      res = impl.listAssetsByFormat( "testListByFormat", "testListByFormat", 2, 0 );      
      assertEquals(2, res.data.length);
      assertEquals(uuid, res.data[0].id);
      
      
      res = impl.listAssetsByFormat( "testListByFormat", "testListByFormat", 2, 2 );      
      assertEquals(2, res.data.length);
      assertEquals(uuid3, res.data[0].id);
      
  }
  
    
}
