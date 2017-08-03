/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.executor;

import static java.util.Arrays.asList;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.APPLICATION_NAME;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_AUTH_BASIC_PASSWORD;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_AUTH_BASIC_USERNAME;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.KUBERNETES_MASTER;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.PROJECT_NAME;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.PROVIDER_NAME;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.RESOURCE_SECRETS_URI;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.RESOURCE_STREAMS_URI;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.RESOURCE_TEMPLATE_PARAM_VALUES;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.RESOURCE_TEMPLATE_URI;
import static org.guvnor.ala.openshift.config.OpenShiftProperty.SERVICE_NAME;
import static org.guvnor.ala.pipeline.StageUtil.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.openshift.access.OpenShiftAccessInterface;
import org.guvnor.ala.openshift.access.OpenShiftClient;
import org.guvnor.ala.openshift.access.impl.OpenShiftAccessInterfaceImpl;
import org.guvnor.ala.openshift.config.OpenShiftParameters;
import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeExecConfig;
import org.guvnor.ala.openshift.config.impl.ContextAwareOpenShiftRuntimeExecConfig;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.guvnor.ala.openshift.config.impl.OpenShiftRuntimeConfigImpl;
import org.guvnor.ala.openshift.model.OpenShiftRuntime;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeState;
import org.guvnor.ala.openshift.service.OpenShiftRuntimeManager;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.registry.local.InMemoryRuntimeRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.junit.Test;

/**
 * Simple test using the Pipeline API and the openshift Provider & Executors
 */
public class OpenShiftExecutorTest {

    /**
     * Make sure we can reuse the provider for two different runtimes.
     * TODO: replace with more lightweight image and non-internal (minishift?) environment.
     * @throws Exception
     */
    @Test
    public void testProviderConfigReuse() throws Exception {
        OpenShiftProviderConfigImpl providerConfig = new OpenShiftProviderConfigImpl().clear();
        providerConfig.setName(getClass().getSimpleName());
        providerConfig.setKubernetesMaster("https://ce-os-rhel-master.usersys.redhat.com:8443");
        providerConfig.setKubernetesAuthBasicUsername("admin");
        providerConfig.setKubernetesAuthBasicPassword("admin");
        OpenShiftClient client = OpenShiftAccessInterfaceImpl.buildOpenShiftClient(providerConfig);

        OpenShiftRuntimeConfigImpl runtimeConfigA = createRuntimeConfig("tpcr1");
        OpenShiftRuntimeConfigImpl runtimeConfigB = createRuntimeConfig("tpcr2");
        client.create(runtimeConfigA);
        client.create(runtimeConfigB);

        client.destroy(runtimeConfigA.getRuntimeId().toString());
        client.destroy(runtimeConfigB.getRuntimeId().toString());
    }

    private OpenShiftRuntimeConfigImpl createRuntimeConfig(String testName) throws Exception {
        final String prjName = createProjectName(testName);
        final String appName = "myapp";
        final String svcName = appName + "-execserv";
        OpenShiftRuntimeConfigImpl runtimeConfig = new OpenShiftRuntimeConfigImpl();
        runtimeConfig.setProjectName(prjName);
        runtimeConfig.setServiceName(svcName);
        runtimeConfig.setApplicationName(appName);
        runtimeConfig.setResourceSecretsUri(getUri("bpmsuite-app-secret.json"));
        runtimeConfig.setResourceStreamsUri(getUri("jboss-image-streams.json"));
        runtimeConfig.setResourceTemplateUri(getUri("bpmsuite70-execserv.json"));
        runtimeConfig.setResourceTemplateParamValues(new OpenShiftParameters()
                .param("APPLICATION_NAME", appName)
                .param("IMAGE_STREAM_NAMESPACE", prjName)
                .param("KIE_ADMIN_PWD", "admin1!")
                .param("KIE_SERVER_PWD", "execution1!")
                .toString());
        return runtimeConfig;
    }

