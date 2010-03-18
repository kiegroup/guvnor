package org.drools.guvnor.server.util;

import junit.framework.Assert;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.verifier.VerifierConfiguration;
import org.junit.Before;
import org.junit.Test;

public class VerifierRunnerTest {

    private ServiceImplementation serviceImplementation;
    private PackageItem           packageItem;

    @Before
    public void setUp() throws Exception {
        serviceImplementation = new ServiceImplementation();

        serviceImplementation.repository = new RulesRepository( TestEnvironmentSessionHelper.getSession() );

        packageItem = serviceImplementation.getRulesRepository().createPackage( "VerifierRunnerTest",
                                                                                "" );
    }

    @Test
    public void verifyPackageItem() {
        VerifierRunner verifierRunner = checkinDRLAssetToPackage( "/VerifierCauseTrace.drl" );

        AnalysisReport report = verifierRunner.verify( packageItem,
                                                       VerifierConfiguration.VERIFYING_SCOPE_KNOWLEDGE_PACKAGE );

        Assert.assertNotNull( report );
        Assert.assertEquals( 1,
                             report.warnings.length );

        System.out.println( report.warnings[0].description );
    }

    private VerifierRunner checkinDRLAssetToPackage(String assetName) {
        VerifierRunner verifierRunner = new VerifierRunner();
        AssetItem asset = packageItem.addAsset( "verifyPackageItem",
                                                "" );
        asset.updateFormat( AssetFormats.DRL );

        asset.updateContent( IO.read( this.getClass().getResourceAsStream( assetName ) ) );
        asset.checkin( "" );

        return verifierRunner;
    }
}
