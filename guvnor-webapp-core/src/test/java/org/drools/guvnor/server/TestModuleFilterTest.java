package org.drools.guvnor.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.client.rpc.Module;
import org.junit.Test;

public class TestModuleFilterTest {
    @Test
    public void testAssetItemFilterAndDoesNotAccept() {
        ModuleFilter filter = new ModuleFilter(null);
        assertFalse( filter.accept( new Object(),
                                    "action" ) );
    }

    @Test
    public void testAssetItemFilterAndAccepts() {
        ModuleFilter filter = new ModuleFilter(null);
        assertTrue( filter.accept( new Module(),
                                   "action" ) );
    }

    @Test
    public void testIsNullSafe() {
        ModuleFilter filter = new ModuleFilter(null);
        assertFalse( filter.accept( null,
                                    null ) );
    }
}
