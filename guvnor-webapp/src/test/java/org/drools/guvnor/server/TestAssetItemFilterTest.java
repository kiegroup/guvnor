package org.drools.guvnor.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.repository.AssetItem;
import org.junit.Test;

public class TestAssetItemFilterTest {
    @Test
    public void testAssetItemFilterAndDoesNotAccept() {
        AssetItemFilter filter = new AssetItemFilter();
        assertFalse( filter.accept( new Object(),
                                    "action" ) );
    }

    @Test
    public void testAssetItemFilterAndAccepts() {
        AssetItemFilter filter = new AssetItemFilter();
        assertTrue( filter.accept( new AssetItem(),
                                   "action" ) );
    }

    @Test
    public void testIsNullSafe() {
        PackageFilter filter = new PackageFilter();
        assertFalse( filter.accept( null,
                                    null ) );
    }
}
