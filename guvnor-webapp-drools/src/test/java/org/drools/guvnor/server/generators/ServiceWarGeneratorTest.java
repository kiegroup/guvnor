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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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
import static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption.*;
import static org.drools.guvnor.server.generators.ServiceWarGenerator.*;
import static org.drools.guvnor.server.maven.cache.GuvnorArtifactCacheSupport.*;
import static org.junit.Assert.*;

public class ServiceWarGeneratorTest {

    private static final Collection<AssetReference> resources = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a", "drl", "http://localhost/c/source", "uuid1"));
        add(new AssetReference("myPkg", "aa", "drl", "http://localhost/cc/source", "uuid2"));
        add(new AssetReference("myPkg", "ab", "change_set", "http://localhost/cd/source", "uuid3"));
    }};

    private static final Collection<AssetReference> models = new ArrayList<AssetReference>() {{
        add(new AssetReference("myPkg", "a.jar", "model", "http://localhost/a.jar", "uudi44"));
    }};

    private static final ServiceConfig REST_SERVICE_CONFIG = new ServiceConfig() {{
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.addModels(models);
        kbase1.addResources(resources);
        final ServiceKSessionConfig ksession1 = new ServiceKSessionConfig("ksession1");
        final ServiceKAgentConfig kagent = new ServiceKAgentConfig("kagent1");
        kagent.setNewInstance(false);

        kbase1.addKsession(ksession1);
        kbase1.addKagent(kagent);

        addKBase(kbase1);
        setPollingFrequency(70);
    }};

    private static final ServiceConfig WS_SERVICE_CONFIG = new ServiceConfig() {{
        final ServiceKBaseConfig kbase1 = new ServiceKBaseConfig("kbase1");
        kbase1.addModels(models);
        kbase1.addResources(resources);
        final ServiceKSessionConfig ksession1 = new ServiceKSessionConfig("ksession1");
        ksession1.setProtocol(WEB_SERVICE);

        final ServiceKAgentConfig kagent = new ServiceKAgentConfig("kagent1");
        kagent.setNewInstance(false);

        kbase1.addKsession(ksession1);
        kbase1.addKagent(kagent);

        addKBase(kbase1);
        setPollingFrequency(70);
    }};

    //new ServiceConfig("70", "rest", resources, models, null);