    /**
     * Tests the ALA pipeline and runtime lifecycle.
     * TODO: replace with more lightweight image and non-internal (minishift?) environment.
     * @throws Exception
     */
    @Test
    public void testPipelineAndLifecycle() throws Exception {
        final RuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final OpenShiftAccessInterface openshiftAccessInterface = new OpenShiftAccessInterfaceImpl();

        final Stage<Input, ProviderConfig> providerConfig =
                config( "OpenShift Provider Config", (s) -> new OpenShiftProviderConfig() {} );

        final Stage<ProviderConfig, RuntimeConfig> runtimeExec =
                config( "OpenShift Runtime Config", (s) -> new ContextAwareOpenShiftRuntimeExecConfig() );

        final Pipeline pipe = PipelineFactory
                .startFrom( providerConfig )
                .andThen( runtimeExec )
                .buildAs( "my pipe" );

        final OpenShiftRuntimeExecExecutor<OpenShiftRuntimeExecConfig> openshiftRuntimeExecExecutor = new OpenShiftRuntimeExecExecutor<>( runtimeRegistry, openshiftAccessInterface );
        final PipelineExecutor executor = new PipelineExecutor( asList(
                new OpenShiftProviderConfigExecutor( runtimeRegistry ),
                openshiftRuntimeExecExecutor ) );

        final String pvrName = getClass().getSimpleName();
        final String prjName = createProjectName("tpal1");
        final String appName = "myapp";
        final String svcName = appName + "-execserv";
        
        String templateParams = new OpenShiftParameters()
                .param("APPLICATION_NAME", appName)
                .param("IMAGE_STREAM_NAMESPACE", prjName)
                .param("KIE_ADMIN_PWD", "admin1!")
                .param("KIE_SERVER_PWD", "execution1!")
                .toString();

        Input input = new Input() {{
            // provider properties
            put(KUBERNETES_MASTER.inputKey(), "https://ce-os-rhel-master.usersys.redhat.com:8443");
            put(KUBERNETES_AUTH_BASIC_USERNAME.inputKey(), "admin");
            put(KUBERNETES_AUTH_BASIC_PASSWORD.inputKey(), "admin");
            put(PROVIDER_NAME.inputKey(), pvrName);
            // runtime properties
            put(APPLICATION_NAME.inputKey(), appName);
            put(PROJECT_NAME.inputKey(), prjName);
            put(RESOURCE_SECRETS_URI.inputKey(), getUri("bpmsuite-app-secret.json"));
            put(RESOURCE_STREAMS_URI.inputKey(), getUri("jboss-image-streams.json"));
            put(RESOURCE_TEMPLATE_PARAM_VALUES.inputKey(), templateParams);
            put(RESOURCE_TEMPLATE_URI.inputKey(), getUri("bpmsuite70-execserv.json"));
            put(SERVICE_NAME.inputKey(), svcName);
        }};
        executor.execute( input, pipe, (Runtime b) -> System.out.println( b ) );

        OpenShiftRuntimeManager runtimeManager = new OpenShiftRuntimeManager( runtimeRegistry, openshiftAccessInterface );
        OpenShiftRuntime openshiftRuntime = getRuntime(runtimeRegistry, runtimeManager, null, true);
        assertEquals( OpenShiftRuntimeState.READY, openshiftRuntime.getState().getState() );

        runtimeManager.start( openshiftRuntime );
        openshiftRuntime = getRuntime(runtimeRegistry, runtimeManager, openshiftRuntime, true);
        assertEquals( OpenShiftRuntimeState.STARTED, openshiftRuntime.getState().getState() );

        runtimeManager.stop( openshiftRuntime );
        openshiftRuntime = getRuntime(runtimeRegistry, runtimeManager, openshiftRuntime, true);
        assertEquals( OpenShiftRuntimeState.READY, openshiftRuntime.getState().getState() );

        openshiftRuntimeExecExecutor.destroy( openshiftRuntime );
        openshiftRuntime = getRuntime(runtimeRegistry, runtimeManager, openshiftRuntime, false);
        assertNull( openshiftRuntime );

        openshiftAccessInterface.dispose();
    }

    private OpenShiftRuntime getRuntime(
            RuntimeRegistry runtimeRegistry,
            OpenShiftRuntimeManager runtimeManager,
            OpenShiftRuntime openshiftRuntime,
            boolean expected ) {

        if (openshiftRuntime != null) {
            runtimeManager.refresh(openshiftRuntime);
        }

        if (expected) {
            List<Runtime> allRuntimes = runtimeRegistry.getRuntimes(0, 10, "", true);
            assertEquals( 1, allRuntimes.size() );

            Runtime runtime = allRuntimes.get( 0 );
            assertTrue( runtime instanceof OpenShiftRuntime );

            return ( OpenShiftRuntime ) runtime;
        } else {
            return null;
        }
    }

    private String getUri(String resourcePath) throws URISyntaxException {
        if (!resourcePath.startsWith("/")) {
            resourcePath = "/" + resourcePath;
        }
        return getClass().getResource(resourcePath).toURI().toString();
    }

    private String createProjectName(String testName) {
        return new StringBuilder()
                .append(System.getProperty("user.name", "anon").replaceAll("[^A-Za-z0-9]", "-"))
                .append('-')
                .append(testName != null ? testName : "test")
                .append('-')
                .append(new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()))
                .toString();
    }

}
