/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.jaxrs;

import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.jaxrs.jaxb.*;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mvel2.util.StringAppender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestTestingBase {

    protected Logger logger;

    protected static Level LogLevel = Level.INFO;

    protected static Dispatcher dispatcher;

    protected static ServiceImplementation Service;

    @BeforeClass
    public static void Initialize() {
        Service = Resource.Service;

        try {
            ResteasyDeployment deployment = EmbeddedContainer.start();
            dispatcher = deployment.getDispatcher();
            Service.clearRulesRepository();
            Service.installSampleRepository();
            Service.rebuildPackages();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void Destroy() {
        try {
            EmbeddedContainer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String GetContent (HttpURLConnection connection) throws IOException {
        StringAppender ret = new StringAppender();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            ret.append(line + "\n");
        }

        return ret.toString();
    }

    @Before
    public void setUp() throws Exception {
        logger = Logger.getLogger(getClass().getSimpleName());
    }

    protected Package createTestPackage(String title) {
        Category c = new Category();
        c.setName("test");

        Package p = new Package();
        PackageMetadata metadata = new PackageMetadata();
        metadata.setCreated(new Date(System.currentTimeMillis()));
        metadata.setUuid(UUID.randomUUID().toString());
        metadata.setLastContributor("awaterma");
        metadata.setLastModified(new Date(System.currentTimeMillis()));

        p.setMetadata(metadata);
        p.setCategory(c);
        p.setCheckInComment("Check in comment for test package.");
        p.setTitle(title);
        p.setDescription("A simple test package with 0 assets.");
        return p;
    }
}
