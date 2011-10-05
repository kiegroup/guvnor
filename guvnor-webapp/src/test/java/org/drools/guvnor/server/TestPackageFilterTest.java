package org.drools.guvnor.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.client.rpc.PackageConfigData;
import org.junit.Test;

public class TestPackageFilterTest {
    @Test
    public void testAssetItemFilterAndDoesNotAccept() {
        PackageFilter filter = new PackageFilter(null);
        assertFalse( filter.accept( new Object(),
                                    "action" ) );
    }

    @Test
    public void testAssetItemFilterAndAccepts() {
        PackageFilter filter = new PackageFilter(null);
        assertTrue( filter.accept( new PackageConfigData(),
                                   "action" ) );
    }

    @Test
    public void testIsNullSafe() {
        PackageFilter filter = new PackageFilter(null);
        assertFalse( filter.accept( null,
                                    null ) );
    }
}
