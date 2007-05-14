package org.drools.brms.server.files;

import junit.framework.TestCase;

public class DeploymentURIHelperTest extends TestCase {

    public void testGetPackageToExport() throws Exception {
        String uri = "/org.drools.brms.JBRMS/package/boo/ya+man";

        PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(uri);

        assertEquals( "ya man", helper.getVersion() );
        assertEquals( "boo", helper.getPackageName() );
        assertFalse(helper.isLatest());

        helper = new PackageDeploymentURIHelper("/asset/bar/LATEST");
        assertTrue(helper.isLatest());
        assertEquals("bar", helper.getPackageName());
        
    }

}
