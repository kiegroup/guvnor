/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.services.tests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerRuntimeConfigImpl;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Ignore;

import static org.junit.Assert.*;

public class RuntimeEndpointsTestIT {

    private final String APP_URL = "http://localhost:8080/api/";

    @Ignore
    public void checkService() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target( APP_URL );
        ResteasyWebTarget restEasyTarget = ( ResteasyWebTarget ) target;
        RuntimeProvisioningService proxy = restEasyTarget.proxy( RuntimeProvisioningService.class );

        ProviderTypeList allProviderTypes = proxy.getProviderTypes( 0, 10, "", true );

        assertNotNull( allProviderTypes );
        assertEquals( 2, allProviderTypes.getItems().size() );

        DockerProviderConfig dockerProviderConfig = new DockerProviderConfigImpl();
        proxy.registerProvider( dockerProviderConfig );

        ProviderList allProviders = proxy.getProviders( 0, 10, "", true );
        assertEquals( 1, allProviders.getItems().size() );
        assertTrue( allProviders.getItems().get( 0 ) instanceof DockerProvider );
        DockerProvider dockerProvider = ( DockerProvider ) allProviders.getItems().get( 0 );
        DockerRuntimeConfig runtimeConfig = new DockerRuntimeConfigImpl( dockerProvider, "kitematic/hello-world-nginx", "8080", true );

        RuntimeList allRuntimes = proxy.getRuntimes( 0, 10, "", true );
        assertEquals( 0, allRuntimes.getItems().size() );

        String newRuntime = proxy.newRuntime( runtimeConfig );

        allRuntimes = proxy.getRuntimes( 0, 10, "", true );
        assertEquals( 1, allRuntimes.getItems().size() );

        allRuntimes = proxy.getRuntimes( 0, 10, "", true );
        assertEquals( 1, allRuntimes.getItems().size() );

        Runtime runtime = allRuntimes.getItems().get( 0 );

        assertTrue( runtime instanceof DockerRuntime );
        DockerRuntime dockerRuntime = ( DockerRuntime ) runtime;

        assertEquals( "Running", dockerRuntime.getState().getState() );
        proxy.stopRuntime( newRuntime );

        allRuntimes = proxy.getRuntimes( 0, 10, "", true );
        assertEquals( 1, allRuntimes.getItems().size() );
        runtime = allRuntimes.getItems().get( 0 );

        assertTrue( runtime instanceof DockerRuntime );
        dockerRuntime = ( DockerRuntime ) runtime;

        assertEquals( "Stopped", dockerRuntime.getState().getState() );

        proxy.destroyRuntime( newRuntime );

        allRuntimes = proxy.getRuntimes( 0, 10, "", true );
        assertEquals( 0, allRuntimes.getItems().size() );

    }

}
