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

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.io.IOUtils;
import org.drools.agent.HttpClientImpl;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.SystemEventListenerFactory;
import org.kie.agent.KnowledgeAgent;
import org.kie.agent.KnowledgeAgentConfiguration;
import org.kie.agent.KnowledgeAgentFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.definition.rule.Rule;
import org.kie.io.Resource;
import org.kie.io.ResourceChangeScannerConfiguration;
import org.kie.io.ResourceFactory;
import org.drools.rule.Package;

import com.google.gwt.user.client.rpc.SerializationException;

import static org.junit.Assert.*;

public class PackageDeploymentServletChangeSetIntegrationTest extends GuvnorIntegrationTest {

    public PackageDeploymentServletChangeSetIntegrationTest() {
        autoLoginAsAdmin = false;
    }

    //    @BeforeClass
    // HACK - Fixable after this is fixed: https://issues.jboss.org/browse/ARQ-540
    @Test @InSequence(-1)
    public void startServers() throws Exception {
        ModuleItem pkgA = rulesRepository.createModule("applyChangeSetTwice",
                "this is package applyChangeSetTwice");
        AssetItem ruleA1 = pkgA.addAsset("ruleA1", "", null, AssetFormats.DRL);
        ruleA1.updateContent("rule 'ruleA1' when org.kie.Person() then end");
        ruleA1.checkin("version 1");
        AssetItem ruleA2 = pkgA.addAsset("ruleA2", "", null, AssetFormats.DRL);
        ruleA2.updateContent("rule 'ruleA2' when org.kie.Person() then end");
        ruleA2.checkin("version 1");
        repositoryPackageService.createModuleSnapshot(pkgA.getName(), "snapshotA1", false, "");

        ModuleItem pkgB = rulesRepository.createModule("scanForChangeInRepository",
                "this is package scanForChangeInRepository");
        AssetItem ruleB1 = pkgB.addAsset("ruleB1", "", null, AssetFormats.DRL);
        ruleB1.updateContent("rule 'ruleA1' when org.kie.Person() then end");
        ruleB1.checkin("version 1");
        AssetItem ruleB2 = pkgB.addAsset("ruleB2", "", null, AssetFormats.DRL);
        ruleB2.updateContent("rule 'ruleA2' when org.kie.Person() then end");
        ruleB2.checkin("version 1");
        repositoryPackageService.createModuleSnapshot(pkgB.getName(), "snapshotB1", false, "");

        ModuleItem pkgC = rulesRepository.createModule("downloadPackageWithHttpClientImpl",
                "this is package scanForChangeInRepository");
        AssetItem ruleC1 = pkgC.addAsset("ruleC1", "", null, AssetFormats.DRL);
        ruleC1.updateContent("rule 'ruleA1' when org.kie.Person() then end");
        ruleC1.checkin("version 1");
        repositoryPackageService.createModuleSnapshot(pkgC.getName(), "snapshotC1", false, "");

        repositoryPackageService.rebuildPackages();
        repositoryPackageService.rebuildSnapshots();
    }

    @Test
    @RunAsClient
    public void applyChangeSetTwice(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "org.kie.guvnor.Guvnor/package/applyChangeSetTwice/LATEST/ChangeSet.xml");
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
    public void scanForChangeInRepository(@ArquillianResource URL baseURL) throws Exception {
        URL url = new URL(baseURL, "org.kie.guvnor.Guvnor/package/scanForChangeInRepository/LATEST/ChangeSet.xml");
        Resource res = ResourceFactory.newUrlResource(url);
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("admin", "admin".toCharArray());
            }
        });

        // system event listener
        SystemEventListenerFactory.setSystemEventListener(new PrintStreamSystemEventListener(System.out));

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeAgentConfiguration conf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
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

            kagent.applyChangeSet(res);
            kbase = kagent.getKnowledgeBase();
            Thread.sleep(1000);
            assertEquals(2, kbase.getKnowledgePackages().iterator().next().getRules().size());
            System.out.println("BUGZILLA 733008 total rules: " + kbase.getKnowledgePackages().iterator().next().getRules().size());
            for (Rule r : kbase.getKnowledgePackages().iterator().next().getRules()) {
                System.out.println(r.getName());
            }

            // Change Guvnor's repo with REST api by deleting asset rule2
            Abdera abdera = new Abdera();
            AbderaClient client = new AbderaClient(abdera);
            client.addCredentials(baseURL.toExternalForm(),
                    null,
                    null,
                    new org.apache.commons.httpclient.UsernamePasswordCredentials("admin",
                            "admin"));
            ClientResponse deleteResponse = client.delete(
                    new URL(baseURL, "rest/packages/scanForChangeInRepository/assets/ruleB2").toExternalForm());
            assertEquals(204, deleteResponse.getStatus());
            ClientResponse binaryResponse = client.get(
                    new URL(baseURL, "rest/packages/scanForChangeInRepository/binary").toExternalForm());
            assertEquals(200, binaryResponse.getStatus());


            // detect the change
            Thread.sleep(6000);
            kbase = kagent.getKnowledgeBase();
            assertEquals(1, kbase.getKnowledgePackages().iterator().next().getRules().size());

        } finally {
            kagent.dispose();
            ResourceFactory.getResourceChangeNotifierService().stop();
            ResourceFactory.getResourceChangeScannerService().stop();
        }
    }

    @Test
    @RunAsClient
    public void downloadPackageWithHttpClientImpl(@ArquillianResource URL baseURL)
            throws IOException, ClassNotFoundException {
        URL url = new URL(baseURL, "org.kie.guvnor.Guvnor/package/downloadPackageWithHttpClientImpl/snapshotC1");
        Resource resource = ResourceFactory.newUrlResource(url);
        KnowledgeAgentConfiguration conf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("admin", "admin".toCharArray());
            }
        });
        InputStream in = null;
        Collection<KnowledgePackage> kpkgs = null;
        try {
            in = resource.getInputStream();
            Object object = DroolsStreamUtils.streamIn(in);
            if ( object instanceof Collection ) {
                kpkgs = (Collection<KnowledgePackage>) object;
            } else if ( object instanceof KnowledgePackageImp ) {
                kpkgs = Collections.singletonList((KnowledgePackage) object);
            } else if( object instanceof Package ) {
                kpkgs = Collections.singletonList( (KnowledgePackage) new KnowledgePackageImp( (Package) object ) );
            } else if( object instanceof Package[] ) {
                kpkgs = new ArrayList<KnowledgePackage>();
                for( Package pkg : (Package[]) object ) {
                    kpkgs.add( new KnowledgePackageImp( pkg ) );
                }
            } else {
                throw new RuntimeException("Unknown binary format trying to load resource "+resource.toString());
            }
        } finally {
            IOUtils.closeQuietly(in);
        }

        assertNotNull(kpkgs);
        assertFalse(kpkgs.isEmpty());
        assertNotNull(kpkgs.iterator().next());
    }

}
