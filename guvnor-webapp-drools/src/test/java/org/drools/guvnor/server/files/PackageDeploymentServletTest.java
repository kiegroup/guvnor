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

package org.drools.guvnor.server.files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.drools.guvnor.server.GuvnorTestBase;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.util.codec.Base64;
import org.junit.Before;
import org.junit.Test;

public class PackageDeploymentServletTest extends GuvnorTestBase {

    @Inject
    private PackageDeploymentServlet packageDeploymentServlet;

    /*
        @Test
        public void testLoadingRules() throws Exception {
            RulesRepository repo = getRulesRepository();

            RepositoryPackageService impl = new RepositoryPackageService();
            impl.setRulesRepositoryForTest( repo );

            PackageItem pkg = repo.createPackage( "testPDSGetPackage",
                                                  "" );
            AssetItem header = pkg.addAsset( "drools",
                                             "" );
            header.updateFormat( "package" );
            header.updateContent( "import org.drools.guvnor.server.files.SampleFact\n global org.drools.guvnor.server.files.SampleFact sf" );
            header.checkin( "" );

            AssetItem asset = pkg.addAsset( "someRule",
                                            "" );
            asset.updateContent( "when \n SampleFact() \n then \n System.err.println(42);" );
            asset.updateFormat( AssetFormats.DRL );
            asset.checkin( "" );

            assertNull( impl.buildPackage( pkg.getUUID(),
                                           true ) );

            //check source
            PackageDeploymentServlet serv = new PackageDeploymentServlet();
            MockHTTPRequest req = new MockHTTPRequest( "/package/testPDSGetPackage/LATEST.drl",
                                                       null );
            MockHTTPResponse res = new MockHTTPResponse();
            serv.doGet( req,
                        res );

            assertNotNull( res.extractContentBytes() );
            String drl = res.extractContent();
            assertTrue( drl.indexOf( "rule" ) > -1 );

            //now binary
            serv = new PackageDeploymentServlet();
            req = new MockHTTPRequest( "/package/testPDSGetPackage/LATEST",
                                       null );
            res = new MockHTTPResponse();
            serv.doGet( req,
                        res );

            assertNotNull( res.extractContentBytes() );
            byte[] bin = res.extractContentBytes();
            byte[] bin_ = pkg.getCompiledPackageBytes();

            org.drools.rule.Package o = (org.drools.rule.Package) DroolsStreamUtils.streamIn( new ByteArrayInputStream( bin ) );
            assertNotNull( o );
            assertEquals( 1,
                          o.getRules().length );
            assertEquals( 1,
                          o.getGlobals().size() );

            assertEquals( bin_.length,
                          bin.length );

            assertSameArray( bin_,
                             bin );

            //now some snapshots
            impl.createPackageSnapshot( "testPDSGetPackage",
                                        "SNAP1",
                                        false,
                                        "hey" );

            serv = new PackageDeploymentServlet();
            req = new MockHTTPRequest( "/package/testPDSGetPackage/SNAP1.drl",
                                       null );
            res = new MockHTTPResponse();
            serv.doGet( req,
                        res );

            assertNotNull( res.extractContentBytes() );
            drl = new String( res.extractContentBytes() );
            assertTrue( drl.indexOf( "rule" ) > -1 );

            //now binary
            serv = new PackageDeploymentServlet();
            req = new MockHTTPRequest( "/package/testPDSGetPackage/SNAP1",
                                       null );
            res = new MockHTTPResponse();
            serv.doGet( req,
                        res );

            assertNotNull( res.extractContentBytes() );
            bin = res.extractContentBytes();
            bin_ = pkg.getCompiledPackageBytes();
            assertEquals( bin_.length,
                          bin.length );

            //now get an individual asset source
            serv = new PackageDeploymentServlet();
            req = new MockHTTPRequest( "/package/testPDSGetPackage/SNAP1/someRule.drl",
                                       null );
            res = new MockHTTPResponse();
            serv.doGet( req,
                        res );

            assertNotNull( res.extractContentBytes() );
            drl = res.extractContent();
            System.err.println( drl );
            assertTrue( drl.indexOf( "rule" ) > -1 );
            assertEquals( -1,
                          drl.indexOf( "package" ) );

            //now test HEAD
            serv = new PackageDeploymentServlet();
            req = new MockHTTPRequest( "/package/testPDSGetPackage/LATEST",
                                       null );
            req.method = "HEAD";
            res = new MockHTTPResponse();
            serv.doHead( req,
                         res );
            assertTrue( res.headers.size() > 0 );
            String lm = res.headers.get( "Last-Modified" );
            assertNotNull( lm );

            serv = new PackageDeploymentServlet();
            req = new MockHTTPRequest( "/package/testPDSGetPackage/LATEST",
                                       null );
            req.method = "HEAD";
            res = new MockHTTPResponse();
            serv.doHead( req,
                         res );
            assertTrue( res.headers.size() > 0 );

            assertEquals( lm,
                          res.headers.get( "Last-Modified" ) );

            serv = new PackageDeploymentServlet();
            req = new MockHTTPRequest( "/package/testPDSGetPackage/LATEST.drl",
                                       null );
            req.method = "HEAD";
            res = new MockHTTPResponse();
            serv.doHead( req,
                         res );
            assertTrue( res.headers.size() > 0 );

            assertEquals( lm,
                          res.headers.get( "Last-Modified" ) );
            System.out.println( lm );

            //
            //now lets run it in a real server !
            //
            TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
            server.setPort(8181);
            server.addServlet("/package/*", new PackageDeploymentServlet());
            server.start();

            ResourceFactory.getResourceChangeNotifierService().start();
            ResourceFactory.getResourceChangeScannerService().start();

            ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
            sconf.setProperty( "drools.resource.scanner.interval",
                               "1" );
            ResourceFactory.getResourceChangeScannerService().configure( sconf );

            String xml = "";
            xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
            xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
            xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
            xml += "    <add> ";
            xml += "        <resource source='http://localhost:9000/package/testPDSGetPackage/LATEST.drl' type='DRL' />";
            xml += "    </add> ";
            xml += "</change-set>";

            FileManager fileManager = new FileManager();
            fileManager.setUp();

            File fxml = fileManager.newFile( "changeset.xml" );
            Writer output = new BufferedWriter( new FileWriter( fxml ) );
            output.write( xml );
            output.close();

            KnowledgeAgent ag = KnowledgeAgentFactory.newKnowledgeAgent( "fii",
                                                                         KnowledgeAgentFactory.newKnowledgeAgentConfiguration() );
            ag.applyChangeSet( ResourceFactory.newUrlResource( fxml.toURI().toURL() ) );

            KnowledgeBase kb = ag.getKnowledgeBase();
            assertEquals( 1,
                          kb.getKnowledgePackages().size() );
            KnowledgePackage kp = kb.getKnowledgePackages().iterator().next();
            assertTrue( kb.getKnowledgePackages().size() > 0 );
            assertEquals( 1,
                          kp.getRules().size() );

            //check the HEAD method
            HttpClient client = new HttpClient();
            HeadMethod hm = new HeadMethod( "http://localhost:9000/package/testPDSGetPackage/LATEST.drl" );
            client.executeMethod( hm );
            Header lastMod = hm.getResponseHeader( "lastModified" );
            Thread.sleep( 50 );
            long now = System.currentTimeMillis();
            long before = Long.parseLong( lastMod.getValue() );
            assertTrue( before < now );

            //now lets add a rule
            asset = pkg.addAsset( "someRule2",
                                  "" );
            asset.updateContent( "when \n SampleFact() \n then \n System.err.println(43);" );
            asset.updateFormat( AssetFormats.DRL );
            asset.checkin( "" );

            assertNull( impl.buildPackage( pkg.getUUID(),
                                           true ) );

            Thread.sleep( 3000 );

            kb = ag.getKnowledgeBase();
            assertEquals( 1,
                          kb.getKnowledgePackages().size() );
            kp = kb.getKnowledgePackages().iterator().next();

            if ( kp.getRules().size() != 2 ) {
                Thread.sleep( 2000 );
                kb = ag.getKnowledgeBase();
                assertEquals( 1,
                              kb.getKnowledgePackages().size() );
                kp = kb.getKnowledgePackages().iterator().next();
            }

            if ( kp.getRules().size() != 2 ) {
                Thread.sleep( 2000 );
                kb = ag.getKnowledgeBase();
                assertEquals( 1,
                              kb.getKnowledgePackages().size() );
                kp = kb.getKnowledgePackages().iterator().next();
            }

            assertEquals( 2,
                          kp.getRules().size() );

            server.stop();
            repo.logout();

        }
    */

