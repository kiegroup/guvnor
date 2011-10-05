/*
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

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.server.util.IO;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class VerificationServiceImplementationIntegrationTest extends GuvnorTestBase {

    @Inject
    private VerificationServiceImplementation verificationService;

    @Test
    public void testVerifierCauseTrace() throws Exception {
        PackageItem pkg = rulesRepository.createPackage("testVerifierCauseTrace",
                "");
        AssetItem asset = pkg.addAsset( "SomeDRL",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );

        asset.updateContent( IO.read( this.getClass().getResourceAsStream( "/VerifierCauseTrace.drl" ) ) );
        asset.checkin( "" );

        AnalysisReport report = verificationService.analysePackage( pkg.getUUID() );
        assertNotNull( report );
        assertEquals( 0,
                      report.warnings.length );

    }

    @Test
    public void testVerifier() throws Exception {
        PackageItem pkg = rulesRepository.createPackage("testVerifier",
                "");
        AssetItem asset = pkg.addAsset( "SomeDRL",
                                        "" );
        asset.updateFormat( AssetFormats.DRL );

        asset.updateContent( IO.read( this.getClass().getResourceAsStream( "/AnalysisSample.drl" ) ) );
        asset.checkin( "" );

        AnalysisReport report = verificationService.analysePackage( pkg.getUUID() );
        assertNotNull( report );
        assertEquals( 0,
                      report.errors.length );
        assertEquals( 7,
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
        assertEquals( 0,
                      report.factUsages[1].fields.length );
        assertEquals( 0,
                      report.factUsages[2].fields.length );

    }

}
