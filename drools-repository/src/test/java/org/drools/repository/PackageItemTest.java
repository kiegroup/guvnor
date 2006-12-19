package org.drools.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class PackageItemTest extends TestCase {

    public void testListPackages() throws Exception {
        RulesRepository repo = getRepo();
        repo.createPackage( "testListPackages1", "lalalala" );
        
        List list = iteratorToList( repo.listPackages() );
        int prevSize = list.size();
        repo.createPackage( "testListPackages2", "abc" );
        
        list = iteratorToList( repo.listPackages() );
        
        assertEquals(prevSize + 1, list.size());
    }
    
    public void testRulePackageItem() throws Exception {
        RulesRepository repo = getRepo();
            
        //calls constructor
        PackageItem rulePackageItem1 = repo.createPackage("testRulePackage", "desc");
        assertNotNull(rulePackageItem1);
        assertEquals("testRulePackage", rulePackageItem1.getName());
        
        Iterator it = getRepo().listPackages();
        assertTrue(it.hasNext());

        while (it.hasNext()) {
            PackageItem pack = (PackageItem) it.next();
            if (pack.getName().equals( "testRulePackage" )) {
                return;
            }
        }
        fail("should have picked up the testRulePackage but didnt.");

        
    }
    
    /**
     * This tests creating a "baseline" of a RulePackage,
     * basically updating all the resources, and checking it in as a version.
     * This is showing off "multi dimensional versioning".
     */
    public void testBaselinePackage() throws Exception {
        RulesRepository repo = getRepo();
        
        PackageItem pack = repo.createPackage( "testBaselinePackage", "for testing baselines" );
        
        AssetItem rule1 = pack.addAsset( "rule 1", "yeah" );
        AssetItem rule2 = pack.addAsset( "rule 2", "foobar" );
        
        assertEquals(StateItem.DRAFT_STATE_NAME, rule1.getState().getName());
        
        StateItem state = repo.getState( "deployed" );
        
        repo.save();
        
        assertNull(pack.getPrecedingVersion());
        
        //the first version, frozen with 2 rules
        pack.createBaseline("commit comment", state);
        
        //check head
        pack = repo.loadPackage( "testBaselinePackage" );
        assertEquals(2, iteratorToList(pack.getAssets()).size());
        
        //now remove a rule from head
        pack.removeAsset( "rule 1" );
        repo.save();
        assertEquals(1, iteratorToList( pack.getAssets() ).size());
        
        pack.createBaseline( "another", state );
        
        PackageItem prev = (PackageItem) pack.getPrecedingVersion();
        assertEquals(2, iteratorToList( prev.getAssets() ).size());
        
        assertNotNull(prev.getSucceedingVersion());
        
        PackageItem succ = (PackageItem) prev.getSucceedingVersion();
        
        assertEquals(1, iteratorToList( succ.getAssets() ).size());
        
    }

    /** Continues to show how multi dimensional versioning works */
    public void testPackageBaselineWithRuleChanges() throws Exception {
        String packName = StackUtil.getCurrentMethodName();
        PackageItem pack = getRepo().createPackage( packName, "yeah" );
        
        AssetItem rule = pack.addAsset( "foobar", "waah" );        
        rule.updateContent( "this is something" );        
        rule.checkin( "something" );
        
        StateItem state = getRepo().getState( "something" );
        
        pack.createBaseline( "another one", state );
        
        pack = getRepo().loadPackage( packName );
        
        rule = (AssetItem) pack.getAssets().next();
        rule.updateContent( "blah" );
        rule.checkin( "woot" );
        
        pack.createBaseline( "yeah", state );
        
        pack = getRepo().loadPackage( packName );
        rule = (AssetItem) pack.getAssets().next();
        assertEquals("blah", rule.getContent());
        
        PackageItem prev = (PackageItem) pack.getPrecedingVersion();
        rule = (AssetItem) prev.getAssets().next();
        assertEquals("this is something", rule.getContent());
        
    }

    private RulesRepository getRepo() {
        return RepositorySessionUtil.getRepository();
    }

    public void testLoadRulePackageItem() {

        PackageItem rulePackageItem = getRepo().createPackage("testLoadRuleRuleItem", "desc");

        rulePackageItem = getRepo().loadPackage("testLoadRuleRuleItem");
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItem", rulePackageItem.getName());
        
        assertEquals("desc", rulePackageItem.getDescription());
        assertEquals(PackageItem.PACKAGE_FORMAT, rulePackageItem.getFormat());
        // try loading rule package that was not created 
        try {
            rulePackageItem = getRepo().loadPackage("anotherRuleRuleItem");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }    
    
    /**
     * This will test getting rules of specific versions out of a package.
     */
    public void testPackageRuleVersionExtraction() throws Exception {
        PackageItem pack = getRepo().createPackage( "package extractor", "foo" );
        
        AssetItem rule1 = pack.addAsset( "rule number 1", "yeah man" );
        rule1.checkin( "version0" );
        
        AssetItem rule2 = pack.addAsset( "rule number 2", "no way" );
        rule2.checkin( "version0" );
        
        AssetItem rule3 = pack.addAsset( "rule number 3", "yes way" );
        rule3.checkin( "version0" );
        
        getRepo().save();
        
        pack = getRepo().loadPackage( "package extractor" );
        List rules = iteratorToList( pack.getAssets() );
        assertEquals(3, rules.size());
        
        StateItem state = getRepo().getState( "foobar" );
        
        rule1.updateState( "foobar" );
        rule1.checkin( "yeah" );
        
        pack = getRepo().loadPackage( "package extractor" );
        
        rules = iteratorToList( pack.getAssetsWithStatus(state) );
        
        assertEquals(1, rules.size());
        
        //now lets try an invalid state tag
        rules = iteratorToList( pack.getAssetsWithStatus( getRepo().getState( "whee" ) ) );
        assertEquals(0, rules.size());
        
        //and Draft, as we start with Draft, should be able to get all three back
        //although an older version of one of them
        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( StateItem.DRAFT_STATE_NAME )) );
        assertEquals(3, rules.size());
        
        //now do an update, and pull it out via state
        rule1.updateContent( "new content" );
        rule1.updateState( "draft" );
        rule1.checkin( "latest" );
        
        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( "draft" )) );
        assertEquals(1, rules.size());
        AssetItem rule = (AssetItem) rules.get( 0 );
        assertEquals("new content", rule.getContent());
        
        //get the previous one via state
        
        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( "foobar" )) );
        assertEquals(1, rules.size());
        AssetItem prior = (AssetItem) rules.get( 0 );
        
        assertFalse("new content".equals( prior.getContent() ));
        
    }
    
    public void testIgnoreState() throws Exception {
        PackageItem pack = getRepo().createPackage( "package testIgnoreState", "foo" );
        
        
        AssetItem rule1 = pack.addAsset( "rule number 1", "yeah man" );
        rule1.updateState( "x" );
        rule1.checkin( "version0" );
        
        
        AssetItem rule2 = pack.addAsset( "rule number 2", "no way" );
        rule2.updateState( "x" );
        rule2.checkin( "version0" );
        
        AssetItem rule3 = pack.addAsset( "rule number 3", "yes way" );
        rule3.updateState( "disabled" );
        rule3.checkin( "version0" );
        
        getRepo().save();
        
        
        Iterator result = pack.getAssetsWithStatus( getRepo().getState( "x" ), getRepo().getState( "disabled" ) );
        List l = iteratorToList( result );
        assertEquals(2, l.size());
    }
    
    public void testDuplicatePackageName() throws Exception {
        PackageItem pack = getRepo().createPackage( "dupePackageTest", "testing for dupe" );        
        assertNotNull(pack.getName());
        
        try {
            getRepo().createPackage( "dupePackageTest", "this should fail" );
            fail("Should not be able to add a package of the same name.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
        
    }
    
    public void testPackageInstanceWrongNodeType() throws Exception {
        PackageItem pack = getRepo().loadDefaultPackage();        
        AssetItem rule = pack.addAsset( "packageInstanceWrongNodeType", "" );
        
        try {
            new PackageItem(this.getRepo(), rule.getNode());
            fail("Can't create a package from a rule node.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
        
    }
    
    
    public void testLoadRulePackageItemByUUID() throws Exception {

        PackageItem rulePackageItem = getRepo().createPackage("testLoadRuleRuleItemByUUID", "desc");

        String uuid = null;
            uuid = rulePackageItem.getNode().getUUID();


        rulePackageItem = getRepo().loadPackageByUUID(uuid);
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItemByUUID", rulePackageItem.getName());
        
        // try loading rule package that was not created 
        try {
            rulePackageItem = getRepo().loadPackageByUUID("01010101-0101-0101-0101-010101010101");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }    
    
    public void testAddRuleRuleItem() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testAddRuleRuleItem","desc");

            
            AssetItem ruleItem1 = rulePackageItem1.addAsset("testAddRuleRuleItem", "test description");
            ruleItem1.updateContent( "test content" );
            ruleItem1.checkin( "updated the rule content" );
            
            Iterator rulesIt = rulePackageItem1.getAssets();
            assertNotNull(rulesIt);
            AssetItem first = (AssetItem) rulesIt.next();
            assertFalse(rulesIt.hasNext());
            assertEquals("testAddRuleRuleItem", first.getName());
            
            //test that it is following the head revision                        
            ruleItem1.updateContent("new lhs");
            ruleItem1.checkin( "updated again" );
            rulesIt = rulePackageItem1.getAssets();
            assertNotNull(rulesIt);
            
            List rules = iteratorToList( rulesIt );
            assertEquals(1, rules.size());
            assertEquals("testAddRuleRuleItem", ((AssetItem)rules.get(0)).getName());
            assertEquals("new lhs", ((AssetItem)rules.get(0)).getContent());
                        
            AssetItem ruleItem2 = rulePackageItem1.addAsset("testAddRuleRuleItem2", "test content");
            
            rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(2, rules.size());  

    }

    
    List iteratorToList(Iterator it) {
        List list = new ArrayList();
        while(it.hasNext()) {
            list.add( it.next() );
        }
        return list;
    }






    
    public void testGetRules() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testGetRules", "desc");
                        
            AssetItem ruleItem1 = rulePackageItem1.addAsset("testGetRules", "desc" );
            ruleItem1.updateContent( "test lhs content" );

            
            List rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testGetRules", ((AssetItem)rules.get(0)).getName());
                                  
            AssetItem ruleItem2 = rulePackageItem1.addAsset("testGetRules2", "desc" );
            ruleItem2.updateContent( "test lhs content" );
            
            rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(2, rules.size());            

            //now lets test loading rule
            AssetItem loaded = rulePackageItem1.loadAsset( "testGetRules" );
            assertNotNull(loaded);
            assertEquals("testGetRules", loaded.getName());
            assertEquals("desc", loaded.getDescription());
           
            
    }

    public void testToString() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testToStringPackage", "desc");
            
            AssetItem ruleItem1 = rulePackageItem1.addAsset("testToStringPackage", "test lhs content" );
            ruleItem1.updateContent( "test lhs content" );
            
            assertNotNull(rulePackageItem1.toString());                        

    }
    
    public void testRemoveRule() {
            PackageItem rulePackageItem1 = getRepo().createPackage("testRemoveRule", "desc");
            
            AssetItem ruleItem1 = rulePackageItem1.addAsset("testRemoveRule", "test lhs content" );
            ruleItem1.updateContent( "test lhs content" ); 
            
            
            
            Iterator rulesIt = rulePackageItem1.getAssets();
            AssetItem next = (AssetItem) rulesIt.next();
            
            assertFalse(rulesIt.hasNext());
            assertEquals("testRemoveRule", next.getName());
                               
            
            
            ruleItem1.updateContent("new lhs");
            List rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule", ((AssetItem)rules.get(0)).getName());
            assertEquals("new lhs", ((AssetItem)rules.get(0)).getContent());
                        
            AssetItem ruleItem2 = rulePackageItem1.addAsset("testRemoveRule2", "test lhs content");
            
            //remove the rule, make sure the other rule in the pacakge stays around
            rulePackageItem1.removeAsset(ruleItem1.getName());
            rulePackageItem1.rulesRepository.save();
            rules = iteratorToList(rulePackageItem1.getAssets());
            assertEquals(1, rules.size());
            assertEquals("testRemoveRule2", ((AssetItem)rules.get(0)).getName());
            
            //remove the rule that is following the head revision, make sure the pacakge is now empty
            rulePackageItem1.removeAsset(ruleItem2.getName());
            rules = iteratorToList(rulePackageItem1.getAssets());
            assertNotNull(rules);
            assertEquals(0, rules.size());

    }
        
    public void testSearchByFormat() throws Exception {
        PackageItem pkg = getRepo().createPackage( "searchByFormat", "" );
        getRepo().save();
        
        
        AssetItem item = pkg.addAsset( "searchByFormatAsset1", "" );
        item.updateFormat( "xyz" );
        item.checkin( "la" );
        
        item = pkg.addAsset( "searchByFormatAsset2", "wee" );
        item.updateFormat( "xyz" );
        item.checkin( "la" );
        
        item = pkg.addAsset( "searchByFormatAsset3", "wee" );
        item.updateFormat( "ABC" );
        item.checkin( "la" );
        
        AssetItemIterator it = pkg.queryAssets( "drools:format='xyz'" );        
        List list = iteratorToList( it );
        assertEquals(2, list.size());
        
        
    }

    
    
    public void testGetFormat() {        
            PackageItem rulePackageItem1 = getRepo().createPackage("testGetFormat", "woot");
            assertNotNull(rulePackageItem1);
            assertEquals(PackageItem.PACKAGE_FORMAT, rulePackageItem1.getFormat());    

    }        
}