//    private static final ServiceConfig  = null;//new ServiceConfig("70", "ws", resources, models, null);

    private static final Set<String> LIBS = new HashSet<String>() {{
        add("log4j-1.2.16.jar");
        add("jdom-1.0.jar");
    }};

    private static final String MODEL_NAME = "org/drools/guvnor/server/jarWithSourceFiles.jar";

    @Before
    @After
    public void cleanUp() {
        cleanTempDir();
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointer1() throws IOException {
        buildWar(null, (Map<String, File>) null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullPointer2() throws IOException {
        buildWar(new ServiceConfig(REST_SERVICE_CONFIG), (Map<String, File>) null, null);
    }

    @Test
    public void testNoModelUsingRest() throws IOException {
        setupLocalCache();

        final File temp = File.createTempFile("drools-service", ".jar");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        final OutputStream out = new FileOutputStream(temp);

        buildWar(new ServiceConfig(REST_SERVICE_CONFIG), (Map<String, File>) null, out);

        final WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class, temp);
        final Collection<String> fileNames = new LinkedList<String>();

        for (final Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet()) {
            final String extension = getExtension(entry.getKey().get());
            final String fileName = getName(entry.getKey().get());
            if (extension.equalsIgnoreCase("jar")) {
                fileNames.add(fileName);
            } else if (extension.equalsIgnoreCase("xml")) {
                validateGeneratedFiles("rest-", fileName, toString(entry.getValue().getAsset().openStream()));
            }
        }

        assertEquals(2, fileNames.size());
        assertTrue(LIBS.containsAll(fileNames));
    }

    @Test
    public void testRest() throws IOException, URISyntaxException {
        setupLocalCache();

        final File temp = File.createTempFile("drools-service", ".jar");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        final OutputStream out = new FileOutputStream(temp);
        final Map<String, File> models = new HashMap<String, File>() {{
            put("jarWithSourceFiles.jar", new File(ServiceWarGeneratorTest.class.getClassLoader().getResource(MODEL_NAME).toURI()));
        }};

        buildWar(new ServiceConfig(REST_SERVICE_CONFIG), models, out);

        final WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class, temp);
        final Collection<String> fileNames = new LinkedList<String>();

        for (final Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet()) {
            final String extension = getExtension(entry.getKey().get());
            final String fileName = getName(entry.getKey().get());
            if (extension.equalsIgnoreCase("jar")) {
                fileNames.add(fileName);
            } else if (extension.equalsIgnoreCase("xml")) {
                validateGeneratedFiles("rest-", fileName, toString(entry.getValue().getAsset().openStream()));
            }
        }

        assertEquals(3, fileNames.size());
        assertTrue(fileNames.containsAll(LIBS));
        assertTrue(fileNames.contains("jarWithSourceFiles.jar"));
    }

    @Test
    public void testNoModelUsingWs() throws IOException {
        setupLocalCache();

        final File temp = File.createTempFile("drools-service", ".jar");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        final OutputStream out = new FileOutputStream(temp);

        buildWar(new ServiceConfig(WS_SERVICE_CONFIG), (Map<String, File>) null, out);

        final WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class, temp);
        final Collection<String> fileNames = new LinkedList<String>();

        for (final Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet()) {
            final String extension = getExtension(entry.getKey().get());
            final String fileName = getName(entry.getKey().get());
            if (extension.equalsIgnoreCase("jar")) {
                fileNames.add(fileName);
            } else if (extension.equalsIgnoreCase("xml")) {
                validateGeneratedFiles("ws-", fileName, toString(entry.getValue().getAsset().openStream()));
            }
        }
        assertEquals(2, fileNames.size());
        assertTrue(LIBS.containsAll(fileNames));
    }

    @Test
    public void testWs() throws IOException, URISyntaxException {
        setupLocalCache();

        final File temp = File.createTempFile("drools-service", ".jar");
        // Delete temp file when program exits.
        temp.deleteOnExit();

        final OutputStream out = new FileOutputStream(temp);
        final Map<String, File> models = new HashMap<String, File>() {{
            put("jarWithSourceFiles.jar", new File(ServiceWarGeneratorTest.class.getClassLoader().getResource(MODEL_NAME).toURI()));
        }};

        buildWar(new ServiceConfig(WS_SERVICE_CONFIG), models, out);

        final WebArchive archive = ShrinkWrap.createFromZipFile(WebArchive.class, temp);
        final Collection<String> fileNames = new LinkedList<String>();

        for (final Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet()) {
            final String extension = getExtension(entry.getKey().get());
            final String fileName = getName(entry.getKey().get());
            if (extension.equalsIgnoreCase("jar")) {
                fileNames.add(fileName);
            } else if (extension.equalsIgnoreCase("xml")) {
                validateGeneratedFiles("ws-", fileName, toString(entry.getValue().getAsset().openStream()));
            }
        }
        assertEquals(3, fileNames.size());
        assertTrue(fileNames.containsAll(LIBS));
        assertTrue(fileNames.contains("jarWithSourceFiles.jar"));
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

    private void validateGeneratedFiles(final String protocol, final String fileName, final String generatedContent) {
        final String origContent = getResourceContent("org/drools/guvnor/server/generators/" + protocol + fileName);
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
            BufferedInputStream inContent = new BufferedInputStream(ServiceWarGeneratorTest.class.getClassLoader().getResourceAsStream(fileName));
            IOUtils.copy(inContent, outContent);

            return outContent.toString();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Error " + fileName, ex);
        }
    }

}
