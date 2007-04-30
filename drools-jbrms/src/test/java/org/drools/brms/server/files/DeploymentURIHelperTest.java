package org.drools.brms.server.files;

import junit.framework.TestCase;

public class DeploymentURIHelperTest extends TestCase {

    public void testGetPackageToExport() throws Exception {
        String uri = "/org.drools.brms.JBRMS/asset/boo/ya+man";
        //from getPathInfo() on req.

        PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(uri);

        assertEquals( "ya man", helper.getVersion() );
        assertEquals( "boo", helper.getPackageName() );
        assertFalse(helper.isLatest());

        helper = new PackageDeploymentURIHelper("/foo/bar/LATEST");
        assertTrue(helper.isLatest());
        assertEquals("bar", helper.getPackageName());
        
    }

}
