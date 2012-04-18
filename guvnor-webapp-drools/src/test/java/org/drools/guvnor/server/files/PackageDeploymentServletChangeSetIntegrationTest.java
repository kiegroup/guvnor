/*
 * Copyright 2012 JBoss Inc
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

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;

import com.google.gwt.user.client.rpc.SerializationException;


public class PackageDeploymentServletChangeSetIntegrationTest extends GuvnorIntegrationTest {

    public PackageDeploymentServletChangeSetIntegrationTest() {
        autoLoginAsAdmin = false;
    }
    

    //    @BeforeClass
    // HACK - Fixable after this is fixed: https://issues.jboss.org/browse/ARQ-540
    @Test @InSequence(-1)
    public void startServers() throws Exception {
        ModuleItem pkg = rulesRepository.createModule( "fileManagerServicePackage1",
                                                                   "this is package fileManagerServicePackage1" );
        AssetItem rule1 = pkg.addAsset( "rule1",
                                       "" );
        rule1.updateFormat(AssetFormats.DRL);
        rule1.updateContent("rule 'rule1' when org.drools.Person() then end");
        rule1.checkin("version 1");

        // Create rule2
        AssetItem rule2 = pkg.addAsset( "rule2",
                                       "" );
        rule2.updateFormat(AssetFormats.DRL);
        rule2.updateContent("rule 'rule2' when org.drools.Person() then end");
        rule2.checkin("version 1");

        String snapshotName = "SNAP1";
        repositoryPackageService.createModuleSnapshot(pkg.getName(),
                snapshotName,
                false,
                "");
        repositoryPackageService.rebuildPackages();
        repositoryPackageService.rebuildSnapshots();
    }

    @Test
    @RunAsClient
    public void applyChangeSetTwice(@ArquillianResource URL baseURL) throws Exception {
        System.out.println(baseURL);
	
	URL url = new URL(baseURL, "org.drools.guvnor.Guvnor/package/fileManagerServicePackage1/LATEST/ChangeSet.xml");
        Resource res = ResourceFactory.newUrlResource(url);
        KnowledgeAgentConfiguration conf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("admin", "admin".toCharArray());
            }
        });
        KnowledgeAgent ka = KnowledgeAgentFactory.newKnowledgeAgent("test", conf);
        System.out.println("Applying changeset, round #1");
        Thread.sleep(1000);
        ka.applyChangeSet(res);
        for (KnowledgePackage pkg : ka.getKnowledgeBase().getKnowledgePackages()) {
            System.out.printf("  %s (%d)%n", pkg.getName(), pkg.getRules().size());
        }

        System.out.println("Applying changeset, round #2");
        Thread.sleep(1000);
        ka.applyChangeSet(res);
        for (KnowledgePackage pkg : ka.getKnowledgeBase().getKnowledgePackages()) {
            System.out.printf("  %s (%d)%n", pkg.getName(), pkg.getRules().size());
        }
    }
    
    
    @Test
    @RunAsClient
    public void scanForChangeInRepository(@ArquillianResource URL baseURL) throws MalformedURLException, InterruptedException, SerializationException, URISyntaxException {
        
        System.out.println("\nBUGZILLA 733008 reproducer\n");
        
        URL url = new URL(baseURL, "org.drools.guvnor.Guvnor/package/fileManagerServicePackage1/LATEST/ChangeSet.xml");
        Resource res = ResourceFactory.newUrlResource(url);
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("admin", "admin".toCharArray());
            }
        });
        
        // system event listener
        SystemEventListenerFactory
                        .setSystemEventListener(new PrintStreamSystemEventListener(
                                        System.out));

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgentConfiguration conf = KnowledgeAgentFactory
                .newKnowledgeAgentConfiguration();
        // needs to be newInstance=true, bugzilla 733008 works fine with newInstance=false
        conf.setProperty("drools.agent.newInstance", "true");
        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
                "agent", kbase, conf);
        
        try {
            
            ResourceFactory.getResourceChangeNotifierService().start();
            ResourceFactory.getResourceChangeScannerService().start();

            ResourceChangeScannerConfiguration sconf = ResourceFactory
                        .getResourceChangeScannerService()
                        .newResourceChangeScannerConfiguration();
            sconf.setProperty("drools.resource.scanner.interval", "5");
            ResourceFactory.getResourceChangeScannerService().configure(sconf);

            // first time, loading package mortgages from Guvnor
            kagent.applyChangeSet(res);
            kbase = kagent.getKnowledgeBase();
            Thread.sleep(1000);
            System.out.println("BUGZILLA 733008 total rules: " + kbase.getKnowledgePackages().iterator().next().getRules().size());
            for (Rule r : kbase.getKnowledgePackages().iterator().next().getRules()) {
        	System.out.println(r.getName());
            }

            // change Guvnor's repo with REST api by deleting asset rule2
            Abdera abdera = new Abdera();
            AbderaClient client = new AbderaClient( abdera );
            client.addCredentials( baseURL.toExternalForm(),
                                   null,
                                   null,
                                   new org.apache.commons.httpclient.UsernamePasswordCredentials( "admin",
                                                                                  "admin" ) );
            String queryURL = (new URL(baseURL, "rest/packages/fileManagerServicePackage1/assets/rule2")).toExternalForm();
            ClientResponse resp = client.delete(queryURL);
            System.out.println(resp);
            
            // detect the change
            Thread.sleep(6000);
            System.out.println("BUGZILLA 733008 total rules: " + kbase.getKnowledgePackages().iterator().next().getRules().size());
            kbase = kagent.getKnowledgeBase();
            for (Rule r : kbase.getKnowledgePackages().iterator().next().getRules()) {
        	System.out.println(r.getName());
            }
        
        } finally {
                kagent.dispose();
                ResourceFactory.getResourceChangeNotifierService().stop();
                ResourceFactory.getResourceChangeScannerService().stop();
        }
    }

}