    @Test
    public void testScenariosAndChangeSet() throws Exception {

        rulesRepository.createModule("testScenariosURL",
                "");
        repositoryPackageService.createModuleSnapshot("testScenariosURL",
                "SNAP1",
                false,
                "");

        Base64 enc = new Base64();
        final String encodedAuthorization = enc.encodeToString( "admin:admin".getBytes() );

        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + encodedAuthorization );
            }
        };
        //now run the scenarios
        MockHTTPRequest req = new MockHTTPRequest( "/package/testScenariosURL/LATEST/SCENARIOS",
                                                   headers );
        MockHTTPResponse res = new MockHTTPResponse();
        packageDeploymentServlet.doGet(req,
                res);
        String testResult = res.extractContent();
        assertNotNull( testResult );
        assertEquals( "No test scenarios found.",
                      testResult );

        req = new MockHTTPRequest( "/package/testScenariosURL/SNAP1/SCENARIOS",
                                   headers );
        res = new MockHTTPResponse();
        packageDeploymentServlet.doGet( req,
                    res );
        testResult = res.extractContent();
        assertNotNull( testResult );
        assertEquals("No test scenarios found.",
                testResult);

        req = new MockHTTPRequest( "/package/testScenariosURL/SNAP1/ChangeSet.xml",
                                   headers );
        req.url = new StringBuffer( "http://foo/ChangeSet.xml" );
        res = new MockHTTPResponse();

        packageDeploymentServlet.doGet(req,
                res);
        testResult = res.extractContent();
        assertNotNull( testResult );
        assertTrue( testResult.indexOf( "<resource source='http://foo' type='PKG' />" ) > 0 );
    }

    @Test
    public void testPNG() throws Exception {
        
        ModuleItem pkg = rulesRepository.createModule( "testPNGPackage",
                                              "" );
        AssetItem asset = pkg.addAsset( "myprocess",
                                        "" );
        asset.updateFormat("pgn");
        asset.updateBinaryContentAttachment(getClass().getResource( "resources/myprocess.png" ).openStream());
        asset.updateContent( "import org.drools.guvnor.server.files.SampleFact\n global org.drools.guvnor.server.files.SampleFact sf" );
        asset.checkin("");

        AssetItem assetnew = rulesRepository.loadAssetByUUID( asset.getUUID() );
        assertEquals( "myprocess",
                      assetnew.getName() );

        //check png
        Base64 enc = new Base64();
        final String encodedAuthorization = enc.encodeToString( "admin:admin".getBytes() );
        Map<String, String> headers = new HashMap<String, String>() {
            {
                put( "Authorization",
                     "BASIC " + encodedAuthorization );
            }
        };
        MockHTTPRequest req = new MockHTTPRequest( "/package/testPNGPackage/LATEST/myprocess.png",
                                                   headers );
        MockHTTPResponse res = new MockHTTPResponse();
        packageDeploymentServlet.doGet(req,
                res);

        assertNotNull( res.extractContentBytes() );
        byte[] bin = res.extractContentBytes();
        assertTrue( bin.length > 0 );
    }

}
