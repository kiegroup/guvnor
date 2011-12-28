/*
 * Copyright 2010 JBoss Inc
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

package org.drools.repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;


public class ModuleItemTest extends RepositoryTestCase {

    private ModuleItem loadGlobalArea() {
        return getRepo().loadGlobalArea();
    }

    @Test
    public void testListPackages() throws Exception {
        RulesRepository repo = getRepo();
        ModuleItem item = repo.createModule( "testListPackages1", "lalalala" );

        assertNotNull(item.getCreator());

        item.updateStringProperty( "goo", "whee" );
        assertEquals("goo", item.getStringProperty( "whee" ));
        assertFalse(item.getCreator().equals( "" ));

        List list = iteratorToList( repo.listModules() );
        int prevSize = list.size();
        repo.createModule( "testListPackages2", "abc" );

        list = iteratorToList( repo.listModules() );

        assertEquals(prevSize + 1, list.size());
    }

    @Test
    @Ignore("JackRabbit errors about node type for property {}testing. Probably repository changes have broken test.")
    public void testAddPackageProperties() throws Exception {
        RulesRepository repo = getRepo();
        ModuleItem item = repo.createModule( "testListPackages1", "lalalala" );

        assertNotNull(item.getCreator());

        String[] testProp = new String[]{"Test1","Test2"};

        item.node.checkout();
        item.node.setProperty("testing", testProp);
        //item.node.setProperty("testing", "blah");

        String[] newProp = item.getStringPropertyArray( "testing" );
        assertTrue((testProp[0]).equals(newProp[0]));
        assertTrue(("Test2").equals(newProp[1]));

        //assertEquals(testProp[0], );
        assertFalse(item.getCreator().equals( "" ));

        List list = iteratorToList( repo.listModules() );
        int prevSize = list.size();
        repo.createModule( "testListPackages2", "abc" );

        list = iteratorToList( repo.listModules() );

        assertEquals(prevSize + 1, list.size());
    }

    @Test
    public void testPackageRemove() throws Exception {
        RulesRepository repo = getRepo();
        ModuleItem p = repo.createModule("removeMe", "");
        AssetItem a = p.addAsset("Whee", "");
        a.updateContent("yeah");
        a.checkin("la");
        p.addAsset("Waa", "");
        repo.save();


        ModuleItem pkgNested = p.createSubModule("NestedGoodness");
        assertNotNull(pkgNested);

        int n = iteratorToList(repo.listModules()).size();

        p = repo.loadModule("removeMe");
        p.remove();
        repo.save();

        int n_ = iteratorToList(repo.listModules()).size();
        assertEquals(n - 1, n_);
    }

    @Test
    public void testRulePackageItem() throws Exception {
        RulesRepository repo = getRepo();

        //calls constructor
        ModuleItem rulePackageItem1 = repo.createModule("testRulePackage", "desc");
        assertNotNull(rulePackageItem1);
        assertEquals("testRulePackage", rulePackageItem1.getName());

        Iterator it = getRepo().listModules();
        assertTrue(it.hasNext());

        while (it.hasNext()) {
            ModuleItem pack = (ModuleItem) it.next();
            if (pack.getName().equals( "testRulePackage" )) {
                return;
            }
        }
        fail("should have picked up the testRulePackage but didnt.");
    }

    /**
     * This is showing how to copy a package with standard JCR
     */
    @Test
    public void testPackageCopy() throws Exception {
        RulesRepository repo = getRepo();

        ModuleItem pkg = repo.createModule( "testPackageCopy", "this is something" );

        AssetItem it1 = pkg.addAsset( "testPackageCopy1", "la" );
        AssetItem it2 = pkg.addAsset( "testPackageCopy2", "la" );

        it1.updateContent( "new content" );
        it2.updateContent( "more content" );
        it1.checkin( "c" );
        it2.checkin( "c" );

        it1 = pkg.loadAsset( "testPackageCopy1" );
        List hist1 = iteratorToList( it1.getHistory() );
        System.out.println(hist1.size());


        repo.getSession().getWorkspace().copy( pkg.getNode().getPath(), pkg.getNode().getPath() + "_");

        ModuleItem pkg2 = repo.loadModule( "testPackageCopy_" );
        assertNotNull(pkg2);

        assertEquals(2, iteratorToList( pkg2.getAssets() ).size() );
        AssetItem it1_ = pkg2.loadAsset( "testPackageCopy1" );

        it1.updateContent( "new content2" );
        it1.checkin( "la" );
        it1_ = pkg2.loadAsset( "testPackageCopy1" );
        assertEquals("new content", it1_.getContent());
    }

    @Test
    public void testPackageSnapshot() throws Exception {
        RulesRepository repo = getRepo();

        ModuleItem pkg = repo.createModule( "testPackageSnapshot", "this is something" );
        assertFalse(pkg.isSnapshot());


        AssetItem it1 = pkg.addAsset( "testPackageCopy1", "la" );
        AssetItem it2 = pkg.addAsset( "testPackageCopy2", "la" );

        it1.updateContent( "new content" );
        it1.updateFormat( "drl" );
        it2.updateContent( "more content" );
        it2.updateFormat( "drl" );
        it1.checkin( "c" );
        it2.checkin( "c" );

        long ver1 = it1.getVersionNumber();
        long ver2 = it2.getVersionNumber();
        assertFalse( ver1 == 0 );

        assertEquals(2, iteratorToList(pkg.listAssetsByFormat( new String[] {"drl"} )).size());
        repo.createModuleSnapshot( "testPackageSnapshot", "PROD 2.0" );

        //just check we can load it all via UUID as well...
        ModuleItem pkgLoaded = repo.loadModuleSnapshot( "testPackageSnapshot", "PROD 2.0" );
        assertTrue(pkgLoaded.isSnapshot());
        assertEquals("PROD 2.0", pkgLoaded.getSnapshotName());
        assertEquals("testPackageSnapshot", pkgLoaded.getName());

        ModuleItem _pkgLoaded = repo.loadModuleByUUID( pkgLoaded.getUUID() );
        assertNotNull(_pkgLoaded);
        assertEquals(pkgLoaded.getCreatedDate(), _pkgLoaded.getCreatedDate());
        assertEquals(pkgLoaded.getName(), _pkgLoaded.getName());
        //assertEquals("testPackageSnapshot", pkgLoaded.getName());
        List loadedAssets = iteratorToList( pkgLoaded.getAssets() );
        List _loadedAssets = iteratorToList( _pkgLoaded.getAssets() );
        assertEquals(loadedAssets.size(), _loadedAssets.size());

        //now make some changes on the main line
        it1.updateContent( "XXX" );
        it1.checkin( "X" );
        assertFalse(it1.getVersionNumber()==  ver1 );
        AssetItem it3 = pkg.addAsset( "testPackageCopy3", "x" );
        it3.updateFormat( "drl" );
        it3.checkin( "a" );
        assertEquals(3, iteratorToList( pkg.listAssetsByFormat( new String[] {"drl"} )).size());



        ModuleItem pkg2 = repo.loadModuleSnapshot( "testPackageSnapshot", "PROD 2.0" );
        assertNotNull(pkg2);
        List snapAssets = iteratorToList( pkg2.getAssets() );
        assertEquals(2, snapAssets.size());
        assertFalse(pkg2.getUUID().equals( pkg.getUUID() ));
        assertTrue(snapAssets.get( 0 ) instanceof AssetItem);
        assertTrue(snapAssets.get( 1 ) instanceof AssetItem);

        AssetItem sn1 = (AssetItem) snapAssets.get( 0 );
        AssetItem sn2 = (AssetItem) snapAssets.get( 1 );
        assertEquals("la", sn1.getDescription());
        assertEquals("la", sn2.getDescription());
        assertEquals(ver1, sn1.getVersionNumber());
        assertEquals(ver2, sn2.getVersionNumber());


        assertEquals(2, iteratorToList(pkg2.listAssetsByFormat( new String[] {"drl"} )).size());

        //now check we can list the snappies
        String[] res = repo.listModuleSnapshots("testPackageSnapshot");

        assertEquals(1, res.length);
        assertEquals("PROD 2.0", res[0]);

        res = repo.listModuleSnapshots( "does not exist" );
        assertEquals(0, res.length);

        repo.removeModuleSnapshot( "testPackageSnapshot", "XX" );
        //does nothing... but should not barf...
        try {
            repo.removeModuleSnapshot( "NOTHING SENSIBLE", "XX" );
            fail("should not be able to remove this.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }

        repo.removeModuleSnapshot( "testPackageSnapshot", "PROD 2.0" );
        repo.save();

        res = repo.listModuleSnapshots( "testPackageSnapshot" );
        assertEquals(0, res.length);

        repo.createModuleSnapshot( "testPackageSnapshot", "BOO" );
        res = repo.listModuleSnapshots( "testPackageSnapshot" );
        assertEquals(1, res.length);
        repo.copyModuleSnapshot( "testPackageSnapshot", "BOO", "BOO2" );
        res = repo.listModuleSnapshots( "testPackageSnapshot" );
        assertEquals(2, res.length);



        repo.copyModuleSnapshot( "testPackageSnapshot", "BOO", "BOO2" );
        res = repo.listModuleSnapshots( "testPackageSnapshot" );
        assertEquals(2, res.length);


        assertEquals("BOO", res[0]);
        assertEquals("BOO2", res[1]);
    }

    @Test
    public void testLoadRulePackageItem() {

        ModuleItem rulePackageItem = getRepo().createModule("testLoadRuleRuleItem", "desc");

        rulePackageItem = getRepo().loadModule("testLoadRuleRuleItem");
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItem", rulePackageItem.getName());

        assertEquals("desc", rulePackageItem.getDescription());
        assertEquals(ModuleItem.MODULE_FORMAT, rulePackageItem.getFormat());
        // try loading rule package that was not created
        try {
            rulePackageItem = getRepo().loadModule("anotherRuleRuleItem");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }

    /**
     * This will test getting rules of specific versions out of a package.
     */
    @Test
    public void testPackageRuleVersionExtraction() throws Exception {
        ModuleItem pack = getRepo().createModule( "package extractor", "foo" );

        AssetItem rule1 = pack.addAsset( "rule number 1", "yeah man" );
        rule1.checkin( "version0" );

        AssetItem rule2 = pack.addAsset( "rule number 2", "no way" );
        rule2.checkin( "version0" );

        AssetItem rule3 = pack.addAsset( "rule number 3", "yes way" );
        rule3.checkin( "version0" );

        getRepo().save();

        pack = getRepo().loadModule( "package extractor" );
        List rules = iteratorToList( pack.getAssets() );
        assertEquals(3, rules.size());

        getRepo().createState( "foobar" );

        StateItem state = getRepo().getState( "foobar" );

        rule1.updateState( "foobar" );
        rule1.checkin( "yeah" );

        pack = getRepo().loadModule( "package extractor" );

        rules = iteratorToList( pack.getAssetsWithStatus(state) );

        assertEquals(1, rules.size());

        //now lets try an invalid state tag
        getRepo().createState( "whee" );
        rules = iteratorToList( pack.getAssetsWithStatus( getRepo().getState( "whee" ) ) );
        assertEquals(0, rules.size());

        //and Draft, as we start with Draft, should be able to get all three back
        //although an older version of one of them
        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( StateItem.DRAFT_STATE_NAME )) );
        assertEquals(3, rules.size());

        //now do an update, and pull it out via state
        rule1.updateContent( "new content" );
        getRepo().createState( "extractorState" );
        rule1.updateState( "extractorState" );
        rule1.checkin( "latest" );

        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( "extractorState" )) );
        assertEquals(1, rules.size());
        AssetItem rule = (AssetItem) rules.get( 0 );
        assertEquals("new content", rule.getContent());

        //get the previous one via state

        getRepo().createState( "foobar" );
        rules = iteratorToList( pack.getAssetsWithStatus(getRepo().getState( "foobar" )) );
        assertEquals(1, rules.size());
        AssetItem prior = (AssetItem) rules.get( 0 );

        assertFalse("new content".equals( prior.getContent() ));
    }

    @Test
    public void testIgnoreState() throws Exception {
        ModuleItem pack = getRepo().createModule( "package testIgnoreState", "foo" );

        getRepo().createState( "x" );
        AssetItem rule1 = pack.addAsset( "rule number 1", "yeah man" );
        rule1.updateState( "x" );
        rule1.checkin( "version0" );


        AssetItem rule2 = pack.addAsset( "rule number 2", "no way" );
        rule2.updateState( "x" );
        rule2.checkin( "version0" );

        AssetItem rule3 = pack.addAsset( "rule number 3", "yes way" );
        getRepo().createState( "disabled" );

        rule3.updateState( "disabled" );
        rule3.checkin( "version0" );

        getRepo().save();


        Iterator result = pack.getAssetsWithStatus( getRepo().getState( "x" ), getRepo().getState( "disabled" ) );
        List l = iteratorToList( result );
        assertEquals(2, l.size());
    }

    @Test
    public void testDuplicatePackageName() throws Exception {
        ModuleItem pack = getRepo().createModule( "dupePackageTest", "testing for dupe" );
        assertNotNull(pack.getName());

        try {
            getRepo().createModule( "dupePackageTest", "this should fail" );
            fail("Should not be able to add a package of the same name.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testPackageInstanceWrongNodeType() throws Exception {
        ModuleItem pack = getRepo().loadDefaultModule();
        AssetItem rule = pack.addAsset( "packageInstanceWrongNodeType", "" );

        try {
            new ModuleItem(this.getRepo(), rule.getNode());
            fail("Can't create a package from a rule node.");
        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testLoadRulePackageItemByUUID() throws Exception {

        ModuleItem rulePackageItem = getRepo().createModule("testLoadRuleRuleItemByUUID", "desc");

        String uuid = null;
            uuid = rulePackageItem.getNode().getUUID();


        rulePackageItem = getRepo().loadModuleByUUID(uuid);
        assertNotNull(rulePackageItem);
        assertEquals("testLoadRuleRuleItemByUUID", rulePackageItem.getName());

        // try loading rule package that was not created
        try {
            rulePackageItem = getRepo().loadModuleByUUID("01010101-0101-0101-0101-010101010101");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testAddAssetTrailingWhitespace() {
        ModuleItem pkg = getRepo().createModule("testAddAssetTrailingWhitespace","desc");
        pkg.addAsset("wee ", "");

        assertNotNull(pkg.loadAsset("wee"));
    }

    @Test
    public void testAddRuleRuleItem() {
            ModuleItem rulePackageItem1 = getRepo().createModule("testAddRuleRuleItem","desc");


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

    @Test
    public void testAddRuleItemFromGlobalArea() {
        AssetItem ruleItem1 = loadGlobalArea().addAsset("testAddRuleItemFromGlobalAreaRuleItem", "test description");
        ruleItem1.updateContent( "test content" );
        ruleItem1.checkin( "updated the rule content" );
        
        ModuleItem rulePackageItem2 = getRepo().createModule("testAddRuleItemFromGlobalArea1","desc");
        AssetItem linkedRuleItem1 = rulePackageItem2.addAssetImportedFromGlobalArea(ruleItem1.getName());
        linkedRuleItem1.updateContent( "test content for linked" );
        linkedRuleItem1.checkin( "updated the rule content for linked" );
 
        //test that it is following the head revision
        ruleItem1.updateContent("new lhs");
        ruleItem1.checkin( "updated again" );
        
        Iterator rulesIt2 = rulePackageItem2.getAssets();
        List rules2 = iteratorToList(rulesIt2);
        assertEquals(1, rules2.size());

        AssetItem ai = (AssetItem) rules2.get(0);
        assertTrue(ai.getName().equals("testAddRuleItemFromGlobalAreaRuleItem"));
        assertEquals("new lhs", ai.getContent());
        assertEquals("test description", ai.getDescription());
        assertEquals("updated again", ai.getCheckinComment());
    }
    
    private List iteratorToList(Iterator it) {
        List list = new ArrayList();
        while(it.hasNext()) {
            list.add( it.next() );
        }
        return list;
    }

    @Test
    public void testGetRules() {
        ModuleItem rulePackageItem1 = getRepo().createModule("testGetRules", "desc");

        assertFalse(rulePackageItem1.containsAsset("goober"));


        AssetItem ruleItem1 = rulePackageItem1.addAsset("testGetRules", "desc" );
        ruleItem1.updateContent( "test lhs content" );


        assertTrue(rulePackageItem1.containsAsset( "testGetRules" ));
        assertFalse(rulePackageItem1.containsAsset( "XXXXYYYYZZZZ" ));


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

    @Test
    public void testToString() {
        ModuleItem rulePackageItem1 = getRepo().createModule("testToStringPackage", "desc");

        AssetItem ruleItem1 = rulePackageItem1.addAsset("testToStringPackage", "test lhs content" );
        ruleItem1.updateContent( "test lhs content" );

        assertNotNull(rulePackageItem1.toString());
    }

    @Test
    public void testRemoveRule() {
        ModuleItem rulePackageItem1 = getRepo().createModule("testRemoveRule", "desc");

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
        rulePackageItem1.loadAsset(ruleItem1.getName()).remove();
        rulePackageItem1.rulesRepository.save();
        rules = iteratorToList(rulePackageItem1.getAssets());
        assertEquals(1, rules.size());
        assertEquals("testRemoveRule2", ((AssetItem)rules.get(0)).getName());

        //remove the rule that is following the head revision, make sure the pacakge is now empty
        rulePackageItem1.loadAsset(ruleItem2.getName()).remove();
        rules = iteratorToList(rulePackageItem1.getAssets());
        assertNotNull(rules);
        assertEquals(0, rules.size());
    }

    @Test
    public void testSearchByFormat() throws Exception {
        ModuleItem pkg = getRepo().createModule( "searchByFormat", "" );
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

        Thread.sleep( 150 );

        AssetItemIterator it = pkg.queryAssets( "drools:format='xyz'" );
        List list = iteratorToList( it );
        assertEquals(2, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        assertTrue(list.get( 1 ) instanceof AssetItem);


        AssetItemIterator it2 = pkg.listAssetsByFormat( new String[] {"xyz"} );
        List list2 = iteratorToList( it2 );
        assertEquals(2, list2.size());
        assertTrue(list2.get( 0 ) instanceof AssetItem);
        assertTrue(list2.get( 1 ) instanceof AssetItem);

        it2 = pkg.listAssetsByFormat( new String[] {"xyz", "ABC"} );
        list2 = iteratorToList( it2 );
        assertEquals(3, list2.size());
        assertTrue(list2.get( 0 ) instanceof AssetItem);
        assertTrue(list2.get( 1 ) instanceof AssetItem);
        assertTrue(list2.get( 2 ) instanceof AssetItem);
    }

    @Test
    public void testSearchSharedAssetByFormat() throws Exception {
        AssetItem item = loadGlobalArea().addAsset( "testSearchSharedAssetByFormat", "" );
        item.updateFormat( "testSearchSharedAssetByFormat" );
        item.checkin( "la" );
        
        AssetItemIterator it = loadGlobalArea().queryAssets( "drools:format='testSearchSharedAssetByFormat'" );
        List list = iteratorToList( it );
        assertEquals(1, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        
        ModuleItem pkg2 = getRepo().createModule( "testSearchSharedAssetByFormat", "" );
        getRepo().save();
        AssetItem linkedItem = pkg2.addAssetImportedFromGlobalArea(item.getName());

        Thread.sleep( 150 );

        item = loadGlobalArea().loadAsset("testSearchSharedAssetByFormat");
        assertEquals("testSearchSharedAssetByFormat", item.getFormat());

        it = loadGlobalArea().queryAssets( "drools:format='testSearchSharedAssetByFormat'" );
        list = iteratorToList( it );
        assertEquals(1, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
 
/*        linkedItem = pkg2.loadAsset("testSearchLinkedAssetByFormatAsset2");
        assertNotNull(linkedItem);
        assertEquals("global", linkedItem.getPackageName());

        it = pkg2.queryAssets( "drools:format='xyz'" );
        list = iteratorToList( it );*/
        
        //REVISIT: Not working yet.
        //assertEquals(1, list.size());
        //assertTrue(list.get( 0 ) instanceof AssetItem);
    }

    @Test
    public void testListArchivedAssets() throws Exception {
        ModuleItem pkg = getRepo().createModule( "org.drools.archivedtest", "" );
        getRepo().save();


        AssetItem item = pkg.addAsset( "archivedItem1", "" );
        item.archiveItem( true );
        item.checkin( "la" );

        item = pkg.addAsset( "archivedItem2", "wee" );
        item.archiveItem( true );
        item.checkin( "la" );

        item = pkg.addAsset( "archivedItem3", "wee" );
        item.archiveItem( true );
        item.checkin( "la" );

        item = pkg.addAsset( "NOTarchivedItem", "wee" );
        item.checkin( "la" );


        Thread.sleep( 150 );

        AssetItemIterator it = pkg.listArchivedAssets();

        List list = iteratorToList( it );
        assertEquals(3, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        assertTrue(list.get( 1 ) instanceof AssetItem);
        assertTrue(list.get( 2 ) instanceof AssetItem);


        it = pkg.queryAssets( "", true );

        list = iteratorToList( it );
        assertEquals(4, list.size());
    }

    @Test
    public void testExcludeAssetTypes() throws Exception {
        ModuleItem pkg = getRepo().createModule( "testExcludeAssetTypes", "" );
        getRepo().save();


        AssetItem item = pkg.addAsset( "a1", "" );
        item.updateFormat("drl");
        item.checkin( "la" );

        item = pkg.addAsset( "a2", "wee" );
        item.updateFormat("xls");
        item.checkin( "la" );


        AssetItemIterator it = pkg.listAssetsNotOfFormat(new String[] {"drl"});
        List ls = iteratorToList(it);
        assertEquals(1, ls.size());
        AssetItem as = (AssetItem) ls.get(0);
        assertEquals("a2", as.getName());

        it = pkg.listAssetsNotOfFormat(new String[] {"drl", "wang"});
        ls = iteratorToList(it);
        assertEquals(1, ls.size());
        as = (AssetItem) ls.get(0);
        assertEquals("a2", as.getName());

        it = pkg.listAssetsNotOfFormat(new String[] {"drl", "xls"});
        ls = iteratorToList(it);
        assertEquals(0, ls.size());
    }

    @Test
    public void testSortHistoryByVersionNumber() {
        ModuleItem item = new ModuleItem();
        List l = new ArrayList();

        AssetItem i1 = new MockAssetItem(42);
        AssetItem i2 = new MockAssetItem(1);

        l.add( i2 );
        l.add( i1 );

        assertEquals(i2, l.iterator().next());

        item.sortHistoryByVersionNumber( l );

        assertEquals(i1, l.iterator().next());
    }

    @Test
    public void testMiscProperties() {
        ModuleItem item = getRepo().createModule( "testHeader", "ya" );

        updateHeader( "new header", item );
        item.updateExternalURI( "boo" );
        getRepo().save();
        assertEquals("new header", getHeader(item));
        item = getRepo().loadModule("testHeader");
        assertEquals("new header", getHeader(item));
        assertEquals("boo", item.getExternalURI());
    }

    @Test
    public void testGetFormatAndUpToDate() {
        ModuleItem rulePackageItem1 = getRepo().createModule("testGetFormat",
                "woot");
        assertNotNull(rulePackageItem1);
        assertEquals(ModuleItem.MODULE_FORMAT, rulePackageItem1.getFormat());
        assertFalse(rulePackageItem1.isBinaryUpToDate());
        rulePackageItem1.updateBinaryUpToDate(true);
        assertTrue(rulePackageItem1.isBinaryUpToDate());
        rulePackageItem1.updateBinaryUpToDate(false);
        assertFalse(rulePackageItem1.isBinaryUpToDate());
    }

    @Test
    public void testFormatOtherThanDroolsPackage() {
        ModuleItem rulePackageItem1 = getRepo().createModule(
                "testFormatOtherThanDroolsPackage", "woot");
        assertNotNull(rulePackageItem1);
        // PACKAGE_FORMAT is the default module format
        assertEquals(ModuleItem.MODULE_FORMAT, rulePackageItem1.getFormat());
        rulePackageItem1.updateFormat("soaservice");

        ModuleItem item = getRepo().loadModule(
                "testFormatOtherThanDroolsPackage");
        assertEquals("soaservice", item.getFormat());
    }
    
    public static void updateHeader(String h, ModuleItem pkg) {
        pkg.checkout();
        AssetItem as = null;
        if (pkg.containsAsset("drools")) {
            as = pkg.loadAsset("drools");
        } else {
            as = pkg.addAsset("drools", "");
        }
        as.updateContent(h);
        //as.checkin("");
    }

    public static String getHeader(ModuleItem pkg) {
        if (pkg.containsAsset("drools")) {
            return pkg.loadAsset("drools").getContent();
        } else {
            return "";
        }
    }

    @Test
    public void testPackageCheckinConfig() {
        ModuleItem item = getRepo().createModule( "testPackageCheckinConfig", "description" );

        AssetItem rule = item.addAsset( "testPackageCheckinConfig", "w" );
        rule.checkin( "goo" );

        assertEquals(1, iteratorToList( item.getAssets() ).size());
        updateHeader( "la", item );
        item.checkin( "woot" );

        updateHeader( "we", item );
        item.checkin( "gah" );

//        PackageItem pre = (PackageItem) item.getPrecedingVersion();
//        assertNotNull(pre);
//        assertEquals("la", getHeader(pre));

        AssetItem rule_ = getRepo().loadAssetByUUID( rule.getUUID() );
        assertEquals(rule.getVersionNumber(), rule_.getVersionNumber());

        item = getRepo().loadModule( "testPackageCheckinConfig");
        long v = item.getVersionNumber();
        item.updateCheckinComment( "x" );
        getRepo().save();

        assertEquals(v, item.getVersionNumber());
    }
    
    @Test 
    public void testPackageWorkspaceProperty() throws Exception {
        RulesRepository repo = getRepo();
        ModuleItem item = repo.createModule( "testPackageWorkspaceProperty1", "lalalala" );
        getRepo().save();
        
        String[] workspaces = repo.loadModule(item.getName()).getWorkspaces();

        item.removeWorkspace("workspace1");
        
        workspaces = item.getWorkspaces();
        assertEquals(workspaces.length, 0);

        item.addWorkspace("workspace1");
        item.addWorkspace("workspace2");
        item.addWorkspace("workspace1");
        item.addWorkspace("workspace2");

        workspaces = item.getWorkspaces();
        assertEquals(workspaces.length, 2);
        
        item.removeWorkspace("workspace1");
        item.removeWorkspace("workspace3");

        workspaces = item.getWorkspaces();
        assertEquals(workspaces.length, 1);
        assertTrue((workspaces[0]).equals("workspace2"));
    }

    @Test
    public void testDependencies() throws Exception {
        RulesRepository repo = getRepo();
        ModuleItem item = repo.createModule("testDependencies", "lalalala");
        getRepo().save();

        String[] dependencies = item.getDependencies();
        assertEquals(dependencies.length, 0);

        AssetItem rule = item.addAsset("testDependenciesAsset1", "w");
        rule.checkout();
        rule.checkin("version 1");
        rule.checkout();
        rule.checkin("version 2");
        rule.checkout();
        rule.checkin("version 3");
        
        dependencies = item.getDependencies();
        assertEquals(dependencies.length, 1);
        assertEquals(
                "testDependenciesAsset1?version=LATEST",
                dependencies[0]);
        

        item.updateDependency("testDependenciesAsset1?version=LATEST");
        item.checkin("Update dependency");
        dependencies = item.getDependencies();
        assertEquals(
                "testDependenciesAsset1?version=LATEST",
                dependencies[0]);
        

        item.updateDependency("testDependenciesAsset1?version=2");
        item.checkin("Update dependency");
        dependencies = item.getDependencies();
        assertEquals(
                "testDependenciesAsset1?version=2",
                dependencies[0]);
    }
    
    @Test
    public void testDependenciesWithHistoricalVersion() throws Exception {
        RulesRepository repo = getRepo();
        ModuleItem item = repo.createModule("testDependenciesWithHistoricalVersion", "lalalala");
        getRepo().save();

        String[] dependencies = item.getDependencies();
        assertEquals(dependencies.length, 0);

        AssetItem rule = item.addAsset("testDependenciesWithHistoricalVersionAsset1", "w");
        
        dependencies = item.getDependencies();
        assertEquals(dependencies.length, 1);
        assertEquals(
                "testDependenciesWithHistoricalVersionAsset1?version=LATEST",
                dependencies[0]);
        
        item.checkout();
        item.checkin("v1");
        ModuleItem historicalPackage = getRepo().loadModule("testDependenciesWithHistoricalVersion", 2);
        dependencies = historicalPackage.getDependencies();
        assertEquals(1, dependencies.length);
        //testDependenciesWithHistoricalVersionAsset1's version is 1 because it was forced to be 
        //checked in when the package got checked in. 
        assertEquals(
                "testDependenciesWithHistoricalVersionAsset1?version=1",
                dependencies[0]);
        
        item.checkout();
        item.checkin("v2");
        historicalPackage = getRepo().loadModule("testDependenciesWithHistoricalVersion", 3);
        dependencies = historicalPackage.getDependencies();
        assertEquals(1, dependencies.length);
        //testDependenciesWithHistoricalVersionAsset1's version is 1 because it was forced to be 
        //checked in when the package got checked in. 
        assertEquals(
                "testDependenciesWithHistoricalVersionAsset1?version=1",
                dependencies[0]);
     }

    @Test
    public void testListAssetsByFormatForHistoricalPackage() throws Exception {
    	//Package version 1(Initial version)
        ModuleItem pkg = getRepo().createModule( "testListAssetsByFormatForHistoricalPackage", "" );
        getRepo().save();

        AssetItem item = pkg.addAsset( "testVersionedAssetItemIteratorAsset1", "" );
        item.updateFormat( "xyz" );
        item.checkin( "la" );

        item = pkg.addAsset( "testVersionedAssetItemIteratorAsset2", "wee" );
        item.updateFormat( "xyz" );
        item.checkin( "la" );

        item = pkg.addAsset( "testVersionedAssetItemIteratorAsset3", "wee" );
        item.updateFormat( "ABC" );
        item.checkin( "version 1" );
        item.checkout();
        item.checkin("version 2");
        item.checkout();
        item.checkin("version 3");
        item.checkout();
        item.checkin("version 4");
        
    	//Create package version 2       
        pkg.updateDependency("testVersionedAssetItemIteratorAsset3?version=2");
        pkg.checkin("Update dependency");

        //Create package version 3       
        pkg.checkout();
        pkg.checkin("version 3");
        
        item.checkout();
        item.checkin("version 5");        
        
        
        //Verify package Latest version         
        pkg = getRepo().loadModule("testListAssetsByFormatForHistoricalPackage");
        String[] dependencies = pkg.getDependencies();
        assertEquals(dependencies.length, 3);

        AssetItemIterator it = pkg.listAssetsByFormat( new String[] {"xyz", "ABC"} );
        List list = iteratorToList( it );
        assertEquals(3, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        assertTrue(list.get( 1 ) instanceof AssetItem);
        assertTrue(list.get( 2 ) instanceof AssetItem);
        
        //verify that iterator returns the correct version of assets. By default, the iterator 
        //return the latest versions. 
        it = pkg.listAssetsByFormat( new String[] {"ABC"} );
        assertTrue(it instanceof VersionedAssetItemIterator);
        list = iteratorToList( it );
        assertEquals(1, list.size());
        AssetItem ai = (AssetItem)list.get(0);
        assertEquals(5, ai.getVersionNumber());
        assertEquals("version 5", ai.getCheckinComment());            
        
        //verify that iterator returns the correct version of assets. The version is specified by dependency.        
        it = pkg.listAssetsByFormat( new String[] {"ABC"} );
        assertTrue(it instanceof VersionedAssetItemIterator);
        ((VersionedAssetItemIterator)it).setReturnAssetsWithVersionsSpecifiedByDependencies(true);
        list = iteratorToList( it );
        assertEquals(1, list.size());
        ai = (AssetItem)list.get(0);
        assertEquals(2, ai.getVersionNumber());
        assertEquals("version 2", ai.getCheckinComment());
 
        
        //Verify historical package version 2
        ModuleItem historicalPackage = getRepo().loadModule("testListAssetsByFormatForHistoricalPackage", 2);
/*        PackageHistoryIterator historyIterator = pkg.getHistory();
        PackageItem historicalPackage = null;
        while ( historyIterator.hasNext() ) {
        	PackageItem historical = historyIterator.next();
            long version = historical.getVersionNumber();
            if ( version == 2 ) {
            	historicalPackage = historical;
                break;
            }
        }*/
        
        it = historicalPackage.listAssetsByFormat( new String[] {"xyz", "ABC"} );
        list = iteratorToList( it );
        assertEquals(3, list.size());
        assertTrue(list.get( 0 ) instanceof AssetItem);
        assertTrue(list.get( 1 ) instanceof AssetItem);
        assertTrue(list.get( 2 ) instanceof AssetItem);
        
        //verify that iterator returns the correct version of assets. When package version 2 was created,
        //asset testVersionedAssetItemIteratorAsset3 was at version 4
        it = historicalPackage.listAssetsByFormat( new String[] {"ABC"} );
        assertTrue(it instanceof VersionedAssetItemIterator);
        //((VersionedAssetItemIterator)it).setReturnAssetsWithVersionSpecifiedByDependencies(true);
        list = iteratorToList( it );
        assertEquals(1, list.size());
        ai = (AssetItem)list.get(0);
        assertEquals(4, ai.getVersionNumber());
        assertEquals("version 4", ai.getCheckinComment());           
        
        //verify that iterator returns the correct version of assets. The version is specified by dependency,
        //which is version 2.
        it = historicalPackage.listAssetsByFormat( new String[] {"ABC"} );
        assertTrue(it instanceof VersionedAssetItemIterator);
        ((VersionedAssetItemIterator)it).setReturnAssetsWithVersionsSpecifiedByDependencies(true);
        list = iteratorToList( it );
        assertEquals(1, list.size());
        ai = (AssetItem)list.get(0);
        assertEquals(2, ai.getVersionNumber());
        assertEquals("version 2", ai.getCheckinComment()); 
    }
    
    static class MockAssetItem extends AssetItem {
        private long version;

        MockAssetItem(long ver) {
            this.version = ver ;
        }

        public long getVersionNumber() {
            return this.version;
        }

        public boolean equals(Object in) {
            return in == this;
        }

        public String toString() {
            return Long.toString( this.version );
        }
    }
}
