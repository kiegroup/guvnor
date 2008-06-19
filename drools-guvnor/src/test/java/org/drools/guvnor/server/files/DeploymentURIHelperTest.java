package org.drools.guvnor.server.files;
/*
 * Copyright 2005 JBoss Inc
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



import org.drools.guvnor.server.files.PackageDeploymentURIHelper;

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
        assertFalse(helper.isAsset());
    }

    public void testGetPackageWithDRL() throws Exception {
    	String uri = "/org.drools.brms.JBRMS/package/boo/ya+man.drl";
        PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(uri);

        assertTrue(helper.isSource());
        assertEquals( "ya man", helper.getVersion() );
        assertEquals( "boo", helper.getPackageName() );
        assertFalse(helper.isLatest());
        assertFalse(helper.isAsset());

    }

    public void testGetAssetDRL() throws Exception {
    	String uri = "/org.drools.brms.JBRMS/package/packName/LATEST/assetName.drl";
        PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(uri);
        assertTrue(helper.isSource());
        assertEquals("LATEST", helper.getVersion());
        assertEquals("packName", helper.getPackageName());
        assertEquals("assetName", helper.getAssetName());
        assertTrue(helper.isAsset());


    }

}