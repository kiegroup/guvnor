package org.drools.repository.events;

import junit.framework.TestCase;
import org.drools.repository.RulesRepository;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import org.drools.repository.RepositorySessionUtil;

import java.io.ByteArrayInputStream;

/**
 * @author Michael Neale
 */
public class StorageEventManagerTest extends TestCase {

    @Override
    protected void tearDown() throws Exception {
        StorageEventManager.le = null;
        StorageEventManager.se = null;
    }


    public void testLoadEvent() {
        System.setProperty("guvnor.loadEventListener", "org.drools.repository.events.MockLoadEvent");
        LoadEvent le = StorageEventManager.loadEvent();
        assertNotNull(le);
        assertTrue(le instanceof MockLoadEvent);

        System.setProperty("guvnor.loadEventListener", "");
        assertNull(StorageEventManager.loadEvent());


        StorageEventManager.le = le;
        assertNotNull(StorageEventManager.getLoadEvent());
        assertTrue(StorageEventManager.hasLoadEvent());

        StorageEventManager.le = null;
        assertFalse(StorageEventManager.hasLoadEvent());


    }


    public void testSaveEvent() {
        System.setProperty("guvnor.saveEventListener", "org.drools.repository.events.MockSaveEvent");
        SaveEvent le = StorageEventManager.saveEvent();
        assertNotNull(le);
        assertTrue(le instanceof MockSaveEvent);

        System.setProperty("guvnor.saveEventListener", "");
        assertNull(StorageEventManager.saveEvent());


        StorageEventManager.se = le;
        assertNotNull(StorageEventManager.getSaveEvent());
        assertTrue(StorageEventManager.hasSaveEvent());

        StorageEventManager.se = null;
        assertFalse(StorageEventManager.hasSaveEvent());


    }


    public void testAssetContentCallbacks() {

        StorageEventManager.le = null;
        StorageEventManager.se = null;

        RulesRepository repo = getRepo();
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset("testAssetContentCallbacks", "");
        assertEquals(0, asset.getContentLength());
        asset.updateContent("boo");
        asset.checkin("");

        asset.updateContent("whee");
        StorageEventManager.le = new MockLoadEvent();
        StorageEventManager.se = new MockSaveEvent();

        asset.checkin("");
        assertTrue(((MockSaveEvent)StorageEventManager.se).checkinCalled);


        
        asset.getContent();
        assertTrue(((MockLoadEvent) StorageEventManager.le).loadCalled);
        

    }


    private RulesRepository getRepo() {
        return RepositorySessionUtil.getRepository();
    }

}
