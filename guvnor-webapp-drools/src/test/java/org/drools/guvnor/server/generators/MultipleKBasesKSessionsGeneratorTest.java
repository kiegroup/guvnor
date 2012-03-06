/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.generators;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig;
import org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig;
import org.drools.guvnor.client.rpc.MavenArtifact;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.io.FilenameUtils.*;
import static org.drools.ClockType.*;
import static org.drools.conf.AssertBehaviorOption.*;
import static org.drools.conf.EventProcessingOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.MarshallingOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption.*;
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.SessionType.*;
import static org.drools.guvnor.server.generators.ServiceWarGenerator.*;
import static org.drools.guvnor.server.maven.cache.GuvnorArtifactCacheSupport.*;
import static org.junit.Assert.*;

public class MultipleKBasesKSessionsGeneratorTest {

    private static final List<AssetReference> resources = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a", "drl", "http://localhost/c/source", "uuid1"));
        add(new AssetReference("myPkg", "aa", "drl", "http://localhost/cc/source", "uuid2"));
        add(new AssetReference("myPkg", "ab", "change_set", "http://localhost/cd/source", "uuid3"));
    }};

    private static final List<AssetReference> models = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
    }};

    @Before
    @After
    public void cleanUp() {
        cleanTempDir();
    }

    @Test
    public void testMultiKBaseKSession() throws IOException, URISyntaxException {
        setupLocalCache();

        final ServiceConfig config = new ServiceConfig() {{
            final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");
            kbase1.addModels(models);
            kbase1.addResources(resources);
            kbase1.setAssetsUser("admin");
            kbase1.setAssetsPassword("admin");
            kbase1.setAssertBehavior(IDENTITY);

            final ServiceKSessionConfig ksession1 = new ServiceKSessionConfig("ksession1");
            ksession1.setType(STATEFUL);
            ksession1.setUrl("myksession1");
            ksession1.setKeepReference(true);
            ksession1.setClockType(PSEUDO_CLOCK);

            final ServiceKSessionConfig ksession2 = new ServiceKSessionConfig("ksession2");
            ksession2.setUrl("myksession2");
            ksession2.setKeepReference(false);
            ksession2.setMarshalling(JAXB);

            kbase1.addKsession(ksession1);
            kbase1.addKsession(ksession2);

            addKBase(kbase1);

            final ServiceKBaseConfig kbase2 = new ServiceKBaseConfig("kbase2");
            kbase2.addResource(new AssetReference("myPkg", "ax", "change_set", "http://localhost/cd/source", "uuidx9"));
            kbase2.setAssetsUser("admin");
            kbase2.setAssetsPassword("admin");
            kbase2.setMbeans(true);
            kbase2.setEventProcessingMode(STREAM);

            final ServiceKSessionConfig ksession3 = new ServiceKSessionConfig("ksession3");
            ksession3.setUrl("myksession3");
            ksession3.setProtocol(WEB_SERVICE);
            ksession3.setClockType(REALTIME_CLOCK);

            final ServiceKSessionConfig ksession4 = new ServiceKSessionConfig("ksession4");
            ksession4.setProtocol(WEB_SERVICE);
            ksession4.setMarshalling(JSON);

            kbase2.addKsession(ksession3);
            kbase2.addKsession(ksession4);

            addKBase(kbase2);
        }};

        final File temp = File.createTempFile("drools-service", ".jar");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        final OutputStream out = new FileOutputStream(temp);

        buildWar(config, (Map<String, File>) null, out);

        final WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class, temp);

        for (final Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet()) {
            final String extension = getExtension(entry.getKey().get());
            final String fileName = getName(entry.getKey().get());
            if (extension.equalsIgnoreCase("xml")) {
                validateGeneratedFiles("multi1", fileName, toString(entry.getValue().getAsset().openStream()));
            }
        }
    }

    @Test
    public void testMultiKBaseKSessionAndKAgents() throws IOException, URISyntaxException {
        setupLocalCache();

        final ServiceConfig config = new ServiceConfig() {{
            final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");
            kbase1.addModels(models);
            kbase1.addResources(resources);
            kbase1.setAssetsUser("admin");
            kbase1.setAssetsPassword("admin");
            kbase1.setAssertBehavior(IDENTITY);

            final ServiceKSessionConfig ksession1 = new ServiceKSessionConfig("ksession1");
            ksession1.setType(STATEFUL);
            ksession1.setUrl("myksession1");
            ksession1.setKeepReference(true);
            ksession1.setClockType(PSEUDO_CLOCK);

            final ServiceKSessionConfig ksession2 = new ServiceKSessionConfig("ksession2");
            ksession2.setUrl("myksession2");
            ksession2.setKeepReference(false);
            ksession2.setMarshalling(JAXB);

            kbase1.addKsession(ksession1);
            kbase1.addKsession(ksession2);

            final ServiceKAgentConfig kagent1 = new ServiceKAgentConfig("kagent1");
            kagent1.setNewInstance(false);
            kagent1.setUseKBaseClassloader(true);
            kagent1.addResource(resources.get(0));
            kagent1.addResource(resources.get(1));

            final ServiceKAgentConfig kagent2 = new ServiceKAgentConfig("kagent2");
            kagent2.setNewInstance(false);

            kbase1.addKagent(kagent1);
            kbase1.addKagent(kagent2);

            addKBase(kbase1);

            final ServiceKBaseConfig kbase2 = new ServiceKBaseConfig("kbase2");
            kbase2.addResource(new AssetReference("myPkg", "ax", "change_set", "http://localhost/cd/source", "uuidx9"));
            kbase2.setAssetsUser("admin");
            kbase2.setAssetsPassword("admin");
            kbase2.setMbeans(true);
            kbase2.setEventProcessingMode(STREAM);

            final ServiceKSessionConfig ksession3 = new ServiceKSessionConfig("ksession3");
            ksession3.setUrl("myksession3");
            ksession3.setProtocol(WEB_SERVICE);
            ksession3.setClockType(REALTIME_CLOCK);

            final ServiceKSessionConfig ksession4 = new ServiceKSessionConfig("ksession4");
            ksession4.setProtocol(WEB_SERVICE);
            ksession4.setMarshalling(JSON);

            kbase2.addKsession(ksession3);
            kbase2.addKsession(ksession4);

            final ServiceKAgentConfig kagent3 = new ServiceKAgentConfig("kagent3");
            kagent3.setUseKBaseClassloader(true);

            final ServiceKAgentConfig kagent4 = new ServiceKAgentConfig("kagent4");
            kagent4.setUseKBaseClassloader(true);
            kagent4.setNewInstance(true);

            final ServiceKAgentConfig kagent5 = new ServiceKAgentConfig("kagent5");
            kagent5.addResource(resources.get(2));

            kbase2.addKagent(kagent3);
            kbase2.addKagent(kagent4);
            kbase2.addKagent(kagent5);

            addKBase(kbase2);
        }};

        final File temp = File.createTempFile("drools-service", ".jar");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        final OutputStream out = new FileOutputStream(temp);

        buildWar(config, (Map<String, File>) null, out);

        final WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class, temp);

        for (final Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet()) {
            final String extension = getExtension(entry.getKey().get());
            final String fileName = getName(entry.getKey().get());
            if (extension.equalsIgnoreCase("xml")) {
                validateGeneratedFiles("multi2", fileName, toString(entry.getValue().getAsset().openStream()));
            }
        }
    }

    private void setupLocalCache() {
        final Collection<String> repositories = new ArrayList<String>() {{
            add(getURLtoLocalUserMavenRepo());
        }};

        final Collection<MavenArtifact> dependencies = new ArrayList<MavenArtifact>() {{
            add(new MavenArtifact("log4j:log4j:jar:1.2.16:compile"));
            add(new MavenArtifact("jdom:jdom:jar:1.0:compile"));
        }};

        buildCache(repositories, dependencies);
    }

    private void validateGeneratedFiles(final String folder, final String fileName, final String generatedContent) {
        final String origContent = getResourceContent("org/drools/guvnor/server/generators/" + folder + "/" + fileName);
        assertEquals("Following file content doesn't match - " + fileName, origContent, generatedContent);
    }

    public String toString(final InputStream is) throws IOException {
        if (is != null) {
            final Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    private static String getResourceContent(final String fileName) {
        try {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            BufferedInputStream inContent = new BufferedInputStream(MultipleKBasesKSessionsGeneratorTest.class.getClassLoader().getResourceAsStream(fileName));
            IOUtils.copy(inContent, outContent);

            return outContent.toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error " + fileName, ex);
        }
    }

}
