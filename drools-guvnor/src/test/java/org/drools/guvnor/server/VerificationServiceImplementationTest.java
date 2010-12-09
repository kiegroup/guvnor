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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.server.repository.MailboxService;
import org.drools.guvnor.server.util.IO;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.junit.After;
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
        	e.printStackTrace();
            fail( "Failed to set up rules repository" );
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
        assertNotNull( report );
        assertEquals( 1,
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
        assertNotNull( report );
        assertEquals( 0,
                             report.errors.length );
        assertEquals( 8,
                             report.warnings.length );
        assertEquals( 1,
                             report.notes.length );
        assertEquals( 3,
                             report.factUsages.length );

        assertNotNull( report.notes[0].description );
        assertNull( report.notes[0].reason );
        assertEquals( 2,
                             report.notes[0].causes.length );
        assertNotNull( report.notes[0].causes[0] );
        assertNotNull( report.notes[0].causes[1] );

        assertEquals( "Message",
                             report.factUsages[0].name );
        assertEquals( "RedundancyPattern",
                             report.factUsages[1].name );
        assertEquals( "RedundancyPattern2",
                             report.factUsages[2].name );

        assertEquals( 0,
                             report.factUsages[0].fields.length );
        assertEquals( 1,
                             report.factUsages[1].fields.length );
        assertEquals( 1,
                             report.factUsages[2].fields.length );

        assertEquals( "a",
                             report.factUsages[1].fields[0].name );
        assertEquals( "a",
                             report.factUsages[2].fields[0].name );

        assertEquals( 3,
                             report.factUsages[1].fields[0].rules.length );
        assertEquals( 2,
                             report.factUsages[2].fields[0].rules.length );

        assertNotNull( report.factUsages[1].fields[0].rules[0] );

    }
    
    @After
    public void tearDown() throws Exception {
    	MailboxService.getInstance().stop();
        TestEnvironmentSessionHelper.shutdown();
    }
}
