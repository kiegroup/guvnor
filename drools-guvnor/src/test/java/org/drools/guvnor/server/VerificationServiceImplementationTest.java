/**
 * Copyright 2010 JBoss Inc
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

import junit.framework.Assert;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.server.util.IO;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.Before;
import org.junit.Test;

public class VerificationServiceImplementationTest {

    private ServiceImplementation serviceImplementation;
    private VerificationService   verificationService;

    @Before
    public void setUp() {
        serviceImplementation = new ServiceImplementation();
        try {
            serviceImplementation.repository = new RulesRepository( TestEnvironmentSessionHelper.getSession() );
        } catch ( Exception e ) {
            Assert.fail( "Failed to set up rules repository" );
        }

        verificationService = new VerificationServiceImplementation();
    }

    @Test
    public void testVerifierCauseTrace() throws Exception {
        PackageItem pkg = serviceImplementation.repository.createPackage( "testVerifierCauseTrace",
                                                                          "" );
        AssetItem asset = pkg.addAsset( "SomeDRL",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );

        asset.updateContent( IO.read( this.getClass().getResourceAsStream( "/VerifierCauseTrace.drl" ) ) );
        asset.checkin( "" );

        AnalysisReport report = verificationService.analysePackage( pkg.getUUID() );
        Assert.assertNotNull( report );
        Assert.assertEquals( 1,
                             report.warnings.length );

    }

    @Test
    public void testVerifier() throws Exception {
        PackageItem pkg = serviceImplementation.repository.createPackage( "testVerifier",
                                                                          "" );
        AssetItem asset = pkg.addAsset( "SomeDRL",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );

        asset.updateContent( IO.read( this.getClass().getResourceAsStream( "/AnalysisSample.drl" ) ) );
        asset.checkin( "" );

        AnalysisReport report = verificationService.analysePackage( pkg.getUUID() );
        Assert.assertNotNull( report );
        Assert.assertEquals( 0,
                             report.errors.length );
        Assert.assertEquals( 8,
                             report.warnings.length );
        Assert.assertEquals( 1,
                             report.notes.length );
        Assert.assertEquals( 3,
                             report.factUsages.length );

        Assert.assertNotNull( report.notes[0].description );
        Assert.assertNull( report.notes[0].reason );
        Assert.assertEquals( 2,
                             report.notes[0].causes.length );
        Assert.assertNotNull( report.notes[0].causes[0] );
        Assert.assertNotNull( report.notes[0].causes[1] );

        Assert.assertEquals( "Message",
                             report.factUsages[0].name );
        Assert.assertEquals( "RedundancyPattern",
                             report.factUsages[1].name );
        Assert.assertEquals( "RedundancyPattern2",
                             report.factUsages[2].name );

        Assert.assertEquals( 0,
                             report.factUsages[0].fields.length );
        Assert.assertEquals( 1,
                             report.factUsages[1].fields.length );
        Assert.assertEquals( 1,
                             report.factUsages[2].fields.length );

        Assert.assertEquals( "a",
                             report.factUsages[1].fields[0].name );
        Assert.assertEquals( "a",
                             report.factUsages[2].fields[0].name );

        Assert.assertEquals( 3,
                             report.factUsages[1].fields[0].rules.length );
        Assert.assertEquals( 2,
                             report.factUsages[2].fields[0].rules.length );

        Assert.assertNotNull( report.factUsages[1].fields[0].rules[0] );

    }
}
