/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.junit.Test;

import static org.junit.Assert.*;

public class SampleRepositoryTest extends GuvnorTestBase {

    @Test
    public void testImportSampleRepository() throws Exception {
        repositoryPackageService.installSampleRepository();
        PackageConfigData[] cfgs = repositoryPackageService.listPackages();
        assertEquals( 2,
                      cfgs.length );
        assertTrue( cfgs[0].getName().equals( "mortgages" ) || cfgs[1].getName().equals( "mortgages" ) );
        String puuid = (cfgs[0].getName().equals( "mortgages" )) ? cfgs[0].getUuid() : cfgs[1].getUuid();
        BulkTestRunResult bulkTestRunResult = repositoryPackageService.runScenariosInPackage( puuid );
        assertNull(bulkTestRunResult.getResult());
    }

}
