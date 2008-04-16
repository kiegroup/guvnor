package org.drools.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import junit.framework.TestCase;

public class RulesRepositoryTest extends TestCase {

    public void testDefaultPackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        Iterator it = repo.listPackages();
        boolean foundDefault = false;
        while(it.hasNext()) {
            PackageItem item = (PackageItem) it.next();
            if (item.getName().equals( RulesRepository.DEFAULT_PACKAGE )) {
                foundDefault = true;
            }
        }
        assertTrue(foundDefault);

        PackageItem def = repo.loadDefaultPackage();
        assertNotNull(def);
        assertEquals(RulesRepository.DEFAULT_PACKAGE, def.getName());

        String userId = repo.getSession().getUserID();
        assertNotNull(userId);
        assertFalse(userId.equals( "" ));

    }

    public void testAddVersionARule() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pack = repo.createPackage( "testAddVersionARule", "description" );
        repo.save();

        AssetItem rule = pack.addAsset( "my rule", "foobar" );
        assertEquals("my rule", rule.getName());

        rule.updateContent( "foo foo" );
        rule.checkin( "version0" );

        pack.addAsset( "other rule", "description" );

        rule.updateContent( "foo bar" );
        rule.checkin( "version1" );

        PackageItem pack2 =  repo.loadPackage( "testAddVersionARule" );

        Iterator it =  pack2.getAssets();

        it.next();
        it.next();

        assertFalse(it.hasNext());

        AssetItem prev = (AssetItem) rule.getPrecedingVersion();

        assertEquals("foo bar", rule.getContent());
        assertEquals("foo foo", prev.getContent());



    }

    public void testFindByState() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pkg = repo.createPackage("testFindByStatePackage", "heheheh");
        AssetItem asset1 = pkg.addAsset("asset1", "");
        AssetItem asset2 = pkg.addAsset("asset2", "");
        repo.createState("testFindByState");
        repo.save();
        asset1.updateState("testFindByState");
        asset2.updateState("testFindByState");
        asset1.checkin("");
        asset2.checkin("");

        AssetPageList list = repo.findAssetsByState("testFindByState", true, 0, -1);
        assertEquals(2, list.assets.size());


    }

    public void testFindRulesByName() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        repo.loadDefaultPackage().addAsset( "findRulesByNamex1", "X" );
        repo.loadDefaultPackage().addAsset( "findRulesByNamex2", "X" );
        repo.save();

        List list = iteratorToList(repo.findAssetsByName( "findRulesByNamex1" ));
	    assertEquals(1, list.size());

        list = iteratorToList(repo.findAssetsByName( "findRulesByNamex2" ));
        assertEquals(1, list.size());


        list = iteratorToList( repo.findAssetsByName( "findRulesByNamex%") );
        assertEquals(2, list.size());

        repo.createPackageSnapshot( RulesRepository.DEFAULT_PACKAGE, "testFindRulesByName" );
        repo.save();

        list = iteratorToList(repo.findAssetsByName( "findRulesByNamex2" ));
        AssetItem item = (AssetItem)list.get( 0 );
        assertEquals("findRulesByNamex2", item.getName());
        assertEquals("X", item.getDescription());
        assertEquals(1, list.size());

        list = iteratorToList( repo.findAssetsByName( "findRulesByNamex%" ) );
        assertEquals(2, list.size());


    }


    public void testLoadRuleByUUIDWithConcurrentSessions() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();

        PackageItem rulePackageItem = repo.loadDefaultPackage();
        AssetItem rule = rulePackageItem.addAsset( "testLoadRuleByUUID", "this is a description");

        repo.save();

        String uuid = rule.getNode().getUUID();

        AssetItem loaded = repo.loadAssetByUUID(uuid);
        assertNotNull(loaded);
        assertEquals("testLoadRuleByUUID", loaded.getName());
        assertEquals( "this is a description", loaded.getDescription());

        long oldVersionNumber = loaded.getVersionNumber();

        loaded.updateContent( "xxx" );
        loaded.checkin( "woo" );




        AssetItem reload = repo.loadAssetByUUID( uuid );
        assertEquals("testLoadRuleByUUID", reload.getName());
        assertEquals("xxx", reload.getContent());
        System.out.println(reload.getVersionNumber());
        System.out.println(loaded.getVersionNumber());
        assertFalse(reload.getVersionNumber() ==  oldVersionNumber );


        // try loading rule package that was not created
        try {
            repo.loadAssetByUUID("01010101-0101-0101-0101-010101010101");
            fail("Exception not thrown loading rule package that was not created.");
        } catch (RulesRepositoryException e) {
            // that is OK!
            assertNotNull(e.getMessage());
        }

        //now test concurrent session access...

        AssetItem asset1 = repo.loadDefaultPackage().addAsset( "testMultiSession", "description" );
        asset1.updateContent( "yeah" );
        asset1.checkin( "boo" );
        uuid = asset1.getUUID();
        asset1.updateState( "Draft" );
        repo.save();

        Session s2 = repo.getSession().getRepository().login(new SimpleCredentials("fdd", "password".toCharArray()));

        RulesRepository repo2 = new RulesRepository(s2);

        AssetItem asset2 = repo2.loadAssetByUUID( uuid );
        asset2.updateContent( "yeah 42" );
        asset2.checkin( "yeah" );

        asset1 = repo.loadAssetByUUID( uuid );
        assertEquals("yeah 42", asset1.getContent());
        asset1.updateContent( "yeah 43" );
        asset1.checkin( "la" );

        asset2 = repo2.loadAssetByUUID( uuid );
        assertEquals( "yeah 43", asset2.getContent() );
    }

    public void testAddRuleCalendarWithDates() {
        RulesRepository rulesRepository = RepositorySessionUtil.getRepository();


            Calendar effectiveDate = Calendar.getInstance();
            Calendar expiredDate = Calendar.getInstance();
            expiredDate.setTimeInMillis(effectiveDate.getTimeInMillis() + (1000 * 60 * 60 * 24));
            AssetItem ruleItem1 = rulesRepository.loadDefaultPackage().addAsset("testAddRuleCalendarCalendar", "desc");
            ruleItem1.updateDateEffective( effectiveDate );
            ruleItem1.updateDateExpired( expiredDate );

            assertNotNull(ruleItem1);
            assertNotNull(ruleItem1.getNode());
            assertEquals(effectiveDate, ruleItem1.getDateEffective());
            assertEquals(expiredDate, ruleItem1.getDateExpired());

            ruleItem1.checkin( "ho " );
    }

    public void testGetState() {
        RulesRepository rulesRepository = RepositorySessionUtil.getRepository();

            StateItem state0 = rulesRepository.createState( "testGetState" );
            assertNotNull(state0);
            assertEquals("testGetState", state0.getName());
            StateItem stateItem1 = rulesRepository.getState("testGetState");
            assertNotNull(stateItem1);
            assertEquals("testGetState", stateItem1.getName());

            StateItem stateItem2 = rulesRepository.getState("testGetState");
            assertNotNull(stateItem2);
            assertEquals("testGetState", stateItem2.getName());
            assertEquals(stateItem1, stateItem2);
    }

    public void testGetTag() {
            RulesRepository rulesRepository = RepositorySessionUtil.getRepository();

            CategoryItem root = rulesRepository.loadCategory( "/" );
            CategoryItem tagItem1 = root.addCategory( "testGetTag", "ho");
            assertNotNull(tagItem1);
            assertEquals("testGetTag", tagItem1.getName());
            assertEquals("testGetTag", tagItem1.getFullPath());

            CategoryItem tagItem2 = rulesRepository.loadCategory("testGetTag");
            assertNotNull(tagItem2);
            assertEquals("testGetTag", tagItem2.getName());
            assertEquals(tagItem1, tagItem2);

            //now test getting a tag down in the tag hierarchy
            CategoryItem tagItem3 = tagItem2.addCategory( "TestChildTag1", "ka");
            assertNotNull(tagItem3);
            assertEquals("TestChildTag1", tagItem3.getName());
            assertEquals("testGetTag/TestChildTag1", tagItem3.getFullPath());
    }



    public void testListPackages() {
        RulesRepository rulesRepository = RepositorySessionUtil.getRepository();
            rulesRepository.createPackage("testListPackages", "desc");

            assertTrue(rulesRepository.containsPackage( "testListPackages" ));
            assertFalse(rulesRepository.containsPackage( "XXXXXXX" ));

            Iterator it = rulesRepository.listPackages();
            assertTrue(it.hasNext());

            boolean found = false;
            while ( it.hasNext() ) {
                PackageItem element = (PackageItem) it.next();
                if (element.getName().equals( "testListPackages" ))
                {
                    found = true;
                    break;
                }
                System.out.println(element.getName());
            }
            assertTrue(found);

    }

    /**
     * Here we are testing to make sure that category links don't pick up stuff in snapshots area.
     */
    public void testCategoriesAndSnapshots() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.loadCategory( "/" ).addCategory( "testCategoriesAndSnapshots", "X" );

        PackageItem pkg = repo.createPackage( "testCategoriesAndSnapshots", "");
        pkg.addAsset( "testCat1", "x", "/testCategoriesAndSnapshots", "drl");
        pkg.addAsset( "testCat2", "x", "/testCategoriesAndSnapshots", "drl");
        repo.save();

        List items = repo.findAssetsByCategory( "/testCategoriesAndSnapshots", 0, -1 ).assets;
        assertEquals(2, items.size());

        repo.createPackageSnapshot( "testCategoriesAndSnapshots", "SNAP 1" );
        items = repo.findAssetsByCategory( "testCategoriesAndSnapshots", 0, -1  ).assets;
        assertEquals(2, items.size());


    }

    public void testMoveRulePackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pkg = repo.createPackage( "testMove", "description" );
        AssetItem r = pkg.addAsset( "testMove", "description" );
        r.checkin( "version0" );
        String uuid = r.getUUID();
        assertEquals("testMove", r.getPackageName());

        repo.save();

        assertEquals(1, iteratorToList( pkg.getAssets()).size());

        repo.createPackage( "testMove2", "description" );
        repo.moveRuleItemPackage( "testMove2", r.node.getUUID(), "explanation" );

        pkg = repo.loadPackage( "testMove" );
        assertEquals(0, iteratorToList( pkg.getAssets() ).size());

        pkg = repo.loadPackage( "testMove2" );
        assertEquals(1, iteratorToList( pkg.getAssets() ).size());

        r = (AssetItem) pkg.getAssets().next();
        assertEquals("testMove", r.getName());
        assertEquals("testMove2", r.getPackageName());
        assertEquals("explanation", r.getCheckinComment());

        AssetItem p = (AssetItem) r.getPrecedingVersion();
        assertEquals("testMove", p.getPackageName());
        assertEquals("version0", p.getCheckinComment());
        assertEquals(uuid, r.getUUID());
    }

    public void testCopyAsset() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.createPackage( "testCopyAsset", "asset" );
        AssetItem item = repo.loadDefaultPackage().addAsset( "testCopyAssetSource", "desc" );
        item.updateContent( "la" );
        item.checkin( "" );
        item.updateDescription( "mmm" );
        item.checkin( "again" );
        assertEquals(2, item.getVersionNumber());

        String uuid = repo.copyAsset( item.getUUID(), "testCopyAsset", "testCopyAssetDestination" );
        AssetItem dest = repo.loadAssetByUUID( uuid );
        assertEquals("la", dest.getContent());
        assertEquals("testCopyAsset", dest.getPackageName());
        assertFalse(uuid.equals( item.getUUID() ));
        assertEquals(1, dest.getVersionNumber());
    }

    public void testRenameAsset() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        repo.createPackage( "testRenameAsset", "asset" );
        AssetItem item = repo.loadPackage("testRenameAsset").addAsset( "testRenameAssetSource", "desc" );
        item.updateContent( "la" );
        item.checkin( "" );

        String uuid = repo.renameAsset( item.getUUID(), "testRename2");
        item = repo.loadAssetByUUID( uuid );
        assertEquals("testRename2", item.getName());
        assertEquals("testRename2", item.getTitle());

        List assets = iteratorToList( repo.loadPackage( "testRenameAsset" ).getAssets() );
        assertEquals(1, assets.size());
        item = (AssetItem) assets.get( 0 );
        assertEquals("testRename2", item.getName());
        assertEquals("la", item.getContent());

    }

    public void testRenamePackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem original = repo.createPackage( "testRenamePackage", "asset" );
        List packagesOriginal = iteratorToList( repo.listPackages() );
        AssetItem item = repo.loadPackage("testRenamePackage").addAsset( "testRenameAssetSource", "desc" );
        item.updateContent( "la" );
        item.checkin( "" );

        String uuid = repo.renamePackage( original.getUUID(), "testRenamePackage2");

        PackageItem pkg = repo.loadPackageByUUID( uuid );
        assertEquals("testRenamePackage2", pkg.getName());

        List assets = iteratorToList( repo.loadPackage( "testRenamePackage2" ).getAssets() );
        assertEquals(1, assets.size());
        item = (AssetItem) assets.get( 0 );
        assertEquals("testRenameAssetSource", item.getName());
        assertEquals("la", item.getContent());
        assertEquals("testRenamePackage2", item.getPackageName());

        List packageFinal = iteratorToList( repo.listPackages() );
        assertEquals(packagesOriginal.size(), packageFinal.size());

    }



    public void testCopyPackage() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem source = repo.createPackage( "testCopyPackage", "asset" );
        AssetItem item = source.addAsset( "testCopyPackage", "desc" );
        item.updateContent( "la" );
        item.checkin( "" );
        repo.save();

        repo.copyPackage( "testCopyPackage", "testCopyPackage2" );
        PackageItem dest = repo.loadPackage( "testCopyPackage2" );
        assertNotNull(dest);
        assertFalse( source.getUUID().equals( dest.getUUID() ));

        assertEquals(1, iteratorToList( dest.getAssets()).size());
        AssetItem item2 = (AssetItem) dest.getAssets().next();

        assertEquals("testCopyPackage", item.getPackageName());
        assertEquals("testCopyPackage2", item2.getPackageName());


        item2.updateContent( "goober choo" );
        item2.checkin( "yeah" );

        assertEquals("la", item.getContent());


        try {
            repo.copyPackage( "testCopyPackage", "testCopyPackage2" );
            fail("should not be able to copy when existing.");

        } catch (RulesRepositoryException e) {
            assertNotNull(e.getMessage());
        }


    }


    public void testListStates()  {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        StateItem[] items = repo.listStates();
        assertTrue(items.length > 0);

        repo.createState( "testListStates" );

        StateItem[] items2 = repo.listStates();
        assertEquals(items.length + 1, items2.length);
    }

    public void testImportExport() {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        byte []repository_unitest;
        byte []repository_backup;

        try {

            repository_backup = repo.dumpRepositoryXml();
            assertNotNull( repository_backup );

            repo.createPackage( "testImportExport", "nodescription" );
            repository_unitest = repo.dumpRepositoryXml();
            repo.importRulesRepository( repository_backup );
            assertFalse( repo.containsPackage( "testImportExport" ) );
            repo.importRulesRepository( repository_unitest );
            assertTrue( repo.containsPackage( "testImportExport" ) );
        } catch ( Exception e ) {
            fail("Can't throw any exception.");
            e.printStackTrace();
        }
    }

    public void testExportZippedRepository () throws PathNotFoundException, IOException, RepositoryException {

        RulesRepository repo = RepositorySessionUtil.getRepository();
        byte []repository_unitest;

        repository_unitest =  repo.exportRulesRepository();

        ByteArrayInputStream bin = new ByteArrayInputStream(repository_unitest);
        ZipInputStream zis = new ZipInputStream (bin);

        ZipEntry entry =  zis.getNextEntry();
        assertEquals( entry.getName() , "repository_export.xml" );
        assertFalse( entry.isDirectory() );
    }


    public static<T> List<T> iteratorToList(Iterator<T> it) {
        List<T> list = new ArrayList<T>();
        while(it.hasNext()) {
            list.add( it.next() );
        }
        return list;
    }
}
