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

import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.server.test.GuvnorIntegrationTest;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Ignore;
import org.junit.Test;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

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

}
