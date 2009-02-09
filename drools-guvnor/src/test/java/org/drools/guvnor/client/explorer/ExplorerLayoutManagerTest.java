package org.drools.guvnor.client.explorer;

import junit.framework.TestCase;



/**
 * @author Michael Neale
 */

@SuppressWarnings({"NonJREEmulationClassesInClientCode"})
public class ExplorerLayoutManagerTest extends TestCase {
    public void testHistoryToken() {
        ExplorerLayoutManager.BookmarkInfo bi = ExplorerLayoutManager.handleHistoryToken("");
        assertNotNull(bi);
        assertTrue(bi.showChrome);
        assertFalse(bi.loadAsset);
        assertNotNull(ExplorerLayoutManager.handleHistoryToken(null));

        bi = ExplorerLayoutManager.handleHistoryToken("asset=123&nochrome");
        assertTrue(bi.loadAsset);
        assertFalse(bi.showChrome);
        assertEquals("123", bi.assetId);

        bi = ExplorerLayoutManager.handleHistoryToken("asset=123&nochrome=true");
        assertTrue(bi.loadAsset);
        assertFalse(bi.showChrome);
        assertEquals("123", bi.assetId);

        


    }
}
